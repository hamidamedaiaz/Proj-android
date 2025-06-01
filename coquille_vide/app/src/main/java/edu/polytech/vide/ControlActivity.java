package edu.polytech.vide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 **
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

    private int seekBarValue = 30;
    private TaquinState taquinState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        int menuNumber = 1;
        //get index from MainActivity
        Intent intent = getIntent();
        if(intent!=null){
            menuNumber = intent.getIntExtra(getString(R.string.index),1);
            Log.d(TAG,"received menu#"+menuNumber);
        }

        //index to start menu  --> becomes dynamic
        Bundle args = new Bundle();
        args.putInt(getString(R.string.index), menuNumber);
        MenuFragment menu = new MenuFragment();
        menu.setArguments(args);
        Log.d(TAG,"send to MenuFragment menu#"+menuNumber);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_menu, menu);
        transaction.commit();
    }

    @Override
    public void onMenuChange(int index) {
        Fragment mainFragment = null;
        switch (index){
            case TAG_MENU1-1: mainFragment = new Screen1Fragment(); break;
            case TAG_MENU2-1: {
                mainFragment = new Screen2Fragment();
                Bundle args = new Bundle();
                args.putInt(getString(R.string.seekbarvalue), seekBarValue);
                mainFragment.setArguments(args);
            } break;
            case TAG_MENU3-1: mainFragment = new Screen3Fragment(); break;
            case TAG_MENU4-1: {
                mainFragment = new Screen4Fragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.tanquinstate), taquinState);
                mainFragment.setArguments(args);
            } break;
            case TAG_MENU5-1: mainFragment = new Screen5Fragment(); break;
            case TAG_MENU6-1: mainFragment = new Screen6Fragment(); break;
            default: mainFragment = new Screen1Fragment();  //why not ?
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, mainFragment);
        //TODO: addToBackStack doesn't work in all cases
        transaction.addToBackStack(null); // to be able to browse with back button...
        transaction.commit();
    }

    @Override
    public void onClick(int numFragment) {
        Log.d(TAG, "Menu " + numFragment + " has clicked!");
    }

    @Override
    public void onDataChange(int numFragment, Object data) {
        Log.d(TAG,"received "+ data + " from fragment#" + numFragment);
        switch(numFragment){
            case 1: break;
            case 2: seekBarValue = (Integer)data; break;
            case 3: break;
            case 4: taquinState = (TaquinState)data; break;
            case 5: break;
            case 6: break;
        }
    }
}