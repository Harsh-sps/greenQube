package com.aistream.greenqube.mvp.model;

/**
 * Created by Administrator on 8/4/2017.
 */

public class DataUpdate {
    private long downloadId;
    private int sttDownload;
    private int versioncode;
    private String pathupdate;

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public int getSttDownload() {
        return sttDownload;
    }

    public void setSttDownload(int sttDownload) {
        this.sttDownload = sttDownload;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getPathupdate() {
        return pathupdate;
    }

    public void setPathupdate(String pathupdate) {
        this.pathupdate = pathupdate;
    }
}
