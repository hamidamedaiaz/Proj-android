package edu.polytech.vide;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

public class TaquinState implements Parcelable {
    private List<Integer> buttonsValue = new ArrayList<>();
    private boolean startVisibility;
    private int holePosition;

    public TaquinState(List<Button> buttons, int holePosition) {
        buttons.forEach(button -> buttonsValue.add(Integer.parseInt(button.getText().toString())));
        this.startVisibility = !buttons.stream().allMatch(button -> button.getText().toString().equals(button.getTag().toString()));
        this.holePosition = holePosition;
    }

    protected TaquinState(Parcel in) {
        buttonsValue = new ArrayList<>();
        in.readList(buttonsValue, Integer.class.getClassLoader());
        startVisibility = in.readByte() != 0;
        holePosition = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(buttonsValue);
        dest.writeByte((byte) (startVisibility ? 1 : 0));
        dest.writeInt(holePosition);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaquinState> CREATOR = new Creator<TaquinState>() {
        @Override
        public TaquinState createFromParcel(Parcel in) {
            return new TaquinState(in);
        }

        @Override
        public TaquinState[] newArray(int size) {
            return new TaquinState[size];
        }
    };

    public List<Integer> getButtonsValue() {
        return buttonsValue;
    }

    public boolean isStartVisibility() {
        return startVisibility;
    }

    public int getHolePosition() {
        return holePosition;
    }
}