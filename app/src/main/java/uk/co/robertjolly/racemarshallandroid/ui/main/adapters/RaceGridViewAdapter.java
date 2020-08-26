package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.ArrayList;
import java.util.Objects;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;


/**
 * This is the class responsible for handling the displaying of the grid.
 */
public class RaceGridViewAdapter extends BaseAdapter {

    private final Context mContext;
    private SelectionsStateManager selectionsStateManager;
    private DisplayFilterManager displayFilterManager;
    private ArrayList<Racer> toShow;

    /**
     * Constructor for the race view adapter
     * @param mContext context
     * @param selectionsStateManager The selections state manager, which shall be used to determine which racers to display in the grid.
     * @param displayFilterManager Filters, which shall be used to help determine which racers in the grid.
     */
    public RaceGridViewAdapter(Context mContext, SelectionsStateManager selectionsStateManager, DisplayFilterManager displayFilterManager) {
        this.mContext = mContext;

        setSelectionsStateManager(selectionsStateManager);
        setDisplayFilterManager(displayFilterManager);
        toShow = selectionsStateManager.getShowableList(getDisplayFilterManager().getFilterList());


        //observer to change the grid, if selections has changed
        selectionsStateManager.addObserver((observable, o) -> {
            toShow = getSelectionsStateManager().getShowableList(getDisplayFilterManager().getFilterList());
            notifyDataSetChanged();
        });

        getDisplayFilterManager().addObserver((observable, o) -> { //observer to change the grid, if filters have changed
            toShow = getSelectionsStateManager().getShowableList(getDisplayFilterManager().getFilterList());
            notifyDataSetChanged();
        });

    }

    /**
     * Gets the number of items to show in the grid.
     * @return Number of items to show in the grid (All Racers in Selected Checkpoint + 6)
     */
    @Override
    public int getCount() {
        //+6 exists to create a 'buffer' - prevents floating action buttons from making the bottom two rows unclickable/harder to click.
        return getToShow().size() + 6;
    }

    /**
     * This is a function required to extend base adapter
     * @param i index
     * @return Always null
     */
    @Override
    public Object getItem(int i) {
        return null;
    }

    /**
     * This is a function required to extend base adapter
     * @param i index
     * @return Always 0
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * This gets the view at a given index. This handles the setting up of buttons.
     * @param i index
     * @param view view
     * @param viewGroup view group
     * @return View (Button) with the data, colour and size set for the grid.
     */
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final Button thisButton = new Button(mContext);
        //note, button size setting (next 3 lines) from: https://stackoverflow.com/questions/11010111/how-to-set-the-width-of-a-button-in-dp-during-runtime-in-android
        final float scale = mContext.getResources().getDisplayMetrics().density;
        thisButton.setWidth((int)(100 * scale));
        thisButton.setHeight((int)(100 * scale));

        //ArrayList<Racer> toShow = getToShow();
        //Prevents crash if button count too large and creates invisible 'buffer' buttons
        if (i >= getToShow().size()) {
            thisButton.setVisibility(View.INVISIBLE);
        } else {
            try {
                if (getSelectionsStateManager().isSelected(getToShow().get(i))) { //if button is selected - blue colour
                    thisButton.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                } else if (Objects.requireNonNull(getSelectionsStateManager().getSelectedCheckpoint()).getRacerData(getToShow().get(i)).getRaceTimes().getDroppedOutTime() != null) {
                    thisButton.getBackground().setColorFilter(Color.parseColor("#5E35B1"), PorterDuff.Mode.MULTIPLY);
                } else if (getSelectionsStateManager().getSelectedCheckpoint().getRacerData(getToShow().get(i)).getRaceTimes().getNotStartedTime() != null) {
                    thisButton.getBackground().setColorFilter(Color.parseColor("#5E35B1"), PorterDuff.Mode.MULTIPLY);
                } else if (getSelectionsStateManager().getSelectedCheckpoint().getRacerData(getToShow().get(i)).getRaceTimes().getOutTime() != null) {
                    thisButton.getBackground().setColorFilter(Color.parseColor("#D81B60"), PorterDuff.Mode.MULTIPLY);
                } else if (getSelectionsStateManager().getSelectedCheckpoint().getRacerData(getToShow().get(i)).getRaceTimes().getInTime() != null) {
                    thisButton.getBackground().setColorFilter(Color.parseColor("#FDD835"), PorterDuff.Mode.MULTIPLY);
                } else {
                    thisButton.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                }

                //set the button text to the racer number
                thisButton.setText(String.valueOf(toShow.get(i).getRacerNumber()));

                thisButton.setOnClickListener(view1 -> {
                    ArrayList<Racer> toShow = getToShow();
                    if (getSelectionsStateManager().isSelected(toShow.get(i))) {
                        getSelectionsStateManager().removeSelected(toShow.get(i));
                        thisButton.getBackground().clearColorFilter();
                    } else {
                        getSelectionsStateManager().addSelected(toShow.get(i));
                        thisButton.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    }
                    getSelectionsStateManager().notifyObservers();
                });
            } catch (Exception e) {
                Log.e("ERROR", "Button at index " + i + " Could not be created correctly. It has been made invisible.");
                thisButton.setVisibility(View.INVISIBLE);
            }

        }

        return thisButton;
    }

    /**
     * Getter for the SelectionsStateManager stored in RaceGridViewAdapter
     * @return The stored SelectionsStateManager
     */
    public SelectionsStateManager getSelectionsStateManager() {
        return selectionsStateManager;
    }

    /**
     * Setter for the SelectionsStateManager stored in RaceGridViewAdapter
     * @param selectionsStateManager The SelectionsStateManager to store in this object.
     */
    public void setSelectionsStateManager(SelectionsStateManager selectionsStateManager) {
        this.selectionsStateManager = selectionsStateManager;
    }

    /**
     * Getter for the DisplayFilterManager stored in RaceGridViewAdapter
     * @return The displayFilterManager stored
     */
    public DisplayFilterManager getDisplayFilterManager() {
        return displayFilterManager;
    }

    /**
     * Setter for the DisplayFilterManager stored in RaceGridViewAdapter
     * @param displayFilterManager The DisplayFilterManager to store in this object.
     */
    public void setDisplayFilterManager(DisplayFilterManager displayFilterManager) {
        this.displayFilterManager = displayFilterManager;
    }

    /**
     * Getter for the racers that are stored in this adapter, to be shown
     * @return An ArrayList of Racers that are to be shown by this adapter
     */
    public ArrayList<Racer> getToShow() {
        return toShow;
    }
}
