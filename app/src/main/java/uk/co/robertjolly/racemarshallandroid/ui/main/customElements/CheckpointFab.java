package uk.co.robertjolly.racemarshallandroid.ui.main.customElements;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;

//Documentation: https://developer.android.com/reference/com/google/android/material/floatingactionbutton/FloatingActionButton
//Android material components, https://github.com/material-components/material-components-android, Apache 2.0 License.
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.ArrayList;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;

/**
 * This is the class responsible for handling the creation and setting of the checkpoints fab button.
 * This is the button that allows the changing and displaying of possible checkpoints.
 */
public class CheckpointFab {

    /**
     * This finds and sets a listener to the checkpoints floating action button, so that checkpoints can be changed
     * Contains the logic for the displaying/hiding the checkpoint.
     * @param view view which contains the checkpointsFob
     * @param activity current activity
     * @param checkpoints checkpoints which can be modified on user-interaction.
     */
    public static void createCheckpointFob(View view, final Activity activity, final Checkpoints checkpoints) {
        final FloatingActionButton fobCheckpoint = view.findViewById(R.id.checkpointsFob);

        //this displays a list of current checkpoints, and on user click, will allow the change of the active checkpoint.
        fobCheckpoint.setOnClickListener(view1 -> {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.CustomDialogTheme);
            dialogBuilder.setTitle(R.string.selected_checkpoints);
            dialogBuilder.setCancelable(true);

            final ArrayList<Integer> possibilities = checkpoints.getCheckpointNumberList();
            CharSequence[] checkpointNumberStrings = new CharSequence[possibilities.size()];
            int selectedIndex = 0;
            int count = 0;
            for (int item : possibilities) {
                checkpointNumberStrings[count] = String.valueOf(item);
                if (checkpoints.getCurrentCheckpointNumber() == item) {
                    selectedIndex = count;
                }
                count++;
            }

            dialogBuilder.setSingleChoiceItems(checkpointNumberStrings, selectedIndex, (dialogInterface, i) -> {
                checkpoints.setCurrentCheckpointNumber(possibilities.get(i));
                checkpoints.notifyObservers();
            });
            dialogBuilder.show();
        });

        //determines if the fab should be hidden or not, when checkpoints are changed (shown on >1 checkpoint)
        checkpoints.addObserver((observable, o) -> {
            if (getShouldBeVisible(checkpoints)) {
                fobCheckpoint.show();
            } else {
                fobCheckpoint.hide();
            }
        });

        if (getShouldBeVisible(checkpoints)) {
            fobCheckpoint.show();
        } else {
            fobCheckpoint.hide();
        }
    }

    /**
     * Determines whether or not the checkpoints fab should be visible.
     * It would be pointless to show when there is only one checkpoint, as the selected
     * checkpoint could not be changed.
     * @param checkpoints The checkpoints from which to count.
     * @return A boolean, indicating whether or not the fab should be visible.
     */
    private static boolean getShouldBeVisible(Checkpoints checkpoints) {
        return checkpoints.getCheckpointNumberList().size() > 1;
    }

}
