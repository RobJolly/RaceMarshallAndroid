package uk.co.robertjolly.racemarshallandroid.data;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

/**
 * This class manages the logic behind which checkpoints to display, or not to display, in the list
 * of racer times
 */
public class CheckOffStateManager extends Observable {
    private Checkpoints checkpoints;
    private TimesFilterManager timesFilterManager;
    private Observer checkpointsObserver;
    private Observer timesFilterManagerObserver;

    /**
     * Constructor - The list of possible checkpoints, and the list of filters, to be applied to the
     * list of checkpoints.
     * @param checkpoints The complete checkpoint data for the application
     * @param filters The filters, to decide to show or not show records in checkpoints.
     */
    public CheckOffStateManager(Checkpoints checkpoints, TimesFilterManager filters) {
        this.checkpoints = checkpoints;
        this.timesFilterManager = filters;

        checkpointsObserver = new Observer() { //calls for an update if the checkpoints change
            @Override
            public void update(Observable observable, Object o) {
                setChanged();
                notifyObservers();
            }
        };
        timesFilterManagerObserver = new Observer() { //calls for an update if the time filters change
            @Override
            public void update(Observable observable, Object o) {
                setChanged();
                notifyObservers();
            }
        };

        checkpoints.addObserver(checkpointsObserver);
        timesFilterManager.addObserver(timesFilterManagerObserver);
    }

    /**
     *
     * @return gets a list of racers, in order of most recent checkpoint interaction, that have atleast
     * one time/interaction to display.
     */
    public ArrayList<Racer> getListToDisplay() {
        return calculateListToDisplay();
    }

    /**
     *
     * @return A list of racers, in order of most recent checkpoint interaction, that have atleast
     *      * one time/interaction to display.
     */
    private ArrayList<Racer> calculateListToDisplay() {
        TreeMap<Racer, ReportedRaceTimes> racersWithTimes;
        racersWithTimes = new TreeMap<>();
        //gets checkpoints with either IN, OUT, DroppedOut, DidNotStart times, or any combination of the above.
        if ((checkpoints.hasCheckpoints()) && (checkpoints.hasCheckpoint(checkpoints.getCurrentCheckpointNumber()))) {
            racersWithTimes = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getRacersWithTimes();
        }


        //This is the filtering logic.
        ArrayList<Racer> toReport = new ArrayList<>(racersWithTimes.keySet());
        ArrayList<Racer> toRemove = new ArrayList<>();
        for (Racer racer : toReport) {
            if (!timesFilterManager.shouldShow(checkpoints.getCurrentSelectedCheckpoint(), racer)) {
                toRemove.add(racer);
            }
        }
        toReport.removeAll(toRemove);

        //This sorts the list, based on most recent recorded change.
        Collections.sort(toReport, (racer, t1) -> {
            Date racerDate = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(racer).getRaceTimes().getLastSetTime();
            Date racer2Date = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(t1).getRaceTimes().getLastSetTime();


            if (racerDate.after(racer2Date)) {
                return -1;
            } else if (racerDate.before(racer2Date)){
                return 1;
            } else {
                return Integer.compare(racer.getRacerNumber(), t1.getRacerNumber());
            }
        });

        return toReport;
    }

    /**
     * This function is designed to remove the observers created by this class. This may be used to
     * ensure that no observers are left over after an object of this class is no longer needed.
     */
    public void removeObservers() {
        checkpoints.deleteObserver(checkpointsObserver);
        timesFilterManager.deleteObserver(timesFilterManagerObserver);
    }
}
