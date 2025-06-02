package edu.polytech.vide;

import static android.os.Build.VERSION_CODES.M;

public class C {
    private final String TAG = "leo " + getClass().getSimpleName();
    private int currentSpeed = 0;
    private edu.polytech.vide.V_Fragment view;

    private final android.os.Handler handler = new android.os.Handler();
    private Runnable updateTask;
    private boolean isUpdating = false;
    private int iterations = 0;
    private M model;

    public C(edu.polytech.vide.V_Fragment view, int lines, int columns) {
        this.view = view;
        this.model = new M(lines, columns);
        model.addObserver(view); // la vue observe le modèle
    }

    public M getModel() {
        return model;
    }

    public void onButtonClicked(int id) {
        stopModelUpdates();
        iterations = 0;
        view.updateIteration(iterations);

        if (id == R.id.button1) {
            model.setPlaneur();
        } else if (id == R.id.button2) {
            model.setCarre();
        } else {
            model.setPhare();
        }

        if (currentSpeed > 0) startModelUpdates();
    }

    public void onSpeedChanged(int speed) {
        currentSpeed = speed;

        if (speed == 0) {
            stopModelUpdates();
        } else {
            view.enableStopButton();
            stopModelUpdates();
            startModelUpdates();
        }
    }

    public void onStopPressed() {
        if (isUpdating) {
            stopModelUpdates();
        } else {
            startModelUpdates();
        }
    }

    private void startModelUpdates() {
        if (isUpdating || currentSpeed==0 ) return;

        isUpdating = true;
        updateTask = new Runnable() {
            @Override
            public void run() {
                model.nextGeneration();
                iterations++;
                view.updateIteration(iterations);
                handler.postDelayed(this, 1000/currentSpeed);
            }
        };
        handler.post(updateTask);
    }

    private void stopModelUpdates() {
        if (isUpdating && updateTask != null) {
            handler.removeCallbacks(updateTask);
            isUpdating = false;
        }
    }
}