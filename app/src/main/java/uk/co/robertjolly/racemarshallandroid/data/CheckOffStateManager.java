package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

public class CheckOffStateManager extends Observable {
    private Checkpoint checkpoint;
    private ArrayList<Racer> toDisplay;
    private DisplayFilterManager filters;

    public CheckOffStateManager(Checkpoint checkpoint, DisplayFilterManager filters) {
        this.checkpoint = checkpoint;
        this.filters = filters;

/*        filters.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                //do nothing for now
            }
        });*/

        this.toDisplay = calculateListToDisplay();

        checkpoint.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                toDisplay = calculateListToDisplay();
                setChanged();
                notifyObservers();
            }
        });
    }

    public ArrayList<Racer> getListToDisplay() {
        return toDisplay;
    }


    private ArrayList<Racer> calculateListToDisplay() {
        TreeMap<Racer, ReportedRaceTimes> racersWithTimes;
        racersWithTimes = checkpoint.getRacersWithTimes();

        ArrayList<Racer> toReport = new ArrayList<>();
        for (Racer racer : racersWithTimes.keySet()) {
            toReport.add(racer); //here's where you'd do the filtering
        }

        Collections.sort(toReport, new Comparator<Racer>() {
            @Override
            public int compare(Racer racer, Racer t1) {

                Date racerDate = checkpoint.getReportedRaceTime(racer).getRaceTimes().getLastSetTime();
                Date racer2Date = checkpoint.getReportedRaceTime(t1).getRaceTimes().getLastSetTime();

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
