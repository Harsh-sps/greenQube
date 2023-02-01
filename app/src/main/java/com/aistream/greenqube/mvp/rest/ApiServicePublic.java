package com.aistream.greenqube.mvp.rest;

import com.aistream.greenqube.mvp.model.AccountInfoList;
import com.aistream.greenqube.mvp.model.AccountList;
import com.aistream.greenqube.mvp.model.BalanceRespon;
import com.aistream.greenqube.mvp.model.DataUser;
import com.aistream.greenqube.mvp.model.ListFavorites;
import com.aistream.greenqube.mvp.model.ListStatusUpdateFav;
import com.aistream.greenqube.mvp.model.MovieList;
import com.aistream.greenqube.mvp.model.RechargeReponse;
import com.aistream.greenqube.mvp.model.ResponAppUpgradeInfo;
import com.aistream.greenqube.mvp.model.ResponBilling;
import com.aistream.greenqube.mvp.model.ResponDetectFMS;
import com.aistream.greenqube.mvp.model.ResponDownloadRight;
import com.aistream.greenqube.mvp.model.ResponReportUser;
import com.aistream.greenqube.mvp.model.ResponSignUp;
import com.aistream.greenqube.mvp.model.ResponSyncFavorite;
import com.aistream.greenqube.mvp.model.ResponTypeRegister;
import com.aistream.greenqube.mvp.model.ResponseWrapper;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.model.SyncFavorites;
import com.aistream.greenqube.mvp.model.UpdateFavorite;
import com.aistream.greenqube.mvp.model.WifiList;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 5/17/2017.
 */

public interface ApiServicePublic {
    @GET("fag/login/type")
    Call<ResponseWrapper<ResponTypeRegister>> getTypeRegister(@Header("User-Agent") String userAgent, @Query("player_type") String playertype);

    @GET("fag/login/open")
    Call<ResponseWrapper<AccountList>> loginWithType0(@Header("User-Agent") String userAgent, @Header("Fma-Authentication") String fmaToken, @Query("identity") String identity, @Query("password") String password, @Query("unique_id") String unique_id, @Query("player_type") String player_type, @Query("device") String device, @Query("manufacturer") String manufacturer, @Query("model") String model, @Query("os") String os, @Query("os_version") String os_version, @Query("app") String app, @Query("app_version") String app_version, @Query("recharge_pin") String recharge_pin);

    @GET("fag/otp")
    Call<ResponseWrapper<Status>> getOTP(@Header("User-Agent") String userAgent, @Query("identity") String identity);

    @GET("fag/signup")
    Call<ResponseWrapper<ResponSignUp>> getSignUp(@Header("User-Agent") String userAgent, @Query("identity") String identity, @Query("otp") String otp, @Query("surname") String surname, @Query("name") String name, @Query("player_type") String player_type, @Query("unique_id") String unique_id, @Query("device") String device, @Query("device_model") String device_model, @Query("os") String os, @Query("os_version") String os_version, @Query("app") String app, @Query("app_version") String app_version, @Query("email") String email, @Query("gender") String gender, @Query("nickname") String nickname, @Query("occupation") String occupation, @Query("age") String age, @Query("birthday") String birthday);

    @GET("fag/signin")
    Call<ResponseWrapper<AccountList>> getLogin(@Header("User-Agent") String userAgent, @Query("identity") String identity, @Query("player_type") String player_type, @Query("otp") String otp, @Query("unique_id") String unique_id, @Query("device") String device, @Query("os") String os, @Query("os_version") String os_version, @Query("app") String app, @Query("app_version") String app_version, @Query("recharge_pin") String recharge_pin);

    @GET("fag/movies")
    Call<ResponseWrapper<MovieList>> getMovieListNew(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken);

    @GET("fag/movies/hot")
    Call<ResponseWrapper<MovieList>> getHotMovieList(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken);

    @GET("fag/movies/recommendation")
    Call<ResponseWrapper<MovieList>> getNewReleaseList(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken);

    @GET("fag/routers")
    Call<ResponseWrapper<WifiList>> getWifiRouterList(@Header("User-Agent") String userAgent, @Query("longi") double longi, @Query("lati") double lati);

    @GET("fag/account/profile")
    Call<ResponseWrapper<AccountInfoList>> getAccountInfo(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken);

    @PUT("fag/account/update")
    Call<ResponseWrapper> updateAccount(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Body String key, @Header("Fma-Authentication") String fmaToken);

    @GET("fag/account/balancesheet")
    Call<ResponseWrapper<BalanceRespon>> accountBalancee(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken);

    @GET("fag/account/recharge")
    Call<ResponseWrapper<RechargeReponse>> accountRecharge(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Query("pin") String pin);

    @GET("fag/account/bill")
    Call<ResponseWrapper<ResponBilling>> getBillingStart(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Header("fx-credential") String billingToken, @Query("id") int id);

    @GET("fag/account/verification")
    Call<ResponseWrapper<Status>> getVerification(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Query("otp") String otp, @Query("identity") String numPhone);

    @GET("fag/favorites")
    Call<ResponseWrapper<ListFavorites>> getFavoritesMov(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken);

    @PUT("fag/favorites/sync")
    Call<ResponseWrapper<ResponSyncFavorite>> synchFavoritesMov(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Body List<SyncFavorites> synFavorite);

    @PUT("fag/favorites/update")
    Call<ResponseWrapper<ListStatusUpdateFav>> updateFavoritesMov(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Body UpdateFavorite updateFavorite);

    @GET("fag/appversion")
    Call<ResponseWrapper> getAppVersion(@Header("User-Agent") String userAgent, @Query("type") String type);

    @POST("/report_user_data")
    Call<ResponseWrapper<ResponReportUser>> backUPData(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Body DataUser status);

    //get API download
    @GET("/get_download_right")
    Call<ResponseWrapper<ResponDownloadRight>> getCookie(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Header("fx-credential") String billingToken, @Query("id") int id, @Query("streaming") int streaming);

    @GET
    Call<ResponseBody> downFileM3U8(@Url String url, @Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Header("Cookie") String keyCoockie);

    @GET("fag/account/plan")
    Call<ResponseWrapper<Status>> switchPlan(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Query("planid") int planid);

    @GET("fag/appversion")
    Call<ResponseWrapper<ResponAppUpgradeInfo>> getAppUpgradeInfo(@Header("User-Agent") String userAgent, @Query("type") String type);

    @GET("fag/routers/detect")
    Call<ResponseWrapper<ResponDetectFMS>> getDetectFMS(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Query("localip") String localip, @Query("subnet_mask") String subnet_mask);

    @PUT("fag/account/update")
    Call<ResponseWrapper<Status>> updateAccountInfo(@Header("User-Agent") String userAgent, @Header("Authentication") String token, @Header("Fma-Authentication") String fmaToken, @Body Object updateInfo);

    @GET("check_status")
    Call<ResponseWrapper<Status>> checkWifi(@Header("User-Agent") String userAgent);

    @GET("fag/version")
    Call<ResponseWrapper<Status>> checkFAG(@Header("User-Agent") String userAgent);
}


