package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.adapter.ItemDownloadAdapter;
import com.aistream.greenqube.mvp.view.ItemDownloadListener;
import com.aistream.greenqube.mvp.view.ViewDownload;
import com.aistream.greenqube.R;
import com.aistream.greenqube.mvp.view.ViewListener;

/**
 * Created by Jeff on 12/19/2018.
 */

public class CustomDialogPendingMovies extends Dialog implements View.OnClickListener, ViewListener {
    private Context mContext;
    private ItemDownloadAdapter pendingAdapter;
    private RecyclerView recyc_pending;
    private LinearLayout ll_nomovie;
    private TextView tv_changeExternal;
    private OgleApplication mOgleApp;
    private ViewDownload viewDownload;
    private ItemDownloadListener downloadListener;

    public CustomDialogPendingMovies(@NonNull Context context, ViewDownload viewDownload,
                                     ItemDownloadAdapter pendingAdapter) {
        super(context, R.style.AppThemeDialog);
        mContext = context;
        this.pendingAdapter = pendingAdapter;
        this.viewDownload = viewDownload;
        this.pendingAdapter.showHideItemDelete(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pending_download);

        //show status bar
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);

        mContext = this.getContext();
        mOgleApp = (OgleApplication) getContext().getApplicationContext();
        initView();
    }

    private void initView() {
        FrameLayout btn_back = (FrameLayout)findViewById(R.id.btn_back);
        ll_nomovie = (LinearLayout) findViewById(R.id.ll_nomovie);
        recyc_pending = (RecyclerView) findViewById(R.id.recyc_download);

        ((LinearLayout) findViewById(R.id.frm_notification)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.ll_storage)).setVisibility(View.GONE);
        ll_nomovie.setVisibility(View.GONE);
        recyc_pending.setVisibility(View.VISIBLE);

        recyc_pending.setHasFixedSize(true);
        recyc_pending.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration mDividerItemDecoration1 = new DividerItemDecoration(recyc_pending.getContext(),
                ((LinearLayoutManager) recyc_pending.getLayoutManager()).getOrientation());
        recyc_pending.addItemDecoration(mDividerItemDecoration1);
        recyc_pending.setAdapter(pendingAdapter);
        ItemTouchHelper.Callback callback = new RecycleItemTouchHelper(pendingAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyc_pending);

        btn_back.setOnClickListener(this);

        downloadListener = new ItemDownloadListener(getContext(), viewDownload, this, pendingAdapter) {
            @Override
            public void dataSetChanged(int items) {
                viewDownload.updatePendingMovies(items);
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        viewDownload.closeDialog();
    }

    @Override
    public void refreshRecycleView() {
        pendingAdapter.updateData(viewDownload.getPendingMovies());
        recyc_pending.setAdapter(pendingAdapter);
    }
}
