package com.aistream.greenqube.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.customs.ItemTouchHelperCallback;
import com.aistream.greenqube.mvp.view.ItemDownloadListener;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ItemUpdateProgressViewHolder;
import com.aistream.greenqube.mvp.view.ViewDownload;
import com.aistream.greenqube.services.DownloadManager;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by NguyenQuocDat on 12/25/2017.
 */

public class ItemDownloadAdapter extends RecyclerView.Adapter<ItemDownloadAdapter.MyViewHolder> implements ItemTouchHelperCallback {

    private PresenterMainImp presenterMain;
    private List<MovieDownload> listDownload = new ArrayList<>();
    private Context mContext;
    DataBaseHelper dataBaseHelper;
    SimpleDateFormat inputDf;
    ViewDownload viewDownload;
    SharedPreferences preferences;
    OgleApplication ogleApplication;
    private Map<Integer, MovieDownload> selectMap;
    private ItemDownloadListener listener;

    public void setItemDownloadListener(ItemDownloadListener listener) {
        this.listener = listener;
    }

    public void removeItem(MovieDownload... mvDownloads) {
        boolean isRemove = false;
        for (MovieDownload mvDownload: mvDownloads) {
            int position = getPos(mvDownload);
            if (position >= 0) {
                selectMap.remove(mvDownload.getMvId());
                listDownload.remove(position);
                isRemove = true;
            }
        }

        if (isRemove) {
            notifyDataSetChanged();
            if (listener != null) {
                listener.dataSetChanged(listDownload.size());
            }
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < listDownload.size()) {
            MovieDownload info = listDownload.get(position);
            if (info != null) {
                selectMap.remove(info.getMvId());
                listDownload.remove(position);
                notifyItemRemoved(position);
            }

            if (listener != null) {
                listener.dataSetChanged(listDownload.size());
            }
        }
    }

    public void addItem(int pos, MovieDownload info) {
        if (pos >= 0) {
            listDownload.add(pos, info);
            notifyDataSetChanged();

            if (listener != null) {
                listener.dataSetChanged(listDownload.size());
            }
        }
    }

    public void replaceItem(int pos, MovieDownload info) {
        if (pos >= 0) {
            listDownload.set(pos, info);
            notifyDataSetChanged();

            if (listener != null) {
                listener.dataSetChanged(listDownload.size());
            }
        }
    }

    public MovieDownload updateData(int mvId, long downloadId, int status, String path, int reason) {
        MovieDownload movie = null;
        try {
            for (MovieDownload movieDownload : listDownload) {
                if (movieDownload.getMvId() == mvId) {
                    if (downloadId > 0) {
                        movieDownload.setIdDownload((int) downloadId);
                        movieDownload.setTypeDownload(3);
                    }
                    movieDownload.setStatus(status);
                    movieDownload.setPath(path);
                    movieDownload.setReason(reason);
                    movie = movieDownload;
                    break;
                }
            }
            notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return movie;
    }

    public int getPos(MovieDownload mvDownload) {
        int position = -1;
        int i = 0;
        for (MovieDownload movieDownload: listDownload) {
            if (movieDownload.getMvId() == mvDownload.getMvId()) {
                position = i;
                break;
            }
            i++;
        }
        return position;
    }

    public ItemDownloadAdapter(Context cont, List<MovieDownload> liMvDownload, PresenterMainImp presenterMain, ViewDownload view) {
        this.mContext = cont;
        ogleApplication = (OgleApplication) cont.getApplicationContext();
        this.presenterMain = presenterMain;
        this.viewDownload = view;
        this.listDownload = liMvDownload;
        dataBaseHelper = DataBaseHelper.getInstance();
        inputDf = new SimpleDateFormat("yyyy-MM-dd");
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        selectMap = new HashMap<>();
    }

    public void updateData(List<MovieDownload> listDownload) {
        this.listDownload = listDownload;
        listener.clickCancelBtn();
        if (listener != null) {
            listener.dataSetChanged(listDownload.size());
        }
    }

    @Override
    public ItemDownloadAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_downloadmovie, parent, false);
        return new ItemDownloadAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemDownloadAdapter.MyViewHolder holder, int position) {
        MovieDownload mvDownload = getItem(position);
        if (mvDownload != null) {
            holder.bind(mvDownload, position);
        }
    }

    @Override
    public int getItemCount() {
        if (listDownload == null)
            return 0;
        return listDownload.size();
    }

    public MovieDownload getItem(int position) {
        if (listDownload != null && listDownload.size() > position && position >= 0) {
            return listDownload.get(position);
        }
        return null;
    }

    boolean isShowDelete = false;

    @Override
    public boolean onMove(int fromPos, int toPos) {
        return false;
    }

    @Override
    public void onItemDelete(int pos) {
        MovieDownload item = getItem(pos);
        removeItem(pos);
        listener.deleteMovies(item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements ItemUpdateProgressViewHolder {
//        private SwipeRevealLayout swipeLayout;
//        private View deletaView;

        TextView tv_name;
        ImageView img_movie;
        ImageView img_statusDownload;

        LinearLayout ll_downsucess;
        LinearLayout ll_downloading;
        ProgressBar progress_bar;

        TextView tv_mb;
        TextView tv_status;
        LinearLayout ll_progress;
        LinearLayout ll_click;
        CheckBox rb_delete;
        TextView tv_duration;
        TextView tv_pending_movies;
        TextView tv_vip;
        TextView tv_inque;
        TextView tv_percent;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            img_movie = (ImageView) itemView.findViewById(R.id.img_movie);
            tv_vip = (TextView) itemView.findViewById(R.id.tv_vip);
            tv_pending_movies = (TextView) itemView.findViewById(R.id.tv_pending_movies);
            img_statusDownload = (ImageView) itemView.findViewById(R.id.img_statusDownload);

            ll_downsucess = (LinearLayout) itemView.findViewById(R.id.ll_downsucess);
            ll_downloading = (LinearLayout) itemView.findViewById(R.id.ll_downloading);
            progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            tv_mb = (TextView) itemView.findViewById(R.id.tv_mb);

            ll_progress = (LinearLayout) itemView.findViewById(R.id.ll_progress);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            ll_click = (LinearLayout) itemView.findViewById(R.id.ll_click);
            rb_delete = (CheckBox) itemView.findViewById(R.id.rb_delete);
            tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);
            tv_inque = (TextView) itemView.findViewById(R.id.tv_inque);
            tv_percent = (TextView) itemView.findViewById(R.id.tv_percent);
        }


        public void bind(final MovieDownload data, int position) {
            presenterMain.showMovieVIP(tv_vip, data.getMvId());
            rb_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectMovie(rb_delete.isChecked(), data);
                }
            });

            Picasso.with(mContext).load(Config.picURLPath + data.getImage()).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2).into(img_movie, new Callback() {

                @Override
                public void onSuccess() {
                    Log.v("Picasso", "fetch image success in try again.");
                }

                @Override
                public void onError() {
                    Log.v("Picasso", "Could not fetch image again...");
                    Picasso.with(mContext)
                            .load(Config.picURLPath + data.getImage())
                            .fit().centerCrop()
                            .config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2)
                            .into(img_movie);
                }

            });

            tv_duration.setText(OgleHelper.getDurationString(data.getDuration()) + " | " + data.getFileSize() + "MB");
            tv_pending_movies.setVisibility(View.GONE);
            if (data.getMvId() == -1) {
                tv_name.setText(data.getMvName());
                tv_status.setVisibility(View.GONE);
                ll_downloading.setVisibility(View.GONE);
                ll_progress.setVisibility(View.GONE);
                tv_duration.setVisibility(View.GONE);
                img_statusDownload.setVisibility(View.GONE);
                tv_inque.setVisibility(View.VISIBLE);
                tv_pending_movies.setVisibility(View.VISIBLE);
                tv_pending_movies.setText(OgleHelper.formatMsg(mContext.getResources().getString(R.string.pending_movies),
                        String.valueOf(viewDownload.getPendingDownloadCount())));
            } else {
                tv_inque.setVisibility(View.GONE);
                MovieDownload mvDownload = listDownload.get(0);
                if (mvDownload.getMvId() == -1) {
                    tv_name.setText(String.valueOf(position) + ". " + data.getMvName());
                } else {
                    tv_name.setText(String.valueOf(position + 1) + ". " + data.getMvName());
                }
                updateStatus(data);
            }

            img_movie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getMvId() == -1) {
                        viewDownload.showDialogPendingDownload();
                        return;
                    }
                    if (data.getTypeDownload() != 0
                            && data.getTypeDownload() != 1) {
                        presenterMain.processMovie(data.getMvId());
                    }
                }
            });

            ll_click.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if (data.getMvId() == -1) {
                        viewDownload.showDialogPendingDownload();
                    }
                }
            });

            if (isShowDelete) {
                rb_delete.setVisibility(View.VISIBLE);
                if (selectMap.containsKey(data.getMvId())) {
                    rb_delete.setChecked(true);
                } else {
                    rb_delete.setChecked(false);
                }
            } else {
                rb_delete.setVisibility(View.GONE);
                rb_delete.setChecked(false);
            }
        }

        private void selectMovie(boolean isSelect, MovieDownload data) {
            if (isSelect) {
                selectMap.put(data.getMvId(), data);
                rb_delete.setChecked(true);
            } else {
                selectMap.remove(data.getMvId());
                rb_delete.setChecked(false);
            }
            listener.showDeleteCount(selectMap.size());
        }

        @Override
        public void updateDownloadProgress(int mTotalTotal, int mTotalCurrent, double speed) {
            progress_bar.setMax(mTotalTotal);
            progress_bar.setProgress(mTotalCurrent);
            tv_mb.setText(mTotalCurrent / 1048576 + "MB / " + mTotalTotal / 1048576 + "MB");
            tv_percent.setText(((int)((float)mTotalCurrent * 100/ mTotalTotal)) + "%");
            if (speed >= 0) {
                setMsg(1, String.format(mContext.getResources().getString(R.string.downloading_speed),
                        OgleHelper.formatDouble(speed * 8 / 1048576, 2)+"Mbps"));
            }
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public void updateStatus(MovieDownload movieDownload) {
            int typeDownLoad = movieDownload.getTypeDownload();
            int getcode = movieDownload.getTypeCodeRespon();
            if (typeDownLoad == 0 || typeDownLoad == 1) {
                ll_progress.setVisibility(View.GONE);
                setMsg(1, OgleHelper.showDownloadPendingMsg(mContext));
                img_statusDownload.setVisibility(View.GONE);
            } else if (typeDownLoad == 2) {
                ll_progress.setVisibility(View.GONE);
                img_statusDownload.setVisibility(View.VISIBLE);
                img_statusDownload.setBackgroundResource(R.drawable.icerror123little);
                String errTxt = presenterMain.getDownloadErrorMsg(getcode);
                setMsg(2, errTxt);
            } else {
                updateDownloadStatus(movieDownload, movieDownload.getStatus(), movieDownload.getReason());
            }
        }

        private void updateDownloadStatus(MovieDownload movieDownload, int status, int reason) {
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    if (Config.isAllowDownload) {
                        setMsg(1, mContext.getResources().getString(R.string.waitingconnection));
                    } else {
                        setMsg(2, mContext.getResources().getString(R.string.pauseisnotmac));
                    }
                    showDownloading(true);
                    ll_progress.setVisibility(View.VISIBLE);
                    //update download progress
                    updateDownloadProgress(movieDownload.getFileSize() * 1048576, movieDownload.getDownloadSize(), -1);
                    if (movieDownload.hasStartDownloaded()) {
                        img_statusDownload.setVisibility(View.VISIBLE);
                        img_statusDownload.setBackgroundResource(R.drawable.icreadylittle);
                    } else {
                        img_statusDownload.setVisibility(View.GONE);
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    String reasonText = "";
                    switch (reason) {
                        case DownloadManager.ERROR_CANNOT_RESUME:
                            reasonText = "ERROR_CANNOT_RESUME";
                            break;
                        case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            reasonText = "ERROR_DEVICE_NOT_FOUND";
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            reasonText = "ERROR_FILE_ALREADY_EXISTS";
                            break;
                        case DownloadManager.ERROR_FILE_ERROR:
                            reasonText = "ERROR_FILE_ERROR";
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                            reasonText = "ERROR_HTTP_DATA_ERROR";
                            break;
                        case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                            reasonText = "ERROR_INSUFFICIENT_SPACE";
                            break;
                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            reasonText = "ERROR_TOO_MANY_REDIRECTS";
                            break;
                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                            break;
                        case DownloadManager.ERROR_FILE_MISSING:
                            reasonText = "ERROR_FILE_MISSING";
                            break;
                        case DownloadManager.ERROR_UNKNOWN:
                            reasonText = "ERROR_UNKNOWN";
                            break;
                        default:
                            reasonText = "ERROR_UNKNOWN_DEFAULT";
                            break;
                    }
                    Log.i("DownloadMovieType", "Fail DownLoad Manager: " + reason);
                    showDownloading(true);
                    if (!TextUtils.isEmpty(reasonText)) {
                        reasonText += ", ERROR Code["+reason+"]";
                    } else {
                        reasonText = mContext.getResources().getString(R.string.errorcode).replace("XXX", String.valueOf(reason));
                    }
                    setMsg(2, reasonText);
                    ll_progress.setVisibility(View.GONE);
                    img_statusDownload.setBackgroundResource(R.drawable.icerror123little);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    MovieDownload mDownload = presenterMain.getMovieDownloadById(movieDownload.getMvId());
                    if (mDownload != null) {
                        movieDownload = mDownload;
                    }
                    //file is missing
                    if (movieDownload.getDownloadSize() == 0) {
                        updateDownloadStatus(movieDownload, DownloadManager.STATUS_FAILED, com.aistream.greenqube.services.DownloadManager.ERROR_FILE_MISSING);
                        return;
                    }
                    ll_downloading.setVisibility(View.GONE);
                    ll_downsucess.setVisibility(View.VISIBLE);
                    tv_status.setVisibility(View.GONE);
                    tv_duration.setVisibility(View.VISIBLE);
                    img_statusDownload.setVisibility(View.VISIBLE);
                    //refresh storage usage
                    viewDownload.updateStorage();

                    int resid = R.drawable.icreadylittle;
                    if (movieDownload.getExpireTime() > 0) {
                        tv_status.setVisibility(View.VISIBLE);
                        String expireDate = OgleHelper.getDateTime(movieDownload.getExpireTime());
                        String msg = "";
                        int colorId = -1;
                        if (!movieDownload.hasExpired()) {
                            viewDownload.updateStorage();
                            colorId = R.color.green;
                            msg = String.format(mContext.getResources().getString(R.string.statusdownload5), expireDate);
                        } else {
                            msg = String.format(mContext.getResources().getString(R.string.rentalperiodendtime), expireDate);
                            colorId = R.color.purple;
                            resid = R.drawable.icerror123little;
                        }
                        tv_status.setText(msg);
                        tv_status.setTextColor(mContext.getResources().getColor(colorId));
                    }
                    img_statusDownload.setBackgroundResource(resid);
                    break;
                case DownloadManager.STATUS_PENDING:
                    showDownloading(true);
                    setMsg(1, mContext.getResources().getString(R.string.waitingconnectturn));
                    ll_progress.setVisibility(View.GONE);
                    img_statusDownload.setVisibility(View.GONE);
                    break;
                case DownloadManager.STATUS_RUNNING:
                    showDownloading(true);
                    ll_progress.setVisibility(View.VISIBLE);
                    img_statusDownload.setVisibility(View.VISIBLE);
                    img_statusDownload.setBackgroundResource(R.drawable.icreadylittle);
                    updateDownloadProgress(movieDownload.getFileSize() * 1048576,
                                                        movieDownload.getmTotalCurrent(),
                                                        movieDownload.getSpeed());
                    break;
            }
        }

        private void showDownloading(boolean isShow) {
            if (isShow) {
                ll_downloading.setVisibility(View.VISIBLE);
                ll_downsucess.setVisibility(View.GONE);
            } else {
                ll_downloading.setVisibility(View.GONE);
                ll_downsucess.setVisibility(View.VISIBLE);
            }
        }

        private void setMsg(int type, String msg) {
            Resources resources = mContext.getResources();
            switch (type) {
                case 1: //normal
                    tv_status.setTextColor(resources.getColor(R.color.green));
                    break;
                case 2: //not normal
                    tv_status.setTextColor(resources.getColor(R.color.purple));
                    break;
            }
            tv_status.setText(msg);
            tv_status.setVisibility(View.VISIBLE);
        }
    }

    public void showHideItemDelete(boolean isShow) {
        isShowDelete = isShow;
        clearDelState();
        notifyDataSetChanged();
    }


    public void updateDataFail(int mvId, int typeDownload, int typeCodeRes) {
        try {
            for (MovieDownload movieDownload : listDownload) {
                if (movieDownload.getMvId() == mvId) {
                    movieDownload.setTypeDownload(typeDownload);
                    movieDownload.setTypeCodeRespon(typeCodeRes);
                    break;
                }
            }
            notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateNetworkState() {
        notifyDataSetChanged();
    }

    public List<MovieDownload> getDownloadList() {
        return listDownload;
    }

    public List<MovieDownload> getSelectMovieList() {
        return new ArrayList<>(selectMap.values());
    }

    public boolean hasPendingMovie() {
        if (!listDownload.isEmpty()) {
            MovieDownload movieDownload = listDownload.get(0);
            if (movieDownload.getMvId() == -1) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDownloadingMovies() {
        if (!listDownload.isEmpty()) {
            Iterator<MovieDownload> iterator = listDownload.iterator();
            while (iterator.hasNext()) {
                MovieDownload next = iterator.next();
                if (next.getTypeDownload() == 0 || next.getTypeDownload() == 1) {
                    return true;
                } else if (next.getStatus() == DownloadManager.STATUS_PAUSED ||
                            next.getStatus() == DownloadManager.STATUS_PENDING ||
                            next.getStatus() == DownloadManager.STATUS_RUNNING) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearDelState() {
        selectMap.clear();
    }
}