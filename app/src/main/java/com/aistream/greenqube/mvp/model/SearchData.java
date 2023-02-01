package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 8/29/2018.
 */

public class SearchData {
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("movie_nums")
    @Expose
    private Integer movieNums;
    @SerializedName("search_time")
    @Expose
    private long searchTime;

//    public SearchData(Integer type, String item, Integer movieNums, long searchTime) {
//        this.type = type;
//        this.item = item;
//        this.movieNums = movieNums;
//        this.searchTime = searchTime;
//    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getMovieNums() {
        return movieNums;
    }

    public void setMovieNums(Integer movieNums) {
        this.movieNums = movieNums;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

}
