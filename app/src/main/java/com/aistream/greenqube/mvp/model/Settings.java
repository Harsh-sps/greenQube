package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 5/16/2017.
 */

public class Settings {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("picURLPath")
    @Expose
    private String picURLPath;
    @SerializedName("picURLPathLocal")
    @Expose
    private String picURLPathLocal;
    @SerializedName("movieURLPath")
    @Expose
    private String movieURLPath;
    @SerializedName("movieURLPathPOC")
    @Expose
    private String movieURLPathPOC;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPicURLPath() {
        return picURLPath;
    }

    public void setPicURLPath(String picURLPath) {
        this.picURLPath = picURLPath;
    }

    public String getMovieURLPath() {
        return movieURLPath;
    }

    public void setMovieURLPath(String movieURLPath) {
        this.movieURLPath = movieURLPath;
    }

    public String getMovieURLPathPOC() {
        return movieURLPathPOC;
    }

    public void setMovieURLPathPOC(String movieURLPathPOC) {
        this.movieURLPathPOC = movieURLPathPOC;
    }

    public String getPicURLPathLocal() {
        return picURLPathLocal;
    }

    public void setPicURLPathLocal(String picURLPathLocal) {
        this.picURLPathLocal = picURLPathLocal;
    }
}
