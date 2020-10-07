package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

//From https://github.com/google/gson. Apache 2.0 license.
import com.google.gson.annotations.SerializedName;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.io.Serializable;
import java.text.SimpleDateFormat;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

/**
 * Class to store and handle both the RaceTimes, and Whether or not they've been reported.
 */
public class ReportedRaceTimes implements Parcelable, Serializable {
    @SerializedName("raceTimes")
    private RaceTimes raceTimes;
    @SerializedName("reportedItems")
    private ReportedItems reportedItems;

    /**
     * Constructor for ReportedRaceTimes
     */
    public ReportedRaceTimes() {
        raceTimes = new RaceTimes();
        reportedItems = new ReportedItems();
    }

    /**
     * Gets the RaceTimes stored in this object
     * @return the RaceTimes for this object
     */
    public RaceTimes getRaceTimes() {
        return raceTimes;
    }

    /**
     * Gets the reportedItems stored in this object
     * @return the reportedItems stored in this object
     */
    public ReportedItems getReportedItems() {
        return reportedItems;
    }

    /**
     * Checks whether or not all of the SET times have been reported. Will return true if no time is set.
     * @return Boolean, indicating if all of the times stored have been set
     */
    public boolean allReported() {
        return (inReportedIfSet() & outReportedIfSet() & droppedOutReportedIfSet() & didNotStartReportedIfSet());
    }

    /**
     * Find if there is an in-time reported, if it is set. Returns true if it isn't set.
     * @return Whether or not, if set, the in-time is reported. Returns true if it isn't set.
     */
    public boolean inReportedIfSet() {
        if (getRaceTimes().getInTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.IN));
        } else {
            return true;
        }
    }

    /**
     * Find if there is an out-time reported, if it is set. Returns true if it isn't set.
     * @return Whether or not, if set, the out-time is reported. Returns true if it isn't set.
     */
    public boolean outReportedIfSet() {
        if (getRaceTimes().getOutTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.OUT));
        } else {
            return true;
        }
    }

    /**
     * Find if there is an dropped-out-time reported, if it is set. Returns true if it isn't set.
     * @return Whether or not, if set, the dropped-out-time is reported. Returns true if it isn't set.
     */
    public boolean droppedOutReportedIfSet() {
        if (getRaceTimes().getDroppedOutTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.DROPPEDOUT));
        } else {
            return true;
        }
    }

    /**
     * Find if there is an did-not-start-time reported, if it is set. Returns true if it isn't set.
     * @return Whether or not, if set, the did-not-start-time is reported. Returns true if it isn't set.
     */
    public boolean didNotStartReportedIfSet() {
        if (getRaceTimes().getNotStartedTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.DIDNOTSTART));
        } else {
            return true;
        }
    }

    /**
     * Sets all unreported items to reported, if the time associated has been set
     *
     */
    public void setAllUnreportedToReported() {
        if (!inReportedIfSet()) {
            getReportedItems().setReportedItem(TimeTypes.IN, true);
        }
        if (!outReportedIfSet()) {
            getReportedItems().setReportedItem(TimeTypes.OUT, true);
        }
        if (!droppedOutReportedIfSet()) {
            getReportedItems().setReportedItem(TimeTypes.DROPPEDOUT, true);
        }
        if (!didNotStartReportedIfSet()) {
            getReportedItems().setReportedItem(TimeTypes.DIDNOTSTART, true);
        }
    }

    /**
     * Sets all reported items to unreported.
     */
    public void setAllReportedToUnreported() {
        getReportedItems().setReportedItem(TimeTypes.IN, false);
        getReportedItems().setReportedItem(TimeTypes.OUT, false);
        getReportedItems().setReportedItem(TimeTypes.DROPPEDOUT, false);
        getReportedItems().setReportedItem(TimeTypes.DIDNOTSTART, false);
    }

    /**
     * Gets a time, of the given type, in the HH:mm:ss format, with words representing the given time type,
     * (e.g. in for checked in, dropped out for dropped out, etc.), with the words representing the given time type
     * bracketed if the given type has also been reported. The formatted time is separated from the descriptor by ": ".
     * An example of a string produced would be: "(dropped out): 12:42:33"
     * @param type The type of the time to find
     * @return String representing time formatted as explained above. Empty String if specified time isn't set.
     */
    public String getFormattedDisplayTime(TimeTypes type, Resources resources) {
        String toReturn = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        switch (type) {
            case IN:
                if (getRaceTimes().getInTime() != null) {
                    if (getReportedItems().getReportedItem(TimeTypes.IN)) {
                        toReturn = toReturn + "(" + resources.getString(R.string.in_uncaps) + "): ";
                    } else {
                        toReturn = toReturn + resources.getString(R.string.in_uncaps) + ": ";
                    }
                    toReturn = toReturn + timeFormat.format(getRaceTimes().getInTime());
                }
                break;
            case OUT:
                if (getRaceTimes().getOutTime() != null) {
                    if (getReportedItems().getReportedItem(TimeTypes.OUT)) {
                        toReturn = toReturn + "(" + resources.getString(R.string.out_uncaps) + "): ";
                    } else {
                        toReturn = toReturn + resources.getString(R.string.out_uncaps) + ": ";
                    }
                    toReturn = toReturn + timeFormat.format(getRaceTimes().getOutTime());
                }
                break;
            case DROPPEDOUT:
                if (getRaceTimes().getDroppedOutTime() != null) {
                    if (getReportedItems().getReportedItem(TimeTypes.DROPPEDOUT)) {
                        toReturn = toReturn + "(" + resources.getString(R.string.dropped_out_uncaps) + "): ";
                    } else {
                        toReturn = toReturn + resources.getString(R.string.dropped_out_uncaps) + ": ";
                    }
                    toReturn = toReturn + timeFormat.format(getRaceTimes().getDroppedOutTime());
                }
                break;
            case DIDNOTSTART:
                if (getRaceTimes().getNotStartedTime() != null) {
                    if (getReportedItems().getReportedItem(TimeTypes.DIDNOTSTART)) {
                        toReturn = toReturn + "(" + resources.getString(R.string.did_not_start_uncaps) + "): ";
                    } else {
                        toReturn = toReturn + resources.getString(R.string.did_not_start_uncaps) + ": ";
                    }
                    toReturn = toReturn + timeFormat.format(getRaceTimes().getNotStartedTime());
                }
        }

        return toReturn;
    }

    /**
     * Function required for implementation of Parcelable.
     * @return Always 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the data from this class to the given parcel
     * @param parcel The parcel in which to write
     * @param i flags
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(raceTimes, i);
        parcel.writeParcelable(reportedItems, i);
    }

    /**
     * Constructor to initialise this class from the given parcel.
     * @param in The parcel from which to construct
     */
    protected ReportedRaceTimes(Parcel in) {
        raceTimes = in.readParcelable(RaceTimes.class.getClassLoader());
        reportedItems = in.readParcelable(ReportedItems.class.getClassLoader());
    }

    /**
     * Function required for Parcelable implementation
     */
    public static final Creator<ReportedRaceTimes> CREATOR = new Creator<ReportedRaceTimes>() {
        @Override
        public ReportedRaceTimes createFromParcel(Parcel in) {
            return new ReportedRaceTimes(in);
        }

        @Override
        public ReportedRaceTimes[] newArray(int size) {
            return new ReportedRaceTimes[size];
        }
    };

    /**
     * This clears the time stored for a given time type.
     * @param type the type of the time to clear
     */
    public void clearTime(TimeTypes type) {
        getRaceTimes().clearTime(type);
        getReportedItems().setReportedItem(type, false);
    }

}
