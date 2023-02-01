package com.aistream.greenqube.upgrade.listener;

import com.aistream.greenqube.upgrade.Update;

/**
 * notice user cancle upgrade
 */
public interface IUpdateDialogFragmentListener {
    /**
     * cancel upgrade
     * @param updateApp updateApp
     */
    void onUpdateNotifyDialogCancel(Update updateApp);
}
