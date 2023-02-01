package com.aistream.greenqube.mvp.view;

import com.aistream.greenqube.mvp.model.MovieDownload;

import java.util.List;

/**
 * Created by PhuDepTraj on 5/14/2018.
 */

public interface ViewFilter {
    void showFilterMovieOfGenre(int genID, String genreName);
    void showAllMovieContinue(List<MovieDownload> listContinue);
}
