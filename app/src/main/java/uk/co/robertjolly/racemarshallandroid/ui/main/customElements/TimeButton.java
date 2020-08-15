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

//TODO Java doc this
@SuppressLint("AppCompatCustomView")
public class TimeButton extends Button {
    private Date timeSelected;
    private boolean timeOverridden = false;
    private boolean paused = false;


    //TODO Java doc this
    public TimeButton(Context context) {
        super(context);
        startTimer();
        setListener();
    }

    //TODO Java doc this
    public TimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        startTimer();
        setListener();
    }


    //TODO Java doc this
    private Date getTimeSelected() {
        return timeSelected;
    }

    //TODO Java doc this
    public void setTimeSelected(Date timeSelected) {
        this.timeSelected = timeSelected;
        stopTimer();
        updateText();
    }

    //TODO Java doc this
    private boolean isPaused() {
        return paused;
    }

    //TODO Java doc this
    private void setPaused(boolean paused) {
        this.paused = paused;
    }

    //TODO Java doc this
    public Date getTime() {
        if (isTimeOverriden()) {
            return timeSelected;
        } else {
            return Calendar.getInstance().getTime();
        }
    }

    //TODO Java doc this
    public boolean isTimeOverriden() {
        return timeOverridden;
    }

    //TODO Java doc this
    private void setTimeOverriden(boolean timeOverriden) {
        this.timeOverridden = timeOverriden;
    }

    //TODO Java doc this
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

    //TODO Java doc this
    private void stopTimer() {
        setTimeOverriden(true);
    }

    //TODO Java doc this
    private void resetTimeSelected() {
        this.timeSelected = null;
        setTimeOverriden(false);
        startTimer();
    }

    //TODO Java doc this
    private void updateText() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if (isTimeOverriden() && !isPaused()) {
            setText(timeFormat.format(getTimeSelected()));
        } else if (!isPaused()){
            setText(timeFormat.format(Calendar.getInstance().getTime()));
        }
    }

    //TODO Java doc this
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
