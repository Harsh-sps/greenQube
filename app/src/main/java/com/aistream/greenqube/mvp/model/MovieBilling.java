package com.aistream.greenqube.mvp.model;

import android.os.SystemClock;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieBilling {

    @SerializedName("movie_id")
    @Expose
    private int movieId;

    @SerializedName("expire_on")
    @Expose
    private long expireOn;

    @SerializedName("rental_start")
    @Expose
    private long rentalStart;

    @SerializedName("rental_end")
    @Expose
    private long rentalEnd;

    @SerializedName("rental_certificate")
    @Expose
    private String rentalCertificate;

    public boolean hasExpired() {
        if (getRemainRentalPeriod() > 0) {
            return false;
        }
        return true;
    }

    public long getRemainRentalPeriod() {
        long currClock = SystemClock.elapsedRealtime();
        long remainTime = 0;
        if (currClock < rentalStart) {
            remainTime = rentalEnd - rentalStart - currClock;
        } else {
            remainTime = rentalEnd - currClock;
        }
        return remainTime;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public long getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(long expireOn) {
        this.expireOn = expireOn;
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

    public String getRentalCertificate() {
        return rentalCertificate;
    }

    public void setRentalCertificate(String rentalCertificate) {
        this.rentalCertificate = rentalCertificate;
    }
}
