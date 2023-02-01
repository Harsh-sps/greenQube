package com.aistream.greenqube.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.R;

public class Toaster {

    private static final int SHORT_TOAST_DURATION = 2000;

    private Toaster() {}

    private static CountDownTimer countDownTimer;
    private static Toast toast;

    public static void makeLongToast(String text, int gravity, long durationInMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            toast.cancel();
        }
        toast = Toast.makeText(OgleApplication.getInstance(), text, Toast.LENGTH_SHORT);
        toast.setGravity(gravity | Gravity.CENTER_HORIZONTAL, 0, 0);
        centerText(OgleApplication.getInstance(), toast.getView());

        final Toast t = toast;
        countDownTimer = new CountDownTimer(Math.max(durationInMillis - SHORT_TOAST_DURATION, 1000), 1000) {
            @Override
            public void onFinish() {
                t.show();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                t.show();
            }
        }.start();
    }

    private static void centerText(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setGravity(Gravity.CENTER);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.size14));
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int n = group.getChildCount();
            for (int i = 0; i < n; i++) {
                centerText(context, group.getChildAt(i));
            }
        }
    }
}