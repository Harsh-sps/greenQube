package com.aistream.greenqube.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.R;
import com.aistream.greenqube.mvp.model.AccountList;
import com.aistream.greenqube.mvp.model.AccountLogin;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.APIResultCallBack;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.util.OgleHelper;

import java.util.Map;

import retrofit2.Response;

public class LoginManager {
    private String TAG = "LoginManager";
    private OgleApplication mOgleApp;
    private SharedPreferences mPref;
    private Context mContext;

    public interface CallBack {
        void before();
        void success();
        void fail(String numPhone, String msg);
    }

    public LoginManager(Context context) {
        this.mContext = context.getApplicationContext();
        mOgleApp = (OgleApplication) context.getApplicationContext();
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * open login
     */
    public void openLogin(final CallBack callBack) {
        String pass = mPref.getString("devicePwd", "");
        AccountLogin accountLogin = mOgleApp.getAccountLogin();
        if (TextUtils.isEmpty(mOgleApp.serialDevice) || mOgleApp.serialDevice.contains("unknown")) {
            mOgleApp.serialDevice = accountLogin.getDevice();
        }
        APICall.openLogin(mOgleApp.serialDevice, pass, accountLogin.getDevice(),
                OgleApplication.playerType, mOgleApp.devicenameAndHwmodel, mOgleApp.manufacturer, mOgleApp.devicenameAndHwmodel,
                "adr", mOgleApp.osVersion, mOgleApp.appname, mOgleApp.appversion, accountLogin.getVoucherCode(),
                new APIResultCallBack<AccountList>() {
                    @Override
                    public void before() {
                        if (callBack != null) callBack.before();
                    }

                    @Override
                    public void onSuccess(AccountList body, Response response) {
                        handleLoginSuccess(response, body,null, null, callBack);
                    }

                    @Override
                    public void onError(Response response, AccountList body, Throwable t) {
                        handleLoginError(response, body, t, null, null, callBack);
                    }
                });
    }

    /**
     * otp login
     * @param numPhone
     * @param otp
     */
    public void otpLogin(final String numPhone, final String otp, final CallBack callBack) {
        numPhone.substring(0, 5);
        Log.i(TAG, "NumphoneLogin: " + numPhone);
        AccountLogin accountLogin = mOgleApp.getAccountLogin();
        APICall.signIn(Config.isEndpointPublic, numPhone, otp,
                numPhone, OgleApplication.playerType, accountLogin.getDevice(), mOgleApp.devicenameAndHwmodel, "adr",
                mOgleApp.osVersion, mOgleApp.appname, mOgleApp.appversion, accountLogin.getVoucherCode(),
                new APIResultCallBack<AccountList>(){

                    @Override
                    public void before() {
                        if (callBack != null) callBack.before();
                    }

                    @Override
                    public void onSuccess(AccountList body, Response response) {
                        Log.d(TAG, "onSuccess numPhone: "+numPhone+", otp: "+otp);
                        handleLoginSuccess(response, body, numPhone, otp, callBack);
                    }

                    @Override
                    public void onError(Response response, AccountList body, Throwable t) {
                        Log.d(TAG, "onError numPhone: "+numPhone+", otp: "+otp);
                        handleLoginError(response, body, t, numPhone, otp, callBack);
                    }
                });
    }

    /**
     * handle login success
     * @param response
     * @param numPhone
     * @param otp
     */
    private void handleLoginSuccess(Response response, AccountList body, String numPhone, String otp, CallBack callBack) {
        AccountLogin accountLogin = body.getData();
        Log.d(TAG, "handleLoginSuccess accountLogin: "+accountLogin+", numPhone: "+numPhone+", otp: "+otp);
        if (accountLogin != null) {
            SharedPreferences.Editor editor = mPref.edit();
            if (body.getStatus().getCode() == 55) {
                editor.putInt("accountExpired", 1);
            } else {
                editor.putInt("accountExpired", 0);
            }

            AccountLogin oldLoginInfo = mOgleApp.getAccountLogin();
            boolean isChange = false;
            //online login
            if (!TextUtils.isEmpty(accountLogin.getUserId())) {
                mOgleApp.flagGetDataInDay = System.currentTimeMillis() + (2 * 60 * 60 * 1000);

                editor.putLong("flagGetDataInDay", mOgleApp.flagGetDataInDay);
                if (!TextUtils.isEmpty(accountLogin.getToken())) {
                    editor.putString("stringToken", accountLogin.getToken());
                }

                if (!TextUtils.isEmpty(numPhone)) {
                    editor.putString("numberPhone", numPhone);
                    if (!OgleHelper.same(numPhone, oldLoginInfo.getPhoneNumber())) {
                        oldLoginInfo.setPhoneNumber(numPhone);
                        isChange = true;
                    }
                }

                if (!TextUtils.isEmpty(otp)) {
                    editor.putString("accountPwd", otp);
                }
                editor.putString("devicePwd", accountLogin.getPassword());
                editor.putInt("is_permanent", accountLogin.getIs_permanent());
                editor.putString("userId", accountLogin.getUserId());
            }

            //get balance
            if (OgleHelper.emptyToNull(accountLogin.getBalance()) != null) {
                editor.putString("accountBalance", accountLogin.getBalance());
            }

            if (OgleHelper.emptyToNull(accountLogin.getUserName()) != null) {
                String userName = accountLogin.getUserName();
                if (TextUtils.isEmpty(mPref.getString("userName", ""))) {
                    editor.putString("userName", userName);
                    oldLoginInfo.setUserName(userName);
                    isChange = true;
                }
            }

            //plan name
            if (!TextUtils.isEmpty(accountLogin.getPlanName())) {
                editor.putString("demoPlan", accountLogin.getPlanName());
            }

            //plan price
            if (!TextUtils.isEmpty(accountLogin.getPlanPrice())) {
                editor.putString("planPrice", accountLogin.getPlanPrice());
            }

            //max allow downloads
            if (accountLogin.getMaxDownloads() > 0) {
                editor.putInt("max_downloads", accountLogin.getMaxDownloads());
            }

            //support contact messages
            if (!accountLogin.getSupportContactMsgs().isEmpty()) {
                for (Map.Entry<String, String> entry: accountLogin.getSupportContactMsgs().entrySet()) {
                    editor.putString(entry.getKey(), entry.getValue());
                }
            }
            //available downloads
            editor.putInt("availableDownloads", accountLogin.getAvailableDownloads());
            editor.commit();

            String accountEncInfo = accountLogin.getAccountEncInfo();
            if (OgleHelper.emptyToNull(accountEncInfo) != null) {
                if (!OgleHelper.same(oldLoginInfo.getAccountEncInfo(), accountEncInfo)) {
                    oldLoginInfo.setAccountEncInfo(accountEncInfo);
                    isChange = true;
                }
            }

            //save account info
            if (isChange) {
                mOgleApp.saveAccountInfo();
            }
        }
        if (callBack != null) callBack.success();
    }

    /**
     * handle login error
     * @param response
     * @param body
     * @param t
     * @param numPhone
     * @param otp
     * @param callBack
     */
    private void handleLoginError(Response response, AccountList body, Throwable t, String numPhone, String otp, CallBack callBack) {
        String msg = mContext.getResources().getString(R.string.error_loginfail1);
        int code = -1;
        if (response != null && body != null) {
            Status status = body.getStatus();
            if (status != null) {
                code = status.getCode();
                if (TextUtils.isEmpty(numPhone)) {
                    numPhone = status.getAccount();
                }
                if (!TextUtils.isEmpty(status.getMessage())) {
                    msg = status.getMessage();
                }
            }
        }

        if (code == 55) { //account expired
            Log.d(TAG, "Account is expired");
            handleLoginSuccess(response, body, numPhone, otp, callBack);
        } else {
            if (callBack != null) callBack.fail(numPhone, msg);
        }
    }

    /**
     * get otp
     * @param numPhone
     */
    public void getOTP(String numPhone, final CallBack callBack) {
        Log.i(TAG, "GetOT numPhone: " + numPhone);
        APICall.getOtp(numPhone, new APIResultCallBack<Status>(){

            @Override
            public void before() {
                if (callBack != null) callBack.before();
            }

            @Override
            public void onSuccess(Status body) {
                if (callBack != null) callBack.success();
            }

            @Override
            public void onError(int httpCode, Status body, Throwable t) {
                String msg = "Fail to send OTP, please check internet.";
                if (body != null) {
                    int code = body.getCode();
                    if (code == 8) {
                        msg = mContext.getResources().getString(R.string.overmaxotpfail);
                    } else {
                        msg = mContext.getResources().getString(R.string.msgotpfail);
                    }
                }
                if (callBack != null) callBack.fail(null, msg);
            }
        });
    }
}
