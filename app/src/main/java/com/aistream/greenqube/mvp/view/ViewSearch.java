package com.aistream.greenqube.mvp.view;

import com.aistream.greenqube.mvp.model.MovieInfo;

import java.util.List;

/**
 * Created by MyPC on 29/05/2017.
 */

public interface ViewSearch {
    void loadSearch(List<MovieInfo> movieInfoList, CharSequence charFilter);
}
