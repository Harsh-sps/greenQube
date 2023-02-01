package com.aistream.greenqube.upgrade;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.aistream.greenqube.upgrade.listener.ExceptionHandler;
import com.aistream.greenqube.upgrade.listener.ExceptionHandlerHelper;
import com.aistream.greenqube.upgrade.listener.IUpdateDialogFragmentListener;
import com.aistream.greenqube.upgrade.service.DownloadService;
import com.aistream.greenqube.upgrade.utils.AppUpgradeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * app upgrade manager
 */
public class AppUpgradeManager {
    final static String INTENT_KEY = "update_dialog_values";
    final static String THEME_KEY = "theme_color";
    final static String TOP_IMAGE_KEY = "top_resId";
    private final static String UPDATE_APP_KEY = "UPDATE_APP_KEY";
    private static final String TAG = AppUpgradeManager.class.getSimpleName();
    private Map<String, String> mParams;
    private Activity mActivity;
    private HttpManager mHttpManager;
    private String mUpdateUrl;
    private int mThemeColor;
    @DrawableRes
    int mTopPic;
    private String mAppKey;
    private Update mUpdateApp;
    private String mTargetPath;
    private boolean mHideDialog;
    private boolean mOnlyWifi;
    private UpdateFrom updateFrom;
    private IUpdateDialogFragmentListener mUpdateDialogFragmentListener;

    private AppUpgradeManager(Builder builder) {
        mActivity = builder.getActivity();
        mHttpManager = new UpdateHttpManager();
        mUpdateUrl = builder.getUpdateUrl();
        mThemeColor = builder.getThemeColor();
        mTopPic = builder.getTopPic();
        mTargetPath = builder.getTargetPath();
        mParams = builder.getParams();
        mHideDialog = builder.isHideDialog();
        mOnlyWifi = builder.isOnlyWifi();
        mUpdateDialogFragmentListener = builder.getUpdateDialogFragmentListener();
        updateFrom = AppUpgradeUtils.getAppInstallFrom(mActivity);
    }

    /**
     * download apk
     * @param context
     * @param updateInfo
     * @param downloadCallback
     */
    public static void download(final Context context, @NonNull final Update updateInfo, @Nullable final DownloadService.DownloadCallback downloadCallback) {
        if (updateInfo != null) {
            DownloadService.bindService(context.getApplicationContext(), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    ((DownloadService.DownloadBinder) service).start(updateInfo, downloadCallback);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            });
        }
    }

    public Context getContext() {
        return mActivity;
    }

    /**
     * @return update version
     */
    public Update fillUpdateAppData() {
        if (mUpdateApp != null) {
            mUpdateApp.setTargetPath(mTargetPath);
            mUpdateApp.setHttpManager(mHttpManager);
            mUpdateApp.setHideDialog(mHideDialog);
            mUpdateApp.setOnlyWifi(mOnlyWifi);
            return mUpdateApp;
        }
        return null;
    }

    /**
     * to update page
     */
    public void showDialogFragment(Update updateInfo) {
        if (mActivity != null && !mActivity.isFinishing()) {
            Bundle bundle = new Bundle();
            fillUpdateAppData();
            bundle.putSerializable(INTENT_KEY, mUpdateApp);
            if (mThemeColor != 0) {
                bundle.putInt(THEME_KEY, mThemeColor);
            }

            if (mTopPic != 0) {
                bundle.putInt(TOP_IMAGE_KEY, mTopPic);
            }

            UpdateDialogFragment
                    .newInstance(bundle)
                    .setUpdateDialogFragmentListener(mUpdateDialogFragmentListener)
                    .show(((FragmentActivity) mActivity).getSupportFragmentManager(), "dialog");

        }

    }

    /**
     * upgrade
     */

    public void update(UpdateCallback callback) {
        checkNewApp(callback);
    }

    /**
     * check whether has new version
     *
     * @param callback upgrade callback
     */
    public void checkNewApp(final UpdateCallback callback) {
        if (callback == null) {
            return;
        }

        if (updateFrom == UpdateFrom.GOOGLE_PLAY) {
            mUpdateApp = AppUpgradeUtils.getLatestAppVersionGooglePlay(mActivity);
            checkUpgrade(callback);
        } else {
            callback.onBefore();

            if (DownloadService.isRunning || UpdateDialogFragment.isShow) {
                callback.onAfter(true);
                return;
            }

            //build custom params
            Map<String, String> params = new HashMap<String, String>();
            //add custom params
            if (mParams != null && !mParams.isEmpty()) {
                params.clear();
                params.putAll(mParams);
            }

            //call update api
            mHttpManager.asyncGet(mUpdateUrl, params, new HttpManager.Callback() {
                @Override
                public void onResponse(String result) {
                    if (result != null) {
                        processData(result, callback);
                    } else {
                        callback.onAfter(false);
                    }
                }

                @Override
                public void onError(String error) {
                    callback.onAfter(false);
                    callback.noNewApp(error);
                }
            });
        }
    }

    /**
     * download background
     * @param downloadCallback
     */
    public void download(@Nullable final DownloadService.DownloadCallback downloadCallback) {
        if (mUpdateApp != null) {
            mUpdateApp.setTargetPath(mTargetPath);
            mUpdateApp.setHttpManager(mHttpManager);
            DownloadService.bindService(mActivity.getApplicationContext(), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    ((DownloadService.DownloadBinder) service).start(mUpdateApp, downloadCallback);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            });
        }
    }

    /**
     * download background
     */
    public void download() {
        download(null);
    }

    /**
     * parse upgrade info
     *
     * @param result
     * @param callback
     */
    private void processData(String result, @NonNull UpdateCallback callback) {
        try {
            mUpdateApp = callback.parseResponse(result);
            checkUpgrade(callback);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private void checkUpgrade(@NonNull UpdateCallback callback) {
        if (mUpdateApp != null) {
            callback.showUpgradeDialog(mUpdateApp, mActivity, this);
        } else {
            callback.onAfter(false);
        }
    }

    public static class Builder {
        private Activity mActivity;
        private String mUpdateUrl = UpgradeConfig.CUSTOM_URL;
        //1，progress color
        private int mThemeColor = 0;
        @DrawableRes
        int mTopPic = 0;
        //apk download target path
        private String mTargetPath;
        //custom params
        private Map<String, String> params;
        //whether hide progress
        private boolean mHideDialog = false;
        private boolean mOnlyWifi;
        private IUpdateDialogFragmentListener mUpdateDialogFragmentListener;

        public Map<String, String> getParams() {
            return params;
        }

        /**
         * set custom params
         * @return Builder
         */
        public Builder setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public String getTargetPath() {
            return mTargetPath;
        }

        /**
         * apk download path
         * @param targetPath
         * @return Builder
         */
        public Builder setTargetPath(String targetPath) {
            mTargetPath = targetPath;
            return this;
        }

        public Activity getActivity() {
            return mActivity;
        }

        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public String getUpdateUrl() {
            return mUpdateUrl;
        }

        /**
         * update api url
         * @param updateUrl
         * @return Builder
         */
        public Builder setUpdateUrl(String updateUrl) {
            mUpdateUrl = updateUrl;
            return this;
        }

        public int getThemeColor() {
            return mThemeColor;
        }

        /**
         * set progress color
         * @param themeColor
         * @return Builder
         */
        public Builder setThemeColor(int themeColor) {
            mThemeColor = themeColor;
            return this;
        }

        public int getTopPic() {
            return mTopPic;
        }

        /**
         * top picture
         * @param topPic
         * @return Builder
         */
        public Builder setTopPic(int topPic) {
            mTopPic = topPic;
            return this;
        }

        public IUpdateDialogFragmentListener getUpdateDialogFragmentListener() {
            return mUpdateDialogFragmentListener;
        }

        /**
         *  UpdateDialogFragment listener
         * @param updateDialogFragmentListener
         * @return Builder
         */
        public Builder setUpdateDialogFragmentListener(IUpdateDialogFragmentListener updateDialogFragmentListener) {
            this.mUpdateDialogFragmentListener = updateDialogFragmentListener;
            return this;
        }

        /**
         * @return app upgrade manager
         */
        public AppUpgradeManager build() {
            //verify
            if (getActivity() == null || TextUtils.isEmpty(getUpdateUrl())) {
                throw new NullPointerException("invalid params: activity, updateUrl can not be null");
            }

            if (TextUtils.isEmpty(getTargetPath())) {
                String path = "";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
                    try {
                        path = getActivity().getExternalCacheDir().getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(path)) {
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    }
                } else {
                    path = getActivity().getCacheDir().getAbsolutePath();
                }
                setTargetPath(path);
            }
            return new AppUpgradeManager(this);
        }

        /**
         * hide progress in dialog
         * @return Builder
         */
        public Builder hideDialogOnDownloading() {
            mHideDialog = true;
            return this;
        }

        /**
         * @return whether hide dialog
         */
        public boolean isHideDialog() {
            return mHideDialog;
        }

        public Builder setOnlyWifi() {
            mOnlyWifi = true;
            return this;
        }

        public boolean isOnlyWifi() {
            return mOnlyWifi;
        }

        public Builder handleException(ExceptionHandler exceptionHandler) {
            ExceptionHandlerHelper.init(exceptionHandler);
            return this;
        }

    }

}

