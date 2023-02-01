package com.aistream.greenqube.mvp.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.WifiInfo;

import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Administrator on 5/18/2017.
 */

public class Config {
    //public static String[] DOMAINS = {"sunderban.aistream.tv", "sunderban.aistream.tv"};
    public static String[] DOMAINS = {"konnekt.aistream.tv", "konnekt.aistream.tv"};
    public static String[] FAG_SERVERS = {"https://" + DOMAINS[0], "https://" +DOMAINS[1]};
    public static String domain = DOMAINS[0];
    public static String[] FMSIPS;
    public static int SELECT_FMS_INDEX = 0;
    public static String apiEndpointPublic = "https://" + domain;
    public static String apiEndpointPublicDownM3U8 = "http://" + domain + ":88/";
    public static String picURLPath = apiEndpointPublicDownM3U8;
    public static boolean isEndpointPublic = true;

    public static String macAddress = "";

    public static boolean isAllowDownloadLocal = false;

    public static  boolean isAllowDownload = false;

    public static WifiInfo currWifiInfo;

    /**
     * change fma domain
     */
    public static void changeFMA(String server) {
        if (!TextUtils.isEmpty(server) && server.startsWith("https://")) {
            domain = server.substring("https://".length());
        }
    }

    /**
     * FMS HA
     */
    public static void changeFMS() {
        if (isAllowDownloadLocal && FMSIPS != null && FMSIPS.length > 1) {
            if (SELECT_FMS_INDEX + 1 >= FMSIPS.length) {
                SELECT_FMS_INDEX = 0;
            } else {
                SELECT_FMS_INDEX += 1;
            }
        }
    }

    /**
     * get public download url
     * @return
     */
    public static String getPublicDownLoadUrl() {
        String url = apiEndpointPublic;
        if (isAllowDownloadLocal && FMSIPS != null) {
            url = "https://"+FMSIPS[SELECT_FMS_INDEX] + "/";
        }
        return url;
    }

    /**
     * get public download m3u8 url
     * @return
     */
    public static String getPublicDownLoadM3u8Url() {
        String url = apiEndpointPublicDownM3U8;
        if (isAllowDownloadLocal && FMSIPS != null) {
            url = "http://"+FMSIPS[SELECT_FMS_INDEX] + "/";
        }
        return url;
    }

    public static String getDownloadUrl(String requestUrl) {
        //http://api.oglepoc.flixsys.com:88/Carnival-Story-1954-800k/Carnival-Story-1954-800k.ts
        String uri = requestUrl.substring("http://".length());
        return getPublicDownLoadM3u8Url() + uri.substring(uri.indexOf("/") + 1);
    }

    public static boolean isOgleWifi(Context context, NetworkInfo info, List<WifiInfo> wifiInfoList) {
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                if (wifiInfoList != null) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                    String bssid = wifiManager.getConnectionInfo().getBSSID();
                    String mac = bssid.toUpperCase();
                    Log.i("DownloadMovie", "Config - Mac ACP: " + mac);
                    for (WifiInfo wifiIn : wifiInfoList) {
                        Log.i("DownloadMovie", "Config - Mac Access: " + wifiIn.getMac() + " - mac5g: " + wifiIn.getMac5g());
                        if (wifiIn.getMac().replace(" ", "").toUpperCase().equals(mac)
                                || wifiIn.getMac5g().replace(" ", "").toUpperCase().equals(mac)) {
                            currWifiInfo = wifiIn;
                            isAllowDownload = true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * check whether connect FMA WIFI
     * @return
     */
    public static boolean isCheckMac() {
        Log.i("DownloadMovie", "Check Mac, isAllowDownload: " + isAllowDownload);
        if (isAllowDownload) return true;
        List<WifiInfo> wifiInfoList = DataBaseHelper.getInstance().getWifiInfoList();
        Context context = OgleApplication.getInstance().getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                if (wifiInfoList != null) {
                    if (Config.isAllowDownloadLocal) {
                        isAllowDownload = true;
                        return true;
                    } else {
                        return isOgleWifi(context, info, wifiInfoList);
                    }
                }
            }
        }
        return false;
    }
}
