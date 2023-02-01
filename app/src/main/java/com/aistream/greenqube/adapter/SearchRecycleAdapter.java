package com.aistream.greenqube.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ViewLibrary;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 29/05/2017.
 */

public class SearchRecycleAdapter extends RecyclerView.Adapter<SearchRecycleAdapter.viewholder> implements Filterable {
    private Context mContext;
    private ViewLibrary viewLibrary;
    private PresenterMainImp presenterMainImp;
    List<MovieInfo> infoList = new ArrayList<>();
    List<MovieInfo> infoListFilter = new ArrayList<>();
    private OgleApplication mApplication;

    public SearchRecycleAdapter(Context cont, List<MovieInfo> info, ViewLibrary library, PresenterMainImp presenterMain) {
        this.viewLibrary = library;
        this.mContext = cont;
        mApplication = (OgleApplication) cont.getApplicationContext();
        this.infoList = info;
        this.presenterMainImp = presenterMain;
        infoListFilter = info;
    }

    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        return new SearchRecycleAdapter.viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(viewholder holder, int position) {
        if (infoListFilter != null && infoListFilter.size() > 0 && 0 <= position && position < infoListFilter.size()) {
            ((SearchRecycleAdapter.viewholder) holder).bind(infoListFilter.get(position));
            holder.txt_name_movie.setText(String.valueOf(position + 1) + ". " + infoListFilter.get(position).getName());
        }
    }

    public class viewholder extends RecyclerView.ViewHolder {
        private LinearLayout ll_item;
        private ImageView img_movie_preview;
        private TextView txt_name_movie;
        private TextView txt_filesize;
        private TextView txt_duration;
        private TextView txt_repleasedate;
        private TextView txt_price;
        private TextView tv_vip;
        private Button btn_download;

        public viewholder(View itemView) {
            super(itemView);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
            img_movie_preview = (ImageView) itemView.findViewById(R.id.img_movie_preview);
            txt_name_movie = (TextView) itemView.findViewById(R.id.txt_name_movie);
            txt_filesize = (TextView) itemView.findViewById(R.id.txt_filesize);
            txt_duration = (TextView) itemView.findViewById(R.id.txt_duration);
            txt_repleasedate = (TextView) itemView.findViewById(R.id.txt_repleasedate);
            txt_price = (TextView) itemView.findViewById(R.id.txt_price);
            tv_vip = (TextView) itemView.findViewById(R.id.tv_vip);
            btn_download = (Button) itemView.findViewById(R.id.btn_download);
        }

        public void bind(final MovieInfo info) {
            txt_filesize.setText("FILE SIZE: " + info.getQualityList().get(0).getFileSize() + "MB");
            txt_duration.setText("Duration: " + OgleHelper.getDurationString(info.getDuration()));
            txt_repleasedate.setText("Release Date: " + info.getReleaseDate().substring(0, 10));
            viewLibrary.showMovieVIP(tv_vip, info);

            info.setMovieBilling(mApplication.getMovieBillingInfo(info.getMovieId()));
            OgleHelper.showMoviePrice(mContext, txt_price, info);

            Picasso.with(mContext).load(Config.picURLPath + info.getPoster()).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2).into(img_movie_preview, new Callback() {

                @Override
                public void onSuccess() {
                    Log.v("Picasso", "fetch image success in try again.");
                }

                @Override
                public void onError() {
                    Log.v("Picasso", "Could not fetch image again...");
                    Picasso.with(mContext)
                            .load(Config.picURLPath + info.getPoster())
                            .fit().centerCrop()
                            .config(Bitmap.Config.RGB_565).error(R.drawable.preview_2).placeholder(R.drawable.preview_2)
                            .into(img_movie_preview);
                }

            });

            ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewLibrary.showMovieDetail(info);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return infoListFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                FilterResults filterResults = new FilterResults();
                filterResults.values = new ArrayList<>();
                if (charString.isEmpty()) {
                    filterResults.values = infoList;
                } else {
                    List<MovieInfo> filteredList = new ArrayList<>();
                    try {
                        if (infoList != null && infoList.size() > 0) {
                            for (MovieInfo row : infoList) {
                                if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                    filteredList.add(row);
                                }
                            }
                            filterResults.values = filteredList;
                        }
//                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                infoListFilter = (ArrayList<MovieInfo>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
