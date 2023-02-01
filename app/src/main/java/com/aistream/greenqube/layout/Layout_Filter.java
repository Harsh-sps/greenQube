package com.aistream.greenqube.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.view.ViewFilter;
import com.aistream.greenqube.mvp.view.ViewLibrary;
import com.aistream.greenqube.R;
import com.aistream.greenqube.adapter.ItemAdapterMovie;
import com.aistream.greenqube.adapter.ItemMovieContinueAdapter;
import com.aistream.greenqube.adapter.RecycleGenreAdapter;
import com.aistream.greenqube.adapter.SearchRecycleAdapter;
import com.aistream.greenqube.customs.GridSpacingItemDecoration;

import java.util.List;

/**
 * Created by PhuDepTraj on 4/16/2018.
 */

public class Layout_Filter extends LinearLayout implements ViewFilter, View.OnClickListener {
    private Context mContext;
    private ViewLibrary viewLibrary;
    private PresenterMainImp presenterMainImp;
    private RecyclerView re_movie;
    private RecyclerView re_genre;
    private ItemAdapterMovie adapterMovie;
    private RecycleGenreAdapter adapterGenre;
    private FrameLayout frm_Genre;
    private LinearLayout ll_searchMov;
    private LinearLayout ll_showALlMovie;
    private ImageView im_back;
    private TextView title_toolbar;
    private ImageView ic_search;
    private TextView tv_cancel;
    private EditText et_search;
    private RecyclerView recyc_search;
    SearchRecycleAdapter searchAdapter;
    private int genID;
    private String genName;

    public Layout_Filter(Context context, ViewLibrary viewLibrary, PresenterMainImp presenterMainImp, int type, int genId, String genName) {
        super(context);
        this.genID = genId;
        this.genName = genName;
        this.presenterMainImp = presenterMainImp;
        this.mContext = context;
        this.viewLibrary = viewLibrary;
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(R.layout.layout_filter, this, true);
        initView();

        if (type == 0) {
            ll_searchMov.setVisibility(VISIBLE);
            ll_showALlMovie.setVisibility(GONE);
        } else {
            ll_showALlMovie.setVisibility(VISIBLE);
            ll_searchMov.setVisibility(GONE);
            showFilterMovieOfGenre(genId, genName);
        }
    }

    private void initView() {
        re_movie = (RecyclerView) findViewById(R.id.re_movie);
        re_genre = (RecyclerView) findViewById(R.id.re_genre);
        frm_Genre = (FrameLayout) findViewById(R.id.frm_Genre);
        ll_showALlMovie = (LinearLayout) findViewById(R.id.ll_showALlMovie);
        ll_searchMov = (LinearLayout) findViewById(R.id.ll_searchMov);
        im_back = (ImageView) findViewById(R.id.im_back);
        title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        ic_search = (ImageView) findViewById(R.id.ic_search);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        et_search = (EditText) findViewById(R.id.et_search);
        recyc_search = (RecyclerView) findViewById(R.id.recyc_search);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyc_search.getLayoutParams();
        if (genID == PresenterMainImp.ALL) {
            layoutParams.bottomMargin = (int)mContext.getResources().getDimension(R.dimen.size30);
        } else {
            layoutParams.bottomMargin = 0;
        }
        recyc_search.setLayoutParams(layoutParams);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        re_genre.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            }
        });
        re_genre.setLayoutManager(horizontalLayoutManager);
        //
        im_back.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        ic_search.setOnClickListener(this);
        searchResult();
    }

    private void searchResult() {
        recyc_search.setLayoutManager(new GridLayoutManager(getContext(), 1));
        GridSpacingItemDecoration.Builder builder = new GridSpacingItemDecoration.Builder();
        builder.verticalSpacing((int) getResources().getDimension(R.dimen.size10));
        recyc_search.addItemDecoration(new GridSpacingItemDecoration(builder));
        searchAdapter = new SearchRecycleAdapter(getContext(),
                            viewLibrary.loadMovieFromGen(genID), viewLibrary, presenterMainImp);
        recyc_search.setAdapter(searchAdapter);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("Testchangesearch", "Text Search: " + s);
                searchAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void showFilterMovieOfGenre(int genID, String genreName) {
        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        re_movie.setLayoutManager(manager);
        GridSpacingItemDecoration.Builder builder = new GridSpacingItemDecoration.Builder();
        builder.horizontalSpacing((int) getResources().getDimension(R.dimen.size5));
        builder.verticalSpacing((int) getResources().getDimension(R.dimen.size10));
        re_movie.addItemDecoration(new GridSpacingItemDecoration(builder));
        re_movie.setItemAnimator(new DefaultItemAnimator());
        title_toolbar.setText(genreName);
        adapterMovie = new ItemAdapterMovie(mContext,
                    viewLibrary.loadMovieFromGen(genID), viewLibrary, true);
        re_movie.setAdapter(adapterMovie);
    }

    @Override
    public void showAllMovieContinue(List<MovieDownload> listContinue) {
        GridLayoutManager manager = new GridLayoutManager(mContext, 2);
        re_movie.setLayoutManager(manager);
        GridSpacingItemDecoration.Builder builder = new GridSpacingItemDecoration.Builder();
        builder.horizontalSpacing((int) getResources().getDimension(R.dimen.size5));
        builder.verticalSpacing((int) getResources().getDimension(R.dimen.size5));
        re_movie.addItemDecoration(new GridSpacingItemDecoration(builder));
        re_movie.setItemAnimator(new DefaultItemAnimator());
        title_toolbar.setText(getResources().getString(R.string.continuewatching));
        frm_Genre.setVisibility(GONE);
        ic_search.setVisibility(GONE);
        ItemMovieContinueAdapter continueAdapter = new ItemMovieContinueAdapter(mContext, listContinue, viewLibrary, presenterMainImp);
        re_movie.setAdapter(continueAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_back:
                viewLibrary.onBackpress();
                break;
            case R.id.tv_cancel:
                hideKeyboardFrom(mContext, tv_cancel);
                viewLibrary.onBackpress();
                break;
            case R.id.ic_search:
                ll_searchMov.setVisibility(VISIBLE);
                ll_showALlMovie.setVisibility(GONE);
                break;
        }
    }

    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public int getGenID() {
        return genID;
    }
}
