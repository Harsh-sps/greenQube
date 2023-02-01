package com.aistream.greenqube.mvp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.Actor;
import com.aistream.greenqube.mvp.model.Audio;
import com.aistream.greenqube.mvp.model.Director;
import com.aistream.greenqube.mvp.model.Genre;
import com.aistream.greenqube.mvp.model.Keyword;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.model.Producer;
import com.aistream.greenqube.mvp.model.Publisher;
import com.aistream.greenqube.mvp.model.Quality;
import com.aistream.greenqube.mvp.model.Subtitle;
import com.aistream.greenqube.mvp.model.VideoType;
import com.aistream.greenqube.mvp.model.WifiInfo;
import com.aistream.greenqube.services.DownloadManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 5/19/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final Gson gson = new Gson();
    private static final JsonParser parser = new JsonParser();

    private static final String TAG = "DataBaseHelper";

    private static class DataBaseHelperHolder {
        private static final DataBaseHelper instance = new DataBaseHelper();
    }

    public synchronized static DataBaseHelper getInstance() {
        return DataBaseHelperHolder.instance;
    }

    // Logcat tag
    private static final String LOG = "DataBaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 16;

    // Database Name
    private static final String DATABASE_NAME = "HotSpotNew.db";

    // Table Names
    private static final String TABLE_GENRE = "GenreInfo";
    private static final String TABLE_MOVIE = "MovieInfo";
    private static final String TABLE_HOT_MOVIE = "HotMovie";
    private static final String TABLE_NEW_RELEASE = "NewRelease";
    private static final String TABLE_QUALITY = "QualityInfo";
    private static final String TABLE_WIFI_ROUTER = "WifiRouter";
    private static final String TABLE_DOWNLOAD_MOVIE = "downloadmovie";
    private static final String TABLE_DOWNLOAD_RIGHT = "DownloadRight";
    private static final String TABLE_MOVIE_FREE_PLANS = "MovieFreePlans";


    // Column names of GenreInfo
    private static final String KEY_GENREID = "genreid";
    private static final String KEY_GENRENAME = "genrename";

    // Column names of MovieInfo
    private static final String KEY_MVID = "movie_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SYNOPSIS = "synopsis";
    private static final String KEY_PGLEVEL = "pg_level";
    private static final String KEY_PGNAME = "pg_name";
    private static final String KEY_RATINGID = "rating_id";
    private static final String KEY_DOWNLOADS = "downloads";
    private static final String KEY_PUBLISHDATE = "publish_date";
    private static final String KEY_REPLEASEDATE = "release_date";
    private static final String KEY_EXPIREON = "expire_on";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_POSTER = "poster";
    private static final String KEY_PREVIEW = "preview";
    private static final String KEY_THEATRICAL_POSTER = "theatrical_poster";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_STARSCORE = "star_score";
    private static final String KEY_TYPE = "type";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DIRECTORS = "directors";
    private static final String KEY_ACTORS = "actors";
    private static final String KEY_PUBLISHERS = "publishers";
    private static final String KEY_PRODUCERS = "producers";
    private static final String KEY_KEYWORDS = "keywords";
    private static final String KEY_GENRES = "genres";
    private static final String KEY_SUBTITLES = "subtitles";
    private static final String KEY_AUDIOS = "audios";
    private static final String KEY_QUALITYLIST = "quality_list";
    private static final String KEY_VIP = "vip";
    private static final String KEY_CONTENT_TYPE = "content_type";
    private static final String KEY_VIDEO_TYPE = "video_type";

    // Column names of getWifiRouterList
    private static final String KEY_ID_WF = "id_wf";
    private static final String KEY_ROUTER_ID = "routerid";
    private static final String KEY_MAC = "mac";
    private static final String KEY_MAC_5G = "mac_5g";
    private static final String KEY_SSID = "ssid";
    private static final String KEY_SSID5G = "ssid5g";
    private static final String KEY_PASS = "password";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_REGION = "region";
    private static final String KEY_CITY = "city";
    private static final String KEY_POSTTAL_CODE = "postal_code";
    private static final String KEY_HOTSPOT_ID = "hotspot_id";
    private static final String KEY_COUNTRY_WF = "country";
    private static final String KEY_NAME_WF = "name_wf";
    private static final String KEY_HW_BRAND = "hw_brand";
    private static final String KEY_HW_MODEL = "hwModel";
    private static final String KEY_ADDRESS1 = "address1";
    private static final String KEY_ADDRESS2 = "address2";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_ICON_MAP = "icon";
    private static final String KEY_STATUS_WF = "status";
    private static final String KEY_VISIBLEONAPP_WF = "visibleonapps";
    private static final String KEY_LOCATION = "location";

    // Column names of QualityInfo
    private static final String KEY_QUALITY = "quality";
    private static final String KEY_DRMTYPE = "drm_type";
    private static final String KEY_FILENAME = "file_name";
    private static final String KEY_FILESIZE = "file_size";
    private static final String KEY_ASPECTRATIO = "aspect_ratio";
    private static final String KEY_RESOLUTION = "resolution";
    private static final String KEY_PRICE = "price";
    private static final String KEY_POINTS = "points";
    private static final String KEY_RENTAL_PERIOD = "rental_period";
    private static final String KEY_MD5 = "md5";
    private static final String KEY_DOWNLOAD_ID = "downloadid";
    private static final String KEY_MVID_FOREIGN_KEY = "mvid";

    //colum names of Download
    private static final String KEY_ID_DOWNLOAD = "downloadid";//iddownload cua DownloadManager
    private static final String KEY_MVID_FOREIGN = "mvid"; //FOREIGN_KEY
    private static final String KEY_STATUS_DL = "statusdl"; // status of downloadmanager
    private static final String KEY_FILENAME_DL = "filename";
    private static final String KEY_MVNAME_DL = "mvname";
    private static final String KEY_IMAGE_DL = "image";
    private static final String KEY_PATH_DL = "path";
    private static final String KEY_TIME_DL = "time";
    private static final String KEY_TYPE_MOVIE = "typeMovie";
    private static final String KEY_SCREENDOWN_DL = "screendown";
    private static final String KEY_DATAGSON_DL = "datagson";
    private static final String KEY_REASON_DL = "reason";
    private static final String KEY_TYPE_DL = "typedownload";//0 is waitting to download; 1 is waitting network; 2 is fail; 3 is added to downloadmanager
    private static final String KEY_FILESIZE_DL = "fileSize";
    private static final String KEY_TYPE_CODE_RESPON = "typeCodeRespon";//0
    private static final String KEY_DRM_TYPE_DL = "drmType";
    private static final String KEY_FAIL_DESCRIPTION_REQUEST = "failDescription";
    private static final String KEY_TIME_CONTINUE = "timeContinue";
    private static final String KEY_RENTALRERIOD_DL = "rentalPeriod";
    private static final String KEY_RENTALDAYRERIOD_DL = "rentalDayPeriod";
    private static final String KEY_EXPIRETIME_DL = "expireTime";
    private static final String KEY_DURATION_DL = "duration";
    private static final String KEY_RENTAL_START = "rental_start";
    private static final String KEY_RENTAL_END = "rental_end";
//    private static final String KEY_TYPE_M3U8_OR_DLRIGHT = "typeM3U8orDownloadRight";

    //TABLE_MOVIR_FREE_PLANS
    private static final String KEY_MOVIE_ID_FREE_PLANS = "movieId";
    private static final String KEY_FREE_PLANS = "free_plans";

    // Column names of VideoType
    private static final String KEY_TYPEID = "type";
    private static final String KEY_TYPENAME = "typename";

    // TABLE_GENRE create statement
    private static final String CREATE_TABLE_GENRE = "CREATE TABLE "
            + TABLE_GENRE + "(" + KEY_GENREID + " INTEGER PRIMARY KEY," + KEY_GENRENAME
            + " TEXT" + ")";

    // TABLE_MOVIE create statement
    private static final String CREATE_TABLE_MOVIE = "CREATE TABLE " + TABLE_MOVIE
            + "(" + KEY_MVID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_SYNOPSIS + " TEXT,"
            + KEY_PGLEVEL + " INTEGER," + KEY_PGNAME + " TEXT," + KEY_RATINGID + " INTEGER," + KEY_DOWNLOADS + " INTEGER,"
            + KEY_PUBLISHDATE + " TEXT," + KEY_REPLEASEDATE + " TEXT," + KEY_EXPIREON + " LONG,"
            + KEY_DURATION + " INTEGER," + KEY_POSTER + " TEXT," + KEY_PREVIEW + " TEXT," + KEY_THEATRICAL_POSTER
            + " TEXT," + KEY_COUNTRY + " TEXT," + KEY_STARSCORE + " TEXT,"
            + KEY_TYPE + " INTEGER," + KEY_STATUS + " INTEGER," + KEY_DIRECTORS + " TEXT," + KEY_ACTORS + " TEXT,"
            + KEY_PUBLISHERS + " TEXT," + KEY_PRODUCERS + " TEXT," + KEY_KEYWORDS + " TEXT," + KEY_GENRES + " TEXT,"
            + KEY_SUBTITLES + " TEXT," + KEY_AUDIOS + " TEXT, " + KEY_QUALITYLIST + " TEXT," + KEY_VIP + " INTEGER,"
            + KEY_RENTAL_START + " BIGINT," + KEY_RENTAL_END + " BIGINT, " + KEY_CONTENT_TYPE + " INTEGER, "
            + KEY_VIDEO_TYPE + " TEXT)";

    // TABLE_HOT_MOVIE create statement
    private static final String CREATE_TABLE_HOT_MOVIE = "CREATE TABLE " + TABLE_HOT_MOVIE
            + "(" + KEY_MVID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_SYNOPSIS + " TEXT,"
            + KEY_PGLEVEL + " INTEGER," + KEY_PGNAME + " TEXT," + KEY_RATINGID + " INTEGER," + KEY_DOWNLOADS + " INTEGER,"
            + KEY_PUBLISHDATE + " TEXT," + KEY_REPLEASEDATE + " TEXT," + KEY_EXPIREON + " LONG,"
            + KEY_DURATION + " INTEGER," + KEY_POSTER + " TEXT," + KEY_PREVIEW + " TEXT," + KEY_THEATRICAL_POSTER + " TEXT," + KEY_COUNTRY + " TEXT," + KEY_STARSCORE + " TEXT,"
            + KEY_TYPE + " INTEGER," + KEY_STATUS + " INTEGER," + KEY_DIRECTORS + " TEXT," + KEY_ACTORS + " TEXT,"
            + KEY_PUBLISHERS + " TEXT," + KEY_PRODUCERS + " TEXT," + KEY_KEYWORDS + " TEXT," + KEY_GENRES + " TEXT,"
            + KEY_SUBTITLES + " TEXT," + KEY_AUDIOS + " TEXT, " + KEY_QUALITYLIST + " TEXT," + KEY_VIP + " INTEGER, " + KEY_CONTENT_TYPE + " INTEGER, "
            + KEY_VIDEO_TYPE + " TEXT)";

    // TABLE_NEW_RELEASE create statement
    private static final String CREATE_TABLE_NEW_RELEASE = "CREATE TABLE " + TABLE_NEW_RELEASE
            + "(" + KEY_MVID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_SYNOPSIS + " TEXT,"
            + KEY_PGLEVEL + " INTEGER," + KEY_PGNAME + " TEXT," + KEY_RATINGID + " INTEGER," + KEY_DOWNLOADS + " INTEGER,"
            + KEY_PUBLISHDATE + " TEXT," + KEY_REPLEASEDATE + " TEXT," + KEY_EXPIREON + " LONG,"
            + KEY_DURATION + " INTEGER," + KEY_POSTER + " TEXT," + KEY_PREVIEW + " TEXT," + KEY_THEATRICAL_POSTER + " TEXT," + KEY_COUNTRY + " TEXT," + KEY_STARSCORE + " TEXT,"
            + KEY_TYPE + " INTEGER," + KEY_STATUS + " INTEGER," + KEY_DIRECTORS + " TEXT," + KEY_ACTORS + " TEXT,"
            + KEY_PUBLISHERS + " TEXT," + KEY_PRODUCERS + " TEXT," + KEY_KEYWORDS + " TEXT," + KEY_GENRES + " TEXT,"
            + KEY_SUBTITLES + " TEXT," + KEY_AUDIOS + " TEXT, " + KEY_QUALITYLIST + " TEXT," + KEY_VIP + " INTEGER, " + KEY_CONTENT_TYPE + " INTEGER, "
            + KEY_VIDEO_TYPE + " TEXT)";

    // TABLE_QUALITY create statement
    private static final String CREATE_TABLE_QUALITY = " CREATE TABLE " + TABLE_QUALITY
            + "(" + KEY_QUALITY + " INTEGER," + KEY_DRMTYPE + " INTEGER,"
            + KEY_FILENAME + " TEXT," + KEY_FILESIZE + " INTEGER," + KEY_ASPECTRATIO + " TEXT,"
            + KEY_RESOLUTION + " INTEGER," + KEY_PRICE + " REAL," + KEY_POINTS + " INTEGER," + KEY_RENTAL_PERIOD + " INTEGER,"
            + KEY_MD5 + " TEXT," + KEY_DOWNLOAD_ID + " INTEGER," + KEY_MVID_FOREIGN_KEY + " INTEGER" + ")";

    // TABLE_WIFI_ROUTER create statement
    private static final String CREATE_TABLE_WIFI_ROUTER = "CREATE TABLE " + TABLE_WIFI_ROUTER
            + "(" + KEY_ID_WF + " INTEGER PRIMARY KEY," + KEY_ROUTER_ID + " TEXT," + KEY_MAC + " TEXT,"
            + KEY_MAC_5G + " TEXT," + KEY_SSID + " TEXT," + KEY_SSID5G + " TEXT," + KEY_PASS + " TEXT," + KEY_LONG + " REAL,"
            + KEY_LAT + " REAL," + KEY_REGION + " TEXT," + KEY_CITY + " TEXT," + KEY_POSTTAL_CODE + " TEXT,"
            + KEY_HOTSPOT_ID + " TEXT," + KEY_COUNTRY_WF + " TEXT," + KEY_NAME_WF + " TEXT," + KEY_HW_BRAND + " TEXT,"
            + KEY_HW_MODEL + " TEXT," + KEY_ADDRESS1 + " TEXT," + KEY_ADDRESS2 + " TEXT," + KEY_NOTES + " TEXT,"
            + KEY_ICON_MAP + " TEXT," + KEY_STATUS_WF + " INTEGER," + KEY_VISIBLEONAPP_WF + " INTEGER, " + KEY_LOCATION + " TEXT" + ")";

    //TABLE DOWNLOAD
    String CREATE_TABLE_DOWNLOAD = "CREATE TABLE " + TABLE_DOWNLOAD_MOVIE
            + "(" +
            KEY_MVID_FOREIGN + " INTEGER PRIMARY KEY," +
            KEY_ID_DOWNLOAD + " INTEGER," +
            KEY_STATUS_DL + " INTEGER," +
            KEY_SCREENDOWN_DL + " INTEGER," +
            KEY_TYPE_MOVIE + " INTEGER," +
            KEY_DATAGSON_DL + " TEXT," +
            KEY_FILENAME_DL + " TEXT," +
            KEY_MVNAME_DL + " TEXT," +
            KEY_IMAGE_DL + " TEXT," +
            KEY_PATH_DL + " TEXT," +
            KEY_REASON_DL + " INTEGER," +
            KEY_TIME_DL + " TEXT," +
            KEY_TYPE_DL + " INTEGER," +
            KEY_FILESIZE_DL + " INTEGER," +
            KEY_TYPE_CODE_RESPON + " INTEGER," +
            KEY_DRM_TYPE_DL + " INTEGER," +
            KEY_FAIL_DESCRIPTION_REQUEST + " TEXT," +
            KEY_TIME_CONTINUE + " TEXT," +
            KEY_RENTALRERIOD_DL + " TEXT," +
            KEY_RENTALDAYRERIOD_DL + " TEXT," +
            KEY_EXPIRETIME_DL + " LONG," +
            KEY_DURATION_DL + " INTEGER," +
            KEY_RENTAL_START + " BIGINT," +
            KEY_RENTAL_END + " BIGINT," +
            KEY_VIP + " BIGINT, " +
            KEY_CONTENT_TYPE + "INTEGER, "+
            KEY_VIDEO_TYPE + "TEXT)";

    private DataBaseHelper() {
        super(OgleApplication.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GENRE);
        db.execSQL(CREATE_TABLE_MOVIE);
        db.execSQL(CREATE_TABLE_HOT_MOVIE);
        db.execSQL(CREATE_TABLE_NEW_RELEASE);
        db.execSQL(CREATE_TABLE_QUALITY);
        db.execSQL(CREATE_TABLE_WIFI_ROUTER);
        db.execSQL(CREATE_TABLE_DOWNLOAD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade oldVersion: "+oldVersion+", newVersion: "+newVersion);
        if (oldVersion < newVersion) {
            if (newVersion < 13) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOT_MOVIE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_RELEASE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUALITY);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI_ROUTER);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOAD_MOVIE);
                onCreate(db);
            }

            if (oldVersion < 13 && newVersion >= 13) {
                //check table whether has vip column
                String[] tables = new String[]{ TABLE_MOVIE, TABLE_HOT_MOVIE, TABLE_NEW_RELEASE, TABLE_DOWNLOAD_MOVIE};
                for (String table: tables) {
                    db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + KEY_VIP + " INTEGER;");
                }
            }

            if (oldVersion < 14 &&  newVersion >= 14) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE_FREE_PLANS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOAD_RIGHT);
                db.execSQL("ALTER TABLE " + TABLE_MOVIE + " ADD COLUMN " + KEY_RENTAL_START + " BIGINT DEFAULT 0;");
                db.execSQL("ALTER TABLE " + TABLE_MOVIE + " ADD COLUMN " + KEY_RENTAL_END + " BIGINT DEFAULT 0;");
                db.execSQL("ALTER TABLE " + TABLE_DOWNLOAD_MOVIE + " ADD COLUMN " + KEY_RENTAL_START + " BIGINT DEFAULT 0;");
                db.execSQL("ALTER TABLE " + TABLE_DOWNLOAD_MOVIE + " ADD COLUMN " + KEY_RENTAL_END + " BIGINT DEFAULT 0;");
                db.execSQL("ALTER TABLE " + TABLE_DOWNLOAD_MOVIE + " ADD COLUMN " + KEY_VIP + " BIGINT DEFAULT 0;");
            }

            if (oldVersion < 15 && newVersion >= 15) {
                //check table whether has vip column
                String[] tables = new String[]{ TABLE_MOVIE, TABLE_HOT_MOVIE, TABLE_NEW_RELEASE, TABLE_DOWNLOAD_MOVIE};
                for (String table: tables) {
                    db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + KEY_CONTENT_TYPE + " INTEGER;");
                    db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + KEY_VIDEO_TYPE + " TEXT;");
                }
            }

            if (oldVersion < 16 && newVersion >= 16) {
                //check table whether has vip column
                String[] tables = new String[]{ TABLE_WIFI_ROUTER};
                for (String table: tables) {
                    db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + KEY_LOCATION + " TEXT;");
                }
            }
        }
    }

    /**
     * get table columns
     * @param tableName
     * @return
     */
    private List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> colList = new ArrayList<>();
        String sql = "PRAGMA table_info("+tableName+")";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String column = cursor.getString(cursor.getColumnIndex("name"));
                colList.add(column);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return colList;
    }

    public void insertListGenre(Collection<Genre> genreList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Genre genre : genreList) {
                ContentValues values = new ContentValues();
                values.put(KEY_GENREID, genre.getId());
                values.put(KEY_GENRENAME, genre.getName());
                db.insertWithOnConflict(TABLE_GENRE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteGenderList() {
        deleteTable(TABLE_GENRE);
    }

    public List<Genre> getGenreList() {
        List<Genre> genreList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_GENRE + " GROUP BY " + KEY_GENRENAME;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Genre genre = new Genre(c.getInt((c.getColumnIndex(KEY_GENREID))), (c.getString(c.getColumnIndex(KEY_GENRENAME))));
                    genreList.add(genre);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genreList;
    }

    public void deleteTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + tableName;
        try {
            db.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertListWifiList(List<WifiInfo> wifiInfoList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (WifiInfo wifiInfo : wifiInfoList) {
                ContentValues values = new ContentValues();
                values.put(KEY_ID_WF, wifiInfo.getId());
                values.put(KEY_ROUTER_ID, wifiInfo.getRouterid());
                values.put(KEY_MAC, wifiInfo.getMac());
                values.put(KEY_MAC_5G, wifiInfo.getMac5g());
                values.put(KEY_SSID, wifiInfo.getSsid());
                values.put(KEY_SSID5G, wifiInfo.getSsid5g());
                values.put(KEY_PASS, wifiInfo.getPassword());
                values.put(KEY_LONG, wifiInfo.getLongitude());
                values.put(KEY_LAT, wifiInfo.getLatitude());
                values.put(KEY_REGION, wifiInfo.getRegion());
                values.put(KEY_CITY, wifiInfo.getCity());
                values.put(KEY_POSTTAL_CODE, wifiInfo.getPostalCode());
                values.put(KEY_HOTSPOT_ID, wifiInfo.getHotspotId());
                values.put(KEY_COUNTRY_WF, wifiInfo.getCountry());
                values.put(KEY_NAME_WF, wifiInfo.getName());
                values.put(KEY_HW_BRAND, wifiInfo.getHwBrand());
                values.put(KEY_HW_MODEL, wifiInfo.getHwModel());
                values.put(KEY_ADDRESS1, wifiInfo.getAddress1());
                values.put(KEY_ADDRESS2, wifiInfo.getAddress2());
                values.put(KEY_NOTES, wifiInfo.getNotes());
                values.put(KEY_ICON_MAP, wifiInfo.getIcon());
                values.put(KEY_STATUS_WF, wifiInfo.getStatus());
                values.put(KEY_VISIBLEONAPP_WF, wifiInfo.getVisibleonapps());
                values.put(KEY_LOCATION, wifiInfo.getLocation());
                db.insertWithOnConflict(TABLE_WIFI_ROUTER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteListWifi() {
        deleteTable(TABLE_WIFI_ROUTER);
    }

    public List<WifiInfo> getWifiInfoList() {
        List<WifiInfo> wifiInfoList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WIFI_ROUTER;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    WifiInfo wifiInfo = new WifiInfo();
                    wifiInfo.setId(c.getInt(c.getColumnIndex(KEY_ID_WF)));
                    wifiInfo.setRouterid(c.getString(c.getColumnIndex(KEY_ROUTER_ID)));
                    wifiInfo.setMac(c.getString(c.getColumnIndex(KEY_MAC)));
                    wifiInfo.setMac5g(c.getString(c.getColumnIndex(KEY_MAC_5G)));
                    wifiInfo.setSsid(c.getString(c.getColumnIndex(KEY_SSID)));
                    wifiInfo.setSsid5g(c.getString(c.getColumnIndex(KEY_SSID5G)));
                    wifiInfo.setPassword(c.getString(c.getColumnIndex(KEY_PASS)));
                    wifiInfo.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONG)));
                    wifiInfo.setLatitude(c.getDouble(c.getColumnIndex(KEY_LAT)));
                    wifiInfo.setRegion(c.getString(c.getColumnIndex(KEY_REGION)));
                    wifiInfo.setCity(c.getString(c.getColumnIndex(KEY_CITY)));
                    wifiInfo.setPostalCode(c.getString(c.getColumnIndex(KEY_POSTTAL_CODE)));
                    wifiInfo.setHotspotId(c.getString(c.getColumnIndex(KEY_HOTSPOT_ID)));
                    wifiInfo.setCountry(c.getString(c.getColumnIndex(KEY_COUNTRY_WF)));
                    wifiInfo.setName(c.getString(c.getColumnIndex(KEY_NAME_WF)));
                    wifiInfo.setHwBrand(c.getString(c.getColumnIndex(KEY_HW_BRAND)));
                    wifiInfo.setHwModel(c.getString(c.getColumnIndex(KEY_HW_MODEL)));
                    wifiInfo.setAddress1(c.getString(c.getColumnIndex(KEY_ADDRESS1)));
                    wifiInfo.setAddress2(c.getString(c.getColumnIndex(KEY_ADDRESS2)));
                    wifiInfo.setNotes(c.getString(c.getColumnIndex(KEY_NOTES)));
                    wifiInfo.setIcon(c.getString(c.getColumnIndex(KEY_ICON_MAP)));
                    wifiInfo.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS_WF)));
                    wifiInfo.setVisibleonapps(c.getInt(c.getColumnIndex(KEY_VISIBLEONAPP_WF)));
                    wifiInfo.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                    wifiInfoList.add(wifiInfo);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wifiInfoList;
    }

    /**
     * query movie list by sql
     * @param sql
     * @return
     */
    private List<MovieInfo> queryMovieList(String sql, String... params) {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(sql, params);
            if (c.moveToFirst()) {
                do {
                    MovieInfo movieInfo = getMovieInfoByCursor(c);
                    movieInfoList.add(movieInfo);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movieInfoList;
    }

    /**
     * build movie info from cursor
     * @param c
     * @return
     */
    private MovieInfo getMovieInfoByCursor(Cursor c) {
        MovieInfo movieInfo = new MovieInfo();
        movieInfo.setMovieId(c.getInt((c.getColumnIndex(KEY_MVID))));
        movieInfo.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        movieInfo.setSynopsis(c.getString(c.getColumnIndex(KEY_SYNOPSIS)));
        movieInfo.setPgLevel(c.getInt(c.getColumnIndex(KEY_PGLEVEL)));
        movieInfo.setPgName(c.getString(c.getColumnIndex(KEY_PGNAME)));
        movieInfo.setRatingId(c.getInt(c.getColumnIndex(KEY_RATINGID)));
        movieInfo.setDownloads(c.getInt((c.getColumnIndex(KEY_DOWNLOADS))));
        movieInfo.setPublishDate(c.getString((c.getColumnIndex(KEY_PUBLISHDATE))));
        movieInfo.setReleaseDate((c.getString(c.getColumnIndex(KEY_REPLEASEDATE))));
        movieInfo.setExpireOn(c.getLong(c.getColumnIndex(KEY_EXPIREON)));
        movieInfo.setDuration(c.getInt(c.getColumnIndex(KEY_DURATION)));
        movieInfo.setPoster(c.getString(c.getColumnIndex(KEY_POSTER)));
        movieInfo.setPreview(c.getString(c.getColumnIndex(KEY_PREVIEW)));
        movieInfo.setTheatricalPoster(c.getString(c.getColumnIndex(KEY_THEATRICAL_POSTER)));
        movieInfo.setCountry(c.getString((c.getColumnIndex(KEY_COUNTRY))));
        movieInfo.setStarScore(c.getString((c.getColumnIndex(KEY_STARSCORE))));
        movieInfo.setType((c.getInt(c.getColumnIndex(KEY_TYPE))));
        movieInfo.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
        movieInfo.setDirectors(readList(c.getString(c.getColumnIndex(KEY_DIRECTORS)), new TypeToken<Director>() {
        }.getType()));
        movieInfo.setActors(readList(c.getString(c.getColumnIndex(KEY_ACTORS)), new TypeToken<Actor>() {
        }.getType()));
        movieInfo.setPublishers(readList(c.getString(c.getColumnIndex(KEY_PUBLISHERS)), new TypeToken<Publisher>() {
        }.getType()));
        movieInfo.setProducers(readList(c.getString(c.getColumnIndex(KEY_PRODUCERS)), new TypeToken<Producer>() {
        }.getType()));
        movieInfo.setKeywords(readList(c.getString(c.getColumnIndex(KEY_KEYWORDS)), new TypeToken<Keyword>() {
        }.getType()));
        movieInfo.setGenres(readList(c.getString(c.getColumnIndex(KEY_GENRES)), new TypeToken<Genre>() {
        }.getType()));//model
        movieInfo.setSubtitles(readList(c.getString(c.getColumnIndex(KEY_SUBTITLES)), new TypeToken<Subtitle>() {
        }.getType()));
        movieInfo.setAudios(readList(c.getString(c.getColumnIndex(KEY_AUDIOS)), new TypeToken<Audio>() {
        }.getType()));
        movieInfo.setQualityList(readList(c.getString(c.getColumnIndex(KEY_QUALITYLIST)), new TypeToken<Quality>() {
        }.getType()));
        movieInfo.setVip(c.getInt(c.getColumnIndex(KEY_VIP)));
        movieInfo.setContentType(c.getInt(c.getColumnIndex(KEY_CONTENT_TYPE)));
        String videoTypeStr = c.getString(c.getColumnIndex(KEY_VIDEO_TYPE));
        try {
            if (!TextUtils.isEmpty(videoTypeStr)) {
                movieInfo.setVideoType(new Gson().fromJson(videoTypeStr, VideoType.class));
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        try {
            int columnIndex = c.getColumnIndex(KEY_RENTAL_START);
            if (columnIndex > 0) {
                movieInfo.setRentalStart(c.getLong(c.getColumnIndex(KEY_RENTAL_START)));
                movieInfo.setRentalEnd(c.getLong(c.getColumnIndex(KEY_RENTAL_END)));
            }
        } catch (Exception e) {
        }
        return movieInfo;
    }

    //movie
    public List<MovieInfo> getAllListMovie() {
        String selectQuery = "SELECT * FROM " + TABLE_MOVIE;
        return queryMovieList(selectQuery);
    }

    /**
     * get all purchased movies
     * @return
     */
    public List<MovieInfo> getAllPurchasedMovie() {
        String selectQuery = "SELECT * FROM " + TABLE_MOVIE + " where " + KEY_TYPE + " = ? and ("+ KEY_RENTAL_END + " > " + KEY_RENTAL_START + " or "+KEY_RENTAL_START + " = 0)";
        return queryMovieList(selectQuery, String.valueOf(2));
    }

    /**
     * get movie info by id
     * @param mvId
     * @return
     */
    public MovieInfo getMovieInfo(int mvId) {
        MovieInfo movieInfo = null;
        String selectQuery = "SELECT  * FROM " + TABLE_MOVIE + " WHERE " + KEY_MVID + "=?";
        List<MovieInfo> movieList = queryMovieList(selectQuery, String.valueOf(mvId));
        if (!movieList.isEmpty()) {
            movieInfo = movieList.get(0);
        }
        return movieInfo;
    }

    /**
     * insert movies to db
     * @param tableName
     * @param movieInfoList
     */
    private void insertMovies(String tableName, Map<Integer, MovieInfo> movieInfoList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Map.Entry<Integer, MovieInfo> entry : movieInfoList.entrySet()) {
                ContentValues values = buildMovieContentValues(entry.getValue());
                db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private ContentValues buildMovieContentValues(MovieInfo movieInfo) {
        ContentValues values = new ContentValues();
        values.put(KEY_MVID, movieInfo.getMovieId());
        values.put(KEY_NAME, movieInfo.getName());
        values.put(KEY_SYNOPSIS, movieInfo.getSynopsis());
        values.put(KEY_PGLEVEL, movieInfo.getPgLevel());
        values.put(KEY_PGNAME, movieInfo.getPgName());
        values.put(KEY_RATINGID, movieInfo.getRatingId());
        values.put(KEY_DOWNLOADS, movieInfo.getDownloads());
        values.put(KEY_PUBLISHDATE, movieInfo.getPublishDate());
        values.put(KEY_REPLEASEDATE, movieInfo.getReleaseDate());
        values.put(KEY_EXPIREON, movieInfo.getExpireOn());
        values.put(KEY_DURATION, movieInfo.getDuration());
        values.put(KEY_POSTER, movieInfo.getPoster());
        values.put(KEY_PREVIEW, movieInfo.getPreview());
        values.put(KEY_THEATRICAL_POSTER, movieInfo.getTheatricalPoster());
        values.put(KEY_COUNTRY, movieInfo.getCountry());
        values.put(KEY_STARSCORE, movieInfo.getStarScore());
        values.put(KEY_TYPE, movieInfo.getType());
        values.put(KEY_STATUS, movieInfo.getStatus());
        values.put(KEY_DIRECTORS, insertList(movieInfo.getDirectors()));
        values.put(KEY_ACTORS, insertList(movieInfo.getActors()));
        values.put(KEY_PUBLISHERS, insertList(movieInfo.getPublishers()));
        values.put(KEY_PRODUCERS, insertList(movieInfo.getProducers()));
        values.put(KEY_KEYWORDS, insertList(movieInfo.getKeywords()));
        values.put(KEY_GENRES, insertList(movieInfo.getGenres()));
        values.put(KEY_SUBTITLES, insertList(movieInfo.getSubtitles()));
        values.put(KEY_AUDIOS, insertList(movieInfo.getAudios()));
        values.put(KEY_QUALITYLIST, insertList(movieInfo.getQualityList()));
        values.put(KEY_VIP, movieInfo.getVip());
        values.put(KEY_CONTENT_TYPE, movieInfo.getContentType());
        if (movieInfo.getVideoType() != null) {
            values.put(KEY_VIDEO_TYPE, new Gson().toJson(movieInfo.getVideoType()));
        }
        if (movieInfo.getRentalStart() > 0) {
            values.put(KEY_RENTAL_START, movieInfo.getRentalStart());
            values.put(KEY_RENTAL_END, movieInfo.getRentalEnd());
        }
        return values;
    }

    public void insertListMovie(Map<Integer, MovieInfo> movieInfoList) {
        insertMovies(TABLE_MOVIE, movieInfoList);
    }

    public void deleteMovieList() {
        deleteTable(TABLE_MOVIE);
    }

    public void updateMovieExpireTime(int mvId, long expireTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_EXPIREON, expireTime);
            db.update(TABLE_MOVIE, cv, KEY_MVID + " = ?", new String[]{String.valueOf(mvId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get all hot movies
     * @return
     */
    public List<MovieInfo> getAllListHotMovie() {
        String selectQuery = "SELECT * FROM " + TABLE_HOT_MOVIE;
        return queryMovieList(selectQuery);
    }

    /**
     * insert hot movies
     * @param movieInfoList
     */
    public void insertListHotMovie(Map<Integer, MovieInfo> movieInfoList) {
        insertMovies(TABLE_HOT_MOVIE, movieInfoList);
    }

    /**
     * delete all hot movies
     */
    public void deleteHotMovieList() {
        deleteTable(TABLE_HOT_MOVIE);
    }

    /**
     * get all promotion movies
     * @return
     */
    public List<MovieInfo> getAllListNewRelease() {
        String selectQuery = "SELECT * FROM " + TABLE_NEW_RELEASE;
        return queryMovieList(selectQuery);
    }

    public void insertListNewReplease(Map<Integer, MovieInfo> movieInfoList) {
        insertMovies(TABLE_NEW_RELEASE, movieInfoList);
    }

    public void deleteNewRepleaseList() {
        deleteTable(TABLE_NEW_RELEASE);
    }

    //all download, type download 1:wifi and mac fail, 2:fail request
    //typecoderespon: 0:code==200, 1:code!=200, 2:Code=FailRequest
    public long addDownLoadQueue(MovieInfo movieInfo) {
        SQLiteDatabase db = getWritableDatabase();
        long result = -1;
        try {
            Quality defaultQuality = movieInfo.getDefaultQuality();
            ContentValues values = new ContentValues();
            values.put(KEY_ID_DOWNLOAD, 0);
            values.put(KEY_FILENAME_DL, defaultQuality.getFileName());
            values.put(KEY_MVID_FOREIGN, movieInfo.getMovieId());
            values.put(KEY_IMAGE_DL, movieInfo.getTheatricalPoster());
            values.put(KEY_DATAGSON_DL, ReadWriteFile.convertObjToString(movieInfo));
            values.put(KEY_TYPE_MOVIE, movieInfo.getType());
            values.put(KEY_MVNAME_DL, movieInfo.getName());
            values.put(KEY_TYPE_DL, 0);
            values.put(KEY_FILESIZE_DL, defaultQuality.getFileSize());
            values.put(KEY_TYPE_CODE_RESPON, -1);
            values.put(KEY_DRM_TYPE_DL, defaultQuality.getDrmType());
            values.put(KEY_DURATION_DL, movieInfo.getDuration());
            values.put(KEY_RENTALRERIOD_DL, defaultQuality.getRentalForMillSeconds());
            values.put(KEY_RENTALDAYRERIOD_DL, defaultQuality.getRentalForMillSeconds() / 86400000);
            result = db.insertOrThrow(TABLE_DOWNLOAD_MOVIE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * query movie download list
     * @param sql
     * @param params
     * @return
     */
    private List<MovieDownload> queryMovieDownloads(String sql, String... params) {
        SQLiteDatabase db = getReadableDatabase();
        List<MovieDownload> array = new ArrayList<>();
        try {
            Cursor c = db.rawQuery(sql, params);
            if (c.moveToFirst()) {
                do {
                    array.add(getMovieDownloadByCursor(c));
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * build movie download info by cursor
     * @param c
     * @return
     */
    private MovieDownload getMovieDownloadByCursor(Cursor c) {
        MovieDownload movieDownload = new MovieDownload();
        movieDownload.setIdDownload(c.getInt((c.getColumnIndex(KEY_ID_DOWNLOAD))));
        movieDownload.setMvId(c.getInt((c.getColumnIndex(KEY_MVID_FOREIGN))));
        movieDownload.setStatus(c.getInt((c.getColumnIndex(KEY_STATUS_DL))));
        movieDownload.setFileName(c.getString((c.getColumnIndex(KEY_FILENAME_DL))));
        movieDownload.setImage(c.getString((c.getColumnIndex(KEY_IMAGE_DL))));
        movieDownload.setPath(c.getString((c.getColumnIndex(KEY_PATH_DL))));
        movieDownload.setMvName(c.getString((c.getColumnIndex(KEY_MVNAME_DL))));
        movieDownload.setTime(c.getString(c.getColumnIndex(KEY_TIME_DL)));
        movieDownload.setScreenDown(c.getInt((c.getColumnIndex(KEY_SCREENDOWN_DL))));
        movieDownload.setDataGson(c.getString((c.getColumnIndex(KEY_DATAGSON_DL))));
        movieDownload.setTypeMovie(c.getInt(c.getColumnIndex(KEY_TYPE_MOVIE)));
        movieDownload.setReason(c.getInt((c.getColumnIndex(KEY_REASON_DL))));
        movieDownload.setTypeDownload(c.getInt((c.getColumnIndex(KEY_TYPE_DL))));
        movieDownload.setFileSize(c.getInt(c.getColumnIndex(KEY_FILESIZE_DL)));
        movieDownload.setTypeCodeRespon(c.getInt(c.getColumnIndex(KEY_TYPE_CODE_RESPON)));
        movieDownload.setDrmType(c.getInt(c.getColumnIndex(KEY_DRM_TYPE_DL)));
        movieDownload.setFailDescription(c.getString(c.getColumnIndex(KEY_FAIL_DESCRIPTION_REQUEST)));
        movieDownload.setTimeContinue(c.getLong(c.getColumnIndex(KEY_TIME_CONTINUE)));
        movieDownload.setRentalPeriod(c.getLong(c.getColumnIndex(KEY_RENTALRERIOD_DL)));
        movieDownload.setRentalDayPeriod(c.getLong(c.getColumnIndex(KEY_RENTALDAYRERIOD_DL)));
        movieDownload.setExpireTime(c.getLong(c.getColumnIndex(KEY_EXPIRETIME_DL)));
        movieDownload.setDuration(c.getInt(c.getColumnIndex(KEY_DURATION_DL)));
        movieDownload.setRentalStart(c.getLong(c.getColumnIndex(KEY_RENTAL_START)));
        movieDownload.setRentalEnd(c.getLong(c.getColumnIndex(KEY_RENTAL_END)));
        return movieDownload;
    }

    /**
     * get all movie download
     * @return
     */
    public List<MovieDownload> getAllMovieDownload() {
        String usersSelectQuery = String.format("SELECT * FROM %s", TABLE_DOWNLOAD_MOVIE);
        return queryMovieDownloads(usersSelectQuery);
    }

    /**
     * get all pending movie download
     * @return
     */
    public List<MovieDownload> getAllPendingMovieDownload() {
        String usersSelectQuery = String.format("SELECT * FROM %s WHERE "+ KEY_TYPE_DL + " in (0,1)", TABLE_DOWNLOAD_MOVIE);
        return queryMovieDownloads(usersSelectQuery);
    }

    /**
     * get download success movie list
     * @return
     */
    public List<MovieDownload> getDownloadSuccMovieList() {
        String usersSelectQuery = "SELECT * FROM "+ TABLE_DOWNLOAD_MOVIE + " where " + KEY_TYPE_DL + " = ? and ("+KEY_RENTAL_END + " > " + KEY_RENTAL_START+ " or "+KEY_RENTAL_START +" = 0)";
        return queryMovieDownloads(usersSelectQuery, "3");
    }

    public MovieDownload getMovieDownload(int mvId) {
        MovieDownload movieDownload = null;
        String selectQuery = "SELECT  * FROM " + TABLE_DOWNLOAD_MOVIE + " WHERE " + KEY_MVID_FOREIGN + "=?";
        List<MovieDownload> movieDownloads = queryMovieDownloads(selectQuery, String.valueOf(mvId));
        if (!movieDownloads.isEmpty()) {
            movieDownload = movieDownloads.get(0);
        }
        return movieDownload;
    }

    /**
     * get wating for download movie
     * @return
     */
    public MovieDownload getMovieIsWaitingDownload() {
        MovieDownload movieDownload = null;
        String selectQuery = "SELECT  * FROM " + TABLE_DOWNLOAD_MOVIE + " WHERE " + KEY_TYPE_DL + "=?";
        List<MovieDownload> movieDownloads = queryMovieDownloads(selectQuery, String.valueOf(0));
        if (!movieDownloads.isEmpty()) {
            movieDownload = movieDownloads.get(0);
        }
        return movieDownload;
    }

    /**
     * get downloading and pending movie list
     * @return
     */
    public List<MovieDownload> getAllMovieDownloadRunOrPen() {
        String selectQuery = "SELECT  * FROM " + TABLE_DOWNLOAD_MOVIE + " WHERE " + KEY_STATUS_DL + "=?" + " OR " + KEY_STATUS_DL + "=?";
        return queryMovieDownloads(selectQuery, String.valueOf(DownloadManager.STATUS_RUNNING), String.valueOf(DownloadManager.STATUS_PENDING));
    }

    public void updateTypeMovieDownload(int mvId, int typeDownload, int typeCode) {
        //update typeDownloadMovie
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_TYPE_DL, typeDownload);
            cv.put(KEY_TYPE_CODE_RESPON, typeCode);
            db.update(TABLE_DOWNLOAD_MOVIE, cv, KEY_MVID_FOREIGN + " = ?", new String[]{String.valueOf(mvId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTypeNetworkDownload(int typeDownload, int oldType) {
        //update typeDownloadMovie
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_TYPE_DL, typeDownload);
            db.update(TABLE_DOWNLOAD_MOVIE, cv, KEY_TYPE_DL + " = ?", new String[]{String.valueOf(oldType)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTypeMovieDownloadAndIDPath(int mvId, int idDown, int typeDownload, String path) {
        //update typeDownloadMovie
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_TYPE_DL, typeDownload);
            cv.put(KEY_PATH_DL, path);
            cv.put(KEY_ID_DOWNLOAD, idDown);
            db.update(TABLE_DOWNLOAD_MOVIE, cv, KEY_MVID_FOREIGN + " = ?", new String[]{String.valueOf(mvId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update movie watch time
     * @param userId
     * @param mvId
     * @param timeContinue
     * @param duration
     */
    public void updateTimeContinue(String userId, int mvId, long timeContinue, long duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_TIME_CONTINUE, timeContinue);
            if (duration > 0) {
                duration = duration / 1000;
                cv.put(KEY_DURATION_DL, duration);
            }
            db.update(TABLE_DOWNLOAD_MOVIE, cv, KEY_MVID_FOREIGN + " = ?", new String[]{String.valueOf(mvId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update movie billing expired date
     * @param mvId
     * @param expireTime
     */
    public void updateBillingExpireTime(int mvId, long expireTime, long rentalStart, long rentalEnd) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_EXPIREON, expireTime);
            cv.put(KEY_RENTAL_START, rentalStart);
            cv.put(KEY_RENTAL_END, rentalEnd);
            db.update(TABLE_MOVIE, cv, KEY_MVID + " = ?", new String[]{String.valueOf(mvId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update movie download expired time
     * @param mvId
     * @param expireTime
     * @param rentalStart
     * @param rentalEnd
     */
    public void updateDownloadExpiredTime(int mvId, long expireTime, long rentalStart, long rentalEnd) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_EXPIRETIME_DL, expireTime);
            cv.put(KEY_RENTAL_START, rentalStart);
            cv.put(KEY_RENTAL_END, rentalEnd);
            db.update(TABLE_DOWNLOAD_MOVIE, cv, KEY_MVID_FOREIGN + " = ?", new String[]{String.valueOf(mvId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update movie download remain expired time
     * @param downloadList
     */
    public void updateDownloadRemainRentalTime(List<MovieDownload> downloadList) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            for (MovieDownload movieDownload: downloadList) {
                ContentValues cv = new ContentValues();
                cv.put(KEY_RENTAL_START, movieDownload.getRentalStart());
                cv.put(KEY_RENTAL_END, movieDownload.getRentalEnd());
                db.update(TABLE_DOWNLOAD_MOVIE, cv, KEY_MVID_FOREIGN + " = ?", new String[]{String.valueOf(movieDownload.getMvId())});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * update movie remain expired time
     * @param movieList
     */
    public void updateMovieRemainRentalTime(List<MovieInfo> movieList) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            for (MovieInfo movieInfo: movieList) {
                ContentValues cv = new ContentValues();
                cv.put(KEY_RENTAL_START, movieInfo.getRentalStart());
                cv.put(KEY_RENTAL_END, movieInfo.getRentalEnd());
                db.update(TABLE_MOVIE, cv, KEY_MVID + " = ?", new String[]{String.valueOf(movieInfo.getMovieId())});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void updateStatusDownload(int mvId, int status, String path, int reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_STATUS_DL, status);
            cv.put(KEY_PATH_DL, path);
            cv.put(KEY_REASON_DL, reason);
            db.update(TABLE_DOWNLOAD_MOVIE, cv, KEY_MVID_FOREIGN + " = ?", new String[]{String.valueOf(mvId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int deleteDownload(int idMv) {
        SQLiteDatabase db = getWritableDatabase();
        int result = 0;
        try {
            result = db.delete(TABLE_DOWNLOAD_MOVIE, KEY_MVID_FOREIGN + "= ?", new String[]{String.valueOf(idMv)});
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add row table parent Movie");
        }
        return result;
    }

    public void deleteAllDownload() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_DOWNLOAD_MOVIE, null, null);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all");
        }
    }

    public void deleteAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_GENRE, null, null);
            db.delete(TABLE_MOVIE, null, null);
            db.delete(TABLE_HOT_MOVIE, null, null);
            db.delete(TABLE_QUALITY, null, null);
            db.delete(TABLE_WIFI_ROUTER, null, null);
            db.delete(TABLE_DOWNLOAD_MOVIE, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all");
        } finally {
            db.endTransaction();
        }
    }

    private String insertList(List eList) {
        String jsonCartList = gson.toJson(eList);
        return jsonCartList;
    }

    private List readList(String string, Type type) {
        JsonElement jsonElement = parser.parse(string);
        ArrayList list = null;
        if (jsonElement instanceof JsonArray) {
            JsonArray ex = jsonElement.getAsJsonArray();
            int size = ex.size();
            list = new ArrayList(size);
            JsonElement json;
            for (int i = 0; i < size; ++i) {
                json = ex.get(i);
                list.add(gson.fromJson(json, type));
            }
        }
        return list;
    }
}
