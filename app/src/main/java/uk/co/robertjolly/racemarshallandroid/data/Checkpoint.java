package uk.co.robertjolly.racemarshallandroid.data;

import java.util.Objects;
import java.util.SortedMap;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;


public class Checkpoint {
    short checkPointNumber;
    SortedMap<Racer, ReportedRaceTimes> racerData;

    public Checkpoint(short checkPointNumber, short racerNumber) {
        this.checkPointNumber = checkPointNumber;

        for (short i = 0; i < racerNumber; i++) {
            racerData.put(new Racer(racerNumber), new ReportedRaceTimes());
        }
    }

    public boolean reportedItem(short racerNumber, TimeTypes type) {
        if (racerData.containsKey(new Racer(racerNumber))) {
            return Objects.requireNonNull(racerData.get(new Racer(racerNumber))).reportedItems.getReportedItem(type);
        } else {
            //TODO: ERROR HERE
            return false;
        }
    }

    public void setReportedItem(short racerNumber, TimeTypes type, boolean isReported) {
        if (racerData.containsKey(new Racer(racerNumber))) {
            Objects.requireNonNull(racerData.get(new Racer(racerNumber))).reportedItems.setReportedItem(type,isReported);
        } else {
            //TODO: ERROR HERE
        }
    }


}
