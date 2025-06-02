package edu.polytech.vide;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant un personnage Valorant
 * Les sources sont ici : http://edu.info06.net/valorant/characters.json
 * Les portraits des personnages sont à l'adresse http://edu.info06.net/valorant/pictures_faces/
 * exemple http://edu.info06.net/valorant/pictures_faces/breach_face.jpg
 */
public class ValorantCharacter implements Parcelable {

    private final String TAG = "frallo " + getClass().getSimpleName();

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private Map<String, String> name;

    @JsonProperty("description")
    private Map<String, String> description;

    @JsonProperty("skills")
    private Map<String, Integer> skills;

    @JsonProperty("value")
    private float value;

    @JsonProperty("pictureFace")
    private String pictureFace;

    @JsonProperty("pictureFull")
    private String pictureFull;

    public ValorantCharacter() {
        super();
    }

    protected ValorantCharacter(Parcel in) {
        id = in.readInt();
        name = readStringMap(in);
        description = readStringMap(in);
        skills = readIntegerMap(in);
        value = in.readFloat();
        pictureFace = in.readString();
        pictureFull = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        writeStringMap(dest, name);
        writeStringMap(dest, description);
        writeIntegerMap(dest, skills);
        dest.writeFloat(value);
        dest.writeString(pictureFace);
        dest.writeString(pictureFull);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ValorantCharacter> CREATOR = new Creator<ValorantCharacter>() {
        @Override
        public ValorantCharacter createFromParcel(Parcel in) {
            return new ValorantCharacter(in);
        }

        @Override
        public ValorantCharacter[] newArray(int size) {
            return new ValorantCharacter[size];
        }
    };

    private void writeStringMap(Parcel dest, Map<String, String> map) {
        if (map == null) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    private Map<String, String> readStringMap(Parcel in) {
        int size = in.readInt();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(in.readString(), in.readString());
        }
        return map;
    }

    private void writeIntegerMap(Parcel dest, Map<String, Integer> map) {
        if (map == null) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(map.size());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeInt(entry.getValue());
        }
    }

    private Map<String, Integer> readIntegerMap(Parcel in) {
        int size = in.readInt();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(in.readString(), in.readInt());
        }
        return map;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() {
        return name.get(Settings.language);
    }

    public String getDescription() {
        return description.get(Settings.language);
    }

    public int getUtility(){
        return skills.get("utility");
    }

    public int getCrowdControl(){
        return skills.get("crowdControl");
    }

    public int getDamage(){
        return skills.get("damage");
    }

    public String getPictureFace() {
        return pictureFace;
    }

    public void setPictureFace(String pictureFace) {
        this.pictureFace = "http://edu.info06.net/valorant"
                + "/pictures_faces/"
                + pictureFace;
    }

    public float getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return getName()+"("+ skills +")";
    }
}