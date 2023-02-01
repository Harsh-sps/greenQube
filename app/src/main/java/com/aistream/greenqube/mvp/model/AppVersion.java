package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 8/2/2017.
 */

public class AppVersion {
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("verandroid")
    @Expose
    private String verandroid;
    @SerializedName("vcandroid")
    @Expose
    private int vcandroid;
    @SerializedName("apkuri")
    @Expose
    private String apkuri;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getVerandroid() {
        return verandroid;
    }

    public void setVerandroid(String verandroid) {
        this.verandroid = verandroid;
    }

    public int getVcandroid() {
        return vcandroid;
    }

    public void setVcandroid(int vcandroid) {
        this.vcandroid = vcandroid;
    }

    public String getApkuri() {
        return apkuri;
    }

    public void setApkuri(String apkuri) {
        this.apkuri = apkuri;
    }
}
