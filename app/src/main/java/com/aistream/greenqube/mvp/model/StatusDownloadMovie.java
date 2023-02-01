package com.aistream.greenqube.mvp.model;

/**
 * Created by PhuDepTraj on 5/28/2018.
 */

public class StatusDownloadMovie {
    private Integer dlMovIdDown;
    private Integer dlMovID;
    private String dlMovName;
    private String dlImage;
    private Integer dlStatus = -1;

    public Integer getDlMovIdDown() {
        return dlMovIdDown;
    }

    public void setDlMovIdDown(Integer dlMovIdDown) {
        this.dlMovIdDown = dlMovIdDown;
    }

    public Integer getDlMovID() {
        return dlMovID;
    }

    public void setDlMovID(Integer dlMovID) {
        this.dlMovID = dlMovID;
    }

    public String getDlMovName() {
        return dlMovName;
    }

    public void setDlMovName(String dlMovName) {
        this.dlMovName = dlMovName;
    }

    public String getDlImage() {
        return dlImage;
    }

    public void setDlImage(String dlImage) {
        this.dlImage = dlImage;
    }

    public Integer getDlStatus() {
        return dlStatus;
    }

    public void setDlStatus(Integer dlStatus) {
        this.dlStatus = dlStatus;
    }
}
