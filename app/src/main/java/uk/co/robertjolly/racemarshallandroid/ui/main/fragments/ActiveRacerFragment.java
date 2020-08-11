package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.primitives.Chars;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.SelectionManagerGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;
import uk.co.robertjolly.racemarshallandroid.ui.main.customElements.checkpointFob;

public class ActiveRacerFragment extends Fragment implements CheckpointGrabber, SelectionManagerGrabber {
    private DisplayFilterManager displayFilterManager = new DisplayFilterManager();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        //find orientation of screen
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.active_racers_portrait_fragment,container,false);
        } else {
            view = inflater.inflate(R.layout.active_racers_sideways_fragment,container,false);
        }

        FloatingActionButton fobFilter = (FloatingActionButton) view.findViewById(R.id.filterFob);
        fobFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(R.string.FilterRacers);
                dialogBuilder.setCancelable(true);

                dialogBuilder.setMultiChoiceItems(grabDisplayFilterManager().getFilterNames(getResources()), grabDisplayFilterManager().getBooleanFilterList(), new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        grabDisplayFilterManager().changeFilter(i, b);
                        grabDisplayFilterManager().notifyObservers();
                    }
                });
                dialogBuilder.show();
            }
        });

        checkpointFob.createCheckpointFob(view, getActivity(), grabCheckpoints());

        return view;
    }

    /*public void createCheckpointFob(View view) {
        FloatingActionButton fobCheckpoint = (FloatingActionButton) view.findViewById(R.id.checkpointsFob);
        fobCheckpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(R.string.selectedCheckpoints);
                dialogBuilder.setCancelable(true);

                final ArrayList<Integer> possibilities = grabCheckpoints().getCheckpointNumberList();
                CharSequence[] checkpointNumberStrings = new CharSequence[possibilities.size()];
                int selectedIndex = 0;
                int count = 0;
                for (int item : possibilities) {
                    checkpointNumberStrings[count] = (CharSequence) String.valueOf(item);
                    if (grabSelectionManager().getCheckpointSelected() == item) {
                        selectedIndex = count;
                    }
                    count++;
                }

                dialogBuilder.setSingleChoiceItems(checkpointNumberStrings, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        grabCheckpoints().setCurrentCheckpointNumber(possibilities.get(i));
                        grabCheckpoints().notifyObservers();
                        //grabSelectionManager().changeCheckpoint(possibilities.get(i));
                        //grabSelectionManager().notifyObservers();
                    }
                });
                dialogBuilder.show();
            }
        });
    }*/

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Checkpoints grabCheckpoints() {
        return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).grabCheckpoints();
    }

    @Override
    public SelectionsStateManager grabSelectionManager() {
        return ((SectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).grabSelectionManager();
    }

    public DisplayFilterManager grabDisplayFilterManager() {
        return displayFilterManager;
    }


}
