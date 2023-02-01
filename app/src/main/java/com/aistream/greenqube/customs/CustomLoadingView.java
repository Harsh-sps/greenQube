package com.aistream.greenqube.customs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.aistream.greenqube.R;


/**
 * Created by Thái on 3/21/2017.
 */

public class CustomLoadingView {

    public Dialog m_Dialog;

    public CustomLoadingView() {
    }

    public void showProgress(Context context) {
        if (!((Activity) context).isFinishing()) {
            m_Dialog = new Dialog(context);
            m_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            m_Dialog.setContentView(R.layout.layout_loading);
            m_Dialog.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            m_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            m_Dialog.getWindow().setDimAmount(0.3f);
            m_Dialog.setCancelable(false);
            m_Dialog.setCanceledOnTouchOutside(false);
            m_Dialog.show();
        } else {
            hideProgress();
        }
    }

    public void hideProgress() {
        if (m_Dialog != null) {
            if (m_Dialog.isShowing()) {
                m_Dialog.dismiss();
                m_Dialog = null;
            }
        }
    }

    public boolean isShowLoading() {
        if (m_Dialog != null) {
            return m_Dialog.isShowing();
        }
        return false;
    }
}
