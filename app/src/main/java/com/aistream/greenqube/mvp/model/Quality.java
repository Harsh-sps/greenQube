package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 5/16/2017.
 */

public class Quality {
    @SerializedName("quality")
    @Expose
    private Integer quality;
    @SerializedName("drm_type")
    @Expose
    private Integer drmType;
    @SerializedName("file_name")
    @Expose
    private String fileName;
    @SerializedName("file_size")
    @Expose
    private Integer fileSize;
    @SerializedName("aspect_ratio")
    @Expose
    private String aspectRatio;
    @SerializedName("resolution")
    @Expose
    private Integer resolution;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("points")
    @Expose
    private Integer points;
    @SerializedName("rental_period")
    @Expose
    private Integer rentalPeriod;
    @SerializedName("md5")
    @Expose
    private String md5;

    public long getRentalForMillSeconds() {
        return (long) (rentalPeriod * 24L * 60 * 60 * 1000);
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public Integer getDrmType() {
        return drmType;
    }

    public void setDrmType(Integer drmType) {
        this.drmType = drmType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(Integer rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
