package com.aistream.greenqube.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.AccountInfo;
import com.aistream.greenqube.mvp.model.AccountInfoList;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.APIResultCallBack;
import com.aistream.greenqube.mvp.view.ViewAccount;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.R;
import com.aistream.greenqube.util.OgleHelper;

import retrofit2.Response;

/**
 * Created by PhuDepTraj on 4/12/2018.
 */

public class PresenterAccount {
    private Context mContext;
    private ViewAccount viewAccount;
    private PresenterMainImp presenterMainImp;
    private OgleApplication ogleApplication;
    private SharedPreferences mPref;
    private ViewMain viewMain;
    private AccountInfo accountInfo;

    public PresenterAccount(Context cont, ViewMain main, ViewAccount viewAccount, PresenterMainImp presenterMainImp) {
        this.mContext = cont;
        this.viewMain = main;
        this.viewAccount = viewAccount;
        this.presenterMainImp = presenterMainImp;
        this.ogleApplication = (OgleApplication) mContext.getApplicationContext();
        this.mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.accountInfo = new AccountInfo(ogleApplication.getAccountLogin());
    }

    public void getDataAccount() {
        APICall.getAccountInfo(new APIResultCallBack<AccountInfoList>(){
            @Override
            public void before() {
            }

            @Override
            public void onSuccess(AccountInfoList body, Response response) {
                AccountInfo account = body.getData();
                if (account != null) {
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putInt("accountExpired", 0);
                    if (OgleHelper.emptyToNull(account.getUserId()) != null) {
                        editor.putString("userID", account.getUserId());
                    }

                    //check account expired
                    if (account.getStatus() != null && account.getStatus() == 6) {
                        editor.putInt("accountExpired", 1);
                    }
                    //get account balance
                    if (OgleHelper.emptyToNull(account.getBalance()) != null) {
                        editor.putString("accountBalance", account.getBalance());
                    }
                    //available downloads
                    editor.putInt("availableDownloads", account.getAvailableDownloads());
                    editor.commit();
                    Log.i("AccountInfo", "get Account Info Success!");
                }
            }

            @Override
            public void onError(int httpCode, AccountInfoList body, Throwable t) {
                Log.i("AccountInfo", "get Account Info Fail!");
                String msg = mContext.getResources().getString(R.string.error_loginfail1);
                int code = -1;
                if (body != null) {
                    Status status = body.getStatus();
                    if (status != null) {
                        code = status.getCode();
                        msg = status.getMessage();
                    }
                }
                if (code == 55) {
                    mPref.edit().putInt("accountExpired", 1).commit();
                }
                Log.d("AccountInfo", "get Account Info Fail: "+msg);
            }

            @Override
            public void after() {
                viewAccount.refreshAccount();
            }
        });
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }
}
