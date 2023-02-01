package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PhuDepTraj on 9/20/2018.
 */

public class ItemBackupData {
    @SerializedName("Download Data")
    @Expose
    private List<DownloadData> downloadData = null;
    @SerializedName("Error Data")
    @Expose
    private List<ErrorData> errorData = null;
    @SerializedName("Movie Browse Data")
    @Expose
    private List<MovieBrowseData> movieBrowseData = null;
    @SerializedName("Movie Playback Data")
    @Expose
    private List<MoviePlaybackData> moviePlaybackData = null;
    @SerializedName("Search Data")
    @Expose
    private List<SearchData> searchData = null;

//    public ItemBackupData(List<DownloadData> downloadData, List<ErrorData> errorData, List<MovieBrowseData> movieBrowseData,
//                          List<MoviePlaybackData> moviePlaybackData, List<SearchData> searchData) {
//        this.downloadData = downloadData;
//        this.errorData = errorData;
//        this.movieBrowseData = movieBrowseData;
//        this.moviePlaybackData = moviePlaybackData;
//        this.searchData = searchData;
//    }

    public List<DownloadData> getDownloadData() {
        return downloadData;
    }

    public void setDownloadData(List<DownloadData> downloadData) {
        this.downloadData = downloadData;
    }

    public List<ErrorData> getErrorData() {
        return errorData;
    }

    public void setErrorData(List<ErrorData> errorData) {
        this.errorData = errorData;
    }

    public List<MovieBrowseData> getMovieBrowseData() {
        return movieBrowseData;
    }

    public void setMovieBrowseData(List<MovieBrowseData> movieBrowseData) {
        this.movieBrowseData = movieBrowseData;
    }

    public List<MoviePlaybackData> getMoviePlaybackData() {
        return moviePlaybackData;
    }

    public void setMoviePlaybackData(List<MoviePlaybackData> moviePlaybackData) {
        this.moviePlaybackData = moviePlaybackData;
    }

    public List<SearchData> getSearchData() {
        return searchData;
    }

    public void setSearchData(List<SearchData> searchData) {
        this.searchData = searchData;
    }
}
