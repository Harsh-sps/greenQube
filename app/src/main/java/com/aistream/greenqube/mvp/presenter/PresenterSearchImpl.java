package com.aistream.greenqube.mvp.presenter;

import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.view.ViewSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 29/05/2017.
 */

public class PresenterSearchImpl{
    private ViewSearch viewSearch;
    private PresenterMainImp presenterMainImp;
    private List<MovieInfo> movieInfoListResult = new ArrayList<>();

    public PresenterSearchImpl(ViewSearch viewSearch, PresenterMainImp presenterMainImp) {
        this.viewSearch = viewSearch;
        this.presenterMainImp = presenterMainImp;
    }
}
