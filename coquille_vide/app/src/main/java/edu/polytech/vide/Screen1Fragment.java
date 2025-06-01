package edu.polytech.vide;

import static edu.polytech.vide.Notifiable.TAG_MENU1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Screen1Fragment avec notifications selon le cours
 */
public class Screen1Fragment extends Fragment {
    private final String TAG = "frallo "+getClass().getSimpleName();
    private static final int REQUEST_NOTIFICATION_PERMISSION = 101;
    private Notifiable notifiable;

    // Service de notification
    private NotificationService notificationService;
    private boolean serviceBound = false;

    // Launcher pour demander la permission de notification (API 33+)
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public Screen1Fragment() {
        Log.d(TAG,"screenFragment type 1 created");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialiser le launcher pour les permissions selon le cours
        initPermissionLauncher();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof Notifiable) {
            notifiable = (Notifiable) requireActivity();
            Log.d(TAG, "Class " + requireActivity().getClass().getSimpleName() + " implements Notifiable.");
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        // Initialiser les vues
        ImageView ralloImage = view.findViewById(R.id.picture);
        Button sendButton = view.findViewById(R.id.send);

        // Animation de Rallo
        startRalloAnimation(ralloImage);

        // Configurer le bouton d'envoi
        sendButton.setOnClickListener(v -> sendNotification());

        // Vérifier permissions et démarrer service selon le cours
        checkPermissionAndStartService();

        return view;
    }

    /**
     * Initialise le launcher pour demander la permission de notification
     */
    private void initPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Permission POST_NOTIFICATIONS accepted");
                        Toast.makeText(getContext(), "Permission accordée", Toast.LENGTH_SHORT).show();
                        startNotificationService();
                    } else {
                        Log.e(TAG, "Permission POST_NOTIFICATIONS refused");
                        Toast.makeText(getContext(), "Permission refusée - les notifications ne fonctionneront pas", Toast.LENGTH_LONG).show();
                        considerToAllow();
                        // Démarrer le service quand même pour la compatibilité
                        startNotificationService();
                    }
                }
        );
    }

    /**
     * Vérifie les permissions selon le cours
     */
    private void checkPermissionAndStartService() {
        // API 33+ (incluant API 36) - Demander la permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "API 33+ détectée - demande de permission requise");
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Demande de permission de notification");
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Log.d(TAG, "Permission déjà accordée");
                startNotificationService();
            }
        } else {
            // API 29 et inférieures - Pas de permission requise selon le cours
            Log.d(TAG, "API 29 détectée - aucune permission requise");
            startNotificationService();
        }
    }

    /**
     * Démarre le service selon le cours
     */
    private void startNotificationService() {
        Intent serviceIntent = new Intent(getContext(), NotificationService.class);
        requireContext().startService(serviceIntent);
        bindService();
        Log.d(TAG, "Service démarré");
    }

    /**
     * Lie le service dans onStart selon le cours
     */
    private void bindService() {
        Intent intent = new Intent(getContext(), NotificationService.class);
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Service lié");
    }

    /**
     * ServiceConnection selon le cours
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NotificationService.LocalBinder binder = (NotificationService.LocalBinder) service;
            notificationService = binder.getService();
            serviceBound = true;
            Log.d(TAG, "service is connected");
            Toast.makeText(getContext(), "Service prêt!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notificationService = null;
            serviceBound = false;
            Log.d(TAG, "Service déconnecté");
        }
    };

    /**
     * Envoie une notification via le service
     */
    private void sendNotification() {
        if (serviceBound && notificationService != null) {
            Log.d(TAG, "--> send");
            notificationService.sendNotification("Wanna send a notification?", "Notification depuis Screen1Fragment");
            Toast.makeText(getContext(), "Notification envoyée!", Toast.LENGTH_SHORT).show();

            // Notifier l'activité
            notifiable.onClick(TAG_MENU1);
        } else {
            Log.w(TAG, "Service non disponible");
            Toast.makeText(getContext(), "Service non disponible", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gestion du refus de permission selon le cours
     */
    private void considerToAllow() {
        Log.d(TAG, "please considere the situation :/");
        // TODO: Expliquer à l'utilisateur pourquoi on a besoin de la permission
    }

    /**
     * Animation de Rallo au démarrage
     */
    private void startRalloAnimation(ImageView imageView) {
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rallo_entrance);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "Animation de Rallo démarrée");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "Animation de Rallo terminée");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        imageView.startAnimation(scaleAnimation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            requireContext().unbindService(serviceConnection);
            serviceBound = false;
        }
        Log.d(TAG, "Fragment détruit");
    }
}