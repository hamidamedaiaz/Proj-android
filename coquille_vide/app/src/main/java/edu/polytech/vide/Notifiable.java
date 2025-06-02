package edu.polytech.vide;

import android.content.Context;

public interface Notifiable {
    void onClick(int numFragment);
    void onDataChange(int numFragment, Object object);

    Context getContext();
}

