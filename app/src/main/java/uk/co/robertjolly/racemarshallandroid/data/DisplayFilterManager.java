package uk.co.robertjolly.racemarshallandroid.data;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Observable;

import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.enums.RacerDisplayFilter;

//TODO Implement a static array of racerDisplayFilters, so there's a consistent order - fewer bugs, less code.
public class DisplayFilterManager extends Observable {
    private ArrayList<RacerDisplayFilter> filterList = implementRacerFilters();

    public ArrayList<RacerDisplayFilter> implementRacerFilters() {
        ArrayList<RacerDisplayFilter> returningList;

        returningList = loadFilters();

        if (returningList == null) {
            returningList = new ArrayList<>();
            returningList.add(RacerDisplayFilter.CHECKEDIN);
            returningList.add(RacerDisplayFilter.TOPASS);
        }
        return returningList;
    }

    public ArrayList<RacerDisplayFilter> loadFilters() {
        return null;
    }

    public boolean[] getBooleanFilterList() {
        boolean[] returningArray = new boolean[]{false, false, false, false, false};
        for (RacerDisplayFilter filter : getFilterList()) {
            switch (filter) {
                case TOPASS:
                    returningArray[0] = true;
                    break;
                case CHECKEDIN:
                    returningArray[1] = true;
                    break;
                case CHECKEDOUT:
                    returningArray[2] = true;
                    break;
                case DROPPEDOUT:
                    returningArray[3] = true;
                    break;
                case DIDNOTSTART:
                    returningArray[4] = true;
                    break;
                default: //do nothing, filter not expected.
            }
        }
        return returningArray;
    }

    public ArrayList<RacerDisplayFilter> getFilterList() {
        return filterList;
    }

    public String[] getFilterNames(Resources resources) {
        return new String[]{resources.getString(R.string.toPass), resources.getString(R.string.checkedIn), resources.getString(R.string.checkedOut), resources.getString(R.string.droppedOut), resources.getString(R.string.didNotStart)};
    }

    public void changeFilter(int i, boolean b) {
        if (b) {
            switch (i) {
                case 0:
                    if (!getFilterList().contains(RacerDisplayFilter.TOPASS))
                    getFilterList().add(RacerDisplayFilter.TOPASS);
                    setChanged();
                    break;
                case 1:
                    if (!getFilterList().contains(RacerDisplayFilter.CHECKEDIN))
                        getFilterList().add(RacerDisplayFilter.CHECKEDIN);
                        setChanged();
                    break;
                case 2:
                    if (!getFilterList().contains(RacerDisplayFilter.CHECKEDOUT)) {
                     getFilterList().add(RacerDisplayFilter.CHECKEDOUT);
                     setChanged();
                    }
                    break;
                case 3:
                    if (!getFilterList().contains(RacerDisplayFilter.DROPPEDOUT)) {
                        getFilterList().add(RacerDisplayFilter.DROPPEDOUT);
                        setChanged();
                    }
                    break;
                case 4:
                    if (!getFilterList().contains(RacerDisplayFilter.DIDNOTSTART))
                        getFilterList().add(RacerDisplayFilter.DIDNOTSTART);
                        setChanged();
                    break;
                default: //do nothing
            }
        } else {
            switch (i) {
                case 0:
                    getFilterList().remove(RacerDisplayFilter.TOPASS);
                    setChanged();
                    break;
                case 1:
                    getFilterList().remove(RacerDisplayFilter.CHECKEDIN);
                    setChanged();
                    break;
                case 2:
                    getFilterList().remove(RacerDisplayFilter.CHECKEDOUT);
                    setChanged();
                    break;
                case 3:
                    getFilterList().remove(RacerDisplayFilter.DROPPEDOUT);
                    setChanged();
                    break;
                case 4:
                    getFilterList().remove(RacerDisplayFilter.DIDNOTSTART);
                    setChanged();
                    break;
                default: //do nothing
            }
        }
    }
}
