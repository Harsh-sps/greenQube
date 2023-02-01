package com.aistream.greenqube.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.customs.CustomDialogNotMac;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.services.DownloadManager;
import com.aistream.greenqube.util.DialogCallBack;
import com.aistream.greenqube.util.OgleHelper;
import com.flixsys.ogle.fsplayersdk.FSPlayer;
import com.flixsys.ogle.sdk.DrmSdk;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.R;
import com.aistream.greenqube.chromecast.CastSessionListener;
import com.aistream.greenqube.chromecast.FlixsysChromcast;
import com.aistream.greenqube.mvp.rest.Config;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

/**
 * Created by NguyenQuocDat on 15/06/2018.
 */

public class PlayerActivity1 extends AppCompatActivity implements View.OnClickListener, CastSessionListener {

    OgleApplication mApplication;
    private SimpleExoPlayerView simpleExoPlayerView;
    TextView tvName;
    private DataBaseHelper dataBaseHelper;

    public boolean playerState = true;

    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";
    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";
    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
    public static final String DRM_LICENSE_URL = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";

    public static final String NAME_MOVIE = "name_movie";
    public static final String FILENAME_MOVIE = "fileName_movie";
    public static final String MOVIE_ID = "movie_id";
    public static final String TIME_CONTINUE = "time_continue";
    public static final String DOWNLOAD_COOKIE = "download_cookie";
    public String TAG = "PlayerActivity";
    //
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //    private FileServer fserver;
    private FSPlayer fsPlayer;
    private String fileName_movie;
    private String movieName;
    private int movieID;
    private long positionCurrentTimeStop = 0;
    private String playUrl;
    private Uri[] uris;
    private String downloadCookie;
    private SharedPreferences mPref;
    private boolean savePos = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        registerReceiver();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        } else {
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        mApplication = (OgleApplication) getApplicationContext();
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        dataBaseHelper = DataBaseHelper.getInstance();
        setContentView(R.layout.activity_player);

        //add google cast button
        MediaRouteButton mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        Log.i(TAG, "mMediaRouteButton: " + mMediaRouteButton);
        FlixsysChromcast.initButton(this, mMediaRouteButton);
        //regist chromecast session listener
        FlixsysChromcast.setCastSessionListener(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_VIEW.equals(action)) {
            uris = new Uri[]{intent.getData()};
            playUrl = uris[0].toString();
            fileName_movie = intent.getStringExtra(FILENAME_MOVIE);
            movieName = intent.getStringExtra(NAME_MOVIE);
            movieID = intent.getIntExtra(MOVIE_ID, 0);
            positionCurrentTimeStop = intent.getLongExtra(TIME_CONTINUE, 0);
            downloadCookie = intent.getStringExtra(DOWNLOAD_COOKIE);
        }

        new Thread() {
            @Override
            public void run() {
                boolean isSuscess = DrmSdk.FetchKey(PlayerActivity1.this, uris[0].toString().replace(fileName_movie + ".ts", fileName_movie + ".m3u8"), mApplication.getFMAToken());
                Log.i("FetchKey", "FetchKey = " + isSuscess + " folder = " + uris[0].toString().replace(fileName_movie + ".ts", fileName_movie + ".m3u8"));
            }
        }.start();

        initView();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "handler Message, Network Exception");
                    showMsg(getResources().getString(R.string.movie_download_incomplete));
                    break;
                case 2:
                    Log.d(TAG, "handler Message, play finish");
                    showMsg(getResources().getString(R.string.movie_play_complete));
                    break;
            }
        }
    };

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_SUCCESS);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_START);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == DownloadManager.ACTION_DOWNLOAD_SUCCESS) {
                String path = intent.getStringExtra("path").replace("file://", "");
                int mid = intent.getIntExtra("movieId", 0);
                Log.d(TAG, "download Movie success, path: "+path+", mid: "+mid+", movieID: "+movieID);
                if (playUrl.startsWith(Config.apiEndpointPublicDownM3U8) && mid == movieID) {
                    playUrl = path;
                    startPlay();
                }
            } else if (action == DownloadManager.ACTION_DOWNLOAD_START) {
                int mid = intent.getIntExtra("movieId", 0);
                if (playUrl.startsWith(Config.apiEndpointPublicDownM3U8) && mid == movieID) {
                    mApplication.showToast(getResources().getString(R.string.download_back), Gravity.CENTER);
                    mPref.edit().putBoolean("onlinePlay", false).commit();
                }
            } else if (action == ConnectivityManager.CONNECTIVITY_ACTION) {
                if (playUrl.startsWith(Config.apiEndpointPublicDownM3U8)) {
                    if (!Config.isCheckMac()) {
                        MovieDownload movieDownload = DataBaseHelper.getInstance().getMovieDownload(movieID);
                        if (movieDownload != null) {
                            int downloadSize = movieDownload.getDownloadSize();
                            if (downloadSize > 0) {
                                long fileSize = movieDownload.getFileSize() * 1L * 1024 * 1024;
                                long duration = movieDownload.getDuration() * 1000L;
                                long currPlayPos = 0;
                                if (fsPlayer != null) {
                                    currPlayPos = fsPlayer.GetCurrentPosition();
                                    if (fsPlayer.GetDuration() > 0) {
                                        duration = fsPlayer.GetDuration();
                                    }
                                }
                                double currDownloadPos = ((double)downloadSize / fileSize) * duration;
                                Log.d(TAG, "switch network, downloadSize: "+downloadSize+", fileSize: "+fileSize+", duration: "+duration+", currPlayPos: "+currPlayPos+", currDownloadPos: "+currDownloadPos);
                                if (currDownloadPos > currPlayPos) {
                                    playUrl = movieDownload.getPath().replace("file://", "");
                                    startPlay();
                                }
                            }
                        }
                    }
                }
            } else if (intent.getAction() == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                Log.i("CallPhone", "Call Phone");
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String state = extras.getString(TelephonyManager.EXTRA_STATE);
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        //pause here
                        Log.i("CallPhone", "Call Phone pause");
                        stopPlayer();
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        //pause here
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        //play here
                        Log.i("CallPhone", "Call Phone play");
                        startPlay();
                    }
                }
            }
        }
    };

    private void showMsg(String msg) {
        final CustomDialogNotMac dialogNotMac = CustomDialogNotMac.getInstance(this);
        if (!dialogNotMac.isShowing()) {
            OgleHelper.showMessage(this, msg, new DialogCallBack() {
                @Override
                public void ok() {
                    onBackPressed();
                }

                @Override
                public void cancel() {

                }
            });
        }
    }

    private void startPlay() {
        try {
            stopPlayer();
            //keep screen always on
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//PLAY FULL SCREEN
            if (playUrl.startsWith(Config.apiEndpointPublicDownM3U8)) {
                fsPlayer = new FSPlayer(this, playUrl, simpleExoPlayerView, positionCurrentTimeStop, mApplication.getFMAToken(), downloadCookie, handler);
            } else {
                savePos = true;
                String root = playUrl.replace("/" + fileName_movie + "/" + fileName_movie + ".ts", "");
                String fileName = "/" + fileName_movie + "/" + fileName_movie + ".m3u8";
                Log.d(TAG, "root: "+root+", fileName: "+fileName+", playerState: "+playerState+", positionCurrentTimeStop: "+positionCurrentTimeStop);
                fsPlayer = new FSPlayer(this, root, fileName, simpleExoPlayerView, positionCurrentTimeStop, playerState, handler);
            }
            mApplication.updateDownloadRentalTime(movieID, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();

        tvName = (TextView) findViewById(R.id.tvExoName);
        tvName.setText(movieName);
        LinearLayout llExoBack = (LinearLayout) findViewById(R.id.llExoBack);
        llExoBack.setOnClickListener(this);

        verifyStoragePermissions(this); // for sdcard accress
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llExoBack:
                Log.d(TAG, "onBackPressed");
                super.onBackPressed();
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);//exit PLAY FULL SCREEN
                break;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent");
        if (fsPlayer != null)
            fsPlayer.Stop();
        setIntent(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        stopPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        stopPlayer();
    }

    /**
     * stop player
     */
    private void stopPlayer() {
        try {
            if (fsPlayer != null) {
                positionCurrentTimeStop = fsPlayer.GetCurrentPosition();
                long duration = fsPlayer.GetDuration();
                String userId = mApplication.getUserId();
                Log.i(TAG, "Save Time Continue At Stop: " + positionCurrentTimeStop);
                if (positionCurrentTimeStop >= 0) {
                    dataBaseHelper.updateTimeContinue(userId, movieID, positionCurrentTimeStop, duration);
                }
                playerState = fsPlayer.IsPlaying();
                fsPlayer.Stop();
                fsPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //set screen off
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        startPlay();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //landscapse
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        } else {
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConnect() {
        if (fsPlayer != null)
            fsPlayer.Stop();
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        unregisterReceiver(broadcastReceiver);
        stopPlayer();
    }

//    int mSurfaceYDisplayRange = 0;
//    int mTouchY = 0;
//    int mTouchX = 0;
//    float mInitTouchY = 0f;
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        DisplayMetrics screen = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(screen);
//
//        if (mSurfaceYDisplayRange == 0)
//            mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);
//
//        float x_changed, y_changed;
//        if (mTouchX != -1f && mTouchY != -1f) {
//            y_changed = event.getRawY() - mTouchY;
//            x_changed = event.getRawX() - mTouchX;
//        } else {
//            x_changed = 0f;
//            y_changed = 0f;
//        }
//
//        float coef = Math.abs (y_changed / x_changed);
//        float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);
//        float delta_y = Math.max(1f,((mInitTouchY - event.getRawY()) / screen.xdpi + 0.5f)*2f);
//
//        switch (event.getAction())
//        {
//
//            case MotionEvent.ACTION_DOWN:
//                // Audio
//                mTouchY = mInitTouchY = event.getRawY();
//                mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                mTouchAction = TOUCH_NONE;
//                // Seek
//                mTouchX = event.getRawX();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                // No volume/brightness action if coef < 2 or a secondary display is connected
//                if (mTouchAction != TOUCH_SEEK && coef > 2)
//                {
//                    if (Math.abs(y_changed/mSurfaceYDisplayRange) < 0.05)
//                        return false;
//                    mTouchY = event.getRawY();
//                    mTouchX = event.getRawX();
//                    // Volume (Up or Down - Right side)
//                    if ( (int)mTouchX > (3 * screen.widthPixels / 5)){
//                        doVolumeTouch(y_changed);
//                    }
//                    // Brightness (Up or Down - Left side)
//                    if ( (int)mTouchX < (2 * screen.widthPixels / 5)){
//                        doBrightnessTouch(y_changed);
//                    }
//                } else {
//                    // Seek (Right or Left move)
//                    doSeekTouch(Math.round(delta_y), xgesturesize, false);
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                // Seek
//                if (mTouchAction == TOUCH_SEEK)
//                    doSeekTouch(Math.round(delta_y), xgesturesize, true);
//                mTouchX = -1f;
//                mTouchY = -1f;
//                break;
//        }
//        return mTouchAction != TOUCH_NONE;
//    }
}
