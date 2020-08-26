package uk.co.robertjolly.racemarshallandroid.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//From https://github.com/google/gson. Apache 2.0 license.
import com.google.gson.annotations.SerializedName;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import javax.annotation.Nullable;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

/**
 * Checkpoints is a cass to store multiple checkpoints, with a single main 'active'/focused checkpoint.
 */
public class Checkpoints extends Observable implements Parcelable, Serializable {
    @SerializedName("checkpoints")
    ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    @SerializedName("currentCheckpointNumber")
    int currentCheckpointNumber = 0; //the checkpoint that is currently selected

    /**
     * Constructor for checkpoints.
     */
    public Checkpoints() {
    }

    /**
     * Getter for all checkpoints stored in the Checkpoints class
     * @return ArrayList containing all checkpoints
     */
    private ArrayList<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     * @param checkpointNumber The checkpoint number to search the checkpoints for.
     * @return If the checkpoint is in the list or not.
     */
    public boolean hasCheckpoint(int checkpointNumber) {
        if (hasCheckpoints()) {
            //Note since the checkpoints are expected to be very low in number (i.e. around 12-20), searching this way is rather slow, but probably fine.
            for (Checkpoint checkpoint : getCheckpoints()) { //for each checkpoint, check if they match the number input.
                if (checkpoint.getCheckPointNumber() == checkpointNumber) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param checkpointNumber The checkpoint number to search the checkpoints for.
     * @return The checkpoint with that checkpoint number in the list. Returns null if it cannot be found.
     */
    public Checkpoint getCheckpoint(int checkpointNumber) {
        //Note since the checkpoints are expected to be very low in number (i.e. around 12-20), searching this way is probably fine.
        for (Checkpoint checkpoint : getCheckpoints()) { //for each checkpoint, check if they match the number input.
            if (checkpoint.getCheckPointNumber() == checkpointNumber) {
                return checkpoint; //if so, return
            }
        }
        return null;
    }

    /**
     * This function adds a checkpoint to the list of checkpoints stored in the class, if it does not already exist.
     * Will not override the checkpoint if it does exist.
     * @param checkpoint Checkpoint to add
     */
    public void addCheckpoint(Checkpoint checkpoint) {
        if (hasCheckpoint(checkpoint.getCheckPointNumber())) {
            Log.w("Warning", "Checkpoint already exists. Cannot be added. Checkpoint number is: " + String.valueOf(checkpoint.getCheckPointNumber()));
        } else {
            checkpoints.add(checkpoint);
            setChanged();
        }
    }

    /**
     * @return An ArrayList of the checkpoint numbers that are stored.
     */
    public ArrayList<Integer> getCheckpointNumberList() {
        ArrayList<Integer> checkpointNumbers = new ArrayList<>();
        //Note since the checkpoints are expected to be very low in number (i.e. around 12-20), searching this way is probably fine.
        if (hasCheckpoints()) {
            for (Checkpoint checkpoint : getCheckpoints()) {
                checkpointNumbers.add(checkpoint.getCheckPointNumber());
            }
        }
        return checkpointNumbers;
    }

    /**
     * Getter for the currently selected checkpoint.
     * @return The currently selected checkpoint, null if currently selected checkpoint does not exist.
     */
    @Nullable
    public Checkpoint getCurrentSelectedCheckpoint() {
        Checkpoint toReturn = null;
        try {
            toReturn = this.getCheckpoint(getCurrentCheckpointNumber());
        } catch (Exception e) {
            //Checkpoint doesn't exist for w/e reason, don't need to do anything, but log it.
            Log.w("Warning", "Currently selected checkpoint cannot be found. Checkpoint selected is: " + String.valueOf(getCurrentCheckpointNumber()));
        }
        return toReturn;
    }

    /**
     * Getter for the selected checkpoint number
     * @return The currently selected checkpoint.
     */
    public int getCurrentCheckpointNumber() {
        return currentCheckpointNumber;
    }

    /**
     * Setter for the selected checkpoint number. Does not require that checkpoint exists.
     * @param currentCheckpointNumber The checkpoint that is selected.
     */
    public void setCurrentCheckpointNumber(int currentCheckpointNumber) {
        this.currentCheckpointNumber = currentCheckpointNumber;
        setChanged();
    }

    /**
     * Allows the setting of the input racer's time (given by TimeType) to the input date, for the currently selected checkpoint
     * @param racer The racer you wish to change
     * @param times The time of the racer you wish to change
     * @param date The date you wish to change to.
     */
    public void setTime(Racer racer, TimeTypes times, Date date) {
        if (hasCheckpoints()) {
            try {
                getCurrentSelectedCheckpoint().setTime(racer, times, date);
                setChanged();
            } catch (Exception e) {
                Log.e("Error", "Attempted to set time when no currently selected checkpoint exists. Selected checkpoint number is: " + String.valueOf(getCheckpointNumberList()));
            }
            getCurrentSelectedCheckpoint().setTime(racer, times, date);
            setChanged();
        } else {
            Log.w("Warning", "No checkpoints are stored at this time. Cannot set the time.");
        }
    }

    /**
     * Describe contents function, required for Parcelable implementation.
     * @return Always returns 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Function to write the contents of the class to the given parcel
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(currentCheckpointNumber);
        //Writing the array of checkpoints this way, as I was having errors using writeArray function.
        parcel.writeInt(getCheckpoints().size());
        for (Checkpoint checkpoint : getCheckpoints()) {
            parcel.writeParcelable(checkpoint, i);
        }
    }

    /**
     * Constructor to create class from the contents of the given parcel.
     * @param in
     */
    protected Checkpoints(Parcel in) {
        setCurrentCheckpointNumber(in.readInt());
        int numberOfCheckpoints = in.readInt();
        clearCheckpointData();

        for (int i = 0; i < numberOfCheckpoints; i++) {
            Checkpoint checkpoint = in.readParcelable(Checkpoint.class.getClassLoader());
            getCheckpoints().add(checkpoint);
        }
    }

    /**
     * Required for Parcelable implementation
     */
    public static final Creator<Checkpoints> CREATOR = new Creator<Checkpoints>() {
        @Override
        public Checkpoints createFromParcel(Parcel in) {
            return new Checkpoints(in);
        }

        @Override
        public Checkpoints[] newArray(int size) {
            return new Checkpoints[size];
        }
    };

    /**
     * Function to write the contents of the checkpoint to the given filename
     * @param filename filename to store
     * @param context context of app
     * @return boolean, indicating whether not write was successful
     */
    public boolean writeToFile(String filename, Context context) {
        boolean writeSuccessful = false;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (Exception e) {
            Log.e("Error", "Failed to write, couldn't open the given file. Error message: " + e.getMessage());
        }

        ObjectOutputStream objectOutputStream = null;
        try {
            if (fileOutputStream != null) { //if fileOutputStream couldn't be opened, no use trying
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to write, couldn't open the object output stream. Error message: " + e.getMessage());
        }

        try {
            if (objectOutputStream != null) { //if objectOutputStream couldn't be opened, no use trying
                objectOutputStream.writeObject(this);
                writeSuccessful = true; //write has been completed, change this so it indicates so.
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to object to the object output stream. Error message: " + e.getMessage());
        }

        try {
            if (fileOutputStream != null) { //if fileOutputStream couldn't be opened, no use trying to close
                fileOutputStream.close();
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to close file output stream. Error message: " + e.getMessage());
        }

        return writeSuccessful;
    }

    /**
     * Finds out whether or not this class has any checkpoints stored.
     * @return Boolean indicating, if number of checkpoints exceed 0.
     */
    public boolean hasCheckpoints() {
        return (getCheckpoints().size() > 0);

    }

    /**
     * Finds out for all checkpoints stored, the number of unpassed or unreported racers.
     * @return Number of unpassed or unreported racers, in all checkpoints.
     */
    public int getNumberUnpassedOrUnreported() {
        int numberUnreported = 0;
        for (Checkpoint checkpoint : checkpoints) {
            numberUnreported = numberUnreported + checkpoint.getNumberUnreportedAndUnpassedRacers();
        }

        return numberUnreported;
    }

    /**
     * Function to clear all stored checkpoints within this class. Does not alter currently selected checkpoint.
     */
    public void clearCheckpointData() {
        checkpoints.clear();
        setChanged();
    }

    /**
     * Function to completely delete the Checkpoint with the given checkpoint number. Does not alter currently selected checkpoint.
     */
    public void deleteCheckpoint(int checkpointNumber) {
        ArrayList<Checkpoint> toRemove = new ArrayList<>();
        //get list of all checkpoints with the given checkpoint number. Probably not needed but to be safe.
        if (getCheckpointNumberList().contains(checkpointNumber)) {
            for (Checkpoint checkpoint : checkpoints) {
                if (checkpoint.getCheckPointNumber() == checkpointNumber) {
                    toRemove.add(checkpoint);
                }
            }
        }

        getCheckpoints().removeAll(toRemove);
        setChanged();
    }

}
