package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 6/13/2018.
 */

public class RechargeReponse {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private RechargePin rechargePin;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public RechargePin getData() {
        return rechargePin;
    }

    public void setData(RechargePin data) {
        this.rechargePin = data;
    }
}
