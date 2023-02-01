package com.aistream.greenqube.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.aistream.greenqube.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * app crash exception handler
 */
public class CrashCatchHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    private Context context;
    private static final CrashCatchHandler INSTANCE = new CrashCatchHandler();
    private Thread.UncaughtExceptionHandler defaultHandler;
    private JsonObject deviceInfo;
    private Gson gson = new Gson();
    private String appName;

    private CrashCatchHandler() {

    }

    public static CrashCatchHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        this.context = context;
        appName = context.getResources().getString(R.string.app_name);
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * handle exception
     * @param ex
     */
    private void handleException(Throwable ex) {
        if (ex == null) {
            return;
        }
        JsonObject deviceInfo = collectDeviceInfo(context);
        saveCrashInfoToFile(deviceInfo, ex);
    }

    private String getRamInfo() {
        long totalMem = 0;
        long availMem = 0;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        totalMem = memoryInfo.totalMem;
        availMem = memoryInfo.availMem;
        return availMem + "/" + totalMem;
    }

    /**
     * collect device info
     * @param context
     * @return
     */
    public JsonObject collectDeviceInfo(Context context) {
        JsonObject deviceInfo = new JsonObject();
        deviceInfo.addProperty("appName", appName);
        deviceInfo.addProperty("SDK", Build.VERSION.SDK_INT);
        deviceInfo.addProperty("ANDROID", Build.VERSION.RELEASE);
        deviceInfo.addProperty("LG", Locale.getDefault().getLanguage());
        deviceInfo.addProperty("RAM", getRamInfo());
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "" : pi.versionName;
                String versionCode = pi.versionCode + "";
                deviceInfo.addProperty("packageName", pi.packageName);
                deviceInfo.addProperty("versionName", versionName);
                deviceInfo.addProperty("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an NameNotFoundException occured when collect package info");
        }

        // get system firmware info
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                deviceInfo.addProperty(field.getName(), field.get(null).toString());
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "an IllegalArgumentException occured when collect system reflect field info", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "an IllegalAccessException occured when collect system reflect field info", e);
            }
        }
        return deviceInfo;
    }

    /**
     * save crash exception info to file
     * @param deviceInfo
     * @param ex
     */
    private void saveCrashInfoToFile(JsonObject deviceInfo, Throwable ex) {
        Log.d(TAG, "saveCrashInfoToFile deviceInfo: "+gson.toJson(deviceInfo));
        // get crash exception info
        String crashInfo = "";
        Writer writer = null;
        try {
            writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    crashInfo = writer.toString();
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        deviceInfo.addProperty("exception", crashInfo);

        //write file with time stamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName;
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        } else {
            //clear history crash info file
            clearCrashHistoryFile(rootDir);
        }
        String crashFileName = rootPath + "/crash_" + simpleDateFormat.format(new Date()) + ".log";
        try {
            FileOutputStream fos = new FileOutputStream(crashFileName);
            fos.write(gson.toJson(deviceInfo).getBytes());
            fos.close();
            Log.d(TAG, "save crash exception info to file: "+crashFileName);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "an FileNotFoundException occured when write crashfile to sdcard", e);
        } catch (IOException e) {
            Log.e(TAG, "an IOException occured when write crashfile to sdcard", e);
        }
    }

    /**
     * clear crash history file
     * @param rootDir
     */
    private void clearCrashHistoryFile(File rootDir) {
        try {
            File[] files = rootDir.listFiles();
            Map<String, String> fileMap = new HashMap<>();
            if (files != null) {
                for (File file: files) {
                    if (file.isFile() && file.getName().startsWith("crash_")) {
                        String fileName = file.getName();
                        String timeStr = fileName.substring(fileName.indexOf("_")+1, fileName.indexOf(".log"));
                        fileMap.put(timeStr, file.getAbsolutePath());
                    }
                }
            }

            int maxAllow = 4;
            if (fileMap.size() > maxAllow) {
                List<String> timeList = new ArrayList(fileMap.keySet());
                Collections.sort(timeList);
                for (String timeStr: timeList) {
                    if (fileMap.size() <= maxAllow) {
                        break;
                    }
                    String filePath = fileMap.get(timeStr);
                    File file = new File(filePath);
                    file.delete();
                    fileMap.remove(timeStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
