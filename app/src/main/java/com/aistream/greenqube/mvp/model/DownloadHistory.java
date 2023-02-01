package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 8/29/2018.
 */

public class DownloadHistory {
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("content_id")
    @Expose
    private Integer contentId;
    @SerializedName("speed")
    @Expose
    private Integer speed;
    @SerializedName("pause_times")
    @Expose
    private long pauseTimes;
    @SerializedName("start_time")
    @Expose
    private long startTime;
    @SerializedName("end_time")
    @Expose
    private long endTime;

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
}
