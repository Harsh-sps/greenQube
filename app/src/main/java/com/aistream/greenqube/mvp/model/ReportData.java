package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 8/29/2018.
 */

public class ReportData {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("app_name")
    @Expose
    private String appName;
    @SerializedName("app_version")
    @Expose
    private String appVersion;
    @SerializedName("device")
    @Expose
    private String device;
    @SerializedName("os")
    @Expose
    private String os;
    @SerializedName("os_version")
    @Expose
    private String osVersion;
    @SerializedName("unique_id")
    @Expose
    private String uniqueId;
    @SerializedName("data")
    @Expose
    private ItemBackupData data;

    public ReportData(String userId, String appName, String appVersion, String device,
                      String os, String osVersion, String uniqueId, ItemBackupData data) {
        this.userId = userId;
        this.appName = appName;
        this.appVersion = appVersion;
        this.device = device;
        this.os = os;
        this.osVersion = osVersion;
        this.uniqueId = uniqueId;
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public ItemBackupData getData() {
        return data;
    }

    public void setData(ItemBackupData data) {
        this.data = data;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }


}
