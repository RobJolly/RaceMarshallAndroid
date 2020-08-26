package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;


/**
 * Function to store In, Out, DroppedOut and NotStarted times, along with the time where a time was
 * last set.
 */
public class RaceTimes implements Parcelable, Serializable {
    @SerializedName("inTime")
    private Date inTime;
    @SerializedName("outTime")
    private Date outTime;
    @SerializedName("droppedOutTime")
    private Date droppedOutTime;
    @SerializedName("notStartedTime")
    private Date notStartedTime;
    @SerializedName("lastSetTime")
    private Date lastSetTime;

    //TODO Java doc this
    public RaceTimes() {
    }

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
        return lastSetTime;
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
        lastSetTime = Calendar.getInstance().getTime();
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

    //TODO Java doc this
    protected RaceTimes(Parcel in) {
        long inTime = in.readLong();
        if (inTime == -1) {
            this.inTime = null;
        } else {
            this.inTime = new Date(inTime);
        }

        long outTime = in.readLong();
        if (outTime == -1) {
            this.outTime = null;
        } else {
            this.outTime = new Date(outTime);
        }

        long droppedOutTime = in.readLong();
        if (droppedOutTime == -1) {
            this.droppedOutTime = null;
        } else {
            this.droppedOutTime = new Date(droppedOutTime);
        }

        long notStartedTime = in.readLong();
        if (notStartedTime == -1) {
            this.notStartedTime = null;
        } else {
            this.notStartedTime = new Date(notStartedTime);
        }

        long lastSetTime = in.readLong();
        if (lastSetTime == -1) {
            this.lastSetTime = null;
        } else {
            this.lastSetTime = new Date(lastSetTime);
        }

    }

    //TODO Java doc this
    public static final Creator<RaceTimes> CREATOR = new Creator<RaceTimes>() {
        @Override
        public RaceTimes createFromParcel(Parcel in) {
            return new RaceTimes(in);
        }

        @Override
        public RaceTimes[] newArray(int size) {
            return new RaceTimes[size];
        }
    };

    //TODO Java doc this
    @Override
    public int describeContents() {
        return 0;
    }

    //TODO Java doc this
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        try {
            parcel.writeLong(inTime.getTime());
        } catch (Exception e){
            parcel.writeLong(-1);
        }
        try {
            parcel.writeLong(outTime.getTime());
        } catch (Exception e){
            parcel.writeLong(-1);
        }
        try {
            parcel.writeLong(droppedOutTime.getTime());
        } catch (Exception e){
            parcel.writeLong(-1);
        }
        try {
            parcel.writeLong(notStartedTime.getTime());
        } catch (Exception e){
            parcel.writeLong(-1);
        }
        try {
            parcel.writeLong(lastSetTime.getTime());
        } catch (Exception e){
            parcel.writeLong(-1);
        }
    }

}
