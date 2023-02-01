
package com.aistream.greenqube.chromecast;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.util.Log;

import com.flixsys.ogle.sdk.FileServer;

import com.aistream.greenqube.OgleApplication;
import com.aistream.greenqube.mvp.model.MovieBilling;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.cast.framework.media.RemoteMediaClient.Listener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient.ProgressListener;
import com.google.android.gms.common.images.WebImage;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class FlixsysChromcast {
    private static final String TAG = "FlixsysChromcastTag";
    public static OgleApplication mApplication;
    private static final String ErrorMsg = "this device has not installed GooglePlayService,do not support Chromcast!";
    private static CastContext mCastContext;
    private static CastSession mCastSession;
    private static SessionManagerListener<CastSession> mSessionManagerListener;
    private static Map<String, CastSessionListener> castSessionMap = new TreeMap<>();
    //main activity context
    private static Context mainContext;
    private static Context castBtnContext;
    private static MediaRouteButton mMediaRouteButton;
    private static RemoteMediaClient remoteMediaClient;
    private static Listener remoteMediaClientListener;
    private static long currentPos = 0L;
    private static long streamDuration = 0L;
    private static int currPlayMode;
    private static FileServer fileServer;
    /**
     * play progress listner
     */
    private static ProgressListener progressListener = new ProgressListener() {
        public void onProgressUpdated(long progress, long duration) {
            currentPos = progress / 1000L;
            streamDuration = duration / 1000L;
        }
    };

    static Timer castTimer = new Timer();
    private static boolean castAvailable = false;

    public FlixsysChromcast() {
    }

    public static void setCastSessionListener(CastSessionListener lis) {
        if (lis != null) {
            String simpleName = lis.getClass().getSimpleName();
            castSessionMap.put(simpleName, lis);
        }
    }

    public static void init(Context ctx, Bundle savedInstanceState) {
        initTime();
        removeSessionManagerListener();
        setupCastListener();
        castSessionMap.clear();
        try {
            mainContext = ctx;
            mCastContext = CastContext.getSharedInstance(mainContext);
            mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
            castAvailable = true;
            Log.d(TAG, "init mCastContext: "+mCastContext+", mCastSession:"+mCastSession);
        } catch (Exception e) {
            castAvailable = false;
            Log.d(TAG, "init CastContext fail ", e);
        }
    }

    /**
     * setup cast listener
     */
    public static void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {
            public void onSessionEnded(CastSession session, int error) {
                if (session == mCastSession) {
                    mCastSession = null;
                }
                Log.d("mSessionManagerListener", "onSessionEnded:" + error+", mCastSession:"+mCastSession);
                this.onApplicationDisconnected();
            }

            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                Log.d("mSessionManagerListener", "onSessionResumed:" + wasSuspended);
                this.onApplicationConnected(session);
            }

            public void onSessionResumeFailed(CastSession session, int error) {
                Log.d("mSessionManagerListener", "onSessionResumeFailed:" + error);
                this.onApplicationDisconnected();
            }

            public void onSessionStarted(CastSession session, String sessionId) {
                Log.d("mSessionManagerListener", "onSessionStarted:" + sessionId);
                this.onApplicationConnected(session);
            }

            public void onSessionStartFailed(CastSession session, int error) {
                Log.d("mSessionManagerListener", "onSessionStartFailed:" + error);
                this.onApplicationDisconnected();
            }

            public void onSessionStarting(CastSession session) {
                Log.d("mSessionManagerListener", "onSessionStarting:" + session);
            }

            public void onSessionEnding(CastSession session) {
                Log.d("mSessionManagerListener", "onSessionEnding:" + session);
            }

            public void onSessionResuming(CastSession session, String sessionId) {
                Log.d("mSessionManagerListener", "onSessionResuming:" + sessionId);
            }

            public void onSessionSuspended(CastSession session, int reason) {
                Log.d("mSessionManagerListener", "onSessionSuspended:" + reason);
            }

            private void onApplicationConnected(CastSession castSession) {
                FlixsysChromcast.mCastSession = castSession;
                Log.d(TAG, "onApplicationConnected mCastSession:"+mCastSession);
                FlixsysChromcast.remoteMediaClient = FlixsysChromcast.mCastSession.getRemoteMediaClient();
                FlixsysChromcast.remoteMediaClient.addProgressListener(FlixsysChromcast.progressListener, 0L);
                FlixsysChromcast.startCastTimer();
                Log.d("mSessionManagerListener", "onApplicationConnected:" + castSession.toString());
                Collection<CastSessionListener> castSessionListeners = castSessionMap.values();
                if (castSessionListeners != null && !castSessionListeners.isEmpty()) {
                    for(CastSessionListener listener: castSessionListeners) {
                        listener.onConnect();
                    }
                }
            }

            private void onApplicationDisconnected() {
                FlixsysChromcast.stopCastTimer();
                FlixsysChromcast.initButton(castBtnContext, FlixsysChromcast.mMediaRouteButton);
                Log.d("mSessionManagerListener", "onApplicationDisconnected:DISCONNECT");
                Collection<CastSessionListener> castSessionListeners = castSessionMap.values();
                if (castSessionListeners != null && !castSessionListeners.isEmpty()) {
                    for(CastSessionListener listener: castSessionListeners) {
                        listener.onDisconnect();
                    }
                }
            }
        };
    }

    /**
     * init media router button
     * @param ctx
     * @param mMediaRouteButtonT
     */
    public static void initButton(Context ctx, MediaRouteButton mMediaRouteButtonT) {
        castBtnContext = ctx;
        mMediaRouteButton = mMediaRouteButtonT;
        Log.d(TAG, "initButton:"+mMediaRouteButton);
        if (mMediaRouteButton != null) {
            try {
                CastButtonFactory.setUpMediaRouteButton(castBtnContext, mMediaRouteButtonT);
            } catch (Exception var3) {
                Log.d(TAG, "initButton:" + var3.getLocalizedMessage());
            }
        }
    }

    /**
     * whether support google cast
     * @return
     */
    public static boolean isCastAvailable() {
        return castAvailable;
    }

    /**
     * set media client listener
     * @param remoteMediaClientListener
     */
    public static void setRemoteMediaClientListener(Listener remoteMediaClientListener) {
        remoteMediaClientListener = remoteMediaClientListener;
    }

    /**
     * start google cast
     *
     * @param subtitle   subtitle
     * @param title   title
     * @param root     movie store dir
     * @param filepath     movie relative path
     * @param imgurl   movie img url
     * @param position  start position
     * @param autoPlay
     * @param token    FAG token
     */
    public static void startCast(int mvId, String subtitle, String title, String root,
                                 String filepath, String imgurl, long position, boolean autoPlay, String token) throws Exception {
        Log.d(TAG, "startCast mCastSession:" +mCastSession);
        Log.d(TAG, "startCast subtitle:" +subtitle+", title:"+title+", root: "+root+", filepath: "+filepath
                            +", imgurl: "+imgurl+", position: "+position+", autoPlay: "+autoPlay+", token: "+token);

        if (mCastSession != null) {
            remoteMediaClient = mCastSession.getRemoteMediaClient();
            Log.d(TAG, "startCast remoteMediaClient:" +remoteMediaClient);
            if (remoteMediaClient != null) {
                remoteMediaClient.addProgressListener(progressListener, 0L);
                remoteMediaClient.addListener(remoteMediaClientListener);

                String url = startCastHttpServer(root, filepath);
                playMediaInfo(mvId, url, subtitle, title, imgurl, position, autoPlay, token);
            }
        }
    }

    /**
     * start cast http server
     * @param root
     * @param filepath
     * @return
     * @throws IOException
     */
    public static String startCastHttpServer(String root, String filepath) throws IOException {
        String ipaddr = mApplication.getIpAddress();
        if (fileServer == null) {
            fileServer = new FileServer(0, root);
            fileServer.start();
            Log.d(TAG, "start http server, port:" +fileServer.getListeningPort());
        }
        int port = fileServer.getListeningPort();
        String url = "http://"+ipaddr+":"+port+"/"+filepath;
        if (filepath.startsWith("/")) {
            url = "http://"+ipaddr+":" + port + filepath;
        }
        Log.d(TAG, "startCast url:" +url);
        return url;
    }

    /**
     * load chromecast media info
     * @param url
     * @param subtitle
     * @param title
     * @param imgurl
     * @param position
     * @param autoPlay
     * @param token
     * @throws Exception
     */
    private static void playMediaInfo(int mvId, String url, String subtitle, String title, String imgurl,
                                            long position, boolean autoPlay, String token)  throws Exception{
        //build movie meta data
        MediaMetadata movieMetadata = new MediaMetadata(1);
        movieMetadata.addImage(new WebImage(Uri.parse(imgurl)));
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, subtitle);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);

        //play mine type, default hls
        String mimeType = "application/x-mpegurl";
        if (url.endsWith(".mpd")) { //dash
            mimeType = "application/dash+xml";
        }

        //build play media info
        MediaInfo mediaInfo = new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(mimeType)
                .setStreamDuration(0)
                .setMetadata(movieMetadata)
                .build();

        //build customer data
        JSONObject customData = null;
        customData = new JSONObject();
        customData.put("drmtype", 2);  //0: no drm  1: widevine  2: oma
        customData.put("customer_license_headers", "Authentication,fx-credential");
        customData.put("Authentication", "Bearer "+token);
        MovieBilling movieBillingInfo = mApplication.getMovieBillingInfo(mvId);
        customData.put("fx-credential", movieBillingInfo == null? "": movieBillingInfo.getRentalCertificate());

        MediaLoadOptions mediaLoadOptions = new MediaLoadOptions.Builder()
                .setAutoplay(autoPlay)
                .setPlayPosition(position)
                .setCustomData(customData).build();
        remoteMediaClient.load(mediaInfo, mediaLoadOptions);
    }

    /**
     * add session manager listener
     */
    public static void addSessionManagerListener(Context ctx) {
        if (mCastContext != null) {
            mCastContext.getSessionManager().addSessionManagerListener(
                        mSessionManagerListener, CastSession.class);
            mCastSession = CastContext.getSharedInstance(ctx).getSessionManager()
                    .getCurrentCastSession();
        }
        Log.e(TAG, "addSessionManagerListener mCastSession: "+mCastSession);
    }

    /**
     * unregister session manager listener
     */
    public static void removeSessionManagerListener() {
        Log.e(TAG, "removeSessionManagerListener mCastSession: "+mCastSession);
        if (mCastContext != null) {
            mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);
        }
        mCastSession = null;
    }

    private static void initTime() {
        currentPos = 0L;
        streamDuration = 0L;
    }

    private static void startCastTimer() {
        stopCastTimer();
        castTimer = new Timer();
        castTimer.schedule(new TimerTask() {
            public void run() {
                Log.d("current play position: ", "" + currentPos+", duration: "+streamDuration);
            }
        }, 0L, 1000L);
    }

    private static void stopCastTimer() {
        if (castTimer != null) {
            castTimer.cancel();
            castTimer = null;
        }
    }
}
