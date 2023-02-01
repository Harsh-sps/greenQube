package com.aistream.greenqube.mvp.presenter;


import com.aistream.greenqube.mvp.model.WifiInfo;
import com.aistream.greenqube.mvp.view.ViewHotspot;

import java.util.List;


public class PresenterHotspotImp {
    private ViewHotspot viewHostpot;
    private PresenterMainImp presenterMainImp;
    private List<WifiInfo> wifiInfoList;

    public PresenterHotspotImp(ViewHotspot viewHostpot, PresenterMainImp presenterMainImp) {
        this.viewHostpot = viewHostpot;
        this.presenterMainImp = presenterMainImp;
    }

    public void loadWifi() {
        if (presenterMainImp != null) {
            wifiInfoList = presenterMainImp.getListWifi();
            if (wifiInfoList.size() > 0) {
                viewHostpot.loadWifiList(wifiInfoList);
            }
        }
    }
}
