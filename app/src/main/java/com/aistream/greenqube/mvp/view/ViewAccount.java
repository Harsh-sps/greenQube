package com.aistream.greenqube.mvp.view;

import com.aistream.greenqube.mvp.model.AccountInfo;

/**
 * Created by PhuDepTraj on 4/12/2018.
 */

public interface ViewAccount {
    void refreshAccount();

    AccountInfo getAccountInfo();
}
