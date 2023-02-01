package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PhuDepTraj on 9/18/2018.
 */

public class DownloadRight {
    @SerializedName("movieId")
    @Expose
    private Integer movieId;
    @SerializedName("is_free")
    @Expose
    private Integer isFree;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("drm_key")
    @Expose
    private String drmKey;
    @SerializedName("expiry_times")
    @Expose
    private long expiryTimes;

    private long rentalStart;

    private long rentalEnd;

    @SerializedName("available_downloads")
    @Expose
    private int availableDownloads;

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDrmKey() {
        return drmKey;
    }

    public void setDrmKey(String drmKey) {
        this.drmKey = drmKey;
    }

    public long getExpiryTimes() {
        return expiryTimes;
    }

    public void setExpiryTimes(long expiryTimes) {
        this.expiryTimes = expiryTimes;
    }

    public long getRentalStart() {
        return rentalStart;
    }

    public void setRentalStart(long rentalStart) {
        this.rentalStart = rentalStart;
    }

    public long getRentalEnd() {
        return rentalEnd;
    }

    public void setRentalEnd(long rentalEnd) {
        this.rentalEnd = rentalEnd;
    }

    public int getAvailableDownloads() {
        return availableDownloads;
    }

    public void setAvailableDownloads(int availableDownloads) {
        this.availableDownloads = availableDownloads;
    }
}
