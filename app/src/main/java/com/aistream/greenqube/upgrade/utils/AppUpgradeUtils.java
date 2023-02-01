package com.aistream.greenqube.upgrade.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.aistream.greenqube.upgrade.Update;
import com.aistream.greenqube.upgrade.UpdateFrom;
import com.aistream.greenqube.upgrade.UpgradeConfig;
import com.aistream.greenqube.upgrade.listener.ExceptionHandler;
import com.aistream.greenqube.upgrade.listener.ExceptionHandlerHelper;

import org.jsoup.Jsoup;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 *  app upgrade utils
 */

public class AppUpgradeUtils {
    public static final int REQ_CODE_INSTALL_APP = 99;

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static String getAppName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        }
        return "";
    }

    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getAppInstalledVersion(Context context) {
        String version = "0.0.0.0";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static Integer getAppInstalledVersionCode(Context context) {
        Integer versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static UpdateFrom getAppInstallFrom(Context context) {
        try {
            String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
            if (installer != null && installer.equals("com.android.vending")) {
                Log.d("AppUpgrade", "app install from google play");
                return UpdateFrom.GOOGLE_PLAY;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("AppUpgrade", "app custom install");
        return UpdateFrom.CUSTOM;
    }

    public static Update getLatestAppVersionGooglePlay(Context context) {
        Update updateInfo = null;
        try {
            String res = String.format(UpgradeConfig.PLAY_STORE_URL, getAppPackageName(context), Locale.getDefault().getLanguage());
            URL updateURL = new URL(res);
            String version = getJsoupString(updateURL.toString(), ".hAyfc .htlgb", 7);
            if (TextUtils.isEmpty(version)) {
                Log.e("AppUpdater", "Cannot retrieve google play latest version. Is it configured properly?");
            } else {
                updateInfo = new Update();
                updateInfo.setVersion(version);
                updateInfo.setApkUrl(res);
                updateInfo.setUpdateFrom(UpdateFrom.GOOGLE_PLAY);
            }
        } catch (Exception e) {
            Log.e("AppUpdater", "getLatestAppVersionGooglePlay erro: ", e);
        }
        return updateInfo;
    }

    private static String getJsoupString(String url, String css, int position) throws Exception {
        return Jsoup.connect(url)
                .timeout(30000)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get()
                .select(css)
                .get(position)
                .ownText();
    }

    public static int compareVersion(String new_version, String old_version) {
        String[] thisParts = new_version.split("\\.");
        String[] thatParts = old_version.split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                    Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart)
                return -1;
            if (thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    /**
     * get app file
     * @param updateInfo
     * @return
     */
    public static File getAppFile(Update updateInfo) {
        String appName = getApkName(updateInfo);
        Log.d("AppUpgrade", "targetPath: "+updateInfo.getTargetPath()+", appName: "+appName+", version: "+updateInfo.getVersion());
        return new File(updateInfo.getTargetPath()
                .concat(File.separator + updateInfo.getVersion())
                .concat(File.separator + appName));
    }

    /**
     * get apk name
     * @param updateInfo
     * @return
     */
    @NonNull
    public static String getApkName(Update updateInfo) {
        String apkUrl = updateInfo.getApkUrl();
        String appName = "";
        if (!TextUtils.isEmpty(apkUrl)) {
            appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length());
        }
        return appName;
    }

    public static boolean appIsDownloaded(Update updateInfo) {
        File appFile = getAppFile(updateInfo);
        Log.d("AppUpgrade", "app download path: "+appFile.getAbsolutePath()+", isexist: "+appFile.exists());
        return appFile.exists();
    }

    public static void deleteDownloadFile(Update updateInfo) {
        File appFile = getAppFile(updateInfo);
        Log.d("AppUpgrade", "delete app download, path: "+appFile.getAbsolutePath()+", isexist: "+appFile.exists());
        appFile.delete();
    }

    public static boolean installApp(Context context, File appFile) {
        try {
            Intent intent = getInstallAppIntent(context, appFile);
            if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
        return false;
    }

    public static boolean installApp(Activity activity, File appFile) {
        try {
            Intent intent = getInstallAppIntent(activity, appFile);
            Log.d("AppUpgrade", "intent: "+intent);
            if (activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                activity.startActivityForResult(intent, REQ_CODE_INSTALL_APP);
            }
            return true;
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
        return false;
    }

    public static boolean installApp(Fragment fragment, File appFile) {
        Log.d("AppUpgrade", "appFile: "+appFile.getAbsolutePath());
        return installApp(fragment.getActivity(), appFile);
    }

    public static Intent getInstallAppIntent(Context context, File appFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            //support android 7
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", appFile);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                //support android 8
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                    if (!hasInstallPermission) {
                        startInstallPermissionSettingActivity(context);
                        return null;
                    }
                }

            } else {
                Uri uri = Uri.fromFile(appFile);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
        return null;
    }

    /**
     * goto unknow source app install page
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Context context) {
        //new api 8
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goToGooglePlay(Context context, Update updateInfo) {
        UpdateFrom updateFrom = updateInfo.getUpdateFrom();
        Intent intent = null;
        if (updateFrom.equals(UpdateFrom.GOOGLE_PLAY)) {
            try {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getAppPackageName(context)));
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.getApkUrl()));
                context.startActivity(intent);
            }
        }
    }

    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "";
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAppOnForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    public static Drawable getAppIcon(Context context) {
        try {
            return context.getPackageManager().getApplicationIcon(context.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {


        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    public static int dip2px(int dip, Context context) {
        return (int) (dip * getDensity(context) + 0.5f);
    }

    public static float getDensity(Context context) {
        return getDisplayMetrics(context).density;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }
}
