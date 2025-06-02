package edu.polytech.vide;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
public class ControlActivity extends AppCompatActivity implements Menuable, Notifiable, PostExecuteActivity<ValorantCharacter> {
    private final String TAG = "frallo "+getClass().getSimpleName();


    private static final int REQUEST_NOTIFICATION_PERMISSION = 101;
    private List<Float> rangingBarValues = Arrays.asList(10f, 50f);
    private TaquinState tanquinState;
    private NotificationService notificationService;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NotificationService.LocalBinder binder = (NotificationService.LocalBinder) service;
            notificationService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notificationService = null;
        }
    };

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
        //Log.d(TAG,"send to MenuFragment menu#"+menuNumber);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_menu, menu);
        transaction.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},  REQUEST_NOTIFICATION_PERMISSION);
            }
            else {
                startNotificationService(); // on a déjà la permission
            }
        }
        else {
            startNotificationService(); // pas besoin de permission en dessous d'API 33
        }
    }
    public void startMVC(int[] tabl) {
        Log.d(TAG, "MVC Started");

        edu.polytech.vide.V_Fragment gameOfLifeFragment = new edu.polytech.vide.V_Fragment();
        Bundle args = new Bundle();
        args.putIntArray(getString(R.string.tableau), tabl); // example size
        gameOfLifeFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, gameOfLifeFragment);
        transaction.commit();
    }




    @Override
    protected void onStart() {
        super.onStart();
        // Lier le service
        Intent intent = new Intent(this, NotificationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (notificationService != null) {
            // Assure-toi de détacher le service quand tu n'en as plus besoin
            unbindService(serviceConnection);
        }
    }

    // Appelé après que l'utilisateur accepte/refuse la permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startNotificationService(); // permission accordée
            } else {
                Log.e(TAG, "Permission POST_NOTIFICATIONS refused");
            }
        }
    }

    private void startNotificationService() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);
    }

    @Override
    public void onMenuChange(int index) {
        Fragment fragment = null;
        switch (index){
            case 0: fragment = new Screen1Fragment(); break;
            case 1: {
                //Log.d(TAG,"sending rangingBarValues = "+rangingBarValues.get(0)+", "+rangingBarValues.get(1));
                fragment = new Screen2Fragment();
                Bundle args = new Bundle();
                float[] send = {rangingBarValues.get(0),rangingBarValues.get(1)};
                args.putFloatArray(getString(R.string.seekbarvalue), send);
                fragment.setArguments(args);
            }  break;
            case 2: fragment = new Screen3Fragment(); break;
            case 3: {
                fragment = new Screen4Fragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.tanquinstate), tanquinState);
                fragment.setArguments(args);
            } break;
            case 4: fragment = new Screen5Fragment();break;
            case 5: fragment = new Screen6Fragment(); break;
            default: fragment = new Screen1Fragment();  //why not ?
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, fragment);
        //TODO: addToBackStack doesn't work in all cases
        //transaction.addToBackStack(null); // to be able to browse with back button...
        transaction.commit();
    }

    @Override
    public void onClick(int numFragment) {
        Log.d(TAG, "Menu has clicked!");
        switch(numFragment){
            case 1:  {
                Log.d(TAG,"--> send " );
                notificationService.sendNotification("Hello", "Notification depuis l'Activity");
            }
            break;
            case 2:  {
                Settings.language = getString(R.string.language);
                String url = "http://edu.info06.net/valorant/characters.json";
                //todo: try to change context from MainActivity.this in getApplicationContext()
                new HttpAsyncGet<>(url, ValorantCharacter.class, this, null );
            }  break;
            case 3:  break;
            case 4:  break;
            case 5:  break;
            case 6:  break;
        }
    }

    @Override
    public void onDataChange(int numFragment, Object data) {
        //Log.d(TAG,"received "+ data + " data from fragment#"+numFragment);
        switch(numFragment){
            case 1:  break;
            case 2:  // when arrived here, the screenFragment is or Screen2Fragment (select ranging values) or ValorantListFragment (display characters)
                Fragment screenFragment =  getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                //Log.d(TAG,"screenFragment "+screenFragment.getClass().getSimpleName() );
                if(screenFragment.getClass().getSimpleName().equals( "Screen2Fragment" ) ){
                    rangingBarValues = (List<Float>)data;
                }
                else {
                    //get the valorant character selected
                    ValorantCharacter valorantCharacter = ((ValorantListFragment)screenFragment).getItemList((Integer)data);
                    Log.d(TAG,"valorantCharacter selected is " + valorantCharacter.getName() );

                    //display info of selected character
                    Fragment fragment = new ValorantFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.character), valorantCharacter);
                    fragment.setArguments(args);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_main, fragment);
                    //TODO: addToBackStack doesn't work in all cases
                    //transaction.addToBackStack(null); // to be able to browse with back button...
                    transaction.commit();
                }
                break;
            case 3:  break;
            case 4:  tanquinState = (TaquinState)data; break;
            case 5:  break;
            case 6:  break;
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onPostExecute(List<ValorantCharacter> itemList) {
        Fragment fragment = new ValorantListFragment();

        //prepare argument --> displayedCharacters
        ArrayList<ValorantCharacter> displayedCharacters = new ArrayList<>();
        if(displayedCharacters.isEmpty()) {
            for(ValorantCharacter valorant : itemList ){
                if ( valorant.getValue()>=rangingBarValues.get(0)/20  && valorant.getValue()<=rangingBarValues.get(1)/20)
                    displayedCharacters.add(valorant);
            }
        }

        Log.d(TAG,"itemList : " + itemList);
/*
        ValorantCharacter test = displayedCharacters.get(0);
        Log.d(TAG,"test : " + test);
        Log.d(TAG,"test.utility" + test.getUtility());
        Log.d(TAG,"test.cc" + test.getCrowdControl());
        Log.d(TAG,"test.damage" + test.getDamage());


 */
        Bundle args = new Bundle();
        args.putParcelableArrayList(getString(R.string.characterlist), displayedCharacters);
        args.putInt(getString(R.string.size), itemList.size());

        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, fragment);
        //transaction.addToBackStack(null); // to be able to browse with back button...
        transaction.commit();

    }
}
