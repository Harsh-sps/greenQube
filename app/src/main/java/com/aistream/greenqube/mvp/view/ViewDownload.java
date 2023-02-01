package com.aistream.greenqube.mvp.view;

import com.aistream.greenqube.mvp.model.MovieDownload;

import java.util.List;

/**
 * Created by PhuDepTraj on 5/28/2018.
 */

public interface ViewDownload {
    void showDialogWhenFailMac();

    void showDialogFailDownload();

    void updateStorage();

    void showDialogPendingDownload();

    int getPendingDownloadCount();

    void updatePendingMovies(int items);

    void deleteMovies(List<MovieDownload> deleteList);

    void closeDialog();

    List<MovieDownload> getPendingMovies();
}
