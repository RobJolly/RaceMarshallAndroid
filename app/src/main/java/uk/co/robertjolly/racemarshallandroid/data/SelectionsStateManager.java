package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

//TODO Java doc this
public class SelectionsStateManager extends Observable implements Parcelable {

    Checkpoints checkpoints;
    ArrayList<Racer> selected;
    int lastCheckpointSize;

    //TODO Java doc this
    public SelectionsStateManager(final Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
        selected = new ArrayList<>();

        //TODO Think of a better way to do this without lastCheckpointSize, as this is bad practice and could cause unintended bugs
        lastCheckpointSize = checkpoints.getCheckpointNumberList().size();
        //TODO Make this observer its own method (called in more than one constructor)
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

    //TODO Java doc this
    public ArrayList<Racer> getShowableList(ArrayList<RacerDisplayFilter> filters) {
        ArrayList<Racer> shouldShow = new ArrayList<>();
        if ((getCheckpoints().hasCheckpoints()) && (getCheckpoints().hasCheckpoint(getCheckpointSelected()))) {
            for (Racer racer : getCheckpoints().getCheckpoint(getCheckpointSelected()).getRacers()) {
                boolean shouldSelect = false;
                // This means that if anything should be shown by a SINGLE selected filter, it's shown.
                // Regardless of any other filters not selecting it.
                for (RacerDisplayFilter filter : filters) {
                    if (shouldShow(filter, racer)) {
                        shouldSelect = true;
                        break;
                    }
                }
                if (shouldSelect) {
                    shouldShow.add(racer);
                }
            }
        }

        return shouldShow;
    }

    //TODO Java doc this
    public int getCheckpointSelected() {
        return getCheckpoints().getCurrentCheckpointNumber();
    }

    //TODO Java doc this
    Boolean shouldShow(RacerDisplayFilter filter, Racer racer) {
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
                //TODO Error here
                return null;
        }
    }

    //TODO Java doc this
    private Boolean toPass(RaceTimes times) {
        return (times.getDroppedOutTime() == null & times.getNotStartedTime() == null & times.getInTime() == null & times.getOutTime() == null);
    }

    //TODO Java doc this
    private Boolean checkedIn(RaceTimes times) {
        return (times.getDroppedOutTime() == null & times.getNotStartedTime() == null & times.getOutTime() == null & times.getInTime() != null);
    }

    //TODO Java doc this
    private Boolean checkedOut(RaceTimes times) {
        return (times.getDroppedOutTime() == null & times.getNotStartedTime() == null & times.getOutTime() != null);
    }

    //TODO Java doc this
    private Boolean droppedOut(RaceTimes times) {
        return (times.getDroppedOutTime() != null);
    }

    //TODO Java doc this
    private boolean didNotStart(RaceTimes times) {
        return (times.getNotStartedTime() != null);
    }

    //TODO Java doc this
    public Checkpoint getSelectedCheckpoint() {
        return getCheckpoints().getCheckpoint(getCheckpointSelected());
    }

    //TODO Java doc this
    public Checkpoints getCheckpoints() {
        return checkpoints;
    }

    //TODO Java doc this
    public void clearSelected() {
        getSelected().clear();
        setChanged();
        //notifyObservers();
    }

    //TODO Java doc this
    public void addSelected(Racer racer) {
        getSelected().add(racer);
        setChanged();
        //notifyObservers();
    }

    //TODO Java doc this
    //TODO Combine these into one function, they're functionally identical.
    public void setSelectedPassed(Date passed) {
       for (Racer racer : getSelected()) {
           getCheckpoints().setTime(racer, TimeTypes.OUT, passed);
           //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.OUT, passed);
       }
       clearSelected();
       getCheckpoints().notifyObservers();
    }

    //TODO Java doc this
    //TODO: Move to Checkpoints
    public void setSelectedIn(Date passed) {
        for (Racer racer : selected) {
            getCheckpoints().setTime(racer, TimeTypes.IN, passed);
            //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.IN, passed);
        }
        //checkpoints.notifyObservers();
        clearSelected();
        getCheckpoints().notifyObservers();
    }

    //TODO Java doc this
    //TODO: Move to checkpoints
    public void setSelectedNotStarted(Date passed) {
        for (Racer racer : selected) {
            getCheckpoints().setTime(racer, TimeTypes.DIDNOTSTART, passed);
            //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.DIDNOTSTART, passed);
        }
        //checkpoints.notifyObservers();
        clearSelected();
        checkpoints.notifyObservers();
    }

    //TODO Java doc this
    //TODO: Move to checkpoints
    public void setSelectedDroppedOut(Date passed) {
        for (Racer racer : selected) {
            getCheckpoints().setTime(racer, TimeTypes.DROPPEDOUT, passed);
            //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.DROPPEDOUT, passed);
        }
        //checkpoints.notifyObservers();
        clearSelected();
        checkpoints.notifyObservers();
    }

    //TODO Java doc this
    public void removeSelected(Racer racer) {
        selected.remove(racer);
        setChanged();
        //notifyObservers();
    }

    //TODO Java doc this
    public int getSelectedCount() {
        return selected.size();
    }

    //TODO Java doc this
    public ArrayList<Racer> getSelected() {
        return selected;
    }

    //TODO Java doc this
    public boolean isSelected(Racer racer) {
        return selected.contains(racer);
    }

    //TODO Java doc this
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

    //TODO Java doc this
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


    protected SelectionsStateManager(Parcel in) {
        checkpoints = in.readParcelable(Checkpoints.class.getClassLoader());
        int selectedSize = in.readInt();
        selected = new ArrayList<>();
        for (int i = 0; i < selectedSize; i++) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(checkpoints, i);
        parcel.writeInt(selected.size());
        for (Racer racer : selected) {
            parcel.writeParcelable(racer, i);
        }
        parcel.writeInt(lastCheckpointSize);
    }

    public void setCheckpoints(Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
    }
}
