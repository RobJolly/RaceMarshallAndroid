package uk.co.robertjolly.racemarshallandroid.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class ReportedRaceTimes implements Parcelable {
    @SerializedName("raceTimes")
    private RaceTimes raceTimes;
    @SerializedName("reportedItems")
    private ReportedItems reportedItems;

    public ReportedRaceTimes() {
        raceTimes = new RaceTimes();
        reportedItems = new ReportedItems();
    }

    public RaceTimes getRaceTimes() {
        return raceTimes;
    }

    public ReportedItems getReportedItems() {
        return reportedItems;
    }

    public boolean allReported() {
        return (inReportedIfSet() & outReportedIfSet() & droppedOutReportedIfSet() & didNotStartReportedIfSet());
    }

    public boolean inReportedIfSet() {
        if (getRaceTimes().getInTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.IN));
        } else {
            return true;
        }
    }

    public boolean outReportedIfSet() {
        if (getRaceTimes().getOutTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.OUT));
        } else {
            return true;
        }
    }

    public boolean droppedOutReportedIfSet() {
        if (getRaceTimes().getDroppedOutTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.DROPPEDOUT));
        } else {
            return true;
        }
    }

    public boolean didNotStartReportedIfSet() {
        if (getRaceTimes().getNotStartedTime() != null) {
            return (getReportedItems().getReportedItem(TimeTypes.DIDNOTSTART));
        } else {
            return true;
        }
    }

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

    public void setAllReportedToUnreported() {
        getReportedItems().setReportedItem(TimeTypes.IN, false);
        getReportedItems().setReportedItem(TimeTypes.OUT, false);
        getReportedItems().setReportedItem(TimeTypes.DROPPEDOUT, false);
        getReportedItems().setReportedItem(TimeTypes.DIDNOTSTART, false);
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(raceTimes, i);
        parcel.writeParcelable(reportedItems, i);
    }

    protected ReportedRaceTimes(Parcel in) {
        raceTimes = in.readParcelable(RaceTimes.class.getClassLoader());
        reportedItems = in.readParcelable(ReportedItems.class.getClassLoader());
    }

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
