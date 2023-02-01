package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 7/19/2018.
 */

public class ResponSyncFavorite {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private UpdateFavorite data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UpdateFavorite getData() {
        return data;
    }

    public void setData(UpdateFavorite data) {
        this.data = data;
    }
}
