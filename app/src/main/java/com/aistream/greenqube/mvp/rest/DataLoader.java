package com.aistream.greenqube.mvp.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.MovieList;
import com.aistream.greenqube.mvp.model.VideoType;
import com.aistream.greenqube.mvp.model.WifiInfo;
import com.aistream.greenqube.mvp.model.WifiList;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import retrofit2.Response;

public class DataLoader implements Serializable {
    private String TAG = this.getClass().getSimpleName();
    private DataBaseHelper db = DataBaseHelper.getInstance();
    private SharedPreferences mPref;
    private int countSendRequest;
    private int countAPISucc;
    private int requestLimit = 3;
    private boolean isLoading = false;
    private boolean expired = false;

    private Listener listener;

    public interface Listener {
        void success();

        void fail();

        void expired();
    }

    public DataLoader(Context context, Listener listener) {
        this.listener = listener;
        OgleApplication ogleApplication = (OgleApplication) context.getApplicationContext();
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        load();
    }

    public void load() {
        if (!isLoading) {
            isLoading = true;
            expired = false;
            countSendRequest = 0;
            countAPISucc = 0;
            getMovieList(true);
            getPromotionList();
            getWifiRouterList();
        }
    }

    private void endGetData(boolean flag) {
        countSendRequest++;
        if (flag) {
            countAPISucc++;
        }
        boolean isSucc = false;
        if (countSendRequest == requestLimit) {
            if (countSendRequest == countAPISucc) {
                isSucc = true;
            }

            if (listener != null) {
                if (isSucc) {
                    listener.success();
                } else {
                    if (!expired) {
                        listener.fail();
                    }
                }
            }
            isLoading = false;
        }
    }

    private void tokenExpired() {
        if (listener != null && !expired) {
            expired = true;
            listener.expired();
        }
        endGetData(false);
    }

    /**
     * get movie list
     */
    public void getMovieList(final boolean check) {
        APICall.getMovieList(new APIResultCallBack<MovieList>() {
            @Override
            public void before() {
                this.setIgnoreTokenExpired(true);
            }

            @Override
            public void onSuccess(MovieList body, Response response) {
                Log.i(TAG, "load movie List success!");
                Headers header = response.headers();
                String dateStr = header.get("Date");
                Date serverDate = new Date(dateStr);

                Map<Integer, MovieInfo> movieInfoMap = body.getMovieInfoMap();
                if (movieInfoMap != null && !movieInfoMap.isEmpty()) {
                    handleMovieList(movieInfoMap, serverDate);
                    db.deleteMovieList();
                    db.insertListMovie(movieInfoMap);
                }
                if (check) {
                    listener.success();
                }
                endGetData(true);
                mPref.edit().putInt("accountExpired", 0).commit();
            }

            @Override
            public void onError(int httpCode, MovieList body, Throwable t) {
                endGetData(false);
            }

            @Override
            public void onTokenExpired() {
                tokenExpired();
            }
        });
    }

    private void handleMovieList(Map<Integer, MovieInfo> movieInfoMap, Date serverDate) {
        Map<Integer, Genre> genreMap = new HashMap<>();
        Map<Integer, VideoType> videoTypeMap = new HashMap<>();
        for (Map.Entry<Integer, MovieInfo> entry : movieInfoMap.entrySet()) {
            MovieInfo movie = entry.getValue();
            List<Genre> genres = movie.getGenres();
            for (Genre genre : genres) {
                genreMap.put(genre.getId(), genre);
            }

            VideoType videoType = movie.getVideoType();
            if (movie.isShortVideo() && videoType != null) {
                videoTypeMap.put(videoType.getType(), videoType);
            }
        }

        if (!genreMap.isEmpty()) {
            //update genders
            db.deleteGenderList();
            db.insertListGenre(genreMap.values());
        }

        //save video types
        SharedPreferences.Editor editor = mPref.edit();
        String videoTypeStr = "";
        if (!videoTypeMap.isEmpty()) {
            videoTypeStr = new Gson().toJson(videoTypeMap.values());
        }
        editor.putString("VideoTypes", videoTypeStr).commit();
    }

    /**
     * get hot movie list
     */
    public void getHotMovieList() {
        APICall.getHotMovieList(new APIResultCallBack<MovieList>() {
            @Override
            public void onSuccess(MovieList body) {
                Log.i(TAG, "load hot movie List success!");
                Map<Integer, MovieInfo> movieInfoMap = body.getMovieInfoMap();
                if (movieInfoMap != null && !movieInfoMap.isEmpty()) {
                    db.deleteHotMovieList();
                    db.insertListHotMovie(movieInfoMap);
                }
            }
        });
    }

    /**
     * get promotion movies
     */
    public void getPromotionList() {
        APICall.getNewReleaseList(new APIResultCallBack<MovieList>() {
            @Override
            public void before() {
                this.setIgnoreTokenExpired(true);
            }

            @Override
            public void onSuccess(MovieList body) {
                Map<Integer, MovieInfo> movieInfoMap = body.getMovieInfoMap();
                Log.i(TAG, "load promotion movies success, promotionList: " + movieInfoMap.size());
                if (movieInfoMap != null && !movieInfoMap.isEmpty()) {
                    db.deleteNewRepleaseList();
                    db.insertListNewReplease(movieInfoMap);
                }
                endGetData(true);
            }

            @Override
            public void onError(int httpCode, MovieList body, Throwable t) {
                endGetData(false);
            }

            @Override
            public void onTokenExpired() {
                tokenExpired();
            }
        });
    }

    /**
     * get wifi movie list
     */
    public void getWifiRouterList() {
        APICall.getWifiRouterList(mPref.getFloat("longitude", 0),
                mPref.getFloat("latitude", 0),
                new APIResultCallBack<WifiList>() {
                    @Override
                    public void before() {
                        this.setIgnoreTokenExpired(true);
                    }

                    @Override
                    public void onSuccess(WifiList body) {
                        Log.i(TAG, "load wifi List success!");
                        List<WifiInfo> data = body.getData();
                        if (data != null && !data.isEmpty()) {
                            db.deleteListWifi();
                            db.insertListWifiList(data);
                        }
                        endGetData(true);
                    }

                    @Override
                    public void onError(int httpCode, WifiList body, Throwable t) {
                        endGetData(false);
                    }

                    @Override
                    public void onTokenExpired() {
                        tokenExpired();
                    }
                });
    }
}
