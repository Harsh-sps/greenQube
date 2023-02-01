package com.aistream.greenqube.upgrade;

import java.io.Serializable;

/**
 * update version info
 */
public class Update implements Serializable {
    private static final long serialVersionUID = 1L;
    //version
    private String version;
    //version code;
    private int versionCode;
    //apk url
    private String apk_url;
    //update description
    private String update_desc;
    /**
     * update from
     */
    private UpdateFrom updateFrom = UpdateFrom.CUSTOM;
    //title
    private String dialog_title;
    //whether must update
    private boolean constraint;

    private HttpManager httpManager;
    private String targetPath;
    private boolean mHideDialog;
    private boolean mOnlyWifi;

    //whether hide dialog
    public boolean isHideDialog() {
        return mHideDialog;
    }

    public void setHideDialog(boolean hideDialog) {
        mHideDialog = hideDialog;
    }

    public HttpManager getHttpManager() {
        return httpManager;
    }

    public void setHttpManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public boolean isConstraint() {
        return constraint;
    }

    public Update setConstraint(boolean constraint) {
        this.constraint = constraint;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Update setVersion(String version) {
        this.version = version;
        return this;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Update setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public String getApkUrl() {
        return apk_url;
    }


    public Update setApkUrl(String apk_url) {
        this.apk_url = apk_url;
        return this;
    }

    public String getUpdateDesc() {
        return update_desc;
    }

    public Update setUpdateDesc(String update_desc) {
        this.update_desc = update_desc;
        return this;
    }

    public String getDialogTitle() {
        return dialog_title;
    }

    public Update setDialogTitle(String dialog_title) {
        this.dialog_title = dialog_title;
        return this;
    }

    public boolean isOnlyWifi() {
        return mOnlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        mOnlyWifi = onlyWifi;
    }

    public UpdateFrom getUpdateFrom() {
        return updateFrom;
    }

    public void setUpdateFrom(UpdateFrom updateFrom) {
        this.updateFrom = updateFrom;
    }
}
