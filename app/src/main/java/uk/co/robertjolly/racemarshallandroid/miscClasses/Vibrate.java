package uk.co.robertjolly.racemarshallandroid.miscClasses;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import static androidx.core.content.ContextCompat.getSystemService;

public class Vibrate {
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
