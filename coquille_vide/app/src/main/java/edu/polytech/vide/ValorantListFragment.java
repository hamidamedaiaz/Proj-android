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

/**
 * Fragment pour afficher la liste des personnages Valorant - Version corrigée
 */
public class ValorantListFragment extends Fragment {
    private final String TAG = "frallo " + getClass().getSimpleName();
    private CharacterAdapter adapter;
    private Notifiable notifiable;
    private final List<ValorantCharacter> displayedCharacters = new ArrayList<>();
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

        try {
            // CORRECTION 1: Gestion sécurisée des arguments
            if (getArguments() != null) {
                ArrayList<ValorantCharacter> characters = getArguments().getParcelableArrayList(getString(R.string.characterlist));
                if (characters != null) {
                    displayedCharacters.addAll(characters);
                }
                completeSize = getArguments().getInt(getString(R.string.size), 0);

                Log.d(TAG, "--> displayedCharacters size: " + displayedCharacters.size());
                Log.d(TAG, "--> complete size: " + completeSize);

                // CORRECTION 2: Vérification avant d'accéder aux éléments
                if (!displayedCharacters.isEmpty()) {
                    ValorantCharacter test = displayedCharacters.get(0);
                    Log.d(TAG, ">test : " + test.getName());
                    Log.d(TAG, ">test.utility: " + test.getUtility());
                    Log.d(TAG, ">test.cc: " + test.getCrowdControl());
                    Log.d(TAG, ">test.damage: " + test.getDamage());
                } else {
                    Log.w(TAG, "No characters to display");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
        }
    }

    public ValorantCharacter getItemList(int index) {
        // CORRECTION 3: Vérification des limites
        if (index >= 0 && index < displayedCharacters.size()) {
            return displayedCharacters.get(index);
        } else {
            Log.e(TAG, "Index out of bounds: " + index + " (size: " + displayedCharacters.size() + ")");
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_valorant_list, container, false);
            ListView listview = view.findViewById(R.id.listView);

            // CORRECTION 4: Vérification des vues
            TextView cptTextView = view.findViewById(R.id.cpt);
            if (cptTextView != null) {
                cptTextView.setText(displayedCharacters.size() + "/" + completeSize);
            }

            // CORRECTION 5: Création sécurisée de l'adapter
            if (notifiable != null) {
                adapter = new CharacterAdapter(displayedCharacters, notifiable);
                listview.setAdapter(adapter);
            } else {
                Log.e(TAG, "Notifiable is null, cannot create adapter");
            }

            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView", e);
            // Retourner une vue vide en cas d'erreur
            return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
        }
    }
}