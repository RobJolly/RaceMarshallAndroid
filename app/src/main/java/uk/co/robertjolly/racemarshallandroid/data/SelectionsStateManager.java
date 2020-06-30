package uk.co.robertjolly.racemarshallandroid.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class SelectionsStateManager extends Observable {
    Checkpoint checkpointSelection;
    ArrayList<Racer> selected;

    public SelectionsStateManager(Checkpoint checkpointSelection) {
        this.checkpointSelection = checkpointSelection;
        selected = new ArrayList<>();
    }

    public ArrayList<Racer> getShowableList(ArrayList<RacerDisplayFilter> filters) {
        ArrayList<Racer> shouldShow = new ArrayList<>();
        for (Racer racer : checkpointSelection.getRacers()) {
            boolean shouldSelect = false;
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

    Boolean shouldShow(RacerDisplayFilter filter, Racer racer) {
        switch (filter) {
            case TOPASS:
                return (checkpointSelection.getReportedRaceTime(racer).getRaceTimes().hasYetToArrive());
            case CHECKEDIN:
                return (checkpointSelection.getReportedRaceTime(racer).getRaceTimes().isInCheckpoint());
            case CHECKEDOUT:
                return (checkpointSelection.getReportedRaceTime(racer).getRaceTimes().hasPassed());
            case DROPPEDOUT:
                return (checkpointSelection.getReportedRaceTime(racer).getRaceTimes().hasDroppedOut());
            case DIDNOTSTART:
                return (checkpointSelection.getReportedRaceTime(racer).getRaceTimes().didNotStart());
            default:
                //TODO Error here
                return null;
        }
    }

    public Checkpoint getCheckpointSelection() {
        return checkpointSelection;
    }

    public void clearSelected() {
        selected.clear();
        setChanged();
        notifyObservers();
    }

    public void addSelected(Racer racer) {
        selected.add(racer);
        setChanged();
        notifyObservers();
     //   notifyObservers();
    }

    //TODO Combine these into one function, they're functionally identical.
    public void setSelectedPassed(Date passed) {
       for (Racer racer : selected) {
           checkpointSelection.setTime(racer, TimeTypes.OUT, passed);
       }
       checkpointSelection.notifyObservers();
        clearSelected();
    }

    public void setSelectedIn(Date passed) {
        for (Racer racer : selected) {
            checkpointSelection.setTime(racer, TimeTypes.IN, passed);
        }
        checkpointSelection.notifyObservers();
        clearSelected();
    }

    public void setSelectedNotStarted(Date passed) {
        for (Racer racer : selected) {
            checkpointSelection.setTime(racer, TimeTypes.DIDNOTSTART, passed);
        }
        checkpointSelection.notifyObservers();
        clearSelected();
    }

    public void setSelectedDroppedOut(Date passed) {
        for (Racer racer : selected) {
            checkpointSelection.setTime(racer, TimeTypes.DROPPEDOUT, passed);
        }
        checkpointSelection.notifyObservers();
        clearSelected();
    }

    public void removeSelected(Racer racer) {
        selected.remove(racer);
        setChanged();
        notifyObservers();
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
