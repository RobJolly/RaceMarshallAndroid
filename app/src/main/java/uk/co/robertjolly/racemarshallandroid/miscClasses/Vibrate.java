package uk.co.robertjolly.racemarshallandroid.miscClasses;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

/**
 * This is a class that i'm using to hold the 'pulse' command. If I want to do anything more
 * complicated with vibrations, I would add it to this section.
 */
public class Vibrate {

    /**
     * This creates a simple 250ms vibration, in a single pulse.
     * @param context The context of the view calling it
     */
    public void pulse(Context context) {
        try {
            final int vibrationTime = 250;
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(vibrationTime, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(vibrationTime);
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to vibrate");
        }
    }
}
