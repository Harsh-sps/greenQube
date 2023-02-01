package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponDetectFMS {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private List<DetectFMS> data = null;

    /**
     * No args constructor for use in serialization
     */
    public ResponDetectFMS() {
    }

    /**
     * @param status
     * @param data
     */
    public ResponDetectFMS(Status status, List<DetectFMS> data) {
        super();
        this.status = status;
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<DetectFMS> getData() {
        return data;
    }

    public void setData(List<DetectFMS> data) {
        this.data = data;
    }
}
