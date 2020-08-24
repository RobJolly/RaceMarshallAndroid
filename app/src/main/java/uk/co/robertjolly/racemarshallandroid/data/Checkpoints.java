package uk.co.robertjolly.racemarshallandroid.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

/**
 * Checkpoints is a cass to store multiple checkpoints, with a single main 'active'/focused checkpoint.
 */
public class Checkpoints extends Observable implements Parcelable, Serializable {
    @SerializedName("checkpoints")
    ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    @SerializedName("currentCheckpointNumber")
    int currentCheckpointNumber = 0;

    //TODO Java doc this
    public Checkpoints() {
    }

    //TODO Java doc this
    private ArrayList<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     *
     * @param checkpointNumber The checkpoint number to search the checkpoints for.
     * @return If the checkpoint is in the list or not.
     */
    public boolean hasCheckpoint(int checkpointNumber) {
        if (hasCheckpoints()) {
            //Note since the checkpoints are expected to be very low in number (i.e. around 12-20), searching this way is probably fine.
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
     *
     * @param checkpoint Checkpoint to add
     */
    public void addCheckpoint(Checkpoint checkpoint) {
        if (hasCheckpoint(checkpoint.getCheckPointNumber())) {
            //TODO Add Error Here
        } else {
            checkpoints.add(checkpoint);
            setChanged();
        }
    }

    /**
     *
     * @return A list of the checkpoint numbers that are stored.
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
     *
     * @return The currently selected checkpoint.
     */
    public int getCurrentCheckpointNumber() {
        return currentCheckpointNumber;
    }

    /**
     *
     * @param currentCheckpointNumber The checkpoint that is selected.
     */
    public void setCurrentCheckpointNumber(int currentCheckpointNumber) {
        //TODO Error checking - make sure input checkpoint exists.
        this.currentCheckpointNumber = currentCheckpointNumber;
        setChanged();
    }

    //TODO Java doc this
    public void setTime(Racer racer, TimeTypes times, Date date) {
        if ((hasCheckpoints()) && (hasCheckpoint(getCurrentCheckpointNumber()))) {
            getCheckpoint(getCurrentCheckpointNumber()).setTime(racer, times, date);
            setChanged();
        }
    }

    //TODO Java doc this
    public void clearCheckpoints() {
        getCheckpoints().clear();
    }

    //TODO Java doc this
    @Override
    public int describeContents() {
        return 0;
    }

    //TODO Java doc this
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(currentCheckpointNumber);
        parcel.writeInt(getCheckpoints().size());
        for (Checkpoint checkpoint : getCheckpoints()) {
            parcel.writeParcelable(checkpoint, i);
        }
    }

    //TODO Java doc this
    protected Checkpoints(Parcel in) {
        currentCheckpointNumber = in.readInt();
        int numberOfCheckpoints = in.readInt();
        getCheckpoints().clear();

        for (int i = 0; i < numberOfCheckpoints; i++) {
            Checkpoint checkpoint = in.readParcelable(Checkpoint.class.getClassLoader());
            getCheckpoints().add(checkpoint);
        }
    }

    //TODO Java doc this
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

    //TODO Java doc this
    //TODO improve this mess of code
    public boolean writeToFile(String filename, Context context) throws IOException {
        boolean writeSucsessful = false;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (Exception e) {
            //failed to write to file
        }

        ObjectOutputStream objectOutputStream = null;
        try {
            if (fileOutputStream != null) {
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
            }
        } catch (Exception e) {
            //failed to write to file
        }

        try {
            if (objectOutputStream != null) {
                objectOutputStream.writeObject(this);
                writeSucsessful = true;
            }
        } catch (Exception e) {
            //failed to write
        }

        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (Exception e) {
            //failed to write
        }


        return writeSucsessful;
    }

    //TODO Java doc this
    public boolean hasCheckpoints() {
        return (getCheckpoints().size() > 0);

    }

    //TODO Java doc this
    public int getNumberUnreported() {
        int numberUnreported = 0;
        for (Checkpoint checkpoint : checkpoints) {
            numberUnreported = numberUnreported + checkpoint.getNumberUnreported();
        }

        return numberUnreported;
    }

    //TODO Java doc this
    public void clearCheckpointData() {
        checkpoints.clear();
        setChanged();
    }

    //TODO Java doc this
    public void deleteCheckpoint(int checkpointNumber) {
        ArrayList<Checkpoint> toRemove = new ArrayList<>();
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
