package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 9/20/2018.
 */

public class DownloadData {
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("content_id")
    @Expose
    private Integer contentId;
    @SerializedName("speed")
    @Expose
    private Integer speed;
    @SerializedName("expire_time")
    @Expose
    private long expireTime;
    @SerializedName("pause_times")
    @Expose
    private long pauseTimes;
    @SerializedName("start_time")
    @Expose
    private long startTime;
    @SerializedName("end_time")
    @Expose
    private long endTime;
    @SerializedName("download_size")
    @Expose
    private Integer downloadSize;
    @SerializedName("download_from")//wifi name
    @Expose
    private String downloadFrom;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getPauseTimes() {
        return pauseTimes;
    }

    public void setPauseTimes(long pauseTimes) {
        this.pauseTimes = pauseTimes;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Integer getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(Integer downloadSize) {
        this.downloadSize = downloadSize;
    }

    public String getDownloadFrom() {
        return downloadFrom;
    }

    public void setDownloadFrom(String downloadFrom) {
        this.downloadFrom = downloadFrom;
    }
}
