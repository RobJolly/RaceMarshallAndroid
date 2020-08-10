package uk.co.robertjolly.racemarshallandroid.data;

import java.util.Calendar;
import java.util.Date;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

/**
 * Function to store In, Out, DroppedOut and NotStarted times, along with the time where a time was
 * last set.
 */
public class RaceTimes {
    Date inTime;
    Date outTime;
    Date droppedOutTime;
    Date notStartedTime;
    Date LastSetTime;

    /**
     * Allows the setting of a given time type, to a given date
     * @param time The time to be set to the given type
     * @param type The given type of time to set
     */
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

    /**
     * Sets the time of check in, to the given time
     * @param inTime the time to set
     */
    private void setInTime (Date inTime) {
        this.inTime = inTime;
        setLastSetTime();
    }

    /**
     * Sets the time of checked out, to the given time
     * @param outTime the time to set
     */
    private void setOutTime(Date outTime) {
        this.outTime = outTime;
        setLastSetTime();
    }

    /**
     * Sets the time of drop out, to the given time
     * @param droppedOutTime the time to set
     */
    private void setDroppedOutTime(Date droppedOutTime) {
        this.droppedOutTime = droppedOutTime;
        setLastSetTime();
    }

    /**
     * Sets the time of failed to start, to the given time
     * @param notStartedTime the time to set
     */
    private void setNotStartedTime(Date notStartedTime) {
        this.notStartedTime = notStartedTime;
        setLastSetTime();
    }

    /**
     * @return The time that a time was last set to the class
     */
    public Date getLastSetTime() {
        return LastSetTime;
    }

    /**
     *
     * @return The date of check in
     */
    public Date getInTime() {
        return inTime;
    }

    /**
     *
     * @return The date of check out
     */
    public Date getOutTime() {
        return outTime;
    }

    /**
     *
     * @return the date of drop out
     */
    public Date getDroppedOutTime() {
        return droppedOutTime;
    }

    /**
     *
     * @return the date of did not start
     */
    public Date getNotStartedTime() {
        return notStartedTime;
    }

    /**
     * Sets the time that this class last had a date set, to the current time
     */
    private void setLastSetTime() {
        LastSetTime = Calendar.getInstance().getTime();
    }

    /**
     * Determines if a racer has passed the checkpoint yet
     * @return If the racer has passed the checkpoint
     */
    public boolean hasPassed() {
        if (outTime != null) {
            return true;
        } else {
            return false;
        }
    }

}
