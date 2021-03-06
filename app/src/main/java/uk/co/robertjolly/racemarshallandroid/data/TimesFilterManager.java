package uk.co.robertjolly.racemarshallandroid.data;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerTimesFilter;
import uk.co.robertjolly.racemarshallandroid.miscClasses.SaveAndLoadManager;

/**
 * This class is designed to handle the filters for a racers reported items. E.g. if the racer has been
 * reported, or unreported.
 */
public class TimesFilterManager extends Observable implements Parcelable, Serializable {

    @SerializedName("filterList")
    ArrayList<RacerTimesFilter> filterList;

    public TimesFilterManager(SaveAndLoadManager saveAndLoadManager) {
        filterList = implementRacerFilters(saveAndLoadManager);
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
    public ArrayList<RacerTimesFilter> implementRacerFilters(SaveAndLoadManager saveAndLoadManager) {
        ArrayList<RacerTimesFilter> returningList;

        returningList = saveAndLoadManager.loadTimesFilters();

        if (returningList == null) {
            returningList = new ArrayList<>();
            returningList.add(RacerTimesFilter.REPORTED);
            returningList.add(RacerTimesFilter.UNREPORTED);
        }
        return returningList;
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

    /**
     * This determines if, for a given checkpoint, a racer should be shown, according to the filters stored
     * within the class.
     * Note: Filters are inclusive.
     * @param checkpoint The checkpoint that the racer's data is contained in
     * @param racer The racer to check if it should be shown
     * @return A boolean, indicating whether or not, a racer should be shown. True if it should.
     */
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

    /**
     * This sets the filters used for an object of this class.
     * Note: Filters are inclusive.
     * @param filterList An array list of the filters to apply.
     */
    public void setFilterList(ArrayList<RacerTimesFilter> filterList) {
        this.filterList = filterList;
    }

    /**
     * Function to write the contents of the timesFilterManager to its given filepath
     * @param context context of app
     * @return boolean,indicating whether not write was successful
     */
    public boolean writeToFile(String filepath, Context context) {
        boolean writeSuccessful = false;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filepath, Context.MODE_PRIVATE);
        } catch (Exception e) {
            Log.e("Error", "Failed to write, couldn't open the given file. Error message: " + e.getMessage());
        }

        ObjectOutputStream objectOutputStream = null;
        try {
            if (fileOutputStream != null) { //if fileOutputStream couldn't be opened, no use trying
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to write, couldn't open the object output stream. Error message: " + e.getMessage());
        }

        try {
            if (objectOutputStream != null) { //if objectOutputStream couldn't be opened, no use trying
                objectOutputStream.writeObject(this);
                writeSuccessful = true; //write has been completed, change this so it indicates so.
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to object to the object output stream. Error message: " + e.getMessage());
        }

        try {
            if (fileOutputStream != null) { //if fileOutputStream couldn't be opened, no use trying to close
                fileOutputStream.close();
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to close file output stream. Error message: " + e.getMessage());
        }

        return writeSuccessful;
    }
}
