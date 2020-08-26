package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

//From https://github.com/google/gson. Apache 2.0 license.
import com.google.gson.annotations.SerializedName;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.io.Serializable;

/**
 * This class stores information about a racer (i.e. Racer Number)
 */
//TODO Find out why having a comparable here is a bad idea/warned against
public class Racer implements Comparable, Parcelable, Serializable {

    @SerializedName("racerNumber")
    private int racerNumber;

    /**
     * Constructor for the racer
     * @param racerNumber The number of the racer
     */
    public Racer(int racerNumber) {
        setRacerNumber(racerNumber);
    }


    /**
     * Getter for the number of the racer
     */
    public int getRacerNumber() {
        return racerNumber;
    }

    /**
     * Setter for the racer number
     * @param racerNumber the number of the racer
     */
    private void setRacerNumber(int racerNumber) {
        this.racerNumber = racerNumber;
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

    /**
     * Function required for Parcelable implementation
     * @return Always 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Constructor, given parcel from which racer shall be made
     * @param in parcel from which to construct racer
     */
    protected Racer(Parcel in) {
        racerNumber = in.readInt();
    }

    /**
     * Function required to implement Parcelable.
     */
    public static final Creator<Racer> CREATOR = new Creator<Racer>() {
        @Override
        public Racer createFromParcel(Parcel in) {
            return new Racer(in);
        }

        @Override
        public Racer[] newArray(int size) {
            return new Racer[size];
        }
    };

    /**
     * Writes the class's data to the given parcel
     * @param parcel parcel to write the data to
     * @param i the index/location from which to write
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(racerNumber);
    }

    /**
     * To string, specifically implemented such that JSON/GSON values can be gathered.
     * @return RacerNumber, in string form
     */
    @NonNull
    @Override
    public String toString() {
        return String.valueOf(racerNumber);
    }
}
