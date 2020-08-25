package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.Objects;
import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

/**
 * A numbered checkpoint - Storing information about racers in the race, their given times and if those times
 * have been reported yet.
 */
public class Checkpoint extends Observable implements Parcelable, Serializable {
    @SerializedName("checkPointNumber")
    int checkPointNumber;
    @SerializedName("racerData")
    TreeMap<Racer, ReportedRaceTimes> racerData;

    /**
     * Constructor for the checkpoint
     * @param checkPointNumber the number of the given checkpoint
     * @param racerNumber The number of racers in the race (Index 0)
     */
    public Checkpoint(int checkPointNumber, int racerNumber) {
        this.checkPointNumber = checkPointNumber;
        racerData = new TreeMap<>();
        for (int i = 0; i < racerNumber; i++) {
            racerData.put(new Racer(i+1), new ReportedRaceTimes());
        }
    }

    /**
     *
     * @param racerNumber The number of the racer
     * @param type The time type to check
     * @return Whether or not the given racer has the given time reported, in the checkpoint
     */
    public boolean reportedItem(int racerNumber, TimeTypes type) {
        if (racerData.containsKey(new Racer(racerNumber))) {
            return Objects.requireNonNull(racerData.get(new Racer(racerNumber))).getReportedItems().getReportedItem(type);
        } else {
            //TODO: ERROR HERE
            return false;
        }
    }

    /**
     * @return the number of the checkpoint
     */
    public int getCheckPointNumber() {
        return checkPointNumber;
    }

    /**
     * Is able to set whether or not a given time, for a given racer, is marked as reported or not
     * @param racerNumber the racer for which to change
     * @param type the type of time that you wish to update
     * @param isReported whether or not you wish to set the given time as reported or not reported
     */
    public void setReportedItem(int racerNumber, TimeTypes type, boolean isReported) {
        if (racerData.containsKey(new Racer(racerNumber))) {
            Objects.requireNonNull(racerData.get(new Racer(racerNumber))).getReportedItems().setReportedItem(type,isReported);
            setChanged();
        } else {
            //TODO: ERROR HERE
        }
    }

    /**
     * @return A list of racers that have not passed the checkpoint so far. Includes those checked into the checkpoint.
     */
    public ArrayList<Racer> getAllNotPassed() {
        ArrayList<Racer> unpassedRacers = new ArrayList<>();
        for (HashMap.Entry<Racer, ReportedRaceTimes> entry : racerData.entrySet()) {
            if (!entry.getValue().getRaceTimes().hasPassed()) {
                unpassedRacers.add(entry.getKey());
            }
        }

        return unpassedRacers;
    }

    /**
     * @return A list of racers that have passed the checkpoint so far. Does not include those checked into the checkpoint.
     */
    public ArrayList<Racer> getAllPassed() {
        ArrayList<Racer> unpassedRacers = new ArrayList<>();
        for (HashMap.Entry<Racer, ReportedRaceTimes> entry : racerData.entrySet()) {
            if (entry.getValue().getRaceTimes().hasPassed()) {
                unpassedRacers.add(entry.getKey());
            }
        }

        return unpassedRacers;
    }

    /**
     * Sets a given time type for the given racer, to the input time.
     * @param racer The racer for which data is to be changed
     * @param type The time of the time you wish to change
     * @param timeToSet The time you wish to set the time to
     */
    public void setTime(Racer racer, TimeTypes type, Date timeToSet) {
        if (racerData.containsKey(racer)) {
            Objects.requireNonNull(racerData.get(racer)).getRaceTimes().setTime(timeToSet, type);
            setChanged();
        } else {
            //TODO: ERROR HERE
        }
    }

    /**
     * @return A list of racers where atleast one time has been set, at any point.
     */
    public TreeMap<Racer, ReportedRaceTimes> getRacersWithTimes() {
        TreeMap<Racer, ReportedRaceTimes> setTimes = new TreeMap<>();

        for (Racer racer : racerData.keySet()) {
            if (racerData.get(racer).getRaceTimes().getLastSetTime() != null) {
                setTimes.put(racer, racerData.get(racer));
            }
        }

        return setTimes;
    }

    /**
     * Getter for the race times of a given racer
     * @param racer racer to get the times for
     * @return the ReportedRaceTimes associated with the given racer
     */
    public ReportedRaceTimes getRacerData(Racer racer) {
        return racerData.get(racer);
    }

    /**
     * @param racer the racer for which to get the ReportedRaceTimes for
     * @return the list of ReportedRaceTimes for the given racer
     */
    public ReportedRaceTimes getReportedRaceTime(Racer racer) {
        return racerData.get(racer);
    }

    /**
     * @return A list of racers stored within the checkpoint.
     */
    public Set<Racer> getRacers() {
        return racerData.keySet();
    }

    //TODO Java doc this
    @Override
    public int describeContents() {
        return 0;
    }

    //TODO Java doc this
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(checkPointNumber);
        parcel.writeInt(racerData.keySet().size());
        for (Racer racer : racerData.keySet()) {
            parcel.writeParcelable(racer, i);
            parcel.writeParcelable(racerData.get(racer), i);
        }
    }

    //TODO Java doc this
    protected Checkpoint(Parcel in) {
        this.checkPointNumber = in.readInt();
        int numberOfRacers = in.readInt();

        racerData = new TreeMap<>();

        int count = 0;
        for (int i = 0; i < numberOfRacers; i++) {
            Racer racer = in.readParcelable(Racer.class.getClassLoader());
            ReportedRaceTimes times = in.readParcelable(ReportedRaceTimes.class.getClassLoader());
            racerData.put(racer, times);
        }
    }

    //TODO Java doc this
    public static final Creator<Checkpoint> CREATOR = new Creator<Checkpoint>() {
        @Override
        public Checkpoint createFromParcel(Parcel in) {
            return new Checkpoint(in);
        }

        @Override
        public Checkpoint[] newArray(int size) {
            return new Checkpoint[size];
        }
    };

    //TODO Java doc this
    public int getNumberUnreported() {
        int notReportedCount = 0;
        for (ReportedRaceTimes times : racerData.values()) {
            if (!times.allReported()) { //if all times haven't been reported
                notReportedCount++;
            } else if (!times.getRaceTimes().hasPassed()) { //if the racer hasn't yet passed
                notReportedCount++;
            }
        }

        return notReportedCount;
    }
}
