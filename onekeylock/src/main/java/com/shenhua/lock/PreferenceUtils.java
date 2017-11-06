package com.shenhua.lock;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shenhua on 2/10/2017.
 * Email shenhuanet@126.com
 */
public class PreferenceUtils {

    private static PreferenceUtils instance = null;
    private static String name = null;

    public static PreferenceUtils getInstance(String preferenceName) {
        if (instance == null) {
            instance = new PreferenceUtils();
            name = preferenceName;
        }
        return instance;
    }

    public static void write(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static void write(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static void write(Context context, String key, Boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static String readString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static int readInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static Boolean readBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).apply();
    }

}
