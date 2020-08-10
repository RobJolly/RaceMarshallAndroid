package uk.co.robertjolly.racemarshallandroid.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private DisplayFilterManager filters;

    /**
     * Constructor - The list of possible checkpoints, and the list of filters, to be applied to the
     * list of checkpoints.
     * @param checkpoints The complete checkpoint data for the application
     * @param filters The filters, to decide to show or not show records in checkpoints.
     */
    public CheckOffStateManager(Checkpoints checkpoints, DisplayFilterManager filters) {
        this.checkpoints = checkpoints;
        this.filters = filters;

        checkpoints.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                setChanged();
                notifyObservers();
            }
        });
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
        //gets checkpoints with either IN, OUT, DroppedOut, DidNotStart times, or any combination of the above.
        racersWithTimes = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getRacersWithTimes();

        ArrayList<Racer> toReport = new ArrayList<>();
        for (Racer racer : racersWithTimes.keySet()) {
            toReport.add(racer); //here's where you'd do the filtering
        }

        //This sorts the list, based on most recent recorded change.
        Collections.sort(toReport, new Comparator<Racer>() {
            @Override
            public int compare(Racer racer, Racer t1) {
                Date racerDate = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(racer).getRaceTimes().getLastSetTime();
                Date racer2Date = checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()).getReportedRaceTime(t1).getRaceTimes().getLastSetTime();


                if (racerDate.after(racer2Date)) {
                    return -1;
                } else if (racerDate.before(racer2Date)){
                    return 1;
                } else {
                    return Integer.compare(racer.getRacerNumber(), t1.getRacerNumber());
                }
            }
        });

        return toReport;
    }
}
