package com.aistream.greenqube.mvp.model;

/**
 * Created by NguyenQuocDat on 12/21/2017.
 */

public class MovieContinueWatching {
    private int idwc;
    private String fileName;
    private String image;
    private String path;
    private String time;
    private String mvName;
    private int drmtype;

    public MovieContinueWatching() {
    }

    public int getIdwc() {
        return idwc;
    }

    public void setIdwc(int idwc) {
        this.idwc = idwc;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMvName() {
        return mvName;
    }

    public void setMvName(String mvName) {
        this.mvName = mvName;
    }

    public int getDrmtype() {
        return drmtype;
    }

    public void setDrmtype(int drmtype) {
        this.drmtype = drmtype;
    }

}
