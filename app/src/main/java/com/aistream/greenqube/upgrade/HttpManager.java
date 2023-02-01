package com.aistream.greenqube.upgrade;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * app update interface
 */
public interface HttpManager extends Serializable {

    void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack);

    /**
     * app download
     */
    void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull FileCallback callback);

    /**
     * download callback
     */
    interface FileCallback {
        /**
         * progress
         */
        void onProgress(float progress, long total);

        void onError(String error);

        void onResponse(File file);

        void onBefore();
    }

    /**
     * network callback
     */
    interface Callback {
        void onResponse(String result);

        void onError(String error);
    }
}
