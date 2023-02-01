package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.AccountLogin;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.R;
import com.aistream.greenqube.util.OgleHelper;

import java.util.List;

/**
 * Created by PhuDepTraj on 3/19/2018.
 */

public class CustomDialog_SupportInfo extends Dialog implements View.OnClickListener {
    private ViewMain viewMain;
    private FrameLayout btn_back;
    private Context mContext;
    private OgleApplication ogleApplication;
    private SharedPreferences mPref;
    private TabLayout contact_tab;
    private TextView tv_deviceName;
    private TextView tv_osversion;
    private TextView tv_qrouter;
    private TextView tv_appversion;
    private TextView tv_wifitype;
    private TextView tv_device;
    private TextView tv_strength;
    private PresenterMainImp presenterMainImp;
    private int clickCounts = 0;

    public CustomDialog_SupportInfo(Context context, ViewMain view, PresenterMainImp main) {
        super(context, R.style.AppThemeDialog);
        this.viewMain = view;
        this.mContext = context;
        this.presenterMainImp = main;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        ogleApplication = (OgleApplication) mContext.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_supportinfo);

        //show status bar
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);

        initView();
    }

    private void initView() {
        String model = Build.MODEL;
        String manufacture = Build.MANUFACTURER;
        String version = android.os.Build.VERSION.RELEASE;

        contact_tab = (TabLayout) findViewById(R.id.contact_tab);
        btn_back = (FrameLayout) findViewById(R.id.btn_back);
        tv_deviceName = (TextView) findViewById(R.id.tv_deviceName);
        tv_osversion = (TextView) findViewById(R.id.tv_osversion);
        tv_qrouter = (TextView) findViewById(R.id.tv_qrouter);
        tv_appversion = (TextView) findViewById(R.id.tv_appversion);
        tv_wifitype = (TextView) findViewById(R.id.tv_wifitype);
        tv_strength = (TextView) findViewById(R.id.tv_strength);
        tv_device = (TextView) findViewById(R.id.tv_device);

        //add tab
        addTab(R.drawable.ic_whatsapp, R.string.whatsapp);
        addTab(R.drawable.ic_viber, R.string.viber);
        addTab(R.drawable.ic_wechat, R.string.weChat);
        addTab(R.drawable.ic_messenger, R.string.messenger);

        //show data
        tv_deviceName.setText(manufacture.toUpperCase() + " - " + model);
        loadWifiInfo();
        tv_osversion.setText("Android " + version);
        tv_appversion.setText(getVersion());

        AccountLogin accountLogin = ogleApplication.getAccountLogin();
        tv_device.setText(accountLogin.getDevice());

        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_resetapp)).setOnClickListener(this);
        contact_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabClick(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tabClick(tab);
            }
        });
    }

    private void tabClick(TabLayout.Tab tab) {
        String msg = "";
        switch (tab.getPosition()) {
            case 0:
                msg = mPref.getString("Whatsapp", "Please add our Whatsapp ID/Phone +1236 688 1211 to get assistance.");
                break;
            case 1:
                msg = mPref.getString("Viber", "Please add our Viber ID/phone  +1236 688 1211 to get assistance.");
                break;
            case 2:
                msg = mPref.getString("Wechat", "Please add our WeChat ID \"greenQube\" to get assistance.");
                break;
            case 3:
                msg = mPref.getString("Messenger", "Please add our Messenger ID \"greenQube\" to get assistance.");
                break;
        }

        if (!TextUtils.isEmpty(msg)) {
            OgleHelper.showMessage(mContext, msg, null);
        }
    }

    private void addTab(int resID, int tagID) {
        String tag = mContext.getResources().getString(tagID);
        TabLayout.Tab newTab = contact_tab.newTab();
        newTab.setCustomView(getTabIconView(resID, tag));
        newTab.setTag(tag);
        contact_tab.addTab(newTab);
    }

    private View getTabIconView(int resID, String title) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_item_tab, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView txt_title = (TextView) view.findViewById(R.id.title);
        icon.setBackgroundResource(resID);
        txt_title.setText(title);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
            case R.id.tv_resetapp:
                final CustomDialogChargeMovieDownLoad dg = CustomDialogChargeMovieDownLoad.getInstance(mContext);
                dg.withTitle(mContext.getResources().getString(R.string.confirmreset))
                        .setOkClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewMain.resetApp();
                            }
                        }).setNoClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dg.dismiss();
                    }
                }).show();
                break;
        }
    }

    private String getVersion() {
        PackageManager manager = getContext().getPackageManager();
        PackageInfo info = new PackageInfo();
        try {
            info = manager.getPackageInfo(getContext().getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return info.versionName;
    }

    /**
     * check wifi whether 5G
     */
    private boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }

    private void loadWifiInfo() {
        WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    String ssid = wifiInfo.getSSID();
                    String frequency = "2.4Ghz";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        frequency = is5GHz(wifiInfo.getFrequency())? "5Ghz": "2.4Ghz";
                    } else {
                        if (ssid != null && ssid.length() > 2) {
                            String wifiSsid = ssid.substring(1, ssid.length() - 1);
                            List<ScanResult> scanResults = manager.getScanResults();
                            if (scanResults != null) {
                                for(ScanResult scanResult:scanResults){
                                    if(scanResult.SSID.equals(wifiSsid)){
                                        frequency = is5GHz(scanResult.frequency)? "5Ghz": "2.4Ghz";
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    int rssid = wifiInfo.getRssi();
                    int level = WifiManager.calculateSignalLevel(rssid, 1000);
                    String strength = OgleHelper.formatDouble(Math.round(level*100/1000), 2) + "%";

                    tv_qrouter.setText(!TextUtils.isEmpty(ssid)? ssid.replace("\"", ""): "");
                    tv_wifitype.setText(frequency);
                    tv_strength.setText(strength);
                }
            }
        }
    }
}
