package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 5/16/2017.
 */

public class GenreList {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("genrelist")
    @Expose
    private List<Genre> genrelist = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<Genre> getGenrelist() {
        return genrelist;
    }

    public void setGenrelist(List<Genre> genrelist) {
        this.genrelist = genrelist;
    }

}
