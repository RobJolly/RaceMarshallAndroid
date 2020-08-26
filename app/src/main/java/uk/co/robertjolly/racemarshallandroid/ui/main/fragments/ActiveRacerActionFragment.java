package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.app.AlertDialog;
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

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.MainActivity;
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.TimeButton;

//TODO Java doc this
public class ActiveRacerActionFragment extends Fragment implements SelectionManagerGrabber, CheckpointGrabber{
    private SelectionsStateManager selectionsStateManager;

    //TODO Java doc this
    //TODO Add actions for savedInstanceState != null
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectionsStateManager = grabSelectionManager();

    }

    //TODO Java doc this
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.active_racers_action_fragment, container, false);
        view.setElevation(12); //doesn't work when set in XML - I'm unsure why, but I should fix this later when I know.
        if (savedInstanceState != null) {
            try {
                boolean hasTime = savedInstanceState.getBoolean("hasTime");
                if (hasTime) {
                   Long timeLong = savedInstanceState.getLong("timeOverriden");
                   TimeButton timeButton = view.findViewById(R.id.timeButton);
                   timeButton.setTimeSelected(new Date(timeLong));

                }
            } catch (Exception e) {
                //can't get lost time, will just have to abandon that.
            }
        }

        selectionsStateManager.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                try {
                    setSelectedRacersText((TextView) view.findViewById(R.id.selectedRacersTextView));
                } catch (Exception e) {
                    //TODO some kind of error here
                }
                view.findViewById(R.id.inButton).setEnabled(selectionsStateManager.areCompatableIn());
                view.findViewById(R.id.outButton).setEnabled(selectionsStateManager.areCompatableOut());
                view.findViewById(R.id.otherActionButton).setEnabled(selectionsStateManager.areCompatableOther());
            }
        });

        setSelectedRacersText((TextView) view.findViewById(R.id.selectedRacersTextView));
        Button deselectAllButton = view.findViewById(R.id.deselectAllButton);
        deselectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionsStateManager.clearSelected();
                selectionsStateManager.notifyObservers();
            }
        });
        Button outButton = view.findViewById(R.id.outButton);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               selectionsStateManager.setSelectedPassed(((TimeButton) getActivity().findViewById(R.id.timeButton)).getTime());
               selectionsStateManager.notifyObservers();
            }
        });

        Button inButton = view.findViewById(R.id.inButton);
        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TimeButton testBtn = view1.findViewById(R.id.timeButton);
                selectionsStateManager.setSelectedIn(((TimeButton) getActivity().findViewById(R.id.timeButton)).getTime());
                selectionsStateManager.notifyObservers();
            }
        });

        Button otherActionButton = view.findViewById(R.id.otherActionButton);
        otherActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(R.string.otherActions);
                dialogBuilder.setCancelable(true);
                

                String[] options = {getResources().getString(R.string.droppedOut), getResources().getString(R.string.didNotStart), getResources().getString(R.string.cancel)};
                dialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                selectionsStateManager.setSelectedDroppedOut(((TimeButton) getActivity().findViewById(R.id.timeButton)).getTime());
                                selectionsStateManager.notifyObservers();
                                break;
                            case 1:
                                selectionsStateManager.setSelectedNotStarted(((TimeButton) getActivity().findViewById(R.id.timeButton)).getTime());
                                selectionsStateManager.notifyObservers();
                                break;
                            default: //do nothing
                        }
                    }
                });
                dialogBuilder.show();
            }
        });

        return view;
    }

    //TODO Java doc this
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

    //TODO Javadoc this
    @Override
    public Checkpoints grabCheckpoints() {
        MainActivity activity = (MainActivity) getActivity();
        return activity.getCheckpoints();
    }

    //TODO Javadoc this
    @Override
    public SelectionsStateManager grabSelectionManager() {
        return ((ActiveRacerFragment) getParentFragment()).getSelectionsStateManager();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            TimeButton button = (TimeButton) getView().findViewById(R.id.timeButton);
            if (button.isTimeOverriden()) {
                outState.putBoolean("hasTime", button.isTimeOverriden());
                outState.putLong("timeOverriden", button.getTime().getTime());
            }
        } catch (Exception e) {
            //Time button just doesn't exist yet, that's fine.
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
