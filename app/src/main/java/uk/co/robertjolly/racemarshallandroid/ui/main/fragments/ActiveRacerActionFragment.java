package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

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

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.TimeButton;

import com.ikovac.timepickerwithseconds.*; //Note - this is not mine, but an opensource project.

public class ActiveRacerActionFragment extends Fragment {
    private static final String TAG = "ActiveRacerActionFragment";
    private boolean timeChanged = false;
    private List<Integer> selectedRacers = new ArrayList<Integer>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_racers_action_fragment, container, false);
        view.setElevation(12); //doesn't work when set in XML - I'm unsure why, but I should fix this later when I know.
        //((TextView) view.findViewById(R.id.selectedRacersTextView)).setElevation(6);

        Button deselectAllButton = view.findViewById(R.id.deselectAllButton);
        deselectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSelected();
            }
        });
        startup(view);
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

    public void startup(View view) {
      //  initialiseTimeButton(view);
    }

    public void setSelectedRacers(ArrayList<Integer> passedSelectedRacers) {
        this.selectedRacers = passedSelectedRacers;

        TextView test = (TextView) this.getView().findViewById(R.id.selectedRacersTextView);
        if (selectedRacers != null && selectedRacers.size()>0) {
            test.setText(selectedRacers.toString());
        } else {
            test.setText(R.string.selectedRacersTextViewString);
        }
    }

    public void resetSelected() {
        this.selectedRacers.clear();
        TextView test = (TextView) this.getView().findViewById(R.id.selectedRacersTextView);
        test.setText(R.string.selectedRacersTextViewString);

        ((ActiveRacerDisplayFragment) getFragmentManager().findFragmentById(R.id.selectionFragment)).resetSelected();
    }
}
