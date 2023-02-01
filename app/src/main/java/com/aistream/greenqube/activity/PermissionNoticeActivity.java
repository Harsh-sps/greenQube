package com.aistream.greenqube.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aistream.greenqube.R;

/**
 * Created by Administrator on 9/22/2017.
 */

public class PermissionNoticeActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        setContentView(R.layout.activity_permission);

        Typeface JuraRegular = Typeface.createFromAsset(getAssets(), "fonts/Jura-Regular.ttf");
        ((TextView) findViewById(R.id.tv_notice1)).setTypeface(JuraRegular);
        ((TextView) findViewById(R.id.tv_notice2)).setTypeface(JuraRegular);

        Button nextBtn = findViewById(R.id.btn_next);
        nextBtn.setTypeface(JuraRegular);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data = new Intent();
        setResult(Activity.RESULT_CANCELED, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Permission", "PermissionNoticeActivity onDestroy");
    }
}


