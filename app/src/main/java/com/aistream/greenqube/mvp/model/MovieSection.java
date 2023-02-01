package com.aistream.greenqube.mvp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/19/2017.
 */

public class MovieSection {
    private int genreId;
    private String genName;
    private List<MovieInfo> movieInfoListDisplay = new ArrayList<>();

    public MovieSection(int genreId, String genName, List<MovieInfo> movieInfoList) {
        this.genreId = genreId;
        this.genName = genName;
        this.movieInfoListDisplay = movieInfoList;
    }

    public int getGenreId() {
        return genreId;
    }

    public String getGenName() {
        return genName;
    }


    public List<MovieInfo> getMovieInfoList() {
        return movieInfoListDisplay;
    }

}
