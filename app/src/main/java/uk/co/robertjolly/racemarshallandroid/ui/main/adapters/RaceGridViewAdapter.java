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

import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.ActiveRacerDisplayFragment;


//TODO Java doc this
public class RaceGridViewAdapter extends BaseAdapter {

    private final Context mContext;
    private SelectionsStateManager selectionsStateManager;
    private DisplayFilterManager displayFilterManager;
    private ArrayList<Racer> toShow;

    //TODO Java doc this
    public RaceGridViewAdapter(Context mContext, SelectionsStateManager selectionsStateManager, DisplayFilterManager displayFilterManager) {
        this.mContext = mContext;

        setSelectionsStateManager(selectionsStateManager);
        setDisplayFilterManager(displayFilterManager);
        toShow = selectionsStateManager.getShowableList(getDisplayFilterManager().getFilterList()); //Setting filters here - change later

        selectionsStateManager.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                toShow = getSelectionsStateManager().getShowableList(getDisplayFilterManager().getFilterList());
                notifyDataSetChanged();
            }
        });

        getDisplayFilterManager().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                toShow = getSelectionsStateManager().getShowableList(getDisplayFilterManager().getFilterList());
                notifyDataSetChanged();
            }
        });

    }

    //TODO Java doc this
    @Override
    public int getCount() {
        //+6 exists to create a 'buffer' - prevents floating action buttons from making the bottom two rows unclickable/harder to click.
        return toShow.size() + 6;
    }

    //TODO Java doc this
    @Override
    public Object getItem(int i) {
        return null;
    }

    //TODO Java doc this
    @Override
    public long getItemId(int i) {
        return 0;
    }

    //TODO Java doc this
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final Button thisButton = new Button(mContext);
        //note, button size setting (next 3 lines) from: https://stackoverflow.com/questions/11010111/how-to-set-the-width-of-a-button-in-dp-during-runtime-in-android
        final float scale = mContext.getResources().getDisplayMetrics().density;
        thisButton.setWidth((int)(100 * scale));
        thisButton.setHeight((int)(100 * scale));

        //Prevents crash if button count too large and creates invisible 'buffer' buttons
        if (i >= toShow.size()) {
            thisButton.setVisibility(View.INVISIBLE);
        } else {
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
                    selectionsStateManager.notifyObservers();
                }
            });
        }


        return thisButton;
    }

    //TODO Java doc this
    public SelectionsStateManager getSelectionsStateManager() {
        return selectionsStateManager;
    }

    //TODO Java doc this
    public void setSelectionsStateManager(SelectionsStateManager selectionsStateManager) {
        this.selectionsStateManager = selectionsStateManager;
    }

    //TODO Java doc this
    public DisplayFilterManager getDisplayFilterManager() {
        return displayFilterManager;
    }

    //TODO Java doc this
    public void setDisplayFilterManager(DisplayFilterManager displayFilterManager) {
        this.displayFilterManager = displayFilterManager;
    }
}
