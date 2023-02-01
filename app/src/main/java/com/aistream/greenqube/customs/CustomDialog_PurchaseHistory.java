package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.adapter.ItemAdapterPurchaseHistory;
import com.aistream.greenqube.mvp.model.Balance;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.R;

import java.util.List;

/**
 * Created by PhuDepTraj on 3/19/2018.
 */

public class CustomDialog_PurchaseHistory extends Dialog implements View.OnClickListener {
    private ViewMain viewMain;
    private FrameLayout btn_back;
    private Context mContext;
    private RecyclerView re_purchase;
    private OgleApplication ogleApplication;
    TextView tv_nodata;
    private SharedPreferences mPref;

    public CustomDialog_PurchaseHistory(@NonNull Context context, ViewMain view) {
        super(context, R.style.AppThemeDialog);
        this.viewMain = view;
        this.mContext = context;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        ogleApplication = (OgleApplication) mContext.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_purechasehistory);

        //show status bar
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);

        initView();
        getDataPurchaseHistory();
    }

    private void initView() {
        btn_back = (FrameLayout) findViewById(R.id.btn_back);
        re_purchase = (RecyclerView) findViewById(R.id.re_purchase);
        tv_nodata = (TextView) findViewById(R.id.tv_nodata);

        re_purchase.setHasFixedSize(true);
        re_purchase.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(re_purchase.getContext(),
                ((LinearLayoutManager) re_purchase.getLayoutManager()).getOrientation());
        re_purchase.addItemDecoration(mDividerItemDecoration);

        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
        }
    }

    private void getDataPurchaseHistory() {
        List<Balance> balanceList = ogleApplication.balanceList;
        if (balanceList.isEmpty()) {
            tv_nodata.setVisibility(View.VISIBLE);
        } else {
            tv_nodata.setVisibility(View.GONE);
            ItemAdapterPurchaseHistory purchaseHistory = new ItemAdapterPurchaseHistory(mContext, balanceList);
            re_purchase.setAdapter(purchaseHistory);
        }
    }
}
