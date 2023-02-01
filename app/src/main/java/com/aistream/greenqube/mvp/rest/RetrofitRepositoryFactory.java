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

/**
 * Created by Administrator on 8/2/2017.
 */

public class RetrofitRepositoryFactory {

    private static String getBearerToken(String token) {
        return "Bearer "+token;
    }

    private static String getUserAgent() {
        return "OGLE-APP/Android";
    }

    //new API
    public static Call<ResponseWrapper<ResponTypeRegister>> getTypeRegister(String typePlayer) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getTypeRegister(getUserAgent(), typePlayer);
    }

    public static Call<ResponseWrapper<AccountList>> loginWithType0(String identity, String password, String unique_id,
                                                                    String player_type, String device, String manufacturer,
                                                                    String model, String os, String os_version, String app,
                                                                    String app_version, String recharge_pin, String fmaToken) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).
                loginWithType0("OGLE-APP/Android", getBearerToken(fmaToken), identity, password, unique_id, player_type, device, manufacturer,
                        model, os, os_version, app, app_version, recharge_pin);
    }

    public static Call<ResponseWrapper<Status>> getOTP(String identity) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getOTP(getUserAgent(), identity);
    }

    public static Call<ResponseWrapper<ResponSignUp>> getSignUp(String identity, String otp, String surname,
                                                                String name, String player_type, String unique_id, String device,
                                                                String device_model, String os, String os_version, String app,
                                                                String app_version, String email, String gender, String nickname,
                                                                String occupation, String age, String birthday) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic)
                .getSignUp(getUserAgent(), identity, otp, surname, name, player_type, unique_id, device, device_model, os,
                        os_version, app, app_version, email, gender, nickname, occupation, age, birthday);
    }

    public static Call<ResponseWrapper<AccountList>> getLogin(boolean isPublic, String identity, String player_type, String otp, String unique_id,
                                                              String device, String os, String os_version, String app, String app_version, String recharge_pin) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getLogin(getUserAgent(),
                identity, player_type, otp, unique_id, device, os, os_version, app, app_version, recharge_pin);
    }

    public static Call<ResponseWrapper<MovieList>> getMovieList(String bearer, String fmaToken) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getMovieListNew(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken));
    }

    public static Call<ResponseWrapper<MovieList>> getHotMovieList(String bearer, String fmaToken) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getHotMovieList(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken));
    }

    public static Call<ResponseWrapper<MovieList>> getNewReleaseList(String bearer, String fmaToken) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getNewReleaseList(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken));
    }

    public static Call<ResponseWrapper<WifiList>> getWifiRouterList(boolean isPublic, double longitue, double latitude) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getWifiRouterList(getUserAgent(), longitue, latitude);
    }

    public static Call<ResponseWrapper<ResponBilling>> getBillingStart(String bearer, String fmaToken, String billingToken, int id) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getBillingStart(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), billingToken, id);
    }

    public static Call<ResponseWrapper<Status>> getVerification(String bearer, String fmaToken, String otp, String numPhone) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getVerification(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), otp, numPhone);
    }

    public static Call<ResponseWrapper<AccountInfoList>> getAccountInfo(boolean isPublic, String bearer, String fmaToken) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getAccountInfo(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken));
    }

    public static Call<ResponseWrapper<RechargeReponse>> getRechargePin(boolean isPublic, String bearer, String fmaToken, String pin) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).accountRecharge(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), pin);
    }

    public static Call<ResponseWrapper<RechargeReponse>> renewAccount(boolean isPublic, String bearer, String fmaToken, String pin) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).accountRecharge(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), pin);
    }

    public static Call<ResponseWrapper<BalanceRespon>> getBalanceSheet(String bearer, String fmaToken) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).accountBalancee(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken));
    }

    public static Call<ResponseWrapper<ListFavorites>> getFavorites(String bearer, String fmaToken) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getFavoritesMov(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken));
    }

    public static Call<ResponseWrapper<ListStatusUpdateFav>> updateFavoritesMov(boolean isPublic, String bearer, String fmaToken, UpdateFavorite updateFavorite) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).updateFavoritesMov(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), updateFavorite);
    }

    public static Call<ResponseWrapper<ResponSyncFavorite>> synchFavoritesMov(String bearer, String fmaToken, List<SyncFavorites> updateFavorite) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).synchFavoritesMov(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), updateFavorite);
    }

    public static Call<ResponseWrapper<ResponReportUser>> bacUpData(String bearer, String fmaToken, DataUser dataBackup) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).backUPData(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), dataBackup);
    }

    public static Call<ResponseWrapper<ResponDownloadRight>> getCookieDownload(String bearer, String fmaToken, String billingToken, int id, int streaming) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.getPublicDownLoadUrl()).getCookie(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), billingToken, id, streaming);
    }

    public static Call<ResponseBody> downFileM3U8(String url, String bearer, String fmaToken, String coockie) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.getPublicDownLoadM3u8Url()).downFileM3U8(url, getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), coockie);
    }

    public static Call<ResponseWrapper<Status>> switchPlan(String bearer, String fmaToken, int planid) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).switchPlan(getUserAgent(), getBearerToken(bearer), getBearerToken(fmaToken), planid);
    }

    public static Call<ResponseWrapper<ResponAppUpgradeInfo>> getAppUpgradeInfo(String playerId) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getAppUpgradeInfo(getUserAgent(), playerId);
    }

    public static Call<ResponseWrapper<ResponDetectFMS>> getDetectFMS(String token, String fmaToken, String localip, String subnet_mask) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).getDetectFMS(getUserAgent(), getBearerToken(token), getBearerToken(fmaToken), localip, subnet_mask);
    }

    public static Call<ResponseWrapper<Status>> updateAccountInfo(String token, String fmaToken, Object updateInfo) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).updateAccountInfo(getUserAgent(), "Bearer " + token, "Bearer "+fmaToken, updateInfo);
    }

    public static Call<ResponseWrapper<Status>> checkWifi() {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, Config.apiEndpointPublic).checkWifi(getUserAgent());
    }

    public static Call<ResponseWrapper<Status>> checkFAG(String server) {
        return RetrofitRepository.getInstance().getApiService(ApiServicePublic.class, server).checkFAG(getUserAgent());
    }
}
