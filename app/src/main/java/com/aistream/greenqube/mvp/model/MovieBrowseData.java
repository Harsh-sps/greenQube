package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 8/29/2018.
 */

public class MovieBrowseData {
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("content_id")
    @Expose
    private Integer contentId;
    @SerializedName("browse_time")
    @Expose
    private long browseTime;

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

    public long getBrowseTime() {
        return browseTime;
    }

    public void setBrowseTime(long browseTime) {
        this.browseTime = browseTime;
    }

}
