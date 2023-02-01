package com.aistream.greenqube.mvp.model;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PhuDepTraj on 5/9/2018.
 */

public class AccountLogin {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("device")
    @Expose
    private String device;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("token_expiry_time")
    @Expose
    private long tokenExpiryTime;
    @SerializedName("plan_id")
    @Expose
    private int planId;
    @SerializedName("plan_name")
    @Expose
    private String planName;
    @SerializedName("ad_pictures")
    @Expose
    private List<AdPicture> adPictures = null;
    @SerializedName("max_downloads")
    @Expose
    private int maxDownloads;
    @SerializedName("profile_completed")
    @Expose
    private int profileCompleted;
    @SerializedName("message")
    @Expose
    private Message message;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("is_permanent")
    @Expose
    private int is_permanent;

    @SerializedName("next_billing_date")
    @Expose
    private long next_billing_date;

    @SerializedName("plan_price")
    @Expose
    private String planPrice;

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("user_name")
    @Expose
    private String userName;

    @SerializedName("recharge_pin")
    @Expose
    private String voucherCode;

    @SerializedName("phone")
    @Expose
    private String phoneNumber;

    @SerializedName("gender")
    @Expose
    private int gender;

    @SerializedName("birthday")
    @Expose
    private String birthday;

    @SerializedName("is_verified")
    @Expose
    private Integer verified;

    @SerializedName("vip_plans")
    @Expose
    private List<PlanInfo> vipPlanList;

    @SerializedName("balance")
    @Expose
    private String balance;

    @SerializedName("enc_accounting")
    @Expose
    private String accountEncInfo;

    @SerializedName("support_contacts")
    @Expose
    private Map<String, String> supportContactMsgs = new HashMap<>();

    @SerializedName("available_downloads")
    @Expose
    private int availableDownloads;

    public String getRechargeInfo() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device", device);
        jsonObject.addProperty("phone", phoneNumber);
        jsonObject.addProperty("recharge_pin", voucherCode);
        jsonObject.addProperty("enc_accounting", accountEncInfo);
        jsonObject.addProperty("user_name", userName);
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("gender", gender);
        jsonObject.addProperty("birthday", birthday);
        return jsonObject.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTokenExpiryTime() {
        return tokenExpiryTime;
    }

    public void setTokenExpiryTime(long tokenExpiryTime) {
        this.tokenExpiryTime = tokenExpiryTime;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public List<AdPicture> getAdPictures() {
        return adPictures;
    }

    public void setAdPictures(List<AdPicture> adPictures) {
        this.adPictures = adPictures;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getMaxDownloads() {
        return maxDownloads;
    }

    public void setMaxDownloads(int maxDownloads) {
        this.maxDownloads = maxDownloads;
    }

    public int getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(int profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIs_permanent() {
        return is_permanent;
    }

    public void setIs_permanent(int is_permanent) {
        this.is_permanent = is_permanent;
    }

    public long getNext_billing_date() {
        return next_billing_date;
    }

    public void setNext_billing_date(long next_billing_date) {
        this.next_billing_date = next_billing_date;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.planPrice = planPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public void addVoucherCode(String voucherCode) {
        if (!TextUtils.isEmpty(this.voucherCode)) {
            if (!this.voucherCode.contains(voucherCode)) {
                this.voucherCode += "," + voucherCode;

                String[] vcodes = this.voucherCode.split(",");
                if (vcodes.length > 5) {
                    int startIndex = vcodes.length - 5;
                    this.voucherCode = vcodes[startIndex];
                    for (int i = startIndex + 1; i < vcodes.length; i++) {
                        this.voucherCode += "," + vcodes[i];
                    }
                }
            }
        } else {
            this.voucherCode = voucherCode;
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getVerified() {
        return verified;
    }

    public void setVerified(Integer verified) {
        this.verified = verified;
    }

    public List<PlanInfo> getVipPlanList() {
        return vipPlanList;
    }

    public void setVipPlanList(List<PlanInfo> vipPlanList) {
        this.vipPlanList = vipPlanList;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAccountEncInfo() {
        return accountEncInfo;
    }

    public void setAccountEncInfo(String accountEncInfo) {
        this.accountEncInfo = accountEncInfo;
    }

    public Map<String, String> getSupportContactMsgs() {
        return supportContactMsgs;
    }

    public void setSupportContactMsgs(Map<String, String> supportContactMsgs) {
        this.supportContactMsgs = supportContactMsgs;
    }

    public int getAvailableDownloads() {
        return availableDownloads;
    }

    public void setAvailableDownloads(int availableDownloads) {
        this.availableDownloads = availableDownloads;
    }
}
