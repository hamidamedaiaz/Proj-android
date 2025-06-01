package edu.polytech.vide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * ControlActivity has two fragments, as shown in the view below:
 *     Screen1Fragment (dynamic fragment) - This area uses 86% of the surface (6/7).
 *     MenuFragment (static fragment) - This area uses 14% of the surface (1/7).
 *
 * The role of this activity is essential: it acts as the controller of your application.
 *     It has the attribute private int seekBarValue = 30; (30 is the default value).
 *     It has an attribute private TanquinState tanquinState; (class to be created for exercise 7).
 *     It receives information from its fragments (from the menu and the main display area).
 *     It provides information to its fragments (to the menu and the main display area).
 * @author F. Rallo - march 2025
 */
public class ControlActivity extends AppCompatActivity implements Menuable, Notifiable {
    private final String TAG = "frallo "+getClass().getSimpleName();

    // Attributs selon les spécifications
    private int seekBarValue = 30;
    private TaquinState taquinState; // État du jeu Taquin

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        int menuNumber = 1;

        //get index from MainActivity
        Intent intent = getIntent();
        if(intent!=null){
            menuNumber = intent.getIntExtra(getString(R.string.index),1);
            Log.d(TAG,"received menu#"+menuNumber+" from MainActivity");
        }

        //index to start menu  --> becomes dynamic
        Bundle args = new Bundle();
        args.putInt(getString(R.string.index), menuNumber);
        MenuFragment menu = new MenuFragment();
        menu.setArguments(args);
        Log.d(TAG,"send value " + menuNumber + " to MenuFragment");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_menu, menu);
        transaction.commit();
    }

    @Override
    public void onMenuChange(int index) {
        Fragment mainFragment = null;

        // Créer le fragment approprié selon l'index
        switch (index){
            case TAG_MENU1-1:
                mainFragment = new Screen1Fragment();
                break;
            case TAG_MENU2-1:
                mainFragment = new Screen2Fragment();
                break;
            case TAG_MENU3-1:
                mainFragment = new Screen3Fragment();
                break;
            case TAG_MENU4-1:
                mainFragment = new Screen4Fragment();
                // Transmettre l'état du Taquin s'il existe
                if (taquinState != null) {
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.taquinState), taquinState);
                    mainFragment.setArguments(args);
                }
                break;
            case TAG_MENU5-1:
                mainFragment = new Screen5Fragment();
                break;
            case TAG_MENU6-1:
                mainFragment = new Screen6Fragment();
                break;
            default:
                mainFragment = new Screen1Fragment();  //why not ?
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, mainFragment);
        //TODO: addToBackStack doesn't work in all cases
        transaction.addToBackStack(null); // to be able to browse with back button...
        transaction.commit();
    }

    @Override
    public void onClick(int numFragment) {
        Log.d(TAG, "Menu TAG#" + numFragment + " has clicked!");

        switch (numFragment) {
            case TAG_MENU3:
                Log.d(TAG, "Pokémon game button clicked!");
                break;
            case TAG_MENU4:
                Log.d(TAG, "Taquin game interaction!");
                break;
            case TAG_MENU5:
                Log.d(TAG, "Camera button clicked!");
                break;
            case TAG_MENU6:
                Log.d(TAG, "Map permission granted!");
                break;
        }
    }

    @Override
    public void onDataChange(int numFragment, Object data) {
        Log.d(TAG,"received "+ data +" from fragment TAG#"+numFragment);

        // Gestion spécifique selon le fragment
        switch (numFragment) {
            case TAG_MENU2:
                // Données du seekbar
                if (data instanceof Integer) {
                    seekBarValue = (Integer) data;
                    Log.d(TAG, "SeekBar value updated to: " + seekBarValue);
                }
                break;

            case TAG_MENU3:
                // Données du jeu Pokémon
                Log.d(TAG, "Pokémon game data: " + data.toString());
                break;

            case TAG_MENU4:
                // Données du jeu Taquin
                if (data instanceof TaquinState) {
                    taquinState = (TaquinState) data;
                    Log.d(TAG, "Taquin state saved: hole at position " + taquinState.getHolepos());
                } else {
                    Log.d(TAG, "Taquin game event: " + data.toString());
                }
                break;

            case TAG_MENU5:
                // Données de la caméra
                Log.d(TAG, "Camera event: " + data.toString());
                break;

            case TAG_MENU6:
                // Données de la carte/localisation
                Log.d(TAG, "Map/Location data: " + data.toString());
                break;
        }
    }

    // Getters pour que les fragments puissent accéder aux données partagées
    public int getSeekBarValue() {
        return seekBarValue;
    }

    public TaquinState getTaquinState() {
        return taquinState;
    }

    public void setTaquinState(TaquinState state) {
        this.taquinState = state;
    }
}