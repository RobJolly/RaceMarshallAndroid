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

public class RaceTimesTest {

    @Test
    public void writeToParcel() {
        //This will test setting random dates, with some null values (most accurate to a real situation)

        Random randomGen = new Random();
        RaceTimes testTimes = new RaceTimes();
        for (int i = 0; i < 100; i++) { //100 tests is reasonably quick, good range of values
            for (TimeTypes type : TimeTypes.values()) {
                if (randomGen.nextInt(2) == 0) { //50-50 chance value won't be set, will remain null
                    testTimes.setTime(new Date(randomGen.nextLong()), type);
                }
            }

            Parcel testParcel = Parcel.obtain(); //Parcel is created and written to
            testTimes.writeToParcel(testParcel, 0);
            testParcel.setDataPosition(0);

            RaceTimes createdTimes = new RaceTimes(testParcel); //New raceTimes created from the parcel

            assertEquals(testTimes.getInTime(), createdTimes.getInTime()); //All values are tested to ensure the new RaceTimes has been initialised correctly
            assertEquals(testTimes.getOutTime(), createdTimes.getOutTime());
            assertEquals(testTimes.getDroppedOutTime(), createdTimes.getDroppedOutTime());
            assertEquals(testTimes.getNotStartedTime(), createdTimes.getNotStartedTime());
            assertEquals(testTimes.getLastSetTime(), createdTimes.getLastSetTime());
        }
    }
}