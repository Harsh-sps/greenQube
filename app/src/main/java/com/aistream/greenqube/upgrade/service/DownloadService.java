package com.aistream.greenqube.upgrade.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aistream.greenqube.upgrade.HttpManager;
import com.aistream.greenqube.upgrade.Update;
import com.aistream.greenqube.R;
import com.aistream.greenqube.upgrade.utils.AppUpgradeUtils;

import java.io.File;


/**
 * download service
 */
public class DownloadService extends Service {

    private static final int NOTIFY_ID = 0;
    private static final String TAG = DownloadService.class.getSimpleName();
    private static final String CHANNEL_ID = "app_update_id";
    private static final CharSequence CHANNEL_NAME = "app_update_channel";

    public static boolean isRunning = false;
    private NotificationManager mNotificationManager;
    private DownloadBinder binder = new DownloadBinder();
    private NotificationCompat.Builder mBuilder;

    public static void bindService(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        isRunning = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isRunning = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        mNotificationManager = null;
        super.onDestroy();
    }

    /**
     * 创建通知
     */
    private void setUpNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(false);
            channel.enableLights(false);

            mNotificationManager.createNotificationChannel(channel);
        }


        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Start Download...")
                .setContentText("Connecting Server...")
                .setSmallIcon(R.mipmap.app_update_icon)
                .setLargeIcon(AppUpgradeUtils.drawableToBitmap(AppUpgradeUtils.getAppIcon(DownloadService.this)))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

    /**
     * download module
     */
    private void startDownload(Update updateApp, final DownloadCallback callback) {
        String apkUrl = updateApp.getApkUrl();
        Log.d("DownloadService", "startDownload apkUrl: "+apkUrl);
        if (TextUtils.isEmpty(apkUrl)) {
            String contentText = "Invalid download url";
            stop(contentText);
            return;
        }
        String appName = AppUpgradeUtils.getApkName(updateApp);
        File appDir = new File(updateApp.getTargetPath());
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        String target = appDir + File.separator + updateApp.getVersion();
        Log.d("DownloadService", "app download target: "+target);
        updateApp.getHttpManager().download(apkUrl, target, appName, new FileDownloadCallBack(callback));
    }

    private void stop(String contentText) {
        if (mBuilder != null) {
            mBuilder.setContentTitle(AppUpgradeUtils.getAppName(DownloadService.this))
                    .setContentText(contentText);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFY_ID, notification);
        }
        close();
    }

    private void close() {
        stopSelf();
        isRunning = false;
    }

    /**
     * downlaod callback
     */
    public interface DownloadCallback {

        void onStart();

        void onProgress(float progress, long totalSize);

        void setMax(long totalSize);

        boolean onFinish(File file);

        void onError(String msg);

        boolean onInstallAppAndAppOnForeground(File file);
    }

    /**
     * download binder
     */
    public class DownloadBinder extends Binder {
        /**
         * start download
         *
         * @param updateApp
         * @param callback
         */
        public void start(Update updateApp, DownloadCallback callback) {
            startDownload(updateApp, callback);
        }

        public void stop(String msg) {
            DownloadService.this.stop(msg);
        }
    }

    class FileDownloadCallBack implements HttpManager.FileCallback {
        private final DownloadCallback mCallBack;
        int oldRate = 0;

        public FileDownloadCallBack(@Nullable DownloadCallback callback) {
            super();
            this.mCallBack = callback;
        }

        @Override
        public void onBefore() {
            setUpNotification();
            if (mCallBack != null) {
                mCallBack.onStart();
            }
        }

        @Override
        public void onProgress(float progress, long total) {
            int rate = Math.round(progress * 100);
            if (oldRate != rate) {
                if (mCallBack != null) {
                    mCallBack.setMax(total);
                    mCallBack.onProgress(progress, total);
                }

                if (mBuilder != null) {
                    mBuilder.setContentTitle("Downloading：" + AppUpgradeUtils.getAppName(DownloadService.this))
                            .setContentText(rate + "%")
                            .setProgress(100, rate, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
                    mNotificationManager.notify(NOTIFY_ID, notification);
                }

                oldRate = rate;
            }


        }

        @Override
        public void onError(String error) {
            Toast.makeText(DownloadService.this, "Upgrade app fail." , Toast.LENGTH_SHORT).show();
            if (mCallBack != null) {
                mCallBack.onError(error);
            }
            try {
                mNotificationManager.cancel(NOTIFY_ID);
                close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onResponse(File file) {
            if (mCallBack != null) {
                if (!mCallBack.onFinish(file)) {
                    close();
                    return;
                }
            }

            try {

                if (AppUpgradeUtils.isAppOnForeground(DownloadService.this) || mBuilder == null) {
                    mNotificationManager.cancel(NOTIFY_ID);

                    if (mCallBack != null) {
                        boolean temp = mCallBack.onInstallAppAndAppOnForeground(file);
                        if (!temp) {
                            AppUpgradeUtils.installApp(DownloadService.this, file);
                        }
                    } else {
                        AppUpgradeUtils.installApp(DownloadService.this, file);
                    }
                } else {
                    Intent installAppIntent = AppUpgradeUtils.getInstallAppIntent(DownloadService.this, file);
                    if (installAppIntent != null) {
                        PendingIntent contentIntent = PendingIntent.getActivity(DownloadService.this, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(contentIntent)
                                .setContentTitle(AppUpgradeUtils.getAppName(DownloadService.this))
                                .setContentText("Download completed, please click install.")
                                .setProgress(0, 0, false)
                                .setDefaults((Notification.DEFAULT_ALL));
                        Notification notification = mBuilder.build();
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        mNotificationManager.notify(NOTIFY_ID, notification);
                    }
                }

                close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }
}
