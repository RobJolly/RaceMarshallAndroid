package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.os.Parcel;

//Junit: https://junit.org/junit5/. Eclipse Public License - v 2.0.
import org.junit.Test;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.Date;
import java.util.Random;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

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

    @Test
    public void hasCheckpoint() {
    }

    @Test
    public void getCheckpoint() {
    }

    @Test
    public void addCheckpoint() {
    }

    @Test
    public void getCheckpointNumberList() {
    }

    @Test
    public void getCurrentCheckpointNumber() {
    }

    @Test
    public void setCurrentCheckpointNumber() {
    }

    @Test
    public void setTime() {
    }

    @Test
    public void clearCheckpoints() {
    }

}