package uk.co.robertjolly.racemarshallandroid.ui.main.customElements;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

//From https://github.com/IvanKovac/TimePickerWithSeconds. Open Source, no pre-made license. Use and change freely, no requirement for attribution. No guarantee.
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is the time button. This is a button which will display the current time in format HH:mm:ss,
 * but can be changed/set to a specific time in the shown format when it is clicked.
 */
public class TimeButton extends androidx.appcompat.widget.AppCompatButton {
    private Date timeSelected;
    private boolean timeOverridden = false;
    private boolean paused = false;


    /**
     * Constructor for the timeButton. Starts the timer and sets the listener for the click of the button.
     * @param context context.
     */
    public TimeButton(Context context) {
        super(context);
        startTimer();
        setListener();
    }

    /**
     * Constructor for the timeButton. Starts the timer and sets the listener for the click of the button.
     * @param context context
     * @param attrs attributes
     */
    public TimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        startTimer();
        setListener();
    }


    /**
     * Gets the current time that is selected.
     * @return the time that is selected. Note the user can select HH:mm:ss on clicking timer button, not days or more. Beyond HH:mm:ss time shall be that of the day it was set.
     */
    private Date getTimeSelected() {
        return timeSelected;
    }

    /**
     * Sets the current time that is selected to that input. Stops the timer that's counting up, and updates the text to display input time.
     * @param timeSelected The time to set the button to.
     */
    public void setTimeSelected(Date timeSelected) {
        this.timeSelected = timeSelected;
        stopTimer();
        updateText();
    }

    /**
     * This is the getter for the state of the button: if it is paused or not.
     * @return Boolean indicating if button is not paused, or not.
     */
    private boolean isNotPaused() {
        return !paused;
    }

    /**
     * This sets the button to paused or not, based on input boolean.
     * @param paused boolean, set button to paused or unpaused.
     */
    private void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * This gets the current time stored in the button if it has been overriden with a time. Otherwise, it will get the current time.
     * @return the current time stored in the button if it has been overriden with a time. Otherwise, it will get the current time.
     */
    public Date getTime() {
        if (isTimeOverriden()) {
            return timeSelected;
        } else {
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * A boolean indicating whether or not the time of the button has been overriden.
     * @return whether or not the time of the button has been overriden (not actively displaying the current time)
     */
    public boolean isTimeOverriden() {
        return timeOverridden;
    }

    /**
     * Setter for the timeOverriden value stored in the class
     * @param timeOverriden The value to set the timeOverriden value to.
     */
    private void setTimeOverriden(boolean timeOverriden) {
        this.timeOverridden = timeOverriden;
    }

    /**
     * This starts the timer that counts up, second by second, and displays the current time on the
     * button.
     */
    private void startTimer() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isTimeOverriden()) {
                    updateText();
                    Handler timeHandler = new Handler();
                    timeHandler.postDelayed(this, 1000);
                }
            }
        };

        updateText();
        Handler timeHandler = new Handler();
        timeHandler.postDelayed(runnable, 1000);
    }

    /**
     * This stops the timer that is counting up and displaying the time on the button.
     * No update shall happen to the text of the button beyond this press.
     */
    //TODO Functionally this works, but the timer is still going and taking up memory. I should fix that, but it's not a priority.
    private void stopTimer() {
        setTimeOverriden(true);
    }

    /**
     * Resets the time that is selected for the button, and starts it counting up again.
     */
    private void resetTimeSelected() {
        this.timeSelected = null;
        setTimeOverriden(false);
        startTimer();
    }

    /**
     * This updates the time on the button to display the current time, or the time the button has been overriden to, if it has been set.
     */
    private void updateText() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if (isTimeOverriden() && isNotPaused()) {
            setText(timeFormat.format(getTimeSelected()));
        } else if (isNotPaused()){
            setText(timeFormat.format(Calendar.getInstance().getTime()));
        }
    }

    /**
     * This creates an OnClickListener for the button. This lets the user set the time, or cancel and make it go back to displaying the current time.
     */
    private void setListener() {
        this.setOnClickListener(view -> {
            final MyTimePickerDialog timePicker = new MyTimePickerDialog(view.getContext(), (view1, hourOfDay, minute, seconds) -> {
                Date timeSelected = new Date();
                timeSelected.setHours(hourOfDay);
                timeSelected.setMinutes(minute);

                timeSelected.setSeconds(seconds);
                setPaused(false);
                setTimeSelected(timeSelected);
            },
                    // I'm using these despite it being deprecated, as I can't find a simple alternative.
                    getTime().getHours(), getTime().getMinutes(), getTime().getSeconds(), true);

            timePicker.setOnDismissListener(dialogInterface -> setPaused(false));
            setPaused(true);
            resetTimeSelected();
            timePicker.show();

            /*
            timePicker.setOnCancelListener(dialogInterface -> button.resetTimeSelected()); //Does not work, though would be ideal - OnCancel is not run on clicking cancel button. Therefore pause feature added to mimic this behaviour..
             */
        });
    }
}
