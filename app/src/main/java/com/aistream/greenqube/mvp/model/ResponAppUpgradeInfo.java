package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class ResponAppUpgradeInfo {

    @SerializedName("status")
    @Expose
    private Status status;

    @SerializedName("data")
    @Expose
    private AppUpgradeInfo appUpgradeInfo;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public AppUpgradeInfo getAppUpgradeInfo() {
        return appUpgradeInfo;
    }

    public void setAppUpgradeInfo(AppUpgradeInfo appUpgradeInfo) {
        this.appUpgradeInfo = appUpgradeInfo;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            if (status != null) {
                JSONObject statusJson = new JSONObject();
                statusJson.put("code", (int)status.getCode());
                statusJson.put("message", status.getMessage());
                json.put("status", statusJson);
            }

            if (appUpgradeInfo != null) {
                JSONObject dataJson = new JSONObject();
                dataJson.put("apk_uri", appUpgradeInfo.getApkUrl());
                dataJson.put("version", appUpgradeInfo.getVersion());
                dataJson.put("version_code", appUpgradeInfo.getVersionCode());
                json.put("data", dataJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
