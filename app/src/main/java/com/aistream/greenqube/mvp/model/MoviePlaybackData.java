package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 9/20/2018.
 */

public class MoviePlaybackData {
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("content_id")
    @Expose
    private Integer contentId;
    @SerializedName("content_start")
    @Expose
    private String contentStart;
    @SerializedName("content_end")
    @Expose
    private String contentEnd;
    @SerializedName("play_start_time")
    @Expose
    private long playStartTime;
    @SerializedName("play_end_time")
    @Expose
    private long playEndTime;
    @SerializedName("is_full_play")
    @Expose
    private Integer isFullPlay;

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

    public String getContentStart() {
        return contentStart;
    }

    public void setContentStart(String contentStart) {
        this.contentStart = contentStart;
    }

    public String getContentEnd() {
        return contentEnd;
    }

    public void setContentEnd(String contentEnd) {
        this.contentEnd = contentEnd;
    }

    public long getPlayStartTime() {
        return playStartTime;
    }

    public void setPlayStartTime(long playStartTime) {
        this.playStartTime = playStartTime;
    }

    public long getPlayEndTime() {
        return playEndTime;
    }

    public void setPlayEndTime(long playEndTime) {
        this.playEndTime = playEndTime;
    }

    public Integer getIsFullPlay() {
        return isFullPlay;
    }

    public void setIsFullPlay(Integer isFullPlay) {
        this.isFullPlay = isFullPlay;
    }
}
