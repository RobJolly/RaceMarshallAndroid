package uk.co.robertjolly.racemarshallandroid.ui.main.adapters;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;
import uk.co.robertjolly.racemarshallandroid.data.TimesFilterManager;
import uk.co.robertjolly.racemarshallandroid.miscClasses.SaveAndLoadManager;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.ActiveRacerActionFragment;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.ActiveRacerFragment;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.SettingsFragment;
import uk.co.robertjolly.racemarshallandroid.ui.main.fragments.RacerTimesFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MainTabsSectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Fragment[] mFragmentList;
    private final Context mContext;
    private Checkpoints checkpoints;
    private SelectionsStateManager selectionsStateManager;
    private SaveAndLoadManager saveAndLoadManager;
    /**
     * Constructor for the fragment page adapter.
     *
     * @param context context
     * @param fm fragment manager
     * @param checkpoints checkpoints which will be sent and grabbed from all over the app.
     */
    public MainTabsSectionsPagerAdapter(Context context, FragmentManager fm, Checkpoints checkpoints, SaveAndLoadManager saveAndLoadManager) {
        super(fm); //don't have a choice about using this.
        this.saveAndLoadManager = saveAndLoadManager;
        mFragmentList = getFragments();
        mContext = context;
        setCheckpoints(checkpoints);
        setSelectionsStateManager(new SelectionsStateManager(getCheckpoints()));
    }

    /**
     * Getter for the different 'fragments' of the adapter. Add new fragment here, if you wish to create
     * a new tab. Tabs best kept under about 4 or 5.
     * @return An array of fragments, that are wished to be in the application.
     */
    private Fragment[] getFragments() {
        Fragment[] allTabs = new Fragment[3];
        int[] fragID = new int[3];
        allTabs[0] = new ActiveRacerFragment();
        fragID[0] = allTabs[0].getId();
        allTabs[1] = new RacerTimesFragment();
        fragID[1] = allTabs[1].getId();
        allTabs[2] = new SettingsFragment();
        fragID[2] = allTabs[2].getId();

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

    /**
     * This gets the selections state manager stored within this class
     * @return getter of the selections state manager stored.
     */
    public SelectionsStateManager getSelectionsStateManager() {
        return selectionsStateManager;
    }

    /**
     * This sets the selections state manager stored within this class
     * @param selectionsStateManager setter for the selections state manager stored.
     */
    public void setSelectionsStateManager(SelectionsStateManager selectionsStateManager) {
        this.selectionsStateManager = selectionsStateManager;
    }

    /**
     * This is a class required to extend FragmentPagerAdapter. It calls super.
     * @param container the container
     * @param position position to destroy
     * @param object object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    public DisplayFilterManager grabDisplayFilterManager() {
        return ((ActiveRacerFragment) mFragmentList[0]).getDisplayFilterManager();
    }

    public TimesFilterManager grabTimesFilterManager() {
        return ((RacerTimesFragment) mFragmentList[1]).getTimesFilterManager();
    }

    public SaveAndLoadManager getSaveAndLoadManager() {
        return saveAndLoadManager;
    }
}