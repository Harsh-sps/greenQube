package com.aistream.greenqube.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aistream.greenqube.OgleApplication;

/**
 * @author Jeff
 */
public class StorageStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        OgleApplication mOgleApp = (OgleApplication) context.getApplicationContext();
        StorageEventListener storageEventListener = mOgleApp.getStorageEventListener();
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Log.d("StorageStateReceiver", "SDCard mounted");
            if (storageEventListener != null) {
                storageEventListener.onStorageStateChanged(1);
            }
        } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)
                || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                || action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
            Log.d("StorageStateReceiver", "SDCard removed");
            if (storageEventListener != null) {
                storageEventListener.onStorageStateChanged(0);
            }
        }
    }
}
