package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class stores information about a racer (i.e. Racer Number)
 */
public class Racer implements Comparable {

    int racerNumber;

    /**
     * Constructor for the racer
     * @param racerNumber The number of the racer
     */
    public Racer(int racerNumber) {
        this.racerNumber = racerNumber;
    }

    /**
     * Getter for the number of the racer
     */
    public int getRacerNumber() {
        return racerNumber;
    }

    /**
     * Comparison function for two racers, to determine which racer is 'greater' than the other
     * Compares based on racer numbers.
     */
    @Override
    public int compareTo(Object o) {
        Racer oRacer;
        oRacer = (Racer) o;
        if (getRacerNumber() > oRacer.getRacerNumber()) {
            return 1;
        } else if (getRacerNumber() < ((Racer) o).getRacerNumber()){
            return -1;
        } else {
            return 0;
        }
    }
}
