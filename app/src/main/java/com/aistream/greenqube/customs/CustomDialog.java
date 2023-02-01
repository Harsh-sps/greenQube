package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.R;


public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    public volatile static CustomDialog instance;
    public static final int TYPE_DIALOG_UPDATE = 1010;
    private TextView tv_title;
    private Button btnOk;
    private Button btnCancel;
    private int type;

    public CustomDialog(Context context, int theme, int type) {
        super(context, theme);
        this.type = type;
        initView(context, type);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//        // This work only for android 4.4+
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().getDecorView().setSystemUiVisibility(flags);
//            final View decorView = getWindow().getDecorView();
//            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                        decorView.setSystemUiVisibility(flags);
//                    }
//                }
//            });
//        }
//
////        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
////        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
////        setContentView(R.layout.customdialog);

        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }


    public static CustomDialog getInstance(Context context, int type) {
        synchronized (CustomDialog.class) {
            instance = new CustomDialog(context, R.style.dialog_tran, type);
        }
        return instance;
    }


    private void initView(Context context, int type) {
//        Typeface RobotoMe = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMedium.ttf");
//        Typeface RobotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
        View mDialogView = View.inflate(context, R.layout.custom_dialog, null);
        LinearLayout ll_main = (LinearLayout) mDialogView.findViewById(R.id.ll_main);
        tv_title = (TextView) mDialogView.findViewById(R.id.tv_title);
        btnOk = (Button) mDialogView.findViewById(R.id.btn_yes);
        btnCancel = (Button) mDialogView.findViewById(R.id.btn_no);
        if (type == 1) {
            btnCancel.setVisibility(View.GONE);
            btnOk.setBackgroundResource(R.drawable.bg_btn_signin);
            tv_title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.size100)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.size157));
            params.leftMargin = (int) context.getResources().getDimension(R.dimen.size15);
            params.rightMargin = (int) context.getResources().getDimension(R.dimen.size15);
            ll_main.setLayoutParams(params);
        } else {
            btnCancel.setVisibility(View.VISIBLE);
//            btnOk.setBackgroundResource(R.drawable.bg_btn_yes_dialog);
            tv_title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.size158)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.size215));
            params.leftMargin = (int) context.getResources().getDimension(R.dimen.size15);
            params.rightMargin = (int) context.getResources().getDimension(R.dimen.size15);
            ll_main.setLayoutParams(params);
        }
        setContentView(mDialogView);
    }

    @Override
    public void onClick(View v) {

    }


    public CustomDialog withTitle(CharSequence msg) {
        tv_title.setText(msg);
        return this;
    }

    public CustomDialog withBtnOkText(CharSequence msg) {
        btnOk.setText(msg);
        return this;
    }

    public CustomDialog withBtnCancelText(CharSequence msg) {
        if (msg == null) {
            btnCancel.setVisibility(View.GONE);
        } else {
            btnCancel.setText(msg);
            btnCancel.setVisibility(View.VISIBLE);
        }

        return this;
    }

    public CustomDialog setOkClick(View.OnClickListener click) {

        btnOk.setOnClickListener(click);
        return this;
    }

    public CustomDialog setNoClick(View.OnClickListener click) {
        btnCancel.setOnClickListener(click);
        return this;
    }

    public CustomDialog isCancelableOnTouchOutside(boolean cancelable) {
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
