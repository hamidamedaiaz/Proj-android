package edu.polytech.vide;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ValorantListFragment extends Fragment {
    private final String TAG = "frallo " + getClass().getSimpleName();
    private  CharacterAdapter adapter;
    private Notifiable notifiable;
    private final List<ValorantCharacter> displayedCharacters = new ArrayList<>(); //displayed list
    private int completeSize = 0;

    public ValorantListFragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get current activated index menu
        if (getArguments() != null) {
            displayedCharacters.addAll( getArguments().getParcelableArrayList(getString(R.string.characterlist)) );
            completeSize = getArguments().getInt(getString(R.string.size));
            Log.d(TAG,"--> displayedCharacters " + displayedCharacters);
            Log.d(TAG,"--> displayedCharacters size " + displayedCharacters.size());

            ValorantCharacter test = displayedCharacters.get(0);
            Log.d(TAG,">test : " + test);
            Log.d(TAG,">test.utility" + test.getUtility());
            Log.d(TAG,">test.cc" + test.getCrowdControl());
            Log.d(TAG,">test.damage" + test.getDamage());
        }

    }

    public ValorantCharacter getItemList(int index){
        return displayedCharacters.get(index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valorant_list, container, false);
        ListView listview = view.findViewById(R.id.listView);

        //MAJ affichage
        ((TextView)view.findViewById(R.id.cpt)).setText(displayedCharacters.size()+"/"+completeSize);

        //Création et initialisation de l'Adapter
        adapter = new CharacterAdapter(displayedCharacters, notifiable);

        //Initialisation de la liste avec les données
        listview.setAdapter(adapter);
        return view;
    }
}