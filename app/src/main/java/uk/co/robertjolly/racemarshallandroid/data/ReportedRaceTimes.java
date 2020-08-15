package uk.co.robertjolly.racemarshallandroid.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

//TODO Java doc this
public class ReportedRaceTimes implements Parcelable, Serializable {
    @SerializedName("raceTimes")
    private RaceTimes raceTimes;
    @SerializedName("reportedItems")
    private ReportedItems reportedItems;

    //TODO Java doc this
    public ReportedRaceTimes() {
        raceTimes = new RaceTimes();
        reportedItems = new ReportedItems();
    }

    //TODO Java doc this
    public RaceTimes getRaceTimes() {
        return raceTimes;
    }

    //TODO Java doc this
    public ReportedItems getReportedItems() {
        return reportedItems;
    }

    //TODO Java doc this
    public boolean allReported() {
        return (inReportedIfSet() & outReportedIfSet() & droppedOutReportedIfSet() & didNotStartReportedIfSet());
    }

    //TODO Java doc this
    public boolean inReportedIfSet() {
        if (getRaceTimes().getInTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.IN));
        } else {
            return true;
        }
    }

    //TODO Java doc this
    public boolean outReportedIfSet() {
        if (getRaceTimes().getOutTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.OUT));
        } else {
            return true;
        }
    }

    //TODO Java doc this
    public boolean droppedOutReportedIfSet() {
        if (getRaceTimes().getDroppedOutTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.DROPPEDOUT));
        } else {
            return true;
        }
    }

    //TODO Java doc this
    public boolean didNotStartReportedIfSet() {
        if (getRaceTimes().getNotStartedTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.DIDNOTSTART));
        } else {
            return true;
        }
    }

    //TODO Java doc this
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

    //TODO Java doc this
    public void setAllReportedToUnreported() {
        getReportedItems().setReportedItem(TimeTypes.IN, false);
        getReportedItems().setReportedItem(TimeTypes.OUT, false);
        getReportedItems().setReportedItem(TimeTypes.DROPPEDOUT, false);
        getReportedItems().setReportedItem(TimeTypes.DIDNOTSTART, false);
    }

    //TODO Java doc this
    public String getFormattedDisplayTime(TimeTypes type) {
        String toReturn = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        switch (type) {
            case IN:
                if (getRaceTimes().getInTime() != null) {
                    if (getReportedItems().getReportedItem(TimeTypes.IN)) {
                        toReturn = toReturn + "(" + "in" + "): ";
                    } else {
                        toReturn = toReturn + "in" + ": ";
                    }
                    toReturn = toReturn + timeFormat.format(getRaceTimes().getInTime());
                }
                break;
            case OUT:
                if (getRaceTimes().getOutTime() != null) {
                    if (getReportedItems().getReportedItem(TimeTypes.OUT)) {
                        toReturn = toReturn + "(" + "out" + "): ";
                    } else {
                        toReturn = toReturn + "out" + ": ";
                    }
                    toReturn = toReturn + timeFormat.format(getRaceTimes().getOutTime());
                }
                break;
            case DROPPEDOUT:
                if (getRaceTimes().getDroppedOutTime() != null) {
                    if (getReportedItems().getReportedItem(TimeTypes.DROPPEDOUT)) {
                        toReturn = toReturn + "(" + "dropped out" + "): ";
                    } else {
                        toReturn = toReturn + "dropped out" + ": ";
                    }
                    toReturn = toReturn + timeFormat.format(getRaceTimes().getDroppedOutTime());
                }
                break;
            case DIDNOTSTART:
                if (getRaceTimes().getNotStartedTime() != null) {
                    if (getReportedItems().getReportedItem(TimeTypes.DIDNOTSTART)) {
                        toReturn = toReturn + "(" + "did not start" + "): ";
                    } else {
                        toReturn = toReturn + "did not start" + ": ";
                    }
                    toReturn = toReturn + timeFormat.format(getRaceTimes().getNotStartedTime());
                }
        }

        return toReturn;
    }

    //TODO Java doc this
    @Override
    public int describeContents() {
        return 0;
    }

    //TODO Java doc this
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(raceTimes, i);
        parcel.writeParcelable(reportedItems, i);
    }

    //TODO Java doc this
    protected ReportedRaceTimes(Parcel in) {
        raceTimes = in.readParcelable(RaceTimes.class.getClassLoader());
        reportedItems = in.readParcelable(ReportedItems.class.getClassLoader());
    }

    //TODO Java doc this
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
}
