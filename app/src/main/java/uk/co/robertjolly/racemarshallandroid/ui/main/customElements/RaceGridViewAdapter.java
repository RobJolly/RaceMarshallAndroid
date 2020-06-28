package uk.co.robertjolly.racemarshallandroid.ui.main.customElements;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;


public class RaceGridViewAdapter extends BaseAdapter {

    private final Context mContext;
    public final ArrayList<Integer> activeRacers;

    public RaceGridViewAdapter(Context mContext, ArrayList<Integer> list) {
        this.mContext = mContext;
        this.activeRacers = list;
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


        //note, from: https://stackoverflow.com/questions/11010111/how-to-set-the-width-of-a-button-in-dp-during-runtime-in-android
        final float scale = mContext.getResources().getDisplayMetrics().density;
        thisButton.setWidth((int)(100 * scale));
        thisButton.setHeight((int)(100 * scale));

        thisButton.setText(activeRacers.get(i).toString());

        thisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeRacers.remove(i);
                notifyDataSetChanged();

            }
        });

        return thisButton;
    }
}
