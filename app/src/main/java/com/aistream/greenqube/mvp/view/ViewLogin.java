package com.aistream.greenqube.mvp.view;

/**
 * Created by Administrator on 8/8/2017.
 */

public interface ViewLogin {
    void loginSuccess();

    void hideLoading();

    void showToasApp(String msg, int gravity);

    void hideFrameBG();

    void showDialogLoading();

    void showOTP();

    void otpFail(String msg);

    void updateOTP(String senderOTP, String smsOtp);

    void setNumPhoneWhengetFail(String numPhone);

    void showHideSpinner(int type);

    void showLoginPage();

    void hideLoginPage();
}
