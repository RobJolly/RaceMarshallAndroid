package uk.co.robertjolly.racemarshallandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

//Documentation: https://developer.android.com/reference/com/google/android/material/tabs/TabLayout
//Android material components, https://github.com/material-components/material-components-android, Apache 2.0 License.
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;

//TODO Java doc this
public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter pagerAdapter;
    private Checkpoints checkpoints;
    private boolean showingDialog = false;
    private ArrayList<BroadcastReceiver> registeredReceivers = new ArrayList<>();
    private Observer askDialog;

    //private SelectionsStateManager selected; //bad practice, but works for now.
    //TODO Java doc this
    public MainActivity() {
        initialise();
    }

    //TODO Java doc this
    public MainActivity(int contentLayoutId) {
        super(contentLayoutId);
        initialise();
    }

    //TODO Java doc this
    private void initialise() {
        checkpoints = createRaceData();
        pagerAdapter = createPagerAdapter();
    }

    //TODO Java doc this
    private SectionsPagerAdapter createPagerAdapter() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getCheckpoints());
         return adapter;
    }

    //TODO Java doc this
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
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount()); //bad practice, but a ton of effort to work around
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        if (!checkpoints.hasCheckpoints()) {
            //TODO Change this alert dialog to a dialog fragment - fix bug whereby if screen tilted before input, there will be a crash
            askForCheckpointsDialog(this);
        }

        final Context context = this;
        askDialog = new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                saveData();

                if (!checkpoints.hasCheckpoints()) {
                    if (!showingDialog) {
                        askForCheckpointsDialog(context);
                    }
                } else if (!checkpoints.getCheckpointNumberList().contains(checkpoints.getCurrentCheckpointNumber())){
                    resetSelectedCheckpoint();
                }
            }
        };

        checkpoints.addObserver(askDialog);
        super.onCreate(savedInstanceState);

    }

    private void askForCheckpointsDialog(final Context context) {
        //TODO Change this alert dialog to a dialog fragment - fix bug whereby if screen tilted before input, there will be a crash
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Start New race: Please provide this information:");
        final View initialiseCheckpointView = getLayoutInflater().inflate(R.layout.initial_setup_layout, null);
        alertBuilder.setView(initialiseCheckpointView);
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        final AlertDialog toShow = alertBuilder.create();
        toShow.show();
        //Code to select first box and bring up keyboard - and dismiss keyboard on close.
        toShow.findViewById(R.id.numberOfRacers).requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        toShow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("checkpoints", getCheckpoints());
    }

    //TODO Java doc this
    public Checkpoints getCheckpoints() {
        return checkpoints;
    }

    //TODO Java doc this
    private Checkpoints createRaceData() {
        Checkpoints loadedCheckpointData = loadCheckpoints();
        Checkpoints checkpoints = new Checkpoints();

        if (loadedCheckpointData != null) { //create default checkpoint data
            return loadedCheckpointData;
        } else {
            return new Checkpoints();
        }

     //   return checkpoints;
    }

    //TODO Java doc this
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

    public void setCheckpoints(Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
    }

    //TODO Javadoc this
    public boolean saveData() {
        try {
            return getCheckpoints().writeToFile("checkpoints", this);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceivers();
       super.onDestroy();
    }

    public void unregisterReceivers() {

    }

    public void addReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        registeredReceivers.add(broadcastReceiver);
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        checkpoints.deleteObserver(askDialog);
        super.onConfigurationChanged(newConfig);
    }
}