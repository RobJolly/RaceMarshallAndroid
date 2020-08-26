package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import javax.annotation.Nullable;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

/**
 * The selection state manager handles which racers are selected, and which are not. This class includes
 * functions to edit the data in the given Checkpoints class, in various ways.
 */
public class SelectionsStateManager extends Observable implements Parcelable { //Not Serializable as shouldn't be saved long-term

    private Checkpoints checkpoints;
    private ArrayList<Racer> selected;
    private int lastCheckpointSize;

    //TODO Java doc this
    public SelectionsStateManager(final Checkpoints checkpoints) {
        setCheckpoints(checkpoints);
        selected = new ArrayList<>();

        //TODO Think of a better way to do this without lastCheckpointSize, as this is bad practice and could cause unintended bugs
        lastCheckpointSize = checkpoints.getCheckpointNumberList().size();

        //TODO Make this observer its own method (called in more than one constructor)
        //observer to check whether or not the checkpoints data have changed in such a way to mandate notifying observers
        getCheckpoints().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if (lastCheckpointSize != checkpoints.getCheckpointNumberList().size() && lastCheckpointSize != 0 && checkpoints.getCheckpointNumberList().size() > 0) {
                    lastCheckpointSize = checkpoints.getCheckpointNumberList().size();
                } else {
                    clearSelected();
                    notifyObservers();
                }
            }
        });
    }

    /**
     * This function gets a list of racers, filtered out to include only those for which RacerDisplayFilters exist, to include them.
     * These filters are inclusive (e.g. If one filter shows the given checkpoint, and another doesn't, it will be included in this list).
     * @param filters The filters for which to show/not show racers
     * @return A list of racers stored in the selected checkpoint, showable, based on the filters provided
     */
    public ArrayList<Racer> getShowableList(ArrayList<RacerDisplayFilter> filters) {
        ArrayList<Racer> shouldShow = new ArrayList<>();
        if ((getCheckpoints().hasCheckpoints()) && (getCheckpoints().hasCheckpoint(getCheckpointSelected()))) { //check if the selected checkpoint exists
            for (Racer racer : getCheckpoints().getCheckpoint(getCheckpointSelected()).getRacers()) {
                boolean shouldSelect = false;
                // This means that if anything should be shown by a SINGLE selected filter, it's shown.
                // Regardless of any other filters not selecting it.
                for (RacerDisplayFilter filter : filters) { //exhaustively check a racer against all filters, until it is found to meet one, or we're out of filters
                    if (shouldShow(filter, racer)) {
                        shouldSelect = true;
                        break; //this break exists, as one we know a racer should be shown for any filter, we don't have to check the rest
                    }
                }
                if (shouldSelect) {
                    shouldShow.add(racer);
                }
            }
        } else {
            Log.e("Error", "No selected checkpoint from which to show racers. Selected checkpoint is: " + String.valueOf(getCheckpointSelected()));
        }

        return shouldShow;
    }

    /**
     * Gets the currently selected checkpoint. This is the checkpoint for which most functions are designed to change (the one currently active)
     * @return The currently selected checkpoint.
     */
    public int getCheckpointSelected() {
        return getCheckpoints().getCurrentCheckpointNumber();
    }

    /**
     * Boolean to determine whether or not a given filter, means that a given racer, should be shown.
     * @param filter The filter to check the racer against
     * @param racer The racer for which to filter
     * @return boolean, indicating whether or not the racer should be shown for the given filter.
     */
    private Boolean shouldShow(RacerDisplayFilter filter, Racer racer) {
        switch (filter) {
            case TOPASS:
                return toPass(getCheckpoints().getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            case CHECKEDIN:
                return checkedIn(getCheckpoints().getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            case CHECKEDOUT:
                return checkedOut(getCheckpoints().getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            case DROPPEDOUT:
                return droppedOut(getCheckpoints().getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            case DIDNOTSTART:
                return didNotStart(getCheckpoints().getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            default:
                Log.e("Error", "Checking if a racer should be shown, with a filter that isn't checked for. Filter is: " + filter.name());
                return false;
        }
    }

    //May be better placed in RaceTimes.
    /**
     * Getter for whether or not the givenRace times indicate the racer has yet to pass/be in the checkpoint
     * @param times the times for which to check.
     * @return Boolean indicating if the racer has yet to pass as indicated by: droppedOutTime, notStartedTime, inTime and outTime all not existing for this raceTimes.
     */
    private Boolean toPass(RaceTimes times) {
        return (times.getDroppedOutTime() == null & times.getNotStartedTime() == null & times.getInTime() == null & times.getOutTime() == null);
    }

    //May be better placed in RaceTimes
    /**
     * Getter for whether or not the givenRace times indicate the racer is currently in the checkpoint
     * @param times the times for which to check.
     * @return Boolean indicating if the racer is currently in the checkpoint as indicated by: droppedOutTime, notStartedTime, outTime all not existing for this raceTimes, and inTime existing.
     */
    private Boolean checkedIn(RaceTimes times) {
        return (times.getDroppedOutTime() == null & times.getNotStartedTime() == null & times.getOutTime() == null & times.getInTime() != null);
    }

    //May be better placed in RaceTimes
    /**
     * Getter for whether or not the givenRace times indicate the racer has passed the checkpoint, and has not dropped out or not started.
     * @param times the times for which to check.
     * @return Boolean indicating if the racer is checked out of the checkpoint and continuing the race as indicated by: droppedOutTime, notStartedTime not existing, and outTime existing.
     */
    private Boolean checkedOut(RaceTimes times) {
        return (times.getDroppedOutTime() == null & times.getNotStartedTime() == null & times.getOutTime() != null);
    }

    //May be better placed in RaceTimes
    /**
     * Getter for whether or not the given racer has dropped out, based on RaceTimes.
     * @param times the times for which to check.
     * @return Boolean indicating if the racer has dropped out of the race or not.
     */
    private Boolean droppedOut(RaceTimes times) {
        return (times.getDroppedOutTime() != null);
    }

    /**
     * Getter for whether not not the racer has dropped out, Based on RaceTimes
     * @param times the times for which to check
     * @return Whether or not the given racer has checked out.
     */
    private boolean didNotStart(RaceTimes times) {
        return (times.getNotStartedTime() != null);
    }

    /**
     * Getter for the currently selected checkpoint.
     * @return The currently selected checkpoint, null if does not exist
     */
    @Nullable
    public Checkpoint getSelectedCheckpoint() {
        return getCheckpoints().getCheckpoint(getCheckpointSelected());
    }

    /**
     * Gets the entire list of checkpoints stored in the selectionsStateManager
     * @return list of checkpoints the SelectionsStateManager is working on/effecting/storing.
     */
    public Checkpoints getCheckpoints() {
        return checkpoints;
    }

    /**
     * Clears all of the selected checkpoints.
     */
    public void clearSelected() {
        getSelected().clear();
        setChanged();
    }

    /**
     * Adds a Racer to the selected list, indicating it has been selected
     * @param racer The racer to select
     */
    public void addSelected(Racer racer) {
        getSelected().add(racer);
        setChanged();
    }

    /**
     * Sets the selected racers to have checked out at the given time
     * @param passed the date at which the selected racers passed the checkpoint
     */
    //TODO Combine these next 4 into one function, they're functionally identical.
    public void setSelectedPassed(Date passed) {
       for (Racer racer : getSelected()) {
           getCheckpoints().setTime(racer, TimeTypes.OUT, passed);
       }
       clearSelected();
       getCheckpoints().notifyObservers();
    }

    /**
     * Sets the selected racers to have checked in at the given time
     * @param passed the date at which the selected racers checked in to the checkpoint
     */
    public void setSelectedIn(Date passed) {
        for (Racer racer : selected) {
            getCheckpoints().setTime(racer, TimeTypes.IN, passed);
        }
        clearSelected();
        getCheckpoints().notifyObservers();
    }

    /**
     * Sets the selected racers to have not started at the given time
     * @param passed the date at which the selected racers did not start
     */
    public void setSelectedNotStarted(Date passed) {
        for (Racer racer : selected) {
            getCheckpoints().setTime(racer, TimeTypes.DIDNOTSTART, passed);
        }
        clearSelected();
        checkpoints.notifyObservers();
    }

    /**
     * Sets the selected racers to have dropped out
     * @param passed the date at which the selected racers dropped out
     */
    public void setSelectedDroppedOut(Date passed) {
        for (Racer racer : selected) {
            getCheckpoints().setTime(racer, TimeTypes.DROPPEDOUT, passed);
        }
        clearSelected();
        checkpoints.notifyObservers();
    }

    /**
     * Removes the given racer from the list of selected racers
     * @param racer Racer to remove
     */
    public void removeSelected(Racer racer) {
        if (selected.contains(racer)) {
            selected.remove(racer);
            setChanged();
        } else {
            Log.w("Warning", "A racer has been attempted to be removed from selected that does not exist. Racer Number: " + String.valueOf(racer.getRacerNumber()));
        }
    }

    /**
     * Getter for the number of selected racers
     * @return the number of selected racers
     */
    public int getSelectedCount() {
        return selected.size();
    }

    /**
     * Getter for the selected racers
     * @return An ArrayList of racers that have been selected
     */
    public ArrayList<Racer> getSelected() {
        return selected;
    }

    /**
     * Finds out whether not the given racer is selected or not.
     * @param racer Racer to search for
     * @return whether or not the given racer is selected
     */
    public boolean isSelected(Racer racer) {
        return selected.contains(racer);
    }

    /**
     * Finds out whether not not all of the selected racers can be set as checked in, based on their current set times.
     * @return whether not not all of the selected racers can be set as checked in, based on their current set times
     */
    public boolean areCompatableIn() {
        boolean compatable = true;

        for (Racer racer : selected) {
            ReportedRaceTimes racerTimes = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(racer);
            if (racerTimes.getRaceTimes().getInTime() != null || racerTimes.getRaceTimes().getDroppedOutTime() != null || racerTimes.getRaceTimes().getOutTime() != null || racerTimes.getRaceTimes().getNotStartedTime() != null) {
                compatable = false;
                break;
            }
        }

        return compatable;
    }

    /**
     * Finds out whether not not all of the selected racers can be set as checked out, based on their current set times.
     * @return whether not not all of the selected racers can be set as checked out, based on their current set times
     */
    public boolean areCompatableOut() {
        boolean compatable = true;

        for (Racer racer : selected) {
            ReportedRaceTimes racerTimes = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(racer);
            if (racerTimes.getRaceTimes().getDroppedOutTime() != null || racerTimes.getRaceTimes().getOutTime() != null || racerTimes.getRaceTimes().getNotStartedTime() != null) {
                compatable = false;
                break;
            }

        }
        return compatable;
    }

    /**
     * Finds out whether not not all of the selected racers can be set as dropped out or not started, based on their current set times.
     * @return whether not not all of the selected racers can be set as dropped out or not started, based on their current set times
     */
    public boolean areCompatableOther() {
        boolean compatable = true;

        for (Racer racer : selected) {
            ReportedRaceTimes racerTimes = getCheckpoints().getCheckpoint(getCheckpoints().getCurrentCheckpointNumber()).getReportedRaceTime(racer);
            if (racerTimes.getRaceTimes().getNotStartedTime() != null || racerTimes.getRaceTimes().getDroppedOutTime() != null) {
                compatable = false;
                break;
            }
        }

        return compatable;
    }

    /**
     * Constructor which takes a parcel and sets the selected checkpoint based on that parcel.
     * @param in Parcel from which to take data
     */
    protected SelectionsStateManager(Parcel in) {
        checkpoints = in.readParcelable(Checkpoints.class.getClassLoader());
        int selectedSize = in.readInt();
        selected = new ArrayList<>();
        for (int i = 0; i < selectedSize; i++) { //Doing it this way rather than readParcelableArray as was having errors
            Racer racer = in.readParcelable(Racer.class.getClassLoader());
            selected.add(racer);
        }
        selected = in.createTypedArrayList(Racer.CREATOR);
        lastCheckpointSize = in.readInt();

        //TODO Make this observer its own method (called in more than one constructor)
        getCheckpoints().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if (lastCheckpointSize != checkpoints.getCheckpointNumberList().size()) {
                    lastCheckpointSize = checkpoints.getCheckpointNumberList().size();
                } else {
                    clearSelected();
                    notifyObservers();
                }
            }
        });
    }

    /**
     * Function required to implement Parcelable
     */
    public static final Creator<SelectionsStateManager> CREATOR = new Creator<SelectionsStateManager>() {
        @Override
        public SelectionsStateManager createFromParcel(Parcel in) {
            return new SelectionsStateManager(in);
        }

        @Override
        public SelectionsStateManager[] newArray(int size) {
            return new SelectionsStateManager[size];
        }
    };

    /**
     * Function required to implement Parcelable
     * @return Always 0
     */
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the data stored within this object, to the given parcel
     * @param parcel The parcel in which to write
     * @param i Flags
     */
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(checkpoints, i);
        parcel.writeInt(selected.size());
        for (Racer racer : selected) { //Doing it this way rather than writeParcelableArray as was having errors
            parcel.writeParcelable(racer, i);
        }
        parcel.writeInt(lastCheckpointSize);
    }

    /**
     * Sets the checkpoints data stored within this object
     * @param checkpoints checkpoints for which the SelectionsStateManager will work
     */
    public void setCheckpoints(Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
    }
}
