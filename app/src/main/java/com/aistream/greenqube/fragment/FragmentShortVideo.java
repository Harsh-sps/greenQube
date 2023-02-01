package com.aistream.greenqube.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aistream.greenqube.R;
import com.aistream.greenqube.adapter.ItemMovieContinueAdapter;
import com.aistream.greenqube.adapter.ItemMovieSectionAdapter;
import com.aistream.greenqube.customs.CustomDialog_MovieDetail;
import com.aistream.greenqube.layout.Layout_Filter;
import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.VideoType;
import com.aistream.greenqube.mvp.presenter.PresenterLibraryImp;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.view.ViewLibrary;
import com.aistream.greenqube.mvp.view.ViewMain;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by PhuDepTraj on 4/9/2018.
 */

public class FragmentShortVideo extends Fragment implements ViewLibrary, View.OnClickListener {
    private String TAG = "FragmentShortVideo";
    private ViewMain viewMain;
    private CoordinatorLayout corrdinar;
    private RecyclerView re_allmovie;
    private RecyclerView re_continueWatch;
    private CircleIndicator cirindicator;
    private PresenterMainImp presenterMainImp;
    public PresenterLibraryImp presenterLibraryImp;
    //
    private LinearLayout ll_frmMov;
    private LinearLayout ll_Arrival;
    private LinearLayout ll_hotmovie;
    private LinearLayout ll_allgenre;
    private LinearLayout ll_framebody;
    private FrameLayout frm_detail;
    private TextView tv_seeallallgenre;
    private TextView tv_seeallhotmovie;
    private TextView tv_seeallArrival;
    private FrameLayout frm_poster;
    private TextView title_toolbar;
    private TabLayout main_tab;

    private CustomDialog_MovieDetail movieDetail;
    private Layout_Filter layout_filter;
    private ItemMovieContinueAdapter continueAdapter;
    private int genreId = 1000;
    private VideoType videoType;

    //new design
    private ItemMovieSectionAdapter itemMovieSectionAdapter;

    public FragmentShortVideo() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FragmentShortVideo(PresenterMainImp presenterMainImp, ViewMain viewMain, VideoType videoType) {
        this.viewMain = viewMain;
        this.presenterMainImp = presenterMainImp;
        this.videoType = videoType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_videos, container, false);
        initView(view);

        presenterLibraryImp = new PresenterLibraryImp(getContext(), this,
                presenterMainImp, videoType, PresenterLibraryImp.ALL);
        presenterLibraryImp.showDataLibrary();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initView(View view) {
        corrdinar = (CoordinatorLayout) view.findViewById(R.id.corrdinar);
        ll_frmMov = (LinearLayout) view.findViewById(R.id.ll_frmMov);
        frm_detail = (FrameLayout) view.findViewById(R.id.frm_detail);
        ll_hotmovie = (LinearLayout) view.findViewById(R.id.ll_hotmovie);
        ll_allgenre = (LinearLayout) view.findViewById(R.id.ll_allgenre);
        tv_seeallallgenre = (TextView) view.findViewById(R.id.tv_seeallallgenre);
        tv_seeallhotmovie = (TextView) view.findViewById(R.id.tv_seeallhotmovie);
        frm_poster = (FrameLayout) view.findViewById(R.id.frm_poster);
        title_toolbar = (TextView) view.findViewById(R.id.title_toolbar);
        cirindicator = (CircleIndicator) view.findViewById(R.id.cirindicator);

        // continue watch
        re_continueWatch = (RecyclerView) view.findViewById(R.id.re_hotmovie);
        re_continueWatch.setHasFixedSize(true);
        re_continueWatch.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        re_continueWatch.setNestedScrollingEnabled(false);

        // all movies
        re_allmovie = (RecyclerView) view.findViewById(R.id.re_allmovie);
        itemMovieSectionAdapter = new ItemMovieSectionAdapter(getActivity(), presenterMainImp, this, false);
        re_allmovie.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        re_allmovie.setHasFixedSize(true);
        re_allmovie.setAdapter(itemMovieSectionAdapter);

        tv_seeallallgenre.setOnClickListener(this);
        tv_seeallhotmovie.setOnClickListener(this);
    }

    @Override
    public void showHideFrameAllMov(int mode, int genID, String genName) {
        frm_detail.removeAllViews();
        hideParentFragment();
        if (mode == 0) {// search movie
            ll_frmMov.setVisibility(View.GONE);
            frm_detail.setVisibility(View.VISIBLE);
            viewMain.showHideToolBar(0);
            layout_filter = new Layout_Filter(getContext(), this, presenterMainImp, 0, genID, genName);
            frm_detail.addView(layout_filter);
        } else if (mode == 1) {//show genre movies
            genreId = genID;
            ll_frmMov.setVisibility(View.GONE);
            frm_detail.setVisibility(View.VISIBLE);
            viewMain.showHideToolBar(0);
            layout_filter = new Layout_Filter(getContext(), this, presenterMainImp, 1, genID, genName);
            frm_detail.addView(layout_filter);
        } else if (mode == 2) {// back to frame
            ll_frmMov.setVisibility(View.VISIBLE);
            frm_detail.setVisibility(View.GONE);
            viewMain.showHideToolBar(2);
            showParentFragment();
        }
    }

    @Override
    public void showMoviePromotion(List<MovieInfo> listMoviePromotion) {
    }

    @Override
    public void showContinueWatching(List<MovieDownload> listContinue) {
        if (listContinue.size() == 0) {
            ll_hotmovie.setVisibility(View.GONE);
            tv_seeallhotmovie.setVisibility(View.GONE);
        } else {
            ll_hotmovie.setVisibility(View.VISIBLE);
            if (listContinue.size() < 4) {
                tv_seeallhotmovie.setVisibility(View.GONE);
            } else {
                tv_seeallhotmovie.setVisibility(View.VISIBLE);
            }
            continueAdapter = new ItemMovieContinueAdapter(getContext(), listContinue, this, presenterMainImp);
            re_continueWatch.setAdapter(continueAdapter);
            if (layout_filter != null && layout_filter.getGenID() == 1000) {
                layout_filter.showAllMovieContinue(listContinue);
            }
        }
    }

    @Override
    public void showMovieDetail(MovieInfo movieInfo) {
        viewMain.showMovieDetail(movieInfo);
    }

    @Override
    public void showListMovieOfGenre(List<MovieInfo> movieInfoList, Genre genre) {
        itemMovieSectionAdapter.updateSectionMovie(genre, movieInfoList);
    }

    @Override
    public void onBackpress() {
        if (layout_filter != null && frm_detail.indexOfChild(layout_filter) != -1) {
            presenterLibraryImp.onBackPress(2);
        }
    }

    @Override
    public void showMovieVIP(TextView view, MovieInfo movieInfo) {
        viewMain.showMovieVIP(view, movieInfo);
    }

    @Override
    public boolean checkVisbleFrameSearch() {
        if (layout_filter != null && frm_detail.indexOfChild(layout_filter) != -1 && frm_detail.isShown()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_seeallhotmovie:
                showHideFrameAllMov(1, 1000, "");//
                presenterLibraryImp.loadContinueWatching();
                break;
            case R.id.ic_search:
                showHideFrameAllMov(0, genreId, "");
                break;
        }
    }

    /**
     * load data
     */
    @Override
    public void loadDataLibrary() {
        if (presenterLibraryImp != null) {
            presenterLibraryImp.loadContinueWatching();
        }
    }

    @Override
    public void hideParentFragment() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof ViewLibrary) {
            ViewLibrary viewLibrary = (ViewLibrary) parentFragment;
            viewLibrary.hideParentFragment();
        }
    }

    @Override
    public void showParentFragment() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof ViewLibrary) {
            ViewLibrary viewLibrary = (ViewLibrary) parentFragment;
            viewLibrary.showParentFragment();
        }
    }

    @Override
    public void refreshData() {
        if (presenterLibraryImp != null) {
            itemMovieSectionAdapter.clearMovieSectionList();
            presenterLibraryImp.showDataLibrary();
        }
    }

    @Override
    public List<MovieInfo> loadMovieFromGen(int genreId) {
        if (presenterLibraryImp != null) {
            return presenterLibraryImp.loadMovieFromGen(genreId);
        }
        return new ArrayList<>();
    }
}
