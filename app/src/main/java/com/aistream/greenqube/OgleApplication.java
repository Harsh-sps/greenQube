package com.aistream.greenqube;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aistream.greenqube.BuildConfig;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.AccountLogin;
import com.aistream.greenqube.mvp.model.Balance;
import com.aistream.greenqube.mvp.model.BillingStart;
import com.aistream.greenqube.mvp.model.DownloadResult;
import com.aistream.greenqube.mvp.model.MovieBilling;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.Quality;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.receiver.StorageEventListener;
import com.aistream.greenqube.services.DownloadManager;
import com.aistream.greenqube.upgrade.AppUpgradeManager;
import com.aistream.greenqube.upgrade.UpdateCallback;
import com.aistream.greenqube.util.CrashCatchHandler;
import com.aistream.greenqube.util.OgleHelper;

import com.aistream.greenqube.util.Toaster;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sahooz.library.Country;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 5/9/2017.
 */

public class OgleApplication extends Application {
    private static Context instance = null;
    protected String userAgent;
    private AccountLogin accountLogin = new AccountLogin();
    public static String keyRegister = "SOFLIX_";
    public static String playerType = "2000";
    public String osVersion = "";
    public String devicenameAndHwmodel = "";
    public static String appname = "";
    public String appversion = "";
    public String manufacturer = "";
    public String model = "";
    public boolean isSDCard;
    public int typeAuth = -1;
    private SharedPreferences preferences;
    public long flagGetDataInDay;
    public boolean isOnOffChecked;
    //    public boolean flagCheckNetWork;
    public boolean connNotification;
    public boolean isLoginNoUser;

    //chromecast connect state
    public boolean chromcastConnected = false;
    public String serialDevice = "";
    //local mobile phone number
    private String localPhoneNumber = "";
    private int defaultMcc = 0;
    private String defaultCountryCode = "";
    public File backUpDir;
    public List<Balance> balanceList = new ArrayList<>();
    private Map<String, MovieBilling> movieBillingMap = new HashMap<>();
    public static String macAddress = "";
    private static Toast t = null;
    private DownloadManager downloadManager;
    private StorageEventListener storageEventListener;
    private Gson gson = new Gson();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        instance = getApplicationContext();
        osVersion = endcodeURL(android.os.Build.VERSION.RELEASE);
        devicenameAndHwmodel = endcodeURL(android.os.Build.MODEL);
        manufacturer = endcodeURL(Build.MANUFACTURER);
        appname = this.getResources().getString(R.string.app_name);
        appversion = BuildConfig.VERSION_NAME;
        serialDevice = keyRegister + endcodeURL(Build.SERIAL);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isSDCard = preferences.getBoolean("isSDCard", true);
        isOnOffChecked = preferences.getBoolean("isOnOffChecked", false);
        flagGetDataInDay = preferences.getLong("flagGetDataInDay", 0);
        connNotification = preferences.getBoolean("connNotification", true);
        isLoginNoUser = preferences.getBoolean("isLoginNoUser", false);
        //app upgrade http cache setting
        OkHttpUtils.getInstance()
                .init(this)
                .debug(true, "okHttp")
                .timeout(20 * 1000);
        APICall.ogleApplication = this;
        //disable api check dialog popup
        disableAPIDialog();
        backUpDir = new File(Environment.getExternalStorageDirectory(), appname);
        downloadManager = new DownloadManager(getContentResolver(), getPackageName());
        //set app crash exception catch handler
        CrashCatchHandler.getInstance().init(this);
    }

    /**
     * get account info
     */
    public void restoreAccount() {
        String accountRechargeInfo = OgleHelper.readFileWithAES(new File(backUpDir, "pd"));
        Log.d("Application", "restore accountRechargeInfo: "+accountRechargeInfo);
        if (!TextUtils.isEmpty(accountRechargeInfo)) {
            accountLogin = gson.fromJson(accountRechargeInfo, AccountLogin.class);
        }

        if (TextUtils.isEmpty(accountLogin.getDevice())) {
            accountLogin.setDevice(getDevice());
        }
        Log.d("Application", "restoreAccount device: "+ this.accountLogin.getDevice());
    }

    /**
     * save account info
     */
    public void saveAccountInfo() {
        String accountRechargeInfo = accountLogin.getRechargeInfo();
        Log.d("Application", "save accountRechargeInfo: "+accountRechargeInfo);
        if (!TextUtils.isEmpty(accountRechargeInfo)) {
            OgleHelper.saveFileWithAES(new File(backUpDir, "pd"), accountRechargeInfo);
        }
    }

    /**
     * restore purchase history
     */
    public void restorePurchaseHistory() {
        String purchaseInfo = OgleHelper.readFileWithAES(new File(backUpDir, "phd.log"));
        if (!TextUtils.isEmpty(purchaseInfo)) {
            Log.d("Application", "purchaseInfo: "+purchaseInfo);
            List<Balance> retList = gson.fromJson(purchaseInfo,
                    new TypeToken<List<Balance>>() {
                    }.getType());

            if (retList != null) {
                balanceList = retList;
            }
        }
    }

    /**
     * save purchase history
     */
    public void savePurchaseHistory(Balance balance) {
        try {
            if (balance != null) {
                balanceList.add(balance);
                String purchaseInfo = gson.toJson(balanceList);
                Log.d("Application", "purchaseInfo: "+purchaseInfo);
                OgleHelper.saveFileWithAES(new File(backUpDir, "phd.log"), purchaseInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreMovieBillings() {
        String billingInfo = OgleHelper.readFileWithAES(new File(backUpDir, "mb.log"));
        if (!TextUtils.isEmpty(billingInfo)) {
            Log.d("Application", "billingInfo: "+billingInfo);
            Map<String, MovieBilling> map = gson.fromJson(billingInfo,
                    new TypeToken<Map<String, MovieBilling>>() {
                    }.getType());

            if (map != null) {
                movieBillingMap = map;
            }
        }
    }


    public void billingMovie(MovieInfo movieInfo, BillingStart billData) {
        //update account balance
        accountLogin.setAccountEncInfo(billData.getAccountEncInfo());
        accountLogin.setBalance(String.valueOf(billData.getBalance()));
        saveAccountInfo();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("accountBalance", String.valueOf(billData.getBalance()));
        editor.commit();

        //save movie billing credential
        MovieBilling movieBilling = new MovieBilling();
        movieBilling.setMovieId(movieInfo.getMovieId());
        movieBilling.setExpireOn(movieInfo.getExpireOn());
        movieBilling.setRentalStart(movieInfo.getRentalStart());
        movieBilling.setRentalEnd(movieInfo.getRentalEnd());
        movieBilling.setRentalCertificate(billData.getBillingCredential());
        movieBillingMap.put(String.valueOf(movieInfo.getMovieId()), movieBilling);

        try {
            String billingInfo = gson.toJson(movieBillingMap);
            OgleHelper.saveFileWithAES(new File(backUpDir, "mb.log"), billingInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //record billing history
        Quality defaultQuality = movieInfo.getDefaultQuality();
        Balance balance = new Balance();
        String currency = getResources().getString(R.string.currency);
        balance.setAmount(OgleHelper.formatMsg(getResources().getString(R.string.rent_price_days),
                defaultQuality.getPrice(), String.valueOf(defaultQuality.getRentalPeriod())));
        balance.setBalance(billData.getBalance());
        balance.setName(movieInfo.getName());
        balance.setDownloadDate(OgleHelper.getFormatDate(System.currentTimeMillis()));
        savePurchaseHistory(balance);
    }

    /**
     * hide android 9+ popup "Detected problems with API compatibility" dialog
     */
    private void disableAPIDialog(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) return;
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * get wifi address
     *
     * @return
     */
    public String getWifiAddress() {
        String ipAddr = "";
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ipAddr = (ipAddress & 0xFF) + "." +
                    ((ipAddress >> 8) & 0xFF) + "." +
                    ((ipAddress >> 16) & 0xFF) + "." +
                    (ipAddress >> 24 & 0xFF);
        }
        return ipAddr;
    }

    /**
     * get Mobile ip address
     *
     * @return
     */
    public static String getMobileIpAddress() {
        try {
            for (Enumeration en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("getMobileIpAddress", ex.toString());
        }
        return null;
    }

    /**
     * get local ip address
     *
     * @return
     */
    public String getIpAddress() {
        String ipAddr = "127.0.0.1";
        ConnectivityManager connectionManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) { //network connect
            if ("WIFI".equals(networkInfo.getTypeName())) {
                ipAddr = getWifiAddress();
            } else {
                ipAddr = getMobileIpAddress();
            }
        }
        return ipAddr;
    }

    public String getDevice() {
        String device = preferences.getString("device", "");
        if (TextUtils.isEmpty(device)) {
            String uuid = UUID.randomUUID().toString().toUpperCase();
            device = keyRegister + uuid.replace("-", "");
        }
        return device;
    }

    public String getMacAddress(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            WifiManager wimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            macAddress = wimanager.getConnectionInfo().getMacAddress();
        } else {
            macAddress = getMacAddressAndroid6();
        }
        Log.i("macaddress", "macAddress: " + macAddress);
        return macAddress;
    }

    private String getMacAddressAndroid6() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static Context getInstance() {
        return instance;
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    public boolean useExtensionRenderers() {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }

    public AccountLogin getAccountLogin() {
        return accountLogin;
    }

    public String endcodeURL(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public boolean isCheckNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void deleteCache() {
        try {
            File dir = getCacheDir();
            deleteDir(dir);

        } catch (Exception e) {
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void deleteFolder() {
        File[] dirFile = this.getExternalFilesDirs("");
        for (File file : dirFile) {
            if (file != null) {
                String[] children = file.list();
                for (String s : children) {
                    if (!s.equals("lib")) {
                        deleteDir(new File(file, s));
                    }
                }
            }
        }
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public void clearDataApp() {
        deleteAllMovieDownload();
        DataBaseHelper.getInstance().deleteAllData();
        clearApplicationData();
        deleteCache();
        deleteFolder();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();
    }

    public void deleteAllMovieDownload() {
        DownloadManager downloadManager = new DownloadManager(getContentResolver(), getPackageName());
        DataBaseHelper db = DataBaseHelper.getInstance();
        List<MovieDownload> listMovDown = db.getAllMovieDownload();
        if (listMovDown.size() > 0) {
            for (MovieDownload download : listMovDown) {
                int result = downloadManager.remove(download.getIdDownload());

                Log.i("deleteAllMovieDownload", "result = " + result);
            }
            db.deleteAllDownload();
        }
    }

    /**
     * get local mobile phone number
     * @return
     */
    public String getMobilePhoneNumber() {
        if (TextUtils.isEmpty(localPhoneNumber)) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    @SuppressLint("MissingPermission") List<SubscriptionInfo> subscription = SubscriptionManager.from(this).getActiveSubscriptionInfoList();
                    if (subscription != null) {
                        for (int i = 0; i < subscription.size(); i++) {
                            SubscriptionInfo info = subscription.get(i);
                            Log.d("GetPhoneNumber", "number " + info.getNumber()+", mcc: " + info.getMcc()+", iso: "+info.getCountryIso());
                            if (info.getNumber() != null && info.getNumber().length() > 0) {
                                localPhoneNumber = info.getNumber();
                                defaultMcc = info.getMcc();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("GetPhoneNumber", "getMobilePhoneNumber error");
            }
        }
        return localPhoneNumber;
    }

    public Phonenumber.PhoneNumber parsePhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, "");
            return swissNumberProto;
        } catch (Exception e) {
            Log.d("OgleApplication", "getCountryCode error: ", e);
        }
        return null;
    }

    /**
     * get default country code
     * @return
     */
    public String getDefaultCountryCode() {
        if (TextUtils.isEmpty(defaultCountryCode)) {
            if (!TextUtils.isEmpty(localPhoneNumber)) {
                Phonenumber.PhoneNumber phoneNumber = parsePhoneNumber(localPhoneNumber);
                if (phoneNumber != null) {
                    defaultCountryCode = String.valueOf(phoneNumber.getCountryCode());
                }
            } else {
                Country.getAll(this, null);
                boolean isMatch = false;
                Country country = null;
                String countryName = this.getResources().getConfiguration().locale.getCountry();
                if (countryName != null && !"".equals(countryName)) {
                    country = Country.getCountry(countryName);
                    switch (countryName.toUpperCase()) {
                        case "CN":
                            isMatch = true;
                            break;
                        case "CA":
                            isMatch = true;
                            break;
                        case "SB":
                            isMatch = true;
                            break;
                    }
                }

                if (isMatch && country != null) {
                    defaultCountryCode = String.valueOf(country.code);
                } else {
                    defaultCountryCode = "677";
                }
            }
        }
        return defaultCountryCode;
    }

    /**
     * get app upgrade path
     * @return
     */
    private String getAppUpgradePath() {
        String upgradePath = "";
        File[] externalDirs = getExternalFilesDirs("");
        for (File file : externalDirs) {
            if (file != null) {
                String path = file.getPath();
                if (path.indexOf("/emulated") >=0 && path.indexOf("/Android") >= 0) {
                    File dirFile = new File(path, "upgrade");
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    upgradePath = dirFile.getAbsolutePath();
                }
            }
        }
        return upgradePath;
    }

    /**
     * check app upgrade
     *
     * @param activity
     */
    public void checkAppUpgrade(Activity activity, UpdateCallback callback) {
        String path = getAppUpgradePath();
        Log.d("AppUpgrade", "upgrade path: " + path);
        new AppUpgradeManager
                .Builder()
                .setActivity(activity)
                .setTargetPath(path)
                .build()
                .update(callback == null? new UpdateCallback(): callback);
    }

    public boolean isVIPPlan() {
        int is_formal_plan = preferences.getInt("is_formal_plan", 0);
        if (is_formal_plan > 0) {
            return true;
        }
        return false;
    }

    public boolean isPurchasedVip() {
        long next_billing_date = preferences.getLong("next_billing_date", 0);
        if (next_billing_date > 0) {
            long next_billing_end = preferences.getLong("next_billing_end", 0);
            Log.d("CheckRight", "next_billing_end: "+next_billing_end +", runTime: "+SystemClock.elapsedRealtime());
            if (SystemClock.elapsedRealtime() < next_billing_end) {
                return true;
            }
        }
        return false;
    }

    /**
     * update account next billing date
     * @param nextBillingDate
     * @param serverTime
     */
    public void updateNextBillingDate(long nextBillingDate, long serverTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("next_billing_date", nextBillingDate);
        long next_billing_start = 0;
        long next_billing_end = 0;
        if (nextBillingDate > 0) {
            next_billing_start = SystemClock.elapsedRealtime();
            next_billing_end = next_billing_start + (nextBillingDate - serverTime);
        }
        editor.putLong("next_billing_start", next_billing_start);
        editor.putLong("next_billing_end", next_billing_end);
        Log.d("Application", "next_billing_start: "+ next_billing_start
                + ", next_billing_end: "+next_billing_end+", next_billing_date: "
                + nextBillingDate +", serverTime: "+serverTime);
        editor.commit();
    }

    /**
     * check account
     * @return
     */
    public boolean isAccountExpired() {
        long next_billing_date = preferences.getLong("next_billing_date", 0);
        int accountExpired = preferences.getInt("accountExpired", 0);
        Log.d("CheckRight", "next_billing_date: "+next_billing_date +", accountExpired: "+accountExpired);
        if (accountExpired > 0 && next_billing_date > 0) {
            return true;
        }
        return false;
    }

    /**
     * get user id
     * @return
     */
    public String getUserId() {
        return preferences.getString("userID", "000000");
    }

    public String getToken() {
        return preferences.getString("stringToken", "");
    }

    public String getFMAToken() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_id", accountLogin.getDevice());
        jsonObject.addProperty("player_type", playerType);
        jsonObject.addProperty("enc_accounting", OgleHelper.nullToEmpty(accountLogin.getAccountEncInfo()));
        return jsonObject.toString();
    }

    public String getBillingToken(int mvId) {
        MovieBilling movieBilling = movieBillingMap.get(String.valueOf(mvId));
        if (movieBilling != null) {
            return movieBilling.getRentalCertificate();
        }
        return "";
    }

    /**
     * show toast
     * @param msg
     * @param gravity
     */
    public void showToast(String msg, int gravity) {
        Toaster.makeLongToast(msg, gravity, 5000);
    }

    /**
     * get movie download result by downlaod ids
     * @param ids
     * @return
     */
    public List<DownloadResult> getMovieDownloadResultByIds(long... ids) {
        List<DownloadResult> results = new ArrayList<>();
        Cursor cursor = null;
        if (ids != null && ids.length > 0) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(ids);
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
                    int columnIndex1 = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int columnIndex2 = cursor.getColumnIndex(DownloadManager.COLUMN_MOVIEID);
                    int columnIndex3 = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    int columnIndex4 = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                    int columnIndex5 = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
                    int dowloadId = cursor.getInt(columnIndex);
                    int status = cursor.getInt(columnIndex1);
                    int mvId = cursor.getInt(columnIndex2);
                    int reason = cursor.getInt(columnIndex3);
                    String path = cursor.getString(columnIndex4);
                    String name = cursor.getString(columnIndex5);
                    DownloadResult downloadResult = new DownloadResult(mvId, dowloadId, name, status, path, reason);
                    results.add(downloadResult);
                } while(cursor.moveToNext());
                cursor.close();
            }
        }
        return results;
    }

    public boolean isDownloadSuccess(MovieDownload movieDownload) {
        if (movieDownload != null && movieDownload.getIdDownload() > 0 && movieDownload.getTypeDownload() == 3) {
            List<DownloadResult> results = getMovieDownloadResultByIds(movieDownload.getIdDownload());
            if (!results.isEmpty()) {
                switch (results.get(0).getStatus()) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        return true;
                }
            }
        }
        return false;
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    /**
     * when first play, then calculate expiry time
     */
    public void updateDownloadRentalTime(int mvId, boolean isPlaying) {
        MovieDownload movieDownload = DataBaseHelper.getInstance().getMovieDownload(mvId);
        if (isDownloadSuccess(movieDownload)) {
            if ((isPlaying && movieDownload.getExpireTime() == 0) || movieDownload.getTimeContinue() > 0) {
                if (movieDownload.getRentalPeriod() > 0) {
                    long expireTime = System.currentTimeMillis() + movieDownload.getRentalPeriod();
                    long rentalStart = SystemClock.elapsedRealtime();
                    long rentalEnd = rentalStart + movieDownload.getRentalPeriod();
                    Log.d("Download", "updateDownloadRentalTime currTime: "+new Date()+", rentalStart: "+
                            rentalStart+", rentalPeriod: "+movieDownload.getRentalPeriod()+", rentalEnd: "+rentalEnd);
                    DataBaseHelper.getInstance().updateDownloadExpiredTime(mvId, expireTime, rentalStart, rentalEnd);
                }
            }
        }
    }

    public void changeStorage(int type) {
        SharedPreferences.Editor editor = preferences.edit();
        isSDCard = type == 1? true: false;
        editor.putBoolean("isSDCard", isSDCard);
        editor.commit();
    }

    public String getPlanPrice() {
        String planPrice = preferences.getString("planPrice", "");
        if (!TextUtils.isEmpty(planPrice) && !planPrice.equals("0")) {
            if (planPrice.endsWith("$/Month")) {
                planPrice = String.format(getResources().getString(R.string.plan_price), planPrice.split(" ")[0]);
            } else if (!planPrice.startsWith("$")) {
                planPrice = String.format(getResources().getString(R.string.plan_price), planPrice);
            }
        } else {
            planPrice = "";
        }
        return planPrice;
    }

    public StorageEventListener getStorageEventListener() {
        return storageEventListener;
    }

    public void setStorageEventListener(StorageEventListener storageEventListener) {
        this.storageEventListener = storageEventListener;
    }

    public MovieBilling getMovieBillingInfo(int mvId) {
        return movieBillingMap.get(String.valueOf(mvId));
    }
}
