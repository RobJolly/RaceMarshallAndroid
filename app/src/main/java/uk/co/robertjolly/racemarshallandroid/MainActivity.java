package uk.co.robertjolly.racemarshallandroid;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

//Documentation: https://developer.android.com/reference/com/google/android/material/tabs/TabLayout
//Android material components, https://github.com/material-components/material-components-android, Apache 2.0 License.
import com.google.android.material.tabs.TabLayout;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Observer;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.MainTabsSectionsPagerAdapter;

/**
 * This is the main activity. Ran upon load of the app, and handles EVERYTHING.
 */
public class MainActivity extends AppCompatActivity {

    private MainTabsSectionsPagerAdapter pagerAdapter;
    private Checkpoints checkpoints;
    private boolean showingDialog = false;
    private Observer askDialog;

    /**
     * Constructor for main activity. Initialises data.
     */
    public MainActivity() {
        initialise();
    }

    /**
     * Constructor for main activity. Initialises data.
     * @param contentLayoutId contentLayoutId
     */
    public MainActivity(int contentLayoutId) {
        super(contentLayoutId);
        initialise();
    }

    /**
     * This function initialises the checkpoints data and the pagerAdapter.
     * Checkpoints data is loaded from file if available.
     */
    private void initialise() {
        checkpoints = createRaceData();
        pagerAdapter = createPagerAdapter();
    }

    /**
     * This creates the pagerAdapter that handles the tabs of the activity.
     * @return the pagerAdapter that handles the tabs of the activity.
     */
    private MainTabsSectionsPagerAdapter createPagerAdapter() {
        return new MainTabsSectionsPagerAdapter(this, getSupportFragmentManager(), getCheckpoints());
    }

    /**
     * This is called upon the creation of the main activity and initialises it with some data.
     * This function grabs some data from the savedInstanceState if not null, to set Checkpoints.
     * @param savedInstanceState Saved data from a previous instance, if any exist.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            try {
                setCheckpoints((Checkpoints) savedInstanceState.get("checkpoints"));
            } catch (Exception e) { //something clearly gone wrong with the bundle/bad bundle input
                setCheckpoints(createRaceData());
            }
        } else {
            setCheckpoints(createRaceData());
        }

        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.mainViewPager);
        pagerAdapter = createPagerAdapter();
        viewPager.setAdapter(pagerAdapter);

        /*
        This sets the pager to have all of the tabs loaded at once. This is bad practice, but as I only have
        3 relatively small tabs, its not a massive problem. It's bad practice, but would require more time to
        work around than is worth.
         */
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount()); //bad practice, but a ton of effort to work around
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        if (!checkpoints.hasCheckpoints()) {
            //TODO This would probably be better as a dialog fragment.
            askForCheckpointsDialog(this);
        }

        final Context context = this;
        /*
        This observer handles what's done when checkpoints are empty, by displaying a box for the user to input data,
        and what's done when there's a mismatch between the selectedCheckpoint and the checkpoints that actually exist
        within the Checkpoints object.
         */
        askDialog = (observable, o) -> {
            saveData();

            if (!checkpoints.hasCheckpoints()) {
                if (!showingDialog) {
                    askForCheckpointsDialog(context);
                }
            } else if (!checkpoints.getCheckpointNumberList().contains(checkpoints.getCurrentCheckpointNumber())){
                resetSelectedCheckpoint();
            }
        };

        checkpoints.addObserver(askDialog);
        super.onCreate(savedInstanceState);

    }

    /**
     * This asks the user to provide information to create a new race.
     * @param context context
     */
    private void askForCheckpointsDialog(final Context context) {
        //TODO Change this alert dialog to a dialog fragment - fix bug whereby if screen tilted before input, there will be a crash
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Start New race: Please provide this information:");
        final View initialiseCheckpointView = getLayoutInflater().inflate(R.layout.initial_setup_layout, null);
        alertBuilder.setView(initialiseCheckpointView);
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Done", (dialogInterface, i) -> {

        });
        final AlertDialog toShow = alertBuilder.create();
        toShow.show();
        //Code to select first box and bring up keyboard - and dismiss keyboard on close.
        toShow.findViewById(R.id.numberOfRacers).requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        toShow.setOnDismissListener(dialogInterface -> {
            InputMethodManager inputMethodManager1 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager1.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });

        Button doneButton = toShow.getButton(DialogInterface.BUTTON_POSITIVE);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView checkpointNumberTextBox = initialiseCheckpointView.findViewById(R.id.numberOfCheckpoint);
                TextView racerCountTextBox = initialiseCheckpointView.findViewById(R.id.numberOfRacers);
                int checkPointNumber = 0;
                int racerCount = 0;
                try {
                    checkPointNumber = Integer.parseInt(checkpointNumberTextBox.getText().toString());
                    racerCount = Integer.parseInt(racerCountTextBox.getText().toString());
                    if (checkPointNumber > -1 && racerCount > -1) {
                        Checkpoint createdPoint = new Checkpoint(checkPointNumber, racerCount);
                        checkpoints.addCheckpoint(createdPoint);
                        checkpoints.setCurrentCheckpointNumber(checkPointNumber);
                        toShow.dismiss();
                        checkpoints.notifyObservers();
                    } else {
                        showDialogCheckpointError(context);
                    }

                } catch (Exception e) {
                    showDialogCheckpointError(context);
                }
            }

            private void showDialogCheckpointError(Context context) {
                final AlertDialog.Builder errorBuilder = new AlertDialog.Builder(context);
                errorBuilder.setTitle("Error");
                errorBuilder.setCancelable(true);
                errorBuilder.setMessage("Sorry, something is wrong with the numbers you input. Both number of racers and the checkpoint number must be positive, whole integers.");
                errorBuilder.setPositiveButton("Okay", null);
                errorBuilder.create().show();
            }
        });
        toShow.show();
    }

    /**
     * This resets the selected checkpoint to one which exists within Checkpoints.
     */
    private void resetSelectedCheckpoint() {
        checkpoints.setCurrentCheckpointNumber(checkpoints.getCheckpointNumberList().get(0));
        checkpoints.notifyObservers();
        final AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this);
        errorBuilder.setTitle("Selected Checkpoint Changed");
        errorBuilder.setCancelable(true);
        errorBuilder.setMessage("Your previously selected checkpoint no longer exists. Your selected checkpoint has been changed to checkpoint number: " + getCheckpoints().getCurrentCheckpointNumber());
        errorBuilder.setPositiveButton("Okay", null);
        errorBuilder.create().show();
    }

    /**
     * This is responsible for saving data upon the end of the instance, so that it can be loaded
     * into a new instance of this activity. Stores checkpoints.
     * @param outState The bundle in which to save data.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("checkpoints", getCheckpoints());
    }

    /**
     * This gets the checkpoints stored within this activity.
     * @return the checkpoints stored within this activity.
     */
    public Checkpoints getCheckpoints() {
        return checkpoints;
    }

    /**
     * This function attempts to create the racer data. First, by trying to load from file, and
     * then by creating a new Checkpoint if that can't be done.
     * @return Checkpoints, from file or new if none saved to file.
     */
    private Checkpoints createRaceData() {
        Checkpoints loadedCheckpointData = loadCheckpoints();

        if (loadedCheckpointData != null) { //create default checkpoint data
            return loadedCheckpointData;
        } else {
            return new Checkpoints();
        }
    }

    /**
     * This loads the checkpoints data from file if possible, returns null if not.
     * @return Checkpoints loaded from file, or null if not managed.
     */
    @Nullable
    private Checkpoints loadCheckpoints() {
        try {
            FileInputStream fileInputStream = this.openFileInput("checkpoints");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Checkpoints readInCheckpoints = (Checkpoints) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return readInCheckpoints;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * This sets the checkpoints stored within this activity to those provided.
     * @param checkpoints The checkpoints to store within this activity.
     */
    public void setCheckpoints(Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
    }

    /**
     * This saves the data to file. Returns true if successful, false if not.
     * @return Returns true if successful, false if not.
     */
    public boolean saveData() {
        try {
            return getCheckpoints().writeToFile("checkpoints", this);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This is called when configuration is changed.
     * @param newConfig The new configuration
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        checkpoints.deleteObserver(askDialog); //an observer is deleted. This prevents multiple dialogs from occurring on screen tilt.
        super.onConfigurationChanged(newConfig);
    }
}