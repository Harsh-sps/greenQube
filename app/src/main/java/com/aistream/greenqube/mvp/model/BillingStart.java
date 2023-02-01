package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 7/9/2018.
 */

public class BillingStart {
    @SerializedName("drm_key")
    @Expose
    private String drmKey;
    @SerializedName("expiry_times")
    @Expose
    private long expiryTimes;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("balance")
    @Expose
    private Double balance;

    @SerializedName("available_downloads")
    @Expose
    private int availableDownloads;

    @SerializedName("enc_accounting")
    @Expose
    private String accountEncInfo;

    @SerializedName("enc_credential")
    @Expose
    private String billingCredential;

    public String getDrmKey() {
        return drmKey;
    }

    public void setDrmKey(String drmKey) {
        this.drmKey = drmKey;
    }

    public long getExpiryTimes() {
        return expiryTimes;
    }

    public void setExpiryTimes(long expiryTimes) {
        this.expiryTimes = expiryTimes;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public int getAvailableDownloads() {
        return availableDownloads;
    }

    public void setAvailableDownloads(int availableDownloads) {
        this.availableDownloads = availableDownloads;
    }

    public String getAccountEncInfo() {
        return accountEncInfo;
    }

    public void setAccountEncInfo(String accountEncInfo) {
        this.accountEncInfo = accountEncInfo;
    }

    public String getBillingCredential() {
        return billingCredential;
    }

    public void setBillingCredential(String billingCredential) {
        this.billingCredential = billingCredential;
    }
}
