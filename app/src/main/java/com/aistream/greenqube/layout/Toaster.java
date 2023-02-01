package com.aistream.greenqube.layout;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

/**
 * Created by PhuDepTraj on 10/8/2018.
 */

public class Toaster {
    private static final int SHORT_TOAST_DURATION = 2000;

    private Toaster() {
    }

    public static void makeLongToast(final Context cont, String text, long durationInMillis) {
        final Toast t = Toast.makeText(cont, text, Toast.LENGTH_SHORT);
//        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
        new CountDownTimer(durationInMillis, 1000) {
            int count = 20;

            @Override
            public void onFinish() {
                t.cancel();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                t.show();
                count--;
                t.setText(count + " sec");
            }
        }.start();
    }
}
