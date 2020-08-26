package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.Date;
import java.util.Objects;

//Projects own classes.
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
                   long timeLong = savedInstanceState.getLong("timeOverriden");
                   TimeButton timeButton = view.findViewById(R.id.timeButton);
                   timeButton.setTimeSelected(new Date(timeLong));

                }
            } catch (Exception e) {
                Log.e("Error", "Can't get time from the saved Instance state -- This means the time button hasn't been set");
            }
        }

        selectionsStateManager.addObserver((observable, o) -> {
            try {
                setSelectedRacersText(view.findViewById(R.id.selectedRacersTextView));
            } catch (Exception e) {
                //TODO some kind of error here
            }
            view.findViewById(R.id.inButton).setEnabled(selectionsStateManager.areCompatibleIn());
            view.findViewById(R.id.outButton).setEnabled(selectionsStateManager.areCompatableOut());
            view.findViewById(R.id.otherActionButton).setEnabled(selectionsStateManager.areCompatableOther());
        });

        setSelectedRacersText(view.findViewById(R.id.selectedRacersTextView));
        Button deselectAllButton = view.findViewById(R.id.deselectAllButton);
        deselectAllButton.setOnClickListener(view14 -> {
            selectionsStateManager.clearSelected();
            selectionsStateManager.notifyObservers();
        });
        Button outButton = view.findViewById(R.id.outButton);
        outButton.setOnClickListener(view13 -> {
           selectionsStateManager.setSelectedPassed(((TimeButton) Objects.requireNonNull(getActivity()).findViewById(R.id.timeButton)).getTime());
           selectionsStateManager.notifyObservers();
        });

        Button inButton = view.findViewById(R.id.inButton);
        inButton.setOnClickListener(view12 -> {

            selectionsStateManager.setSelectedIn(((TimeButton) Objects.requireNonNull(getActivity()).findViewById(R.id.timeButton)).getTime());
            selectionsStateManager.notifyObservers();
        });

        Button otherActionButton = view.findViewById(R.id.otherActionButton);
        otherActionButton.setOnClickListener(view1 -> {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(R.string.otherActions);
            dialogBuilder.setCancelable(true);


            String[] options = {getResources().getString(R.string.droppedOut), getResources().getString(R.string.didNotStart), getResources().getString(R.string.cancel)};
            dialogBuilder.setItems(options, (dialogInterface, i) -> {
                switch (i) {
                    case 0:
                        selectionsStateManager.setSelectedDroppedOut(((TimeButton) Objects.requireNonNull(getActivity()).findViewById(R.id.timeButton)).getTime());
                        selectionsStateManager.notifyObservers();
                        break;
                    case 1:
                        selectionsStateManager.setSelectedNotStarted(((TimeButton) Objects.requireNonNull(getActivity()).findViewById(R.id.timeButton)).getTime());
                        selectionsStateManager.notifyObservers();
                        break;
                    default: //do nothing
                }
            });
            dialogBuilder.show();
        });

        return view;
    }

    //TODO Java doc this
    public void setSelectedRacersText(TextView textView) {
        textView.setText("");
        if (selectionsStateManager.getSelectedCount() > 0) {
            for (Racer racer : selectionsStateManager.getSelected()) {
                textView.append(racer.getRacerNumber() + ",");
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
        return Objects.requireNonNull(activity).getCheckpoints();
    }

    //TODO Javadoc this
    @Override
    public SelectionsStateManager grabSelectionManager() {
        return ((ActiveRacerFragment) Objects.requireNonNull(getParentFragment())).getSelectionsStateManager();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            TimeButton button = Objects.requireNonNull(getView()).findViewById(R.id.timeButton);
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
