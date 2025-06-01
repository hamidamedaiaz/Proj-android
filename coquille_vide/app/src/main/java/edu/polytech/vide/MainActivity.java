package edu.polytech.vide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This application has two activities:
 *
 *     MainActivity, which contains a frame-by-frame animation.
 *     ControlActivity, which contains two fragments:
 *         MenuFragment (static fragment ...mais devient dynamique lorsqu'on veut le démarrer avec un paramètre)
 *         Screen1Fragment (dynamic fragment)
 *
 * @author F. Rallo - march 2025
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "frallo "+getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(getApplicationContext(), ControlActivity.class);

        ImageView image = findViewById(R.id.image);
        image.setImageResource(R.drawable.rallo);

        // Démarrer l'animation de Rallo
        startRalloAnimation(image);

        //optional button --> goto menu5
        findViewById(R.id.goDefault).setOnClickListener(clic -> {
            int menuNumber = 5;  //this is number, not index
            intent.putExtra(getString(R.string.index),menuNumber);
            Log.d(TAG,"send menu#"+menuNumber);
            startActivity(intent);
        });
    }

    /**
     * Démarre l'animation d'agrandissement pour l'image de Rallo
     * @param imageView L'ImageView à animer
     */
    private void startRalloAnimation(ImageView imageView) {
        // Charger l'animation depuis les ressources
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.rallo_entrance);

        // Ajouter un listener pour surveiller l'animation
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "Animation de Rallo démarrée dans MainActivity");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "Animation de Rallo terminée dans MainActivity");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Non utilisé
            }
        });

        // Démarrer l'animation
        imageView.startAnimation(scaleAnimation);
    }
}