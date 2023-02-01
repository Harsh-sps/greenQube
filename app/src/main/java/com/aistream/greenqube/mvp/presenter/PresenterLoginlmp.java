/*
 *
 *  * Copyright (C) 2014 Antonio Leiva Gordillo.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.aistream.greenqube.mvp.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.rest.DataLoader;
import com.aistream.greenqube.mvp.view.ViewLogin;
import com.aistream.greenqube.R;
import com.aistream.greenqube.util.OgleHelper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PresenterLoginlmp {
    private Context mContext;
    private DataBaseHelper dataBaseHelper;
    private OgleApplication mOgleApp;
    private SharedPreferences mPref;
    private ViewLogin viewLogin;
    private LoginManager loginManager;

    public PresenterLoginlmp(ViewLogin viewLogin, Context cont) {
        this.mContext = cont;
        this.mOgleApp = (OgleApplication) mContext.getApplicationContext();
        this.viewLogin = viewLogin;
        this.dataBaseHelper = DataBaseHelper.getInstance();
        this.mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        loginManager = new LoginManager(cont);
    }

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SMS_RECEIVED)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus.length == 0) {
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        sb.append(messages[i].getMessageBody());
                    }
                    String sender = messages[0].getOriginatingAddress();
                    String message = sb.toString();
                    Log.i("SMSOTP", "SMS OTP: " + message);
                    Log.i("SMSOTP", "Sender OTP: " + sender);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    viewLogin.updateOTP(sender, message);
                }
            }
        }
    };

    public void registerReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mContext.registerReceiver(broadcastReceiver, mIntentFilter);
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(broadcastReceiver);
    }

    public void getDataFromServer() {
        new DataLoader(mContext, new DataLoader.Listener() {
            @Override
            public void success() {
                viewLogin.loginSuccess();
            }

            @Override
            public void fail() {
                if (isDataPrepareSuccess()) {
                    viewLogin.loginSuccess();
                } else {
                    viewLogin.hideLoading();
                    OgleHelper.showMessage(mContext, mContext.getResources().getString(R.string.load_data_fail), null);
                }
            }

            @Override
            public void expired() {
                if (isDataPrepareSuccess()) {
                    viewLogin.loginSuccess();
                } else {
                    viewLogin.hideLoading();
                    OgleHelper.showMessage(mContext, mContext.getResources().getString(R.string.load_data_fail), null);
                }
            }
        });
    }

    public boolean isDataPrepareSuccess() {
        if (!dataBaseHelper.getAllListMovie().isEmpty() &&
                !dataBaseHelper.getAllListNewRelease().isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * open login
     */
    public void openLogin() {
        loginManager.openLogin(new LoginManager.CallBack() {
            @Override
            public void before() {
                viewLogin.showDialogLoading();
            }

            @Override
            public void success() {
                getDataFromServer();
            }

            @Override
            public void fail(String numPhone, String msg) {
                viewLogin.hideLoading();
                OgleHelper.showMessage(mContext, msg, null);
            }
        });
    }

    /**
     * otp login
     * @param numPhone
     * @param otp
     */
    public void otpLogin(final String numPhone, final String otp) {
        loginManager.otpLogin(numPhone, otp, new LoginManager.CallBack() {
            @Override
            public void before() {
                viewLogin.showDialogLoading();
            }

            @Override
            public void success() {
                getDataFromServer();
            }

            @Override
            public void fail(String numPhone, String msg) {
                viewLogin.hideLoading();
                viewLogin.showLoginPage();
                if (!TextUtils.isEmpty(numPhone)) {
                    viewLogin.setNumPhoneWhengetFail(numPhone);
                }
                OgleHelper.showMessage(mContext, msg, null);
            }
        });
    }

    /**
     * get otp
     * @param numPhone
     */
    public void getOTP(String numPhone) {
        loginManager.getOTP(numPhone, new LoginManager.CallBack() {
            @Override
            public void before() {
                viewLogin.showDialogLoading();
            }

            @Override
            public void success() {
                viewLogin.hideLoading();
                viewLogin.showOTP();
                viewLogin.showToasApp(mContext.getResources().getString(R.string.msgotpsuccess), Gravity.CENTER);
            }

            @Override
            public void fail(String numPhone, String msg) {
                viewLogin.hideLoading();
                viewLogin.otpFail(msg);
            }
        });
    }

    /**
     * check phone number is validation  like china phone number: +8615213141516
     *
     * @param phoneNumber
     * @return
     */
    public boolean isPhoneNumberValid(String phoneNumber, String countryCode) {
        Log.d("PresenterLoginlmp", "isPhoneNumberValid phoneNumber:" + phoneNumber + ", countryCode:" + countryCode);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            Log.e("PresenterLoginlmp", "isPhoneNumberValid NumberParseException was thrown: " + e.toString());
        }
        return false;
    }
}
