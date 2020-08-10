package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.ActiveRacerDisplayFragment;


public class RaceGridViewAdapter extends BaseAdapter implements SelectionManagerGrabber {

    private final Context mContext;
    private SelectionsStateManager selectionsStateManager;
    private final ActiveRacerDisplayFragment mParent;
    private ArrayList<Racer> toShow;

    public RaceGridViewAdapter(Context mContext, ActiveRacerDisplayFragment parentFragment) {
        this.mContext = mContext;
        this.mParent = parentFragment;
        selectionsStateManager = grabSelectionManager();
        toShow = selectionsStateManager.getShowableList(grabDisplayFilterManager().getFilterList()); //Setting filters here - change later

        selectionsStateManager.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                toShow = selectionsStateManager.getShowableList(grabDisplayFilterManager().getFilterList());
                notifyDataSetChanged();
            }
        });

        /*selectionsStateManager.getSelectedCheckpoint().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                toShow = selectionsStateManager.getShowableList(grabDisplayFilterManager().getFilterList());
            }
        });*/

        grabDisplayFilterManager().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                toShow = selectionsStateManager.getShowableList(grabDisplayFilterManager().getFilterList());
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getCount() {
        return toShow.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        selectionsStateManager = grabSelectionManager();

        final Button thisButton = new Button(mContext);
        //note, button size setting (next 3 lines) from: https://stackoverflow.com/questions/11010111/how-to-set-the-width-of-a-button-in-dp-during-runtime-in-android
        final float scale = mContext.getResources().getDisplayMetrics().density;
        thisButton.setWidth((int)(100 * scale));
        thisButton.setHeight((int)(100 * scale));

        if (selectionsStateManager.isSelected(toShow.get(i))) { //if button is selected - blue colour
            thisButton.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        }

        //set the button text to the racer number
        thisButton.setText(String.valueOf(toShow.get(i).getRacerNumber()));

        thisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectionsStateManager.isSelected(toShow.get(i))) {
                    selectionsStateManager.removeSelected(toShow.get(i));
                    thisButton.getBackground().clearColorFilter();
                } else {
                    selectionsStateManager.addSelected(toShow.get(i));
                    thisButton.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                }
            }
        });
        ;


        return thisButton;
    }

    @Override
    public SelectionsStateManager grabSelectionManager() {
        return mParent.grabSelectionManager();
    }

    public DisplayFilterManager grabDisplayFilterManager() {
        return mParent.grabDisplayFilterManager();
    }
}
