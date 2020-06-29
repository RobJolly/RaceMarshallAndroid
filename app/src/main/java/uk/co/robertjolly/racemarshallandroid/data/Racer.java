package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Racer implements Comparable {

    short racerNumber;

    public Racer(short racerNumber) {
        this.racerNumber = racerNumber;
    }

    public short getRacerNumber() {
        return racerNumber;
    }

    //TODO Fix this dodgy code
    @Override
    public int compareTo(Object o) {
        Racer oRacer = (Racer) o;
        if (getRacerNumber() > oRacer.getRacerNumber()) {
            return 1;
        } else if (getRacerNumber() < ((Racer) o).getRacerNumber()){
            return -1;
        } else {
            return 0;
        }
    }
}
