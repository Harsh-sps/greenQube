package com.aistream.greenqube.mvp.model;

public class DownloadResult {

    private int mvId;

    private String name;

    private long downloadId;

    private int status;

    private String path;

    private int reason;

    public DownloadResult(int mvId, long downloadId, String name, int status, String path, int reason) {
        this.mvId = mvId;
        this.name = name;
        this.downloadId = downloadId;
        this.status = status;
        this.path = path;
        this.reason = reason;
    }

    public int getMvId() {
        return mvId;
    }

    public void setMvId(int mvId) {
        this.mvId = mvId;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
