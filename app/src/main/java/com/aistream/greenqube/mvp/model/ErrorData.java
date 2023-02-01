package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 9/20/2018.
 */

public class ErrorData {
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("err_no")
    @Expose
    private String errNo;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("err_time")
    @Expose
    private long errTime;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getErrNo() {
        return errNo;
    }

    public void setErrNo(String errNo) {
        this.errNo = errNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getErrTime() {
        return errTime;
    }

    public void setErrTime(long errTime) {
        this.errTime = errTime;
    }
}
