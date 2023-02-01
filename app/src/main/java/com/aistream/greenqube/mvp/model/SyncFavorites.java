package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 7/19/2018.
 */

public class SyncFavorites {
    @SerializedName("movieid")
    @Expose
    private Integer movieid;
    @SerializedName("favtype")
    @Expose
    private Integer favtype;
    @SerializedName("time")
    @Expose
    private long time;

    public SyncFavorites() {
    }

    public SyncFavorites(Integer idmv, int type, long time) {
        this.movieid = idmv;
        this.favtype = type;
        this.time = time;
    }

    public Integer getMovieid() {
        return movieid;
    }

    public void setMovieid(Integer movieid) {
        this.movieid = movieid;
    }

    public Integer getFavtype() {
        return favtype;
    }

    public void setFavtype(Integer favtype) {
        this.favtype = favtype;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
