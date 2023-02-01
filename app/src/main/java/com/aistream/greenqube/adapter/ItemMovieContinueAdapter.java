package com.aistream.greenqube.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ViewLibrary;
import com.aistream.greenqube.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhuDepTraj on 4/11/2018.
 */

public class ItemMovieContinueAdapter extends RecyclerView.Adapter<ItemMovieContinueAdapter.Viewholder> {
    private Context mContext;
    private ViewLibrary viewLibrary;
    private PresenterMainImp presenterMainImp;
    private List<MovieDownload> movieInfoList = new ArrayList<>();

    public ItemMovieContinueAdapter(Context cont, List<MovieDownload> movieInfoList, ViewLibrary viewLibrary, PresenterMainImp main) {
        this.presenterMainImp = main;
        this.mContext = cont;
        this.viewLibrary = viewLibrary;
        this.movieInfoList = movieInfoList;
        notifyDataSetChanged();
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_continuewatching, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        final MovieDownload movieInfo = movieInfoList.get(position);
        presenterMainImp.showMovieVIP(holder.tv_vip, movieInfo.getMvId());
        holder.tv_namemov.setText(movieInfo.getMvName());
        holder.im_itemmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterMainImp.clickToPlayMovie(movieInfo);
            }
        });
//        Picasso.with(mContext).load(Config.picURLPath + movieInfo.getTheatricalPoster()).config(Bitmap.Config.RGB_565).fit().centerInside().transform(new RoundedCornersTransformation((int) mContext.getResources().getDimension(R.dimen.size5), 0)).error(R.drawable.preview_2).placeholder(R.drawable.preview_2).stableKey(movieInfoList.get(position).getTheatricalPoster()).into(holder.im_itemmovie);
        Picasso.with(mContext).load(Config.picURLPath + movieInfo.getImage()).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2).into(holder.im_itemmovie, new Callback() {

            @Override
            public void onSuccess() {
                Log.v("Picasso", "fetch image success in try again.");
            }

            @Override
            public void onError() {
                Log.v("Picasso", "Could not fetch image again...");
                Picasso.with(mContext)
                        .load(Config.picURLPath + movieInfo.getImage())
                        .fit().centerCrop()
                        .config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2)
                        .into(holder.im_itemmovie);
            }

        });
    }

    @Override
    public int getItemCount() {
        return movieInfoList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView tv_namemov;
        private ImageView im_itemmovie;
        private TextView time_download;
        private ProgressBar pro_continue;
        private TextView tv_vip;

        public Viewholder(View itemView) {
            super(itemView);
            tv_namemov = (TextView) itemView.findViewById(R.id.tv_namemov);
            im_itemmovie = (ImageView) itemView.findViewById(R.id.im_itemmovie);
            time_download = (TextView) itemView.findViewById(R.id.time_download);
            tv_vip = (TextView) itemView.findViewById(R.id.tv_vip);
//            pro_continue = (ProgressBar) itemView.findViewById(R.id.pro_continue);
        }
    }
}
