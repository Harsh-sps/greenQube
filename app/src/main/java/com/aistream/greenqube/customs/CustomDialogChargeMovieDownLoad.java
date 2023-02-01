package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.R;


public class CustomDialogChargeMovieDownLoad extends Dialog implements View.OnClickListener {

    public volatile static CustomDialogChargeMovieDownLoad instance;
    public static final int TYPE_DIALOG_UPDATE = 1010;
    private TextView tv_title;
    private Button btnOk;
    private Button btnCancel;
    private int type;

    public CustomDialogChargeMovieDownLoad(Context context, int theme) {
        super(context, theme);
        initView(context, type);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }


    public static CustomDialogChargeMovieDownLoad getInstance(Context context) {
        synchronized (CustomDialogChargeMovieDownLoad.class) {
            instance = new CustomDialogChargeMovieDownLoad(context, R.style.dialog_tran);
        }
        return instance;
    }


    private void initView(Context context, int type) {
//        Typeface RobotoMe = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMedium.ttf");
//        Typeface RobotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
        View mDialogView = View.inflate(context, R.layout.custom_dialogchargemovie, null);
        LinearLayout ll_main = (LinearLayout) mDialogView.findViewById(R.id.ll_main);
        tv_title = (TextView) mDialogView.findViewById(R.id.tv_title);
        btnOk = (Button) mDialogView.findViewById(R.id.btn_yes);
        btnCancel = (Button) mDialogView.findViewById(R.id.btn_no);
        setContentView(mDialogView);
    }

    @Override
    public void onClick(View v) {

    }


    public CustomDialogChargeMovieDownLoad withTitle(CharSequence msg) {
        tv_title.setText(msg);
        return this;
    }

    public CustomDialogChargeMovieDownLoad withBtnOkText(CharSequence msg) {
        btnOk.setText(msg);
        return this;
    }

    public CustomDialogChargeMovieDownLoad withBtnCancelText(CharSequence msg) {
        if (msg == null) {
            btnCancel.setVisibility(View.GONE);
        } else {
            btnCancel.setText(msg);
            btnCancel.setVisibility(View.VISIBLE);
        }

        return this;
    }

    public CustomDialogChargeMovieDownLoad setOkClick(View.OnClickListener click) {

        btnOk.setOnClickListener(click);
        return this;
    }

    public CustomDialogChargeMovieDownLoad setNoClick(View.OnClickListener click) {
        btnCancel.setOnClickListener(click);
        return this;
    }

    public CustomDialogChargeMovieDownLoad isCancelableOnTouchOutside(boolean cancelable) {
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if(hasFocus){
//            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//            // This work only for android 4.4+
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                getWindow().getDecorView().setSystemUiVisibility(flags);
//                final View decorView = getWindow().getDecorView();
//                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//
//                    @Override
//                    public void onSystemUiVisibilityChange(int visibility) {
//                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                            decorView.setSystemUiVisibility(flags);
//                        }
//                    }
//                });
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        if(type == TYPE_DIALOG_UPDATE){
            System.exit(0);
        }else {
            super.onBackPressed();
        }
    }
}
