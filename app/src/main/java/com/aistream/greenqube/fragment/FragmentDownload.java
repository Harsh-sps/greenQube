package com.aistream.greenqube.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.adapter.ItemDownloadAdapter;
import com.aistream.greenqube.customs.CustomDialogNotMac;
import com.aistream.greenqube.customs.CustomDialogPendingMovies;
import com.aistream.greenqube.customs.RecycleItemTouchHelper;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ItemDownloadListener;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.presenter.PresenterDownload;
import com.aistream.greenqube.mvp.presenter.PresenterLibraryImp;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.view.ViewDownload;
import com.aistream.greenqube.mvp.view.ViewListener;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.R;
import com.aistream.greenqube.services.DownloadManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.*;

/**
 * Created by Administrator on 5/10/2017.
 */

public class FragmentDownload extends Fragment implements ViewDownload, View.OnClickListener, ViewListener {
    private String TAG = "FragmentDownload";
    private PresenterMainImp presenterMainImp;
    private PresenterDownload presenterDownload;
    private ViewMain viewMain;

    private LinearLayout frm_notification;
    private RelativeLayout rl_storage;
    private RelativeLayout rl_sd_storage;
    private RadioButton rb_storage;
    private RadioButton rb_sd_storage;
    private TextView tv_storage;
    private TextView tv_sd_storage;
    private TextView tv_connnotification;
    private ImageView iv_wifi;
    private RecyclerView recyc_download;
    private LinearLayout ll_nomovie;
    public ItemDownloadAdapter pendingAdapter;
    public ItemDownloadAdapter downloadAdapter;
    private PresenterLibraryImp libraryImp;
    private OgleApplication mOgleApp;
    private ImageView iv_edit;
    private int posChangeView;
    private DownloadManager manager;
    private CustomDialogPendingMovies pendingDialog;
    private ItemDownloadListener downloadListener;
    private View mView;
    private ExecutorService executor = newSingleThreadExecutor();

    public FragmentDownload() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FragmentDownload(PresenterMainImp presenterMainImp, ViewMain viewMain) {
        this.viewMain = viewMain;
        this.presenterMainImp = presenterMainImp;
        this.iv_edit = viewMain.getView().findViewById(R.id.iv_edit);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        this.mView = inflater.inflate(R.layout.fragment_download, container, false);
        mOgleApp = (OgleApplication) getContext().getApplicationContext();
        presenterDownload = new PresenterDownload(presenterMainImp, this);

        initView(mView);
        updateStorage();

        return mView;
    }

    private void initView(View view) {
        frm_notification = (LinearLayout) view.findViewById(R.id.frm_notification);
        ll_nomovie = (LinearLayout) view.findViewById(R.id.ll_nomovie);
        rl_storage = (RelativeLayout) view.findViewById(R.id.rl_storage);
        rl_sd_storage = (RelativeLayout) view.findViewById(R.id.rl_sd_storage);
        tv_storage = (TextView) view.findViewById(R.id.tv_storage);
        tv_sd_storage = (TextView) view.findViewById(R.id.tv_sd_storage);
        rb_storage = (RadioButton) view.findViewById(R.id.rb_storage);
        rb_sd_storage = (RadioButton) view.findViewById(R.id.rb_sd_storage);
        recyc_download = (RecyclerView) view.findViewById(R.id.recyc_download);
        tv_connnotification = (TextView) view.findViewById(R.id.tv_connnotification);
        iv_wifi = (ImageView) view.findViewById(R.id.iv_wifi);

        //COMPLETED
        recyc_download.setHasFixedSize(true);
        recyc_download.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyc_download.getContext(),
                ((LinearLayoutManager) recyc_download.getLayoutManager()).getOrientation());
        recyc_download.addItemDecoration(mDividerItemDecoration);
        downloadAdapter = new ItemDownloadAdapter(getActivity(), new ArrayList<MovieDownload>(), presenterMainImp, this);
        recyc_download.setAdapter(downloadAdapter);
        ItemTouchHelper.Callback callback = new RecycleItemTouchHelper(downloadAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyc_download);

        downloadListener = new ItemDownloadListener(getContext(), this, this, downloadAdapter) {
            @Override
            public void dataSetChanged(int items) {
                presenterMainImp.updateDownloadBar();
                if (items == 0) {
                    ll_nomovie.setVisibility(View.VISIBLE);
                }
            }
        };

        rl_storage.setOnClickListener(this);
        rl_sd_storage.setOnClickListener(this);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showWifiStateNotification();
        }
    };

    public void showWifiStateNotification() {
        handler.removeCallbacks(runnable);
        if (downloadAdapter.hasPendingMovie()
                || downloadAdapter.hasDownloadingMovies()) {
            frm_notification.setVisibility(View.VISIBLE);
            setWifiStateNotification();
            handler.postDelayed(runnable, 10000);
        } else {
            hideWifiStateNotification();
        }
    }

    public void hideWifiStateNotification() {
        frm_notification.setVisibility(View.GONE);
    }

    public void setWifiStateNotification() {
        try {
            if (Config.isAllowDownload) {
                iv_wifi.setImageResource(R.drawable.ic_wifi_active);
                tv_connnotification.setTextColor(getResources().getColor(R.color.green));
                tv_connnotification.setText(getResources().getString(R.string.msgconnectogle));
            } else {
                iv_wifi.setImageResource(R.drawable.ic_wifi);
                tv_connnotification.setTextColor(getResources().getColor(R.color.sss));
                tv_connnotification.setText(getResources().getString(R.string.txtnoconnectnotification));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        View view = mView.findViewById(id);
        if (view == null) {
            view = viewMain.getView().findViewById(id);
        }
        return (T) view;
    }

    @Override
    public void refreshRecycleView() {
        showDownloadList();
    }

    @Override
    public List<MovieDownload> getPendingMovies() {
        List<MovieDownload> pendingList = DataBaseHelper.getInstance().getAllPendingMovieDownload();
        return pendingList;
    }

    /**
     * update movie download progress
     * @param mvId
     * @param mTotalTotal
     * @param mTotalCurrent
     */
    public void updateProgress(int mvId, int mTotalTotal, int mTotalCurrent, double speed) {
        int position = getMoviePostion(mvId, downloadAdapter);
        if (position < 0) {
            int pos = getMoviePostion(mvId, pendingAdapter);
            if (pos >= 0) {
                position = updateDownloadMovies(pos);
            }
        }

        if (position >= 0) {
            ItemDownloadAdapter.MyViewHolder holder = (ItemDownloadAdapter.MyViewHolder) recyc_download.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.updateDownloadProgress(mTotalTotal, mTotalCurrent, speed);
                MovieDownload item = downloadAdapter.getItem(position);
                if (item != null) {
                    item.setmTotalCurrent(mTotalCurrent);
                    item.setSpeed(speed);
                }
            }
        }
    }

    /**
     * update movie download status
     * @param mvId
     * @param downloadId
     * @param status
     * @param path
     * @param reason
     */
    public void updateStatus(int mvId, long downloadId, int status, String path, int reason) {
        int position = getMoviePostion(mvId, downloadAdapter);
        if (position < 0 && status != DownloadManager.STATUS_PENDING) {
            int pos = getMoviePostion(mvId, pendingAdapter);
            if (pos >= 0) {
                switch (status){
                    case DownloadManager.STATUS_RUNNING:
                    case DownloadManager.STATUS_FAILED:
                        if (pos >= 0) {
                            position = updateDownloadMovies(pos);
                        }
                        break;
                }
            }
        }

        if (position >= 0) {
            MovieDownload movieDownload = downloadAdapter.updateData(mvId, downloadId, status, path, reason);
            ItemDownloadAdapter.MyViewHolder holder = (ItemDownloadAdapter.MyViewHolder) recyc_download.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.updateStatus(movieDownload);
            }
        }
    }

    /**
     * update pending movie
     * @param pos
     */
    private int updateDownloadMovies(int pos) {
        MovieDownload pendingMovie = pendingAdapter.getItem(pos).cloneMovie();
        pendingAdapter.removeItem(pos);

        int downloading_index = 0;
        //get a pending movie and add to download list and replace pending movie list dir
        if (downloadAdapter.getItemCount() > 0) {
            //check first place whether is pending movie dir
            MovieDownload item = downloadAdapter.getItem(0);
            if(item.getMvId() == -1) { //first place is pending movie dir
                //refresh pending movie
                if (pendingAdapter.getItemCount() > 0) {
                    MovieDownload movieDir = pendingAdapter.getItem(0).cloneMovie();
                    movieDir.setMvId(-1);
                    downloadAdapter.replaceItem(0, movieDir);
                    downloading_index = 1;
                } else {
                    downloadAdapter.removeItem(0);
                }
            }
        }
        downloadAdapter.addItem(downloading_index, pendingMovie);
        return downloading_index;
    }

    /**
     * get movie postion
     * @param mvId
     * @return
     */
    private int getMoviePostion(int mvId, ItemDownloadAdapter adapter) {
        int position = -1;
        if (adapter != null && adapter.getItemCount() > 0) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItem(i) != null && adapter.getItem(i).getMvId() == mvId) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public void deleteDownload(MovieDownload... info) {
        downloadAdapter.removeItem(info);
        if (pendingAdapter != null) {
            if (pendingAdapter.getItemCount() > 0) {
                pendingAdapter.removeItem(info);
            }

            if (pendingAdapter.getItemCount() == 0 || downloadAdapter.hasPendingMovie()) {
                downloadAdapter.removeItem(0);
            }
        }
        showWifiStateNotification();
    }

    @Override
    public void showDialogWhenFailMac() {
        final CustomDialogNotMac dialogBuilder = CustomDialogNotMac.getInstance(getContext());
        dialogBuilder.withTitle(getContext().getResources().getString(R.string.textdialognotmac))
                .withBtnOkText("Ok").setOkClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        }).show();
    }

    @Override
    public void showDialogFailDownload() {
        final CustomDialogNotMac dialogBuilder = CustomDialogNotMac.getInstance(getContext());
        dialogBuilder.withTitle(getContext().getResources().getString(R.string.faildownloadright))
                .withBtnOkText("Ok").setOkClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        }).show();
    }

    @Override
    public void updateStorage() {
        String prefix = "Free: ";
        List<String> storages = presenterMainImp.getAllStorageLocations();
        tv_storage.setText(prefix + OgleHelper.formatAvailableSpace(storages.get(0)));
        if (storages.size() > 1) { //has sd card
            rl_sd_storage.setVisibility(View.VISIBLE);
            tv_sd_storage.setText(prefix + OgleHelper.formatAvailableSpace(storages.get(1)));
        } else {
            rl_sd_storage.setVisibility(View.GONE);
        }

        if (mOgleApp.isSDCard && storages.size() > 1) {
            rb_sd_storage.setChecked(true);
            rb_storage.setChecked(false);
        } else {
            rb_storage.setChecked(true);
            rb_sd_storage.setChecked(false);
        }
    }

    public void onStorageStateChanged(int state) {
        updateStorage();
        showDownloadList();
    }

    @Override
    public void showDialogPendingDownload() {
        pendingDialog = new CustomDialogPendingMovies(getContext(), this, pendingAdapter);
        pendingDialog.show();
    }

    @Override
    public void updatePendingMovies(int items) {
        MovieDownload item = null;
        if (downloadAdapter != null && downloadAdapter.getItemCount() > 0) {
            item = downloadAdapter.getItem(0);
        }

        if (items == 0) {
            if (pendingDialog != null) {
                pendingDialog.dismiss();
            }

            if (item.getMvId() == -1) {
                downloadAdapter.removeItem(0);
            }
        } else {
            MovieDownload item1 = pendingAdapter.getItem(0);
            MovieDownload pendingMovie = item1.cloneMovie();
            pendingMovie.setMvId(-1);
            if (item.getMvId() == -1) {
                downloadAdapter.replaceItem(0, pendingMovie);
            } else {
                downloadAdapter.addItem(0, pendingMovie);
            }
        }
        presenterMainImp.updateDownloadBar();
    }

    public void updateStatusFail(int mvId, int typeDownload, int typeCodeRespone) {
        if (downloadAdapter != null) {
            downloadAdapter.updateDataFail(mvId, typeDownload, typeCodeRespone);
        }
    }

    public void updateSttAtChangeNetWork(int flagNetWork) {
        if (downloadAdapter != null) {
            downloadAdapter.updateNetworkState();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_storage:
                if (!rb_storage.isChecked()) {
                    rb_storage.setChecked(true);
                    rb_sd_storage.setChecked(false);
                    mOgleApp.changeStorage(0);
                    mOgleApp.showToast("Change movie storage to Internal Storage", Gravity.CENTER);
                }
                break;
            case R.id.rl_sd_storage:
                if (!rb_sd_storage.isChecked()) {
                    rb_sd_storage.setChecked(true);
                    rb_storage.setChecked(false);
                    mOgleApp.changeStorage(1);
                    mOgleApp.showToast("Change movie storage to SD Card", Gravity.CENTER);
                }
                break;
        }
    }

    /**
     * load data
     */
    public void loadData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final MovieDownloadList movieDownloadList = classifyMovieDownloadsByType();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDownloadAdapter(movieDownloadList);
                    }
                });
            }
        });
    }

    /**
     * load download list
     */
    public void showDownloadList() {
        updateDownloadAdapter(classifyMovieDownloadsByType());
    }

    private void updateDownloadAdapter(MovieDownloadList movieDownloadList) {
        updateStorage();
        iv_edit.setVisibility(View.GONE);
        ll_nomovie.setVisibility(View.GONE);
        recyc_download.setVisibility(View.GONE);
        if (movieDownloadList != null && !movieDownloadList.isEmpty()) {
            recyc_download.setVisibility(View.VISIBLE);
            List<MovieDownload> pendingList = movieDownloadList.getPendingList();
            List<MovieDownload> downloadingList = movieDownloadList.getDownloadingList();
            List<MovieDownload> downloadList = movieDownloadList.getDownloadList();

            boolean showWifiState = false;
            if (!downloadingList.isEmpty()) { //add first place is downloading movie
                downloadList.add(0, downloadingList.get(0));
                showWifiState = true;
            }
            //handle pending list
            if (!pendingList.isEmpty()) {
                showWifiState = true;
                MovieDownload pendingMovie = pendingList.get(0).cloneMovie();
                pendingMovie.setMvId(-1);
                downloadList.add(0, pendingMovie);
                pendingAdapter = new ItemDownloadAdapter(getActivity(), pendingList, presenterMainImp, this);
            }

            if (!downloadList.isEmpty()) {
                downloadAdapter.updateData(downloadList);
                recyc_download.setAdapter(downloadAdapter);
            }

            if (showWifiState) {
                showWifiStateNotification();
                return;
            }
        } else {
            ll_nomovie.setVisibility(View.VISIBLE);
        }
        hideWifiStateNotification();
    }

    /**
     * classify movie downloads by type
     */
    private MovieDownloadList classifyMovieDownloadsByType() {
        MovieDownloadList movieDownloadList = new MovieDownloadList();
        List<MovieDownload> mList = DataBaseHelper.getInstance().getAllMovieDownload();
        manager = new DownloadManager(mOgleApp.getContentResolver(), mOgleApp.getPackageName());
        DownloadManager.Query query = new DownloadManager.Query();
        if (mList != null && !mList.isEmpty()) {
            for (MovieDownload mvDownload : mList) {
                switch (mvDownload.getTypeDownload()){
                    case 0:
                    case 1:
                        movieDownloadList.getPendingList().add(mvDownload);
                        break;
                    case 2:
                        movieDownloadList.getDownloadList().add(mvDownload);
                        break;
                    default:
                        query.setFilterById(mvDownload.getIdDownload());
                        Cursor cursor = manager.query(query);
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            int status = cursor.getInt(columnIndex);
                            switch (status) {
                                case DownloadManager.STATUS_SUCCESSFUL:
                                    movieDownloadList.getDownloadList().add(mvDownload);
                                    break;
                                case DownloadManager.STATUS_RUNNING:
                                    movieDownloadList.getDownloadingList().add(mvDownload);
                                    break;
                                case DownloadManager.STATUS_PAUSED:
                                case DownloadManager.STATUS_PENDING:
                                    movieDownloadList.getDownloadingList().add(mvDownload);
                                    break;
                                case DownloadManager.STATUS_FAILED:
                                    movieDownloadList.getDownloadList().add(mvDownload);
                                    break;
                            }
                            cursor.close();
                        }
                        break;
                }
            }
        }
        return movieDownloadList;
    }

    @Override
    public int getPendingDownloadCount() {
        if (pendingAdapter != null) {
            return pendingAdapter.getItemCount();
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "---------onResume-------");
        if (downloadAdapter != null) {
            downloadAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteMovies(List<MovieDownload> downloadList){
        List<MovieDownload> deleteList = new ArrayList<>();
        if (downloadList != null && !downloadList.isEmpty()) {
            for (MovieDownload movieDownload: downloadList) {
                if (movieDownload.getMvId() == -1) {
                    List<MovieDownload> pendingList = pendingAdapter.getDownloadList();
                    if (pendingList != null) {
                        for (MovieDownload mvDownload: pendingList) {
                            deleteList.add(mvDownload);
                        }
                    }
                } else {
                    deleteList.add(movieDownload);
                }
            }
        }
        if (!deleteList.isEmpty()) {
            presenterMainImp.deleteMovie(deleteList.toArray(new MovieDownload[deleteList.size()]));
        }
    }

    @Override
    public void closeDialog() {
        downloadListener.checkShowEditBtn();
    }
}

class MovieDownloadList {
    List<MovieDownload> pendingList = new ArrayList<>();
    List<MovieDownload> downloadingList = new ArrayList<>();
    List<MovieDownload> downloadList = new ArrayList<>();

    public List<MovieDownload> getPendingList() {
        return pendingList;
    }

    public List<MovieDownload> getDownloadingList() {
        return downloadingList;
    }

    public List<MovieDownload> getDownloadList() {
        return downloadList;
    }

    public boolean isEmpty() {
        if (pendingList.isEmpty() && downloadingList.isEmpty() && downloadList.isEmpty()) {
            return true;
        }
        return false;
    }
}
