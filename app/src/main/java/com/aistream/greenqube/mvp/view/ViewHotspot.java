package com.aistream.greenqube.mvp.view;

import com.aistream.greenqube.mvp.model.WifiInfo;

import java.util.List;

/**
 * Created by MyPC on 22/05/2017.
 */

public interface ViewHotspot {
    void loadWifiList(List<WifiInfo> wifiList);

    void showMapGoogle(double latitude, double longitude);

}
