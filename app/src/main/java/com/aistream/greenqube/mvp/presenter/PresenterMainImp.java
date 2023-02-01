package com.aistream.greenqube.mvp.presenter;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.R;
import com.aistream.greenqube.customs.CustomDialog_AccountBalance;
import com.aistream.greenqube.customs.CustomDialog_MovieVIP;
import com.aistream.greenqube.customs.CustomDialog_Redeem;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.model.BillingStart;
import com.aistream.greenqube.mvp.model.DataUser;
import com.aistream.greenqube.mvp.model.DetectFMS;
import com.aistream.greenqube.mvp.model.DownloadData;
import com.aistream.greenqube.mvp.model.DownloadResult;
import com.aistream.greenqube.mvp.model.DownloadRight;
import com.aistream.greenqube.mvp.model.ErrorData;
import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.ItemBackupData;
import com.aistream.greenqube.mvp.model.MovieBilling;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.MoviePlaybackData;
import com.aistream.greenqube.mvp.model.Quality;
import com.aistream.greenqube.mvp.model.ReportData;
import com.aistream.greenqube.mvp.model.ResponBilling;
import com.aistream.greenqube.mvp.model.ResponDetectFMS;
import com.aistream.greenqube.mvp.model.ResponDownloadRight;
import com.aistream.greenqube.mvp.model.ResponReportUser;
import com.aistream.greenqube.mvp.model.ResponseWrapper;
import com.aistream.greenqube.mvp.model.Status;
import com.aistream.greenqube.mvp.model.VideoType;
import com.aistream.greenqube.mvp.model.WifiInfo;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.APIResultCallBack;
import com.aistream.greenqube.mvp.rest.CallBack;
import com.aistream.greenqube.mvp.rest.Config;
import com.aistream.greenqube.mvp.rest.DataLoader;
import com.aistream.greenqube.mvp.rest.RetrofitRepositoryFactory;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.services.DownloadManager;
import com.aistream.greenqube.services.downloads.Constants;
import com.aistream.greenqube.util.DialogCallBack;
import com.aistream.greenqube.util.OgleHelper;
import com.aistream.greenqube.util.WifiUtils;
import com.flixsys.ogle.sdk.DrmSdk;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterMainImp {
    private ViewMain viewMain;
    private Context mContext;
    private DataBaseHelper dataBaseHelper;
    private List<MovieInfo> listMovieInfo;
    private List<MovieInfo> listNewReleaseInfo;
    private Map<Integer, MovieInfo> movieMap;
    private List<MovieInfo> listHotMovieInfo;
    private OgleApplication ogleApplication;
    private DownloadManager downloadManager;
    private DownloadManager.Query downloadQuery;
    private SharedPreferences mPref;

    WifiManager wifiManager;
    private WifiUtils wifiUtils;
    private DataLoader dataLoader;
    private boolean firstCheckWifi = true;
    //
    public List<DownloadData> downloadDataList = new ArrayList<>();
    public List<MoviePlaybackData> moviePlaybackDataList = new ArrayList<>();
    public List<ErrorData> errorDataList = new ArrayList<>();
    private HashMap<Integer, List<MovieInfo>> genreMoviesMap = new HashMap<>();
    public static final int ALL = 0;
    public static final int NEW_RELEASES = -4;
    public static final int HOT_MOVIE = -3;
    public static final int FREE_MOVIE = -100;
    private LoginManager loginManager;

    public PresenterMainImp(Context cont, ViewMain viewMain) {
        firstCheckWifi = true;
        this.mContext = cont;
        this.viewMain = viewMain;
        dataBaseHelper = DataBaseHelper.getInstance();
        ogleApplication = (OgleApplication) mContext.getApplicationContext();
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        downloadManager = ogleApplication.getDownloadManager();
        downloadQuery = new DownloadManager.Query();
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiUtils = new WifiUtils(mContext);
        loginManager = new LoginManager(cont);

        getDataFromServer();
    }

    public void getDataFromServer() {
        new DataLoader(mContext, new DataLoader.Listener() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {

            }

            @Override
            public void expired() {

            }
        });
    }

    public void showToasApp(String msg, int gravity) {
        viewMain.showToasApp(msg, gravity);
    }

    public void refreshData() {
        listMovieInfo = null;
        listHotMovieInfo = null;
        listNewReleaseInfo = null;
        listHotMovieInfo = getListMovie();
        listHotMovieInfo = getListHotMovie();
        listNewReleaseInfo = getListNewRelease();
        genreMoviesMap.clear();
    }

    public void showLoading() {
        viewMain.showLoading();
    }

    public void hideLoading() {
        viewMain.hideLoading();
    }

    public void showMovieVIP(TextView view, int mvId) {
        MovieInfo movieInfo = getMovieInfo(mvId);
        if (movieInfo != null) {
            viewMain.showMovieVIP(view, movieInfo);
        }
    }

    public List<VideoType> getVideoTypes() {
        List<VideoType> videoTypes = new ArrayList<>();
        String videoTypeStr = mPref.getString("VideoTypes", "");
        Log.d("JeffVideo", videoTypeStr);
        try {
            if (!TextUtils.isEmpty(videoTypeStr)) {
                videoTypes = new Gson().fromJson(videoTypeStr, new TypeToken<List<VideoType>>() {
                }.getType());
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return videoTypes;
    }

    public List<Genre> getGenreList() {
        List<Genre> genreList = new ArrayList<>();
        genreList.addAll(dataBaseHelper.getGenreList());
        genreList.add(new Genre(NEW_RELEASES, mContext.getResources().getString(R.string.newmovies)));
        genreList.add(new Genre(HOT_MOVIE, mContext.getResources().getString(R.string.hotmovies)));
        return genreList;
    }

    /**
     * load movies by genre
     *
     * @param genId
     * @return
     */
    public List<MovieInfo> loadMovieFromGen(int genId) {
        if (!genreMoviesMap.containsKey(genId)) {
            List<MovieInfo> movieInfoList = new ArrayList<>(getListMovie());
            List<MovieInfo> infoList = new ArrayList<>();
            if (genId == NEW_RELEASES) {
                infoList = new ArrayList<>();
            } else if (genId == HOT_MOVIE) {
                infoList = getListHotMovie();
            } else if (genId == FREE_MOVIE) {
                for (MovieInfo movieInfo : movieInfoList) {
                    if (movieInfo.isFreeNotVip()) {
                        infoList.add(movieInfo);
                    }
                }
            } else {
                for (MovieInfo movieInfo : movieInfoList) {
                    List<Genre> genres = movieInfo.getGenres();
                    for (Genre genre : genres) {
                        if (genre.getId() == genId) {
                            infoList.add(movieInfo);
                        }
                    }
                }
            }
            genreMoviesMap.put(genId, infoList);
        }
        return genreMoviesMap.get(genId);
    }

    /**
     * get all movies
     *
     * @return
     */
    public List<MovieInfo> getListMovie() {
        if (listMovieInfo == null) {
            listMovieInfo = dataBaseHelper.getAllListMovie();
            movieMap = new HashMap<>();
            for (MovieInfo movieInfo : listMovieInfo) {
                movieMap.put(movieInfo.getMovieId(), movieInfo);
            }
        }
        return listMovieInfo;
    }

    /**
     * get movie info from db
     *
     * @param mvid
     * @return
     */
    public MovieInfo getMovieInfoByDB(int mvid) {
        return dataBaseHelper.getMovieInfo(mvid);
    }

    /**
     * get movie info from cache
     *
     * @param mid
     * @return
     */
    public MovieInfo getMovieInfo(int mid) {
        MovieInfo movieInfo = null;
        if (movieMap != null && !movieMap.isEmpty()) {
            movieInfo = movieMap.get(mid);
        }
        return movieInfo;
    }

    public List<MovieInfo> getListHotMovie() {
        if (listHotMovieInfo == null) {
            listHotMovieInfo = dataBaseHelper.getAllListHotMovie();
        }
        return listHotMovieInfo;
    }

    /**
     * get
     *
     * @return
     */
    public List<MovieInfo> getListNewRelease() {
        if (listNewReleaseInfo == null && listMovieInfo != null && !listMovieInfo.isEmpty()) {
            List<MovieInfo> list = new ArrayList<>(listMovieInfo);
            Collections.sort(list, new Comparator<MovieInfo>() {
                @Override
                public int compare(MovieInfo m1, MovieInfo m2) {
                    String s1 = m1.getReleaseDate();
                    String s2 = m2.getReleaseDate();
                    if (!TextUtils.isEmpty(s1) && !TextUtils.isEmpty(s2)) {
                        return s2.compareTo(s1);
                    }
                    return 0;
                }
            });

            listNewReleaseInfo = list;
            if (list.size() > 20) {
                listNewReleaseInfo = list.subList(0, 20);
            }
        }
        return listNewReleaseInfo;
    }

    public List<MovieInfo> getPromotionMovies() {
        List<MovieInfo> listPromotion = dataBaseHelper.getAllListNewRelease();
        return listPromotion;
    }

    private List<MovieInfo> randomMovie(List<MovieInfo> list, int n) {
        if (list.size() > n) {
            Collections.shuffle(list);
            return list.subList(0, n);
        } else {
            return list;
        }
    }

    /**
     * get favorite movies
     *
     * @return
     */
    public List<MovieInfo> getFavMovies() {
        List<MovieInfo> listFav = new ArrayList<>();
        List<MovieInfo> listMovie = getListMovie();
        String fids = mPref.getString("favorites", "");
        if (!TextUtils.isEmpty(fids)) {
            String[] listIdFav = fids.split(",");
            for (int i = 0; i < listIdFav.length; i++) {
                for (MovieInfo movie : listMovie) {
                    if (listIdFav[i].equals(String.valueOf(movie.getMovieId()))) {
                        listFav.add(movie);
                        break;
                    }
                }
            }
        }
        return listFav;
    }

    public List<WifiInfo> getListWifi() {
        return dataBaseHelper.getWifiInfoList();
    }

    /**
     * check movie play right
     *
     * @return
     */
    private boolean checkMoviePlayRight(final int mvId, boolean isOnline) {
        if (checkMoviePermission(mvId)) {
            if (isOnline) {
                return true;
            }
            final MovieDownload movieDownload = getMovieDownloadById(mvId);
            if (movieDownload != null && movieDownload.getTypeDownload() == 3) {
                if (movieDownload.hasExpired()) {
                    final MovieInfo movieInfo = getMovieInfoByDB(mvId);
                    if (movieInfo != null) {
                        if (movieDownload.isPremium()) {  //premium movie
                            showMovieBillingDialog(movieInfo, new DialogCallBack() {
                                @Override
                                public void ok() {
                                    deleteMovie(movieDownload);
                                    viewMain.updateStatusDownload(movieDownload.getMvId(), movieDownload.getIdDownload(),
                                            DownloadManager.STATUS_PENDING, movieDownload.getPath(), -1);
                                    //refresh movie downloads page
                                    viewMain.refreshMovieDownloads();
                                }

                                @Override
                                public void cancel() {
                                }
                            });
                        } else { //free or vip movie
                            OgleHelper.showDialog(mContext, mContext.getResources().getString(R.string.textupdateexpiretime), new DialogCallBack() {
                                @Override
                                public void ok() {
                                    deleteMovie(movieDownload);
                                    viewMain.updateStatusDownload(movieDownload.getMvId(), movieDownload.getIdDownload(),
                                            DownloadManager.STATUS_PENDING, movieDownload.getPath(), -1);
                                    processDownload(movieInfo);
                                    //refresh movie downloads page
                                    viewMain.refreshMovieDownloads();
                                }

                                @Override
                                public void cancel() {
                                }
                            });
                        }
                    } else {
                        ogleApplication.showToast(mContext.getResources().getString(R.string.movie_404), Gravity.CENTER);
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * online playing movie
     *
     * @param movieInfo
     * @param cookie
     * @param pauseTime
     */
    public void onlinePlayMovie(MovieInfo movieInfo, String cookie, long pauseTime, DialogCallBack callBack) {
        if (checkMoviePlayRight(movieInfo.getMovieId(), true)) {
            mPref.edit().putBoolean("onlinePlay", true).commit();
            Quality defaultQuality = movieInfo.getDefaultQuality();
            String fileName = defaultQuality.getFileName();
            String playUrl = Config.apiEndpointPublicDownM3U8 + fileName + "/" + fileName + ".m3u8";
            Log.d("OnlinePlaying", "playing url: " + playUrl);
            String imgurl = Config.picURLPath + movieInfo.getTheatricalPoster();
            viewMain.onlinePlayMovie(movieInfo.getMovieId(),
                    movieInfo.getName(),
                    playUrl,
                    movieInfo.getDefaultQuality().getFileName(),
                    cookie,
                    pauseTime,
                    imgurl);

            //download movie to local
            if (callBack != null) callBack.ok();
        }
    }

    /**
     * offline play movie
     *
     * @param movieDownload
     */
    public void clickToPlayMovie(MovieDownload movieDownload) {
        if (movieDownload != null) {
            if (checkMoviePlayRight(movieDownload.getMvId(), false)) {
                String path = movieDownload.getPath();
                String imgurl = Config.picURLPath + movieDownload.getImage();
                viewMain.clickToPlayMovie(movieDownload.getMvId(),
                        movieDownload.getMvName(),
                        path.replace("file://", "/"),
                        movieDownload.getFileName(),
                        movieDownload.getTimeContinue(),
                        imgurl);
            }
        }
    }

    /**
     * delete download movies
     *
     * @param movieDownloads
     */
    public void deleteMovie(MovieDownload... movieDownloads) {
        if (movieDownloads != null && movieDownloads.length > 0) {
            try {
                for (MovieDownload mvDownload : movieDownloads) {
                    MovieDownload movieDownload = dataBaseHelper.getMovieDownload(mvDownload.getMvId());
                    DataBaseHelper.getInstance().deleteDownload(mvDownload.getMvId());
                    downloadManager.remove(movieDownload.getIdDownload());
                    OgleHelper.deleteFolder(mContext.getExternalFilesDirs(""), movieDownload.getFileName());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            viewMain.deleteDataDownload(movieDownloads);
        }
    }

    /**
     * delete all download movies
     */
    public void deleteAllMovieDownload() {
        DataBaseHelper db = DataBaseHelper.getInstance();
        List<MovieDownload> listMovDown = db.getAllMovieDownload();
        if (listMovDown.size() > 0) {
            for (MovieDownload download : listMovDown) {
                int result = downloadManager.remove(download.getIdDownload());

                Log.i("deleteAllMovieDownload", "result = " + result);
            }
            db.deleteAllDownload();
        }
    }

    public void updateDownloadBar() {
        viewMain.updateDownloadBar();
    }

    public MovieDownload getMovieDownloadById(int mvId) {
        return dataBaseHelper.getMovieDownload(mvId);
    }

    /**
     * get pending and downloading movies count
     *
     * @return
     */
    public int getMovieDownloadCount() {
        int pendingCount = 0;
        int completeCount = 0;
        List<MovieDownload> downloadList = DataBaseHelper.getInstance().getAllMovieDownload();
        if (downloadList != null) {
            for (MovieDownload movieDownload : downloadList) {
                switch (movieDownload.getTypeDownload()) {
                    case 2:
                        completeCount++;
                        break;
                    case 3:
                        List<DownloadResult> results = ogleApplication.getMovieDownloadResultByIds(movieDownload.getIdDownload());
                        for (DownloadResult result : results) {
                            switch (result.getStatus()) {
                                case DownloadManager.STATUS_SUCCESSFUL:
                                case DownloadManager.STATUS_FAILED:
                                    completeCount++;
                                    break;
                            }
                        }
                        break;
                }
            }
            pendingCount = downloadList.size() - completeCount;
        }
        return pendingCount;
    }

    public List<String> getAllStorageLocations() {//0: bo nho trong, 1 the nho
        List<String> results = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = ogleApplication.getExternalFilesDirs("");
            for (File file : externalDirs) {
                if (file != null) {
                    String path = file.getPath().split("/Android")[0];
                    if (!results.contains(path)) {
                        results.add(path);
                        Log.i("DownloadManager", "Storage Available : " + OgleHelper.formatAvailableSpace(path) + ", Path: " + path);
                    }
                }
            }
        }
        return results;
    }

    public String getAppStoragePath() {
        String path = "";
        List<String> listStorage = getAllStorageLocations();
        if (ogleApplication.isSDCard) {
            if (listStorage.size() > 1) {
                path = listStorage.get(1);
            } else {
                path = listStorage.get(0);
            }
        } else {
            path = listStorage.get(0);
        }
        return path;
    }

    public void updateTypeMovieDownload(int mvId, int typeDownload, int typeCode) {
        dataBaseHelper.updateTypeMovieDownload(mvId, typeDownload, typeCode);
        int downloadId = 0;
        String path = "";
        MovieDownload movieDownload = dataBaseHelper.getMovieDownload(mvId);
        if (movieDownload != null) {
            downloadId = movieDownload.getIdDownload();
            path = movieDownload.getPath();
        }
        switch (typeDownload) {
            case 0:
                viewMain.updateStatusDownload(mvId, downloadId, DownloadManager.STATUS_PENDING, path, typeCode);
                viewMain.updateErrorCodeAtDialogIsShow(mvId, typeDownload, -1);
            case 1:
                break;
            case 2:
                viewMain.updateStatusDownload(mvId, 0, DownloadManager.STATUS_FAILED, "", typeCode);
                viewMain.updateErrorCodeAtDialogIsShow(mvId, typeDownload, typeCode);
                break;
            case 3:
                break;
        }
        viewMain.updateDownloadBar();
    }

    /**
     * add movie to download queue
     *
     * @param movieInfo
     * @return
     */
    public long addDownLoadQueue(MovieInfo movieInfo) {
        long result = dataBaseHelper.addDownLoadQueue(movieInfo);
        viewMain.updateDownloadBar();
        return result;
    }

    /**
     * get download right
     *
     * @param movieDownload
     */
    public void getDownloadRight(final MovieDownload movieDownload) {
        APICall.getDownloadRight(movieDownload.getMvId(), 0, new APIResultCallBack<ResponDownloadRight>() {
            @Override
            public void onSuccess(ResponDownloadRight body, Response response) {
                Headers header = response.headers();
                String token = header.get("Set-Cookie");
                downloadMovieM3u8(movieDownload, token);

                //update free download crediets
                DownloadRight data = body.getData();
                int availableDownloads = data.getAvailableDownloads();
                SharedPreferences.Editor editor = mPref.edit();
                editor.putInt("availableDownloads", data.getAvailableDownloads());
                editor.commit();
            }

            @Override
            public void onError(int httpCode, ResponDownloadRight body, Throwable t) {
                int downloadType = 0;
                int typeCode = -1;
                int code = -1;
                List<WifiInfo> wifiInfos = null;
                if (body != null && body.getStatus() != null) {
                    code = body.getStatus().getCode();
                }

                if (code != -1 && code != 44) {
                    if (code == 42) { //router download overloaded, reset movie state to ready and change wifi
                        wifiInfos = body.getWifiList();  //better wifi list, app need change wifi
                    } else {
                        typeCode = 1;
                        downloadType = 2;
                    }
                }
                updateTypeMovieDownload(movieDownload.getMvId(), downloadType, typeCode);

                switch (code) {
                    case -1:  //api call fail
                    case 44:  //Network is not ready
                    case 42: //router download session overload, need switch wifi
                        if (Config.isAllowDownloadLocal) { //current fms error, exchange another fms to download
                            Config.changeFMS();
                        } else if (code == 42) {
                            boolean switchWifiSucc = false;
                            String ssid = "";
                            if (wifiInfos != null && !wifiInfos.isEmpty()) {
                                WifiInfo wifiInfo = wifiInfos.get(0);
                                ssid = wifiInfo.getSsid();
                                switchWifiSucc = wifiUtils.switchWifi(ssid, "");
                            }

                            if (!switchWifiSucc) {
                                if (!TextUtils.isEmpty(ssid)) {
                                    Log.d(TAG, "router download session overload, but switch wifi[" + ssid + "] fail.");
                                } else {
                                    Log.d(TAG, "router download session overload, but no wifi to switch.");
                                }
                            }
                        }
                        mPref.edit().putInt("accountExpired", 0).commit();
                        delayDownload();
                        break;
                    case 55: //account expire
                        mPref.edit().putInt("accountExpired", 1).commit();
                        viewMain.updateErrorCodeAtDialogIsShow(movieDownload.getMvId(), 2, 2);
                        checkExistMovieWaittingToDownLoad();
                        break;
                    default:
                        mPref.edit().putInt("accountExpired", 0).commit();
                        viewMain.updateErrorCodeAtDialogIsShow(movieDownload.getMvId(), 2, 1);
                        checkExistMovieWaittingToDownLoad();
                        break;
                }
            }
        });
    }

    private Handler handler = new Handler();
    private Runnable downloadTask = new Runnable() {
        @Override
        public void run() {
            checkExistMovieWaittingToDownLoad();
        }
    };

    private void delayDownload() {
        //check movie download right after 20 seconds
        handler.removeCallbacks(downloadTask);
        handler.postDelayed(downloadTask, 20000);
    }

    /**
     * get app path
     *
     * @param basePath storage base path
     * @param dirname
     * @return
     */
    private String getAppPath(String basePath, String dirname) {
        if (TextUtils.isEmpty(basePath)) {
            basePath = getAppStoragePath();
        }
        File[] dirFile = mContext.getExternalFilesDirs("");
        boolean flag = false;
        Uri uri = null;
        for (File file : dirFile) {
            if (file.getPath().startsWith(basePath)) {
                flag = true;
                uri = setDestinationFromBase(file, dirname);
                break;
            }
        }

        if (!flag)
            uri = setDestinationInExternalFilesDir(mContext, basePath, dirname);
        File f1 = new File(uri.toString().replace("file://", ""));
        if (!f1.exists()) {
            f1.mkdirs();
        }
        return f1.getAbsolutePath();
    }

    /**
     * download m3u8 file
     *
     * @param movieDownload
     * @param cookie
     */

    public void downloadMovieM3u8(final MovieDownload movieDownload, final String cookie) {
        final String fileName = movieDownload.getFileName();
        String urlM3U8 = fileName + "/" + fileName + ".m3u8";
        APICall.downFileM3U8(urlM3U8, cookie, new APIResultCallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody body) {
                String basePath = getAppStoragePath();
                String appDir = getAppPath(basePath, fileName);
                final File futureStudioIconFile = new File(appDir, fileName + ".m3u8");
                Log.d("DownloadManager", "m3u8 file path: " + futureStudioIconFile.getAbsolutePath());
                if (writeResponBodyToDisk(futureStudioIconFile, body)) {
                    new Thread() {
                        @Override
                        public void run() {
                            boolean isSuscess = DrmSdk.FetchKey(mContext, futureStudioIconFile.getAbsolutePath(), ogleApplication.getFMAToken());
                            Log.i("FetchKey", "FetchKey = " + isSuscess + " - path = " + futureStudioIconFile.getAbsolutePath() + ", version: " + DrmSdk.VERSION);
                        }
                    }.start();

                    downMovieTs(basePath, movieDownload, cookie);
                } else {
                    updateTypeMovieDownload(movieDownload.getMvId(), 2, 3);
                    //check database exist movie is waiting download
                    checkExistMovieWaittingToDownLoad();
                }
            }

            @Override
            public void onError(int httpCode, ResponseBody body, Throwable t) {
                int downloadType = 2;
                int typeCode = 5;
                if (httpCode == 403 || httpCode == 503 || httpCode == -1) {
                    downloadType = 0;
                    typeCode = -1;
                    Config.changeFMS();
                } else if (httpCode == 404) {
                    typeCode = 404;
                }

                updateTypeMovieDownload(movieDownload.getMvId(), downloadType, typeCode);
                if (downloadType == 0) {
                    delayDownload();
                } else {
                    checkExistMovieWaittingToDownLoad();
                }
            }
        });
    }

    /**
     * download ts file
     *
     * @param path
     * @param movieDownload
     * @param cookie
     */
    private void downMovieTs(String path, final MovieDownload movieDownload, final String cookie) {
        if (movieDownload != null && movieDownload.getTypeDownload() == 3) {
            checkExistMovieWaittingToDownLoad();
        } else {
            String fileName = movieDownload.getFileName();
            String filePath = fileName + "/" + fileName + ".ts";
            File[] dirFile = mContext.getExternalFilesDirs("");
            boolean flag = false;
            Uri uri = null;
            for (File file : dirFile) {
                if (file.getPath().startsWith(path)) {
                    flag = true;
                    uri = setDestinationFromBase(file, filePath);
                    break;
                }
            }
            if (!flag)
                uri = setDestinationInExternalFilesDir(mContext, path, fileName);

            Uri uri1 = Uri.parse(Config.getPublicDownLoadM3u8Url() + filePath);
            Log.i("PresenterMain", "LinkDown: " + uri1);
            DownloadManager.Request request = new DownloadManager.Request(uri1);
            request.addRequestHeader("User-Agent", "OGLE-APP/Andriod");
            request.addRequestHeader("Authentication", mPref.getString("stringToken", ""));
            request.addRequestHeader("Cookie", cookie);
            request.setDescription(movieDownload.getMvId());
            request.setTitle(movieDownload.getMvName());
            request.setDestinationUri(uri);

            long idDownload = movieDownload.getIdDownload();
            if (idDownload > 0) { //resume download
                try {
                    downloadManager.restartDownload(idDownload, request);
                } catch (Exception e) {
                }
            } else { //renew a download request
                idDownload = downloadManager.enqueue(request);
            }
            dataBaseHelper.updateTypeMovieDownloadAndIDPath(movieDownload.getMvId(),
                    (int) idDownload, 3, path);
        }
    }

    private boolean writeResponBodyToDisk(File futureStudioIconFile, ResponseBody body) {
        try {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownload = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownload += read;
                    Log.d("Future Studio", "file download" + fileSizeDownload + " of " + fileSize);
                }
                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                body.close();
            }

        } catch (IOException e) {
            return false;
        }

    }

    public Uri setDestinationInExternalFilesDir(Context context, String dirType, String subPath) {
        return setDestinationFromBase(context.getExternalFilesDir(dirType), subPath);
    }

    private Uri setDestinationFromBase(File base, String subPath) {
        if (subPath == null) {
            throw new NullPointerException("subPath cannot be null");
        }
        return Uri.withAppendedPath(Uri.fromFile(base), subPath);
    }

    public void registerReceiver() {
        wifiManager.startScan();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(Constants.ACTION_UPDATE);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        ogleApplication.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void unregisterReceiver() {
        ogleApplication.unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Log.i("DownloadManager", "ACTION_DOWNLOAD_COMPLETE");
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                List<DownloadResult> results = ogleApplication.getMovieDownloadResultByIds(downloadId);
                boolean isDelay = false;
                if (!results.isEmpty()) {
                    DownloadResult downloadResult = results.get(0);
                    int status = downloadResult.getStatus();
                    int mvId = downloadResult.getMvId();
                    String name = downloadResult.getName();
                    int reason = downloadResult.getReason();
                    String path = downloadResult.getPath();
                    Log.d("DownloadManager", "download name: " + name + ", status: " + status + ", reason: " + reason);
                    boolean showError = true;
                    if (status == DownloadManager.STATUS_FAILED) {
                        if (reason == 403 || reason == DownloadManager.ERROR_UNKNOWN
                                || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
                            showError = false;
                            checkDownloadManagerFail(mvId);
                        } else if (reason == DownloadManager.ERROR_FILE_ERROR) { //sdcard unmounted, then try download in internal storage
                            showError = false;
                            isDelay = true;
                            checkDownloadManagerFail(mvId);
                        }
                    } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        ogleApplication.showToast(String.format(mContext.getResources().getString(R.string.download_complete), name), Gravity.CENTER);
                        intent = new Intent();
                        intent.setAction(DownloadManager.ACTION_DOWNLOAD_SUCCESS);
                        intent.putExtra("path", path);
                        intent.putExtra("movieId", mvId);
                        mContext.sendBroadcast(intent);
                        ogleApplication.updateDownloadRentalTime(mvId, false);
                        viewMain.updateStatusDownload(mvId, downloadId, status, path, reason);
                    }

                    if (showError) {
                        viewMain.updateStatusDownload(mvId, downloadId, status, path, reason);
                    }
                }

                if (Config.isAllowDownload) {
                    if (isDelay) {
                        delayDownload();
                    } else {
                        checkExistMovieWaittingToDownLoad();
                    }
                }
            } else if (action.equals(Constants.ACTION_UPDATE)) {
                long downloadId = intent.getLongExtra("downloadid", -1L);
                int mTotalTotal = intent.getIntExtra("mTotalTotal", 0);
                int mTotalCurrent = intent.getIntExtra("mTotalCurrent", 0);
                double mSpeed = intent.getDoubleExtra("mSpeed", 0);
                int movieid = intent.getIntExtra("movieid", 0);
                if (movieid > 0) {
                    viewMain.updateProgressDownload(movieid, downloadId, mTotalTotal, mTotalCurrent, mSpeed);
                }
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.i("DownloadMovie", "CONNECTIVITY_ACTION - PresenterMain");
                checkWifiState(context, true);
            }
        }
    };

    private Runnable hideNetWorkTask = new Runnable() {
        @Override
        public void run() {
            viewMain.setChangeTextNotification(2);
        }
    };

    /**
     * reload token and movie resources
     */
    private Runnable reloadTask = new Runnable() {
        @Override
        public void run() {
            refreshToken(new CallBack() {
                @Override
                public void call() {
                    checkWifiState(mContext, false);
                }
            });
        }
    };

    public void checkConnection(Context context, boolean checkWifiState) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || checkWifiState) {
            checkWifiState(context, false);
        }
    }

    /**
     * check wifi state
     *
     * @param context
     */
    private void checkWifiState(Context context, boolean changeWifi) {
        Config.isAllowDownloadLocal = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkConnected = info == null ? false : info.isConnected();
        Log.d("checkNetWork", "changeWifi: " + changeWifi + ", isNetworkConnected: " + isNetworkConnected);

        handler.removeCallbacks(hideNetWorkTask);
        if (Config.isOgleWifi(mContext, info, DataBaseHelper.getInstance().getWifiInfoList())) {
            Config.isAllowDownload = true;
            Log.i("checkNetWork", "NetWork: On");

            if (!changeWifi) {
                viewMain.showToasApp(mContext.getResources().getString(R.string.msgconnectogle), Gravity.CENTER);
                long savedMilli = mPref.getLong("systemMilli", 0L);
                long diff = System.currentTimeMillis() - savedMilli;
                int hours = (int) (diff / (1000 * 60 * 60));
                if ( dataLoader != null) {
                    dataLoader.getMovieList(true);
                    mPref.edit().putLong("systemMilli", System.currentTimeMillis()).commit();
                }
            }
            dataBaseHelper.updateTypeNetworkDownload(0, 1);
            viewMain.updateSttAtChangeNetWork(0);
            viewMain.setChangeTextNotification(0);
            checkExistMovieWaittingToDownLoad();
            handler.postDelayed(hideNetWorkTask, 10000);
        } else {
            Config.isAllowDownload = false;
            viewMain.updateSttAtChangeNetWork(1);
            viewMain.setChangeTextNotification(1);

            //check location whether enabled
            if (!viewMain.checkLocationEnabled()) {
                return;
            }
        }

        if (changeWifi && isNetworkConnected) {
            if (!firstCheckWifi) {
                //wating for network prepare
                handler.removeCallbacks(reloadTask);
                handler.postDelayed(reloadTask, 3000);
            }
            firstCheckWifi = false;
        }
    }

    /**
     * check whether connect fma wifi
     */
    private void checkConnectFMAWifi() {
        APICall.checkFMA(new APIResultCallBack<Status>() {
            @Override
            public void onSuccess(Status body) {
                Config.isAllowDownload = true;
                viewMain.updateSttAtChangeNetWork(0);
                viewMain.setChangeTextNotification(0);

                Intent intent = new Intent();
                intent.setAction("com.flixsys.soflix.reconnect");
                mContext.sendBroadcast(intent);
                checkExistMovieWaittingToDownLoad();
            }
        });
    }

    /**
     * update movie download status
     *
     * @param mvId
     * @param status
     * @param path
     * @param reason
     */
    public void updateMovieDownloadStatus(int mvId, int status, String path, int reason) {
        DataBaseHelper.getInstance().updateStatusDownload(mvId, status, path, reason);
    }

    /**
     * check account whether recharge the vip plan and the account has not expired
     *
     * @return
     */
    public boolean checkMoviePermission(final int mvId) {
        MovieInfo movieInfo = getMovieInfoByDB(mvId);
        if (movieInfo != null && !movieInfo.isFree()) {
            MovieBilling movieBilling = ogleApplication.getMovieBillingInfo(movieInfo.getMovieId());
            if (movieBilling == null || movieBilling.hasExpired()) {
                showMovieBillingDialog(movieInfo, null);
                return false;
            }
        }
        return true;
    }

    /**
     * online playing movie
     *
     * @param movieInfo
     */
    public void playMovie(final MovieInfo movieInfo, final DialogCallBack callBack) {
        //check plan whether expired, but free movie can free download/playing
        final MovieDownload movieDownload = dataBaseHelper.getMovieDownload(movieInfo.getMovieId());
        long timeContinue = 0;
        if (movieDownload != null && movieDownload.getTypeDownload() == 3) {
            timeContinue = movieDownload.getTimeContinue();
        }

        if (!ogleApplication.isDownloadSuccess(movieDownload)) {
            if (Config.isCheckMac()) {
                final long pauseTime = timeContinue;
                //check whether has playing movie right
                APICall.getDownloadRight(movieInfo.getMovieId(), 1, new APIResultCallBack<ResponDownloadRight>() {
                    @Override
                    public void onSuccess(ResponDownloadRight body, Response response) {
                        String cookie = response.headers().get("Set-Cookie");
                        //online playing movie
                        onlinePlayMovie(movieInfo, response.headers().get("Set-Cookie"), pauseTime, callBack);
                    }

                    @Override
                    public void onError(int httpCode, ResponDownloadRight body, Throwable t) {
                        String msg = mContext.getResources().getString(R.string.error_loginfail1);
                        if (body != null && body.getStatus() != null) {
                            int code = body.getStatus().getCode();
                            switch (code) {
                                case 55: //account expired
                                    mPref.edit().putInt("accountExpired", 1).commit();
                                    showRenewDialog(null);
                                    msg = "";
                                    break;
                                default:
                                    mPref.edit().putInt("accountExpired", 0).commit();
                                    msg = body.getStatus().getMessage();
                                    if (code == 42) { //over max playing sessions
                                        msg = "Exceed max playing sessions, please waiting a moment and try again.";
                                    }
                                    break;
                            }
                        }

                        if (!TextUtils.isEmpty(msg)) {
                            OgleHelper.showMessage(mContext, msg, null);
                        }
                    }
                });
            } else {
                if (movieDownload != null) {
                    String path = movieDownload.getPath();
                    if (!TextUtils.isEmpty(path)) {
                        path = path.replace("file://", "");
                        File file = new File(path);
                        if (file.exists() && file.length() > 0) {
                            //offline playing downloaded movie
                            clickToPlayMovie(movieDownload);
                            return;
                        }
                    }
                }
                //please connect traxi wifi
                OgleHelper.showMessage(mContext, mContext.getResources().getString(R.string.playingdialognotmac), null);
            }
        } else {
            //offline playing downloaded movie
            clickToPlayMovie(movieDownload);
        }
    }

    /**
     * process movie download
     *
     * @param mvId
     */
    public void processMovie(final int mvId) {
        final MovieInfo movieInfo = getMovieInfoByDB(mvId);
        final MovieDownload movieDownload = dataBaseHelper.getMovieDownload(mvId);
        if (movieDownload != null) { //has add to download list
            if (movieDownload.getTypeDownload() == 2) {
                retryDownload(movieDownload);
            } else if (movieDownload.getTypeDownload() == 3) {
                //movie download and added to downloadmanager;
                List<DownloadResult> results = ogleApplication.getMovieDownloadResultByIds(movieDownload.getIdDownload());
                if (!results.isEmpty()) {
                    switch (results.get(0).getStatus()) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            if (movieDownload.getDownloadSize() > 0) {
                                clickToPlayMovie(movieDownload);
                            } else {
                                retryDownload(movieDownload);
                            }
                            break;
                        case DownloadManager.STATUS_FAILED:
                            retryDownload(movieDownload);
                            break;
                        case DownloadManager.STATUS_PAUSED:
                        case DownloadManager.STATUS_RUNNING:
                            playMovie(movieInfo, null);
                            break;
                    }
                }
            } else {
                showMsg(mContext.getResources().getString(R.string.msgaddmovietolistdown));
            }
        } else {
            if (movieInfo == null) {
                ogleApplication.showToast(mContext.getResources().getString(R.string.movie_404), Gravity.CENTER);
            } else if (isMovieAllowedDownload(movieInfo.getMovieId(), movieInfo.getFileSize())
                    && checkMoviePermission(mvId)
                    && checkFreeMovieDownloadLimit(movieInfo)) { //check movie whether allow to download
                processMovieForFree(movieInfo);
            }
        }
    }

    /**
     * check free movie download limitation
     *
     * @param movieInfo
     * @return
     */
    private boolean checkFreeMovieDownloadLimit(MovieInfo movieInfo) {
        if (movieInfo.isFreeNotVip() && !movieInfo.isShortVideo()) {
            int availableDownloads = mPref.getInt("availableDownloads", 0);
            int freePendingMovies = 0;
            List<MovieDownload> pendingDownloads = getDownloadsByType(MovieDownload.PENDING);
            for (MovieDownload movieDownload : pendingDownloads) {
                if (movieDownload.getTypeMovie() == 1) { //free movie
                    MovieInfo movie = getMovieInfo(movieDownload.getMvId());
                    if (movie != null && !movie.isShortVideo()) {
                        freePendingMovies++;
                    }
                }
            }
            if (availableDownloads <= freePendingMovies) {
                String msg = "Your free movie download credits has been used up, please top up your account or wait for the next cycle";
                OgleHelper.showMessage(mContext, msg, null);
                return false;
            }
        }
        return true;
    }

    /**
     * check movie whether allow to download,  check max downloads and check avaliable space
     *
     * @return
     */
    private boolean isMovieAllowedDownload(int mvId, int mvSize) {
        if (checkMaxDownloads() && checkSpaceAllowedDownload(getAppStoragePath(), mvId, mvSize)) {
            return true;
        }
        return false;
    }

    /**
     * show msg whether onlie play or not.
     *
     * @param msg
     */
    private void showMsg(String msg) {
        boolean onlinePlay = mPref.getBoolean("onlinePlay", false);
        if (onlinePlay) {
            ogleApplication.showToast(msg, Gravity.CENTER);
        } else {
            OgleHelper.showMessage(mContext, msg, null);
        }
    }

    /**
     * retry download
     *
     * @param data
     */
    private void retryDownload(final MovieDownload data) {
        if (isMovieAllowedDownload(data.getMvId(), data.getFileSize())) {
            String msg = "";
            if (data.getTypeCodeRespon() == 0 || data.getTypeCodeRespon() == 1
                    || data.getTypeCodeRespon() == 2) {
                msg = mContext.getResources().getString(R.string.faildownloadm3u81);
            } else if (data.getTypeCodeRespon() == 6) {
                msg = mContext.getResources().getString(R.string.errorbilling);
            } else {
                msg = mContext.getResources().getString(R.string.faildownloadm3u81);
            }
            OgleHelper.showDialog(mContext, msg,
                    "Retry", "Cancel", new DialogCallBack() {
                        @Override
                        public void ok() {
                            updateTypeMovieDownload(data.getMvId(), 0, -1);
                            checkExistMovieWaittingToDownLoad();
                        }

                        @Override
                        public void cancel() {
                        }
                    });
        }
    }

    /**
     * check max downloads
     *
     * @return
     */
    private boolean checkMaxDownloads() {
        int max_downloads = mPref.getInt("max_downloads", 0);
        List<MovieDownload> downloads = getDownloadsByType(MovieDownload.PENDING,
                MovieDownload.DOWNLOADING,
                MovieDownload.SUCCESS);
        Log.d(TAG, "checkMaxDownloads max_downloads: " + max_downloads +
                ", downloads.size: " + downloads.size());
        if (max_downloads > 0 && max_downloads <= downloads.size()) {
            String msg = OgleHelper.formatMsg(mContext.getResources().getString(R.string.exceed_max_downloads),
                    String.valueOf(max_downloads));
            showMsg(msg);
            return false;
        }
        return true;
    }

    /**
     * check avaliable disk space allowed download movie
     *
     * @param path
     * @param mvId
     * @param mvSize
     * @return
     */
    private boolean checkSpaceAllowedDownload(String path, int mvId, int mvSize) {
        long download_space = mvSize * 1L * 1024 * 1024;
        List<MovieDownload> downloads = getDownloadsByType(MovieDownload.PENDING, MovieDownload.DOWNLOADING);
        if (downloads != null && !downloads.isEmpty()) {
            for (MovieDownload download : downloads) {
                if (download.getMvId() != mvId) {
                    int downloadSize = download.getDownloadSize();
                    download_space += (download.getFileSize() * 1L * 1024 * 1024 - downloadSize);
                }
            }
        }
        long availableSpace = OgleHelper.getAvailableSpace(path);
        Log.d(TAG, "checkSpace availableSpace: " + availableSpace + ", download_space: " + download_space);
        if ((availableSpace - 100L * 1024 * 1024) > download_space) {
            return true;
        } else {
            showMsg(mContext.getResources().getString(R.string.errinsufficient));
        }
        return false;
    }

    /**
     * get all download success, pending, running and paused movies
     *
     * @param types 1: get all pending movies  2: get all downloading movies  3: get all failed movies  4: get success movies
     * @return
     */
    private List<MovieDownload> getDownloadsByType(int... types) {
        List<Integer> typeList = new ArrayList<>();
        for (int type : types) {
            typeList.add(type);
        }
        List<MovieDownload> movieDownloads = new ArrayList<>();
        List<MovieDownload> downloads = dataBaseHelper.getAllMovieDownload();
        if (downloads != null && !downloads.isEmpty()) {
            for (MovieDownload download : downloads) {
                switch (download.getTypeDownload()) {
                    case 0:
                    case 1:
                        if (typeList.contains(MovieDownload.PENDING)) {
                            movieDownloads.add(download);
                        }
                        break;
                    case 2:
                        if (typeList.contains(MovieDownload.FAIL)) {
                            movieDownloads.add(download);
                        }
                        break;
                    case 3:
                        List<DownloadResult> results =
                                ogleApplication.getMovieDownloadResultByIds(download.getIdDownload());
                        if (!results.isEmpty()) {
                            DownloadResult downloadResult = results.get(0);
                            download.setPath(downloadResult.getPath());
                            switch (downloadResult.getStatus()) {
                                case DownloadManager.STATUS_PAUSED:
                                case DownloadManager.STATUS_PENDING:
                                    if (typeList.contains(MovieDownload.PENDING)) {
                                        movieDownloads.add(download);
                                    }
                                    break;
                                case DownloadManager.STATUS_RUNNING:
                                    if (typeList.contains(MovieDownload.DOWNLOADING)) {
                                        movieDownloads.add(download);
                                    }
                                    break;
                                case DownloadManager.STATUS_SUCCESSFUL:
                                    if (typeList.contains(MovieDownload.SUCCESS)) {
                                        movieDownloads.add(download);
                                    }
                                    break;
                                case DownloadManager.STATUS_FAILED:
                                    if (typeList.contains(MovieDownload.FAIL)) {
                                        movieDownloads.add(download);
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        }
        return movieDownloads;
    }

    /**
     * show vip dialog
     *
     * @param movieInfo
     */
    private void showMovieVIPDialog(MovieInfo movieInfo) {
        CustomDialog_MovieVIP dialog = new CustomDialog_MovieVIP(mContext, movieInfo, viewMain, this);
        dialog.show();
    }

    /**
     * show billing dialog
     *
     * @param movieInfo
     * @param callBack
     */
    private void showMovieBillingDialog(final MovieInfo movieInfo, final DialogCallBack callBack) {
        if (movieInfo != null) {
            Quality defaultQuality = movieInfo.getQualityList().get(0);
            String msg = mContext.getResources().getString(R.string.textchargemovie)
                    .replace("xxx", String.valueOf(defaultQuality.getPrice()))
                    .replace("XXX", String.valueOf(defaultQuality.getRentalPeriod()));
            OgleHelper.showDialog(mContext, msg, new DialogCallBack() {
                @Override
                public void ok() {
                    billing(movieInfo, null, callBack);
                }

                @Override
                public void cancel() {
                }
            });
        }
    }

    /**
     * do billing
     *
     * @param movieInfo
     * @param dialog
     * @param callBack
     */
    public void billing(final MovieInfo movieInfo, final Dialog dialog, final DialogCallBack callBack) {
        APICall.sendBillingStart(movieInfo.getMovieId(), new APIResultCallBack<ResponBilling>() {
            @Override
            public void before() {
                viewMain.showLoading();
            }

            @Override
            public void onSuccess(ResponBilling body, Response response) {
                if (callBack != null) callBack.ok();
                Date serverDate = new Date(response.headers().get("Date"));
                BillingStart billData = body.getData();
                if (billData != null) {
                    movieInfo.setExpireOn(billData.getExpiryTimes());
                    long currTime = serverDate.getTime();
                    if (OgleHelper.checkSystemTimeNormal(serverDate.getTime())) {
                        currTime = System.currentTimeMillis();
                    }
                    long rentalStart = SystemClock.elapsedRealtime();
                    long rentalEnd = rentalStart + (movieInfo.getExpireOn() - currTime);
                    movieInfo.setRentalStart(rentalStart);
                    movieInfo.setRentalEnd(rentalEnd);
                    if (movieMap != null) {
                        movieMap.put(movieInfo.getMovieId(), movieInfo);
                    }
                    //update movie rental expired time
                    dataBaseHelper.updateBillingExpireTime(movieInfo.getMovieId(),
                            movieInfo.getExpireOn(), rentalStart, rentalEnd);

                    //save movie billing info
                    ogleApplication.billingMovie(movieInfo, billData);

                    MovieDownload movieDownload = dataBaseHelper.getMovieDownload(movieInfo.getMovieId());
                    if (movieDownload != null) {
                        getDownloadRight(movieDownload);
                    } else {
                        processMovie(movieInfo.getMovieId());
                    }
                } else {
                    Log.d("PresenterMainImpl", "current billing movie id:" + movieInfo.getMovieId() +
                            ", type: " + movieInfo.getType() + ", billing data is null");
                }
            }

            @Override
            public void onError(int httpCode, ResponBilling response, Throwable t) {
                String msg = mContext.getResources().getString(R.string.error_loginfail1);
                int code = -1;
                if (response != null) {
                    Status status = response.getStatus();
                    if (status != null) {
                        code = status.getCode();
                        msg = status.getMessage();
                    }
                }

                if (code == 55) {
                    showRenewDialog(null);
                } else {
                    msg = TextUtils.isEmpty(msg) ? mContext.getResources().getString(R.string.error_loginfail1) : msg;
                    OgleHelper.showMessage(mContext, msg, null);
                    MovieDownload movieDownload = dataBaseHelper.getMovieDownload(movieInfo.getMovieId());
                    if (movieDownload != null) {
                        updateTypeMovieDownload(movieInfo.getMovieId(), 2, 10);
                    }
                }
            }

            @Override
            public void after() {
                viewMain.hideLoading();
                if (dialog != null) dialog.dismiss();
            }
        });
    }


    private void processMovieForFree(MovieInfo movieInfo) {
        boolean onlinePlay = mPref.getBoolean("onlinePlay", false);
        if (onlinePlay) {
            Intent intent = new Intent();
            intent.putExtra("movieId", movieInfo.getMovieId());
            intent.setAction(DownloadManager.ACTION_DOWNLOAD_START);
            mContext.sendBroadcast(intent);
        } else {
            String msg = mContext.getResources().getString(R.string.msgaddmovietolistdown);
            if (Config.isCheckMac()) {
                msg = mContext.getResources().getString(R.string.msgwhennotmac);
            }

            OgleHelper.showMessage(mContext, mContext.getResources().getString(R.string.msgaddmovietolistdown), new DialogCallBack() {
                @Override
                public void ok() {
                    viewMain.hideDialogMoviePremiumDetail();
                }

                @Override
                public void cancel() {
                }
            });
        }

        viewMain.goneIconAndStt(movieInfo.getMovieId(), MovieDownload.PENDING, OgleHelper.showDownloadPendingMsg(mContext));
        processDownload(movieInfo);
    }


    private void processDownload(MovieInfo movieInfo) {
        Quality defaultQuality = movieInfo.getDefaultQuality();
        MovieDownload movieDownload = dataBaseHelper.getMovieDownload(movieInfo.getMovieId());
        if (movieDownload == null) {
            //Has list movie waitting; add movie to database waitting
            Log.i("DownloadMovie", "Add movie to database status waitting download");
            addDownLoadQueue(movieInfo);
        }
        checkExistMovieWaittingToDownLoad();
    }

    /**
     * download movie
     *
     * @param movieDownload
     */
    private void downloadMovie(MovieDownload movieDownload) {
        if (Config.isCheckMac()) {
            //call download right get token
            getDownloadRight(movieDownload);
        }
    }

    /**
     * get movie's download results
     *
     * @param downloadList
     * @return
     */
    public List<DownloadResult> getMovieDownloadResults(List<MovieDownload> downloadList) {
        List<DownloadResult> results = new ArrayList<>();
        if (!downloadList.isEmpty()) {
            List<Long> idList = new ArrayList<>();
            for (MovieDownload movieDownload : downloadList) {
                int down_id = movieDownload.getIdDownload();
                if (down_id > 0) {
                    idList.add((long) down_id);
                }
            }

            if (!idList.isEmpty()) {
                long[] ids = new long[idList.size()];
                for (int i = 0; i < idList.size(); i++) {
                    ids[i] = idList.get(i);
                }
                results = ogleApplication.getMovieDownloadResultByIds(ids);
            }
        }
        return results;
    }

    /**
     * check whether has movie is downloading
     *
     * @return
     */
    private boolean hasDownloadMovieInQueue() {
        DownloadResult result = null;
        List<DownloadResult> results = getMovieDownloadResults(dataBaseHelper.getAllMovieDownload());
        for (DownloadResult downloadResult : results) {
            switch (downloadResult.getStatus()) {
                case DownloadManager.STATUS_RUNNING:
                case DownloadManager.STATUS_PENDING:
                    result = downloadResult;
                    break;
                case DownloadManager.STATUS_PAUSED:
                    try {
                        downloadManager.resumeDownload(downloadResult.getDownloadId());
                        result = downloadResult;
                    } catch (Exception e) {
                    }
                    break;
            }
        }
        if (result != null) {
            Log.d("DownloadMovie", "hasDownloadMovieInQueue movie: " + result.getName() + ", status: " + result.getStatus());
            return true;
        }
        return false;
    }


    public void checkExistMovieWaittingToDownLoad() {
        MovieDownload movieDownload = dataBaseHelper.getMovieIsWaitingDownload();
        boolean hasDownload = hasDownloadMovieInQueue();
        Log.i("DownloadMovie", "Check exist movie waitting download - " + movieDownload);
        if (movieDownload != null && !hasDownload) {
            Log.i("DownloadMovieType", "movie id: " + movieDownload.getMvId() +
                    " movie type: " + movieDownload.getTypeDownload());
            //Has movie waitting to download; download
            downloadMovie(movieDownload);
        }
    }

    public String getCalculatedDate(int mid) {
        long expri = 0;
        MovieDownload movieDownload = dataBaseHelper.getMovieDownload(mid);
        if (movieDownload != null) {
            expri = movieDownload.getExpireTime();
            Log.i("Rentalperiod", "Movie Name: " + movieDownload.getMvName() +
                    " Movie: " + mid + " Rentalperiod: " + expri);
        }
        return OgleHelper.getFormatDate(expri);
    }


    public void checkDownloadManagerFail(int mvId) {
        MovieDownload movieDownload = dataBaseHelper.getMovieDownload(mvId);
        if (movieDownload.getTypeDownload() == 3) {
            updateTypeMovieDownload(mvId, 0, -1);
        }
    }

    public void backUpDataOgle(ItemBackupData itemData) {

        final ReportData reportData = new ReportData(ogleApplication.getAccountLogin().getUserId(), ogleApplication.appname,
                ogleApplication.appversion, ogleApplication.manufacturer, "adr", ogleApplication.osVersion,
                ogleApplication.getDevice(), itemData);

        final DataUser dataUser = new DataUser();
        dataUser.setReportData(reportData);

        RetrofitRepositoryFactory.bacUpData(ogleApplication.getToken(), ogleApplication.getFMAToken(), dataUser)
                .enqueue(new Callback<ResponseWrapper<ResponReportUser>>() {
                    @Override
                    public void onResponse(Call<ResponseWrapper<ResponReportUser>> call,
                                           Response<ResponseWrapper<ResponReportUser>> response) {
                        if (response.code() == 200) {
                            if (response.body().body.getStatus().getCode() == 0) {
                                Log.i("ResponUserData", "Respon User Data: " +
                                        response.body().body.getStatus().getCode());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseWrapper<ResponReportUser>> call, Throwable t) {

                    }
                });
    }

    private Map<Integer, MovieInfo> listMovieInfo1 = new HashMap<>();
    private Map<Integer, MovieInfo> listHotMovieInfo1 = new HashMap<>();
    private Map<Integer, MovieInfo> listNewRepleaseInfo1 = new HashMap<>();
    private List<WifiInfo> listWifiInfo = new ArrayList<>();
    private final String SECURITY_WEP = "WEP";
    private final String SECURITY_PSK = "PSK";
    private final String SECURITY_NONE = "NONE";

    String getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("WPA")) {
            return SECURITY_PSK;
        }
        return SECURITY_NONE;
    }

    public void connect(String typeSecurity, String password, String ssidConnect) {
//        Integer haveConfig = findNetworkInExistingConfig(wifiManager, ssidConnect);
//        if (haveConfig != null) {
//            connectWifiHaveConfig(wifiManager, ssidConnect);
//        } else {
        if (typeSecurity.equals(SECURITY_PSK)) {
            changeNetworkWPA(wifiManager, ssidConnect, password);
        } else if (typeSecurity.equals(SECURITY_WEP)) {
            changeNetworkWEP(wifiManager, ssidConnect, password);
        } else {
            changeNetworkUnEncrypted(wifiManager, ssidConnect);
        }
//        }
    }

    private void connectWifiHaveConfig(WifiManager wifiManager, String ssid) {
        int netId = -1;
        for (WifiConfiguration tmp : wifiManager.getConfiguredNetworks())
            if (tmp.SSID.replace("\"", "").equals(ssid.replace("\"", ""))) {
                netId = tmp.networkId;
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();
                break;
            }
    }

    // Adding a WPA or WPA2 network
    private void changeNetworkWPA(WifiManager wifiManager, String ssid, String password) {
        WifiConfiguration config = changeNetworkCommon(ssid);
        // Hex passwords that are 64 bits long are not to be quoted.
        config.preSharedKey = quoteNonHex(password, 64);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // For WPA
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // For WPA2
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        updateNetwork(wifiManager, config);
    }

    private void changeNetworkWEP(WifiManager wifiManager, String ssid, String password) {
        WifiConfiguration config = changeNetworkCommon(ssid);
        config.wepKeys[0] = quoteNonHex(password, 10, 26, 58);
        config.wepTxKeyIndex = 0;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        updateNetwork(wifiManager, config);
    }

    private WifiConfiguration changeNetworkCommon(String ssid) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        // Android API insists that an ascii SSID must be quoted to be correctly
        // handled.
        config.SSID = quoteNonHex(ssid);
        return config;
    }

    private static final String TAG = "WIFI";

    // Adding an open, unsecured network
    private void changeNetworkUnEncrypted(WifiManager wifiManager, String ssid) {
        WifiConfiguration config = changeNetworkCommon(ssid);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        updateNetwork(wifiManager, config);
    }

    private void updateNetwork(WifiManager wifiManager, WifiConfiguration config) {
        Integer foundNetworkID = findNetworkInExistingConfig(wifiManager, config.SSID);
        Log.i("findNetwork", "existingConfigs: " + foundNetworkID);
        if (foundNetworkID != null) {
            Log.i(TAG, "Removing old configuration for network " + config.SSID);
            wifiManager.removeNetwork(foundNetworkID);
            wifiManager.saveConfiguration();
        }

        int networkId = wifiManager.addNetwork(config);
        if (networkId >= 0) {
            // Try to disable the current network and start a new one.
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            if (wifiManager.enableNetwork(networkId, true)) {
                Log.i(TAG, "Associating to network " + config.SSID);
                wifiManager.saveConfiguration();

            } else {
                Log.w(TAG, "Failed to enable network " + config.SSID);
            }
        } else {
            Log.w(TAG, "Unable to add network " + config.SSID);
        }
    }

    private Integer findNetworkInExistingConfig(WifiManager wifiManager, String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        Log.i("findNetwork", "existingConfigs: " + existingConfigs);
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID != null && existingConfig.SSID.replace("\"",
                        "").equals(ssid.replace("\"", ""))) {
                    return existingConfig.networkId;
                }
            }
        }
        return null;
    }

    public static String quoteNonHex(String value, int... allowedLengths) {
        return isHexOfLength(value, allowedLengths) ? value : convertToQuotedString(value);
    }

    public static String convertToQuotedString(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        // If already quoted, return as-is
        if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"') {
            return string;
        }
        return '\"' + string + '\"';
    }

    private static final Pattern HEX_DIGITS = Pattern.compile("[0-9A-Fa-f]+");

    public static boolean isHexOfLength(CharSequence value, int... allowedLengths) {
        if (value == null || !HEX_DIGITS.matcher(value).matches()) {
            return false;
        }
        if (allowedLengths.length == 0) {
            return true;
        }
        for (int length : allowedLengths) {
            if (value.length() == length) {
                return true;
            }
        }
        return false;
    }

    private String getSubnetMask() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        InetAddress inetAddress = null;
        try {
            inetAddress = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Log.i("LocalIP", "Local IP: " + getIPv4LocalNetMask(inetAddress, 24));
        return getIPv4LocalNetMask(inetAddress, 24);
    }

    private String localIP() {
        String ip = "";
        WifiManager wifiMan = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        android.net.wifi.WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        return ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff)
                , (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    private String getIPv4LocalNetMask(InetAddress ip, int netPrefix) {

        try {
            int shiftby = (1 << 31);
            for (int i = netPrefix - 1; i > 0; i--) {
                shiftby = (shiftby >> 1);
            }
            String maskString = Integer.toString((shiftby >> 24) & 255) + "." +
                    Integer.toString((shiftby >> 16) & 255) + "." +
                    Integer.toString((shiftby >> 8) & 255) + "." +
                    Integer.toString(shiftby & 255);
            return maskString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Something went wrong here...
        return null;
    }

    public void getFMS() {
        Log.i("LocalIP", "Local IP: " + localIP());
        APICall.getDetectFMS(localIP(), getSubnetMask(),
                new APIResultCallBack<ResponDetectFMS>() {
                    @Override
                    public void onSuccess(ResponDetectFMS response) {
                        if (response.getData().size() > 0) {
                            Config.isAllowDownloadLocal = true;
                            List<DetectFMS> fmsList = response.getData();
                            List<String> ips = new ArrayList<>();
                            for (DetectFMS detectFMS : fmsList) {
                                Log.i("DetectFMSIP", "Local FMS IP: " + detectFMS.getLocalIp());
                                ips.add(detectFMS.getLocalIp());
                            }
                            Config.FMSIPS = ips.toArray(new String[0]);
                            Config.SELECT_FMS_INDEX = 0;

                            viewMain.updateSttAtChangeNetWork(0);
                            viewMain.setChangeTextNotification(0);

                            Intent intent = new Intent();
                            intent.setAction("com.flixsys.soflix.reconnect");
                            mContext.sendBroadcast(intent);
                            checkExistMovieWaittingToDownLoad();
                        }
                    }
                });
    }

    /**
     * get download error by error code
     *
     * @param typeCodeResponse
     * @return
     */
    public String getDownloadErrorMsg(int typeCodeResponse) {
        String errTxt = "";
        switch (typeCodeResponse) {
            case 0:
            case 1:
            case 2:
                errTxt = mContext.getResources().getString(R.string.errorcode).replace("XXX", "600");
                break;
            case 3:
            case 4:
            case 5:
                errTxt = mContext.getResources().getString(R.string.errorcode).replace("XXX", "603");
                break;
            case 6:
                errTxt = mContext.getResources().getString(R.string.errorcode607).replace("XXX", "607");
                break;
            case 7:
            case 8:
                errTxt = mContext.getResources().getString(R.string.errorcode).replace("XXX", "608");
                break;
            case 9:
                errTxt = mContext.getResources().getString(R.string.errorcode).replace("XXX", "609");
                break;
            case 404:
                errTxt = mContext.getResources().getString(R.string.errorcode).replace("XXX", "404");
                break;
            default:
                errTxt = mContext.getResources().getString(R.string.errorcode).replace("XXX", String.valueOf(typeCodeResponse));
                break;
        }
        return errTxt;
    }

    /**
     * update movie download remain rental time
     */
    public void updateDownloadRemainRentalTime() {
        List<MovieDownload> mList = dataBaseHelper.getDownloadSuccMovieList();
        Log.d(TAG, "updateDownloadRemainRentalTime mList.size: " + mList.size());
        DownloadManager.Query query = new DownloadManager.Query();
        List<MovieDownload> updateList = new ArrayList<>();
        for (MovieDownload mvDownload : mList) {
            if (ogleApplication.isDownloadSuccess(mvDownload) && mvDownload.getExpireTime() > 0) {
                boolean isUpdate = adjustRemainRentalTime(mvDownload, mvDownload.getRentalStart(),
                        mvDownload.getRentalEnd(), mvDownload.getExpireTime());
                if (isUpdate) {
                    Log.d(TAG, "update download movie: " + mvDownload.getMvName() +
                            " rentalStart: " + mvDownload.getRentalStart() +
                            " rentalEnd: " + mvDownload.getRentalEnd());
                    updateList.add(mvDownload);
                }
            }
        }

        if (!updateList.isEmpty()) {
            dataBaseHelper.updateDownloadRemainRentalTime(updateList);
        }
    }

    /**
     * update movie remain rental time
     */
    public void updateMovieRemainRentalTime() {
        List<MovieInfo> updateList = new ArrayList<>();
        List<MovieInfo> movieList = dataBaseHelper.getAllPurchasedMovie();
        Log.d(TAG, "updateMovieRemainRentalTime movieList.size: " + movieList.size());
        for (MovieInfo movieInfo : movieList) {
            boolean isUpdate = adjustRemainRentalTime(movieInfo, movieInfo.getRentalStart(),
                    movieInfo.getRentalEnd(), movieInfo.getExpireOn());
            if (isUpdate) {
                Log.d(TAG, "update movie: " + movieInfo.getName() + " rentalStart: "
                        + movieInfo.getRentalStart() + " rentalEnd: " + movieInfo.getRentalEnd());
                updateList.add(movieInfo);
            }
            if (movieMap != null) {
                movieMap.put(movieInfo.getMovieId(), movieInfo);
            }
        }

        if (!updateList.isEmpty()) {
            dataBaseHelper.updateMovieRemainRentalTime(updateList);
        }
    }

    private <T> boolean adjustRemainRentalTime(T obj, long oldRentalStart, long oldRentalEnd, long expireTime) {
        boolean isUpdate = true;
        long rentalStart = oldRentalStart;
        long rentalEnd = oldRentalEnd;
        long systemTime = mPref.getLong("SystemTime", 0);
        long cpuTime = mPref.getLong("CpuTime", 0);
        Log.d(TAG, "systemTime: " + systemTime + ", cpuTime: " + cpuTime);
        if (systemTime > 0 && cpuTime > 0) {
            long currCpuTime = SystemClock.elapsedRealtime();
            long currTime = systemTime + currCpuTime - cpuTime;
            if (expireTime < currTime) {
                rentalStart = cpuTime;
                rentalEnd = cpuTime;
            } else {
                rentalStart = currCpuTime;
                rentalEnd = currCpuTime + (expireTime - currTime);
            }
        } else {
            long realTime = SystemClock.elapsedRealtime();
            long remainRentalTime = oldRentalEnd - oldRentalStart;
            if (realTime < oldRentalStart) { //phone has restarted
                rentalStart = realTime;
                rentalEnd = remainRentalTime > realTime ? remainRentalTime : realTime;
            } else {
                if (realTime >= oldRentalEnd) {
                    rentalStart = oldRentalEnd;
                } else {
                    rentalStart = realTime;
                }
            }
        }

        if (obj instanceof MovieDownload) {
            MovieDownload movieDownload = (MovieDownload) obj;
            movieDownload.setRentalStart(rentalStart);
            movieDownload.setRentalEnd(rentalEnd);
        } else if (obj instanceof MovieInfo) {
            MovieInfo movieInfo = (MovieInfo) obj;
            movieInfo.setRentalStart(rentalStart);
            movieInfo.setRentalEnd(rentalEnd);
        }
        return isUpdate;
    }

    /**
     * show renew plan dialog
     *
     * @param listener
     */
    public void showRenewDialog(final CustomDialog_Redeem.Listener listener) {
        String msg = mContext.getResources().getString(R.string.msg_vip_expired);
        showRedeemDialog(msg);
    }

    private void showRedeemDialog(String msg) {
        OgleHelper.showDialog(mContext, msg,
                "Top Up",
                "Cancel", new DialogCallBack() {
                    @Override
                    public void ok() {
                        CustomDialog_AccountBalance dialog
                                = new CustomDialog_AccountBalance(mContext, null,
                                PresenterMainImp.this);
                        dialog.show();
                    }

                    @Override
                    public void cancel() {
                    }
                });
    }

    public ViewMain getViewMain() {
        return viewMain;
    }

    /**
     * refresh Token
     */
    public void refreshToken(final CallBack callBack) {
        loginManager.openLogin(new LoginManager.CallBack() {
            @Override
            public void before() {
            }

            @Override
            public void success() {
                loadDataFromServer(callBack);
            }

            @Override
            public void fail(String numPhone, String msg) {
                if (callBack != null) callBack.call();
            }
        });
    }

    /**
     * load data from server
     *
     * @param callBack
     */
    private void loadDataFromServer(final CallBack callBack) {
        if (dataLoader == null) {
            dataLoader = new DataLoader(mContext, new DataLoader.Listener() {
                @Override
                public void success() {
                    Log.d("PresenterMain", "refresh Data");
                    refreshData();
                    viewMain.refreshData();
                    if (callBack != null) callBack.call();
                }

                @Override
                public void fail() {
                    if (callBack != null) callBack.call();
                }

                @Override
                public void expired() {
                    refreshToken(callBack);
                }
            });
        } else {
            dataLoader.load();
        }
    }

    /**
     * check fag server is ok
     */
    public void detectFAG(final int index) {
        final String server = Config.FAG_SERVERS[index];
        APICall.checkFAG(server, new APIResultCallBack<Status>() {
            @Override
            public void onSuccess(Status body) {
                Config.changeFMA(server);
            }

            @Override
            public void onError(int httpCode, Status body, Throwable t) {
                int count = index + 1;
                if (count < 2) {
                    detectFAG(count);
                }
            }
        });
    }
}
