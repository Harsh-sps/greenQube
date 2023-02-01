package com.aistream.greenqube.mvp.view;

import android.view.View;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;

/**
 * Created by Administrator on 5/18/2017.
 */

public interface ViewMain {
    void showLoading();

    void hideLoading();

    void backClickToSearch(int pageNume);

    void showHideToolBar(int mode);

    void clickToPlayMovie(int mvid, String name, String url, String fileName, long timeContinue, String imgurl);

    void onlinePlayMovie(int mvid, String name, String url, String fileName, String cookie, long timeContinue, String imgurl);

    void updateStatusDownload(int mvId, long downloadId, int status, String path, int reason);

    void updateProgressDownload(int mvId, long downloadId, int mTotalTotal, int mTotalCurrent, double speed);

    void deleteDataDownload(MovieDownload... movieDownloads);

    void refreshMovieDownloads();

    void updateDownloadBar();

    void updateErrorCodeAtDialogIsShow(int mvID, int typeDownload, int typeCodeResponse);

    void showToasApp(String msg, int gravity);

    void endExpiryTimeToLogin();

    void resetApp();

    void verifyAccount();

    void showLoginPage();

    void deleteDownloadManager();

    void goneIconAndStt(int mvId, int type, String msg);

    void updateSttAtChangeNetWork(int flagNetWork);

    void showHideNoConnection(int flagNetWork);

    void showMovieDetail(MovieInfo movieInfo);

    void onClickMoreLikeThis(MovieInfo movieInfo);

    void hideDialogMoviePremiumDetail();

    void showOrHideTextNotification(int type);

    void setChangeTextNotification(int type);

    void showMovieVIP(TextView view, MovieInfo movieInfo);

    void showTabBar();

    void hideTabBar();

    View getView();

    void refreshData();

    boolean checkLocationEnabled();
}
