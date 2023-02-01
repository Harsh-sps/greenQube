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


public class CustomDialogNotMac extends Dialog implements View.OnClickListener {

    public volatile static CustomDialogNotMac instance;
    public static final int TYPE_DIALOG_UPDATE = 1010;
    private TextView tv_title;
    private Button btnOk;
    private int type;

    public CustomDialogNotMac(Context context, int theme) {
        super(context, theme);
        this.type = type;
        initView(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }


    public static CustomDialogNotMac getInstance(Context context) {
        synchronized (CustomDialogNotMac.class) {
            instance = new CustomDialogNotMac(context, R.style.dialog_tran);
        }
        return instance;
    }


    private void initView(Context context) {
        View mDialogView = View.inflate(context, R.layout.custom_dialog_notmac, null);
        LinearLayout ll_main = (LinearLayout) mDialogView.findViewById(R.id.ll_main);
        tv_title = (TextView) mDialogView.findViewById(R.id.tv_title);
        btnOk = (Button) mDialogView.findViewById(R.id.btn_yes);
//        if (type == 1) {
//            btnOk.setBackgroundResource(R.drawable.bg_btn_signin);
//            tv_title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.size100)));
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.size157));
//            params.leftMargin = (int) context.getResources().getDimension(R.dimen.size15);
//            params.rightMargin = (int) context.getResources().getDimension(R.dimen.size15);
//            ll_main.setLayoutParams(params);
//        } else {
//            btnOk.setBackgroundResource(R.drawable.bg_btn_yes_dialog);
//            tv_title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.size158)));
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.size215));
//            params.leftMargin = (int) context.getResources().getDimension(R.dimen.size15);
//            params.rightMargin = (int) context.getResources().getDimension(R.dimen.size15);
//            ll_main.setLayoutParams(params);
//        }
        setContentView(mDialogView);
    }

    @Override
    public void onClick(View v) {

    }


    public CustomDialogNotMac withTitle(CharSequence msg) {
        tv_title.setText(msg);
        return this;
    }

    public CustomDialogNotMac withBtnOkText(CharSequence msg) {
        btnOk.setText(msg);
        return this;
    }

    public CustomDialogNotMac setOkClick(View.OnClickListener click) {

        btnOk.setOnClickListener(click);
        return this;
    }

    public CustomDialogNotMac isCancelableOnTouchOutside(boolean cancelable) {
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
