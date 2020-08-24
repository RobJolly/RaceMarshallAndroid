package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Map;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.ActiveRacerFragment;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.CheckpointFragment;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.RacerTimesFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Fragment[] mFragmentList = getFragments();
    //private final Fragment[] mFragmentList = getFragments();
    private final Context mContext;
    private Checkpoints checkpoints;
    private SelectionsStateManager selectionsStateManager;

    /**
     * Constructor for the fragment page adapter.
     *
     * @param context
     * @param fm
     * @param checkpoints
     */
    public SectionsPagerAdapter(Context context, FragmentManager fm, Checkpoints checkpoints) {
        super(fm);
        mContext = context;
        setCheckpoints(checkpoints);
        setSelectionsStateManager(new SelectionsStateManager(getCheckpoints()));
        //mFragmentList = getFragments();
    }

    /**
     * Getter for the different 'fragments' of the adapter. Add new fragment here, if you wish to create
     * a new tab. Tabs best kept under about 4 or 5.
     * @return An array of fragments, that are wished to be in the application.
     */
    private Fragment[] getFragments() {
        Fragment[] allTabs = new Fragment[3];
        allTabs[0] = new ActiveRacerFragment();
        allTabs[1] = new RacerTimesFragment();
        allTabs[2] = new CheckpointFragment();

        //pass racer data
     //   Bundle racerData = new Bundle();
    //    racerData.putIntegerArrayList("racers", racers);
     //   allTabs[0].setArguments(racerData);
        return allTabs;
    }

    /**
     * Called to instantiate the fragment for the given position.
     * @param position The index of the fragment
     * @return Fragment at input position
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return mFragmentList[position];
    }


    /**
     * Getter for the names of the tabs.
     * @param position The index of the tab
     * @return A CharSequence, representing the name of the tab at input index.
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    /**
     *
     * @return The number of fragments in the sections page adapter.
     */
    @Override
    public int getCount() {
        return mFragmentList.length;
    }

    /**
     * Allows for the setting of the checkpoints for the pager adapter, post-construction.
     * @param passedCheckpoints Checkpoints to be used in the application.
     */
    public void setCheckpoints(Checkpoints passedCheckpoints) {
        checkpoints = passedCheckpoints;
    }

    /**
     * Returns the checkpoints from wherever they are stored.
     * @return Checkpoints used in this application.
     */
    public Checkpoints getCheckpoints() {
        return checkpoints;
    }

    //TODO Java doc this
    public SelectionsStateManager getSelectionsStateManager() {
        return selectionsStateManager;
    }

    //TODO Java doc this
    public void setSelectionsStateManager(SelectionsStateManager selectionsStateManager) {
        this.selectionsStateManager = selectionsStateManager;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);

    }
}