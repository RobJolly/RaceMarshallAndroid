package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.os.Parcel;
import android.os.Parcelable;

//From https://github.com/google/gson. Apache 2.0 license.
import com.google.gson.annotations.SerializedName;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.io.Serializable;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

//TODO Java doc this
public class ReportedItems implements Parcelable, Serializable {
    @SerializedName("inReported")
    private boolean inReported;
    @SerializedName("outReported")
    private boolean outReported;
    @SerializedName("droppedOutReported")
    private boolean droppedOutReported;
    @SerializedName("didNotStartReported")
    private boolean didNotStartReported;

    //TODO Java doc this
    public ReportedItems() {
    }

    //TODO Java doc this
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
                //TODO: ADD ERROR HERE
        }
    }

    //TODO Java doc this
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
                //TODO: ADD ERROR HERE
                return false;
        }
    }

    //TODO Java doc this
    protected ReportedItems(Parcel in) {
        inReported = in.readByte() != 0;
        outReported = in.readByte() != 0;
        droppedOutReported = in.readByte() != 0;
        didNotStartReported = in.readByte() != 0;
    }

    //TODO Java doc this
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (inReported ? 1 : 0));
        dest.writeByte((byte) (outReported ? 1 : 0));
        dest.writeByte((byte) (droppedOutReported ? 1 : 0));
        dest.writeByte((byte) (didNotStartReported ? 1 : 0));
    }

    //TODO Java doc this
    @Override
    public int describeContents() {
        return 0;
    }

    //TODO Java doc this
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
