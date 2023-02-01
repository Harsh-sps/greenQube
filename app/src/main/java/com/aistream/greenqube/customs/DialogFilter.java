package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import com.aistream.greenqube.adapter.RecycleGenreAdapter;
import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.presenter.PresenterLibraryImp;
import com.aistream.greenqube.R;

import java.util.List;

/**
 * Created by MyPC on 30/05/2017.
 */

public class DialogFilter extends Dialog {

    List<Genre> genreList;
    private RecycleGenreAdapter genreAdapter;
    private PresenterLibraryImp presnterImpl;
    Context mContext;

    public DialogFilter(@NonNull Context context, List<Genre> genreList, PresenterLibraryImp presenter) {
        super(context);
        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        this.genreList = genreList;
        this.mContext = context;
        this.presnterImpl = presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter_genre);
        initView();
    }

    RecyclerView recyc_genre;

    private void initView() {
        recyc_genre = (RecyclerView) findViewById(R.id.recyc_genre);

//        genreAdapter = new RecycleGenreAdapter(mContext,presnterImpl);
        GridLayoutManager manager = new GridLayoutManager(mContext, 1);
        recyc_genre.setLayoutManager(manager);
        recyc_genre.setAdapter(genreAdapter);

//        genreAdapter.addGenrelist(genreList);

    }
}
