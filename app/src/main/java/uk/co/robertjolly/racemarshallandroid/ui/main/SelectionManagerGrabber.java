package uk.co.robertjolly.racemarshallandroid.ui.main;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.SelectionsStateManager;

/**
 * This is the interface for the SelectionManagerGrabbed.
 * This is designed to ensure that the grabSelectionManager() function exists for all implementing classes.
 */
public interface SelectionManagerGrabber {
    /**
     * This function grabs a SelectionsStatesManager from another Fragment or Activity within the app.
     * This is rather unsafe, but is required in some areas. This should be used with caution.
     * @return SelectionsStateManager from another Fragment/Activity
     */
    SelectionsStateManager grabSelectionManager();
}
