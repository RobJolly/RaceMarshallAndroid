package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;

//TODO Java doc this
public class CheckpointFragment extends Fragment implements CheckpointGrabber {

    //TODO Java doc this
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO Java doc this
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkpoints_fragment,container,false);

        setNewCheckpointButton(view);
        //TODO Fix bug whereby racer numbers are not stored in the JSON file.
        setExportJSONButton(view);
        setDeleteAllButton(view);
        setDeleteCheckpointButton(view);
        return view;
    }

    private void setDeleteAllButton(View view) {
        final Button deleteAll = view.findViewById(R.id.deleteAllCheckpoints);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder warnUser = new AlertDialog.Builder(getContext());
                String warningString = "This will delete all data. There are currently " + String.valueOf(grabCheckpoints().getNumberUnreported()) + " unreported or unpassed racers.";
                warnUser.setTitle("Warning:");
                warnUser.setCancelable(true);
                warnUser.setMessage(warningString);
                warnUser.setPositiveButton("Cancel", null);
                warnUser.setNegativeButton("Delete All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        grabCheckpoints().clearCheckpointData();
                        grabCheckpoints().notifyObservers();
                    }
                });
                AlertDialog warnUserDialog = warnUser.create();
                warnUserDialog.show();

            }
        });
    }

    private void setDeleteCheckpointButton(View view) {
        final Button deleteCheckpoint = view.findViewById(R.id.deleteCheckpointButton);
        deleteCheckpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
                dialogBuilder.setTitle("Delete Checkpoint:");
                dialogBuilder.setCancelable(true);
                final ArrayList<Integer> possibilities = grabCheckpoints().getCheckpointNumberList();
                final CharSequence[] checkpointNumberStrings = new CharSequence[possibilities.size()];
                int count = 0;
                for (int item : possibilities) {
                    checkpointNumberStrings[count] = (CharSequence) String.valueOf(item);
                    count++;
                }

                dialogBuilder.setItems(checkpointNumberStrings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder doubleCheckBuilder = new AlertDialog.Builder(getActivity());
                        doubleCheckBuilder.setTitle("Are you sure?");
                        doubleCheckBuilder.setMessage("You are about to delete checkpoint " + checkpointNumberStrings[i] + ". This cannot be reversed. There are " + String.valueOf(grabCheckpoints().getCheckpoint(possibilities.get(i)).getNumberUnreported()) + " Unreported or Unpassed racers in this checkpoint");
                        doubleCheckBuilder.setPositiveButton("Cancel", null);
                        final int selectedItem = i;
                        doubleCheckBuilder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                grabCheckpoints().deleteCheckpoint(possibilities.get(selectedItem));
                                grabCheckpoints().notifyObservers();
                            }
                        });
                        doubleCheckBuilder.setCancelable(true);
                        doubleCheckBuilder.create().show();
                    }
                });

                dialogBuilder.create().show();
            }
        });
    }

    private void setExportJSONButton(View view) {
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
    }

    private void setNewCheckpointButton(View view) {
        Button checkpointButton = view.findViewById(R.id.createCheckpointButton);
        checkpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                final View addNewCheckpointView = getLayoutInflater().inflate(R.layout.create_new_checkpoint, null);
                alertBuilder.setView(addNewCheckpointView);
                alertBuilder.setPositiveButton("Add Checkpoint", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                final AlertDialog toShow = alertBuilder.create();
                toShow.show();
                toShow.findViewById(R.id.newCheckpointNumber).requestFocus();

                //Code to bring up keyboard, and dismiss it on dialog close
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                toShow.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                Button createButtonAction = toShow.getButton(DialogInterface.BUTTON_POSITIVE);
                createButtonAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO add error checking here
                        TextView checkpointNumberTextBox = addNewCheckpointView.findViewById(R.id.newCheckpointNumber);

                        try {
                            int checkPointNumber = Integer.parseInt(checkpointNumberTextBox.getText().toString());
                            //I need to do some error checking hereCheckpoint
                            if (grabCheckpoints().hasCheckpoint(checkPointNumber)) {
                                final AlertDialog.Builder errorMessage = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);
                                errorMessage.setTitle("Error");
                                errorMessage.setCancelable(true);
                                errorMessage.setMessage("That checkpoint already exists and cannot be created.");
                                errorMessage.setPositiveButton("Okay", null);
                                errorMessage.create().show();
                            } else {
                                Checkpoint createdPoint = new Checkpoint(checkPointNumber,grabCheckpoints().getCheckpoint(grabCheckpoints().getCurrentCheckpointNumber()).getRacers().size());
                                grabCheckpoints().addCheckpoint(createdPoint);
                                grabCheckpoints().notifyObservers();
                                toShow.dismiss();
                            }
                        } catch (Exception e) {
                            final AlertDialog.Builder errorMessage = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);
                            errorMessage.setTitle("Error");
                            errorMessage.setCancelable(true);
                            errorMessage.setMessage("Invalid Checkpoint Input. The checkpoint must be a whole integer.");
                            errorMessage.setPositiveButton("Okay", null);
                            errorMessage.create().show();
                        }


                    }
                });
            }
        });
    }

    //TODO Java doc this
    @Override
    public Checkpoints grabCheckpoints() {
        return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).getCheckpoints();

    }
}
