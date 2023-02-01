package com.sahooz.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by android on 17/10/17.
 */

public class Country implements PyEntity {
    private static final String TAG = Country.class.getSimpleName();
    public int code;
    public String mcc;
    public String name, locale, pinyin;
    public int flag;
    private static ArrayList<Country> countries = null;
    private static Map<String, Country> countryMap = new HashMap<>();
    private static Map<String, Country> mccMap = new HashMap<>();

    public Country(int code, String name, String locale, int flag, String mcc) {
        this.code = code;
        this.name = name;
        this.flag = flag;
        this.locale = locale;
        this.mcc = mcc;
    }

    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                ", flag='" + flag + '\'' +
                ", name='" + name + '\'' +
                ", mcc='" + mcc + '\'' +
                '}';
    }

    public static ArrayList<Country> getAll(@NonNull Context ctx, @Nullable ExceptionCallback callback) {
        if(countries != null) return countries;
        countries = new ArrayList<>();
        BufferedReader br = null;
        try {
            String countryCode = ctx.getResources().getConfiguration().locale.getCountry();
            br = new BufferedReader(new InputStreamReader(ctx.getResources().getAssets().open("code.json")));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null)
                sb.append(line);
            br.close();
            JSONArray ja = new JSONArray(sb.toString());
            String key = getKey(ctx);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                int flag = 0;
                String locale = jo.getString("locale");
                if(!TextUtils.isEmpty(locale)) {
                    flag = ctx.getResources().getIdentifier("flag_" + locale.toLowerCase(), "drawable", ctx.getPackageName());
                }

                String[] mccs = null;
                String mccStr = jo.getString("mcc");
                if (!TextUtils.isEmpty(mccStr) && !mccStr.equals("0")) {
                    Country country = new Country(jo.getInt("code"), jo.getString(key), locale, flag, mccStr);
                    countryMap.put(locale, country);
                    countries.add(country);

                    String[] mccArray = mccStr.split("\\.");
                    for(String mcc: mccArray) {
                        mccMap.put(mcc, country);
                    }
                }
            }
        } catch (IOException e) {
            if(callback != null) callback.onIOException(e);
            e.printStackTrace();
        } catch (JSONException e) {
            if(callback != null) callback.onJSONException(e);
            e.printStackTrace();
        }
        return countries;
    }

    public static Country fromJson(String json){
        if(TextUtils.isEmpty(json)) return null;
        try {
            JSONObject jo = new JSONObject(json);
            return new Country(jo.optInt("code") ,jo.optString("name"), jo.optString("locale"), jo.optInt("flag"), jo.optString("mcc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toJson(){
        return "{\"name\":\"" + name + "\", \"code\":" + code + ", \"flag\":" + flag + ",\"locale\":\"" + locale + "\", \"mcc\":\" + mcc + \"}";
    }

    public static void destroy(){ countries = null; }

    private static String getKey(Context ctx) {
//        String country = ctx.getResources().getConfiguration().locale.getCountry();
//        return "CN".equalsIgnoreCase(country)? "zh"
//                : "TW".equalsIgnoreCase(country)? "tw"
//                : "HK".equalsIgnoreCase(country)? "tw"
//                : "en";
        return "en";
    }

    public static Country getCountry(String locale) {
        return countryMap.get(locale);
    }

    public static Country getCountryByMcc(int mcc) {
        if (mcc > 0) {
            return mccMap.get(String.valueOf(mcc));
        }
        return null;
    }

    private static boolean inChina(Context ctx) {
        return "CN".equalsIgnoreCase(ctx.getResources().getConfiguration().locale.getCountry());
    }

    @Override
    public int hashCode() {
        return code;
    }

    @NonNull @Override
    public String getPinyin() {
        if(pinyin == null) {
            pinyin = PinyinUtil.getPingYin(name);
        }
        return pinyin;
    }
}
