package uk.co.robertjolly.racemarshallandroid.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class SelectionsStateManager extends Observable {
    Checkpoints checkpoints;
    ArrayList<Racer> selected;

    public SelectionsStateManager(Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
        selected = new ArrayList<>();

        checkpoints.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                setChanged();
                notifyObservers();
            }
        });
    }

    public ArrayList<Racer> getShowableList(ArrayList<RacerDisplayFilter> filters) {
        ArrayList<Racer> shouldShow = new ArrayList<>();
        for (Racer racer : checkpoints.getCheckpoint(getCheckpointSelected()).getRacers()) {
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
        return shouldShow;
    }

    public int getCheckpointSelected() {
        return checkpoints.getCurrentCheckpointNumber();
    }

    /*public void changeCheckpoint(int racerNumber) {
        if ((getCheckpointSelected() != racerNumber) && ( getAllCheckpoints().getCheckpointNumberList().contains(racerNumber))) {
            checkpointSelected = racerNumber;
            checkpoints.setCurrentCheckpointNumber(racerNumber);
            checkpoints.notifyObservers();
            clearSelected();
        }
    }*/

    Boolean shouldShow(RacerDisplayFilter filter, Racer racer) {
        switch (filter) {
            case TOPASS:
                return toPass(checkpoints.getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            case CHECKEDIN:
                return checkedIn(checkpoints.getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            case CHECKEDOUT:
                return checkedOut(checkpoints.getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            case DROPPEDOUT:
                return droppedOut(checkpoints.getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            case DIDNOTSTART:
                return didNotStart(checkpoints.getCheckpoint(getCheckpointSelected()).getReportedRaceTime(racer).getRaceTimes());
            default:
                //TODO Error here
                return null;
        }
    }

    private Boolean toPass(RaceTimes times) {
        return (times.droppedOutTime == null & times.notStartedTime == null & times.inTime == null & times.outTime == null);
    }

    private Boolean checkedIn(RaceTimes times) {
        return (times.droppedOutTime == null & times.notStartedTime == null & times.outTime == null & times.inTime != null);
    }

    private Boolean checkedOut(RaceTimes times) {
        return (times.droppedOutTime == null & times.notStartedTime == null & times.outTime != null);
    }

    private Boolean droppedOut(RaceTimes times) {
        return (times.droppedOutTime != null);
    }

    private boolean didNotStart(RaceTimes times) {
        return (times.notStartedTime != null);
    }

    public Checkpoint getSelectedCheckpoint() {
        return getAllCheckpoints().getCheckpoint(getCheckpointSelected());
    }

    public Checkpoints getAllCheckpoints() {
        return checkpoints;
    }

    public void clearSelected() {
        selected.clear();
        setChanged();
        //notifyObservers();
    }

    public void addSelected(Racer racer) {
        selected.add(racer);
        setChanged();
        //notifyObservers();
    }

    //TODO Combine these into one function, they're functionally identical.
    public void setSelectedPassed(Date passed) {
       for (Racer racer : selected) {
           getAllCheckpoints().setTime(racer, TimeTypes.OUT, passed);
           //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.OUT, passed);
       }
       clearSelected();
       getAllCheckpoints().notifyObservers();
    }

    //TODO: Move to Checkpoints
    public void setSelectedIn(Date passed) {
        for (Racer racer : selected) {
            getAllCheckpoints().setTime(racer, TimeTypes.IN, passed);
            //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.IN, passed);
        }
        //checkpoints.notifyObservers();
        clearSelected();
        getAllCheckpoints().notifyObservers();
    }

    //TODO: Move to checkpoints
    public void setSelectedNotStarted(Date passed) {
        for (Racer racer : selected) {
            getAllCheckpoints().setTime(racer, TimeTypes.DIDNOTSTART, passed);
            //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.DIDNOTSTART, passed);
        }
        //checkpoints.notifyObservers();
        clearSelected();
        checkpoints.notifyObservers();
    }

    //TODO: Move to checkpoints
    public void setSelectedDroppedOut(Date passed) {
        for (Racer racer : selected) {
            getAllCheckpoints().setTime(racer, TimeTypes.DROPPEDOUT, passed);
            //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.DROPPEDOUT, passed);
        }
        //checkpoints.notifyObservers();
        clearSelected();
        checkpoints.notifyObservers();
    }

    public void removeSelected(Racer racer) {
        selected.remove(racer);
        setChanged();
        //notifyObservers();
    }

    public int getSelectedCount() {
        return selected.size();
    }

    public ArrayList<Racer> getSelected() {
        return selected;
    }

    public boolean isSelected(Racer racer) {
        return selected.contains(racer);
    }
}
