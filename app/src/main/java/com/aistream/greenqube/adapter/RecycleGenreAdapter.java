package com.aistream.greenqube.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.view.ViewFilter;
import com.aistream.greenqube.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhuDepTraj on 5/29/2017.
 */

public class RecycleGenreAdapter extends RecyclerView.Adapter<RecycleGenreAdapter.MyViewHolder> {
    private Context mContext;
    private List<Genre> listGenre = new ArrayList<>();
    private ViewFilter viewFilter;
    //    private PresenterLibraryImp presenterLibraryImp;
    private int selectionPos = 0;

    public int getSelectionPos() {
        return selectionPos;
    }

    public void setSelectionPos(int selectionPos) {
        this.selectionPos = selectionPos;
        notifyDataSetChanged();
    }

    public RecycleGenreAdapter(Context cont, List<Genre> list, ViewFilter view) {
//        this.presenterLibraryImp = libraryImp;
        this.viewFilter = view;
        this.mContext = cont;
        this.listGenre = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre, parent, false);
        return new RecycleGenreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Genre genre = listGenre.get(position);
        holder.tv_genrename.setText(genre.getName());

        holder.fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("GenreSelect", "GenID: " + genre.getId());
                viewFilter.showFilterMovieOfGenre(genre.getId(), genre.getName());
                setSelectionPos(position);
            }
        });

        if (position == selectionPos) {
            holder.fr.setBackgroundResource(R.drawable.bg_itemgenre);
            holder.tv_genrename.setTextColor(Color.parseColor("#222222"));
        } else {
            holder.fr.setBackgroundColor(Color.parseColor("#fcfcfc"));
            holder.tv_genrename.setTextColor(Color.parseColor("#4e4e4e"));
        }
    }

    @Override
    public int getItemCount() {
        return listGenre.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_genrename;
        public FrameLayout fr;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_genrename = (TextView) itemView.findViewById(R.id.tv_genrename);
            fr = (FrameLayout) itemView.findViewById(R.id.frmgenre);
        }
    }
}
