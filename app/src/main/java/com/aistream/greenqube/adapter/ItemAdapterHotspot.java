package com.aistream.greenqube.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.WifiInfo;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ViewHotspot;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 5/16/2017.
 */

public class ItemAdapterHotspot extends RecyclerView.Adapter<ItemAdapterHotspot.Viewholder> {
    private String TAG = ItemAdapterHotspot.class.getSimpleName();
    private Context mContext;
    ViewHotspot viewHotspot;
    private List<WifiInfo> wifiInfoList = new ArrayList<>();
    PresenterMainImp mainImp;

    public ItemAdapterHotspot(Context cont, PresenterMainImp main, List<WifiInfo> listWifi, ViewHotspot hotspot) {
        this.mContext = cont;
        this.viewHotspot = hotspot;
        this.wifiInfoList = listWifi;
        this.mainImp = main;
        notifyDataSetChanged();
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotspot_item_city, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        final WifiInfo wifiInfo = wifiInfoList.get(position);
        String name = wifiInfo.getName();
        if (!TextUtils.isEmpty(wifiInfo.getLocation())) {
            name = wifiInfo.getLocation();
        }
        holder.txt_name.setText(name);
        holder.tv_streetNo.setText(wifiInfo.getAddress1());
        holder.tv_district.setText(wifiInfo.getCity());
        holder.tv_address.setText(wifiInfo.getRegion());
        String distance = "-- Away";
        if (wifiInfo.getDistance() != null) {
            distance = OgleHelper.formatDistance(wifiInfo.getDistance())+" Away";
        }
        holder.txt_distance.setText(distance);
        Picasso.with(mContext).load(Config.picURLPath + wifiInfo.getIcon()).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().config(Bitmap.Config.RGB_565).error(R.drawable.map_preview).placeholder(R.drawable.map_preview).into(holder.img_map, new Callback() {

            @Override
            public void onSuccess() {
                Log.v("Picasso", "fetch image success in try again.");
            }

            @Override
            public void onError() {
                Log.v("Picasso", "Could not fetch image again...");
                Picasso.with(mContext)
                        .load(Config.picURLPath + wifiInfo.getIcon())
                        .fit().centerCrop()
                        .config(Bitmap.Config.RGB_565).error(R.drawable.map_preview).placeholder(R.drawable.map_preview)
                        .into(holder.img_map);
            }
        });

        holder.txt_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWifiDesc(wifiInfo);
            }
        });

        holder.img_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWifiDesc(wifiInfo);
            }
        });

        holder.btn_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "latitude: "+wifiInfo.getLatitude()+", longitude: "+wifiInfo.getLongitude());
                viewHotspot.showMapGoogle(wifiInfo.getLatitude(), wifiInfo.getLongitude());
            }
        });
    }

    private void showWifiDesc(WifiInfo wifiInfo) {
        if (!TextUtils.isEmpty(wifiInfo.getNotes())) {
            OgleHelper.showMessage(mContext, wifiInfo.getNotes(), null);
        }
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView txt_name;
        private TextView txt_distance;
        private TextView tv_address;
        private TextView tv_streetNo;
        private TextView tv_district;
        private ImageView img_map;
        private Button btn_directions;

        public Viewholder(View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_distance = (TextView) itemView.findViewById(R.id.txt_distance);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_streetNo = (TextView) itemView.findViewById(R.id.tv_streetNo);
            tv_district = (TextView) itemView.findViewById(R.id.tv_district);
            img_map = (ImageView) itemView.findViewById(R.id.img_map);
            btn_directions = (Button) itemView.findViewById(R.id.btn_directions);
        }
    }

    @Override
    public int getItemCount() {
        return wifiInfoList.size();
    }

}
