package edu.polytech.vide;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.slider.RangeSlider;

import java.util.List;


public class Screen2Fragment extends Fragment {

    private final String TAG = "frallo "+getClass().getSimpleName();
    private final static int NUM_FRAGMENT = 2;
    private Notifiable notifiable;
    List<Float> rangingBarValues ;

    public Screen2Fragment() {
        Log.d(TAG,"screenFragment type 2 created");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get current activated index menu
        if (getArguments() != null) {
            float[] received = getArguments().getFloatArray(getString(R.string.seekbarvalue));
            rangingBarValues = List.of(received[0],received[1]);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof Notifiable) {
            notifiable = (Notifiable) requireActivity();
            //Log.d(TAG, "Class " + requireActivity().getClass().getSimpleName() + " implements Notifiable.");
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen2, container, false);
        RangeSlider rangeSlider = view.findViewById(R.id.rangeBar);

        // Gestion des changements de valeurs
        rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            rangingBarValues = slider.getValues();
            notifiable.onDataChange(NUM_FRAGMENT, rangingBarValues);
        });

        view.findViewById(R.id.go).setOnClickListener(clic -> {
            notifiable.onDataChange(NUM_FRAGMENT, rangingBarValues);
            notifiable.onClick(NUM_FRAGMENT);
        });

        rangeSlider.setValues(rangingBarValues);
        return view;
    }
}