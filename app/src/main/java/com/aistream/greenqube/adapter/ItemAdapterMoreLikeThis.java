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

import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhuDepTraj on 4/11/2018.
 */

public class ItemAdapterMoreLikeThis extends RecyclerView.Adapter<ItemAdapterMoreLikeThis.Viewholder> {
    private Context mContext;
    //    private ViewLibrary viewLibrary;
    private ViewMain viewMain;
    private List<MovieInfo> movieInfoList = new ArrayList<>();

    public ItemAdapterMoreLikeThis(Context cont, List<MovieInfo> movieInfoList, ViewMain main) {
        this.mContext = cont;
//        this.viewLibrary = library;
        this.viewMain = main;
        this.movieInfoList = movieInfoList;
        notifyDataSetChanged();
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_morelikethis, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        final MovieInfo movieInfo = movieInfoList.get(position);
        viewMain.showMovieVIP(holder.tv_vip, movieInfo);
        holder.im_itemmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMain.onClickMoreLikeThis(movieInfo);
            }
        });
        Picasso.with(mContext).load(Config.picURLPath + movieInfo.getPoster()).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2).into(holder.im_itemmore, new Callback() {

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
                        .into(holder.im_itemmore);
            }

        });
    }

    @Override
    public int getItemCount() {
        return movieInfoList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        //        private TextView tv_namemore;
        private ImageView im_itemmore;
        private TextView tv_vip;

        public Viewholder(View itemView) {
            super(itemView);
//            tv_namemore = (TextView) itemView.findViewById(R.id.tv_namemore);
            im_itemmore = (ImageView) itemView.findViewById(R.id.im_itemmore);
            tv_vip = (TextView) itemView.findViewById(R.id.tv_vip);
        }
    }
}
