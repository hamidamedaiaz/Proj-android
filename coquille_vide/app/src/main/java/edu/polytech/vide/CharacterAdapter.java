package edu.polytech.vide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter pour afficher la liste des personnages Valorant
 * Modifié par Fred le 16/02/2024.
 */
public class CharacterAdapter extends BaseAdapter {
    private final String TAG = "frallo " + getClass().getSimpleName();
    private List<ValorantCharacter> mangaCharacters;
    private LayoutInflater mInflater;
    private Notifiable callBackActivity;

    public CharacterAdapter(List<ValorantCharacter> mangaCharacters, Notifiable callBackActivity) {
        this.mangaCharacters = mangaCharacters;
        this.callBackActivity = callBackActivity;
        mInflater = LayoutInflater.from(((ControlActivity) callBackActivity));
    }

    public int getCount() {
        return mangaCharacters.size();
    }

    public Object getItem(int position) {
        return mangaCharacters.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private void changeViewSize(View view, int width, int height){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        LinearLayout.LayoutParams constraintParams = (LinearLayout.LayoutParams) params;
        constraintParams.width = (int) (width * ((ControlActivity) callBackActivity).getResources().getDisplayMetrics().density);
        constraintParams.height = (int) (height * ((ControlActivity) callBackActivity).getResources().getDisplayMetrics().density);
        view.setLayoutParams(constraintParams);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ConstraintLayout layoutItem;

        // Réutilisation des layouts
        layoutItem = (ConstraintLayout) (convertView == null ?
                mInflater.inflate(R.layout.character_layout, parent, false) : convertView);

        // Récupération des éléments
        TextView name = layoutItem.findViewById(R.id.name);
        View utility = layoutItem.findViewById(R.id.utility);
        View crowdControl = layoutItem.findViewById(R.id.cc);
        View damage = layoutItem.findViewById(R.id.dmg);
        ImageView picture = layoutItem.findViewById(R.id.picture);
        RatingBar ratingBar = layoutItem.findViewById(R.id.ratingBar);

        // Renseignement des valeurs
        ValorantCharacter character = mangaCharacters.get(position);
        name.setText(character.getName());
        Picasso.get().load(character.getPictureFace()).into(picture);
        ratingBar.setRating(character.getValue());

        changeViewSize(utility, (int)(150*0.01*character.getUtility()), 5);
        changeViewSize(crowdControl, (int)(150*0.01*character.getCrowdControl()), 5);
        changeViewSize(damage, (int)(150*0.01*character.getDamage()), 5);

        layoutItem.setOnClickListener( click -> {
            callBackActivity.onDataChange(2, position);
        });

        return layoutItem;
    }
}