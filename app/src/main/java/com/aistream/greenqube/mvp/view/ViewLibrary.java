package com.aistream.greenqube.mvp.view;

import android.widget.TextView;

import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;

import java.util.List;

/**
 * Created by Administrator on 5/17/2017.
 */

public interface ViewLibrary {

    void showHideFrameAllMov(int mode, int genID, String genName);

    void showMoviePromotion(List<MovieInfo> listMoviePromotion);

    void showContinueWatching(List<MovieDownload> listContinue);

    void showMovieDetail(MovieInfo movieInfo);

    void showListMovieOfGenre(List<MovieInfo> movieInfoList, Genre genre);

    void onBackpress();

    void showMovieVIP(TextView view, MovieInfo movieInfo);

    void loadDataLibrary();

    void hideParentFragment();

    void showParentFragment();

    void refreshData();

    List<MovieInfo> loadMovieFromGen(int genreId);

    boolean checkVisbleFrameSearch();
}
