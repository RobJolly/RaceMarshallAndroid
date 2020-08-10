package uk.co.robertjolly.racemarshallandroid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.service.autofill.FillEventHistory;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter pagerAdapter;
    private Checkpoints checkpoints;
    private SelectionsStateManager selected; //bad practice, but works for now.

    public MainActivity() {
        initialise();
    }

    public MainActivity(int contentLayoutId) {
        super(contentLayoutId);
        //initialise();
    }

    private void initialise() {
        checkpoints = createRaceData();
        selected = createSelectionStateManager();
        pagerAdapter = createPagerAdapter();
    }

    private SectionsPagerAdapter createPagerAdapter() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getCheckpoints(), getSelected());
         return adapter;
    }

    private SelectionsStateManager createSelectionStateManager() {
        return new SelectionsStateManager(checkpoints);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        ViewPager viewPager = findViewById(R.id.mainViewPager);
   //     ((ViewPager)findViewById(R.id.mainViewPager)).setElevation(12); //Doesn't work when set in XML, unsure why but fix when I know;
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Start New race: Please provide this information:");

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

    private Checkpoints getCheckpoints() {
        return checkpoints;
    }

    //here is where I plan to load in or ask for the overall race data. For now this just defaults to 100.
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

    public SelectionsStateManager getSelected() {
        return selected;
    }

    private Checkpoints loadCheckpoints() {
        return null;
    }
}