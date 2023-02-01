package com.aistream.greenqube.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ViewLibrary;
import com.aistream.greenqube.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewPageAdapter extends PagerAdapter {
    private List<MovieInfo> movieInfoList = new ArrayList<>();
    private Context mContext;
    private ViewLibrary viewLibrary;

    public ViewPageAdapter(Context cont, ViewLibrary viewLibrary) {
        this.mContext = cont;
        this.viewLibrary = viewLibrary;
    }

    public String getNameMovie(int pos) {
        if (pos >= 0 && pos < movieInfoList.size())
            return movieInfoList.get(pos).getName();
        return "";
    }

    @Override
    public int getCount() {
        return movieInfoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemview = inflater.inflate(R.layout.item_promotion, container, false);
        final ImageView img = (ImageView) itemview.findViewById(R.id.im_promotion);
        final TextView tv_vip = (TextView) itemview.findViewById(R.id.tv_vip);
        TextView tv_name = (TextView) itemview.findViewById(R.id.tv_name);

        final MovieInfo movieInfo = movieInfoList.get(position);
        tv_name.setText(movieInfo.getName());
        viewLibrary.showMovieVIP(tv_vip, movieInfo);

        Picasso.with(mContext).load(Config.picURLPath + movieInfo.getPreview()).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().config(Bitmap.Config.RGB_565).error(R.drawable.preview_1).placeholder(R.drawable.preview_1).into(img, new Callback() {

            @Override
            public void onSuccess() {
                Log.v("Picasso", "fetch image success in try again.");
            }

            @Override
            public void onError() {
                Log.v("Picasso", "Could not fetch image again...");
                Picasso.with(mContext)
                        .load(Config.picURLPath + movieInfo.getPreview())
                        .fit().centerCrop()
                        .config(Bitmap.Config.RGB_565).error(R.drawable.preview_1).placeholder(R.drawable.preview_1)
                        .into(img);
            }

        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewLibrary.showMovieDetail(movieInfoList.get(position));
            }
        });

        //add item to viewpage
        ((ViewPager) container).addView(itemview);
        return itemview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);
    }

    public void addListMovie(List<MovieInfo> movieInfoList) {
        if (movieInfoList != null && !movieInfoList.isEmpty()) {
            this.movieInfoList = movieInfoList;
            notifyDataSetChanged();
        }
    }
}
