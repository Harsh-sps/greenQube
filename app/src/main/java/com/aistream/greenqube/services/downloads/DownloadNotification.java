/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aistream.greenqube.services.downloads;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.aistream.greenqube.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * This class handles the updating of the Notification Manager for the
 * cases where there is an ongoing download. Once the download is complete
 * (be it successful or unsuccessful) it is no longer the responsibility
 * of this component to show the download in the notification manager.
 */
class DownloadNotification {

    Context mContext;
    HashMap<Long, NotificationItem> mNotifications;
    private SystemFacade mSystemFacade;

    static final String LOGTAG = "DownloadNotification";
    static final String WHERE_RUNNING =
            "(" + Downloads.COLUMN_STATUS + " >= '100') AND (" +
                    Downloads.COLUMN_STATUS + " <= '199') AND (" +
                    Downloads.COLUMN_VISIBILITY + " IS NULL OR " +
                    Downloads.COLUMN_VISIBILITY + " == '" + Downloads.VISIBILITY_VISIBLE + "' OR " +
                    Downloads.COLUMN_VISIBILITY +
                    " == '" + Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED + "')";
    static final String WHERE_COMPLETED =
            Downloads.COLUMN_STATUS + " >= '200' AND " +
                    Downloads.COLUMN_VISIBILITY +
                    " == '" + Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED + "'";


    /**
     * This inner class is used to collate downloads that are owned by
     * the same application. This is so that only one notification line
     * item is used for all downloads of a given application.
     */
    static class NotificationItem {
        int mId;  // This first db _id for the download for the app
        int mvId;//movid download
        long mTotalCurrent = 0;
        long mTotalTotal = 0;
        double mSpeed = 0;   //download speed
        String mPackageName;  // App package name
        String mDescription;
        String mTitles;
        String mPausedText = null;
        int mStatus = 0;

        /*
         * Add a second download to this notification item.
         */
        void addItem(String title, long currentBytes, long totalBytes, double speed, int mStatus, int mvId) {
            mSpeed = speed;
            mTotalCurrent += currentBytes;
            if (totalBytes <= 0 || mTotalTotal == -1) {
                mTotalTotal = -1;
            } else {
                mTotalTotal += totalBytes;
            }
//            if (mTitleCount < 2) {
//                mTitles[mTitleCount] = title;
//            }
//            mTitleCount++;
            this.mTitles = title;
            this.mStatus = mStatus;
            this.mvId = mvId;
        }
    }


    /**
     * Constructor
     *
     * @param ctx The context to use to obtain access to the
     *            Notification Service
     */
    DownloadNotification(Context ctx, SystemFacade systemFacade) {
        mContext = ctx;
        mSystemFacade = systemFacade;
        mNotifications = new HashMap<Long, NotificationItem>();
    }

    /*
     * Update the notification ui.
     */
    public void updateNotification(Collection<DownloadInfo> downloads) {
        updateActiveNotification(downloads);
        updateCompletedNotification(downloads);
    }

    private void updateActiveNotification(Collection<DownloadInfo> downloads) {
        // Collate the notifications
        mNotifications.clear();
        for (DownloadInfo download : downloads) {
            if (!isActiveAndVisible(download)) {
                continue;
            }
            String packageName = download.mPackage;
            long max = download.mTotalBytes;
            long progress = download.mCurrentBytes;
            double speed = download.mSpeed;
            long id = download.mId;
            String title = download.mTitle;
            int mvId = download.mvId;
            if (title == null || title.length() == 0) {
                title = mContext.getResources().getString(
                        R.string.download_unknown_title);
            }

            NotificationItem item;
            if (mNotifications.containsKey(id)) {
                item = mNotifications.get(id);
                item.addItem(title, progress, max, speed, download.mStatus, mvId);
            } else {
                item = new NotificationItem();
                item.mId = (int) id;
                item.mPackageName = packageName;
                item.mDescription = download.mDescription;
                item.addItem(title, progress, max, speed, download.mStatus, mvId);
                mNotifications.put(id, item);
            }
            if (download.mStatus == Downloads.STATUS_QUEUED_FOR_WIFI
                    && item.mPausedText == null) {
                item.mPausedText = mContext.getResources().getString(
                        R.string.notification_need_wifi_for_size);
            }
        }

        // Add the notifications
        for (Map.Entry<Long, NotificationItem> entrySet : mNotifications.entrySet()) {
            NotificationItem item = entrySet.getValue();
            if (item.mTotalTotal <= 0) {
                continue;
            }
            // Build the notification object
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

            boolean hasPausedText = (item.mPausedText != null);
            int iconResource = android.R.drawable.stat_sys_download;
            if (hasPausedText) {
                iconResource = android.R.drawable.stat_sys_warning;
            }
            builder.setSmallIcon(iconResource);
            builder.setOngoing(true);

            // Build the RemoteView object
            RemoteViews expandedView = new RemoteViews(mContext.getPackageName(),
                    R.layout.status_bar_ongoing_event_progress_bar);
            StringBuilder title = new StringBuilder(item.mTitles);
//            if (item.mTitleCount > 1) {
//                title.append(mContext.getString(R.string.notification_filename_separator));
//                title.append(item.mTitles[1]);
//                builder.setNumber(item.mTitleCount);
//                if (item.mTitleCount > 2) {
//                    title.append(mContext.getString(R.string.notification_filename_extras,
//                            new Object[] { Integer.valueOf(item.mTitleCount - 2) }));
//                }
//            } else {
            expandedView.setTextViewText(R.id.description, item.mDescription);
//            }
            expandedView.setTextViewText(R.id.title, title);

            if (hasPausedText) {
                expandedView.setViewVisibility(R.id.progress_bar, View.GONE);
                expandedView.setTextViewText(R.id.paused_text, item.mPausedText);
            } else {
                expandedView.setViewVisibility(R.id.paused_text, View.GONE);
                expandedView.setProgressBar(R.id.progress_bar,
                        (int) item.mTotalTotal,
                        (int) item.mTotalCurrent,
                        item.mTotalTotal == -1);
            }

            String downloadText = getDownloadingText(item.mTotalTotal, item.mTotalCurrent);
            expandedView.setTextViewText(R.id.progress_text, downloadText);
            expandedView.setImageViewResource(R.id.appIcon, iconResource);
            builder.setContent(expandedView);

            Intent intent = new Intent(Constants.ACTION_LIST);
            intent.setClassName(mContext.getPackageName(), DownloadReceiver.class.getName());
            intent.setData(ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, item.mId));
            intent.putExtra("multiple", mNotifications.size());
            builder.setContentIntent(PendingIntent.getBroadcast(mContext, 0, intent, 0));

            if (item.mStatus == Downloads.STATUS_RUNNING && item.mTitles != mContext.getResources().getString(
                    R.string.download_unknown_title)) {
                mSystemFacade.postNotification(item.mId, builder.build());
                Intent intentUpdate = new Intent(Constants.ACTION_UPDATE);
                intentUpdate.putExtra("downloadid", entrySet.getKey());
                intentUpdate.putExtra("downloadtext", downloadText);
                intentUpdate.putExtra("mTotalTotal",  (int)item.mTotalTotal);
                intentUpdate.putExtra("mTotalCurrent", (int) item.mTotalCurrent);
                intentUpdate.putExtra("mSpeed", item.mSpeed);
                intentUpdate.putExtra("movieid", (int) item.mvId);
                mContext.sendBroadcast(intentUpdate);
            } else {
                mSystemFacade.cancelNotification(item.mId);
            }
//             mSystemFacade.postNotification(download.mId, builder.build());
        }
    }


    private void updateCompletedNotification(Collection<DownloadInfo> downloads) {
        for (DownloadInfo download : downloads) {
            if (!isCompleteAndVisible(download)) {
                continue;
            }
            // Add the notifications
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);

            long id = download.mId;
            String title = download.mTitle;
            if (title == null || title.length() == 0) {
                title = mContext.getResources().getString(
                        R.string.download_unknown_title);
            }
            Uri contentUri =
                    ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, id);
            String caption;
            Intent intent;
            if (Downloads.isStatusError(download.mStatus)) {
                caption = mContext.getResources()
                        .getString(R.string.notification_download_failed);
                intent = new Intent(Constants.ACTION_LIST);
            } else {
                caption = mContext.getResources()
                        .getString(R.string.notification_download_complete);
                if (download.mDestination == Downloads.DESTINATION_EXTERNAL) {
                    intent = new Intent(Constants.ACTION_OPEN);
                } else {
                    intent = new Intent(Constants.ACTION_LIST);
                }
            }
            intent.setClassName(mContext.getPackageName(),
                    DownloadReceiver.class.getName());
            intent.setData(contentUri);

            builder.setWhen(download.mLastMod);
            builder.setContentTitle(title);
            builder.setContentText(caption);
            builder.setContentIntent(PendingIntent.getBroadcast(mContext, 0, intent, 0));
            intent = new Intent(Constants.ACTION_HIDE);
            intent.setClassName(mContext.getPackageName(),
                    DownloadReceiver.class.getName());
            intent.setData(contentUri);

            builder.setDeleteIntent(PendingIntent.getBroadcast(mContext, 0, intent, 0));

            if (download.mStatus == Downloads.STATUS_RUNNING)
                mSystemFacade.postNotification(download.mId, builder.build());
            else
                mSystemFacade.cancelNotification(download.mId);
//             mSystemFacade.postNotification(download.mId, builder.build());
        }
    }

    private boolean isActiveAndVisible(DownloadInfo download) {
        return 100 <= download.mStatus && download.mStatus < 200
                && download.mVisibility != Downloads.VISIBILITY_HIDDEN;
    }

    private boolean isCompleteAndVisible(DownloadInfo download) {
        return download.mStatus >= 200
                && download.mVisibility == Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
    }

    /*
     * Helper function to build the downloading text.
     */
    private String getDownloadingText(long totalBytes, long currentBytes) {
        if (totalBytes <= 0) {
            return "";
        }
        long progress = currentBytes * 100 / totalBytes;
        StringBuilder sb = new StringBuilder();
        sb.append(progress);
        sb.append('%');
        return sb.toString();
    }

}
