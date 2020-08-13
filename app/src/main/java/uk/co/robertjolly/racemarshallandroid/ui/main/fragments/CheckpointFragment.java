package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Objects;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;

public class CheckpointFragment extends Fragment implements CheckpointGrabber {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkpoints_fragment,container,false);

        Button checkpointButton = view.findViewById(R.id.createCheckpointButton);
        checkpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setTitle("Create New Checkpoint");

                final View addNewCheckpointView = getLayoutInflater().inflate(R.layout.create_new_checkpoint, null);
                alertBuilder.setView(addNewCheckpointView);
                alertBuilder.setPositiveButton("Add Checkpoint", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO add error checking here
                        TextView checkpointNumberTextBox = addNewCheckpointView.findViewById(R.id.newCheckpointNumber);
                        int checkPointNumber = Integer.parseInt(checkpointNumberTextBox.getText().toString());

                        //I need to do some error checking hereCheckpoint
                        Checkpoint createdPoint = new Checkpoint(checkPointNumber,grabCheckpoints().getCheckpoint(grabCheckpoints().getCurrentCheckpointNumber()).getRacers().size());
                        grabCheckpoints().addCheckpoint(createdPoint);
                        grabCheckpoints().notifyObservers();
                    }
                });

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                final AlertDialog toShow = alertBuilder.create();
                toShow.show();
            }
        });

        Button exportJsonButton = view.findViewById(R.id.exportJsonButton);
        exportJsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This is code to create an android share event - In this case, the json values of all of the checkpoints are sent.
                //This means that storing all data, through email or whatever other method the user wishes to use, is possible.
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String test = new Gson().toJson(grabCheckpoints());
                sendIntent.putExtra(Intent.EXTRA_TEXT, test); //I need a to-String or JSON creator here
                sendIntent.setType("text/json");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }
        });

        return view;
    }

    @Override
    public Checkpoints grabCheckpoints() {
        return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).grabCheckpoints();

    }
}
