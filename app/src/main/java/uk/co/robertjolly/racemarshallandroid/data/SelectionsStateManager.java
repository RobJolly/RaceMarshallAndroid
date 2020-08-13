package uk.co.robertjolly.racemarshallandroid.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

//TODO Java doc this
public class SelectionsStateManager extends Observable {
    Checkpoints checkpoints;
    ArrayList<Racer> selected;
    int lastCheckpointSize;

    //TODO Java doc this
    public SelectionsStateManager(final Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
        selected = new ArrayList<>();

        //TODO Think of a better way to do this without lastCheckpointSize, as this is bad practice and could cause unintended bugs
        lastCheckpointSize = checkpoints.getCheckpointNumberList().size();
        checkpoints.addObserver(new Observer() {
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

    //TODO Java doc this
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

    //TODO Java doc this
    public int getCheckpointSelected() {
        return checkpoints.getCurrentCheckpointNumber();
    }

    //TODO Java doc this
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

    //TODO Java doc this
    private Boolean toPass(RaceTimes times) {
        return (times.droppedOutTime == null & times.notStartedTime == null & times.inTime == null & times.outTime == null);
    }

    //TODO Java doc this
    private Boolean checkedIn(RaceTimes times) {
        return (times.droppedOutTime == null & times.notStartedTime == null & times.outTime == null & times.inTime != null);
    }

    //TODO Java doc this
    private Boolean checkedOut(RaceTimes times) {
        return (times.droppedOutTime == null & times.notStartedTime == null & times.outTime != null);
    }

    //TODO Java doc this
    private Boolean droppedOut(RaceTimes times) {
        return (times.droppedOutTime != null);
    }

    //TODO Java doc this
    private boolean didNotStart(RaceTimes times) {
        return (times.notStartedTime != null);
    }

    //TODO Java doc this
    public Checkpoint getSelectedCheckpoint() {
        return getAllCheckpoints().getCheckpoint(getCheckpointSelected());
    }

    //TODO Java doc this
    public Checkpoints getAllCheckpoints() {
        return checkpoints;
    }

    //TODO Java doc this
    public void clearSelected() {
        selected.clear();
        setChanged();
        //notifyObservers();
    }

    //TODO Java doc this
    public void addSelected(Racer racer) {
        selected.add(racer);
        setChanged();
        //notifyObservers();
    }

    //TODO Java doc this
    //TODO Combine these into one function, they're functionally identical.
    public void setSelectedPassed(Date passed) {
       for (Racer racer : selected) {
           getAllCheckpoints().setTime(racer, TimeTypes.OUT, passed);
           //getAllCheckpoints().getCheckpoint(getCheckpointSelected()).setTime(racer, TimeTypes.OUT, passed);
       }
       clearSelected();
       getAllCheckpoints().notifyObservers();
    }

    //TODO Java doc this
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

    //TODO Java doc this
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

    //TODO Java doc this
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


}
