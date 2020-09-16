package uk.co.robertjolly.racemarshallandroid.miscClasses;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import uk.co.robertjolly.racemarshallandroid.MainActivity;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.TimesFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerTimesFilter;

public class SaveAndLoadManager {
    MainActivity mainActivity;

    public SaveAndLoadManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * This saves the data to file. Returns true if successful, false if not.
     */
    public void saveCheckpointData(Checkpoints checkpoints) {
        try {
            checkpoints.writeToFile("checkpoints", mainActivity);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "Failed to write checkpoint data to file");
        }
    }

    /**
     * This loads the checkpoints data from file if possible, returns null if not.
     * @return Checkpoints loaded from file, or null if not managed.
     */
    @Nullable
    public Checkpoints loadCheckpoints() {
        try {
            FileInputStream fileInputStream = mainActivity.openFileInput("checkpoints");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Checkpoints readInCheckpoints = (Checkpoints) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return readInCheckpoints;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loading function for the display filters
     * @return The stored list of filters, or null if cannot be found.
     */
    @javax.annotation.Nullable
    public ArrayList<RacerDisplayFilter> loadDisplayFilters() {
        try {
            FileInputStream fileInputStream = mainActivity.openFileInput("displayFilterManager");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            DisplayFilterManager readInFilter = (DisplayFilterManager) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return readInFilter.getFilterList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This saves the data to file. Returns true if successful, false if not.
     */
    public void saveDisplayFilterManagerData(DisplayFilterManager displayFilterManager) {
        try {
            if (!displayFilterManager.writeToFile("displayFilterManager", mainActivity)) {
                Log.e("Error", "Failed to write display filter data to file");
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to write display filter data to file");
        }
    }

    public void deleteDisplayFilterSave() {
        if (!mainActivity.getFileStreamPath("displayFilterManager").delete()) {
            Log.e("Error", "Failed to delete filter manager files");
        }
    }


    /**
     * Loading function for the time filters
     * @return The stored list of filters, or null if cannot be found.
     */
    @javax.annotation.Nullable
    public ArrayList<RacerTimesFilter> loadTimesFilters() {
        try {
            FileInputStream fileInputStream = mainActivity.openFileInput("timesFilterManager");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            TimesFilterManager readInFilter = (TimesFilterManager) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return readInFilter.getFilterList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This saves the data to file. Returns true if successful, false if not.
     */
    public void saveTimesFilterManagerData(TimesFilterManager timesFilterManager) {
        try {
            if (!timesFilterManager.writeToFile("timesFilterManager", mainActivity)) {
                Log.e("Error", "Failed to write times filter data to file");
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to write times filter data to file");
        }
    }

    public void deleteTimesFilterSave() {
        if (!mainActivity.getFileStreamPath("timesFilterManager").delete()) {
            Log.e("Error", "Failed to delete filter manager files");
        }
    }
}
