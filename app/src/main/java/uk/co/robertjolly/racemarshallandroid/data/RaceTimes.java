package uk.co.robertjolly.racemarshallandroid.data;

import java.util.Calendar;
import java.util.Date;

public class RaceTimes {
    Date inTime;
    Date outTime;
    Date droppedOutTime;
    Date notStartedTime;
    Date LastSetTime;

    public void setInTime (Date inTime) {

    }

    public void setOutTime(Date outTime) {

    }

    public void setDroppedOutTime(Date droppedOutTime) {

    }

    public void setNotStartedTime(Date notStartedTime) {

    }

    public Date getLastSetTime() {
        return LastSetTime;
    }

    private void setLastSetTime() {
        LastSetTime = Calendar.getInstance().getTime();
    }
}
