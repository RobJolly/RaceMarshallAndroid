package uk.co.robertjolly.racemarshallandroid.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Observable;
import java.util.TreeMap;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;


public class Checkpoint extends Observable {
    int checkPointNumber;
    TreeMap<Racer, ReportedRaceTimes> racerData;

    public Checkpoint(int checkPointNumber, int racerNumber) {
        this.checkPointNumber = checkPointNumber;
        racerData = new TreeMap<>();
        for (int i = 0; i < racerNumber; i++) {
            racerData.put(new Racer(i+1), new ReportedRaceTimes());
        }
    }

    public boolean reportedItem(int racerNumber, TimeTypes type) {
        if (racerData.containsKey(new Racer(racerNumber))) {
            return Objects.requireNonNull(racerData.get(new Racer(racerNumber))).getReportedItems().getReportedItem(type);
        } else {
            //TODO: ERROR HERE
            return false;
        }
    }

    public int getCheckPointNumber() {
        return checkPointNumber;
    }

    public void setReportedItem(int racerNumber, TimeTypes type, boolean isReported) {
        if (racerData.containsKey(new Racer(racerNumber))) {
            notifyObservers();
            Objects.requireNonNull(racerData.get(new Racer(racerNumber))).getReportedItems().setReportedItem(type,isReported);
        } else {
            //TODO: ERROR HERE
        }
    }

    public ArrayList<Racer> getAllNotPassed() {
        ArrayList<Racer> unpassedRacers = new ArrayList<>();

        for (HashMap.Entry<Racer, ReportedRaceTimes> entry : racerData.entrySet()) {
            if (!entry.getValue().getRaceTimes().hasPassed()) {
                unpassedRacers.add(entry.getKey());
            }
        }

        return unpassedRacers;
    }

    public ArrayList<Racer> getAllPassed() {
        ArrayList<Racer> unpassedRacers = new ArrayList<>();

        for (HashMap.Entry<Racer, ReportedRaceTimes> entry : racerData.entrySet()) {
            if (entry.getValue().getRaceTimes().hasPassed()) {
                unpassedRacers.add(entry.getKey());
            }
        }

        return unpassedRacers;
    }

    public void setTime(int racerNumber, TimeTypes type, Date timeToSet) {
        if (racerData.containsKey(new Racer(racerNumber))) {
            notifyObservers();
            Objects.requireNonNull(racerData.get(new Racer(racerNumber))).getRaceTimes().setTime(timeToSet, type);
        } else {
            //TODO: ERROR HERE
        }
    }

}
