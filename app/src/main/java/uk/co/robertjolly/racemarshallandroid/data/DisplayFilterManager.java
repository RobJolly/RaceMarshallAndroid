package uk.co.robertjolly.racemarshallandroid.data;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//From https://github.com/google/gson. Apache 2.0 license.
import com.google.gson.annotations.SerializedName;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.miscClasses.SaveAndLoadManager;

//TODO Implement a static array of racerDisplayFilters, so there's a consistent order - fewer bugs, less code.
//TODO Implement saving/loaded of display filters.
/**
 * This handles the filters for which racers to display or not display, on the racer interaction screen
 */
public class DisplayFilterManager extends Observable implements Parcelable, Serializable {
    @SerializedName("filterList")
    private ArrayList<RacerDisplayFilter> filterList;

    /**
     * Constructor for the DisplayFilterManager. Attempts to load from file, but if failed for any reason will default to having
     * two filters, Checked In and Unreported, selected.
     */
    public DisplayFilterManager(SaveAndLoadManager saveAndLoadManager) {
        filterList = implementRacerFilters(saveAndLoadManager);
    }

    /**
     * Gets the list of filters
     * @return Loads the filters, if saved, failing that, will return with two default filters; CheckedIn and ToPass.
     */
    public ArrayList<RacerDisplayFilter> implementRacerFilters(SaveAndLoadManager saveAndLoadManager) {
        ArrayList<RacerDisplayFilter> returningList;

        returningList = saveAndLoadManager.loadDisplayFilters();

        if (returningList == null) {
            returningList = new ArrayList<>();
            returningList.add(RacerDisplayFilter.CHECKEDIN);
            returningList.add(RacerDisplayFilter.TOPASS);
        }
        return returningList;
    }



    /**
     * @return A boolean list of whether or not filters are in the list in order: TOPASS, CHECKEDIN, CHECKEDOUT, DROPPEDOUT, DIDNOTSTART
     */
    public boolean[] getBooleanFilterList() {
        boolean[] returningArray = new boolean[]{false, false, false, false, false};
        for (RacerDisplayFilter filter : getFilterList()) {
            switch (filter) {
                case TOPASS:
                    returningArray[0] = true;
                    break;
                case CHECKEDIN:
                    returningArray[1] = true;
                    break;
                case CHECKEDOUT:
                    returningArray[2] = true;
                    break;
                case DROPPEDOUT:
                    returningArray[3] = true;
                    break;
                case DIDNOTSTART:
                    returningArray[4] = true;
                    break;
                default: //filter not expected/checked for
                    Log.w("Warning", "A filter has appeared that is not checked for. Filter is called: " + filter.name());
            }
        }
        return returningArray;
    }

    /**
     * Getter for the filter list stored in this class
     * @return An ArrayList of all stored filters
     */
    public ArrayList<RacerDisplayFilter> getFilterList() {
        return filterList;
    }

    public void setFilterList(ArrayList<RacerDisplayFilter> filterList) {
        this.filterList = filterList;
    }

    /**
     * @param resources The resources for the android application
     * @return An array of strings, naming the filters.
     */
    public String[] getFilterNames(Resources resources) {
        return new String[]{resources.getString(R.string.to_pass), resources.getString(R.string.checked_in), resources.getString(R.string.checked_out), resources.getString(R.string.dropped_out), resources.getString(R.string.did_not_start)};
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
                    if (!getFilterList().contains(RacerDisplayFilter.TOPASS))
                    getFilterList().add(RacerDisplayFilter.TOPASS);
                    setChanged();
                    break;
                case 1:
                    if (!getFilterList().contains(RacerDisplayFilter.CHECKEDIN))
                        getFilterList().add(RacerDisplayFilter.CHECKEDIN);
                        setChanged();
                    break;
                case 2:
                    if (!getFilterList().contains(RacerDisplayFilter.CHECKEDOUT)) {
                     getFilterList().add(RacerDisplayFilter.CHECKEDOUT);
                     setChanged();
                    }
                    break;
                case 3:
                    if (!getFilterList().contains(RacerDisplayFilter.DROPPEDOUT)) {
                        getFilterList().add(RacerDisplayFilter.DROPPEDOUT);
                        setChanged();
                    }
                    break;
                case 4:
                    if (!getFilterList().contains(RacerDisplayFilter.DIDNOTSTART))
                        getFilterList().add(RacerDisplayFilter.DIDNOTSTART);
                        setChanged();
                    break;
                default:
                    Log.w("Warning", "Filter attempting to be changed that is out of index. Index: " + i);
            }
        } else {
            switch (i) {
                case 0:
                    getFilterList().remove(RacerDisplayFilter.TOPASS);
                    setChanged();
                    break;
                case 1:
                    getFilterList().remove(RacerDisplayFilter.CHECKEDIN);
                    setChanged();
                    break;
                case 2:
                    getFilterList().remove(RacerDisplayFilter.CHECKEDOUT);
                    setChanged();
                    break;
                case 3:
                    getFilterList().remove(RacerDisplayFilter.DROPPEDOUT);
                    setChanged();
                    break;
                case 4:
                    getFilterList().remove(RacerDisplayFilter.DIDNOTSTART);
                    setChanged();
                    break;
                default:
                    Log.w("Warning", "Filter attempting to be changed that is out of index. Index: " + i);
            }
        }
    }

    /**
     * Constructor for class that will create based on input parcel
     * @param in Parcel from which to construct class
     */
    protected DisplayFilterManager(Parcel in) {
        String filepath = in.readString();
        int arraySize = in.readInt();
        filterList = new ArrayList<>();
        for (int i = 0; i < arraySize; i++) { //using this rather than readArray as was having errors with readArray
            filterList.add(RacerDisplayFilter.values()[in.readInt()]);
        }
    }

    /**
     * Required function to implement Parcelable
     */
    public static final Creator<DisplayFilterManager> CREATOR = new Creator<DisplayFilterManager>() {
        @Override
        public DisplayFilterManager createFromParcel(Parcel in) {
            return new DisplayFilterManager(in);
        }

        @Override
        public DisplayFilterManager[] newArray(int size) {
            return new DisplayFilterManager[size];
        }
    };

    /**
     * Required function to implement Parcelable
     * @return Always 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Function to write the data of this class to the given parcel
     * @param parcel Parcel to write to
     * @param i index/location of the parcel from which to write
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(filterList.size());
        for (RacerDisplayFilter filter : filterList) {
            parcel.writeInt(filter.ordinal()); //write the enum as ordinal (Int)
        }
    }


    /**
     * Function to write the contents of the displayFilterManager to its given filepath
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
