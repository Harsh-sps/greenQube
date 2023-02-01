package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhuDepTraj on 9/18/2018.
 */

public class ResponDownloadRight {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private DownloadRight data;

    @SerializedName("wifiList")
    @Expose
    private List<WifiInfo> wifiList = new ArrayList<>();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public DownloadRight getData() {
        return data;
    }

    public void setData(DownloadRight data) {
        this.data = data;
    }

    public List<WifiInfo> getWifiList() {
        return wifiList;
    }

    public void setWifiList(List<WifiInfo> wifiList) {
        this.wifiList = wifiList;
    }
}
