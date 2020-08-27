package uk.co.robertjolly.racemarshallandroid.data;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerTimesFilter;

public class TimesFilterManager extends Observable implements Parcelable, Serializable {

    @SerializedName("filterList")
    ArrayList<RacerTimesFilter> filterList;

    public TimesFilterManager() {
        filterList = implementRacerFilters();
    }

    protected TimesFilterManager(Parcel in) {
        int arraySize = in.readInt();
        filterList = new ArrayList<>();
        for (int i = 0; i < arraySize; i++) { //using this rather than readArray as was having errors with readArray
            filterList.add(RacerTimesFilter.values()[in.readInt()]);
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(filterList.size());
        for (RacerTimesFilter filter : filterList) {
            parcel.writeInt(filter.ordinal()); //write the enum as ordinal (Int)
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimesFilterManager> CREATOR = new Creator<TimesFilterManager>() {
        @Override
        public TimesFilterManager createFromParcel(Parcel in) {

            return new TimesFilterManager(in);
        }

        @Override
        public TimesFilterManager[] newArray(int size) {
            return new TimesFilterManager[size];
        }
    };

    /**
     * Gets the list of filters
     * @return Loads the filters, if saved, failing that, will return with two default filters; Reported and Unreported.
     */
    public ArrayList<RacerTimesFilter> implementRacerFilters() {
        ArrayList<RacerTimesFilter> returningList;

        returningList = loadFilters();

        if (returningList == null) {
            returningList = new ArrayList<>();
            returningList.add(RacerTimesFilter.REPORTED);
            returningList.add(RacerTimesFilter.UNREPORTED);
        }
        return returningList;
    }

    public ArrayList<RacerTimesFilter> loadFilters() {
        return null;
    }

    /**
     * @return A boolean list of whether or not filters are in the list in order: REPORTED, DROPPEDOUT
     */
    public boolean[] getBooleanFilterList() {
        boolean[] returningArray = new boolean[]{false, false};
        for (RacerTimesFilter filter : getFilterList()) {
            switch (filter) {
                case REPORTED:
                    returningArray[0] = true;
                    break;
                case UNREPORTED:
                    returningArray[1] = true;
                    break;
                default: //filter not expected/checked for
                    Log.w("Warning", "A filter has appeared that is not checked for. Filter is called: " + filter.name());
            }
        }
        return returningArray;
    }

    public ArrayList<RacerTimesFilter> getFilterList() {
        return filterList;
    }

    /**
     * @param resources The resources for the android application
     * @return An array of strings, naming the filters.
     */
    public String[] getFilterNames(Resources resources) {
        return new String[]{resources.getString(R.string.reported), resources.getString(R.string.unreported)};
    }

    /**
     * Allows the changing of the filter in position i, to be there or not, based on boolean b.
     * @param i filter at position i
     * @param b should the filter be active or not
     */
    public void changeFilter(int i, boolean b) {
        if (b) {
            switch (i) {
                case 0:
                    if (!getFilterList().contains(RacerTimesFilter.REPORTED))
                        getFilterList().add(RacerTimesFilter.REPORTED);
                    setChanged();
                    break;
                case 1:
                    if (!getFilterList().contains(RacerTimesFilter.UNREPORTED))
                        getFilterList().add(RacerTimesFilter.UNREPORTED);
                    setChanged();
                    break;
                default:
                    Log.w("Warning", "Filter attempting to be changed that is out of index. Index: " + i);
            }
        } else {
            switch (i) {
                case 0:
                    getFilterList().remove(RacerTimesFilter.REPORTED);
                    setChanged();
                    break;
                case 1:
                    getFilterList().remove(RacerTimesFilter.UNREPORTED);
                    setChanged();
                    break;
                default:
                    Log.w("Warning", "Filter attempting to be changed that is out of index. Index: " + i);
            }
        }
    }

    public boolean shouldShow(Checkpoint checkpoint, Racer racer) {
        boolean shouldShow = false;
        for (RacerTimesFilter filter : filterList) {
            if (shouldShow) {
                break;
            }

            switch (filter) {
                case REPORTED:
                    if (checkpoint.getRacerData(racer).allReported()) {
                        shouldShow = true;
                        break;
                    }
                    break;
                case UNREPORTED:
                    if (!checkpoint.getRacerData(racer).allReported()) {
                        shouldShow = true;
                        break;
                    }
                    break;
                default:
                    Log.e("ERROR", "Attempting to find logic for RacerTimesFilter that is not checked for. Filter is: " + filter.name());
            }
        }
        return shouldShow;
    }

}
