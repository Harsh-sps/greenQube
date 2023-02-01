package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PhuDepTraj on 7/11/2018.
 */

public class BalanceRespon {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private List<Balance> liBalan = null;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Balance> getData() {
        return liBalan;
    }

    public void setData(List<Balance> data) {
        this.liBalan = data;
    }
}
