package com.aistream.greenqube.mvp.rest;

import retrofit2.Response;

public interface IResultCallBack<T> {

    public void before();

    public void onSuccess(T body);

    public void onSuccess(T body, Response response);

    public void onTokenExpired();

    public void onError(int httpCode, T body, Throwable t);

    public void onError(Response response, T body, Throwable t);

    public void after();
}
