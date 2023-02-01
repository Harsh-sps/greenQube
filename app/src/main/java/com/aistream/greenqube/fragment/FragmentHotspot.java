package com.aistream.greenqube.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.adapter.ItemAdapterHotspot;
import com.aistream.greenqube.customs.GridSpacingItemDecoration;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.WifiInfo;
import com.aistream.greenqube.mvp.presenter.PresenterHotspotImp;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.view.ViewHotspot;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 5/10/2017.
 */

public class FragmentHotspot extends Fragment implements ViewHotspot {
    private String TAG = "FragmentHotspot";
    private OgleApplication ogleApplication;
    private RecyclerView recyclerView;
    ItemAdapterHotspot adapterHotspot;

    private PresenterHotspotImp presenterHotspot;
    private PresenterMainImp presenterMainImp;
    private DataBaseHelper dataBaseHelper;
    private SharedPreferences mPref;

    public FragmentHotspot() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FragmentHotspot(PresenterMainImp presenterMainImp) {
        this.presenterMainImp = presenterMainImp;
        Log.i(TAG, "----------create----");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ogleApplication = (OgleApplication) getContext().getApplicationContext();
        presenterHotspot = new PresenterHotspotImp(this, presenterMainImp);
        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Log.i(TAG, "----------onCreate----");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotspot, container, false);
        dataBaseHelper = DataBaseHelper.getInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyc_hotspot);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        GridSpacingItemDecoration.Builder builder = new GridSpacingItemDecoration.Builder();
        builder.verticalSpacing((int) getResources().getDimension(R.dimen.size10));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(builder));
        loadData();
        return view;
    }

    public void loadData() {
        presenterHotspot.loadWifi();
    }

    @Override
    public void loadWifiList(List<WifiInfo> wifiList) {
        float localLongitude = 0;
        float localLatitude = 0;
        if (Config.currWifiInfo != null) {
            localLongitude = Config.currWifiInfo.getLongitude().floatValue();
            localLatitude = Config.currWifiInfo.getLatitude().floatValue();
        } else {
            localLongitude = mPref.getFloat("longitude", 0);
            localLatitude = mPref.getFloat("latitude", 0);
        }

        List<WifiInfo> listVisible = new ArrayList<>();
        for (WifiInfo wifiInfo : wifiList) {
            if (wifiInfo.getVisibleonapps() == 1) {
                if (localLatitude != 0 || localLongitude != 0) {
                    wifiInfo.setDistance(OgleHelper.getDistance(localLatitude, localLongitude, wifiInfo.getLatitude(), wifiInfo.getLongitude()));
                }
                listVisible.add(wifiInfo);
            }
        }

        //sort wifilist by distance
        if (!listVisible.isEmpty() && (localLatitude != 0 || localLongitude != 0)) {
            Collections.sort(listVisible, new Comparator<WifiInfo>() {
                @Override
                public int compare(WifiInfo w1, WifiInfo w2) {
                    return Double.compare(w1.getDistance(), w2.getDistance());
                }
            });
        }

        adapterHotspot = new ItemAdapterHotspot(getContext(), presenterMainImp, listVisible, this);
        recyclerView.setAdapter(adapterHotspot);
    }

    @Override
    public void showMapGoogle(double latitude, double longitude) {
        Uri uri = Uri.parse("geo:" + latitude + "," + longitude);
        Intent in = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(in);
        } catch (Exception e) {
            OgleHelper.showMessage(getContext(), "Current device has not support map, please install google map first.", null);
        }
    }
}
