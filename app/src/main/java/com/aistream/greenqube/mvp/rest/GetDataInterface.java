package com.aistream.greenqube.mvp.rest;

import com.google.gson.reflect.TypeToken;

/**
 * Created by PhuDepTraj on 5/7/2018.
 */

public interface GetDataInterface {
    void onGetDataSuscess(Object data, TypeToken typeToken);
    void onGetDataFailure(int responseCode);
}
