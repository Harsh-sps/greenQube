package com.aistream.greenqube.mvp.gson;

import android.util.Log;

import com.aistream.greenqube.mvp.model.AccountInfoList;
import com.aistream.greenqube.mvp.model.AccountList;
import com.aistream.greenqube.mvp.model.BalanceRespon;
import com.aistream.greenqube.mvp.model.DownloadRight;
import com.aistream.greenqube.mvp.model.ListFavorites;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.MovieList;
import com.aistream.greenqube.mvp.model.RechargeReponse;
import com.aistream.greenqube.mvp.model.ResponAppUpgradeInfo;
import com.aistream.greenqube.mvp.model.ResponBilling;
import com.aistream.greenqube.mvp.model.ResponDetectFMS;
import com.aistream.greenqube.mvp.model.ResponDownloadRight;
import com.aistream.greenqube.mvp.model.ResponOTP;
import com.aistream.greenqube.mvp.model.ResponReportUser;
import com.aistream.greenqube.mvp.model.ResponSignUp;
import com.aistream.greenqube.mvp.model.ResponSyncFavorite;
import com.aistream.greenqube.mvp.model.ResponTypeRegister;
import com.aistream.greenqube.mvp.model.ResponseWrapper;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.model.WifiInfo;
import com.aistream.greenqube.mvp.model.WifiList;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 5/17/2017.
 */

public class ResponseDeserializer implements JsonDeserializer<ResponseWrapper> {
    private Type registerType = new TypeToken<ResponTypeRegister>() {
    }.getType();

    private Type loginWithType0 = new TypeToken<ResponTypeRegister>() {
    }.getType();

    private Type otpType = new TypeToken<ResponOTP>() {
    }.getType();

    private Type signUpType = new TypeToken<ResponSignUp>() {
    }.getType();

    private Type accountLoginType = new TypeToken<AccountList>() {
    }.getType();

    private Type movieListNewType = new TypeToken<MovieList>() {
    }.getType();

    private Type movieInfoNewType = new TypeToken<MovieInfo>() {
    }.getType();

    private Type listHotMovieType = new TypeToken<MovieList>() {
    }.getType();

    private Type listNewRepleaseType = new TypeToken<MovieList>() {
    }.getType();

    private Type wifiListType = new TypeToken<WifiList>() {
    }.getType();

    private Type wifiInfoType = new TypeToken<WifiInfo>() {
    }.getType();

    private Type accountInfoType = new TypeToken<AccountInfoList>() {// account user
    }.getType();

    private Type favoritesType = new TypeToken<ListFavorites>() {
    }.getType();

    private Type updateFavoritesType = new TypeToken<ResponSyncFavorite>() {
    }.getType();

    private Type getCookieType = new TypeToken<ResponDownloadRight>() {
    }.getType();

    private Type downloadRightType = new TypeToken<DownloadRight>() {
    }.getType();

    private Type downM3U8Type = new TypeToken<ResponseBody>() {
    }.getType();

    private Type downTSType = new TypeToken<ResponseBody>() {
    }.getType();

    private Type reChargePinType = new TypeToken<RechargeReponse>() {
    }.getType();

    private Type purChangerMovieType = new TypeToken<ResponBilling>() {
    }.getType();

    private Type balanceType = new TypeToken<BalanceRespon>() {
    }.getType();

    private Type backUpData = new TypeToken<ResponReportUser>() {
    }.getType();

    private Type statusType = new TypeToken<Status>() {
    }.getType();

    private Type appUpgradeInfoResponseType = new TypeToken<ResponAppUpgradeInfo>() {
    }.getType();

    private Type getDetectFMSType = new TypeToken<ResponDetectFMS>() {
    }.getType();

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Override
    public ResponseWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ResponseWrapper responseWrapper = new ResponseWrapper();
        if (typeOfT.toString().contains(registerType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, registerType);
        } else if (typeOfT.toString().contains(loginWithType0.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, loginWithType0);
        } else if (typeOfT.toString().contains(otpType.toString().replace("class ", ""))) {
            JsonElement statusElm = jsonObject.get("status");
            if (statusElm != null) {
                JsonObject jObj = statusElm.getAsJsonObject();
                responseWrapper.body = gson.fromJson(jObj, statusType);
            } else {
                responseWrapper.body = gson.fromJson(jsonObject, statusType);
            }
        } else if (typeOfT.toString().contains(accountLoginType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, accountLoginType);
        } else if (typeOfT.toString().contains(movieListNewType.toString().replace("class ", ""))) {
            MovieList movieListNew = new MovieList();
            Map<Integer, MovieInfo> movieListNewMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                if (entry.getKey().equals("status")) {
                    JsonObject jObj = entry.getValue().getAsJsonObject();
                    int code = jObj.get("code").getAsInt();
                    Log.i("statusMovie", "status: " + jObj);
                    Status statusMovie = new Status();
                    statusMovie.setCode(code);
                    movieListNew.setStatus(statusMovie);

                } else if (entry.getKey().equals("data")) {
                    JsonArray array = entry.getValue().getAsJsonArray();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject jObj = array.get(i).getAsJsonObject();
                        MovieInfo movieInfoNew = gson.fromJson(jObj, movieInfoNewType);
                        movieListNewMap.put(jObj.get("movie_id").getAsInt(), movieInfoNew);
                    }
                }
            }
            movieListNew.setMovieInfoMap(movieListNewMap);
            responseWrapper.body = movieListNew;
        } else if (typeOfT.toString().contains(listHotMovieType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, listHotMovieType);
        } else if (typeOfT.toString().contains(listNewRepleaseType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, listHotMovieType);
        } else if (typeOfT.toString().contains(wifiListType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, wifiListType);
        } else if (typeOfT.toString().contains(accountInfoType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, accountInfoType);
        } else if (typeOfT.toString().contains(favoritesType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, favoritesType);
        } else if (typeOfT.toString().contains(updateFavoritesType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, updateFavoritesType);
        } else if (typeOfT.toString().contains(getCookieType.toString().replace("class ", ""))) {
            ResponDownloadRight downRight = new ResponDownloadRight();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                if (entry.getKey().equals("status")) {
                    JsonObject jObj = entry.getValue().getAsJsonObject();
                    Status status = gson.fromJson(jObj, statusType);
                    downRight.setStatus(status);
                } else if (entry.getKey().equals("data")) {
                    try {
                        JsonObject jObj = entry.getValue().getAsJsonObject();
                        DownloadRight downloadRight = gson.fromJson(jObj, downloadRightType);
                        downRight.setData(downloadRight);
                    } catch (Exception e) {
                        try {
                            JsonArray array = entry.getValue().getAsJsonArray();
                            for (int i = 0; i < array.size(); i++) {
                                JsonObject jObj = array.get(i).getAsJsonObject();
                                WifiInfo wifiInfo = gson.fromJson(jObj, wifiInfoType);
                                downRight.getWifiList().add(wifiInfo);
                            }
                        } catch (Exception e1) {
                        }
                    }
                }
            }
            responseWrapper.body = downRight;
        } else if (typeOfT.toString().contains(downM3U8Type.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, downM3U8Type);
        } else if (typeOfT.toString().contains(downTSType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, downTSType);
        } else if (typeOfT.toString().contains(signUpType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, signUpType);
        } else if (typeOfT.toString().contains(reChargePinType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, reChargePinType);
        } else if (typeOfT.toString().contains(purChangerMovieType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, purChangerMovieType);
        } else if (typeOfT.toString().contains(balanceType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, balanceType);
        } else if (typeOfT.toString().contains(backUpData.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, backUpData);
        } else if (typeOfT.toString().contains(statusType.toString().replace("class ", ""))) {
            JsonObject jObj = jsonObject;
            if (jsonObject.has("status")) {
                jObj = jsonObject.get("status").getAsJsonObject();
            }
            responseWrapper.body = gson.fromJson(jObj, statusType);
        } else if (typeOfT.toString().contains(appUpgradeInfoResponseType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, appUpgradeInfoResponseType);
        } else if (typeOfT.toString().contains(getDetectFMSType.toString().replace("class ", ""))) {
            responseWrapper.body = gson.fromJson(jsonObject, getDetectFMSType);
        }

        return responseWrapper;
    }
}
