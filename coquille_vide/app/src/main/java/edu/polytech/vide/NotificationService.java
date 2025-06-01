package edu.polytech.vide;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends Service {
    private final String TAG = "frallo " + getClass().getSimpleName();
    private final String CHANNEL_ID = "notif_channel_id";
    private Uri uriSound;
    private final IBinder binder = new LocalBinder(); // Selon le cours

    // LocalBinder selon le cours
    public class LocalBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Créer le canal au démarrage selon le cours
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.deleteNotificationChannel(CHANNEL_ID); // Supprime l'ancien canal

            CharSequence name = "Nom du canal";
            String description = "Description du canal";
            uriSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
                    getApplicationContext().getPackageName() + "/" + R.raw.mj);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);

            // Son seulement pour API 36+ selon le cours
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                channel.setSound(uriSound, audioAttributes);
                Log.d(TAG, "Canal créé avec son pour API " + Build.VERSION.SDK_INT);
            } else {
                Log.d(TAG, "Canal créé sans son pour API " + Build.VERSION.SDK_INT);
            }

            notificationManager.createNotificationChannel(channel);
        } else {
            Log.d(TAG, "Canal de notification inutile pour API " + Build.VERSION.SDK_INT);
        }

        Log.d(TAG, "service has created the notification channel");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service lié");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service détruit");
    }

    /**
     * Envoie une notification selon le cours
     */
    public void sendNotification(String title, String message) {
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(title != null ? title : "Titre par défaut")
                    .setContentText(message != null ? message : "Message par défaut")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Son seulement pour API 36+ selon le cours
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && uriSound != null) {
                builder.setSound(uriSound);
                Log.d(TAG, "Son ajouté à la notification");
            } else {
                Log.d(TAG, "Pas de son pour API " + Build.VERSION.SDK_INT);
            }

            notificationManager.notify(1, builder.build());
            Log.d(TAG, "Notification envoyée");
        } else {
            Log.d(TAG, "Permission de notification non accordée");
        }
    }
}