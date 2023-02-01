package com.aistream.greenqube.mvp.rest;

import retrofit2.Response;

public class APIResultCallBack<T> implements IResultCallBack<T>{

    private boolean ignoreTokenExpired = false;

    @Override
    public void before() {
    }

    @Override
    public void onSuccess(T body) {
    }

    @Override
    public void onSuccess(T body, Response response) {
    }

    @Override
    public void onTokenExpired() {
    }

    @Override
    public void onError(int httpCode, T body, Throwable t) {
    }

    @Override
    public void onError(Response response, T body, Throwable t) {
    }

    @Override
    public void after() {
    }

    public boolean isIgnoreTokenExpired() {
        return ignoreTokenExpired;
    }

    public void setIgnoreTokenExpired(boolean ignoreTokenExpired) {
        this.ignoreTokenExpired = ignoreTokenExpired;
    }
}
