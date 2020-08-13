package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Parcel;

import org.junit.Test;

import java.util.Date;
import java.util.Random;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

import static org.junit.Assert.*;

public class CheckpointsTest {

    @Test
    public void writeToParcel() {
        //TODO Finish these tests
        Checkpoints testCheckpoints = new Checkpoints();
        testCheckpoints.addCheckpoint(new Checkpoint(1, 150));
        testCheckpoints.addCheckpoint(new Checkpoint(3, 150));
        testCheckpoints.currentCheckpointNumber = 3;

        testCheckpoints.setTime(new Racer(5), TimeTypes.DIDNOTSTART, new Date(new Random().nextLong()));

        Parcel testParcel = Parcel.obtain();
        testCheckpoints.writeToParcel(testParcel, 0);
        testParcel.setDataPosition(0);
        Checkpoints grabbedPoints = new Checkpoints(testParcel);
    }
}