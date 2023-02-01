package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.AccountLogin;
import com.aistream.greenqube.mvp.model.Balance;
import com.aistream.greenqube.mvp.model.RechargePin;
import com.aistream.greenqube.mvp.model.RechargeReponse;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.APIResultCallBack;
import com.aistream.greenqube.R;
import com.aistream.greenqube.mvp.view.ViewAccount;
import com.aistream.greenqube.util.DialogCallBack;
import com.aistream.greenqube.util.OgleHelper;

import retrofit2.Response;

/**
 * Created by PhuDepTraj on 3/19/2018.
 */

public class CustomDialog_AccountBalance extends Dialog implements View.OnClickListener, KeyBoardLayout.OnNumberClickListener {
    private FrameLayout btn_back;
    private FrameLayout main_toolbar;
    private EditText et_redeemcode;
    private OgleApplication ogleApplication;
    private TextView tv_balance;
    private Context mContext;
    private ViewAccount viewAccount;
    private PresenterMainImp presenterMainImp;
    private SharedPreferences mPref;

    public CustomDialog_AccountBalance(@NonNull Context context, ViewAccount viewAccount, PresenterMainImp main) {
        super(context, R.style.AppThemeDialog);
        this.viewAccount = viewAccount;
        this.presenterMainImp = main;
        this.mContext = context;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        ogleApplication = (OgleApplication) mContext.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_accountbalance);

        //show status bar
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);

        //show status bar
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);

        initView();
    }

    private void initView() {
        main_toolbar = (FrameLayout) findViewById(R.id.main_toolbar);
        btn_back = (FrameLayout) findViewById(R.id.btn_back);
        et_redeemcode = (EditText) findViewById(R.id.et_redeemcode);
        tv_balance = (TextView) findViewById(R.id.tv_balance);

        btn_back.setVisibility(View.VISIBLE);
        tv_balance.setText(String.format(mContext.getString(R.string.balance_info),
                "$"+mPref.getString("accountBalance", "0")));

        et_redeemcode.setInputType(InputType.TYPE_NULL);
        btn_back.setOnClickListener(this);
        ((KeyBoardLayout) findViewById(R.id.keyboard_recharge)).setOnNumberClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
        }
    }

    private String getPinRedeemCode() {
        String pinCode = et_redeemcode.getText().toString();
        if (!TextUtils.isEmpty(pinCode)) {
            return pinCode.replaceAll("-", "");
        }
        return pinCode;
    }

    private void setPinRedeemCode(final String pin) {
        final AccountLogin accountLogin = ogleApplication.getAccountLogin();
        APICall.getRechargePin(pin, new APIResultCallBack<RechargeReponse>(){
            @Override
            public void before() {
                presenterMainImp.showLoading();
            }

            @Override
            public void onSuccess(RechargeReponse body, Response response) {
                RechargePin data = body.getData();
                accountLogin.addVoucherCode(pin);
                accountLogin.setAccountEncInfo(data.getAccountEncInfo());
                ogleApplication.saveAccountInfo();

                //generate recharge records
                Balance balance = new Balance();
                balance.setDownloadDate(OgleHelper.getFormatDate(System.currentTimeMillis()));
                balance.setName("Voucher Recharge");
                balance.setAmount(data.getAmount());
                balance.setVoucherCode(pin);
                ogleApplication.savePurchaseHistory(balance);

                SharedPreferences.Editor editor = mPref.edit();
                editor.putString("accountBalance", data.getBalance());
                editor.putInt("accountExpired", 0);
                editor.putInt("availableDownloads", data.getAvailableDownloads());
                editor.commit();

                //refresh account balance
                if (viewAccount != null)
                    viewAccount.refreshAccount();

                presenterMainImp.showToasApp("Top Up Success", Gravity.CENTER);
                dismiss();
            }

            @Override
            public void onError(int httpCode, RechargeReponse body, Throwable t) {
                String msg = mContext.getResources().getString(R.string.error_loginfail1);
                int code = -1;
                if (body != null) {
                    Status status = body.getStatus();
                    if (status != null) {
                        code = status.getCode();
                        if (!TextUtils.isEmpty(status.getMessage())) {
                            msg = status.getMessage();
                        }
                    }
                }

                if (code == 45) {
                    OgleHelper.showDialog(getContext(), msg, "Verify Now", "Cancel", new DialogCallBack() {
                        @Override
                        public void ok() {
                            presenterMainImp.getViewMain().showLoginPage();
                        }

                        @Override
                        public void cancel() {
                        }
                    });
                } else {
                    OgleHelper.showMessage(mContext, msg, null);
                }
            }

            @Override
            public void after() {
                presenterMainImp.hideLoading();
            }
        });
    }

    @Override
    public void onNumberReturn(String number) {
        String oldPinCode = et_redeemcode.getText().toString();
        String newPinCode = oldPinCode;
        if (oldPinCode.length() > 0 && (oldPinCode.length() + 1) % 5 == 0) {
            newPinCode += "-";
        }
        newPinCode += number;
        try {
            et_redeemcode.setText(newPinCode);
            et_redeemcode.setSelection(newPinCode.length());
        } catch (Exception e) {
            et_redeemcode.setText(oldPinCode);
            et_redeemcode.setSelection(oldPinCode.length());
        }
    }

    @Override
    public void onNumberDelete() {
        String pinCode = et_redeemcode.getText().toString();
        int len = pinCode.length();
        if (len > 0) {
            et_redeemcode.setText(pinCode.substring(0, len - 1));
        }
        et_redeemcode.setSelection(len > 1? (len - 1) : 0);
    }

    @Override
    public void onNumberEnter() {
        String pinRedeemCode = getPinRedeemCode();
        if (pinRedeemCode.length() >= 16) {
            setPinRedeemCode(pinRedeemCode);
        } else {
            presenterMainImp.getViewMain().showToasApp("Vourcher PIN must be not less than 16 digits", Gravity.CENTER);
        }
    }
}
