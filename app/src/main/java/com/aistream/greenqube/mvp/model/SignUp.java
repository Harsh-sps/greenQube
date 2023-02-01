package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PhuDepTraj on 8/27/2018.
 */

public class SignUp {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("token_expiry_times")
    @Expose
    private Integer tokenExpiryTimes;
    @SerializedName("plan_id")
    @Expose
    private Integer planId;
    @SerializedName("plan_name")
    @Expose
    private String planName;
    @SerializedName("ad_pictures")
    @Expose
    private List<AdPictureSignUp> adPictures = null;

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

    public Integer getTokenExpiryTimes() {
        return tokenExpiryTimes;
    }

    public void setTokenExpiryTimes(Integer tokenExpiryTimes) {
        this.tokenExpiryTimes = tokenExpiryTimes;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public List<AdPictureSignUp> getAdPictures() {
        return adPictures;
    }

    public void setAdPictures(List<AdPictureSignUp> adPictures) {
        this.adPictures = adPictures;
    }
}
