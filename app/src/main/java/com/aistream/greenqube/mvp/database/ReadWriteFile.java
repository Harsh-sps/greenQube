package com.aistream.greenqube.mvp.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.aistream.greenqube.mvp.model.ErrorData;
import com.aistream.greenqube.mvp.model.DownloadData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public class ReadWriteFile {

    public static final String PREFIXAPP = "Qgle";

    private static final String LOG_TAG = "ReadWriteFile";
    private static Gson gson = new Gson();

    @SuppressLint("NewApi")
    public static void deleteAllFile(Context cont) {
        try {
            File dirFiles = cont.getFilesDir();
            for (String strFile : dirFiles.list()) {
                File f = new File(dirFiles, strFile);
                if (f.exists()) {
                    f.setWritable(true, false);
                    boolean a = f.delete();
                    Log.i(LOG_TAG, "Delete File" + f + " - " + a);
                }
            }
        } catch (Exception ex) {

        }
    }

    public static boolean writeDownloadDataToFile(String keyRegister, List<DownloadData> downloadDataList, Context context) {
        try {
            String data = gson.toJson(downloadDataList);
            FileOutputStream fos = context.openFileOutput(keyRegister + "downloadDataList.qtf", Context.MODE_PRIVATE);
            Log.i(LOG_TAG, "Write downloadDataList Success: " + " size: " + downloadDataList.size());
            Writer out = new OutputStreamWriter(fos);
            out.write(data);
            out.close();
            return true;
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Write downloadDataList Fail");
            ex.printStackTrace();
            return false;
        }
    }

    public static List<DownloadData> readDownloadDataToFile(String keyRegister, Context context) {
        List<DownloadData> downloadDataList = null;
        try {
            File file = new File(keyRegister + "downloadDataList.qtf");
            if (file != null && file.exists()) {
                FileInputStream fis = context.openFileInput(keyRegister + "downloadDataList.qtf");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                String json = sb.toString();
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(json);
                JsonArray ex = jsonElement.getAsJsonArray();
                downloadDataList = new ArrayList<>();
                for (int i = 0; i < ex.size(); ++i) {
                    JsonElement jsonEle = ex.get(i);
                    downloadDataList.add(gson.fromJson(jsonEle, DownloadData.class));
                }
                Log.i(LOG_TAG, "Read downloadDataList Success: " + " size: " + downloadDataList.size());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "Read MovieInfoList Fail, File not found");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "Read MovieInfoList Fail");
        }
        return downloadDataList;
    }

    public static boolean writeErrorDataToFile(String keyRegister, List<ErrorData> errorDataList, Context context) {
        try {
            String data = gson.toJson(errorDataList);
            FileOutputStream fos = context.openFileOutput(keyRegister + "errorDataList.qtf", Context.MODE_PRIVATE);
            Log.i(LOG_TAG, "Write errorDataList Success: " + " size: " + errorDataList.size());
            Writer out = new OutputStreamWriter(fos);
            out.write(data);
            out.close();
            return true;
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Write errorDataList Fail");
            ex.printStackTrace();
            return false;
        }
    }

    public static List<ErrorData> readErrorDataToFile(String keyRegister, Context context) {
        List<ErrorData> errorDataList = null;
        try {
            FileInputStream fis = context.openFileInput(keyRegister + "errorDataList.qtf");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(json);
            JsonArray ex = jsonElement.getAsJsonArray();
            errorDataList = new ArrayList<>();
            for (int i = 0; i < ex.size(); ++i) {
                JsonElement jsonEle = ex.get(i);
                errorDataList.add(gson.fromJson(jsonEle, ErrorData.class));
            }
            Log.i(LOG_TAG, "Read errorDataList Success: " + " size: " + errorDataList.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "Read errorDataList Fail,File not found");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "Read errorDataList Fail");
        }
        return errorDataList;
    }

    public static String convertObjToString(Object movieInfoList) {
        String data = gson.toJson(movieInfoList);
        return data;
    }

    public static Object parseData(String data, TypeToken typeToken) {

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(data);

        Object o = null;
        if (jsonElement instanceof JsonArray) {
            JsonArray ex = jsonElement.getAsJsonArray();
            int size = ex.size();
            ArrayList list = new ArrayList(size);
            JsonElement json;
            for (int i = 0; i < size; ++i) {
                json = ex.get(i);
                list.add(gson.fromJson(json, typeToken.getType()));
            }
            o = list;
        } else if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            o = gson.fromJson(jsonObject, typeToken.getType());
        }
        return o;
    }
}
