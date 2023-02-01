package com.aistream.greenqube.mvp.rest;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import okhttp3.Headers;

import com.alibaba.fastjson.JSONObject;
import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.AccountInfoList;
import com.aistream.greenqube.mvp.model.AccountList;
import com.aistream.greenqube.mvp.model.BalanceRespon;
import com.aistream.greenqube.mvp.model.ListFavorites;
import com.aistream.greenqube.mvp.model.MovieList;
import com.aistream.greenqube.mvp.model.RechargeReponse;
import com.aistream.greenqube.mvp.model.ResponAppUpgradeInfo;
import com.aistream.greenqube.mvp.model.ResponBilling;
import com.aistream.greenqube.mvp.model.ResponDetectFMS;
import com.aistream.greenqube.mvp.model.ResponDownloadRight;
import com.aistream.greenqube.mvp.model.ResponseWrapper;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.model.WifiList;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.util.OgleHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APICall {

    public static ViewMain viewMain;
    public static OgleApplication ogleApplication;

    /**
     * billing
     * @param mvId
     * @param callBack
     */
    public static void sendBillingStart(int mvId, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getBillingStart(ogleApplication.getToken(), ogleApplication.getFMAToken(), ogleApplication.getBillingToken(mvId), mvId)
                .enqueue(new Callback<ResponseWrapper<ResponBilling>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<ResponBilling>> call, Response<ResponseWrapper<ResponBilling>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        ResponBilling body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<ResponBilling>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    /**
     * switch plan
     * @param planid
     * @param callBack
     */
    public static void switchPlan(int planid, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.switchPlan(ogleApplication.getToken(), ogleApplication.getFMAToken(), planid)
                .enqueue(new Callback<ResponseWrapper<Status>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<Status>> call, Response<ResponseWrapper<Status>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        if (httpCode == 200) {
                            status = response.body().body;
                        }
                        processResponse(httpCode, status, status, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<Status>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void openLogin(String identity, String password, String unique_id,
                                 String player_type, String device, String manufacturer,
                                 String model, String os, String os_version, String app,
                                 String app_version, String recharge_pin,
                                 final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.loginWithType0(identity, password, unique_id,
                                        player_type, device, manufacturer, model,
                                        os, os_version, app, app_version,
                                        recharge_pin, ogleApplication.getFMAToken())
                .enqueue(new Callback<ResponseWrapper<AccountList>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<AccountList>> call, Response<ResponseWrapper<AccountList>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        AccountList body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<AccountList>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void signIn(boolean isPublic, String numPhone, final String otp,
                              String identity, String player_type, String unique_id,
                              String device, String os, String os_version, String app,
                              String app_version, String recharge_pin,
                              final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getLogin(isPublic, numPhone, player_type, otp,
                                    unique_id, device, os,
                                    os_version, app, app_version,
                                    recharge_pin)
                .enqueue(new Callback<ResponseWrapper<AccountList>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<AccountList>> call, Response<ResponseWrapper<AccountList>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        AccountList body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<AccountList>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void getBalanceSheet(final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getBalanceSheet(ogleApplication.getToken(), ogleApplication.getFMAToken())
                .enqueue(new Callback<ResponseWrapper<BalanceRespon>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<BalanceRespon>> call, Response<ResponseWrapper<BalanceRespon>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        BalanceRespon body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<BalanceRespon>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void getAppUpgradeInfo(String url, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getAppUpgradeInfo(OgleApplication.playerType)
                .enqueue(new Callback<ResponseWrapper<ResponAppUpgradeInfo>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<ResponAppUpgradeInfo>> call, Response<ResponseWrapper<ResponAppUpgradeInfo>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        ResponAppUpgradeInfo body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<ResponAppUpgradeInfo>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void getDetectFMS(String localIP, String subnet_mask, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getDetectFMS(ogleApplication.getToken(), ogleApplication.getFMAToken(), localIP, subnet_mask)
                .enqueue(new Callback<ResponseWrapper<ResponDetectFMS>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<ResponDetectFMS>> call, Response<ResponseWrapper<ResponDetectFMS>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        ResponDetectFMS body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<ResponDetectFMS>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void getDownloadRight(int mvId, int streaming, final APIResultCallBack callBack) {
        RetrofitRepositoryFactory.getCookieDownload(ogleApplication.getToken(), ogleApplication.getFMAToken(), ogleApplication.getBillingToken(mvId), mvId, streaming)
                .enqueue(new Callback<ResponseWrapper<ResponDownloadRight>>(){

                    @Override
                    public void onResponse(Call<ResponseWrapper<ResponDownloadRight>> call, Response<ResponseWrapper<ResponDownloadRight>> response) {
                        int httpCode = response.code();
                        ResponDownloadRight body = null;
                        Status status = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<ResponDownloadRight>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void getAccountInfo(final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getAccountInfo(Config.isEndpointPublic, ogleApplication.getToken(), ogleApplication.getFMAToken())
                .enqueue(new Callback<ResponseWrapper<AccountInfoList>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<AccountInfoList>> call, Response<ResponseWrapper<AccountInfoList>> response) {
                        int httpCode = response.code();
                        AccountInfoList body = null;
                        Status status = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<AccountInfoList>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void updateAccountInfo(Object updateInfo, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.updateAccountInfo(ogleApplication.getToken(), ogleApplication.getFMAToken(), updateInfo)
                .enqueue(new Callback<ResponseWrapper<Status>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<Status>> call, Response<ResponseWrapper<Status>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        if (httpCode == 200) {
                            status = response.body().body;
                        }
                        processResponse(httpCode, status, status, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<Status>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void getRechargePin(String pin, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getRechargePin(Config.isEndpointPublic, ogleApplication.getToken(), ogleApplication.getFMAToken(), pin)
                .enqueue(new Callback<ResponseWrapper<RechargeReponse>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<RechargeReponse>> call, Response<ResponseWrapper<RechargeReponse>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        RechargeReponse body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<RechargeReponse>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void renewAccount(String pin, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.renewAccount(Config.isEndpointPublic, ogleApplication.getToken(), ogleApplication.getFMAToken(), pin)
                .enqueue(new Callback<ResponseWrapper<RechargeReponse>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<RechargeReponse>> call, Response<ResponseWrapper<RechargeReponse>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        RechargeReponse body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<RechargeReponse>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void downFileM3U8(String urlM3U8, String cookie, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.downFileM3U8(urlM3U8, ogleApplication.getToken(), ogleApplication.getFMAToken(), cookie)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int httpCode = response.code();
                        Status status = null;
                        ResponseBody body = null;
                        if (httpCode == 200) {
                            body = response.body();
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    public static void getWifiRouterList(double longitude, double latitude, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getWifiRouterList(Config.isEndpointPublic, longitude, latitude).enqueue(new Callback<ResponseWrapper<WifiList>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<WifiList>> call, Response<ResponseWrapper<WifiList>> response) {
                int httpCode = response.code();
                Status status = null;
                WifiList body = null;
                if (httpCode == 200) {
                    status = response.body().body.getStatus();
                    body = response.body().body;
                }
                processResponse(httpCode, status, body, response, callBack);
            }

            @Override
            public void onFailure(Call<ResponseWrapper<WifiList>> call, Throwable t) {
                if (callBack != null) {
                    callBack.onError(-1, null, t);
                    callBack.after();
                }
            }
        });
    }

    public static void getMovieList(final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getMovieList(ogleApplication.getToken(), ogleApplication.getFMAToken()).enqueue(new Callback<ResponseWrapper<MovieList>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<MovieList>> call, Response<ResponseWrapper<MovieList>> response) {
                Log.d("user token ===", ogleApplication.getToken());
                Log.d("fma token ===", ogleApplication.getFMAToken());

                int httpCode = response.code();
                Status status = null;
                MovieList body = null;
                if (httpCode == 200) {
                    status = response.body().body.getStatus();
                    body = response.body().body;
                }
                processResponse(httpCode, status, body, response, callBack);
            }

            @Override
            public void onFailure(Call<ResponseWrapper<MovieList>> call, Throwable t) {
                if (callBack != null) {
                    callBack.onError(-1, null, t);
                    callBack.after();
                }
            }
        });
    }

    public static void getHotMovieList(final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getHotMovieList(ogleApplication.getToken(), ogleApplication.getFMAToken()).enqueue(new Callback<ResponseWrapper<MovieList>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<MovieList>> call, Response<ResponseWrapper<MovieList>> response) {
                int httpCode = response.code();
                Status status = null;
                MovieList body = null;
                if (httpCode == 200) {
                    status = response.body().body.getStatus();
                    body = response.body().body;
                }
                processResponse(httpCode, status, body, response, callBack);
            }

            @Override
            public void onFailure(Call<ResponseWrapper<MovieList>> call, Throwable t) {
                if (callBack != null) {
                    callBack.onError(-1, null, t);
                    callBack.after();
                }
            }
        });
    }

    public static void getNewReleaseList(final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getNewReleaseList(ogleApplication.getToken(), ogleApplication.getFMAToken()).enqueue(new Callback<ResponseWrapper<MovieList>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<MovieList>> call, Response<ResponseWrapper<MovieList>> response) {
                int httpCode = response.code();
                Status status = null;
                MovieList body = null;
                if (httpCode == 200) {
                    status = response.body().body.getStatus();
                    body = response.body().body;
                }
                processResponse(httpCode, status, body, response, callBack);
            }

            @Override
            public void onFailure(Call<ResponseWrapper<MovieList>> call, Throwable t) {
                if (callBack != null) {
                    callBack.onError(-1, null, t);
                    callBack.after();
                }
            }
        });
    }

    public static void getFavorites(final APIResultCallBack<ListFavorites> callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getFavorites(ogleApplication.getToken(), ogleApplication.getFMAToken())
                .enqueue(new Callback<ResponseWrapper<ListFavorites>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<ListFavorites>> call, Response<ResponseWrapper<ListFavorites>> response) {
                        int httpCode = response.code();
                        Status status = null;
                        ListFavorites body = null;
                        if (httpCode == 200) {
                            status = response.body().body.getStatus();
                            body = response.body().body;
                        }
                        processResponse(httpCode, status, body, response, callBack);
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<ListFavorites>> call, Throwable t) {
                        if (callBack != null) {
                            callBack.onError(-1, null, t);
                            callBack.after();
                        }
                    }
                });
    }

    /**
     * check wifi whether is our FMA
     * @param callBack
     */
    public static void checkFMA(final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.checkWifi().enqueue(new Callback<ResponseWrapper<Status>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Status>> call, Response<ResponseWrapper<Status>> response) {
                int httpCode = response.code();
                Status status = null;
                if (httpCode == 200) {
                    status = response.body().body;
                } else if (httpCode < 500) {
                    httpCode = 200;
                }
                processResponse(httpCode, status, status, response, callBack);
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Status>> call, Throwable t) {
                if (callBack != null) {
                    callBack.onError(-1, null, t);
                    callBack.after();
                }
            }
        });
    }

    public static void getOtp(String numPhone, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.getOTP(numPhone).enqueue(new Callback<ResponseWrapper<Status>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Status>> call, Response<ResponseWrapper<Status>> response) {
                int httpCode = response.code();
                Status status = null;
                if (httpCode == 200) {
                    status = response.body().body;
                }
                processResponse(httpCode, status, status, response, callBack);
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Status>> call, Throwable t) {
                if (callBack != null) {
                    callBack.onError(-1, null, t);
                    callBack.after();
                }
            }
        });
    }

    /**
     * check fag service is ok
     * @param callBack
     */
    public static void checkFAG(String server, final APIResultCallBack callBack) {
        if (callBack != null) callBack.before();
        RetrofitRepositoryFactory.checkFAG(server).enqueue(new Callback<ResponseWrapper<Status>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Status>> call, Response<ResponseWrapper<Status>> response) {
                int httpCode = response.code();
                Status status = null;
                if (httpCode == 200) {
                    status = response.body().body;
                } else if (httpCode < 500) {
                    httpCode = 200;
                }
                processResponse(httpCode, status, status, response, callBack);
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Status>> call, Throwable t) {
                if (callBack != null) {
                    callBack.onError(-1, null, t);
                    callBack.after();
                }
            }
        });
    }

    private static <T> void processResponse(int httpCode, Status status, T body, Response response, APIResultCallBack callBack) {
        boolean isSuccess = false;
        boolean isTokenExpired = false;
        boolean isVerifyAccount = true;
        if (httpCode == 200) {
            if (status != null) {
                Log.d("APICall", "processResponse status: "+ JSON.toJSONString(status));
                Integer code = status.getCode();
                if (code == null || code == 0) {
                    isSuccess = true;
                } else if (code == 401) {
                    isTokenExpired = true;
                } else if (code == 58) {
                    isVerifyAccount = false;
                }
            } else {
                isSuccess = true;
            }
        } else if (httpCode == 401) {
            isTokenExpired = true;
        }

        if (isSuccess) {
            if (callBack != null) {
                callBack.onSuccess(body);
                callBack.onSuccess(body, response);
            }
        } else {
            if (isTokenExpired) { //
                if (callBack != null) {
                    if (!callBack.isIgnoreTokenExpired()) {
                        if (viewMain != null) {
                            viewMain.endExpiryTimeToLogin();
                        }
                    }
                    callBack.onTokenExpired();
                }
            } else if (!isVerifyAccount) {
                if (viewMain != null) {
                    viewMain.verifyAccount();
                } else {
                    if (callBack != null) callBack.onError(httpCode, body, null);
                }
            } else {
                if (callBack != null) {
                    callBack.onError(httpCode, body, null);
                    callBack.onError(response, body, null);
                }
            }
        }
        if (callBack != null) callBack.after();

        try {
            if (response != null) {
                Headers headers = response.headers();
                String authentication = headers.get("Fma-Authentication");
                if (!TextUtils.isEmpty(authentication)) {
                    String fmaToken = authentication.split(" ")[1].trim();
                    JSONObject json = JSON.parseObject(fmaToken);
                    String playerType = json.getString("player_type");
                    String enc_accounting = json.getString("enc_accounting");

                    if (OgleApplication.playerType.equals(playerType)) {
                        String accountEncInfo = ogleApplication.getAccountLogin().getAccountEncInfo();
                        if (!OgleHelper.same(enc_accounting, accountEncInfo)) {
                            ogleApplication.getAccountLogin().setAccountEncInfo(enc_accounting);
                            ogleApplication.saveAccountInfo();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
