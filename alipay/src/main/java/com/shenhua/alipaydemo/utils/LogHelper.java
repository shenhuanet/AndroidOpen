package com.shenhua.alipaydemo.utils;

import android.util.Log;

/**
 * 日志工具类
 *
 * 使用方法：
 * step 1 : 静态导入对应日志方法，比如 import static cn.neocross.common.LogHelper.LOGD;
 * step 2 : 记录日志 LOG("<日志标签>", "<日志内容>")
 *
 * 注意：tag 长度应小于23个字符
 * Created by nodlee on 2016/4/27.
 */
public class LogHelper {
    public static boolean LOGGING_ENABLED = true;

    public static void LOGD(String tag, String message) {
        if(LOGGING_ENABLED && Log.isLoggable(tag, Log.DEBUG)){
            Log.d(tag, message);
        }
    }

    public static void LOGV(String tag, String message) {
        if(LOGGING_ENABLED && Log.isLoggable(tag, Log.VERBOSE)){
            Log.v(tag, message);
        }
    }

    public static void LOGI(String tag, String message) {
        if(LOGGING_ENABLED && Log.isLoggable(tag, Log.INFO)){
            Log.i(tag, message);
        }
    }

    public static void LOGW(String tag, String message) {
        if(LOGGING_ENABLED && Log.isLoggable(tag, Log.WARN)){
            Log.w(tag, message);
        }
    }

    public static void LOGE(String tag, String message) {
        if(LOGGING_ENABLED && Log.isLoggable(tag, Log.ERROR)){
            Log.e(tag, message);
        }
    }
}
