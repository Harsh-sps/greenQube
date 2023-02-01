package com.aistream.greenqube.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.LoginActivity;
import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.R;
import com.aistream.greenqube.adapter.MainTabFragmentAdapter;
import com.aistream.greenqube.chromecast.CastSessionListener;
import com.aistream.greenqube.chromecast.FlixsysChromcast;
import com.aistream.greenqube.customs.CustomDialogChargeMovieDownLoad;
import com.aistream.greenqube.customs.CustomDialog_MovieDetail;
import com.aistream.greenqube.customs.CustomLoading;
import com.aistream.greenqube.customs.CustomViewPager;
import com.aistream.greenqube.customs.ItemBackClick;
import com.aistream.greenqube.fragment.FragmentDownload;
import com.aistream.greenqube.fragment.FragmentHotspot;
import com.aistream.greenqube.fragment.FragmentSearch;
import com.aistream.greenqube.fragment.Fragment_Account;
import com.aistream.greenqube.fragment.Fragment_Library;
import com.aistream.greenqube.mvp.database.DataBaseHelper;
import com.aistream.greenqube.mvp.database.ReadWriteFile;
import com.aistream.greenqube.mvp.model.DownloadData;
import com.aistream.greenqube.mvp.model.DownloadResult;
import com.aistream.greenqube.mvp.model.ItemBackupData;
import com.aistream.greenqube.mvp.model.MovieDownload;
import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.mvp.presenter.PresenterMainImp;
import com.aistream.greenqube.mvp.rest.APICall;
import com.aistream.greenqube.mvp.rest.DataLoader;
import com.aistream.greenqube.mvp.view.ViewMain;
import com.aistream.greenqube.receiver.StorageEventListener;
import com.aistream.greenqube.services.DownloadManager;
import com.aistream.greenqube.services.downloads.DownloadReceiver;
import com.aistream.greenqube.util.DialogCallBack;
import com.aistream.greenqube.util.LocationHelper;
import com.aistream.greenqube.util.MyLocationListener;
import com.aistream.greenqube.util.OgleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ViewMain, CastSessionListener, StorageEventListener {
    private LinearLayout ll_main;
    private FrameLayout btn_back;
    private TextView toolbar_title;
    private CustomViewPager main_viewpager;
    private TabLayout main_tab;
    private FragmentDownload mDownload;
    private FragmentHotspot mHotspot;
    private FragmentSearch mSearch;
    private Fragment_Account mAccount;
    private MainTabFragmentAdapter mainTabFragmentAdapter;
    private TextView txt_notification;
    private ImageView iv_edit;
    private FrameLayout operate_bar;
    private TextView tv_connnotification;
    private PresenterMainImp presenterMainImp;
    private OgleApplication ogleApplication;
    //
    private Fragment_Library fragment_library;
    private FrameLayout main_toolbar;

    private LinearLayout ll_tabbar;
    private TabLayout.Tab tabLibraryNew;
    DownloadReceiver downloadReceiver;
    SharedPreferences mPref;

    //last play sample
    private UriSample lastPlaySample;

    private String TAG = "OSVERSION";
    private LocationHelper locationHelper;
    private DataLoader dataLoader;
    private List<Fragment> fragmentList = new ArrayList<>();
    private boolean isFront = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init chromecast context
        FlixsysChromcast.init(this, savedInstanceState);
        APICall.viewMain = this;

        downloadReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("com.flixsys.soflix.reconnect");
        registerReceiver(downloadReceiver, filter);
        setContentView(FlixsysChromcast.isCastAvailable() ?
                R.layout.activity_main : R.layout.activity_main_without_control);

        //regist chromecast session listener
        FlixsysChromcast.setCastSessionListener(this);

        ogleApplication = (OgleApplication) getApplicationContext();
        FlixsysChromcast.mApplication = ogleApplication;
        ogleApplication.setStorageEventListener(this);

        presenterMainImp = new PresenterMainImp(this, this);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
//        FirebaseMessaging.getInstance().subscribeToTopic("flixsys-1000");
        Log.i("MainActivity", "onCreate");
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
        List<MovieDownload> array = DataBaseHelper.getInstance().getAllMovieDownloadRunOrPen();
        List<DownloadResult> results = presenterMainImp.getMovieDownloadResults(array);
        for (DownloadResult downloadResult: results) {
            DataBaseHelper.getInstance().updateStatusDownload(downloadResult.getMvId(),
                    downloadResult.getStatus(), downloadResult.getPath(), downloadResult.getReason());
        }

        //get screen height
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenHeight = metric.heightPixels;
        mPref.edit().putInt("ScreenHeight", screenHeight).commit();

        initView();
        handler.postDelayed(Timer_Tick, 10000);
        presenterMainImp.registerReceiver();
        presenterMainImp.checkConnection(this, false);
    }

    /**
     * check app location enabled
     */
    @Override
    public boolean checkLocationEnabled() {
        if (locationHelper == null) {
            locationHelper = new LocationHelper(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!locationHelper.isLocationEnabled()) {
                OgleHelper.showMessage(this, "In order to keep movie download successfully, please open your location to processed.", new DialogCallBack() {
                    @Override
                    public void ok() {
                        locationHelper.openLocationSetting(MainActivity.this);
                    }

                    @Override
                    public void cancel() {
                    }
                });
                return false;
            }
        }
        locationHelper.removeLocationUpdatesListener();
        locationHelper.setLocationListener(new MyLocationListener() {
            @Override
            public void updateLocation(Location location) {
                if (location != null) {
                    mPref.edit().putFloat("longitude", (float) location.getLongitude())
                            .putFloat("latitude", (float) location.getLatitude())
                            .commit();

                    if (mHotspot != null && main_viewpager.getCurrentItem() == 2) {
                        mHotspot.loadData();
                    }
                }
            }

            @Override
            public void updateStatus(String provider, int status, Bundle extras) {
            }
        });
        return true;
    }



    private Handler handler = new Handler();
    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            count++;
            if (count == 360) {
                count = 0;
                List<DownloadData> downloadDataList = ReadWriteFile.readDownloadDataToFile(OgleApplication.keyRegister, getApplicationContext());
                ItemBackupData itemBackupData = new ItemBackupData();
////                itemBackupData.endExpiryTimeToLoginsetDownloadData(presenterMainImp.downloadDataList);
////                itemBackupData.setMoviePlaybackData(presenterMainImp.moviePlaybackDataList);
////                itemBackupData.setErrorData(presenterMainImp.errorDataList);
                itemBackupData.setDownloadData(downloadDataList);
                presenterMainImp.backUpDataOgle(itemBackupData);
            }

            //update purchased movie remain rental time
            presenterMainImp.updateMovieRemainRentalTime();

            //update download remain rental time
            presenterMainImp.updateDownloadRemainRentalTime();

            long next_billing_start = mPref.getLong("next_billing_start", 0);
            long next_billing_end = mPref.getLong("next_billing_end", 0);
            Log.d(TAG, "next_billing_start: "+next_billing_start+", next_billing_end: "+next_billing_end);
            if (next_billing_start < next_billing_end && next_billing_end > 0) {
                long runTime = SystemClock.elapsedRealtime();
                long diffTime = next_billing_end - runTime;
                SharedPreferences.Editor editor = mPref.edit();
                if (diffTime <= 0) {
                    editor.putLong("next_billing_start", next_billing_end);
                } else {
                    editor.putLong("next_billing_start", runTime);
                    editor.putLong("next_billing_end", runTime + diffTime);
                }
                editor.commit();
            }

            if (count % 10 == 0) {
                presenterMainImp.detectFAG(0);
            }

            Log.i("RunUITimer", "count: " + count);
//            presenterMainImp.autoConnectWifiOgle();
            handler.postDelayed(Timer_Tick, 60000);
        }
    };
    int count;

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainActivity", "onStart");
    }

    @Override
    protected void onResume() {
        Log.i("MainActivity", "onResume");
        //add session manager listener
        FlixsysChromcast.addSessionManagerListener(this);
        super.onResume();
        isFront = true;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

        if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof Fragment_Library
                                && fragment_library != null) {
            fragment_library.loadDataLibrary();
        }

        //refresh data
        if (mPref.getBoolean("refresh", false)) {
            if (presenterMainImp != null) presenterMainImp.refreshData();
            if (mAccount != null) {
                mAccount.refreshData();
            }
        }

        if (movieDetail != null && movieDetail.isShowing()) {
            movieDetail.refreshData();
        }
        //check app upgrade
        ogleApplication.checkAppUpgrade(this, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
    }

    @Override
    protected void onStop() {
        Log.i("MainActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("MainActivity", "onDestroy");
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
        presenterMainImp.unregisterReceiver();
        handler.removeCallbacks(Timer_Tick);
    }

    private void initView() {
        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        operate_bar = (FrameLayout) findViewById(R.id.operate_bar);
        setHideStatusBar(ll_main);
        main_tab = (TabLayout) findViewById(R.id.main_tab);
        main_viewpager = (CustomViewPager) findViewById(R.id.main_viewpager);
        main_viewpager.setPagingEnabled(false);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        btn_back = (FrameLayout) findViewById(R.id.btn_back);
        main_toolbar = (FrameLayout) findViewById(R.id.main_toolbar);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        tv_connnotification = (TextView) findViewById(R.id.tv_connnotification);
        ll_tabbar = (LinearLayout) findViewById(R.id.ll_tabbar);

        fragment_library = new Fragment_Library(presenterMainImp, this);
        mDownload = new FragmentDownload(presenterMainImp, this);
        mHotspot = new FragmentHotspot(presenterMainImp);
        mSearch = new FragmentSearch(presenterMainImp);
        mAccount = new Fragment_Account(presenterMainImp, this);

        fragmentList.clear();
        fragmentList.add(fragment_library);
        fragmentList.add(mDownload);
        fragmentList.add(mHotspot);
        fragmentList.add(mAccount);

        //tab library
        addFragmentTab(R.drawable.main_tab_library_selector, R.string.main_title_librarynew, false);
        main_toolbar.setVisibility(View.GONE);

        //tab download
        addFragmentTab(R.drawable.main_tab_download_selector, R.string.main_title_download, true);
        //tab hotspot
        addFragmentTab(R.drawable.main_tab_hotspot_selector, R.string.main_title_hotspot, false);
        //tab account
        addFragmentTab(R.drawable.main_tab_account_selector, R.string.main_title_account, false);

        mainTabFragmentAdapter = new MainTabFragmentAdapter(getSupportFragmentManager(), fragmentList);
        main_viewpager.setAdapter(mainTabFragmentAdapter);
        main_viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(main_tab));
        main_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                iv_edit.setVisibility(View.GONE);
                operate_bar.setVisibility(View.GONE);
                main_toolbar.setVisibility(View.GONE);
                tv_connnotification.setVisibility(View.GONE);
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                    case 2:
                    case 3:
                        main_toolbar.setVisibility(View.VISIBLE);
                        break;
                }
                main_viewpager.setCurrentItem(tab.getPosition());
                toolbar_title.setText((CharSequence) tab.getTag());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        main_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fragment_library.loadDataLibrary();
                } else if (position == 1) {
                    mDownload.loadData();
                } else if (position == 2) {
                } else if (position == 3) {
                    mAccount.loadData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addFragmentTab(int resID, int tagID, boolean flagDownload) {
        String tag = getResources().getString(tagID);
        TabLayout.Tab newTab = main_tab.newTab();
        newTab.setCustomView(getTabIconView(resID, tag, flagDownload));
        newTab.setTag(tag);
        main_tab.addTab(newTab);
    }

    private void setHideStatusBar(LinearLayout ll_main) {
        ll_main.setPadding(0, statusBarHeight(getResources()), 0, 0);
    }

    private int statusBarHeight(android.content.res.Resources res) {
        return (int) (24 * res.getDisplayMetrics().density);
    }

    private View getTabIconView(int resID, String title, boolean flagDownload) {
        View view = LayoutInflater.from(this).inflate(R.layout.main_item_tab, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView txt_title = (TextView) view.findViewById(R.id.title);
        icon.setBackgroundResource(resID);
        txt_title.setText(title);
        if (flagDownload) {
            txt_notification = (TextView) view.findViewById(R.id.txt_notification);
        }
        return view;
    }

    @Override
    public void showLoading() {
        CustomLoading.getInstance().showProgress(MainActivity.this);
    }

    @Override
    public void hideLoading() {
        CustomLoading.getInstance().hideProgress();
    }

    @Override
    public void backClickToSearch(int pageNume) {
        btn_back.setVisibility(View.GONE);
        main_tab.setVisibility(View.VISIBLE);
        ((ItemBackClick) mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem())).backClick();
        main_viewpager.setCurrentItem(pageNume);
    }

    @Override
    public void showHideToolBar(int mode) {
        if (mode == 0) {
            main_tab.setVisibility(View.GONE);
            main_toolbar.setVisibility(View.GONE);
        } else if (mode == 1) {
            main_tab.setVisibility(View.VISIBLE);
            main_toolbar.setVisibility(View.VISIBLE);
            mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem());
            main_viewpager.setCurrentItem(3);
        } else if (mode == 2) {
            main_tab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void clickToPlayMovie(int mvid, String name, String url, String fileName, long timeContinue, String imgurl) {
        lastPlaySample = new UriSample(mvid, name, imgurl, fileName, timeContinue, null, null, null,
                false, url, null, null);
        if (ogleApplication.chromcastConnected) {
            changeToPlayChromeCast();
        } else {
            startActivity(lastPlaySample.buildIntent(this));
        }
    }

    @Override
    public void onlinePlayMovie(int mvid, String name, String url, String fileName, String cookie, long timeContinue, String imgurl) {
        lastPlaySample = new UriSample(mvid, name, imgurl, fileName, timeContinue, null, null, null,
                false, url, null, cookie);
        startActivity(lastPlaySample.buildIntent(this));
    }

    private void showDialog() {
        final CustomDialogChargeMovieDownLoad dgExit = CustomDialogChargeMovieDownLoad.getInstance(this);
        dgExit.withTitle(getResources().getString(R.string.titleexitapp)).isCancelableOnTouchOutside(false)
                .withBtnCancelText("Cancel").setOkClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgExit.dismiss();
                System.exit(0);
                finish();
            }
        }).setNoClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgExit.dismiss();

            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        if (btn_back.getVisibility() == View.VISIBLE) {
        } else {
            if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof Fragment_Library) {
                if (fragment_library != null && fragment_library.checkVisbleFrameSearch()) {
                    fragment_library.onBackpress();
                } else {
                    showDialog();
                }
            } else {
                showDialog();
            }
        }
//        if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof Fragment_Account) {
//            mAccount.accountMore.onBackFragmentAccount();
//        }
    }

    @Override
    public void onConnect() {
        Log.d("ChromcastSesion", "onConnect");
        changeToPlayChromeCast();
        ogleApplication.chromcastConnected = true;
        showToasApp(getResources().getString(R.string.chromecast_connect), Gravity.CENTER);
    }

    /**
     * start play chromecast
     */
    public void changeToPlayChromeCast() {
        if (lastPlaySample != null) {
            try {
                FlixsysChromcast.startCast(lastPlaySample.mvId,
                        lastPlaySample.name,
                        lastPlaySample.name,
                        lastPlaySample.rootDir,
                        lastPlaySample.filePath,
                        lastPlaySample.imgurl,
                        lastPlaySample.timeContinue,
                        true,
                        mPref.getString("stringToken", ""));
                showToasApp(getResources().getString(R.string.start_cast), Gravity.CENTER);
            } catch (Exception e) {
                e.printStackTrace();
                showToasApp("cast fail!", Gravity.CENTER);
            }
        }
    }

    @Override
    public void onDisconnect() {
        Log.d("ChromcastSesion", "onDisconnect");
        if (ogleApplication.chromcastConnected) {
            showToasApp(getResources().getString(R.string.chromecast_disconnect), Gravity.CENTER);
        }
        ogleApplication.chromcastConnected = false;
    }

    @Override
    public void onStorageStateChanged(int state) {
        if (main_viewpager != null
                && mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof FragmentDownload) {
            mDownload.onStorageStateChanged(state);
        }
    }

    private abstract static class Sample {
        public final int mvId;
        public final String name;
        public final String imgurl;
        public final String fileName;
        public final long timeContinue;
        public final boolean preferExtensionDecoders;
        public final UUID drmSchemeUuid;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;
        public final String downloadCookie;

        public Sample(int mvid, String name, String imgurl, String fileName, long timeContinue, UUID drmSchemeUuid, String drmLicenseUrl,
                      String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String cookie) {
            this.mvId = mvid;
            this.name = name;
            this.imgurl = imgurl;
            this.fileName = fileName;
            this.timeContinue = timeContinue;
            this.drmSchemeUuid = drmSchemeUuid;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.preferExtensionDecoders = preferExtensionDecoders;
            this.downloadCookie = cookie;
        }

        public Intent buildIntent(Context context) {
            Intent intent = new Intent(context, PlayerActivity1.class);
            intent.putExtra(PlayerActivity1.MOVIE_ID, mvId);
            intent.putExtra(PlayerActivity1.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
            intent.putExtra(PlayerActivity1.NAME_MOVIE, name);
            intent.putExtra(PlayerActivity1.FILENAME_MOVIE, fileName);
            intent.putExtra(PlayerActivity1.TIME_CONTINUE, timeContinue);
            if (drmSchemeUuid != null) {
                intent.putExtra(PlayerActivity1.DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
                intent.putExtra(PlayerActivity1.DRM_LICENSE_URL, drmLicenseUrl);
                intent.putExtra(PlayerActivity1.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
            }
            intent.putExtra(PlayerActivity1.DOWNLOAD_COOKIE, downloadCookie);
            return intent;
        }

    }

    private static final class UriSample extends Sample {

        public final String uri;
        public final String extension;
        public final boolean preferExtensionDecoders;
        public final String rootDir;
        public final String filePath;

        public UriSample(int mvid, String name, String imgurl, String fileName, long timeContinue, UUID drmSchemeUuid, String drmLicenseUrl,
                         String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,
                         String extension, String cookie) {
            super(mvid, name, imgurl, fileName, timeContinue, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders, cookie);
            this.uri = uri;
            this.extension = extension;
            this.preferExtensionDecoders = preferExtensionDecoders;

            if (!TextUtils.isEmpty(fileName)) {
                this.rootDir = uri.replace("/" + fileName + "/" + fileName + ".ts", "");
                this.filePath = "/" + fileName + "/" + fileName + ".m3u8";
            } else {
                this.rootDir = "";
                this.filePath = "";
            }
        }

        @Override
        public Intent buildIntent(Context context) {
            return super.buildIntent(context)
                    .setData(Uri.parse(uri))
                    .putExtra(PlayerActivity1.EXTENSION_EXTRA, extension)
                    .putExtra(PlayerActivity1.PREFER_EXTENSION_DECODERS, preferExtensionDecoders)
                    .setAction(PlayerActivity1.ACTION_VIEW);
        }

    }

    @Override
    public void updateStatusDownload(int mvId, long downloadId, int status, String path, int reason) {
        presenterMainImp.updateMovieDownloadStatus(mvId, status, path, reason);
        if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof Fragment_Library) {
            if (movieDetail != null && movieDetail.isShowing() && movieDetail.isMovieMatch(mvId)) {
                movieDetail.updateDownloadStatus(status);
            }
        } else if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof FragmentDownload) {
            mDownload.updateStatus(mvId, downloadId, status, path, reason);
        }
        updateDownloadBar();
    }

    @Override
    public void updateProgressDownload(int mvId, long downloadId, int mTotalTotal, int mTotalCurrent, double speed) {
        if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof FragmentDownload) {
            mDownload.updateProgress(mvId, mTotalTotal, mTotalCurrent, speed);
        } else if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof Fragment_Library) {
            if (movieDetail != null && movieDetail.isShowing() && movieDetail.isMovieMatch(mvId)) {
                movieDetail.updateDownloadStatus(DownloadManager.STATUS_RUNNING);
            }
        }
    }

    @Override
    public void deleteDataDownload(MovieDownload... movieDownloads) {
        if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof FragmentDownload) {
            mDownload.deleteDownload(movieDownloads);
        }
        updateDownloadBar();
    }

    @Override
    public void refreshMovieDownloads() {
        if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof FragmentDownload) {
            mDownload.showDownloadList();
        }
        updateDownloadBar();
    }

    @Override
    public void updateDownloadBar() {
        int pendingCount = presenterMainImp.getMovieDownloadCount();
        txt_notification.setText(String.valueOf(pendingCount));
        if (pendingCount == 0) {
            txt_notification.setVisibility(View.GONE);
            txt_notification.setText("0");
        } else {
            txt_notification.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateErrorCodeAtDialogIsShow(int mvID, int typeDownload, int typeCodeResponse) {
        if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof Fragment_Library) {
            if (movieDetail != null && movieDetail.isShowing() && movieDetail.isMovieMatch(mvID)) {
                movieDetail.updateStatus();
            }
        } else if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof FragmentDownload) {
            mDownload.updateStatusFail(mvID, typeDownload, typeCodeResponse);
        }
    }

    @Override
    public void endExpiryTimeToLogin() {
        Log.i("MainActivity", "ExpiryTime");
        presenterMainImp.refreshToken(null);
    }

    @Override
    public void resetApp() {
        presenterMainImp.deleteAllMovieDownload();
        DataBaseHelper.getInstance().deleteAllData();
        ogleApplication.clearApplicationData();
        ogleApplication.deleteCache();
        ogleApplication.deleteFolder();
        logout();
    }

    private void logout() {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mPref.edit().putLong("tokenExpiryTime", 0)
                .putString("stringToken", "").commit();

        Intent a = new Intent(this, LoginActivity.class);
        startActivity(a);
        finish();
        System.exit(1);
    }

    @Override
    public void verifyAccount() {
        OgleHelper.showDialog(this, getResources().getString(R.string.no_rights),
                "Verify Now", "Cancel", new DialogCallBack() {
                    @Override
                    public void ok() {
                        showLoginPage();
                    }

                    @Override
                    public void cancel() {
                    }
                });
    }

    @Override
    public void showLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("logout", true);
        startActivityForResult(intent, 11);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationHelper.REQUEST_LOCATION_CODE:
                if (checkLocationEnabled()) {
                    presenterMainImp.checkConnection(this, true);
                    Intent intent = new Intent();
                    intent.setAction("com.flixsys.soflix.reconnect");
                    sendBroadcast(intent);
                }
                break;
        }
    }

    @Override
    public void deleteDownloadManager() {
        presenterMainImp.deleteAllMovieDownload();
    }


    @Override
    public void goneIconAndStt(int mvId, int type, String msg) {
        if (mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof Fragment_Library) {
            if (movieDetail != null && movieDetail.isShowing() && movieDetail.isMovieMatch(mvId)) {
                movieDetail.goneIconAndStt(type, msg);
            }
        }
    }

    @Override
    public void updateSttAtChangeNetWork(int flagNetWork) {
        if (main_viewpager != null
                && mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof FragmentDownload) {
            mDownload.updateSttAtChangeNetWork(flagNetWork);
        }
    }

    @Override
    public void showHideNoConnection(int flagNetWork) {
    }

    @Override
    public void showMovieDetail(MovieInfo movieInfo) {
        if (fragment_library != null) {
            movieDetail = new CustomDialog_MovieDetail(this, this, presenterMainImp, movieInfo);
            movieDetail.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            movieDetail.show();
        }
    }

    CustomDialog_MovieDetail movieDetail;

    @Override
    public void onClickMoreLikeThis(MovieInfo movieInfo) {
        movieDetail.loadDataDetail(movieInfo);
    }

    @Override
    public void hideDialogMoviePremiumDetail() {
        if (movieDetail != null && movieDetail.isShowing()) {
            movieDetail.dismiss();
        }
    }

    @Override
    public void showOrHideTextNotification(int type) {
        if (tv_connnotification != null) {
            if (type == 0) {
                tv_connnotification.setVisibility(View.VISIBLE);
            } else {
                tv_connnotification.setVisibility(View.GONE);
            }
        } else {
            tv_connnotification = (TextView) findViewById(R.id.tv_connnotification);
            if (type == 0) {
                tv_connnotification.setVisibility(View.VISIBLE);
            } else {
                tv_connnotification.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setChangeTextNotification(int type) {
        if (main_viewpager != null
                && mainTabFragmentAdapter.getItem(main_viewpager.getCurrentItem()) instanceof FragmentDownload) {
            mDownload.setWifiStateNotification();
        }
    }

    @Override
    public void showMovieVIP(TextView view, MovieInfo movieInfo) {
        view.setVisibility(View.GONE);
        if (movieInfo.getVip() == 1) {
            view.setVisibility(View.VISIBLE);
            view.setText("VIP");
            view.setBackgroundColor(getResources().getColor(R.color.gold));
        } else if (movieInfo.getType() == 2) {
            view.setVisibility(View.VISIBLE);
            view.setText("Pay");
            view.setBackgroundColor(getResources().getColor(R.color.sss));
        }
    }

    @Override
    public void showToasApp(String msg, int gravity) {
        if (isFront) {
            ogleApplication.showToast(msg, gravity);
        }
    }

    @Override
    public void showTabBar() {
        ll_tabbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTabBar() {
        ll_tabbar.setVisibility(View.GONE);
    }

    @Override
    public View getView() {
        return ll_main;
    }

    @Override
    public void refreshData() {
        if (fragment_library != null) {
            fragment_library.refreshData();
        }
    }
}
