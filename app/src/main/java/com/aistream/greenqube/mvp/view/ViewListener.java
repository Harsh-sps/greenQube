package com.aistream.greenqube.mvp.view;

import android.support.annotation.IdRes;
import android.view.View;

public interface ViewListener {

    <T extends View> T findViewById(@IdRes int id);

    void refreshRecycleView();
}
