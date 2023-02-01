package com.aistream.greenqube.mvp.presenter;

import android.content.Context;
import android.util.Log;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.VideoType;
import com.aistream.greenqube.mvp.view.ViewLibrary;
import com.aistream.greenqube.services.DownloadManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 5/17/2017.
 */

public class PresenterLibraryImp {

    public static final int ALL = 0;
    public static final int FREE_PREMIUM = 1;
    public static final int VIP = 2;

    private Context mContext;
    private ViewLibrary viewLibrary;
    private DataBaseHelper dataBaseHelper;
    private PresenterMainImp presenterMainImp;
    private VideoType videoType;
    private int type;   // 0: all  1: free and premium  3: vip

    public PresenterLibraryImp(Context cont, ViewLibrary viewLibrary, PresenterMainImp presenterMainImp, VideoType videoType, int type) {
        this.mContext = cont;
        this.viewLibrary = viewLibrary;
        this.presenterMainImp = presenterMainImp;
        this.videoType = videoType;
        dataBaseHelper = DataBaseHelper.getInstance();
        this.type = type;
    }

    /**
     * load genre's movies by genreid
     * @param genId
     * @return
     */
    public List<MovieInfo> loadMovieFromGen(int genId) {
        List<MovieInfo> movieList = new ArrayList<>();
        if (genId == PresenterMainImp.ALL) {
            movieList = presenterMainImp.getListMovie();
        } else if (genId == PresenterMainImp.NEW_RELEASES) {
            movieList = presenterMainImp.getListNewRelease();
        } else {
            movieList = presenterMainImp.loadMovieFromGen(genId);
        }
        List<MovieInfo> genreMovies = new ArrayList<>();
        if (movieList != null && !movieList.isEmpty()) {
            for (MovieInfo movie: movieList) {
                if (movie.isMatchType(videoType) && isMatchType(movie)) {
                    genreMovies.add(movie);
                }
            }
        }

        //handle new release
        if (genId == PresenterMainImp.NEW_RELEASES) {
            if (genreMovies.size() > 20) {
                genreMovies = genreMovies.subList(0, 20);
            }
        }
        return genreMovies;
    }

    private boolean isMatchType(MovieInfo movie) {
        boolean isMatch = false;
        switch (type) {
            case ALL:
                isMatch = true;
                break;
            case FREE_PREMIUM:
                if (!movie.isVip()) {
                    isMatch = true;
                }
                break;
            case VIP:
                if (movie.isVip()) {
                    isMatch = true;
                }
                break;
        }
        return isMatch;
    }

    public void onBackPress(int type) {
        viewLibrary.showHideFrameAllMov(type, 1000, "");
    }

    /**
     * load each genre's movies
     */
    public void loadMovieOfGrene() {
        List<Genre> genreList = presenterMainImp.getGenreList();
        for (Genre classification : genreList) {
            Integer genreId = classification.getId();
            if (genreId != null && genreId != 0) {
                viewLibrary.showListMovieOfGenre(loadMovieFromGen(classification.getId()), classification);
            }
        }
    }

    /**
     * load continue watch movies
     */
    public void loadContinueWatching() {
        List<MovieDownload> listContinues = new ArrayList<>();
        List<MovieDownload> listContinueMovie = dataBaseHelper.getAllMovieDownload();
        Log.i("ContinueWatching", "List Movie Continue: " + listContinueMovie.size());
        if (listContinueMovie.size() > 0) {
            for (MovieDownload movieDownload : listContinueMovie) {
                MovieInfo movieInfo = presenterMainImp.getMovieInfo(movieDownload.getMvId());
                if (movieInfo != null && movieDownload.getTypeDownload() == 3 &&
                        movieDownload.getStatus() == DownloadManager.STATUS_SUCCESSFUL &&
                        movieDownload.getTimeContinue() > 0 &&
                        !movieDownload.hasExpired()) {
                    listContinues.add(movieDownload);
                }
            }
        }
        viewLibrary.showContinueWatching(listContinues);
    }

    /**
     * load promotions movies
     */
    public void loadPromotionMovies() {
        List<MovieInfo> promotionMovies = presenterMainImp.getPromotionMovies();
        List<MovieInfo> movies = new ArrayList<>();
        if (promotionMovies != null && !promotionMovies.isEmpty()) {
            for (MovieInfo movie: promotionMovies) {
                if (isMatchType(movie)) {
                    movies.add(movie);
                }
            }
        }

        if (movies.size() > 5) {
            movies = movies.subList(0, 5);
        }
        viewLibrary.showMoviePromotion(movies);
    }

    public void showDataLibrary() {
        loadMovieOfGrene();
        if (videoType == null) {
            loadPromotionMovies();
        }
        loadContinueWatching();
    }
}
