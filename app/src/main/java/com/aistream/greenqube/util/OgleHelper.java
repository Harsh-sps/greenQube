package com.aistream.greenqube.util;

import android.app.Activity;
import android.content.Context;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aistream.greenqube.mvp.model.MovieInfo;
import com.aistream.greenqube.R;
import com.aistream.greenqube.customs.CustomDialogChargeMovieDownLoad;
import com.aistream.greenqube.customs.CustomDialogNotMac;
import com.aistream.greenqube.mvp.rest.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class OgleHelper {

    private static double EARTH_RADIUS = 6378137;
    private static DecimalFormat format = new DecimalFormat("#0.0");
    private static String key = "e8ffc7e56311679f12b6fc91aa77a5eb";
    private static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    public static void showMoviePrice(Context context, TextView view, MovieInfo info) {
        String price = info.getQualityList().get(0).getPrice();
        int rentalPeriod = info.getQualityList().get(0).getRentalPeriod();
        if (info.getType() == 2) {
            Log.d("MoviePrice", "movie id:"+info.getMovieId()+", name:"+info.getName()+", expireon: "+info.getExpireOn()+", currTime: "+System.currentTimeMillis());
            if (!info.hasExpired()) {
                view.setText(formatMsg(context.getResources().getString(R.string.rental_expires), getFormatDate(info.getExpireOn())));
            } else {
                if (rentalPeriod > 1) {
                    view.setText(formatMsg(context.getResources().getString(R.string.rents), price, String.valueOf(rentalPeriod)));
                } else {
                    view.setText(formatMsg(context.getResources().getString(R.string.rent), price));
                }
            }
        } else {
            view.setText(context.getResources().getString(R.string.freerental));
        }
    }

    public static String showDownloadPendingMsg(Context mContext) {
        if (Config.isAllowDownload) {
            return mContext.getResources().getString(R.string.waitingconnectturn);
        } else {
            return mContext.getResources().getString(R.string.waitingconnection);
        }
    }

    public static String getMoviePrice(Context context, MovieInfo info) {
        String price = info.getQualityList().get(0).getPrice();
        int rentalPeriod = info.getQualityList().get(0).getRentalPeriod();
        if (info.getType() == 2) {
            Log.d("MoviePrice", "movie id:"+info.getMovieId()+", name:"+info.getName()+", expireon: "+info.getExpireOn()+", currTime: "+System.currentTimeMillis());
            if (info.getExpireOn() < System.currentTimeMillis()) {
                if (rentalPeriod > 1) {
                    return formatMsg(context.getResources().getString(R.string.rents), price, String.valueOf(rentalPeriod));
                } else {
                    return formatMsg(context.getResources().getString(R.string.rent), price, String.valueOf(rentalPeriod));
                }
            }
        }
        return "";
    }

    public static String getDayTime(long time) {
        String dateStr = new Date(time).toString();
        String[] strs = dateStr.split(" ");
        return strs[1] + " " + strs[2] + " " + strs[5];
    }

    public static String getDateTime(long time) {
        Date date = new Date(time);
        String ss = "AM";
        int hours = date.getHours();
        if (hours >= 12) {
            ss = "PM";
        }
        String dateStr = date.toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String timeStr = dateFormat.format(new Date(time));
        String[] strs = dateStr.split(" ");
        return strs[1] + "," + strs[2] + " " + timeStr + ss;
    }

    public static String getFormatDate(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String date = dateFormat.format(new Date(time));
        return date;
    }

    public static String getFormatDay(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String date = dateFormat.format(new Date(time));
        return date;
    }

    public static void showMessage(Context context, String btnText, String msg, final DialogCallBack callBack) {
        if (!((Activity)context).isFinishing()) {
            final CustomDialogNotMac dialogNotMac = CustomDialogNotMac.getInstance(context);
            dialogNotMac.withTitle(msg).withBtnOkText(btnText)
                    .setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogNotMac.dismiss();
                            if (callBack != null) callBack.ok();
                        }
                    }).show();
        }
    }

    public static void showMessage(Context context, String msg, final DialogCallBack callBack) {
        if (!((Activity)context).isFinishing()) {
            final CustomDialogNotMac dialogNotMac = CustomDialogNotMac.getInstance(context);
            dialogNotMac.withTitle(msg)
                    .setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogNotMac.dismiss();
                            if (callBack != null) callBack.ok();
                        }
                    }).show();
        }
    }

    public static void showDialog(Context context, String msg, final DialogCallBack callBack) {
        if (!((Activity)context).isFinishing()) {
            final CustomDialogChargeMovieDownLoad dialog = CustomDialogChargeMovieDownLoad.getInstance(context);
            dialog.withTitle(msg)
                    .setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callBack != null) callBack.ok();
                            dialog.dismiss();
                        }
                    }).setNoClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (callBack != null) callBack.cancel();
                        }
                    }).show();
        }
    }

    public static void showDialog(Context context, String msg, String btnOkText, String cancelText, final DialogCallBack callBack) {
        if (!((Activity)context).isFinishing()) {
            final CustomDialogChargeMovieDownLoad dialog = CustomDialogChargeMovieDownLoad.getInstance(context);
            if (!TextUtils.isEmpty(btnOkText)) {
                dialog.withBtnOkText(btnOkText);
            }

            if (!TextUtils.isEmpty(cancelText)) {
                dialog.withBtnCancelText(cancelText);
            }

            dialog.withTitle(msg)
                    .setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callBack != null) callBack.ok();
                            dialog.dismiss();
                        }
                    }).setNoClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (callBack != null) callBack.cancel();
                        }
                    }).show();
        }
    }

    public static String formatMsg(String msg, String... values) {
        String result = msg;
        if (values != null) {
            int i = 0;
            for (String value: values) {
                result = result.replace("{"+i+"}", value);
                i++;
            }
        }
        return result;
    }

    /**
     * get total space
     * @param path
     * @return
     */
    public static long getTotalSpace(String path) {
        StatFs stat = new StatFs(path);
        long bytesTotal = (stat.getBlockCountLong() * stat.getBlockSizeLong());
        return bytesTotal;
    }

    /**
     * format total space
     * @param path
     * @return
     */
    public static String formatTotalSpace(String path) {
        return convertBytes(getTotalSpace(path));
    }

    /**
     * get Available space
     * @param path
     * @return
     */
    public static long getAvailableSpace(String path) {
        StatFs stat = new StatFs(path);
        long bytesAvailable = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        return bytesAvailable;
    }

    /**
     * format Available space
     * @param path
     * @return
     */
    public static String formatAvailableSpace(String path) {
        return convertBytes(getAvailableSpace(path));
    }

    public static long getFreeSpace(String path) {
        StatFs stat = new StatFs(path);
        long bytesAvailable = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        return bytesAvailable;
    }

    /**
     * format bytes
     * @param size
     * @return
     */
    public static String convertBytes(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        DecimalFormat format = new DecimalFormat("#.##");
        if (size < Kb) return format.format(size) + " B";
        if (size >= Kb && size < Mb) return format.format((double) size / Kb) + " KB";
        if (size >= Mb && size < Gb) return format.format((double) size / Mb) + " MB";
        if (size >= Gb && size < Tb) return format.format((double) size / Gb) + " GB";
        if (size >= Tb && size < Pb) return format.format((double) size / Tb) + " TB";
        if (size >= Pb && size < Eb) return format.format((double) size / Pb) + " PB";
        if (size >= Eb) return format.format((double) size / Eb) + " EB";

        return "anything...";
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * calculate two latitude and longitude's distance, unit: m
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;
    }

    public static String formatDistance(double distance) {
        double d = distance / 1000;
        if (d >= 1) {
            return format.format(d)+" km";
        } else {
            return format.format(distance) + "m";
        }
    }

    /**
     * save msg to file and encrypted with AES
     * @param file
     * @param msg
     */
    public static void saveFileWithAES(File file, String msg) {
        CipherOutputStream cos = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            if (file != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cos = new CipherOutputStream(new FileOutputStream(file), cipher);
            cos.write(msg.getBytes());
            cos.flush();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } finally {
            if (cos != null) {
                try {
                    cos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * decrypted file with AES
     * @param file
     * @return
     */
    public static String readFileWithAES(File file) {
        CipherInputStream cis = null;
        StringBuffer sb = new StringBuffer();
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            if (file != null && file.exists()) {
                cis = new CipherInputStream(new FileInputStream(file), cipher);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = cis.read(bytes)) != -1) {
                    byte[] buff = new byte[len];
                    System.arraycopy(bytes, 0, buff, 0, len);
                    sb.append(new String(buff));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } finally {
            if (cis != null) {
                try {
                    cis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static String nullToEmpty(String str) {
        if (str == null) { return ""; }
        if (str.equalsIgnoreCase("null")) {return ""; }
        return str;
    }

    public static String emptyToNull(String str) {
        if (str == null) { return str; }
        if ("".equals(str) || str.equalsIgnoreCase("null")) {
            return null;
        }
        return str;
    }

    public static boolean same(String str1, String str2) {
        String s1 = nullToEmpty(str1);
        String s2 = nullToEmpty(str2);

        if (s1.equals(s2)){
            return true;
        }
        return false;
    }

    public static String getDurationString(int seconds) {
        int minutes = seconds / 60;
        if (seconds > 0 && minutes == 0) {
            minutes = 1;
        }
        return minutes + " Minutes";
    }

    /**
     * check phone local system time whether correct, compare with api server time
     * @param serverTime
     * @return
     */
    public static boolean checkSystemTimeNormal(long serverTime) {
        long currtime = System.currentTimeMillis();
        long diff = currtime - serverTime;
        if (diff > 0 && diff < 180000) {
            return true;
        }
        return false;
    }

    /**
     * delete dir
     * @param dir
     * @return
     */
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    /**
     * delete dir by dirname
     * @param dirFile
     * @param dirName
     */
    public static void deleteFolder(File[] dirFile, String dirName) {
        for (File file : dirFile) {
            if (file != null) {
                String[] children = file.list();
                for (String s : children) {
                    if (s.equals(dirName)) {
                        deleteDir(new File(file, s));
                    }
                }
            }
        }
    }

    public static boolean isEqual(List<String> list1, List<String> list2) {
        if (list1 != null && list2 != null) {
            if (list1.size() == list2.size()) {
                for (String s1: list1) {
                    boolean isFind = false;
                    for (String s2: list2) {
                        if (s1.equals(s2)) {
                            isFind = true;
                            break;
                        }
                        if (!isFind) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * format double with degree
     * @param value
     * @param num
     * @return
     */
    public static String formatDouble(double value, int num) {
        String pattern = "#.0";
        for (int i = 1; i < num; i++) {
            pattern += "0";
        }
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(value);
    }
}
