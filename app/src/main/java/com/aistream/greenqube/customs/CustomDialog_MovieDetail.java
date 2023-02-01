package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.adapter.ItemAdapterMoreLikeThis;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.Actor;
import com.aistream.greenqube.mvp.model.Director;
import com.aistream.greenqube.mvp.model.DownloadResult;
import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.util.DialogCallBack;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.R;
import com.aistream.greenqube.services.DownloadManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PhuDepTraj on 3/19/2018.
 */

public class CustomDialog_MovieDetail extends Dialog implements View.OnClickListener {
    private OgleApplication ogleApplication;
    private ViewMain viewMain;
    private Context mContext;
    private MovieInfo movieInfo;
    private TextView tv_name;
    private TextView tv_viewdown;
    //    private FrameLayout img_backdetail;
    private ImageView img_poster;
    private TextView tv_vip;
    private RecyclerView re_more;
    private LinearLayout ll_share;
    private ItemAdapterMoreLikeThis adapterMoreLikeThis;
    private DataBaseHelper dataBaseHelper;
    //    private ViewLibrary viewLibrary;
    private LinearLayout ll_addFav;
    private ImageView img_addfav;
    private PresenterMainImp presenterMainImp;
    private LinearLayout frm_synopsis;
    //    private TextView tv_synopsis;
    private boolean aniSynopsis;
    private TextView tv_namesyn;
    private TextView genrename;
    private TextView tv_des;
    private TextView derecter;
    private TextView tv_actor;
    private ImageView img_download;
    private TextView tv_status;
    private TextView tv_statusdownload;
    private TextView tv_moviePre;
    private FrameLayout frm_download;
    private ImageView img_backdetail;
    private Button btn_download;
    private TextView txt_timeMovie;
    private AppBarLayout appbar;
    //    private FrameLayout btn_close;
    SharedPreferences mPref;
    private LinearLayout backDetail;
    private TextView releasedate;
    private TextView tv_filesize;
    private LinearLayout ll_more;
    private LinearLayout ll_bar;

    public CustomDialog_MovieDetail(@NonNull Context context, ViewMain view, PresenterMainImp mainImp, MovieInfo movieInfo) {
        super(context, R.style.AppThemeDialog);
        ogleApplication = (OgleApplication) context.getApplicationContext();
        this.viewMain = view;
        this.presenterMainImp = mainImp;
        dataBaseHelper = DataBaseHelper.getInstance();
        this.mContext = context;
        this.movieInfo = movieInfo;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_moviedetail);

        initView();
        loadDataDetail(movieInfo);
        Log.i("OnCreate", "Movie Detail");
    }

    private void initView() {
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_viewdown = (TextView) findViewById(R.id.tv_viewdown);
        img_poster = (ImageView) findViewById(R.id.img_poster);
        tv_vip = (TextView) findViewById(R.id.tv_vip);
        ll_share = (LinearLayout) findViewById(R.id.ll_share);
        ll_addFav = (LinearLayout) findViewById(R.id.ll_addFav);
        img_addfav = (ImageView) findViewById(R.id.img_addfav);
        backDetail = (LinearLayout) findViewById(R.id.backDetail);
        genrename = (TextView) findViewById(R.id.genrename);
        tv_des = (TextView) findViewById(R.id.tv_des);
        derecter = (TextView) findViewById(R.id.derecter);
        tv_actor = (TextView) findViewById(R.id.actor);
        img_download = (ImageView) findViewById(R.id.img_download);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_moviePre = (TextView) findViewById(R.id.tv_moviePre);
        frm_download = (FrameLayout) findViewById(R.id.frm_download);
        img_backdetail = (ImageView) findViewById(R.id.img_backdetail);
        btn_download = (Button) findViewById(R.id.btn_download);
        txt_timeMovie = (TextView) findViewById(R.id.txt_timeMovie);
        releasedate = (TextView) findViewById(R.id.releasedate);
        tv_filesize = (TextView) findViewById(R.id.filesize);
        ll_more = (LinearLayout) findViewById(R.id.ll_more);
        ll_bar = (LinearLayout) findViewById(R.id.ll_bar);

        re_more = (RecyclerView) findViewById(R.id.re_more);
        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        re_more.setLayoutManager(manager);
        GridSpacingItemDecoration.Builder builder = new GridSpacingItemDecoration.Builder();
        builder.horizontalSpacing((int) mContext.getResources().getDimension(R.dimen.size5));
        builder.verticalSpacing((int) mContext.getResources().getDimension(R.dimen.size10));
        re_more.addItemDecoration(new GridSpacingItemDecoration(builder));
        re_more.setItemAnimator(new DefaultItemAnimator());

        ll_addFav.setOnClickListener(this);
        frm_download.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        btn_download.setOnClickListener(this);
        backDetail.setOnClickListener(this);
    }

    /**
     * check whether show play button
     */
    private void checkShowPlayBtn() {
        boolean isShow = false;
        if (Config.isCheckMac()) {
            isShow = true;
        } else {
            MovieDownload movieDownload = DataBaseHelper.getInstance().getMovieDownload(movieInfo.getMovieId());
            if (movieDownload != null) {
                if (movieDownload.hasStartDownloaded()) {
                    isShow = true;
                }
            }
        }

        if (isShow) {
            frm_download.setVisibility(View.VISIBLE);
        } else {
            frm_download.setVisibility(View.GONE);
        }
    }

    public void loadDataDetail(MovieInfo info) {
        appbar.setExpanded(true);
        this.movieInfo = info;
        checkShowPlayBtn();
        tv_name.setText(info.getName());
        viewMain.showMovieVIP(tv_vip, info);
        txt_timeMovie.setText(OgleHelper.getDurationString(info.getDuration()));
        releasedate.setText(" " + info.getReleaseDate().substring(0, 10));
        tv_filesize.setText("Size: " + info.getFileSize() + "MB");
        info.setMovieBilling(ogleApplication.getMovieBillingInfo(info.getMovieId()));
        OgleHelper.showMoviePrice(mContext, tv_viewdown, info);
        String director = "";
        if (info.getDirectors() != null) {
            for (Director director1 : info.getDirectors()) {
                if (director.equals("")) {
                    director = director + director1.getName();
                } else {
                    director = director + ", " + director1.getName();
                }
            }
        }
        derecter.setText(" " + director);

        String actor = "";
        if (info.getActors() != null) {
            for (Actor actor1 : info.getActors()) {
                if (actor.equals("")) {
                    actor = actor + actor1.getName();
                } else {
                    actor = actor + ", " + actor1.getName();
                }
            }
        }
        tv_actor.setText(" " + actor);
        String since = info.getReleaseDate();
        String genreName = "";
        if (info.getGenres() != null) {
            for (Genre genre : info.getGenres()) {
                if (genreName.equals("")) {
                    genreName = genreName + genre.getName();
                } else {
                    genreName = genreName + ", " + genre.getName();
                }
            }
        }
        genrename.setText("Genre(s): " + genreName);
        tv_des.setText(info.getSynopsis());
        Picasso.with(mContext).load(Config.picURLPath + info.getPreview()).fit().centerCrop().config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2).into(img_poster);
        loadMore(info);
        if (isFav(movieInfo.getMovieId())) {
            img_addfav.setBackgroundResource(R.drawable.ic_fullheart);
        } else {
            img_addfav.setBackgroundResource(R.drawable.ic_heart);
        }
        //update download status
        updateStatus();

        ll_more.measure(0, 0);
        int height = ll_more.getMeasuredHeight();
        int screenHeight = mPref.getInt("ScreenHeight", 0);
        Log.d("Measure", "screen height: "+screenHeight+", more like recycle height: "+height);
        if (screenHeight > 0) {
            ll_bar.setMinimumHeight(screenHeight - height - 30);
        }
    }

    /**
     * check current movie whether favorite movie
     * @param mvId
     * @return
     */
    private boolean isFav(int mvId) {
        String fIds = mPref.getString("favorites", "");
        String[] ids = fIds.split(",");
        for (String id: ids) {
            if (id.equals(String.valueOf(mvId))) {
                return true;
            }
        }
        return false;
    }

    /**
     * add / delete fav
     * @param mvId
     */
    private void updateFav(int mvId) {
        String fIds = mPref.getString("favorites", "0");
        fIds = "," + fIds + ",";
        if (!(fIds.contains("," + mvId + ","))) {
            fIds += mvId + ",";
            img_addfav.setBackgroundResource(R.drawable.ic_fullheart);
        } else {
            fIds = fIds.replace("," + mvId + ",", ",");
            img_addfav.setBackgroundResource(R.drawable.ic_heart);
        }

        if (fIds.length() > 1) {
            fIds = fIds.substring(1, fIds.length() - 1);
        } else if (fIds.length() == 1) {
            fIds = "";
        }
        mPref.edit().putString("favorites", fIds).commit();
    }

    private void loadMore(MovieInfo movieInfo) {
        Map<Integer, MovieInfo> movieMap = new HashMap<>();
        for (Genre genre : movieInfo.getGenres()) {
            List<MovieInfo> movies = presenterMainImp.loadMovieFromGen(genre.getId());
            if (movies != null) {
                for (MovieInfo movie: movies) {
                    if (movie.getMovieId() != movieInfo.getMovieId()
                            && movieInfo.isMatchType(movie.getVideoType())) {
                        movieMap.put(movie.getMovieId(), movie);
                    }
                }
            }
        }

        List<MovieInfo> movieList = new ArrayList<>();
        List<MovieInfo> listMore = new ArrayList<>(movieMap.values());
        if (listMore.size() > 6) {
            if (movieInfo.getVideoType() == null) {
                List<MovieInfo> movies = new ArrayList<>();
                List<MovieInfo> vipMovies = new ArrayList<>();
                List<MovieInfo> freeMovies = new ArrayList<>();
                for (MovieInfo mvInfo: listMore) {
                    if (mvInfo.isVip()) {
                        vipMovies.add(mvInfo);
                    } else {
                        freeMovies.add(mvInfo);
                    }
                }

                if (vipMovies.size() < freeMovies.size()) {
                    selectMovies(vipMovies, 3, movieList);
                    selectMovies(freeMovies, 6 - movieList.size(), movieList);
                } else {
                    selectMovies(freeMovies, 3, movieList);
                    selectMovies(vipMovies, 6 - movieList.size(), movieList);
                }
            } else {
                selectMovies(listMore, 6, movieList);
            }
        } else {
            movieList = listMore;
        }
        adapterMoreLikeThis = new ItemAdapterMoreLikeThis(mContext, movieList, viewMain);
        re_more.setAdapter(adapterMoreLikeThis);
    }

    private void selectMovies(List<MovieInfo> source, int num, List<MovieInfo> target) {
        if (source.size() < num) {
            target.addAll(source);
        } else {
            Collections.shuffle(source);
            target.addAll(source.subList(0, num));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backDetail:
                dismiss();
                break;
            case R.id.ll_addFav:
                updateFav(movieInfo.getMovieId());
                break;
            case R.id.ll_share:
                Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.notavailable), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case R.id.frm_download:
                checkMovieDownload(new DialogCallBack() {
                    @Override
                    public void ok() {
                        presenterMainImp.playMovie(movieInfo, new DialogCallBack() {
                            @Override
                            public void ok() {
                                delayDownload();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }
                    @Override
                    public void cancel() {
                    }
                });
                break;
            case R.id.btn_download:
                checkMovieDownload(new DialogCallBack() {
                    @Override
                    public void ok() {
                        mPref.edit().putBoolean("onlinePlay", false).commit();
                        downloadMovie();
                    }
                    @Override
                    public void cancel() {
                    }
                });
                break;
        }
    }

    private void checkMovieDownload(final DialogCallBack callBack) {
        if (movieInfo != null && presenterMainImp.checkMoviePermission(movieInfo.getMovieId())) {
            if (callBack != null) callBack.ok();
        }
    }

    private Handler handler = new Handler();
    private Runnable downloadTask = new Runnable() {
        @Override
        public void run() {
            downloadMovie();
        }
    };
    private void delayDownload() {
        clearDownload();
        handler.postDelayed(downloadTask, 2000);
    }

    public void clearDownload() {
        handler.removeCallbacks(downloadTask);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        clearDownload();
    }

    /**
     * download movie
     */
    private void downloadMovie() {
        presenterMainImp.processMovie(movieInfo.getMovieId());
    }

    public boolean isMovieMatch(int mvId) {
        if (mvId == movieInfo.getMovieId()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * update status
     */
    public void updateStatus() {
        MovieDownload movieDownload = DataBaseHelper.getInstance().getMovieDownload(movieInfo.getMovieId());
        if (movieDownload != null) {
            switch (movieDownload.getTypeDownload()) {
                case 0:
                case 1:
                    goneIconAndStt(MovieDownload.PENDING, OgleHelper.showDownloadPendingMsg(mContext));
                    break;
                case 2:
                    goneIconAndStt(MovieDownload.FAIL, presenterMainImp.getDownloadErrorMsg(movieDownload.getTypeCodeRespon()));
                    break;
                case 3:
                    List<DownloadResult> results = ogleApplication.getMovieDownloadResultByIds(movieDownload.getIdDownload());
                    if (!results.isEmpty()) {
                        updateDownloadStatus(results.get(0).getStatus());
                    } else {
                        goneIconAndStt(MovieDownload.PENDING, OgleHelper.showDownloadPendingMsg(mContext));
                    }
                    break;
            }
        } else {
            goneIconAndStt(0, "");
        }
    }

    /**
     * update download status in download manager
     * @param status
     */
    public void updateDownloadStatus(int status) {
        switch (status) {
            case DownloadManager.STATUS_SUCCESSFUL:
                MovieDownload movieDownload = DataBaseHelper.getInstance().getMovieDownload(movieInfo.getMovieId());
                if (movieDownload == null || movieDownload.getDownloadSize() == 0) {
                    goneIconAndStt(MovieDownload.FAIL,
                            mContext.getResources().getString(R.string.downloadfilemission));
                } else {
                    goneIconAndStt(MovieDownload.SUCCESS, "");
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                goneIconAndStt(MovieDownload.FAIL,
                        mContext.getResources().getString(R.string.pauseisnotmac));
                break;
            case DownloadManager.STATUS_RUNNING:
                goneIconAndStt(MovieDownload.DOWNLOADING,
                        mContext.getResources().getString(R.string.downloading));
                break;
            case DownloadManager.STATUS_PENDING:
                goneIconAndStt(MovieDownload.PENDING,
                        mContext.getResources().getString(R.string.waitingconnection));
                break;
            case DownloadManager.STATUS_FAILED:
                goneIconAndStt(MovieDownload.FAIL,
                        mContext.getResources().getString(R.string.statusdownload4));
                break;
            default:
                goneIconAndStt(0, null);
                break;
        }
    }

    /**
     * show download icon with download status
     * @param type
     */
    public void goneIconAndStt(int type, String msg) {
        tv_status.setVisibility(View.GONE);
        btn_download.setEnabled(false);
        int drawId = 0;
        int colorId = R.color.green;
        if (type == 0) {
            drawId = R.drawable.download;
            btn_download.setEnabled(true);
        } else if (type == MovieDownload.PENDING) {
            drawId = R.drawable.download_queue;
        } else if (type == MovieDownload.DOWNLOADING) {
            drawId = R.drawable.downloading;
        } else if (type == MovieDownload.FAIL) {
            drawId = R.drawable.download_error;
            colorId = R.color.purple;
            btn_download.setEnabled(true);
        } else if (type == MovieDownload.SUCCESS) {
            drawId = R.drawable.ic_fav;
            boolean isDownloadSucc = true;
            MovieDownload movieDownload = dataBaseHelper.getMovieDownload(movieInfo.getMovieId());
            if (movieDownload != null && movieDownload.getExpireTime() > 0) {
                String expireDate = OgleHelper.getDateTime(movieDownload.getExpireTime());
                if (!movieDownload.hasExpired()) {
                    msg = String.format(mContext.getResources().getString(R.string.statusdownload5), expireDate);
                } else {
                    msg = String.format(mContext.getResources().getString(R.string.rentalperiodendtime), expireDate);
                    isDownloadSucc = false;
                }
            }
            if (!isDownloadSucc) {
                goneIconAndStt(MovieDownload.FAIL, msg);
                return;
            }
        }

        if (!TextUtils.isEmpty(msg)) {
            tv_viewdown.setText(msg);
            tv_viewdown.setTextColor(mContext.getResources().getColor(colorId));
        }

        if (drawId > 0) {
            Drawable drawable = ContextCompat.getDrawable(
                    mContext,
                    drawId);

            btn_download.setCompoundDrawablesWithIntrinsicBounds(
                    drawable, // Drawable left
                    null, // Drawable top
                    null, // Drawable right
                    null // Drawable bottom
            );
        }
    }

    /**
     * refresh data
     */
    public void refreshData() {
        this.movieInfo = presenterMainImp.getMovieInfo(movieInfo.getMovieId());
        initView();
        loadDataDetail(movieInfo);
        clearDownload();
    }
}
