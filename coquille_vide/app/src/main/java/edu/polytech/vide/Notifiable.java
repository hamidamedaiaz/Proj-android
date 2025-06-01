package edu.polytech.vide;

public interface Notifiable {
    int TAG_MENU1 = 1;
    int TAG_MENU2 = 2;
    int TAG_MENU3 = 3;
    int TAG_MENU4 = 4;
    int TAG_MENU5 = 5;
    int TAG_MENU6 = 6;
    int TAG_MENU7 = 7;
    void onClick(int numFragment);
    void onDataChange(int numFragment, Object object);
}

