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
 * Fragment pour afficher les détails d'un personnage Valorant - Version corrigée
 */
public class ValorantFragment extends Fragment {
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;
    private ValorantCharacter valorantCharacter;

    public ValorantFragment() {
        // Required empty public constructor
    }

    private void changeViewSize(View view, int width, int height) {
        try {
            if (view != null && notifiable != null) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (params instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams constraintParams = (LinearLayout.LayoutParams) params;
                    constraintParams.width = (int) (width * notifiable.getContext().getResources().getDisplayMetrics().density);
                    constraintParams.height = (int) (height * notifiable.getContext().getResources().getDisplayMetrics().density);
                    view.setLayoutParams(constraintParams);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error changing view size", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (getArguments() != null) {
                valorantCharacter = getArguments().getParcelable(getString(R.string.character));
                if (valorantCharacter != null) {
                    Log.d(TAG, "Character loaded: " + valorantCharacter.getName());
                } else {
                    Log.w(TAG, "No character data received");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
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
        try {
            View layoutItem = inflater.inflate(R.layout.fragment_vallorant, container, false);

            // CORRECTION 1: Vérification que le personnage existe
            if (valorantCharacter == null) {
                Log.e(TAG, "ValorantCharacter is null, cannot populate view");
                return layoutItem; // Retourner la vue vide
            }

            // CORRECTION 2: Peuplement sécurisé des vues
            populateViews(layoutItem);

            return layoutItem;
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView", e);
            // Retourner une vue basique en cas d'erreur
            return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
        }
    }

    private void populateViews(View layoutItem) {
        try {
            // Nom du personnage
            TextView nameView = layoutItem.findViewById(R.id.name);
            if (nameView != null && valorantCharacter.getName() != null) {
                nameView.setText(valorantCharacter.getName());
            }

            // Image du personnage
            ImageView characterImage = layoutItem.findViewById(R.id.characterImage);
            if (characterImage != null && valorantCharacter.getPictureFace() != null) {
                Picasso.get()
                        .load(valorantCharacter.getPictureFace())
                        .error(R.drawable.rallo1) // Image par défaut en cas d'erreur
                        .into(characterImage);
            }

            // Description
            TextView infoView = layoutItem.findViewById(R.id.info);
            if (infoView != null && valorantCharacter.getDescription() != null) {
                infoView.setText(valorantCharacter.getDescription());
            }

            // Rating bar
            RatingBar ratingBar = layoutItem.findViewById(R.id.ratingBar);
            if (ratingBar != null) {
                ratingBar.setRating(valorantCharacter.getValue());
            }

            // Barres de compétences
            setupSkillBars(layoutItem);

        } catch (Exception e) {
            Log.e(TAG, "Error populating views", e);
        }
    }

    private void setupSkillBars(View layoutItem) {
        try {
            View utility = layoutItem.findViewById(R.id.utility);
            View crowdControl = layoutItem.findViewById(R.id.cc);
            View damage = layoutItem.findViewById(R.id.dmg);

            if (utility != null && crowdControl != null && damage != null) {
                changeViewSize(utility, (int)(150 * 0.01 * valorantCharacter.getUtility()), 5);
                changeViewSize(crowdControl, (int)(150 * 0.01 * valorantCharacter.getCrowdControl()), 5);
                changeViewSize(damage, (int)(150 * 0.01 * valorantCharacter.getDamage()), 5);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up skill bars", e);
        }
    }
}