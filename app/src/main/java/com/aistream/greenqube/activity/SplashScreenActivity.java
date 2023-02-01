package com.aistream.greenqube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aistream.greenqube.LoginActivity;

/**
 * Created by Administrator on 9/22/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        // This work only for android 4.4+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().getDecorView().setSystemUiVisibility(flags);
//            final View decorView = getWindow().getDecorView();
//            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                        decorView.setSystemUiVisibility(flags);
//                    }
//                }
//            });
//        }
//
//        setContentView(R.layout.activity_splash);
//        ImageView imgView = findViewById(R.id.iv_splash);
//        final long t = System.currentTimeMillis();
//        FrameAnimation frameAnimation = new FrameAnimation(imgView, getRes(), 10, false);
//        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
//            @Override
//            public void onAnimationStart() {
//                Log.d(TAG, "Splash start");
//            }
//
//            @Override
//            public void onAnimationEnd() {
//                Log.d(TAG, "Splash end, use time: "+(System.currentTimeMillis() - t)+"ms");
//                toLoginPage();
//            }
//
//            @Override
//            public void onAnimationRepeat() {
//                Log.d(TAG, "repeat");
//            }
//        });
    }

//    private int[] getRes() {
//        TypedArray typedArray = getResources().obtainTypedArray(R.array.splash);
//        int len = typedArray.length();
//        int[] resId = new int[len];
//        for (int i = 0; i < len; i++) {
//            resId[i] = typedArray.getResourceId(i, -1);
//        }
//        typedArray.recycle();
//        return resId;
//    }

    private void toLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
