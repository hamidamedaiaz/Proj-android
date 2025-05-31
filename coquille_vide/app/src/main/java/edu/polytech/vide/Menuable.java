package edu.polytech.vide;

public interface Menuable {
    public static final int TAG_MENU1 = 1;   // à récupérer
    public static final int TAG_MENU2 = 2;    //valorant  1/3
    public static final int TAG_MENU3 = 3;   // TP noté
    public static final int TAG_MENU4 = 4;   //Taquin (à récupérer)
    public static final int TAG_MENU5 = 5;   //camera
    public static final  int TAG_MENU6 = 6;
    public static final int TAG_MENU7 = 7;   //valorant 2/3
    public static final int TAG_MENU8 = 8;   //valorant 3/3
    void onMenuChange(int index);
}
