package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//From https://github.com/google/gson. Apache 2.0 license.
import com.google.gson.annotations.SerializedName;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

/**
 * A numbered checkpoint - Storing information about racers in the race, their given times and if those times
 * have been reported yet.
 */
public class Checkpoint extends Observable implements Parcelable, Serializable {
    @SerializedName("checkPointNumber")
    int checkPointNumber;
    @SerializedName("racerData")
    TreeMap<Racer, ReportedRaceTimes> racerData = new TreeMap<>(new TreeMap<>((Comparator<Racer> & Serializable) Racer::compareTo));

    /**
     * Constructor for the checkpoint
     * @param checkPointNumber the number of the given checkpoint
     * @param racerNumber The number of racers in the race (Index 0)
     */
    public Checkpoint(int checkPointNumber, int racerNumber) {
        this.checkPointNumber = checkPointNumber;
        for (int i = 0; i < racerNumber; i++) {
            racerData.put(new Racer(i+1), new ReportedRaceTimes());
        }
    }

    /**
     *
     * @param racerNumber The number of the racer
     * @param type The time type to check
     * @return Whether or not the given racer has the given time reported, in the checkpoint, if the racer exists in the checkpoint.
     */
    public boolean reportedItem(int racerNumber, TimeTypes type) {
        if (racerData.containsKey(new Racer(racerNumber))) {
            ReportedRaceTimes reportedRaceTimes;
            reportedRaceTimes = racerData.get(new Racer(racerNumber));
            if (reportedRaceTimes != null) {
                ReportedItems reportedItems = reportedRaceTimes.getReportedItems();
                if (reportedItems != null) {
                    return reportedItems.getReportedItem(type);
                } else {
                    Log.w("Warning", "The racers ReportedItems are null. Racer number: " + String.valueOf(racerNumber));
                    return false;
                }
            } else {
                Log.w("Warning", "The racers ReportedRaceTimes are null. Racer number: " + String.valueOf(racerNumber));
                return false;
            }
        } else {
            Log.w("Warning", "The Racer doesn't exist. Racer number: " + String.valueOf(racerNumber));
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
            Log.e("Error", "The racer doesn't exist to change.");
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
            ReportedRaceTimes grabbedReportedRacerTimes = racerData.get(racer);
            if (grabbedReportedRacerTimes != null) {
                grabbedReportedRacerTimes.getRaceTimes().setTime(timeToSet, type);
                setChanged(); //set changed so various Observers can be noticed when chosen.
            } else {
                Log.e("Error", "The given racers does not have any ReportedRacerTimes, unexpectedly");
            }
        } else {
            Log.e("Error", "The given racer doesn't exist to setTime. Racer Number: " + String.valueOf(racer.getRacerNumber()));
        }
    }

    /**
     * @return A list of racers where atleast one time has been set, at any point.
     */
    public TreeMap<Racer, ReportedRaceTimes> getRacersWithTimes() {
        TreeMap<Racer, ReportedRaceTimes> setTimes = new TreeMap<>();

        for (Racer racer : racerData.keySet()) { //for all racers
            ReportedRaceTimes grabbedReportedRaceTimes; //get their reported times
            grabbedReportedRaceTimes = racerData.get(racer);
            if (grabbedReportedRaceTimes != null) {
                if (grabbedReportedRaceTimes.getRaceTimes().getLastSetTime() != null) {
                    setTimes.put(racer, racerData.get(racer));
                }
            } else {
                Log.e("Error", "One of the given racers does not have any ReportedRacerTimes. Racer Number: " + String.valueOf(racer.getRacerNumber()));
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

    /**
     * describeContents is reported for Parcelable
     * @return Always 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write the contents of this class to the given parcel
     * @param parcel parcel to write to
     * @param i parcel writing location
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(checkPointNumber);
        parcel.writeInt(racerData.keySet().size());
        //Doing this rather than writeParcelableArray as it was giving errors.
        for (Racer racer : racerData.keySet()) {
            parcel.writeParcelable(racer, i);
            parcel.writeParcelable(racerData.get(racer), i);
        }
    }

    /**
     * Constructor, from which Checkpoint will be written from Parcel.
     * @param in parcel from which to construct checkpoint
     */
    protected Checkpoint(Parcel in) {
        this.checkPointNumber = in.readInt();
        int numberOfRacers = in.readInt();

        racerData = new TreeMap<>();
        //Doing this rather than readParcelableArray as it was giving errors.
        for (int i = 0; i < numberOfRacers; i++) {
            Racer racer = in.readParcelable(Racer.class.getClassLoader());
            ReportedRaceTimes times = in.readParcelable(ReportedRaceTimes.class.getClassLoader());
            racerData.put(racer, times);
        }
    }

    /**
     * Required for Parcelable implementation.
     */
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

    /**
     * Gets the number of unreported and unpassed racers. An unreported unpassed racer will be counted once.
     * @return Number of unreported and unpassed racers.
     */
    public int getNumberUnreportedAndUnpassedRacers() {
        int notReportedCount = 0;
        for (ReportedRaceTimes times : racerData.values()) { //for all racers
            if (!times.allReported()) { //if all times haven't been reported
                notReportedCount++;
            } else if (!times.getRaceTimes().hasPassed()) { //if the racer hasn't yet passed
                notReportedCount++;
            }
        }

        return notReportedCount;
    }
}
