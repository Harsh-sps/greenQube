package com.aistream.greenqube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.Balance;
import com.aistream.greenqube.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhuDepTraj on 4/11/2018.
 */

public class ItemAdapterPurchaseHistory extends RecyclerView.Adapter<ItemAdapterPurchaseHistory.Viewholder> {
    private Context mContext;
    private List<Balance> balancesList = new ArrayList<>();
    private OgleApplication ogleApplication;

    public ItemAdapterPurchaseHistory(Context cont, List<Balance> balanList) {
        this.mContext = cont;
        ogleApplication = (OgleApplication) mContext.getApplicationContext();
//        this.viewLibrary = viewLibrary;
        this.balancesList = balanList;
        notifyDataSetChanged();
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_balance, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        final Balance balance = balancesList.get(position);
        holder.tv_mvName.setText(balance.getName());
        holder.tv_time.setText(balance.getDownloadDate());
        String amount = balance.getAmount();
        if (!TextUtils.isEmpty(amount)) {
            String currency = mContext.getResources().getString(R.string.currency);
            if (amount.indexOf("$") >= 0) {
                holder.tv_price.setText(amount);
            } else {
                holder.tv_price.setText(currency + amount);
            }
        } else {
            holder.tv_price.setText("");
        }
        holder.tv_No.setText(String.valueOf(position + 1));

        String voucher = balance.getVoucherCode();
        if (!TextUtils.isEmpty(voucher)) {
            holder.tv_voucher.setText(voucher.substring(0, 4) + "-****-****-" + voucher.substring(voucher.length() - 4));
        } else {
            holder.tv_voucher.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return balancesList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView tv_mvName;
        private TextView tv_time;
        private TextView tv_price;
        private TextView tv_No;
        private TextView tv_voucher;

        public Viewholder(View itemView) {
            super(itemView);
            tv_mvName = (TextView) itemView.findViewById(R.id.tv_mvName);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            tv_No = (TextView) itemView.findViewById(R.id.tv_No);
            tv_voucher = (TextView) itemView.findViewById(R.id.tv_voucher);
        }
    }
}
