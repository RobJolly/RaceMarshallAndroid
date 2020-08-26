package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//From https://github.com/google/gson. Apache 2.0 license.
import com.google.gson.annotations.SerializedName;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.io.Serializable;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

/**
 * Reported items is the class that handles which times, if any, have been marked as reported.
 */
public class ReportedItems implements Parcelable, Serializable {
    @SerializedName("inReported")
    private boolean inReported;
    @SerializedName("outReported")
    private boolean outReported;
    @SerializedName("droppedOutReported")
    private boolean droppedOutReported;
    @SerializedName("didNotStartReported")
    private boolean didNotStartReported;

    /**
     * Constructor for reported items.
     */
    public ReportedItems() {
    }

    /**
     * Sets a given item to the given reported state
     * @param toReport Time type, indicating which time to change
     * @param isReported Boolean, indicating whether or not the given time is reported or not.
     */
    public void setReportedItem(TimeTypes toReport, boolean isReported) {
        switch (toReport) {
            case IN :
                inReported = isReported;
                break;
            case OUT :
                outReported = isReported;
                break;
            case DROPPEDOUT:
                droppedOutReported = isReported;
                break;
            case DIDNOTSTART:
                didNotStartReported = isReported;
                break;
            default:
                Log.e("Error", "Attempted to change a TimeType that isn't checked for. Type is: " + toReport.name());
        }
    }

    /**
     * This gets whether or not the given item is reported.
     * @param toReport which type of time to check
     * @return boolean indicating whether or not the time of type, toReport is reported or not
     */
    public boolean getReportedItem(TimeTypes toReport) {
        switch (toReport) {
            case IN :
                return inReported;
            case OUT :
                return outReported;
            case DROPPEDOUT:
                return droppedOutReported;
            case DIDNOTSTART:
                return didNotStartReported;
            default:
                Log.e("Error", "Attempted to find information about a TimeType that isn't checked for. Type is: " + toReport.name());
                return false;
        }
    }

    /**
     * Constructor that will set data based on the given parcel.
     * @param in the parcel to take data from.
     */
    protected ReportedItems(Parcel in) {
        inReported = in.readByte() != 0;
        outReported = in.readByte() != 0;
        droppedOutReported = in.readByte() != 0;
        didNotStartReported = in.readByte() != 0;
    }

    /**
     * Writes the data of this object to the given parcel
     * @param dest The parcel in which to write data
     * @param flags Any flags for the parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (inReported ? 1 : 0));
        dest.writeByte((byte) (outReported ? 1 : 0));
        dest.writeByte((byte) (droppedOutReported ? 1 : 0));
        dest.writeByte((byte) (didNotStartReported ? 1 : 0));
    }

    /**
     * Required for Parcelable implementation
     * @return Always 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Required for Parcelable implementation
     */
    public static final Creator<ReportedItems> CREATOR = new Creator<ReportedItems>() {
        @Override
        public ReportedItems createFromParcel(Parcel in) {
            return new ReportedItems(in);
        }

        @Override
        public ReportedItems[] newArray(int size) {
            return new ReportedItems[size];
        }
    };
}
