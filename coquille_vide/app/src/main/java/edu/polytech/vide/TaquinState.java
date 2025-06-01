package edu.polytech.vide;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class TaquinState implements Parcelable {
    private int holepos;
    private Integer[] buttons;

    public TaquinState(int holepos, List<Integer> buttons) {
        this.holepos = holepos;
        this.buttons = buttons.toArray(new Integer[0]);
    }

    public TaquinState(Parcel in) {
        this.holepos = in.readInt();
        this.buttons = (Integer[]) in.readArray(Integer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(holepos);
        dest.writeArray(buttons);
    }

    public static Parcelable.Creator<TaquinState> getCreator() {
        return CREATOR;
    }

    // non fini car unused
    public static final Parcelable.Creator<TaquinState> CREATOR = new Parcelable.Creator() {
        @Override
        public TaquinState createFromParcel(Parcel in) {
            return new TaquinState(in);
        }

        @Override
        public TaquinState[] newArray(int size) {
            return new TaquinState[size];
        }
    };

    public int getHolepos() {
        return holepos;
    }

    public Integer[] getButtons() {
        return buttons;
    }
}