package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoType implements Serializable {

    public static final int MOVIE_TYPE = 0;

    @SerializedName("id")
    @Expose
    private int type;

    @SerializedName("name")
    @Expose
    private String name;

    public VideoType() {
    }

    public VideoType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
