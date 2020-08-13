package uk.co.robertjolly.racemarshallandroid.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class ReportedItems implements Parcelable {
    @SerializedName("inReported")
    boolean inReported;
    @SerializedName("outReported")
    boolean outReported;
    @SerializedName("droppedOutReported")
    boolean droppedOutReported;
    @SerializedName("didNotStartReported")
    boolean didNotStartReported;

    public ReportedItems() {
    }

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

    protected ReportedItems(Parcel in) {
        inReported = in.readByte() != 0;
        outReported = in.readByte() != 0;
        droppedOutReported = in.readByte() != 0;
        didNotStartReported = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (inReported ? 1 : 0));
        dest.writeByte((byte) (outReported ? 1 : 0));
        dest.writeByte((byte) (droppedOutReported ? 1 : 0));
        dest.writeByte((byte) (didNotStartReported ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
