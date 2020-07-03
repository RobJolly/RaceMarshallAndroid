package uk.co.robertjolly.racemarshallandroid.data;

import java.util.Calendar;
import java.util.Date;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class RaceTimes {
    Date inTime;
    Date outTime;
    Date droppedOutTime;
    Date notStartedTime;
    Date LastSetTime;

    public void setTime(Date time, TimeTypes type ) {
        switch (type) {
            case IN :
                setInTime(time);
                break;
            case OUT :
                setOutTime(time);
                break;
            case DROPPEDOUT:
                setDroppedOutTime(time);
                break;
            case DIDNOTSTART:
                setNotStartedTime(time);
                break;
            default:
                //TODO: ADD ERROR HERE
        }
    }

    private void setInTime (Date inTime) {
        this.inTime = inTime;
        setLastSetTime();
    }

    private void setOutTime(Date outTime) {
        this.outTime = outTime;
        setLastSetTime();
    }

    private void setDroppedOutTime(Date droppedOutTime) {
        this.droppedOutTime = droppedOutTime;
        setLastSetTime();
    }

    private void setNotStartedTime(Date notStartedTime) {
        this.notStartedTime = notStartedTime;
        setLastSetTime();
    }

    public Date getLastSetTime() {
        return LastSetTime;
    }

    private void setLastSetTime() {
        LastSetTime = Calendar.getInstance().getTime();
    }

    public boolean hasPassed() {
        if (outTime != null) {
            return true;
        } else {
            return false;
        }
    }

}
