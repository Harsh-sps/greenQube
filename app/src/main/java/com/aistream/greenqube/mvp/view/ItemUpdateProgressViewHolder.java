package com.aistream.greenqube.mvp.view;

import com.aistream.greenqube.mvp.model.MovieDownload;

/**
 * Created by PhuDepTraj on 5/31/2018.
 */

public interface ItemUpdateProgressViewHolder {
    void updateDownloadProgress(int mTotalTotal, int mTotalCurrent, double speed);

    void updateStatus(MovieDownload movieDownload);
}
