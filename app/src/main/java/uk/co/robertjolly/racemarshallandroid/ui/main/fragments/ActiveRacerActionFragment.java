package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.TimeButton;

import com.ikovac.timepickerwithseconds.*; //Note - this is not mine, but an opensource project.

public class ActiveRacerActionFragment extends Fragment implements CheckpointGrabber, SelectionManagerGrabber {
   // private boolean timeChanged = false;
    private SelectionsStateManager selectionsStateManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_racers_action_fragment, container, false);
        view.setElevation(12); //doesn't work when set in XML - I'm unsure why, but I should fix this later when I know.
        //((TextView) view.findViewById(R.id.selectedRacersTextView)).setElevation(6);

        selectionsStateManager = grabSelectionManager();

        Button deselectAllButton = view.findViewById(R.id.deselectAllButton);
        deselectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSelected((TextView) view.findViewById(R.id.selectedRacersTextView));
            }
        });
        Button outButton = view.findViewById(R.id.outButton);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               selectionsStateManager.setSelectedPassed(((TimeButton) getView().findViewById(R.id.timeButton)).getTime());
            }
        });

        Button inButton = view.findViewById(R.id.inButton);
        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionsStateManager.setSelectedIn(((TimeButton) getView().findViewById(R.id.timeButton)).getTime());
            }
        });

        Button otherActionButton = view.findViewById(R.id.otherActionButton);
        otherActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(R.string.otherActions);
                dialogBuilder.setCancelable(true);
                String[] options = {getResources().getString(R.string.droppedOut), getResources().getString(R.string.didNotStart), getResources().getString(R.string.cancel)};

                dialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                selectionsStateManager.setSelectedDroppedOut(((TimeButton) getView().findViewById(R.id.timeButton)).getTime());
                                break;
                            case 1:
                                selectionsStateManager.setSelectedNotStarted(((TimeButton) getView().findViewById(R.id.timeButton)).getTime());
                                break;
                            default: //do nothing
                        }
                    }
                });
                dialogBuilder.show();
            }
        });

        selectionsStateManager.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                setSelectedRacersText((TextView) getView().findViewById(R.id.selectedRacersTextView));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //    ((TimeButton) view.findViewById(R.id.timeButton)).startTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setSelectedRacersText(TextView textView) {
        textView.setText("");
        if (selectionsStateManager.getSelectedCount() > 0) {
            for (Racer racer : selectionsStateManager.getSelected()) {
                textView.append(String.valueOf(racer.getRacerNumber()) + ",");
                //TODO add a text trimming function in here, to prevent the textView from being overfilled
            }
            textView.setText(textView.getText().subSequence(0, textView.length()-1));
        } else {
            textView.setText(R.string.selectedRacersTextViewString);
        }

    }

    public void resetSelected(TextView textView) {
        selectionsStateManager.clearSelected();
    }

    @Override
    public Checkpoints grabCheckpoints() {
        return ((ActiveRacerFragment) getParentFragment()).grabCheckpoints();
    }

    @Override
    public SelectionsStateManager grabSelectionManager() {
        return ((ActiveRacerFragment) getParentFragment()).grabSelectionManager();
    }
}
