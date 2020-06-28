package uk.co.robertjolly.racemarshallandroid.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.ActiveRacerFragment;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.RacerTimesFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Fragment[] mFragmentList = getFragments();
    private final Context mContext;
    private ArrayList<Integer> racers;

    public SectionsPagerAdapter(Context context, FragmentManager fm, ArrayList<Integer> racers) {
        super(fm);
        mContext = context;
        this.racers = racers;
        Bundle racerData = new Bundle();
        racerData.putIntegerArrayList("racers", racers);
        mFragmentList[0].setArguments(racerData);

       // racers = passedRacers;
    }

    private Fragment[] getFragments() {
        Fragment[] allTabs = new Fragment[3];
        allTabs[0] = new ActiveRacerFragment();
        allTabs[1] = new RacerTimesFragment();
        allTabs[2] = new RacerTimesFragment();

        //pass racer data
     //   Bundle racerData = new Bundle();
    //    racerData.putIntegerArrayList("racers", racers);
     //   allTabs[0].setArguments(racerData);
        return allTabs;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return mFragmentList[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return mFragmentList.length;
    }

    public void setRacers(ArrayList<Integer> passedRacers) {
        racers = passedRacers;
    }
}