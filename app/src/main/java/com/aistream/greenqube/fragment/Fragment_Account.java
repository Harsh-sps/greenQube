package com.aistream.greenqube.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.customs.CustomDialog_AccountBalance;
import com.aistream.greenqube.customs.CustomDialog_AccountMore;
import com.aistream.greenqube.customs.CustomDialog_Favorites;
import com.aistream.greenqube.customs.CustomDialog_PurchaseHistory;
import com.aistream.greenqube.customs.CustomDialog_SupportInfo;
import com.aistream.greenqube.mvp.model.AccountInfo;
import com.aistream.greenqube.mvp.model.AccountLogin;
import com.aistream.greenqube.mvp.presenter.PresenterAccount;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.view.ViewAccount;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.R;
import com.aistream.greenqube.util.OgleHelper;

/**
 * Created by PhuDepTraj on 4/12/2018.
 */

public class Fragment_Account extends Fragment implements ViewAccount, View.OnClickListener {
    private String TAG = "FragmentAccount";
    private PresenterMainImp presenterMainImp;
    public PresenterAccount presenterAccount;
    private ViewMain viewMain;
    private OgleApplication ogleApplication;

    private LinearLayout ll_mainaccount;
    private FrameLayout frm_profile;
    private FrameLayout frm_recharge;
    private FrameLayout frm_expirydate;
    private FrameLayout frm_purchasehistory;
    private FrameLayout frm_favmovie;
    private FrameLayout frm_about;
    private FrameLayout frm_notifi;
    private FrameLayout frm_planprice;
    private TextView tv_username;
    private TextView tv_userid;
    private TextView tv_money;
    private TextView tv_version;
    private TextView tv_planname;
    private TextView tv_planprice;
    private TextView tv_expirydate;
    private SharedPreferences mPref;
    private TextView tv_accountbaclance;
    private TextView tv_download_credits;
    private Context mContext;

    public Fragment_Account() {
        super();
    }

    @SuppressLint("ValidFragment")
    public Fragment_Account(PresenterMainImp presenterMainImp, ViewMain viewMain) {
        this.viewMain = viewMain;
        this.presenterMainImp = presenterMainImp;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        ogleApplication = (OgleApplication) mContext.getApplicationContext();
        presenterAccount = new PresenterAccount(mContext, viewMain, this, presenterMainImp);
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        presenterAccount.getDataAccount();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        ll_mainaccount = (LinearLayout) view.findViewById(R.id.ll_mainaccount);
        frm_profile = (FrameLayout) view.findViewById(R.id.frm_profile);
        frm_recharge = (FrameLayout) view.findViewById(R.id.frm_recharge);
        frm_expirydate = (FrameLayout) view.findViewById(R.id.frm_expirydate);
        frm_purchasehistory = (FrameLayout) view.findViewById(R.id.frm_purchasehistory);
        frm_favmovie = (FrameLayout) view.findViewById(R.id.frm_favmovie);
        frm_about = (FrameLayout) view.findViewById(R.id.frm_about);
        frm_planprice = (FrameLayout) view.findViewById(R.id.frm_planprice);

        tv_username = (TextView) view.findViewById(R.id.tv_username);
        tv_userid = view.findViewById(R.id.tv_userid);
        tv_money = (TextView) view.findViewById(R.id.tv_money);
        tv_version = (TextView) view.findViewById(R.id.tv_version);
        tv_planname = (TextView) view.findViewById(R.id.tv_planname);
        tv_planprice = (TextView) view.findViewById(R.id.tv_planprice);
        tv_expirydate = (TextView) view.findViewById(R.id.tv_expirydate);
        tv_accountbaclance = (TextView) view.findViewById(R.id.tv_accountbaclance);
        tv_download_credits = (TextView) view.findViewById(R.id.tv_download_credits);
        //show view data
        showData();

        frm_profile.setOnClickListener(this);
        frm_recharge.setOnClickListener(this);
        frm_purchasehistory.setOnClickListener(this);
        frm_favmovie.setOnClickListener(this);
        frm_about.setOnClickListener(this);
    }

    public void loadData() {
        if (presenterAccount != null) {
            presenterAccount.getDataAccount();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frm_profile:
                CustomDialog_AccountMore dgAccMore = new CustomDialog_AccountMore(mContext, this, presenterMainImp);
                dgAccMore.show();
                break;
            case R.id.frm_recharge:
                CustomDialog_AccountBalance dgBalance = new CustomDialog_AccountBalance(mContext, this, presenterMainImp);
                dgBalance.show();
                break;
            case R.id.frm_purchasehistory:
                CustomDialog_PurchaseHistory dgPurechase = new CustomDialog_PurchaseHistory(getActivity(), viewMain);
                dgPurechase.show();
                break;
            case R.id.frm_favmovie:
                CustomDialog_Favorites dgFavorite = new CustomDialog_Favorites(mContext, viewMain, presenterMainImp);
                dgFavorite.show();
//                    viewMain.showToasApp(getContext().getResources().getString(R.string.comingsoon), Gravity.CENTER);
                break;
            case R.id.frm_about:
                CustomDialog_SupportInfo dgSupport = new CustomDialog_SupportInfo(mContext, viewMain, presenterMainImp);
                dgSupport.show();
                break;
        }
    }

    @Override
    public void refreshAccount() {
        showData();
        String accountBalance = mPref.getString("accountBalance", "");
        if (!TextUtils.isEmpty(accountBalance)) {
            tv_money.setText(getResources().getString(R.string.currency) + accountBalance);
        }
    }

    @Override
    public AccountInfo getAccountInfo() {
        return presenterAccount.getAccountInfo();
    }

    /**
     * check account whether login, if logined, then show phone number
     */
    public void showData() {
        String userId = mPref.getString("userId", "");
        String userName = mPref.getString("userName", "New User");
        tv_username.setText(userName);
        if (!TextUtils.isEmpty(userId)) {
            tv_userid.setVisibility(View.VISIBLE);
            tv_userid.setText("Acct No: "+userId);
            tv_username.setPadding(0, 0, 0, 0);
        } else {
            tv_userid.setVisibility(View.GONE);
            tv_username.setPadding(0, (int)getResources().getDimension(R.dimen.size10), 0, 0);
        }

        //show next billing date
        long next_billing_date = mPref.getLong("next_billing_date", 0);
        if (next_billing_date > 0) {
            frm_expirydate.setVisibility(View.VISIBLE);
            tv_expirydate.setText(OgleHelper.getDayTime(next_billing_date));
        } else {
            frm_expirydate.setVisibility(View.GONE);
        }

        //show plan name
        String planName = mPref.getString("demoPlan", "");
        tv_planname.setText(TextUtils.isEmpty(planName)? "": planName);

        //show plan price
        String planPrice = ogleApplication.getPlanPrice();
        if (!TextUtils.isEmpty(planPrice)) {
            tv_planprice.setVisibility(View.VISIBLE);
            tv_planprice.setText(ogleApplication.getPlanPrice());
        } else {
            tv_planprice.setVisibility(View.GONE);
        }

        //show available download credits
        int availableDownloads = mPref.getInt("availableDownloads", 0);
        if (availableDownloads == 9999) {
            tv_download_credits.setText("unlimit");
        } else {
            tv_download_credits.setText(String.valueOf(availableDownloads));
        }
    }

    private boolean checkNormalAccount() {
        AccountLogin accountLogin = ogleApplication.getAccountLogin();
        if (!TextUtils.isEmpty(accountLogin.getPhoneNumber())) {
            return true;
        }
        viewMain.showLoginPage();
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean refresh = mPref.getBoolean("refresh", false);
        if (refresh) {
            refreshData();
        }
    }

    public void refreshData() {
        showData();
        presenterAccount.getDataAccount();
        mPref.edit().putBoolean("refresh", false).commit();
    }
}
