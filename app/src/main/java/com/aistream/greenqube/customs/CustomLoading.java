package com.aistream.greenqube.customs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.aistream.greenqube.R;

/**
 * Created by Administrator on 5/31/2017.
 */

public class CustomLoading {
    public static CustomLoading customLoadingView;
    public Dialog m_Dialog;

    public static CustomLoading getInstance() {
        if (customLoadingView == null) {
            customLoadingView = new CustomLoading();
        }
        return customLoadingView;
    }

    public void showProgress(Context context) {
        if (!((Activity) context).isFinishing()) {
            m_Dialog = new Dialog(context);
            m_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            m_Dialog.setContentView(R.layout.item_loading);
            m_Dialog.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            m_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            m_Dialog.setCancelable(false);
            m_Dialog.setCanceledOnTouchOutside(false);
            m_Dialog.show();
        }

    }

    public void hideProgress() {
        if (m_Dialog != null) {
            m_Dialog.dismiss();
            m_Dialog = null;
        }
    }
}
