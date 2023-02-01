package com.aistream.greenqube.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.MovieSection;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.view.ViewLibrary;
import com.aistream.greenqube.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/19/2017.
 */

public class ItemMovieSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MovieSection> movieSectionList = new ArrayList<>();
    private Context mContext;
    private PresenterMainImp presenterMain;
    private ViewLibrary viewLibrary;
    private boolean isMovie;

    public ItemMovieSectionAdapter(Context mContext, PresenterMainImp presenterMain, ViewLibrary viewLibrary, boolean isMovie) {
        this.viewLibrary = viewLibrary;
        this.mContext = mContext;
        this.presenterMain = presenterMain;
        this.isMovie = isMovie;
    }

    public void clearMovieSectionList() {
        movieSectionList.clear();
        notifyDataSetChanged();
    }

    public void updateSectionMovie(Genre genre, List<MovieInfo> movieInfoList) {
        if (movieInfoList != null && movieInfoList.size() > 0) {
            MovieSection movieSection = new MovieSection(genre.getId(), genre.getName(), movieInfoList);
            if (genre.getId() < 0) {
                movieSectionList.add(0, movieSection);
                notifyItemInserted(0);
            } else {
                movieSectionList.add(movieSection);
                notifyItemInserted(movieSectionList.size() - 1);
            }
        }
    }

    public int getItemSectionVisible() {
        int index = 0;
        for (MovieSection movieSection : movieSectionList) {
            if (movieSection.getMovieInfoList().size() > 0)
                index = index + 1;
            if (index > 0) break;
        }
        return index;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_list_section, parent, false);
        return new MovieSectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((MovieSectionViewHolder) holder).bind(movieSectionList.get(position), mContext, viewLibrary, isMovie);
        ((MovieSectionViewHolder) holder).txt_seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieSection movieSection = movieSectionList.get(position);
                Log.i("GenreClick", "Genre Id: "+movieSection.getGenreId()+", Name: " + movieSection.getGenName());
                viewLibrary.showHideFrameAllMov(1, movieSection.getGenreId(), movieSection.getGenName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieSectionList.size();
    }

    private static final class MovieSectionViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_genName;
        private TextView txt_seeAll;
        private RecyclerView rcy_movie_section;

        public MovieSectionViewHolder(View itemView) {
            super(itemView);
            txt_genName = (TextView) itemView.findViewById(R.id.txt_genName);
            txt_seeAll = (TextView) itemView.findViewById(R.id.txt_seeAll);
            rcy_movie_section = (RecyclerView) itemView.findViewById(R.id.rcy_movie_section);
        }

        public void bind(final MovieSection movieSection, Context mContext, ViewLibrary viewLibrary, boolean isMovie) {
            if (movieSection.getMovieInfoList().size() > 0) {
                txt_genName.setText(movieSection.getGenName());
                ItemAdapterMovie itemMovieHomeAdapter = new ItemAdapterMovie(mContext, movieSection.getMovieInfoList(), viewLibrary, isMovie);
                rcy_movie_section.setHasFixedSize(true);
                rcy_movie_section.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                rcy_movie_section.setAdapter(itemMovieHomeAdapter);
                rcy_movie_section.setNestedScrollingEnabled(false);

                txt_genName.setVisibility(View.VISIBLE);
                rcy_movie_section.setVisibility(View.VISIBLE);
            } else {
                txt_genName.setVisibility(View.GONE);
                rcy_movie_section.setVisibility(View.GONE);
            }

            if (movieSection.getMovieInfoList().size() < 4) {
                txt_seeAll.setVisibility(View.GONE);
            } else {
                txt_seeAll.setVisibility(View.VISIBLE);
            }

        }
    }
}
