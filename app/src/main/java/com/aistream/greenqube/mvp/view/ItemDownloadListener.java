package com.aistream.greenqube.mvp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aistream.greenqube.R;
import com.aistream.greenqube.adapter.ItemDownloadAdapter;
import com.aistream.greenqube.customs.CustomDialogChargeMovieDownLoad;
import com.aistream.greenqube.mvp.model.MovieDownload;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemDownloadListener implements View.OnClickListener {
    private ImageView iv_edit;
    private ImageView iv_delete;
    private ImageView iv_cancel;
    private TextView tv_delmsg;
    private FrameLayout operate_bar;
    private ViewDownload viewDownload;
    private ItemDownloadAdapter adapter;
    private Context context;
    private boolean isShowItem = false;
    private ViewListener viewListener;

    public ItemDownloadListener(Context context, ViewDownload viewDownload, ViewListener view, ItemDownloadAdapter adapter) {
        this.context = context;
        this.viewDownload = viewDownload;
        this.viewListener = view;
        this.iv_edit = view.findViewById(R.id.iv_edit);
        this.iv_delete = view.findViewById(R.id.iv_delete);
        this.iv_cancel = view.findViewById(R.id.iv_cancel);
        this.operate_bar = view.findViewById(R.id.operate_bar);
        this.tv_delmsg = view.findViewById(R.id.tv_delmsg);

        this.iv_edit.setOnClickListener(this);
        this.iv_cancel.setOnClickListener(this);
        this.iv_delete.setOnClickListener(this);
        this.adapter = adapter;
        adapter.setItemDownloadListener(this);
        clickCancelBtn();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit:
                clickEditBtn();
                break;
            case R.id.iv_cancel:
                clickCancelBtn();
                break;
            case R.id.iv_delete:
                clickDeleteBtn();
                break;
        }
    }

    /**
     * click delete btn to delete all select movies
     */
    public void clickDeleteBtn() {
        final List<MovieDownload> selectMovieList = adapter.getSelectMovieList();
        if (!selectMovieList.isEmpty()) {
            deleteMovies(selectMovieList.toArray(new MovieDownload[selectMovieList.size()]));
        }
    }

    /**
     * delete movies
     * @param movieDownloads
     */
    public void deleteMovies(final MovieDownload... movieDownloads) {
        if (movieDownloads != null && movieDownloads.length > 0) {
            final List<MovieDownload> downloads = new ArrayList<>();
            for (MovieDownload mvDownload: movieDownloads) {
                if (mvDownload != null) {
                    downloads.add(mvDownload);
                }
            }
            String msg = context.getResources().getString(R.string.msgdeleteone);
            if (movieDownloads.length > 1) {
                msg = context.getResources().getString(R.string.msgdelete);
            }
            final CustomDialogChargeMovieDownLoad dg = CustomDialogChargeMovieDownLoad.getInstance(context);
            dg.withTitle(msg)
                    .setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dg.dismiss();
                            viewDownload.deleteMovies(downloads);
                            viewDownload.updateStorage();
                            clickCancelBtn();
                        }
                    }).setNoClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dg.dismiss();
                    viewListener.refreshRecycleView();
                }
            }).show();
        }
    }

    public abstract void dataSetChanged(int items);

    /**
     * click edit btn
     */
    public void clickEditBtn() {
        this.iv_edit.setVisibility(View.GONE);
        operate_bar.setVisibility(View.VISIBLE);
        if (adapter != null) {
            adapter.showHideItemDelete(true);
        }
    }

    /**
     * click cancel btn
     */
    public void clickCancelBtn() {
        operate_bar.setVisibility(View.GONE);
        if (adapter != null) {
            adapter.showHideItemDelete(false);
        }
        showDeleteCount(0);
        checkShowEditBtn();
    }

    /**
     * show select how many movies to delete
     * @param count
     */
    @SuppressLint("StringFormatInvalid")
    public void showDeleteCount(int count) {
        if (count > 0) {
            iv_delete.setVisibility(View.VISIBLE);
            tv_delmsg.setText(String.format(context.getResources().getString(R.string.select_delete_notice), String.valueOf(count)));
        } else {
            iv_delete.setVisibility(View.GONE);
            tv_delmsg.setText(context.getResources().getString(R.string.delete_notice));
        }
    }

    /**
     * check whether show edit btn
     */
    public void checkShowEditBtn() {
        boolean isShow = false;
        int itemCount = adapter.getItemCount();
        if (itemCount > 1) {
            isShow = true;
        } else if (itemCount == 1){
            MovieDownload item = adapter.getItem(0);
            if (item.getMvId() != -1) {
                isShow = true;
            }
        }

        if (isShow) {
            iv_edit.setVisibility(View.VISIBLE);
        } else {
            iv_edit.setVisibility(View.GONE);
        }
    }
}
