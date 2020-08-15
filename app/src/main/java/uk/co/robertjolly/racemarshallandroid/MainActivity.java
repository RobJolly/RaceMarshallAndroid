package uk.co.robertjolly.racemarshallandroid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.service.autofill.FillEventHistory;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;

//TODO Java doc this
public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter pagerAdapter;
    private Checkpoints checkpoints;
    //private SelectionsStateManager selected; //bad practice, but works for now.

    //TODO Java doc this
    public MainActivity() {
        initialise();
    }

    //TODO Java doc this
    public MainActivity(int contentLayoutId) {
        super(contentLayoutId);
        //initialise();
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
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //TODO Change this alert dialog to a dialog fragment - fix bug whereby if screen tilted before input, there will be a crash
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Start New race: Please provide this information:");

        if (checkpoints.getCheckpoint(-1) != null) {
            final View initialiseCheckpointView = getLayoutInflater().inflate(R.layout.initial_setup_layout, null);
            alertBuilder.setView(initialiseCheckpointView);
            alertBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkpoints.clearCheckpoints();
                    TextView checkpointNumberTextBox = initialiseCheckpointView.findViewById(R.id.numberOfCheckpoint);
                    TextView racerCountTextBox = initialiseCheckpointView.findViewById(R.id.numberOfRacers);
                    int checkPointNumber = Integer.parseInt(checkpointNumberTextBox.getText().toString());
                    int racerCount = Integer.parseInt(racerCountTextBox.getText().toString());

                    //I need to do some error checking here
                    Checkpoint createdPoint = new Checkpoint(checkPointNumber,racerCount);
                    checkpoints.addCheckpoint(createdPoint);
                    checkpoints.setCurrentCheckpointNumber(checkPointNumber);
                    checkpoints.notifyObservers();
                    //do nothing
                }
            });
            final AlertDialog toShow = alertBuilder.create();
            toShow.show();
        }

        checkpoints.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                saveData();
            }
        });
        super.onCreate(savedInstanceState);

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

        if (loadedCheckpointData == null) { //create default checkpoint data
            checkpoints.addCheckpoint(new Checkpoint(-1, 0));
            //checkpoints.addCheckpoint(new Checkpoint(2, 150));
            checkpoints.setCurrentCheckpointNumber(-1);
        } else {
            checkpoints = loadCheckpoints();
        }

        return checkpoints;
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

    public boolean saveData() {
        try {
            return getCheckpoints().writeToFile("checkpoints", this);
        } catch (Exception e) {
            return false;
        }
    }
}