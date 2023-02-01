package com.aistream.greenqube.upgrade;

import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSONException;

import com.aistream.greenqube.mvp.model.ResponAppUpgradeInfo;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.APIResultCallBack;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.intercepter.NoNetWorkException;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateHttpManager implements HttpManager {

    private static Map<String, String> headers = new HashMap<>();

    static {
        headers.put("User-Agent", "OGLE-APP");
    }

    /**
     * api call
     * @param url
     * @param params
     * @param callBack
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        APICall.getAppUpgradeInfo(url, new APIResultCallBack<ResponAppUpgradeInfo>() {
            @Override
            public void onSuccess(ResponAppUpgradeInfo response) {
                callBack.onResponse(response.toString());
            }

            @Override
            public void onError(int httpCode, ResponAppUpgradeInfo response, Throwable t) {
                String msg = "download apk fail.";
                if (response != null) {
                    Status status = response.getStatus();
                    if (status != null) {
                        msg = status.getMessage();
                    }
                }
                callBack.onError(msg);
            }
        });
    }

    /**
     * download apk
     * @param url
     * @param path
     * @param fileName
     * @param callback
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        OkHttpUtils.get()
                .url(url)
                .headers(headers)
                .build()
                .execute(new FileCallBack(path, fileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        callback.onProgress(progress, total);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        callback.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        callback.onResponse(response);

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        callback.onBefore();
                    }

                    @Override
                    protected String validateError(Exception error, Response response) {
                        Log.d("AppUpgrade","download error: "+error+", response: "+response);
                        if (error != null) {
                            if (error instanceof NoNetWorkException) {
                                return "No network, please try again";
                            } else if (error instanceof SocketTimeoutException) {
                                return "Network connect timeout, please try again";
                            } else if (error instanceof JSONException) {
                                return "json convert excception";
                            } else if (error instanceof ConnectException) {
                                return "server error";
                            }
                            return error.toString();
                        }


                        if (response != null) {
                            int code = response.code();
                            if (code >= 400) {
                                return "server error";
                            }
                        }
                        return "other error";
                    }
                });

    }
}
