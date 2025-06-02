package edu.polytech.vide;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.Arrays;

public class Screen1Fragment extends Fragment {
    private static final String TAG = "leo " + Screen1Fragment.class.getSimpleName();
    private Notifiable notifiable;
    private final int NUM_FRAGMENT = 1;
    private int[] dimensions = {12, 12};  // default value

    public Screen1Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            int[] tabl = args.getIntArray(getString(R.string.tableau));
            if (tabl != null) {
                dimensions = tabl;
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart(); // link service
        Log.d(TAG,"activity started --> service linked" );
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
        view.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControlActivity activity = (ControlActivity) getActivity();
                activity.startMVC(dimensions);
            }
        });

        createWatcher(view);
        /*
        view.findViewById(R.id.buttonGo).setOnClickListener(clic -> {
            notifiable.onClick(NUM_FRAGMENT);
        });*/
        return view;
    }

    private void createWatcher(View view) {
        EditText rowsInput = view.findViewById(R.id.rows);
        EditText columnsInput = view.findViewById(R.id.columns);
        Button go = view.findViewById(R.id.go);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String rowsString = rowsInput.getText().toString();
                String columnsString = columnsInput.getText().toString();

                if (!rowsString.isEmpty() && !columnsString.isEmpty()) {
                    dimensions[0] = Integer.parseInt(rowsString);
                    dimensions[1] = Integer.parseInt(columnsString);

                    go.setVisibility(View.VISIBLE);
                } else {
                    go.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        rowsInput.addTextChangedListener(textWatcher);
        columnsInput.addTextChangedListener(textWatcher);
    }
}
