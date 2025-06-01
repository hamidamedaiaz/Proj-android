package edu.polytech.vide;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Screen2Fragment extends Fragment {
    private final String TAG = "frallo "+getClass().getSimpleName();
    private final static int NUM_FRAGMENT = 2;
    private Notifiable notifiable;
    private int seekBarValue;

    public Screen2Fragment() {
        Log.d(TAG,"screenFragment type 2 created");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get current activated index menu
        if (getArguments() != null) {
            seekBarValue = getArguments().getInt(getString(R.string.seekbarvalue), 0);
        }
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
        View view = inflater.inflate(R.layout.fragment_screen2, container, false);

        // Initialiser les vues
        TextView speedText = view.findViewById(R.id.speed);
        SeekBar seekBar = view.findViewById(R.id.seekBar);
        Button button = view.findViewById(R.id.go);

        // Configurer les valeurs initiales
        speedText.setText("" + seekBarValue);
        seekBar.setProgress(seekBarValue);
        notifiable.onDataChange(NUM_FRAGMENT, seekBarValue);

        // Configurer le listener pour la SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                seekBarValue = value;
                notifiable.onDataChange(NUM_FRAGMENT, seekBarValue);
                Log.d(TAG, "onProgressChanged = " + seekBarValue);
                speedText.setText("" + seekBarValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Configurer le listener pour le bouton
        button.setOnClickListener(clic -> notifiable.onClick(NUM_FRAGMENT));

        return view;
    }
}