package com.aistream.greenqube.customs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.PlanInfo;
import com.aistream.greenqube.mvp.model.Quality;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.APIResultCallBack;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.util.DialogCallBack;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.R;
import com.google.gson.Gson;

public class CustomDialog_MovieVIP extends Dialog implements View.OnClickListener {

    private String TAG = this.getClass().getSimpleName();
    private PresenterMainImp presenterMainImp;
    private MovieInfo movieInfo;
    private Button btn_close;
    private Button btn_vip;
    private TextView btn_rent;
    private TextView sign_in;
    private SharedPreferences mPref;
    private ViewMain viewMain;
    private PlanInfo vipPlan;

    public CustomDialog_MovieVIP(Context context, MovieInfo movieInfo, ViewMain viewMain, PresenterMainImp presenterMainImp) {
        super(context, R.style.dialog_tran);
        this.movieInfo = movieInfo;
        this.viewMain = viewMain;
        this.presenterMainImp = presenterMainImp;
        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String vipPlanStr = mPref.getString("vipPlan", "");
        if (!TextUtils.isEmpty(vipPlanStr)) {
            vipPlan = new Gson().fromJson(vipPlanStr, PlanInfo.class);
        } else {
            vipPlan = new PlanInfo(0, "VIP Plan", "10");
        }
        initView(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void initView(Context context) {
        View mDialogView = View.inflate(context, R.layout.custom_dialogmovievip, null);
        LinearLayout ll_main = (LinearLayout) mDialogView.findViewById(R.id.ll_main);
        btn_close = (Button)mDialogView.findViewById(R.id.btn_close);
        btn_vip = (Button) mDialogView.findViewById(R.id.btn_vip);
        btn_rent = (TextView) mDialogView.findViewById(R.id.btn_rent);
        sign_in = (TextView) mDialogView.findViewById(R.id.sign_in);
        TextView tv_switch_plan_desc = (TextView) mDialogView.findViewById(R.id.tv_switch_plan_desc);

        tv_switch_plan_desc.setText(getContext().getResources().getString(R.string.switch_plan_desc));
        btn_vip.setText(OgleHelper.formatMsg(getContext().getResources().getString(R.string.switch_plan_btn), vipPlan.getPrice()));

        String price = movieInfo.getQualityList().get(0).getPrice();
        int rentalPeriod = movieInfo.getQualityList().get(0).getRentalPeriod();
        if (rentalPeriod > 1) {
            btn_rent.setText(OgleHelper.formatMsg(context.getResources().getString(R.string.rent_price_days), price, String.valueOf(rentalPeriod)));
        } else {
            btn_rent.setText(OgleHelper.formatMsg(context.getResources().getString(R.string.rent_price_day), price));
        }
        btn_rent.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );

        btn_close.setOnClickListener(this);
        btn_vip.setOnClickListener(this);
        btn_rent.setOnClickListener(this);
        sign_in.setOnClickListener(this);

        setContentView(mDialogView);
    }

    @Override
    public void onClick(View view) {
        final Dialog dialog = this;
        String msg = "";
        switch (view.getId()) {
            case R.id.btn_vip:
                Log.d(TAG, "vip...");
                msg = getContext().getResources().getString(R.string.textchargevipmovie)
                        .replace("xxx", vipPlan.getPrice());
                OgleHelper.showDialog(getContext(), msg, new DialogCallBack() {
                            @Override
                            public void ok() {
                                billingVip(movieInfo, dialog);
                            }

                            @Override
                            public void cancel() {
                            }
                });
                break;
            case R.id.btn_rent:
                Log.d(TAG, "billing...");
                Quality defaultQuality = movieInfo.getQualityList().get(0);
                msg = getContext().getResources().getString(R.string.textchargemovie)
                        .replace("xxx", String.valueOf(defaultQuality.getPrice())).replace("XXX", String.valueOf(defaultQuality.getRentalPeriod()));
                OgleHelper.showDialog(getContext(), msg, new DialogCallBack() {
                    @Override
                    public void ok() {
                        presenterMainImp.billing(movieInfo, dialog, null);
                    }
                    @Override
                    public void cancel() {
                    }
                });

                break;
            case R.id.sign_in:
                Log.d(TAG, "signin...");
                viewMain.showLoginPage();
                break;
            case R.id.btn_close:
                dismiss();
                break;
        }
    }

    /**
     * upgrade to vip plan
     * @param movieInfo
     * @param dialog
     */
    private void billingVip(final MovieInfo movieInfo, final Dialog dialog) {
        APICall.switchPlan(vipPlan.getPlanid(), new APIResultCallBack<Status>(){

            @Override
            public void before() {
                viewMain.showLoading();
            }

            @Override
            public void onSuccess(Status status) {
                viewMain.resetApp();
            }

            @Override
            public void onError(int httpCode, Status status, Throwable t) {
                String msg = getContext().getResources().getString(R.string.error_loginfail1);
                if (status != null) {
                    if (status != null && !TextUtils.isEmpty(status.getMessage())) {
                        msg = status.getMessage();
                    }
                }
                OgleHelper.showMessage(getContext(), msg, null);
            }

            @Override
            public void after() {
                viewMain.hideLoading();
                dialog.dismiss();
            }
        });
    }

}
