package uk.co.robertjolly.racemarshallandroid.ui.main.customElements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.security.keystore.StrongBoxUnavailableException;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;

//TODO Java doc this
public class checkpointFob {

    //TODO Java doc this
    public static void createCheckpointFob(View view, final Activity activity, final Checkpoints checkpoints) {
        final FloatingActionButton fobCheckpoint = (FloatingActionButton) view.findViewById(R.id.checkpointsFob);

        fobCheckpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                dialogBuilder.setTitle(R.string.selectedCheckpoints);
                dialogBuilder.setCancelable(true);

                final ArrayList<Integer> possibilities = checkpoints.getCheckpointNumberList();
                CharSequence[] checkpointNumberStrings = new CharSequence[possibilities.size()];
                int selectedIndex = 0;
                int count = 0;
                for (int item : possibilities) {
                    checkpointNumberStrings[count] = (CharSequence) String.valueOf(item);
                    if (checkpoints.getCurrentCheckpointNumber() == item) {
                        selectedIndex = count;
                    }
                    count++;
                }

                dialogBuilder.setSingleChoiceItems(checkpointNumberStrings, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkpoints.setCurrentCheckpointNumber(possibilities.get(i));
                        checkpoints.notifyObservers();
                        //grabSelectionManager().changeCheckpoint(possibilities.get(i));
                        //grabSelectionManager().notifyObservers();
                    }
                });
                dialogBuilder.show();
            }
        });

        checkpoints.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if (getShouldBeVisible(checkpoints)) {
                    fobCheckpoint.show();
                } else {
                    fobCheckpoint.hide();
                }
            }
        });

        if (getShouldBeVisible(checkpoints)) {
            fobCheckpoint.show();
        } else {
            fobCheckpoint.hide();
        }
        /*checkpoints.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                setVisibility(fobCheckpoint, checkpoints);
            }
        });*/
    }

    //TODO Java doc this
    private static boolean getShouldBeVisible(Checkpoints checkpoints) {
       if (checkpoints.getCheckpointNumberList().size() > 1) {
           return true;
       } else {
           return false;
       }
    }

}
