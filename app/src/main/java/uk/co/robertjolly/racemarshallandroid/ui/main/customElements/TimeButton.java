package uk.co.robertjolly.racemarshallandroid.ui.main.customElements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;;


import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("AppCompatCustomView")
public class TimeButton extends Button {
    private Date timeSelected;
    private boolean timeOverridden = false;
    private boolean paused = false;

    //constructors
    public TimeButton(Context context) {
        super(context);
        startTimer();
        setListener();
    }

    public TimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        startTimer();
        setListener();
    }

    //getters + setters
    private Date getTimeSelected() {
        return timeSelected;
    }

    private void setTimeSelected(Date timeSelected) {
        this.timeSelected = timeSelected;
        stopTimer();
        updateText();
    }

    private boolean isPaused() {
        return paused;
    }

    private void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Date getTime() {
        if (isTimeOverriden()) {
            return timeSelected;
        } else {
            return Calendar.getInstance().getTime();
        }
    }

    private boolean isTimeOverriden() {
        return timeOverridden;
    }

    private void setTimeOverriden(boolean timeOverriden) {
        this.timeOverridden = timeOverriden;
    }

    //other functions
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

    private void stopTimer() {
        setTimeOverriden(true);
    }

    private void resetTimeSelected() {
        this.timeSelected = null;
        setTimeOverriden(false);
        startTimer();
    }

    private void updateText() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if (isTimeOverriden() && !isPaused()) {
            setText(timeFormat.format(getTimeSelected()));
        } else if (!isPaused()){
            setText(timeFormat.format(Calendar.getInstance().getTime()));
        }
    }

    private void setListener() {
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyTimePickerDialog timePicker = new MyTimePickerDialog(view.getContext(), new MyTimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(com.ikovac.timepickerwithseconds.TimePicker view, int hourOfDay, int minute, int seconds) {
                        Date timeSelected = new Date();
                        timeSelected.setHours(hourOfDay);
                        timeSelected.setMinutes(minute);
                        ;
                        timeSelected.setSeconds(seconds);
                        setPaused(false);
                        setTimeSelected(timeSelected);
                    }
                }, getTime().getHours(), getTime().getMinutes(), getTime().getSeconds(), true);

                timePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        setPaused(false);
                    }
                });
                setPaused(true);
                resetTimeSelected();
                timePicker.show();
                /*timePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        button.resetTimeSelected();
                    }
                });*/ //Does not work, though would be ideal - OnCancel is not run on clicking cancel button. Therefore pause feature added to mimic this.
            }
        });
    }
}
