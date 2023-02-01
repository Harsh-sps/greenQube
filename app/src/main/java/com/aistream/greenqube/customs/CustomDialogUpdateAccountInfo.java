package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.AccountInfo;
import com.aistream.greenqube.R;

/**
 * Created by Jeff on 12/19/2018.
 */

public class CustomDialogUpdateAccountInfo extends Dialog implements View.OnClickListener{
    private Context mContext;
    private AccountInfo accountInfo;
    private CustomDialog_AccountMore dialog;
    private int position;
    public static final int UPDATE_PICTURE = 0;
    public static final int UPDATE_SURNAME = 1;
    public static final int UPDATE_NAME = 2;
    public static final int UPDATE_GENDER = 3;
    public static final int UPDATE_BIRTHDAY = 4;
    private final Dialog currDialog;

    private EditText ac_surname;
    private EditText ac_name;
    private RadioGroup ac_gender;

    public CustomDialogUpdateAccountInfo(@NonNull Context context, AccountInfo accountInfo,
                                         int position, CustomDialog_AccountMore dialog) {
        super(context, R.style.AppThemeDialog);
        this.accountInfo = accountInfo;
        this.dialog = dialog;
        this.position = position;
        this.currDialog = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_update_accountinfo);
        mContext = this.getContext();
        initView();
    }

    private void initView() {
        FrameLayout btn_back = (FrameLayout)findViewById(R.id.btn_back);
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        LinearLayout ll_surname = (LinearLayout)findViewById(R.id.ll_surname);
        LinearLayout ll_name = (LinearLayout)findViewById(R.id.ll_name);
        LinearLayout ll_gender = (LinearLayout)findViewById(R.id.ll_gender);
        Button btn_update = (Button)findViewById(R.id.btn_update);
        ac_surname = (EditText)findViewById(R.id.ac_surname);
        ac_name = (EditText)findViewById(R.id.ac_name);
        ac_gender = (RadioGroup)findViewById(R.id.ac_gender);

        String title = "";
        LinearLayout layout = null;
        switch (this.position) {
            case UPDATE_PICTURE:
                title = "Update Picture";
                break;
            case UPDATE_SURNAME:
                title = "Update Surname";
                layout = ll_surname;
                ac_surname.setText(accountInfo.getSurname());
                break;
            case UPDATE_NAME:
                title = "Update Name";
                layout = ll_name;
                ac_name.setText(accountInfo.getName());
                break;
            case UPDATE_GENDER:
                title = "Update Gender";
                layout = ll_gender;
                int pos = (accountInfo.getGender() == 2)? 1: 0;
                ((RadioButton)ac_gender.getChildAt(pos)).setChecked(true);
                break;
        }
        toolbar_title.setText(title);
        layout.setVisibility(View.VISIBLE);

        btn_update.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                dismiss();
                break;
            case R.id.btn_update:
                switch (this.position) {
                    case UPDATE_PICTURE:
                        break;
                    case UPDATE_SURNAME:
                        String surname = ac_surname.getText().toString();
                        if (checkValidation(surname, "Account surname")) {
                            if (!TextUtils.isEmpty(surname)) {
                                if (!surname.equals(accountInfo.getSurname())) {
                                    dialog.updateAccount(UPDATE_SURNAME, surname, currDialog);
                                } else {
                                    dismiss();
                                }
                            }
                        }
                        break;
                    case UPDATE_NAME:
                        String name = ac_name.getText().toString();
                        if (checkValidation(name, "Account name")) {
                            if (!TextUtils.isEmpty(name)) {
                                if (!name.equals(accountInfo.getName())) {
                                    dialog.updateAccount(UPDATE_NAME, name, currDialog);
                                } else {
                                    dismiss();
                                }
                            }
                        }
                        break;
                    case UPDATE_GENDER:
                        int gender = 1;
                        if (ac_gender.getChildAt(0).getId() != ac_gender.getCheckedRadioButtonId()) {
                            gender = 2;
                        }
                        if (gender != accountInfo.getGender()) {
                            dialog.updateAccount(UPDATE_GENDER, gender, currDialog);
                        } else {
                            dismiss();
                        }
                        break;
                }
                break;
        }
    }

    private boolean checkValidation(String name, String key) {
        String msg = "";
        if (!TextUtils.isEmpty(name)) {
            if (!isAlpha(name.charAt(0))) {
                msg = key+" must be begin with a character";
            } else if (name.length() < 2 || name.length() > 16){
                msg = key+"'s length not match the limitation[2-16]";
            }
        } else {
            msg = key+" must be not empty";
        }

        if (!TextUtils.isEmpty(msg)) {
            dialog.showToasApp(msg, Gravity.CENTER);
            return false;
        }
        return true;
    }

    private boolean isAlpha(char c) {
        return ((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z')));
    }
}
