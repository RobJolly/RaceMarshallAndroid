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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.MainActivity;
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.Racer;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.miscClasses.Vibrate;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.TimeButton;

/**
 * This is the fragment that handles user input relating to what to do, to selected racers.
 * Allows the users to select racers as in, out, etc.
 */
public class ActiveRacerActionFragment extends Fragment implements SelectionManagerGrabber, CheckpointGrabber{
    private SelectionsStateManager selectionsStateManager;

    /**
     * This is the actions performed on the creation of the fragment
     * @param savedInstanceState saved data from previous instance
     */
    //TODO Add actions for savedInstanceState != null
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectionsStateManager = grabSelectionManager(); //grabs it from above. Not the best, but easy to do.

    }

    /**
     * Actions concerned with the creation of the view to be seen.
     * This adds observers, text, etc.
     * @param inflater The inflater
     * @param container Container of views
     * @param savedInstanceState Saved data from the previous fragment
     * @return View that's setup to view
     */
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
            if (selectionsStateManager.getSelected().size() > 0) {
                new Vibrate().pulse(getContext());
            }
            selectionsStateManager.clearSelected();
            selectionsStateManager.notifyObservers();
        });

        Button outButton = view.findViewById(R.id.outButton);
        outButton.setOnClickListener(view13 -> {
            if (selectionsStateManager.getSelected().size() > 0) {
                new Vibrate().pulse(getContext());
            }
           selectionsStateManager.setSelectedPassed(((TimeButton) Objects.requireNonNull(getActivity()).findViewById(R.id.timeButton)).getTime());
           selectionsStateManager.notifyObservers();
        });

        Button inButton = view.findViewById(R.id.inButton);
        inButton.setOnClickListener(view12 -> {
            if (selectionsStateManager.getSelected().size() > 0) {
                new Vibrate().pulse(getContext());
            }
            selectionsStateManager.setSelectedIn(((TimeButton) Objects.requireNonNull(getActivity()).findViewById(R.id.timeButton)).getTime());
            selectionsStateManager.notifyObservers();
        });

        Button otherActionButton = view.findViewById(R.id.otherActionButton);
        otherActionButton.setOnClickListener(view1 -> {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(R.string.other_actions);
            dialogBuilder.setCancelable(true);

            ArrayList<String> options = new ArrayList<>(); //list of option names for the user to click
            ArrayList<OtherDialogOptions> optionsTypes = new ArrayList<>(); //what the options do.

            if (selectionsStateManager.areCompatableDroppedOut()) {
                options.add(getResources().getString(R.string.dropped_out));
                optionsTypes.add(OtherDialogOptions.DROPPEDOUT);
            }

            if (selectionsStateManager.areCompatableNotStarted()) {
                options.add(getResources().getString(R.string.did_not_start));
                optionsTypes.add(OtherDialogOptions.DIDNOTSTART);
            }
            options.add(getResources().getString(R.string.cancel));
            optionsTypes.add(OtherDialogOptions.CANCEL);

            Object[] stringObjectArray = options.toArray();
            String[] stringArray = Arrays.copyOf(stringObjectArray, stringObjectArray.length, String[].class);
            dialogBuilder.setItems(stringArray, (dialogInterface, i) -> doOtherDialogActions(optionsTypes.get(i)));
            dialogBuilder.show();
        });

        return view;
    }

    /**
     * This sets the text of the given TextView. This displays which racers are selected.
     * @param textView Text view for which to set text.
     */
    private void setSelectedRacersText(TextView textView) {
        textView.setText("");
        if (selectionsStateManager.getSelectedCount() > 0) {
            for (Racer racer : selectionsStateManager.getSelected()) {
                textView.append(racer.getRacerNumber() + ",");
                //TODO add a text trimming function in here, to prevent the textView from being overfilled
            }
            textView.setText(textView.getText().subSequence(0, textView.length()-1));
        } else {
            textView.setText(R.string.selected_racers_text_view_string);
        }

    }

    /**
     * This grabs the checkpoints from the main activity.
     * @return Checkpoints, from the main activity.
     */
    @Override
    public Checkpoints grabCheckpoints() {
        MainActivity activity = (MainActivity) getActivity();
        return Objects.requireNonNull(activity).getCheckpoints();
    }

    /**
     * This grabs the SelectionsStateManager from the parent fragment.
     * @return SelectionsStateManager, from the parent fragment.
     */
    @Override
    public SelectionsStateManager grabSelectionManager() {
        return ((ActiveRacerFragment) Objects.requireNonNull(getParentFragment())).getSelectionsStateManager();
    }

    /**
     * This is run upon the saving of this state, so that it can be loaded/unload
     * @param outState Bundle in which to write data that will be loaded from later
     */
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

    private enum OtherDialogOptions {
        DROPPEDOUT, DIDNOTSTART, CANCEL
    }

    private void doOtherDialogActions(OtherDialogOptions options) {
        switch (options) {
            case DROPPEDOUT:
                if (selectionsStateManager.getSelected().size() > 0) {
                    new Vibrate().pulse(getContext());
                }
                selectionsStateManager.setSelectedDroppedOut(((TimeButton) Objects.requireNonNull(getActivity()).findViewById(R.id.timeButton)).getTime());
                selectionsStateManager.notifyObservers();
                break;
            case DIDNOTSTART:
                if (selectionsStateManager.getSelected().size() > 0) {
                    new Vibrate().pulse(getContext());
                }
                selectionsStateManager.setSelectedNotStarted(((TimeButton) Objects.requireNonNull(getActivity()).findViewById(R.id.timeButton)).getTime());
                selectionsStateManager.notifyObservers();
                break;
            case CANCEL:
                //do nothing, user exited dialog.
                break;
            default:
                Log.w("Warning", "User has clicked an action within otherActionButton that is not intended/accounted for");
                break;
        }
    }
}
