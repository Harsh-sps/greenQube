package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 6/13/2018.
 */

public class RechargePin {
    @SerializedName("drm_key")
    @Expose
    private String drmKey;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("balance")
    @Expose
    private String balance;

    @SerializedName("next_billing_date")
    @Expose
    private long next_billing_date;

    @SerializedName("enc_accounting")
    @Expose
    private String accountEncInfo;

    @SerializedName("credit_downloads")
    @Expose
    private int creditDownloads;

    @SerializedName("available_downloads")
    @Expose
    private int availableDownloads;

    public String getDrmKey() {
        return drmKey;
    }

    public void setDrmKey(String drmKey) {
        this.drmKey = drmKey;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public long getNext_billing_date() {
        return next_billing_date;
    }

    public void setNext_billing_date(long next_billing_date) {
        this.next_billing_date = next_billing_date;
    }

    public String getAccountEncInfo() {
        return accountEncInfo;
    }

    public void setAccountEncInfo(String accountEncInfo) {
        this.accountEncInfo = accountEncInfo;
    }

    public int getCreditDownloads() {
        return creditDownloads;
    }

    public void setCreditDownloads(int creditDownloads) {
        this.creditDownloads = creditDownloads;
    }

    public int getAvailableDownloads() {
        return availableDownloads;
    }

    public void setAvailableDownloads(int availableDownloads) {
        this.availableDownloads = availableDownloads;
    }
}
