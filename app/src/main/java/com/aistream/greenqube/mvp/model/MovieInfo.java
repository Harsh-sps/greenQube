package com.aistream.greenqube.mvp.model;

import android.os.SystemClock;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PhuDepTraj on 5/3/2018.
 */

public class MovieInfo {
    public static final int MOVIE_TYPE = 1;
    public static final int SHORT_VIDEO_TYPE = 4;

    @SerializedName("movie_id")
    @Expose
    private int movieId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("synopsis")
    @Expose
    private String synopsis;
    @SerializedName("pg_level")
    @Expose
    private Integer pgLevel;
    @SerializedName("pg_name")
    @Expose
    private String pgName;
    @SerializedName("rating_id")
    @Expose
    private Integer ratingId;
    @SerializedName("downloads")
    @Expose
    private Integer downloads;
    @SerializedName("publish_date")
    @Expose
    private String publishDate;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("expire_on")
    @Expose
    private long expireOn;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("poster")
    @Expose
    private String poster;
    @SerializedName("preview")
    @Expose
    private String preview;
    @SerializedName("theatrical_poster")
    @Expose
    private String theatricalPoster;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("star_score")
    @Expose
    private String starScore;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("directors")
    @Expose
    private List<Director> directors = null;
    @SerializedName("actors")
    @Expose
    private List<Actor> actors = null;
    @SerializedName("publishers")
    @Expose
    private List<Publisher> publishers = null;
    @SerializedName("producers")
    @Expose
    private List<Producer> producers = null;
    @SerializedName("keywords")
    @Expose
    private List<Keyword> keywords = null;
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;
    @SerializedName("subtitles")
    @Expose
    private List<Subtitle> subtitles = null;
    @SerializedName("audios")
    @Expose
    private List<Audio> audios = null;
    @SerializedName("quality_list")
    @Expose
    private List<Quality> qualityList = null;
    @SerializedName("vip")
    @Expose
    private int vip;  //1: vip

    @SerializedName("content_type")
    @Expose
    private int contentType;

    @SerializedName("video_type")
    @Expose
    private VideoType videoType;

    private long rentalStart;

    private long rentalEnd;

    private MovieBilling movieBilling;

    public boolean isFree() {
        if (type == 2) {
            if (expireOn > System.currentTimeMillis()) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean isVip() {
        if (vip == 1) {
            return true;
        }
        return false;
    }

    public boolean isFreeNotVip() {
        if (type == 1) {
            return true;
        }
        return false;
    }

    public boolean isShortVideo() {
        if (contentType == SHORT_VIDEO_TYPE) {
            return true;
        }
        return false;
    }

    public Quality getDefaultQuality() {
        return qualityList.isEmpty()? null: qualityList.get(0);
    }

    public boolean hasExpired() {
        if (movieBilling != null) {
            this.expireOn = movieBilling.getExpireOn();
            return movieBilling.hasExpired();
        } else {
            int remainRentalTime = getRemainRentalPeriod();
            if (remainRentalTime == 0) {
                return true;
            }
        }
        return false;
    }

    public int getRemainRentalPeriod() {
        long realtime = SystemClock.elapsedRealtime();
        long remainRentalTime = 0;
        if (rentalEnd > 0) {
            remainRentalTime = rentalEnd - rentalStart;
        } else {
            remainRentalTime = expireOn - System.currentTimeMillis();
        }
        if (remainRentalTime < 1000) {
            remainRentalTime = 0;
        }
        return (int)(remainRentalTime / 1000);
    }

    public int getFileSize() {
        if (qualityList != null) {
            Quality quality = qualityList.get(0);
            if (quality != null) {
                return quality.getFileSize();
            }
        }
        return 0;
    }

    public boolean isMatchKeyWorld(String keyword) {
        if (keywords != null) {
            for (Keyword kword: keywords) {
                if (kword.getName().equalsIgnoreCase(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isMatchType(VideoType videoType) {
        if (this.videoType == videoType) {
            return true;
        } else if (this.videoType != null && videoType != null && this.videoType.getType() == videoType.getType()) {
            return true;
        }
        return false;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Integer getPgLevel() {
        return pgLevel;
    }

    public void setPgLevel(Integer pgLevel) {
        this.pgLevel = pgLevel;
    }

    public String getPgName() {
        return pgName;
    }

    public void setPgName(String pgName) {
        this.pgName = pgName;
    }

    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(long expireOn) {
        this.expireOn = expireOn;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTheatricalPoster() {
        return theatricalPoster;
    }

    public void setTheatricalPoster(String theatricalPoster) {
        this.theatricalPoster = theatricalPoster;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStarScore() {
        return starScore;
    }

    public void setStarScore(String starScore) {
        this.starScore = starScore;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public void setDirectors(List<Director> directors) {
        this.directors = directors;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Publisher> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<Publisher> publishers) {
        this.publishers = publishers;
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }

    public List<Quality> getQualityList() {
        return qualityList;
    }

    public void setQualityList(List<Quality> qualityList) {
        this.qualityList = qualityList;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
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

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public MovieBilling getMovieBilling() {
        return movieBilling;
    }

    public void setMovieBilling(MovieBilling movieBilling) {
        this.movieBilling = movieBilling;
    }
}
