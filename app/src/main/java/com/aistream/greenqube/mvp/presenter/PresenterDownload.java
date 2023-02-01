package com.aistream.greenqube.mvp.presenter;

import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.StatusDownloadMovie;
import com.aistream.greenqube.mvp.view.ViewDownload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhuDepTraj on 5/28/2018.
 */

public class PresenterDownload {
    private ViewDownload viewDownload;
    private List<StatusDownloadMovie> movieInfoListDown = new ArrayList<>();
    PresenterMainImp presenterMainImp;
    DataBaseHelper dataBaseHelper;

    public PresenterDownload(PresenterMainImp presenterMain, ViewDownload viewDownload) {
        this.presenterMainImp = presenterMain;
        this.viewDownload = viewDownload;
        dataBaseHelper = DataBaseHelper.getInstance();
    }

}
