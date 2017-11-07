package com.shenhua.libs.firupdater.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Shenhua on 11/30/2016.
 * e-mail shenhuanet@126.com
 */
public class FirUtil {

    private static final String TAG = "FirUtil";
    public static final int SUCCESS = 1;
    public static final int FAILED = 0;

    public static String getFormatSize(long bytes) {
        BigDecimal fileSize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = fileSize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1) return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = fileSize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "KB");
    }

    public static String getFormatTime(long lt) {
        Date date = new Date(lt * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * compare the version between the version code.
     *
     * @param activity       activity
     * @param newVersionCode version code
     * @return true is the diff version or false is the same version.
     */
    public static boolean compareVersion(Activity activity, int newVersionCode) {
        return getCurrentAppVersionCode(activity) != newVersionCode;
    }

    /**
     * get current version code.
     *
     * @param activity activity
     * @return current app version code.
     */
    public static int getCurrentAppVersionCode(Activity activity) {
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "getCurrentAppVersionCode: error: VersionCode is int type.");
            return 1;
        }
    }

    /**
     * get current version name.
     *
     * @param activity activity
     * @return current app version name.
     */
    public static String getCurrentAppVersionName(Activity activity) {
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "getCurrentAppVersionName: error: VersionName is String type.");
            return "1.0";
        }
    }

    public static String buildUpdateUrl(String appToken, String appKeyOrAppPackage) {
        return "http://api.fir.im/apps/latest/" + appKeyOrAppPackage + "?api_token=" + appToken;
    }
}
