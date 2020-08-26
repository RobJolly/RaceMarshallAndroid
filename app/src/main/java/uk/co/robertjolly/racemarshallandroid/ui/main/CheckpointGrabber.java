package uk.co.robertjolly.racemarshallandroid.ui.main;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;

/**
 * This is the interface for the checkpoint grabber.
 * This is designed to ensure that the grabCheckpoints() function exists for all implementing classes.
 */
public interface CheckpointGrabber {
    /**
     * This function grabs a Checkpoint from another Fragment or Activity within the app.
     * This is rather unsafe, but is required in some areas. This should be used with caution.
     * @return Checkpoint from another Fragment/Activity
     */
    Checkpoints grabCheckpoints();
}
