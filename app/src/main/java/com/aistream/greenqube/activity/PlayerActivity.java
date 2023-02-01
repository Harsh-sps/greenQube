package com.aistream.greenqube.activity;

import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.flixsys.soflix.OgleApplication;
//import com.flixsys.soflix.R;
//import com.google.android.exoplayer2.C;
//import com.google.android.exoplayer2.DefaultRenderersFactory;
//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.ExoPlayerFactory;
//import com.google.android.exoplayer2.PlaybackParameters;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.Timeline;
//import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
//import com.google.android.exoplayer2.drm.DrmSessionManager;
//import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
//import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
//import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
//import com.google.android.exoplayer2.drm.UnsupportedDrmException;
//import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
//import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
//import com.google.android.exoplayer2.source.ExtractorMediaSource;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.TrackGroupArray;
//import com.google.android.exoplayer2.source.dash.DashMediaSource;
//import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
//import com.google.android.exoplayer2.source.hls.HlsMediaSource;
//import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
//import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//import com.google.android.exoplayer2.trackselection.TrackSelection;
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
//import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
//import com.google.android.exoplayer2.upstream.HttpDataSource;
//import com.google.android.exoplayer2.util.Util;
//
//import java.net.CookieHandler;
//import java.net.CookieManager;
//import java.net.CookiePolicy;
//import java.util.UUID;


public class PlayerActivity extends Activity {
//        ExoPlayer.EventListener, View.OnClickListener {

//    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
//    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
//    public static final String DRM_LICENSE_URL = "drm_license_url";
//    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";
//    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";
//
//    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
//    public static final String EXTENSION_EXTRA = "extension";
//
//    public static final String ACTION_VIEW_LIST =
//            "com.google.android.exoplayer.demo.action.VIEW_LIST";
//    public static final String URI_LIST_EXTRA = "uri_list";
//    public static final String EXTENSION_LIST_EXTRA = "extension_list";
//
//
//    private boolean shouldAutoPlay;
//    private int resumeWindow;
//    private long resumePosition;
//    private DataSource.Factory mediaDataSourceFactory;
//
//    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
//    private static final CookieManager DEFAULT_COOKIE_MANAGER;
//
//    static {
//        DEFAULT_COOKIE_MANAGER = new CookieManager();
//        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
//    }
//
//    private SimpleExoPlayerView simpleExoPlayerView;
//    private SimpleExoPlayer player;
//    private DefaultTrackSelector trackSelector;
//    private TrackSelectionHelper trackSelectionHelper;
//    private TrackGroupArray lastSeenTrackGroupArray;
//    private EventLogger eventLogger;
//
//    public static final String DRM_MULTI_SESSION = "drm_multi_session";
//
//    private boolean needRetrySource;
//
//    TextView tvName;
//    private Handler mainHandler;
//
////    private FileServer fserver;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        getWindow().getDecorView().setSystemUiVisibility(flags);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        // This work only for android 4.4+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            final View decorView = getWindow().getDecorView();
//            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                        decorView.setSystemUiVisibility(flags);
//                    }
//                }
//            });
//        }
//
//        shouldAutoPlay = true;
//        clearResumePosition();
//        mediaDataSourceFactory = buildDataSourceFactory(true);
//        mainHandler = new Handler();
//        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
//            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
//        }
//
//        setContentView(R.layout.activity_player);
//
//        initView();
//
////        fserver = new FileServer(8080, listStorage.get(0) + "/downloads/movies");
////        try {
////            fserver.start();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//
//    private void initView() {
//        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
//        simpleExoPlayerView.requestFocus();
//
//        tvName = (TextView) findViewById(R.id.tvExoName);
//        LinearLayout llExoBack = (LinearLayout) findViewById(R.id.llExoBack);
//        llExoBack.setOnClickListener(this);
//    }
//
//    @Override
//    public void onNewIntent(Intent intent) {
//        releasePlayer();
//        shouldAutoPlay = true;
//        clearResumePosition();
//        setIntent(intent);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (Util.SDK_INT > 23) {
//            initializePlayer();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if ((Util.SDK_INT <= 23 || player == null)) {
//            initializePlayer();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (Util.SDK_INT <= 23) {
//            releasePlayer();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (Util.SDK_INT > 23) {
//            releasePlayer();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            initializePlayer();
//        } else {
//            finish();
//        }
//    }
//
////    private void initializePlayer() {
////        Intent intent = getIntent();
////        boolean needNewPlayer = player == null;
////        if (needNewPlayer) {
//////            boolean preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS, false);
//////            UUID drmSchemeUuid = intent.hasExtra(DRM_SCHEME_UUID_EXTRA)
//////                    ? UUID.fromString(intent.getStringExtra(DRM_SCHEME_UUID_EXTRA)) : null;
//////            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
//////            if (drmSchemeUuid != null) {
//////                String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL);
//////                String[] keyRequestPropertiesArray = intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES);
//////                try {
//////                    drmSessionManager = buildDrmSessionManager(drmSchemeUuid, drmLicenseUrl,
//////                            keyRequestPropertiesArray);
//////                } catch (UnsupportedDrmException e) {
//////                    int errorStringId = Util.SDK_INT < 18 ? R.string.error_drm_not_supported
//////                            : (e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
//////                            ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
//////                    showToast(errorStringId);
//////                    return;
//////                }
//////            }
//////
//////            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
//////                    ((HostPostApplication) getApplication()).useExtensionRenderers()
//////                            ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
//////                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
//////                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
//////            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,
//////                    drmSessionManager, extensionRendererMode);
//////
//////            TrackSelection.Factory videoTrackSelectionFactory =
//////                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
//////            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//////            trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);
//////            lastSeenTrackGroupArray = null;
////
////            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector());
////            player.addListener(this);
////
//////            eventLogger = new EventLogger(trackSelector);
//////            player.addListener(eventLogger);
//////            player.setAudioDebugListener(eventLogger);
//////            player.setVideoDebugListener(eventLogger);
//////            player.setMetadataOutput(eventLogger);
////
////            simpleExoPlayerView.setPlayer(player);
////            player.setPlayWhenReady(shouldAutoPlay);
//////            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
//////            debugViewHelper.start();
////        }
////        if (needNewPlayer) {
//////            String action = intent.getAction();
////            Uri uri = intent.getData();
////
////            tvName.setText(intent.getStringExtra("nameMovie"));
//////            Uri[] uris;
//////            String[] extensions;
//////            if (ACTION_VIEW.equals(action)) {
//////                uris = new Uri[]{intent.getData()};
//////                extensions = new String[]{intent.getStringExtra(EXTENSION_EXTRA)};
//////            } else if (ACTION_VIEW_LIST.equals(action)) {
//////                String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
//////                uris = new Uri[uriStrings.length];
//////                for (int i = 0; i < uriStrings.length; i++) {
//////                    uris[i] = Uri.parse(uriStrings[i]);
//////                }
//////                extensions = intent.getStringArrayExtra(EXTENSION_LIST_EXTRA);
//////                if (extensions == null) {
//////                    extensions = new String[uriStrings.length];
//////                }
//////            } else {
//////                showToast(getString(R.string.unexpected_intent_action, action));
//////                return;
//////            }
//////            if (Util.maybeRequestReadExternalStoragePermission(this, uris)) {
//////                // The player will be reinitialized if the permission is granted.
//////                return;
//////            }
//////            MediaSource[] mediaSources = new MediaSource[uris.length];
//////            for (int i = 0; i < uris.length; i++) {
//////                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
//////            }
//////            MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
//////                    : new ConcatenatingMediaSource(mediaSources);
////            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
////
//////            File filepath = Environment.getExternalStorageDirectory();
//////            File dir = new File(filepath.getAbsolutePath() + "/apk/20170330_172647.mp4");
//////            Uri uri = Uri.fromFile(dir);
////            if (Util.maybeRequestReadExternalStoragePermission(this, uri)) {
////                // The player will be reinitialized if the permission is granted.
////                return;
////            }
////            DataSpec dataSpec = new DataSpec(uri);
////            final FileDataSource fileDataSource = new FileDataSource();
////            try {
////                fileDataSource.open(dataSpec);
////            } catch (FileDataSource.FileDataSourceException e) {
////                Toast.makeText(PlayerActivity.this, "File not Found", Toast.LENGTH_SHORT).show();
////                finish();
////                e.printStackTrace();
////            }
////
////            DataSource.Factory factory = new DataSource.Factory() {
////                @Override
////                public DataSource createDataSource() {
////                    return fileDataSource;
////                }
////            };
////            MediaSource mediaSource = new ExtractorMediaSource(fileDataSource.getUri(),
////                    mediaDataSourceFactory, new DefaultExtractorsFactory(), null, null);
////
////            if (haveResumePosition) {
////                player.seekTo(resumeWindow, resumePosition);
////            }
////            player.prepare(mediaSource, !haveResumePosition, false);
//////            needRetrySource = false;
//////            updateButtonVisibilities();
////        }
////    }
//
//    private void initializePlayer() {
//        Intent intent = getIntent();
//        boolean needNewPlayer = player == null;
//        if (needNewPlayer) {
//            boolean preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS, false);
//            UUID drmSchemeUuid = intent.hasExtra(DRM_SCHEME_UUID_EXTRA)
//                    ? UUID.fromString(intent.getStringExtra(DRM_SCHEME_UUID_EXTRA)) : null;
//            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
//            if (drmSchemeUuid != null) {
//                String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL);
//                String[] keyRequestPropertiesArray = intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES);
//                try {
//                    drmSessionManager = buildDrmSessionManager(drmSchemeUuid, drmLicenseUrl,
//                            keyRequestPropertiesArray);
//                } catch (UnsupportedDrmException e) {
//                    int errorStringId = Util.SDK_INT < 18 ? R.string.error_drm_not_supported
//                            : (e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
//                            ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
//                    showToast(errorStringId);
//                    return;
//                }
//            }
//
//            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
//                    ((OgleApplication) getApplication()).useExtensionRenderers()
//                            ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
//                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
//                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
//            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,
//                    drmSessionManager, extensionRendererMode);
//
//            TrackSelection.Factory videoTrackSelectionFactory =
//                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
//            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//            trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);
//            lastSeenTrackGroupArray = null;
//
//            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
//            player.addListener(this);
//
//            eventLogger = new EventLogger(trackSelector);
//            player.addListener(eventLogger);
//            player.setAudioDebugListener(eventLogger);
//            player.setVideoDebugListener(eventLogger);
//            player.setMetadataOutput(eventLogger);
//
//            simpleExoPlayerView.setPlayer(player);
//            player.setPlayWhenReady(shouldAutoPlay);
////            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
////            debugViewHelper.start();
//        }
//        if (needNewPlayer || needRetrySource) {
//            String action = intent.getAction();
//            Uri[] uris;
//            String[] extensions;
//            if (ACTION_VIEW.equals(action)) {
//                uris = new Uri[]{intent.getData()};
//                extensions = new String[]{intent.getStringExtra(EXTENSION_EXTRA)};
//            } else if (ACTION_VIEW_LIST.equals(action)) {
//                String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
//                uris = new Uri[uriStrings.length];
//                for (int i = 0; i < uriStrings.length; i++) {
//                    uris[i] = Uri.parse(uriStrings[i]);
//                }
//                extensions = intent.getStringArrayExtra(EXTENSION_LIST_EXTRA);
//                if (extensions == null) {
//                    extensions = new String[uriStrings.length];
//                }
//            } else {
//                showToast(getString(R.string.unexpected_intent_action, action));
//                return;
//            }
//            if (Util.maybeRequestReadExternalStoragePermission(this, uris)) {
//                // The player will be reinitialized if the permission is granted.
//                return;
//            }
//            MediaSource[] mediaSources = new MediaSource[uris.length];
//            for (int i = 0; i < uris.length; i++) {
//                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
//                Log.i("quocdat", "uris = " + uris[i] + " - extensions = " + extensions[i]);
//            }
//            MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
//                    : new ConcatenatingMediaSource(mediaSources);
//            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
//
////      DataSpec dataSpec = new DataSpec(uris[0]);
////      final FileDataSource fileDataSource = new FileDataSource();
////      try {
////        fileDataSource.open(dataSpec);
////      } catch (FileDataSource.FileDataSourceException e) {
////        e.printStackTrace();
////      }
////
////      DataSource.Factory factory = new DataSource.Factory() {
////        @Override
////        public DataSource createDataSource() {
////          return fileDataSource;
////        }
////      };
////      MediaSource mediaSource = new ExtractorMediaSource(fileDataSource.getUri(),
////              factory, new DefaultExtractorsFactory(), null, null);
//
//            if (haveResumePosition) {
//                player.seekTo(resumeWindow, resumePosition);
//            }
//            player.prepare(mediaSource, !haveResumePosition, false);
//            needRetrySource = false;
////            updateButtonVisibilities();
//        }
//    }
//
//    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
//        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
//                : Util.inferContentType("." + overrideExtension);
//        switch (type) {
//            case C.TYPE_SS:
//                return new SsMediaSource(uri, buildDataSourceFactory(false),
//                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
//            case C.TYPE_DASH:
//                return new DashMediaSource(uri, buildDataSourceFactory(false),
//                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
//            case C.TYPE_HLS:
//                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
//            case C.TYPE_OTHER:
//                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
//                        mainHandler, eventLogger);
//            default: {
//                throw new IllegalStateException("Unsupported type: " + type);
//            }
//        }
//    }
//
//    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManager(UUID uuid,
//                                                                           String licenseUrl, String[] keyRequestPropertiesArray) throws UnsupportedDrmException {
//        if (Util.SDK_INT < 18) {
//            return null;
//        }
//        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,
//                buildHttpDataSourceFactory(false));
//        if (keyRequestPropertiesArray != null) {
//            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
//                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
//                        keyRequestPropertiesArray[i + 1]);
//            }
//        }
//        return new DefaultDrmSessionManager<>(uuid,
//                FrameworkMediaDrm.newInstance(uuid), drmCallback, null, mainHandler, eventLogger);
//    }
//
//    private void releasePlayer() {
//
//        if (player != null) {
//            shouldAutoPlay = player.getPlayWhenReady();
//            updateResumePosition();
//            player.release();
//            player = null;
//            trackSelector = null;
//            trackSelectionHelper = null;
//            eventLogger = null;
//        }
//    }
//
//    private void clearResumePosition() {
//        resumeWindow = C.INDEX_UNSET;
//        resumePosition = C.TIME_UNSET;
//    }
//
//    private void updateResumePosition() {
//        resumeWindow = player.getCurrentWindowIndex();
//        resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition())
//                : C.TIME_UNSET;
//    }
//
//    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
//        return ((OgleApplication) getApplication())
//                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
//    }
//
//    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
//        return ((OgleApplication) getApplication())
//                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
//    }
//
//
//    @Override
//    public void onTimelineChanged(Timeline timeline, Object manifest) {
//
//    }
//
//    @Override
//    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
//    }
//
//    @Override
//    public void onLoadingChanged(boolean isLoading) {
//        //Do nothing
//    }
//
//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        if (playbackState == ExoPlayer.STATE_ENDED) {
////            showControls();
//        }
////        updateButtonVisibilities();
//    }
//
//    @Override
//    public void onPlayerError(ExoPlaybackException error) {
//
//    }
//
//    @Override
//    public void onPositionDiscontinuity() {
//        if (needRetrySource) {
//            // This will only occur if the user has performed a seek whilst in the error state. Update the
//            // resume position so that if the user then retries, playback will resume from the position to
//            // which they seeked.
//            updateResumePosition();
//        }
//    }
//
//    @Override
//    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.llExoBack:
//                super.onBackPressed();
//                break;
//        }
//    }
//
//    private void showToast(int messageId) {
//        showToast(getString(messageId));
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        //landscapse
//        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//
//        // This work only for android 4.4+
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().getDecorView().setSystemUiVisibility(flags);
//            final View decorView = getWindow().getDecorView();
//            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                        decorView.setSystemUiVisibility(flags);
//                    }
//                }
//            });
//        } else {
//            getWindow().getDecorView().setSystemUiVisibility(flags);
//        }
//    }

}
