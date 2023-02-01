package com.aistream.greenqube.mvp.model;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by NguyenQuocDat on 12/21/2017.
 */

public class MovieDownload implements Cloneable{

    public static final int PENDING = 1;
    public static final int DOWNLOADING = 2;
    public static final int FAIL = 3;
    public static final int SUCCESS = 4;

    private int idDownload;
    private int mvId;  //-1: pending movie dir
    private String fileName;
    private String image;
    private int statusdl = -1;
    private String path;
    private String time;
    private String mvName;
    private int screenDown;
    private String dataGson;
    private Integer reason = -1;
    private Integer typeDownload;
    private Integer fileSize;
    private int typeMovie;
    private int typeCodeRespon;
    private int typeM3U8orDownloadRight;
    private int drmType;
    private String failDescription;
    private long timeContinue;
    private long rentalPeriod;
    private long rentalDayPeriod;
    private long expireon;
    private long expireTime;
    private int duration;
    private int vip;  //1: vip
    private long rentalStart;
    private long rentalEnd;

    private int mTotalCurrent;
    private double speed = -1;

    public MovieDownload() {
    }

    public MovieDownload cloneMovie() {
        MovieDownload movieDownload = null;
        try {
            movieDownload = (MovieDownload) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return movieDownload;
    }

    public boolean isExpired() {
        if (expireTime > 0 && System.currentTimeMillis() > expireTime) {
            return true;
        }
        return false;
    }

    public boolean hasStartDownloaded() {
        if (!TextUtils.isEmpty(this.path)) {
            File file = new File(path.replace("file://", ""));
            if (file.exists() && file.length() > 0) {
                return true;
            }
        }
        return false;
    }

    public int getDownloadSize() {
        if (!TextUtils.isEmpty(this.path)) {
            File file = new File(path.replace("file://", ""));
            if (file.exists()) {
                return (int) file.length();
            }
        }
        return 0;
    }

    public long getRemainRentalPeriod() {
        long clockTime = SystemClock.elapsedRealtime();
        Log.d("MovieDownload", "movie: "+mvName+", rentalStart: "+rentalStart+", rentalEnd: "+rentalEnd
                +", clockTime: "+clockTime+", expireTime: "+expireTime+", currTime: "+System.currentTimeMillis());
        if (rentalEnd > 0) {
            return rentalEnd - rentalStart;
        } else {
            return expireTime - System.currentTimeMillis();
        }
    }

    public boolean hasExpired() {
        int remainRentalPeriod = (int)(getRemainRentalPeriod() / 1000);
        if (remainRentalPeriod == 0 && expireTime > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPremium() {
        if (typeMovie == 2) {
            return true;
        }
        return false;
    }

    public boolean isVip() {
        if (vip == 1) {
            return true;
        }
        return false;
    }

    public boolean isFreeNotVip() {
        if (typeMovie == 1 && vip == 0) {
            return true;
        }
        return false;
    }

    public long getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(long rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public long getRentalDayPeriod() {
        return rentalDayPeriod;
    }

    public void setRentalDayPeriod(long rentalDayPeriod) {
        this.rentalDayPeriod = rentalDayPeriod;
    }

    public long getTimeContinue() {
        if (duration * 1000L - timeContinue < 5000L) {
            return 0;
        }
        return timeContinue;
    }

    public void setTimeContinue(long timeContinue) {
        this.timeContinue = timeContinue;
    }

    public void setIdDownload(int idDownload) {
        this.idDownload = idDownload;
    }

    public int getIdDownload() {
        return this.idDownload;
    }

    public void setMvId(int id) {
        this.mvId = id;
    }

    public int getMvId() {
        return this.mvId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public void setStatus(int status) {
        this.statusdl = status;
    }

    public int getStatus() {
        return this.statusdl;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return this.time;
    }

    public void setMvName(String mvName) {
        this.mvName = mvName;
    }

    public String getMvName() {
        return this.mvName;
    }

    public void setScreenDown(int screenDown) {
        this.screenDown = screenDown;
    }

    public int getScreenDown() {
        return this.screenDown;
    }

    public void setDataGson(String dataGson) {
        this.dataGson = dataGson;
    }

    public String getDataGson() {
        return this.dataGson;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public Integer getReason() {
        return this.reason;
    }

    public Integer getTypeDownload() {
        return typeDownload;
    }

    public void setTypeDownload(Integer typeDownload) {
        this.typeDownload = typeDownload;
    }

    public void setTypeMovie(int typeMovie) {
        this.typeMovie = typeMovie;
    }

    public int getTypeMovie() {
        return this.typeMovie;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public int getTypeCodeRespon() {
        return typeCodeRespon;
    }

    public void setTypeCodeRespon(int typeCodeRespon) {
        this.typeCodeRespon = typeCodeRespon;
    }

    public int getTypeM3U8orDownloadRight() {
        return typeM3U8orDownloadRight;
    }

    public void setTypeM3U8orDownloadRight(int typeM3U8orDownloadRight) {
        this.typeM3U8orDownloadRight = typeM3U8orDownloadRight;
    }

    public int getDrmType() {
        return drmType;
    }

    public void setDrmType(int drmType) {
        this.drmType = drmType;
    }

    public String getFailDescription() {
        return failDescription;
    }

    public void setFailDescription(String failDescription) {
        this.failDescription = failDescription;
    }

    public long getExpireon() {
        return expireon;
    }

    public void setExpireon(long expireon) {
        this.expireon = expireon;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getmTotalCurrent() {
        if (mTotalCurrent == 0) {
            mTotalCurrent = getDownloadSize();
        }
        return mTotalCurrent;
    }

    public void setmTotalCurrent(int mTotalCurrent) {
        this.mTotalCurrent = mTotalCurrent;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
