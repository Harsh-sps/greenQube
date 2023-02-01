package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.RechargePin;
import com.aistream.greenqube.mvp.model.RechargeReponse;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.APIResultCallBack;
import com.aistream.greenqube.R;

/**
 * Created by PhuDepTraj on 3/19/2018.
 */

public class CustomDialog_Redeem extends Dialog implements View.OnClickListener {
    private FrameLayout btn_back;
    private FrameLayout main_toolbar;
    private EditText et_redeemcode;
    private Button btn_redeem;
    private TextView tv_plan_price;
    private OgleApplication ogleApplication;
    private TextView tv_balance;
    private Context mContext;
    private PresenterMainImp presenterMainImp;
    private SharedPreferences mPref;
    private Listener listener;

    public interface Listener {
        void success();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public CustomDialog_Redeem(@NonNull Context context, PresenterMainImp main) {
        super(context, R.style.AppThemeDialog);
        this.presenterMainImp = main;
        this.mContext = context;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        ogleApplication = (OgleApplication) mContext.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_redeem);

        initView();
    }

    private void initView() {
        main_toolbar = (FrameLayout) findViewById(R.id.main_toolbar);
        btn_back = (FrameLayout) findViewById(R.id.btn_back);
        et_redeemcode = (EditText) findViewById(R.id.et_redeemcode);
        btn_redeem = (Button) findViewById(R.id.btn_redeem);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_plan_price = (TextView) findViewById(R.id.tv_plan_price);

        refreshBalance();
        tv_plan_price.setText(ogleApplication.getPlanPrice());

        btn_back.setOnClickListener(this);
        btn_redeem.setOnClickListener(this);

        et_redeemcode.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        et_redeemcode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence == null || charSequence.length() == 0)
                    return;
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < charSequence.length(); i++) {
                    String s = String.valueOf(charSequence.charAt(i));
                    if (charSequence.charAt(i) == '-' || !s.matches("\\d")) {
                        continue;
                    } else {
                        stringBuilder.append(charSequence.charAt(i));
                        if ((stringBuilder.length() == 5 || stringBuilder.length() == 10 || stringBuilder.length() == 15)
                                && stringBuilder.charAt(stringBuilder.length() - 1) != '-') {
                            stringBuilder.insert(stringBuilder.length() - 1, '-');
                        }
                    }
                }

                if (!stringBuilder.toString().equals(charSequence.toString())) {
                    et_redeemcode.setText(stringBuilder.toString());
                    et_redeemcode.setSelection(stringBuilder.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 19) {
                    btn_redeem.setBackgroundResource(R.drawable.bg_btn_login);
                } else {
                    btn_redeem.setBackgroundResource(R.drawable.bg_btn_login1);
                }
            }
        });
    }

    private void refreshBalance() {
        String accountBalance = mPref.getString("accountBalance", "0");
        tv_balance.setText(accountBalance + " $");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
            case R.id.btn_redeem:
                String pinCode = getPinRedeemCode();
                if (pinCode.length() > 0) {
                    setPinRedeemCode(pinCode);
                }
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
        APICall.renewAccount(pin, new APIResultCallBack<RechargeReponse>(){
            @Override
            public void before() {
                presenterMainImp.showLoading();
            }

            @Override
            public void onSuccess(RechargeReponse body) {
                RechargePin data = body.getData();
                SharedPreferences.Editor edit = mPref.edit();
                edit.putString("accountBalance", String.valueOf(data.getBalance()));
                edit.putInt("accountExpired", 0);
                edit.putInt("availableDownloads", data.getAvailableDownloads());
                edit.commit();
                refreshBalance();
                presenterMainImp.showToasApp("Renew VIP Plan Success", Gravity.CENTER);
                dismiss();
                if (listener != null) {
                    listener.success();
                }
            }

            @Override
            public void onError(int httpCode, RechargeReponse body, Throwable t) {
                String msg = mContext.getResources().getString(R.string.error_loginfail1);
                if (body != null) {
                    Status status = body.getStatus();
                    if (status != null && !TextUtils.isEmpty(status.getMessage())) {
                        msg = status.getMessage();
                    }

                    RechargePin data = body.getData();
                    if (data != null) {
                        mPref.edit().putString("accountBalance", String.valueOf(data.getBalance())).commit();
                        refreshBalance();
                    }
                }
                presenterMainImp.showToasApp(msg, Gravity.CENTER);
            }

            @Override
            public void after() {
                presenterMainImp.hideLoading();
            }
        });
    }
}
