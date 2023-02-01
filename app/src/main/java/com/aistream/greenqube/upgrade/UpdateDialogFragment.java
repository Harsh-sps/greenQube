package com.aistream.greenqube.upgrade;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aistream.greenqube.R;
import com.aistream.greenqube.upgrade.listener.ExceptionHandler;
import com.aistream.greenqube.upgrade.listener.ExceptionHandlerHelper;
import com.aistream.greenqube.upgrade.listener.IUpdateDialogFragmentListener;
import com.aistream.greenqube.upgrade.service.DownloadService;
import com.aistream.greenqube.upgrade.utils.AppUpgradeUtils;
import com.aistream.greenqube.upgrade.utils.ColorUtil;
import com.aistream.greenqube.upgrade.utils.DrawableUtil;
import com.aistream.greenqube.upgrade.view.NumberProgressBar;
import java.io.File;

/**
 * Created by Vector
 * on 2017/7/19 0019.
 */

public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TIPS = "Please grant access to the storage space permissions, the App won't be able to update.";
    public static boolean isShow = false;
    private TextView mContentTextView;
    private Button mUpdateOkButton;
    private Update mUpdateApp;
    private NumberProgressBar mNumberProgressBar;
    private ImageView mIvClose;
    private TextView mTitleTextView;
    private boolean isServerConnected = false;
    /**
     * service callback
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            startDownloadApp((DownloadService.DownloadBinder) service);
            isServerConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServerConnected = false;
        }
    };
    private LinearLayout mLlClose;
    private int mDefaultColor = 0xffe94339;
    private int mDefaultPicResId = R.mipmap.update_dialog_top_bg;
    private ImageView mTopIv;
    private TextView resetBtn;
    private IUpdateDialogFragmentListener mUpdateDialogFragmentListener;
    private DownloadService.DownloadBinder mDownloadBinder;
    private Activity mActivity;

    public UpdateDialogFragment setUpdateDialogFragmentListener(IUpdateDialogFragmentListener updateDialogFragmentListener) {
        this.mUpdateDialogFragmentListener = updateDialogFragmentListener;
        return this;
    }


    public static UpdateDialogFragment newInstance(Bundle args) {
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = true;
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.UpdateAppDialog);
        mActivity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        //click dialog outer, hide dialog
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mUpdateApp != null && mUpdateApp.isConstraint()) {
                        //back to MainActivity
                        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });

        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        lp.height = (int) (displayMetrics.heightPixels * 0.8f);
        dialogWindow.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        //upgrade desc
        mContentTextView = view.findViewById(R.id.tv_update_info);
        //upgrade title
        mTitleTextView = view.findViewById(R.id.tv_title);
        //upgrade button
        mUpdateOkButton = view.findViewById(R.id.btn_ok);
        //upgrade progress
        mNumberProgressBar = view.findViewById(R.id.npb);
        //upgrade close button
        mIvClose = view.findViewById(R.id.iv_close);
        //close line
        mLlClose = view.findViewById(R.id.ll_close);
        //top dialog bg picture
        mTopIv = view.findViewById(R.id.iv_top);
        resetBtn = view.findViewById(R.id.btn_reset);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        mUpdateApp = (Update) getArguments().getSerializable(AppUpgradeManager.INTENT_KEY);
        initTheme();
        if (mUpdateApp != null) {
            final String dialogTitle = mUpdateApp.getDialogTitle();
            final String newVersion = mUpdateApp.getVersion();
            final String updateLog = mUpdateApp.getUpdateDesc();

            String msg = "";
            if (!TextUtils.isEmpty(updateLog)) {
                msg += updateLog;
            }

            //update upgrade desc
            mContentTextView.setText(updateLog);
            //update upgrade title
            mTitleTextView.setText(String.format("New Version(%s)", newVersion));
            //constraint upgrade
            if (mUpdateApp.isConstraint()) {
                mLlClose.setVisibility(View.GONE);
            }
            initEvents();
        }
    }

    /**
     * init dialog theme
     */
    private void initTheme() {
        final int color = getArguments().getInt(AppUpgradeManager.THEME_KEY, -1);
        final int topResId = getArguments().getInt(AppUpgradeManager.TOP_IMAGE_KEY, -1);
        if (-1 == topResId) {
            if (-1 == color) {
                setDialogTheme(mDefaultColor, mDefaultPicResId);
            } else {
                setDialogTheme(color, mDefaultPicResId);
            }

        } else {
            if (-1 == color) {
                setDialogTheme(mDefaultColor, topResId);
            } else {
                setDialogTheme(color, topResId);
            }
        }
    }

    /**
     * set dialog theme
     *
     * @param color
     * @param topResId
     */
    private void setDialogTheme(int color, int topResId) {
        mTopIv.setImageResource(topResId);
        mUpdateOkButton.setBackgroundDrawable(DrawableUtil.getDrawable(AppUpgradeUtils.dip2px(4, getActivity()), color));
        mNumberProgressBar.setProgressTextColor(color);
        mNumberProgressBar.setReachedBarColor(color);
        mUpdateOkButton.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
    }

    private void initEvents() {
        if (AppUpgradeUtils.appIsDownloaded(mUpdateApp)) {
            showInstallBtn(AppUpgradeUtils.getAppFile(mUpdateApp));
        } else {
            mUpdateOkButton.setOnClickListener(this);
        }
        mIvClose.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_ok) {
            //upgrade from google play
            if(mUpdateApp.getUpdateFrom() == UpdateFrom.GOOGLE_PLAY) {
                AppUpgradeUtils.goToGooglePlay(getActivity(), mUpdateApp);
                if (mUpdateApp.isHideDialog() && !mUpdateApp.isConstraint()) {
                    dismiss();
                }
            } else {
                int flag = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (flag != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(getActivity(), TIPS, Toast.LENGTH_LONG).show();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    installApp();
                }
            }
        } else if (i == R.id.iv_close) {
            cancelDownloadService();
            if (mUpdateDialogFragmentListener != null) {
                mUpdateDialogFragmentListener.onUpdateNotifyDialogCancel(mUpdateApp);
            }
            dismiss();
        } else if (i == R.id.btn_reset) {
            if (mUpdateApp != null) {
                AppUpgradeUtils.deleteDownloadFile(mUpdateApp);
            }
            mUpdateOkButton.setText("Upgrade");
            mUpdateOkButton.setVisibility(View.VISIBLE);
            mUpdateOkButton.setOnClickListener(this);
            resetBtn.setVisibility(View.GONE);
            try {
                if (isServerConnected) {
                    getActivity().getApplicationContext().unbindService(conn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelDownloadService() {
        if (mDownloadBinder != null) {
            mDownloadBinder.stop("Cancel");
        }
    }

    private void installApp() {
        if (AppUpgradeUtils.appIsDownloaded(mUpdateApp)) {
            Log.d("AppUpgrade", "apk is download success");
            AppUpgradeUtils.installApp(UpdateDialogFragment.this, AppUpgradeUtils.getAppFile(mUpdateApp));
            if (!mUpdateApp.isConstraint()) {
                dismiss();
            } else {
                showInstallBtn(AppUpgradeUtils.getAppFile(mUpdateApp));
            }
        } else {
            Log.d("AppUpgrade", "start download apk ");
            downloadApp();
            if (mUpdateApp.isHideDialog() && !mUpdateApp.isConstraint()) {
                dismiss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                installApp();
            } else {
                Toast.makeText(getActivity(), TIPS, Toast.LENGTH_LONG).show();
                dismiss();
            }
        }
    }

    /**
     * start download service to download
     */
    private void downloadApp() {
        Log.d("AppUpgrade", "start app download service...");
        DownloadService.bindService(getActivity().getApplicationContext(), conn);
    }

    /**
     * listen download callback
     */
    private void startDownloadApp(DownloadService.DownloadBinder binder) {
        Log.d("AppUpgrade", "startDownloadApp mUpdateApp:"+mUpdateApp);
        if (mUpdateApp != null) {
            this.mDownloadBinder = binder;
            binder.start(mUpdateApp, new DownloadService.DownloadCallback() {
                @Override
                public void onStart() {
                    if (!UpdateDialogFragment.this.isRemoving()) {
                        mNumberProgressBar.setVisibility(View.VISIBLE);
                        mUpdateOkButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onProgress(float progress, long totalSize) {
                    if (!UpdateDialogFragment.this.isRemoving()) {
                        mNumberProgressBar.setProgress(Math.round(progress * 100));
                        mNumberProgressBar.setMax(100);
                    }
                }

                @Override
                public void setMax(long total) {

                }

                @Override
                public boolean onFinish(final File file) {
                    if (!UpdateDialogFragment.this.isRemoving()) {
                        if (mUpdateApp.isConstraint()) {
                            showInstallBtn(file);
                        } else {
                            dismissAllowingStateLoss();
                        }
                    }
                    return true;
                }

                @Override
                public void onError(String msg) {
                    if (!UpdateDialogFragment.this.isRemoving()) {
                        dismissAllowingStateLoss();
                    }
                }

                @Override
                public boolean onInstallAppAndAppOnForeground(File file) {
                    if (!mUpdateApp.isConstraint()) {
                        dismiss();
                    }
                    if (mActivity != null) {
                        AppUpgradeUtils.installApp(mActivity, file);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    private void showInstallBtn(final File file) {
        mNumberProgressBar.setVisibility(View.GONE);
        resetBtn.setVisibility(View.VISIBLE);
        mUpdateOkButton.setText("Install");
        mUpdateOkButton.setVisibility(View.VISIBLE);
        mUpdateOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpgradeUtils.installApp(UpdateDialogFragment.this, file);
            }
        });
    }

    @Override
    public void show(FragmentManager manager, String tag) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (manager.isDestroyed()) {
                return;
            }
        }

        try {
            super.show(manager, tag);
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
    }

    @Override
    public void onDestroyView() {
        isShow = false;
        super.onDestroyView();
    }
}

