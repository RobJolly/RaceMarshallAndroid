package uk.co.robertjolly.racemarshallandroid.data;

import uk.co.robertjolly.racemarshallandroid.data.enums.TimeTypes;

public class ReportedItems {

    boolean inReported;
    boolean outReported;
    boolean droppedOutReported;
    boolean didNotStartReported;

    public void setReportedItem(TimeTypes toReport, boolean isReported) {
        switch (toReport) {
            case IN :
                inReported = isReported;
                break;
            case OUT :
                outReported = isReported;
                break;
            case DROPPEDOUT:
                droppedOutReported = isReported;
                break;
            case DIDNOTSTART:
                didNotStartReported = isReported;
                break;
            default:
                //TODO: ADD ERROR HERE
        }
    }

    public boolean getReportedItem(TimeTypes toReport) {
        switch (toReport) {
            case IN :
                return inReported;
            case OUT :
                return outReported;
            case DROPPEDOUT:
                return droppedOutReported;
            case DIDNOTSTART:
                return didNotStartReported;
            default:
                //TODO: ADD ERROR HERE
                return false;
        }
    }
}
