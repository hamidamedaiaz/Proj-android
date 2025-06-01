package edu.polytech.vide;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Screen4Fragment extends Fragment {
    private final static int NUM_FRAGMENT = 4;
    private Notifiable notifiable;
    private static final int HIDDEN_BUTTON = 9;
    private int holepos = HIDDEN_BUTTON;  //button #9
    private List<Button> buttons;
    private TaquinState tanquinState = null;

    private final String TAG = "frallo "+getClass().getSimpleName();

    public Screen4Fragment() {
        Log.d(TAG,"screenFragment type 4 created");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get current activated index menu
        if (getArguments() != null) {
            tanquinState = getArguments().getParcelable(getString(R.string.tanquinstate));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof Notifiable) {
            notifiable = (Notifiable) requireActivity();
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen4, container, false);
        buttons = findButtonsWithId(view.findViewById(R.id.rootview));

        Collections.sort(buttons, (button1, button2) -> {
            // Comparez les noms des boutons
            String nom1 = getResources().getResourceEntryName(button1.getId());
            String nom2 = getResources().getResourceEntryName(button2.getId());
            return nom1.compareTo(nom2);
        });
        buttons.get(8).setVisibility(View.INVISIBLE);

        //if loaded tanquin state
        if(tanquinState != null){
            view.findViewById(R.id.start).setVisibility(View.INVISIBLE);
            //load holepos
            Log.d(TAG,"holepos reload position : "+tanquinState.getHolePosition() ) ;
            holepos = tanquinState.getHolePosition();

            //load buttons
            for(int i=0 ; i<buttons.size() ; i++) {
                buttons.get(i).setTag(i+"");
                buttons.get(i).setText(tanquinState.getButtonsValue().get(i)+"" );
                buttons.get(i).setVisibility(View.VISIBLE);
                if (tanquinState.getButtonsValue().get(i)==9) {
                    buttons.get(i).setVisibility(View.INVISIBLE);
                    holepos = i;
                }
                buttons.get(i).setEnabled(true);
            }

            //set listeners
            for(Button button : buttons){
                button.setOnClickListener( clic -> {
                    char direction = '\0';
                    switch (holepos - Integer.parseInt(button.getTag()+"")) {
                        case 1: direction = '→'; break;
                        case -1: direction = '←';break;
                        case 3: direction = '↓';break;
                        case -3: direction = '↑';break;
                    }
                    if (direction!='\0') moveButton(button,direction, view.findViewById(R.id.start));
                });
            }
        }

        //when clicked --> shuttle le board, set startButton not visible, enable other buttons
        view.findViewById(R.id.start).setOnClickListener( start -> {
            for(Button button : buttons){
                button.setEnabled(true);
                button.setOnClickListener( clic -> {
                    char direction = '\0';
                    switch (holepos - Integer.parseInt(button.getTag()+"")) {
                        case 1: direction = '→'; break;
                        case -1: direction = '←';break;
                        case 3: direction = '↓';break;
                        case -3: direction = '↑';break;
                    }
                    if (direction!='\0') moveButton(button,direction, view.findViewById(R.id.start));
                });
            }
            start.setVisibility(View.INVISIBLE);
            shuttleButtons(10);   //10 switches
        });
        return view;
    }

    // browse rootView and sort all buttons in "buttons" list
    private List<Button> findButtonsWithId(View view) {
        List<Button> buttons = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof Button) {
                    String idString = getResources().getResourceEntryName(child.getId());
                    if (idString.matches("button[1-9]?")) {
                        buttons.add((Button) child);
                    }
                }
            }
        }
        return buttons;
    }

    /**
     * shuffle the buttons' board by making severals random moves
     * @param levelSuffle is the number of random move to make
     */
    private void shuttleButtons(int levelSuffle) {
        Random random = new Random();
        int indexButtonA;
        int indexButtonB=0;
        for(int i=0 ; i<levelSuffle*2 ; i++){
            indexButtonA = random.nextInt(8);
            if (indexButtonA==0 || indexButtonA==3 || indexButtonA==6) indexButtonB = indexButtonA + 1;
            if (indexButtonA==1 || indexButtonA==2 || indexButtonA==4)  indexButtonB = indexButtonA + 3;
            if (indexButtonA==5 || indexButtonA==7 )  indexButtonB = indexButtonA - 1;

            //switch button1 <-> button2
            CharSequence oldTextButtonA = buttons.get(indexButtonA).getText();
            buttons.get(indexButtonA).setText( buttons.get(indexButtonB).getText() );
            buttons.get(indexButtonB).setText( oldTextButtonA );
        }
    }

    private int findIndexToHoleButton(int fromIndex, char direction){
        int toIndex;
        switch (direction) {
            case '→': toIndex = fromIndex+1;  break;
            case '←': toIndex = fromIndex-1; break;
            case '↓': toIndex = fromIndex+3; break;
            case '↑': toIndex = fromIndex-3; break;
            default:toIndex=-1;  //NEVER ARRIVED --> return null
        }
        return toIndex;
    }

    /**
     * move a button by playing animation and then the hole is changed
     * @param button is the button to move
     * @param direction is the direction to move
     */
    private void moveButton(Button button, char direction, Button start) {
        int indexToHoleButton = findIndexToHoleButton(buttons.indexOf(button), direction);
        Animation animButton = null;
        switch (direction) {
            case '→': animButton = AnimationUtils.loadAnimation( getContext(), R.anim.right); holepos--; break;
            case '←': animButton = AnimationUtils.loadAnimation( getContext(), R.anim.left); holepos++; break;
            case '↓': animButton = AnimationUtils.loadAnimation( getContext(), R.anim.down); holepos -= 3; break;
            case '↑': animButton = AnimationUtils.loadAnimation( getContext(), R.anim.up); holepos += 3; break;
        }
        assert (animButton != null);
        animButton.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                button.setTag(holepos +"");
                button.setVisibility( View.INVISIBLE );
                CharSequence newTextValueForHoleButton = button.getText();
                button.setText(HIDDEN_BUTTON + "");
                buttons.get(indexToHoleButton).setVisibility( View.VISIBLE );
                buttons.get(indexToHoleButton).setText( newTextValueForHoleButton );
                if (isWon()) {
                    Log.d(TAG, "FIN");
                    gameOver(start);
                }
            }
        });
        button.startAnimation(animButton);
        Log.d(TAG,"after move: holePos  = " + holepos);
    }

    private void gameOver(Button start) {
        buttons.forEach( b -> b.setEnabled(false));
        start.setVisibility(View.VISIBLE);
    }

    private boolean isWon() {
        return buttons.stream().allMatch( button -> button.getText().toString().equals(button.getTag().toString()));
    }

    /**
     * send state to notifiable
     * - buttons
     * - start button visibility
     * - holepos
     */
    @Override
    public void onStop() {
        super.onStop();
        if (!isWon()){
            TaquinState state = new TaquinState(buttons ,holepos );
            notifiable.onDataChange(NUM_FRAGMENT,state);
        }
    }
}