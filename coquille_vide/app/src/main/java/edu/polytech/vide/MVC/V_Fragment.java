package edu.polytech.vide;

import static android.os.Build.VERSION_CODES.M;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Observable;
import java.util.Observer;

public class V_Fragment extends Fragment implements Observer {
    private final String TAG = "leo " + getClass().getSimpleName();
    private int lines = 16; // nombre de lignes
    private int columns = 7; // nombre de colonnes
    private final static int NUM_FRAGMENT = 1;
    private Notifiable notifiable;

    private edu.polytech.vide.C controller;
    private TextView[][] textViews;
    private TextView iterations;
    private Button stop;

    private void createGrid(FrameLayout squaresLayout, TextView[][] textViews) {
        // Create the root vertical LinearLayout that will hold the grid
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Add a top divider line
        View topDivider = new View(getContext());
        topDivider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        topDivider.setBackgroundColor(Color.DKGRAY);
        layout.addView(topDivider);

        for (int i = 0; i < lines; i++) {
            // Create a horizontal LinearLayout for each row
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < columns; j++) {
                // Create a TextView to represent a cell
                textViews[i][j] = new TextView(getContext());
                textViews[i][j].setText("");
                textViews[i][j].setTextColor(Color.BLACK);
                textViews[i][j].setGravity(Gravity.CENTER);
                textViews[i][j].setBackgroundColor(Color.WHITE);
                textViews[i][j].setPadding(5, 5, 5, 5);

                // Use layout weight to evenly distribute cells
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                textViews[i][j].setLayoutParams(params);
                rowLayout.addView(textViews[i][j]);

                // Add a vertical divider between cells (except after the last one)
                if (j < columns - 1) {
                    View verticalDivider = new View(getContext());
                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT);
                    verticalDivider.setLayoutParams(dividerParams);
                    verticalDivider.setBackgroundColor(Color.DKGRAY);
                    rowLayout.addView(verticalDivider);
                }
            }

            // Add the completed row to the grid layout
            layout.addView(rowLayout);

            // Add a horizontal divider between rows (except after the last one)
            if (i < lines - 1) {
                View horizontalDivider = new View(getContext());
                horizontalDivider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
                horizontalDivider.setBackgroundColor(Color.DKGRAY);
                layout.addView(horizontalDivider);
            }
        }

        // Add the fully built grid to the parent FrameLayout
        squaresLayout.addView(layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_v, container, false);

        // get nbLines, nbColums
        if (getArguments() != null) {
            int[] params = getArguments().getIntArray(getString(R.string.tableau));
            if (params != null) {
                lines = params[0];
                columns = params[1];
                Log.d(TAG, "reçu (" + lines + ", " + columns + ")");
            } else {
                lines = 0;
                columns = 0;
                Log.d(TAG, "dimensions non reçues");
            }
        }

        FrameLayout squaresLayout = rootView.findViewById(R.id.squares);
        textViews = new TextView[lines][columns];
        createGrid(squaresLayout, textViews);

        controller = new edu.polytech.vide.C(this, lines, columns);
        iterations = rootView.findViewById(R.id.iterations);
        stop = rootView.findViewById(R.id.manageSpeed);

        rootView.findViewById(R.id.button1).setOnClickListener(v -> controller.onButtonClicked(R.id.button1));
        rootView.findViewById(R.id.button2).setOnClickListener(v -> controller.onButtonClicked(R.id.button2));
        rootView.findViewById(R.id.button3).setOnClickListener(v -> controller.onButtonClicked(R.id.button3));

        stop.setOnClickListener(v -> controller.onStopPressed());

        SeekBar seekBar = rootView.findViewById(R.id.seekBar);
        TextView speedView = rootView.findViewById(R.id.speed);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedView.setText(String.valueOf(progress));
                controller.onSpeedChanged(progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edu.polytech.vide.M model = new M(lines, columns);
        edu.polytech.vide.C controller = new edu.polytech.vide.C(this, lines, columns);
        model.addObserver(this);
        this.controller = controller;
    }

    @Override
    public void update(Observable observable, Object o) {
        M model = controller.getModel();
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns; j++) {
                boolean alive = model.isAlive(i, j);
                textViews[i][j].setBackgroundColor(alive ? Color.BLACK : Color.WHITE);
            }
        }
    }

    public void updateIteration(int i) {
        iterations.setText("# " + i);
    }

    public void enableStopButton() {
        stop.setEnabled(true);
    }
}