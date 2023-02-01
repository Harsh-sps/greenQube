package com.aistream.greenqube.mvp.rest;

import com.aistream.greenqube.mvp.model.ResponseWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 8/2/2017.
 */

public interface ApiServiceLocal{
    
//    @GET("json/v1.0.1/getsettings.json")
//    Call<ResponseWrapper<Settings>> getSettings();
//
//    @GET("json/v1.0.1/getgenrelist.json")
//    Call<ResponseWrapper<GenreList>> getGenreList();
//
//    @GET("json/v1.0.1/getmovienr.json")
//    Call<ResponseWrapper<Quantity>> getMovieNr();
//
//    @GET("json/v1.0.1/getmovielist.json")
//    Call<ResponseWrapper<MovieList>> getMovieList();
//
//    @GET("json/v1.0.1/getwifilist.json")
//    Call<ResponseWrapper<WifiList>> getWifiList();
//
//    @GET("json/v1.0.1/getsmblogin.json")
//    Call<ResponseWrapper<SmbLogin>> getSmbLogin();
//
//    @GET("json/v1.0.1/getappversion.json")
//    Call<ResponseWrapper<AppVersion>> getAppVersion();

    @GET("cgi-bin/signin")
    Call<ResponseWrapper> getAppLogin(@Query("mobilenr") String mobilenr, @Query("email") String email);
}
