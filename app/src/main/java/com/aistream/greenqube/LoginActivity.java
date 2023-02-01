package com.aistream.greenqube;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aistream.greenqube.activity.MainActivity;
import com.aistream.greenqube.activity.PermissionNoticeActivity;
import com.aistream.greenqube.customs.CustomLoadingView;
import com.aistream.greenqube.mvp.presenter.PresenterLoginlmp;
import com.aistream.greenqube.mvp.view.ViewLogin;
import com.aistream.greenqube.upgrade.UpdateCallback;
import com.aistream.greenqube.util.DialogCallBack;
import com.aistream.greenqube.util.OgleHelper;
import com.sahooz.library.Country;
import com.sahooz.library.PickActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 5/9/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ViewLogin {
    private PresenterLoginlmp presenterLoginlmp;
    private EditText et_inputPhone;
    private EditText et_inputotp;
    private Button btn_lestgosignin;
    private LinearLayout ll_signin;
    private FrameLayout frm_bg;
    private TextView tv_otpsend;
    private FrameLayout ll_otp;
    private ImageView im_checkotp;
    private ImageView im_checkphone;

    private String otpSignIn;
    SharedPreferences mPref;
    private OgleApplication mOgleApp;
    private int typeChangeLogin = 0;//0: Nomal, 1: send OTP, 2: Login
    private int typeChangeSignup = 0;//0: Nomal, 1: send OTP, 2: Login
    private TextView spnCountryCode;
    private FrameLayout frm_spinner;
    private ScrollView scrollView;
    private Handler handler = new Handler();
    private String[] countryCode;
    private CustomLoadingView loadingView;
    private String TAG = "LoginActivity";
    private CountDownTimer cTimer;
    private boolean isCountFinish = true;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_APP_SETTING = 113;
    private static final int REQUEST_PERMISSION_NOTICE = 114;
    private static final List<String> unPermissions = new ArrayList<>();
    private String[] allPermissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION/*,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE*/};

    private String[] smsPermissions = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LoginActivity", "onCreate");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mOgleApp = (OgleApplication) getApplicationContext();
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        presenterLoginlmp = new PresenterLoginlmp(this, this);
        presenterLoginlmp.registerReceiver();

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        setContentView(R.layout.activity_login);
        cTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isCountFinish = false;
                otpTime = millisUntilFinished;
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                String timer = (String.format("%02d", seconds));

                if (et_inputotp.getText().length() < 6) {
                    btn_lestgosignin.setText(getResources().getString(R.string.sendotpagain).replace("XXX", timer));
                    btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login1);
                }
            }

            @Override
            public void onFinish() {
                otpTime = 0;
                isCountFinish = true;
                if (et_inputotp.getText().length() < 6) {
                    typeChangeLogin = 1;
                    btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login2);
                    btn_lestgosignin.setText(getResources().getString(R.string.smsotp));
                }
            }
        };
        initView();

        //android 6+ apply dynamic permission
        if (!checkPermissions()) {
            return;
        }

        boolean logout = getIntent().getBooleanExtra("logout", false);
        Log.d("LoginServlet", "logout: "+logout);
        if (logout) {
            showLoginPage();
            String numPhone = mPref.getString("numberPhone", "");
            if (TextUtils.isEmpty(numPhone)) {
                numPhone = mOgleApp.getMobilePhoneNumber();
            }
            et_inputPhone.setText(numPhone);
            checkPhoneEditText();
        } else {
            hideLoginPage();
            doLogin();
        }
    }

    /**
     * check app permission
     * @return
     */
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGrantedPermission(null, allPermissions)) {
            //show app initial permission notice
            startActivityForResult(new Intent(this, PermissionNoticeActivity.class), REQUEST_PERMISSION_NOTICE);
            return false;
        }
        return true;
    }

    private boolean checkGrantedPermission(List<String> unAllowedPermissions, String... permissions) {
        boolean flag = true;
        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Permission ["+permission+"] can not be granted.");
                flag = false;
                if (unAllowedPermissions != null)
                    unAllowedPermissions.add(permission);
            }
        }
        return flag;
    }


    private void requestPermissions() {
        List<String> unAllowPermission = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGrantedPermission(unAllowPermission, allPermissions)) {
            requestPermissions(REQUEST_WRITE_STORAGE, unAllowPermission.toArray(new String[unAllowPermission.size()]));
        }
    }

    private void requestPermissions(int reqCode, String... permissions) {
        ActivityCompat.requestPermissions(LoginActivity.this, permissions, reqCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                            @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            hideLoginPage();
            boolean grantSucc = true;
            String noAskPermission = null;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    grantSucc = false;
                    if (!shouldShowRequestPermissionRationale(permissions[i])) {
                        noAskPermission = permissions[i];
                    }
                }
            }

            if (grantSucc) {
                doLogin();
            } else {
                if (noAskPermission != null) {
                    String notice = getResources().getString(R.string.permission_notice);
                    if (noAskPermission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        notice = String.format(notice, "Storage");
                    } else {
                        notice = String.format(notice, "Location");
                    }
                    OgleHelper.showDialog(LoginActivity.this, notice, "Open Now", "Cancel", new DialogCallBack() {
                        @Override
                        public void ok() {
                            goToAppSetting();
                        }

                        @Override
                        public void cancel() {
                            requestPermissions();
                        }
                    });
                } else {
                    checkPermissions();
                }
            }
        } else {
            presenterLoginlmp.getOTP(getPhoneNumber());
        }
    }

    /**
     * open app setting
     */
    private void goToAppSetting() {
        checkGrantedPermission(unPermissions, allPermissions);
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_APP_SETTING);
    }

    /**
     * call login api and load movie data
     */
    private void doLogin() {
        mOgleApp.restoreAccount();
        mOgleApp.restoreMovieBillings();
        mOgleApp.restorePurchaseHistory();

        //check app version
        mOgleApp.checkAppUpgrade(this, new UpdateCallback(){
            @Override
            protected void onAfter(boolean hasNewVersion) {
                if (!hasNewVersion) {
                    if (presenterLoginlmp.isDataPrepareSuccess()) {
                        showDialogLoading();
                        handler.postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                presenterLoginlmp.openLogin();
                            }
                        }, 3000);
                    } else {
                        presenterLoginlmp.openLogin();
                    }
                }
            }
        });
    }

    @Override
    public void showLoginPage() {
        frm_bg.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);

        String phone = et_inputPhone.getText().toString();
        String mobilePhoneNumber = mOgleApp.getMobilePhoneNumber();
        if (TextUtils.isEmpty(phone) && !TextUtils.isEmpty(mobilePhoneNumber)) {
            et_inputPhone.setText(mobilePhoneNumber);
            checkPhoneEditText();
        }
        //check app upgrade
        mOgleApp.checkAppUpgrade(this, null);
    }

    @Override
    public void hideLoginPage() {
        frm_bg.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void initView() {
        loadingView = new CustomLoadingView();
        frm_bg = (FrameLayout) findViewById(R.id.frm_bg);
        ll_signin = (LinearLayout) findViewById(R.id.ll_signin);
        et_inputPhone = (EditText) findViewById(R.id.et_inputPhone);
        et_inputotp = (EditText) findViewById(R.id.et_inputotp);
        btn_lestgosignin = (Button) findViewById(R.id.btn_lestgosignin);
        tv_otpsend = (TextView) findViewById(R.id.tv_otpsend);
        ll_otp = (FrameLayout) findViewById(R.id.ll_otp);
        im_checkotp = (ImageView) findViewById(R.id.im_checkotp);
        im_checkphone = (ImageView) findViewById(R.id.im_checkphone);
        frm_spinner = (FrameLayout) findViewById(R.id.frm_spinner);
        spnCountryCode = (TextView) findViewById(R.id.spnCountryCode);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        //get phone
        spnCountryCode.setText("+" + mOgleApp.getDefaultCountryCode());
        spnCountryCode.setOnClickListener(this);

        et_inputPhone.setText(mOgleApp.getMobilePhoneNumber());
        checkPhoneEditText();

        if (et_inputPhone.getText().length() >= 10 && ll_otp.getVisibility() == View.GONE) {
            typeChangeLogin = 1;
            im_checkphone.setBackgroundResource(R.drawable.ic_checked);
            btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login2);
            btn_lestgosignin.setText(getResources().getString(R.string.smsotp));
            tv_otpsend.setVisibility(View.VISIBLE);
        }
        et_inputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("TextChanged", "beforeTextChanged: " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("TextChanged", "onTextChanged: " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String x = s.toString();
                if (x.length() == 0) {
                    typeChangeLogin = 0;
                    checkPhoneEditText();
                    ll_otp.setVisibility(View.GONE);
                    im_checkphone.setBackgroundResource(R.drawable.ic_unchecked);
                    btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login1);
                    btn_lestgosignin.setText(getResources().getString(R.string.smsotp));
                    et_inputotp.setText("");
                    tv_otpsend.setVisibility(View.VISIBLE);
                } else if (x.length() < 7) {
                    typeChangeLogin = 0;
                    im_checkphone.setBackgroundResource(R.drawable.ic_unchecked);
                    btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login1);
                } else {
                    if (x.startsWith("0")) {
                        if (x.length() >= 8 && et_inputotp.getText().length() == 6) {
                            typeChangeLogin = 2;
                            im_checkphone.setBackgroundResource(R.drawable.ic_checked);
                            btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login3);
                            btn_lestgosignin.setText(getResources().getString(R.string.lestgo));
                        } else if (x.length() >= 8 && ll_otp.getVisibility() == View.GONE) {
                            typeChangeLogin = 1;
                            im_checkphone.setBackgroundResource(R.drawable.ic_checked);
                            btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login2);
                            btn_lestgosignin.setText(getResources().getString(R.string.smsotp));
                            tv_otpsend.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (x.length() >= 7 && et_inputotp.getText().length() == 6) {
                            typeChangeLogin = 2;
                            im_checkphone.setBackgroundResource(R.drawable.ic_checked);
                            btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login3);
                            btn_lestgosignin.setText(getResources().getString(R.string.lestgo));
                        } else if (x.length() >= 7 && ll_otp.getVisibility() == View.GONE) {
                            im_checkphone.setBackgroundResource(R.drawable.ic_checked);
                            if (isCountFinish) {
                                typeChangeLogin = 1;
                                btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login2);
                                btn_lestgosignin.setText(getResources().getString(R.string.smsotp));
                                tv_otpsend.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                Log.i("TextChanged", "typeChangeLogin: " + typeChangeLogin);
            }
        });

        et_inputotp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String x = s.toString();
                if (x.length() == 0 || x.length() < 6) {
                    if (x.length() == 0 && ll_otp.getVisibility() == View.VISIBLE) {
                        tv_otpsend.setVisibility(View.GONE);
                    }
                    if (otpTime == 0) {
                        typeChangeLogin = 1;
                        btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login2);
                        btn_lestgosignin.setText(getResources().getString(R.string.smsotp));
                    } else {
                        int seconds = (int) (otpTime / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        String timer = (String.format("%02d", seconds));
                        btn_lestgosignin.setText(getResources().getString(R.string.sendotpagain).replace("XXX", timer));
                        btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login1);
                    }
                    im_checkotp.setBackgroundResource(R.drawable.ic_unchecked);
                } else {
                    if (x.length() == 6 && et_inputPhone.getText().length() >= 7) {
                        typeChangeLogin = 2;
                        im_checkotp.setBackgroundResource(R.drawable.ic_checked);
                        btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login3);
                        btn_lestgosignin.setText(getResources().getString(R.string.lestgo));
                    }
                }
            }
        });
        btn_lestgosignin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lestgosignin:
                if (typeChangeLogin == 0) {

                } else if (typeChangeLogin == 1) { //send otp
                    String phoneNumber = getPhoneNumber();
                    if (!presenterLoginlmp.isPhoneNumberValid(phoneNumber, "")) {
                        showToasApp("Invalid Phone Number", Gravity.BOTTOM);
                        return;
                    }
                    typeChangeLogin = 0;
                    if (!checkGrantedPermission(null, smsPermissions)) {
                        requestPermissions(12, smsPermissions);
                    } else {
                        presenterLoginlmp.getOTP(phoneNumber);
                    }
                } else if (typeChangeLogin == 2) { //login
                    String phoneNumber = getPhoneNumber();
                    String otpSignIn = et_inputotp.getText().toString();
                    if (!presenterLoginlmp.isPhoneNumberValid(phoneNumber, "")) {
                        showToasApp("Invalid Phone Number", Gravity.BOTTOM);
                        return;
                    }
                    beforeLogin(phoneNumber, otpSignIn);
                }
                Log.i("TextChanged", "typeChangeLogin: " + typeChangeLogin);
                break;
            case R.id.spnCountryCode:
                startActivityForResult(new Intent(getApplicationContext(), PickActivity.class), 111);
                break;
        }
    }

    private String getPhoneNumber() {
        String phoneNumber = String.valueOf(et_inputPhone.getText());
        if (!TextUtils.isEmpty(phoneNumber) && !phoneNumber.contains("+")) {
            phoneNumber = spnCountryCode.getText() + phoneNumber;
        }
        return phoneNumber;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            String countryInfo = data.getStringExtra("country");
            Log.d(TAG, "onActivityResult countryInfo:" + countryInfo);
            Country country = Country.fromJson(data.getStringExtra("country"));
            if (country != null) {
                Log.d(TAG, "onActivityResult country.code=" + country.code);
                Log.d(TAG, "onActivityResult spnCountryCode=" + spnCountryCode);
                spnCountryCode.setText("+" + country.code);
            }
        } else if (requestCode == REQUEST_APP_SETTING) {
            List<String> list = new ArrayList<>();
            checkGrantedPermission(list, allPermissions);
            if (list.size() <= unPermissions.size()) {
                if (list.size() == unPermissions.size()) {
                    if (!OgleHelper.isEqual(list, unPermissions)) {
                        return;
                    }
                }
                requestPermissions(REQUEST_WRITE_STORAGE, allPermissions);
            }
        } else if (requestCode == REQUEST_PERMISSION_NOTICE) {
            if (resultCode == Activity.RESULT_OK) {
                requestPermissions(REQUEST_WRITE_STORAGE, allPermissions);
            } else {
                checkPermissions();
            }
        }
    }

    @Override
    public void loginSuccess() {
        Intent intent = getIntent();
        boolean logout = getIntent().getBooleanExtra("logout", false);
        if (logout) {
            mPref.edit().putBoolean("refresh", true).commit();
            super.onBackPressed();
        } else {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        Log.i("LoginAppSuccess", "Login App Success");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
        presenterLoginlmp.unregisterReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("LoginActivity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("LoginActivity", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LoginActivity", "onPause");
    }

    @Override
    public void hideLoading() {
        loadingView.hideProgress();
        Log.i("LoginActivity", "hideLoading");
    }

    @Override
    public void showDialogLoading() {
        if (loadingView != null) {
            loadingView.hideProgress();
            loadingView.showProgress(this);
            Log.i("LoginActivity", "showDialogLoading");
        }
    }

    private void beforeLogin(String numberPhone, String otp) {
        if (mOgleApp.isCheckNetworkAvailable()) {
            this.otpSignIn = otp;
            presenterLoginlmp.otpLogin(numberPhone, otp);
        } else {
            OgleHelper.showMessage(this, getResources().getString(R.string.notnetwork), null);
        }
    }

    private static Toast t = null;

    @Override
    public void showToasApp(String msg, int gravity) {
        if (t != null) {
            t.cancel();
        }
        t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        t.setDuration(Toast.LENGTH_LONG);
        centerText(t.getView());

        t.setGravity(gravity | Gravity.CENTER_HORIZONTAL, 0, 0);
        //  t.cancel();
        if (!t.getView().isShown()) {
            t.show();
        }
    }

    @Override
    public void hideFrameBG() {
        frm_bg.setVisibility(View.GONE);
    }

    private long otpTime;

    @Override
    public void showOTP() {
        ll_otp.setVisibility(View.VISIBLE);
        tv_otpsend.setVisibility(View.GONE);
        if (cTimer != null) {
            cTimer.cancel();
            otpTime = 0;
        }
        cTimer.start();
    }

    @Override
    public void otpFail(String msg) {
        ll_otp.setVisibility(View.VISIBLE);
        tv_otpsend.setVisibility(View.GONE);
        typeChangeLogin = 1;
        showToasApp(msg, Gravity.CENTER);
    }

    private boolean isNumberic(String s) {
        try {
            int number = Integer.parseInt(s);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    public void updateOTP(String senderOTP, String smsOtp) {
        if (senderOTP != null && smsOtp != null) {
            String ss[] = smsOtp.split("\\s");
            if (smsOtp.contains("OTP")) {
                for (String text : ss) {
                    if (text.length() == 6 && isNumberic(text)) {
                        Log.i("SMSOTP", "number OTB is " + text);
                        et_inputotp.setText(text);
                        String numberPhone = getPhoneNumber();
                        presenterLoginlmp.otpLogin(numberPhone, text);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void setNumPhoneWhengetFail(String numPhone) {
        if (et_inputPhone != null) {
            if(numPhone != null && !numPhone.isEmpty()) {
                et_inputPhone.setText(numPhone);
                typeChangeLogin = 0;
                btn_lestgosignin.setBackgroundResource(R.drawable.bg_btn_login1);
                showOTP();
            }
            checkPhoneEditText();
        }
    }

    @Override
    public void showHideSpinner(int type) {
        if (frm_spinner != null) {
            if (type == 0) {
                frm_spinner.setVisibility(View.VISIBLE);
            } else {
                frm_spinner.setVisibility(View.GONE);
            }
        }
    }

    void centerText(View view) {
        if (view instanceof TextView) {
            ((TextView) view).setGravity(Gravity.CENTER);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.size14));
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int n = group.getChildCount();
            for (int i = 0; i < n; i++) {
                centerText(group.getChildAt(i));
            }
        }
    }

    private void checkPhoneEditText() {
        String phoneNumber = et_inputPhone.getText().toString();
        if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.contains("+")) {
            frm_spinner.setVisibility(View.GONE);
        } else {
            frm_spinner.setVisibility(View.VISIBLE);
            String defaultCountryCode = mOgleApp.getDefaultCountryCode();
            spnCountryCode.setText("+" + defaultCountryCode);
        }
    }
}