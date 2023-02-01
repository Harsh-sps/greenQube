package com.aistream.greenqube.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.presenter.PresenterSearchImpl;
import com.aistream.greenqube.BuildConfig;
import com.aistream.greenqube.R;

/**
 * Created by Administrator on 5/10/2017.
 */

public class FragmentSearch extends Fragment{

    private TextView tv_result;
    private TextView tv_version;
    private RecyclerView recyclerView;
    private PresenterSearchImpl presenterSearch;

    private PresenterMainImp presenterMainImp;

    public FragmentSearch() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FragmentSearch(PresenterMainImp presenterMainImp) {
        this.presenterMainImp = presenterMainImp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("FragmentSearch", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("FragmentSearch", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("FragmentSearch", "onPause");
    }

    @Override
    public void onDestroy() {
        Log.i("FragmentSearch", "onDestroy");
        super.onDestroy();
    }


    private void initView(View view) {
        tv_result = (TextView) view.findViewById(R.id.tv_result);
        tv_version = (TextView) view.findViewById(R.id.tv_version);
        tv_version.setText("v" + BuildConfig.VERSION_NAME);
        final EditText et_search = (EditText) view.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_search.length() == 0) {
                    tv_result.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tv_result.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyc_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

    }

}
