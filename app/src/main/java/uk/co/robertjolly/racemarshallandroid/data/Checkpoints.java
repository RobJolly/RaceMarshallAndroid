package uk.co.robertjolly.racemarshallandroid.data;

import java.util.ArrayList;
import java.util.Observable;

public class Checkpoints extends Observable {
    ArrayList<Checkpoint> checkpoints = new ArrayList<>();

    private ArrayList<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     *
     * @param checkpointNumber The checkpoint number to search the checkpoints for.
     * @return If the checkpoint is in the list or not.
     */
    public boolean hasCheckpoint(int checkpointNumber) {
        //Note since the checkpoints are expected to be very low in number (i.e. around 12-20), searching this way is probably fine.
        for (Checkpoint checkpoint : getCheckpoints()) { //for each checkpoint, check if they match the number input.
            if (checkpoint.getCheckPointNumber() == checkpointNumber) {
                return true;
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
        }
    }

    /**
     *
     * @return A list of the checkpoint numbers that are stored.
     */
    public ArrayList<Integer> getCheckpointNumberList() {
        ArrayList<Integer> checkpointNumbers = new ArrayList<>();
        //Note since the checkpoints are expected to be very low in number (i.e. around 12-20), searching this way is probably fine.
        for (Checkpoint checkpoint : getCheckpoints()) {
            checkpointNumbers.add(checkpoint.getCheckPointNumber());
        }

        return checkpointNumbers;
    }
}
