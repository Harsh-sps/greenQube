package com.aistream.greenqube.customs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.AccountInfo;
import com.aistream.greenqube.mvp.model.AccountLogin;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.APIResultCallBack;
import com.aistream.greenqube.mvp.view.ViewAccount;
import com.aistream.greenqube.R;
import com.aistream.greenqube.util.OgleHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PhuDepTraj on 3/19/2018.
 */

public class CustomDialog_AccountMore extends Dialog implements View.OnClickListener {

    private PresenterMainImp presenterMainImp;
    private ViewAccount viewAccount;
    private FrameLayout btn_back;
    private FrameLayout main_toolbar;
    private FrameLayout frm_sur;
    private FrameLayout frm_name;
    private FrameLayout frm_gender;
    private FrameLayout frm_age;
    private FrameLayout frm_birthday;
    private TextView tv_mobinum;
    private TextView tv_email;
    private TextView tv_surname;
    private TextView tv_name;
    private TextView tv_gender;
    private TextView tv_age;
    private TextView tv_birthday;
    private AccountInfo accountInfo;
    private Context mContext;
    private DatePickerDialog dateDialog;
    private int year, monthOfYear, dayOfMonth;
    private OgleApplication ogleApplication;

    public CustomDialog_AccountMore(@NonNull Context context, ViewAccount viewAccount, PresenterMainImp presenterMainImp) {
        super(context, R.style.AppThemeDialog);
        this.viewAccount = viewAccount;
        this.accountInfo = viewAccount.getAccountInfo();
        this.presenterMainImp = presenterMainImp;
        ogleApplication = (OgleApplication) context.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_accountmore);
        mContext = this.getContext();

        //show status bar
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);

        initView();
    }

    private void initView() {
        main_toolbar = (FrameLayout) findViewById(R.id.main_toolbar);
        btn_back = (FrameLayout) findViewById(R.id.btn_back);
        tv_mobinum = (TextView) findViewById(R.id.tv_mobinum);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_surname = (TextView) findViewById(R.id.tv_surname);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);

        frm_sur = (FrameLayout)findViewById(R.id.frm_sur);
        frm_name = (FrameLayout)findViewById(R.id.frm_name);
        frm_gender = (FrameLayout)findViewById(R.id.frm_gender);
        frm_age = (FrameLayout)findViewById(R.id.frm_age);
        frm_birthday = (FrameLayout)findViewById(R.id.frm_birthday);

        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);

        if (accountInfo != null) {
            refreshAccount();
            frm_sur.setOnClickListener(this);
            frm_name.setOnClickListener(this);
            frm_gender.setOnClickListener(this);
            frm_age.setOnClickListener(this);
            frm_birthday.setOnClickListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewAccount.refreshAccount();
    }

    @Override
    public void onClick(View view) {
        CustomDialogUpdateAccountInfo dialog = null;
        switch (view.getId()) {
            case R.id.btn_back:
                dismiss();
                viewAccount.refreshAccount();
                break;
            case R.id.frm_sur:
                dialog = new CustomDialogUpdateAccountInfo(mContext, accountInfo, CustomDialogUpdateAccountInfo.UPDATE_SURNAME, this);
                dialog.show();
                break;
            case R.id.frm_name:
                dialog = new CustomDialogUpdateAccountInfo(mContext, accountInfo, CustomDialogUpdateAccountInfo.UPDATE_NAME, this);
                dialog.show();
                break;
            case R.id.frm_gender:
                dialog = new CustomDialogUpdateAccountInfo(mContext, accountInfo, CustomDialogUpdateAccountInfo.UPDATE_GENDER, this);
                dialog.show();
                break;
            case R.id.frm_age:
                break;
            case R.id.frm_birthday:
                String birthday = accountInfo.getBirthday();
                Calendar calendar = Calendar.getInstance();

                if (!TextUtils.isEmpty(birthday)) {
                    birthday = birthday.split(" ")[0];
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = format.parse(birthday);
                        calendar.setTime(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                year = calendar.get(Calendar.YEAR);
                monthOfYear = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                dateDialog = new DatePickerDialog(mContext, AlertDialog.THEME_HOLO_DARK,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                String birthday = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                boolean isUpdate = true;
                                if (!TextUtils.isEmpty(accountInfo.getBirthday())) {
                                    if (accountInfo.getBirthday().startsWith(birthday)) {
                                        isUpdate = false;
                                    }
                                }
                                if (isUpdate) {
                                    updateAccount(CustomDialogUpdateAccountInfo.UPDATE_BIRTHDAY, birthday, null);
                                }
                            }
                        }, year, monthOfYear, dayOfMonth);
                dateDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dateDialog.show();
                break;
        }
    }

    public void updateAccount(final int pos, final Object value, final Dialog dialog) {
        Map<String, Object> map = new HashMap<>();
        switch (pos) {
            case CustomDialogUpdateAccountInfo.UPDATE_PICTURE:
                break;
            case CustomDialogUpdateAccountInfo.UPDATE_SURNAME:
                map.put("surname", value);
                break;
            case CustomDialogUpdateAccountInfo.UPDATE_NAME:
                map.put("name", value);
                break;
            case CustomDialogUpdateAccountInfo.UPDATE_GENDER:
                map.put("gender", value);
                break;
            case CustomDialogUpdateAccountInfo.UPDATE_BIRTHDAY:
                map.put("birthday", value);
                break;
        }

        final SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        APICall.updateAccountInfo(map, new APIResultCallBack<Status>(){
            @Override
            public void onSuccess(Status body) {
                String v = String.valueOf(value);
                switch (pos) {
                    case CustomDialogUpdateAccountInfo.UPDATE_PICTURE:
                        break;
                    case CustomDialogUpdateAccountInfo.UPDATE_SURNAME:
                        accountInfo.setSurname(v);
                        break;
                    case CustomDialogUpdateAccountInfo.UPDATE_NAME:
                        accountInfo.setName(v);
                        break;
                    case CustomDialogUpdateAccountInfo.UPDATE_GENDER:
                        accountInfo.setGender(Integer.parseInt(v));
                        break;
                    case CustomDialogUpdateAccountInfo.UPDATE_BIRTHDAY:
                        accountInfo.setBirthday(v);
                        break;
                }
                String userName = accountInfo.getUserName();
                if (!TextUtils.isEmpty(userName)) {
                    mPref.edit().putString("userName", userName.trim()).commit();
                }
                refreshAccount();
                presenterMainImp.showToasApp("Update Success.", Gravity.CENTER);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onError(int httpCode, Status body, Throwable t) {
                String msg = mContext.getResources().getString(R.string.error_loginfail1);
                if (body != null) {
                    if (body != null && !TextUtils.isEmpty(body.getMessage())) {
                        msg = body.getMessage();
                    }
                }
                presenterMainImp.showToasApp(msg, Gravity.CENTER);
            }

            @Override
            public void after() {

            }
        });
    }

    /**
     * refresh account info
     */
    private void refreshAccount() {
        tv_mobinum.setText(accountInfo.getPhone());
        tv_email.setText(accountInfo.getEmail());
        tv_surname.setText(accountInfo.getSurname());
        tv_name.setText(accountInfo.getName());
        if (accountInfo.getGender() != null) {
            if (accountInfo.getGender() == 0) {
                tv_gender.setText("Unknown");
            } else if (accountInfo.getGender() == 1) {
                tv_gender.setText("Male");
            } else if (accountInfo.getGender() == 2) {
                tv_gender.setText("Female");
            }
        }

        if (accountInfo.getAge() != null) {
            if (accountInfo.getAge() <= 0) {
                tv_age.setText("Unknown");
            } else if (accountInfo.getAge() > 0) {
                tv_age.setText(accountInfo.getAge() + "");
            }
        }

        if (accountInfo.getBirthday() != null) {
            if (accountInfo.getBirthday().length() > 10) {
                tv_birthday.setText(accountInfo.getBirthday().substring(0, 10));
            } else {
                tv_birthday.setText(accountInfo.getBirthday());
            }
        }

        AccountLogin accountLogin = ogleApplication.getAccountLogin();
        String username = OgleHelper.nullToEmpty(accountInfo.getName()) +" "
                + OgleHelper.nullToEmpty(accountInfo.getSurname());
        accountLogin.setUserName(username.trim());
        accountLogin.setGender(accountInfo.getGender());
        accountLogin.setBirthday(accountInfo.getBirthday());
        ogleApplication.saveAccountInfo();
    }

    public void showToasApp(String msg, int gravity) {
        presenterMainImp.showToasApp(msg, gravity);
    }
}
