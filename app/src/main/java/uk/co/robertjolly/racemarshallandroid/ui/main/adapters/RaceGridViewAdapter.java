package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.ActiveRacerDisplayFragment;


public class RaceGridViewAdapter extends BaseAdapter {

    private final Context mContext;
    public final ArrayList<Integer> activeRacers;
    private HashMap<Integer, Integer> selected = new HashMap<>();
    private final ActiveRacerDisplayFragment mParent;

    public RaceGridViewAdapter(Context mContext, ActiveRacerDisplayFragment parentFragment, ArrayList<Integer> list) {
        this.mContext = mContext;
        this.activeRacers = list;
        this.mParent = parentFragment;
    }

    @Override
    public int getCount() {
        return activeRacers.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Button thisButton = new Button(mContext);
        //note, button size setting from: https://stackoverflow.com/questions/11010111/how-to-set-the-width-of-a-button-in-dp-during-runtime-in-android
        final float scale = mContext.getResources().getDisplayMetrics().density;
        thisButton.setWidth((int)(100 * scale));
        thisButton.setHeight((int)(100 * scale));

        if (selected.get(activeRacers.get(i)) != null) {
            thisButton.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        }

        thisButton.setText(activeRacers.get(i).toString());

        thisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected.get(activeRacers.get(i)) != null) {
                    selected.remove(activeRacers.get(i));
                    mParent.passSelected(new ArrayList<>(selected.values()));
                    thisButton.getBackground().clearColorFilter();
                } else {
                    selected.put(activeRacers.get(i), i);
               //     passSelected(view);
                    thisButton.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    mParent.passSelected(new ArrayList<>(selected.values()));

                }


            }
        });

        return thisButton;
    }

    private void passSelected(View view) {
     //   ((TextView) (view.findViewById(R.id.selectedRacersTextView))).setText(selected.toString());
    }

    public void resetSelected() {
        selected.clear();
        notifyDataSetChanged();
    }

}
