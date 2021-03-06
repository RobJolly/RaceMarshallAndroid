package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.os.Parcel;

//Junit: https://junit.org/junit5/. Eclipse Public License - v 2.0.
import org.junit.Test;
import static org.junit.Assert.*;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.Date;
import java.util.Random;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class CheckpointTest {

    @Test
    public void writeToParcel() {
        Checkpoint testCheckpoint = new Checkpoint(2, 150);
        Random randGen = new Random();
        //testCheckpoint.getReportedRaceTime(new Racer(3)).getRaceTimes();
        testCheckpoint.getRacerData(new Racer(3)).getRaceTimes().setTime(new Date(randGen.nextLong()), TimeTypes.OUT);
        testCheckpoint.setReportedItem(3, TimeTypes.OUT, true);

        Parcel testParcel = Parcel.obtain();
        testCheckpoint.writeToParcel(testParcel, 0);
        testParcel.setDataPosition(0);
        Checkpoint grabbedPoint = new Checkpoint(testParcel);

        assertEquals(testCheckpoint.getCheckPointNumber(), grabbedPoint.getCheckPointNumber());
        assertEquals(testCheckpoint.getRacerData(new Racer(150)).getRaceTimes().getLastSetTime(), grabbedPoint.getRacerData(new Racer(150)).getRaceTimes().getLastSetTime());
        assertEquals(testCheckpoint.getRacerData(new Racer(150)).getReportedItems().getReportedItem(TimeTypes.DROPPEDOUT), grabbedPoint.getRacerData(new Racer(150)).getReportedItems().getReportedItem(TimeTypes.DROPPEDOUT));
        assertEquals(testCheckpoint.getRacerData(new Racer(3)).getRaceTimes().getLastSetTime(), grabbedPoint.getRacerData(new Racer(3)).getRaceTimes().getLastSetTime());
        assertEquals(testCheckpoint.getRacerData(new Racer(3)).getReportedItems().getReportedItem(TimeTypes.OUT), grabbedPoint.getRacerData(new Racer(3)).getReportedItems().getReportedItem(TimeTypes.OUT));
    }
}