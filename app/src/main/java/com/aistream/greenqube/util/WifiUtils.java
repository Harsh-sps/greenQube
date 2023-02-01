package com.aistream.greenqube.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiUtils {
    private static final String TAG = "WIFIUtils";
    private WifiManager mWifiManager;
    //current connect wifi info
    private WifiInfo mWifiInfo;
    //scan wifi list
    private List<ScanResult> mWifiList;

    public WifiUtils(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    private void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * add wifi and connect
     * @param wcg
     * @return
     */
    private boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        Log.d(TAG, "addNetwork wcgID: "+wcgID);
        //connect wifi
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        mWifiManager.saveConfiguration();
        Log.d(TAG, "enableNetwork result: "+b);
        return b;
    }

    /**
     * disconnect wifi
     * @param netId
     */
    private void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    /**
     * switch wifi
     * @param ssid
     * @param password
     */
    public boolean switchWifi(String ssid, String password) {
        openWifi();
        String curr_ssid = getSSID();
        if (TextUtils.isEmpty(ssid) || (!TextUtils.isEmpty(curr_ssid) && curr_ssid.equals(ssid))) {
            return false;
        }

        Log.d(TAG, "switchWifi ssid: "+ssid);
        ScanResult scanResult = getWifiScanResultBySSID(ssid);
        Log.d(TAG, "getWifiScanResultByBssid scanResult: "+scanResult);
        if (scanResult != null) {
            clearOldNetwork(scanResult.SSID);
            //connect wifi
            WifiConfiguration wifiConfig = createWifiInfo(scanResult.SSID, password, getSecurity(scanResult));
            return this.addNetwork(wifiConfig);
        } else {
            Log.d(TAG, "can not find the wifi, ssid: "+ssid);
        }
        return false;
    }

    /**
     * get scan wifi by ssid
     * @param ssid
     * @return
     */
    private ScanResult getWifiScanResultBySSID(String ssid) {
        ScanResult wifiResult = null;
        List<ScanResult> scanResults = scanWifiList();
        if (scanResults != null && !TextUtils.isEmpty(ssid)) {
            for(ScanResult scanResult: scanResults) {
                Log.d(TAG, "scanResult bssid: "+scanResult.BSSID+", ssid: "+scanResult.SSID);
                if (ssid.equals(scanResult.SSID)) {
                    wifiResult = scanResult;
                }
            }
        }
        return wifiResult;
    }

    /**
     * scan wifi list
     * @return
     */
    public List<ScanResult> scanWifiList() {
        List<ScanResult> scanWifiList = mWifiManager.getScanResults();
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }
        return wifiList;
    }

    /**
     * get security type
     * @param result
     * @return
     */
    private int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return 2;
        } else if (result.capabilities.contains("WPA")) {
            return 3;
        }
        return 1;
    }

    /**
     * create wifi info
     * @param wifi_ssid  ssid name
     * @param password   ssid network password
     * @param secure_type  1: no-password  2: WEP password auth  3: wap/wap2 psk password auth
     * @return
     */
    private WifiConfiguration createWifiInfo(String wifi_ssid, String password, int secure_type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + wifi_ssid + "\"";

        if (secure_type == 1) {
            // WIFICIPHER_NOPASS
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (secure_type == 2) {
            // WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (secure_type == 3) {
            // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * clear old network
     * @param ssid
     * @return
     */
    private void clearOldNetwork(String ssid) {
        List<WifiConfiguration> configNetworks = mWifiManager.getConfiguredNetworks();
        if (configNetworks != null && !TextUtils.isEmpty(ssid)) {
            for (WifiConfiguration wifiConfig : configNetworks) {
                Log.d(TAG, "wifiConfig ssid: "+wifiConfig.SSID+", bssid: "+wifiConfig.BSSID+", networkId: "+wifiConfig.networkId);
                if (wifiConfig.SSID.equals("\"" + ssid + "\"")) {
                    Log.d(TAG, "removeNetwork ssid: "+wifiConfig.SSID+", networkId: "+wifiConfig.networkId);
                    mWifiManager.removeNetwork(wifiConfig.networkId);
                }
            }
        }
    }

    /**
     * get current connect wifi bssid
     * @return
     */
    public String getBSSID() {
        return (mWifiInfo == null) ? null : mWifiInfo.getBSSID();
    }

    public String getSSID() {
        return (mWifiInfo == null) ? null : mWifiInfo.getSSID();
    }

    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }
}
