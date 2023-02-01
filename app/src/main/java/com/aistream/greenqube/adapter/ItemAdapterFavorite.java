package com.aistream.greenqube.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhuDepTraj on 4/11/2018.
 */

public class ItemAdapterFavorite extends RecyclerView.Adapter<ItemAdapterFavorite.Viewholder> {
    private Context mContext;
    private ViewMain viewMain;
    private List<MovieInfo> movieInfoList = new ArrayList<>();
    private OgleApplication mApplication;

    public ItemAdapterFavorite(Context cont, List<MovieInfo> movieInfoList, ViewMain main) {
        this.mContext = cont;
        mApplication = (OgleApplication) cont.getApplicationContext();
        this.viewMain = main;
        this.movieInfoList = movieInfoList;
        notifyDataSetChanged();
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contentlibrary, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        final MovieInfo movieInfo = movieInfoList.get(position);
        viewMain.showMovieVIP(holder.tv_vip, movieInfo);
        holder.tv_namemov.setText(movieInfo.getName());
        movieInfo.setMovieBilling(mApplication.getMovieBillingInfo(movieInfo.getMovieId()));
        OgleHelper.showMoviePrice(mContext, holder.time_download, movieInfo);

        holder.im_itemmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMain.showMovieDetail(movieInfo);
            }
        });
//        Picasso.with(mContext).load(Config.picURLPath + movieInfo.getTheatricalPoster()).config(Bitmap.Config.RGB_565).fit().centerInside().transform(new RoundedCornersTransformation((int) mContext.getResources().getDimension(R.dimen.size5), 0)).error(R.drawable.preview_2).placeholder(R.drawable.preview_2).stableKey(movieInfoList.get(position).getTheatricalPoster()).into(holder.im_itemmovie);
        Picasso.with(mContext).load(Config.picURLPath + movieInfo.getPoster()).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE).config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2).into(holder.im_itemmovie, new Callback() {

            @Override
            public void onSuccess() {
                Log.v("Picasso", "fetch image success in try again.");
            }

            @Override
            public void onError() {
                Log.v("Picasso", "Could not fetch image again...");
                Picasso.with(mContext)
                        .load(Config.picURLPath + movieInfo.getPoster())
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
        private TextView tv_vip;

        public Viewholder(View itemView) {
            super(itemView);
            tv_namemov = (TextView) itemView.findViewById(R.id.tv_namemov);
            im_itemmovie = (ImageView) itemView.findViewById(R.id.im_itemmovie);
            time_download = (TextView) itemView.findViewById(R.id.time_download);
            tv_vip = (TextView) itemView.findViewById(R.id.tv_vip);
        }
    }
}
