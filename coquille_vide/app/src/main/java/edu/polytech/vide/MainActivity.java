package edu.polytech.vide;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
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

        //optional button --> goto menu5
        findViewById(R.id.goDefault).setOnClickListener(clic -> {
            int menuNumber = 5;  //this is number, not index
            intent.putExtra(getString(R.string.index),menuNumber);
            Log.d(TAG,"send menu#"+menuNumber);
            startActivity(intent);
        });
    }
}