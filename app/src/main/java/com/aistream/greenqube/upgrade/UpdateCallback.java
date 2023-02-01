package com.aistream.greenqube.upgrade;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aistream.greenqube.upgrade.utils.AppUpgradeUtils;
import org.json.JSONObject;

/**
 * upgrade callback
 */
public class UpdateCallback {

    /**
     * parse update response
     *
     * @param json update server response data
     * @return Update
     */
    protected Update parseResponse(String json) {
        Update updateInfo = null;
        try {
            Log.d("AppUpgrade","upgrade info: "+json);
            JSONObject jsonObject = new JSONObject(json);
            JSONObject status = (JSONObject) jsonObject.get("status");
            Log.d("AppUpgrade","status: "+status);
            if (status != null && status.getInt("code") == 0) {
                JSONObject data = (JSONObject) jsonObject.get("data");
                if (data != null) {
                    String version = data.optString("version");
                    int versionCode = data.optInt("version_code");
                    String apk_uri = data.optString("apk_uri");
                    Log.d("AppUpgrade","version: "+version+", versionCode: "+versionCode+", apk_uri: "+apk_uri);
                    if (!TextUtils.isEmpty(version)
                            && versionCode > 0
                            && !TextUtils.isEmpty(apk_uri)) {
                        updateInfo = new Update();
                        updateInfo.setVersion(version)
                                .setVersionCode(versionCode)
                                .setApkUrl(apk_uri)
                                .setUpdateDesc("This is a new version")
                                .setConstraint(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateInfo;
    }

    /**
     * show upgrade dialog
     * @param updateInfo        new update info
     */
    protected void showUpgradeDialog(Update updateInfo, Context context, AppUpgradeManager updateAppManager) {
        boolean hasUpdateVersion = false;
        if (updateInfo != null) {
            switch (updateInfo.getUpdateFrom()) {
                case GOOGLE_PLAY:
                    String version = AppUpgradeUtils.getAppInstalledVersion(context);
                    if (!TextUtils.isEmpty(version)) {
                        int flag = AppUpgradeUtils.compareVersion(updateInfo.getVersion(), version);
                        if (flag > 0) {
                            hasUpdateVersion = true;
                        }
                    }
                    break;
                default:
                    int oldVersionCode = AppUpgradeUtils.getAppInstalledVersionCode(context);
                    Log.d("AppUpgrade", "new versionCode:"+updateInfo.getVersionCode()+", old versionCode:"+oldVersionCode);
                    if (updateInfo.getVersionCode() > oldVersionCode) {
                        hasUpdateVersion = true;
                    }
                    break;
            }
        }

        if (hasUpdateVersion) {
            updateAppManager.showDialogFragment(updateInfo);
        }
        onAfter(hasUpdateVersion);
    }

    protected void onAfter(boolean hasNewVersion) {
    }

    protected void noNewApp(String error) {
    }

    protected void onBefore() {
    }

}
