package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by PhuDepTraj on 5/3/2018.
 */

public class MovieList {
    @SerializedName("status")
    @Expose
    private Status status;

    public void MovieListNew() {
        status = new Status();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
//
//    public List<MovieInfo> getData() {
//        return data;
//    }

//    public void setData(List<MovieList> data) {
//        this.data = data;
//    }

    private Map<Integer, MovieInfo> movieInfoMap = null;

    public Map<Integer, MovieInfo> getMovieInfoMap() {
        return movieInfoMap;
    }

    public void setMovieInfoMap(Map<Integer, MovieInfo> movieInfoMap) {
        this.movieInfoMap = movieInfoMap;
    }
}
