package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Parcel;

import org.junit.Test;

import java.util.Date;
import java.util.Random;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

import static org.junit.Assert.*;

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