package edu.polytech.vide;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;


public class MenuFragment extends Fragment{
    private final String TAG = "frallo "+getClass().getSimpleName();
    private Menuable menuable;
    private int currentActivatedIndex = 0;


    public MenuFragment() {
        Log.d(TAG, "MenuFragment created");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        List<ImageView> imageViews = findPicturesMenuFromId( view.findViewById(R.id.itemsMenu));

        //get current activated index menu
        if (getArguments() != null) {
            currentActivatedIndex = getArguments().getInt(getString(R.string.index), 1)-1;  //convert menu number to index
        }
        imageViews.get(currentActivatedIndex).setImageResource(  view.getResources().getIdentifier("menu"+(currentActivatedIndex+1)+"_s", "mipmap", view.getContext().getPackageName()) );
        //Log.d(TAG, "BEGIN : menu index " +  currentActivatedIndex + " is selected");

        //notify activity the menu is selected
        menuable.onMenuChange(currentActivatedIndex);

        TextView text = view.findViewById(R.id.txtFragmentMenu);
        text.setText("Menu");


        for(ImageView imageView : imageViews) {
            imageView.setOnClickListener( menu -> {
                int oldIndex = currentActivatedIndex;
                currentActivatedIndex = Integer.parseInt(imageView.getTag().toString())-1;

                //notify activity currentIndexChange
                menuable.onMenuChange(currentActivatedIndex);

                //display old menu in gray
                imageViews.get(oldIndex).setImageResource(  view.getResources().getIdentifier("menu"+(oldIndex+1), "mipmap", view.getContext().getPackageName()) );

                //display new menu in green
                ((ImageView)menu).setImageResource(  view.getResources().getIdentifier("menu"+(currentActivatedIndex+1)+"_s", "mipmap", view.getContext().getPackageName()) );
            });
        }
        return view;
    }





    // browse rootView and sort all buttons in "buttons" list
    private List<ImageView> findPicturesMenuFromId(View view) {
        List<ImageView> pictures = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof ImageView) {
                    String idString = getResources().getResourceEntryName(child.getId());
                    if (idString.matches("menu[1-9]?")) {
                        pictures.add((ImageView) child);
                    }
                }
            }
        }
        return pictures;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (requireActivity() instanceof Menuable) {
            menuable = (Menuable) requireActivity();
            Log.d(TAG, "Class " + requireActivity().getClass().getSimpleName() + " implements Notifiable.");
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

}
