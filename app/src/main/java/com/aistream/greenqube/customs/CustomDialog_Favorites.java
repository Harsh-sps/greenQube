package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.adapter.ItemAdapterFavorite;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.R;

import java.util.List;

/**
 * Created by PhuDepTraj on 3/19/2018.
 */

public class CustomDialog_Favorites extends Dialog implements View.OnClickListener {
    private ViewMain viewMain;
    private FrameLayout btn_back;
    private Context mContext;
    private RecyclerView re_favorite;
    private OgleApplication ogleApplication;
    TextView tv_nodata;
    ItemAdapterFavorite adapterFavorite;
    private SharedPreferences mPref;
    PresenterMainImp presenterMainImp;
    private ProgressBar progress_bar;

    public CustomDialog_Favorites(@NonNull Context context, ViewMain view, PresenterMainImp main) {
        super(context, R.style.AppThemeDialog);
        this.viewMain = view;
        this.mContext = context;
        this.presenterMainImp = main;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        ogleApplication = (OgleApplication) mContext.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_favorite);

        //show status bar
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);

        initView();
        showFavoriteMovie();
    }

    private void initView() {
        btn_back = (FrameLayout) findViewById(R.id.btn_back);
        re_favorite = (RecyclerView) findViewById(R.id.re_favorite);
        tv_nodata = (TextView) findViewById(R.id.tv_nodata);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        re_favorite.setLayoutManager(manager);
        GridSpacingItemDecoration.Builder builder = new GridSpacingItemDecoration.Builder();
        builder.horizontalSpacing((int) mContext.getResources().getDimension(R.dimen.size5));
        builder.verticalSpacing((int) mContext.getResources().getDimension(R.dimen.size10));
        re_favorite.addItemDecoration(new GridSpacingItemDecoration(builder));
        re_favorite.setItemAnimator(new DefaultItemAnimator());

        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
    }

    private void showFavoriteMovie() {
        List<MovieInfo> listFav = presenterMainImp.getFavMovies();
        if (!listFav.isEmpty()) {
            tv_nodata.setVisibility(View.GONE);
            adapterFavorite = new ItemAdapterFavorite(mContext, listFav, viewMain);
            re_favorite.setAdapter(adapterFavorite);
        } else {
            tv_nodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
        }
    }
}
