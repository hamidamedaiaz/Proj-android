package edu.polytech.vide;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ValorantFragment extends Fragment {
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;

    private ValorantCharacter valorantCharacter;

    public ValorantFragment() {
        // Required empty public constructor
    }



    private void changeViewSize(View view, int width, int height){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        LinearLayout.LayoutParams constraintParams = (LinearLayout.LayoutParams) params;
        constraintParams.width = (int) (width * notifiable.getContext().getResources().getDisplayMetrics().density);
        constraintParams.height = (int) (height * notifiable.getContext().getResources().getDisplayMetrics().density);
        view.setLayoutParams(constraintParams);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            valorantCharacter = getArguments().getParcelable(getString(R.string.character));
            //Log.d(TAG,"*** " + valorantCharacter.getName() );
        }
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
        View layoutItem = inflater.inflate(R.layout.fragment_vallorant, container, false);
        ((TextView)layoutItem.findViewById(R.id.name)).setText(valorantCharacter.getName());
        //((ImageView)layoutItem.findViewById(R.id.characterImage)).setImageResource(valorantCharacter.getPicture());
        Picasso.get().load(valorantCharacter.getPictureFace()).into(((ImageView)layoutItem.findViewById(R.id.characterImage)));
        ((TextView)layoutItem.findViewById(R.id.info)).setText(valorantCharacter.getDescription());
        ((RatingBar)layoutItem.findViewById(R.id.ratingBar)).setRating(valorantCharacter.getValue());

        View utility = layoutItem.findViewById(R.id.utility);
        View crowdControl = layoutItem.findViewById(R.id.cc);
        View damage = layoutItem.findViewById(R.id.dmg);
        changeViewSize(utility, (int)(150*0.01*valorantCharacter.getUtility()), 5);
        changeViewSize(crowdControl, (int)(150*0.01*valorantCharacter.getCrowdControl()), 5);
        changeViewSize(damage, (int)(150*0.01*valorantCharacter.getDamage()), 5);
        return layoutItem;
    }
}