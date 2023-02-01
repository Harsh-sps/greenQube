package com.aistream.greenqube.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.adapter.MainTabFragmentAdapter;
import com.aistream.greenqube.customs.CustomViewPager;
import com.aistream.greenqube.layout.Layout_Filter;
import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.VideoType;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.view.ViewLibrary;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhuDepTraj on 4/9/2018.
 */

@SuppressLint("ValidFragment")
public class Fragment_Library extends Fragment implements ViewLibrary, View.OnClickListener {
    private String TAG = "FragmentLibrary";
    private ViewMain viewMain;
    private PresenterMainImp presenterMainImp;

    private Layout_Filter filter;
    private ImageView ic_search;
    private FrameLayout fl_search;

    private Layout_Filter layout_filter;
    private FrameLayout frm_Genre;
    private FrameLayout frm_detail;
    private LinearLayout ll_frmMov;
    private CustomViewPager main_viewpager;
    private TabLayout main_tab;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<VideoType> videoTypes = new ArrayList<>();

    public Fragment_Library() {
        super();
    }

    @SuppressLint("ValidFragment")
    public Fragment_Library(PresenterMainImp presenterMainImp, ViewMain viewMain) {
        this.viewMain = viewMain;
        this.presenterMainImp = presenterMainImp;
        Log.i(TAG, "create library");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenterMainImp != null) {
            fragmentList = new ArrayList<>();
          //  fragmentList.add(new FragmentVIPMovies(presenterMainImp, viewMain));
            //fragmentList.add(new FragmentMovies(presenterMainImp, viewMain));

            videoTypes = presenterMainImp.getVideoTypes();
            for (VideoType videoType : videoTypes) {
                fragmentList.add(new FragmentShortVideo(presenterMainImp, viewMain, videoType));
            }
        }
        Log.i(TAG, "Library onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "Library onCreateView");
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        main_tab = (TabLayout) view.findViewById(R.id.main_tab);
        main_viewpager = (CustomViewPager) view.findViewById(R.id.main_viewpager);
        ic_search = (ImageView) view.findViewById(R.id.ic_search);
        fl_search = (FrameLayout) view.findViewById(R.id.fl_search);
        ll_frmMov = (LinearLayout) view.findViewById(R.id.ll_frmMov);
        frm_detail = (FrameLayout) view.findViewById(R.id.frm_detail);
        frm_Genre = (FrameLayout) view.findViewById(R.id.frm_Genre);

        main_tab.removeAllTabs();
        //fragment movies
       // addFragmentTab("VIP");
        //fragment movies
       // addFragmentTab("Movies");
        //fragment video types
        for (VideoType videoType : videoTypes) {
            addFragmentTab(videoType.getName().equalsIgnoreCase("Others") ? "Kids" : videoType.getName());
        }

        MainTabFragmentAdapter mainTabFragmentAdapter = new MainTabFragmentAdapter(getChildFragmentManager(), fragmentList);
        main_viewpager.setAdapter(mainTabFragmentAdapter);
        //cancel viewpager left/right scroll
        main_viewpager.setPagingEnabled(false);
        main_viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(main_tab));

        main_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabView(tab, true);
                main_viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabView(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        main_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ViewLibrary viewLibrary = (ViewLibrary) fragmentList.get(position);
                viewLibrary.loadDataLibrary();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        fl_search.setOnClickListener(this);

    }

    /**
     * update tab view
     *
     * @param tab
     * @param isSelect
     */
    private void updateTabView(TabLayout.Tab tab, boolean isSelect) {
        TextView tv_title = (TextView) tab.getCustomView().findViewById(R.id.tv_title);
        FrameLayout tv_line = (FrameLayout) tab.getCustomView().findViewById(R.id.tv_line);
        tv_line.setVisibility(View.GONE);
        int colorId = R.color.color222;
        Typeface typeface = Typeface.DEFAULT;
        if (isSelect) {
            colorId = R.color.green;
            typeface = Typeface.DEFAULT_BOLD;
            tv_line.setVisibility(View.VISIBLE);
        }
        tv_title.setSelected(isSelect);
        tv_title.setTextColor(getContext().getResources().getColor(colorId));
        tv_title.setTypeface(typeface);
    }

    private void addFragmentTab(String tag) {
        TabLayout.Tab newTab = main_tab.newTab();
        newTab.setCustomView(getTabView(tag));
        newTab.setTag(tag);
        main_tab.addTab(newTab);
    }

    private View getTabView(String title) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.main_genre_tab, null);
        TextView txt_title = (TextView) view.findViewById(R.id.tv_title);
        txt_title.setText(title);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_search:
                ll_frmMov.setVisibility(View.GONE);
                frm_detail.setVisibility(View.VISIBLE);
                layout_filter = new Layout_Filter(getContext(), this,
                        presenterMainImp, 0, PresenterMainImp.ALL, "");
                frm_detail.addView(layout_filter);
                break;
        }
    }

    @Override
    public void loadDataLibrary() {
        onResume();
        if (main_tab != null && !fragmentList.isEmpty()) {
            int selectedTabPosition = main_tab.getSelectedTabPosition();
            ViewLibrary viewLibrary = (ViewLibrary) fragmentList.get(selectedTabPosition);
            if (viewLibrary != null) {
                viewLibrary.loadDataLibrary();
            }
        }
    }

    @Override
    public void refreshData() {
        onResume();
        if (main_tab != null && !fragmentList.isEmpty()) {
            int selectedTabPosition = main_tab.getSelectedTabPosition();
            ViewLibrary viewLibrary = (ViewLibrary) fragmentList.get(selectedTabPosition);
            if (viewLibrary != null) {
                viewLibrary.refreshData();
            }
        }
    }

    @Override
    public void showHideFrameAllMov(int mode, int genID, String genName) {
    }

    @Override
    public void showMoviePromotion(List<MovieInfo> listMoviePromotion) {
    }

    @Override
    public void showContinueWatching(List<MovieDownload> listContinue) {
    }

    @Override
    public void showMovieDetail(MovieInfo movieInfo) {
        viewMain.showMovieDetail(movieInfo);
    }

    @Override
    public void showListMovieOfGenre(List<MovieInfo> movieInfoList, Genre genre) {

    }

    @Override
    public void onBackpress() {
        ll_frmMov.setVisibility(View.VISIBLE);
        frm_detail.setVisibility(View.GONE);
        layout_filter = null;

        ViewLibrary viewLibrary = getViewLibrary();
        if (viewLibrary != null) {
            viewLibrary.onBackpress();
        }
    }

    @Override
    public void showMovieVIP(TextView view, MovieInfo movieInfo) {
        viewMain.showMovieVIP(view, movieInfo);
    }

    @Override
    public boolean checkVisbleFrameSearch() {
        if (layout_filter != null && frm_detail.indexOfChild(layout_filter) != -1) {
            return true;
        }

        ViewLibrary viewLibrary = getViewLibrary();
        if (viewLibrary != null) {
            return viewLibrary.checkVisbleFrameSearch();
        }
        return false;
    }

    @Override
    public void hideParentFragment() {
        frm_Genre.setVisibility(View.GONE);
    }

    @Override
    public void showParentFragment() {
        frm_Genre.setVisibility(View.VISIBLE);
        layout_filter = null;
    }

    @Override
    public List<MovieInfo> loadMovieFromGen(int genreId) {
        ViewLibrary viewLibrary = getViewLibrary();
        if (viewLibrary != null) {
            return getViewLibrary().loadMovieFromGen(genreId);
        } else {
            return new ArrayList<>();
        }
    }

    private ViewLibrary getViewLibrary() {
        int currentItem = main_viewpager.getCurrentItem();
        ViewLibrary viewLibrary = (ViewLibrary) fragmentList.get(currentItem);
        return viewLibrary;
    }
}
