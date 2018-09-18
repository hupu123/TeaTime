package com.hugh.teatime.utils;

import android.util.Log;

/**
 * Created by Hugh on 2016/2/4 16:44
 */
public class LogUtil {

    /**
     * Log开关 true=开启，false=关闭
     */
    private static final boolean LOG_SWITCH = true;

    /**
     * LOG信息打印
     *
     * @param tag 标签
     * @param msg 内容
     */
    public static void logI(String tag, String msg) {
        if (LOG_SWITCH) {
            Log.i(tag, msg);
        }
    }

    /**
     * LOG信息打印，标签为result
     *
     * @param msg 内容
     */
    public static void logIResult(String msg) {
        if (LOG_SWITCH) {
            Log.i("result", "-------------> " + msg + " <------------");
        }
    }

    /**
     * LOG错误打印
     *
     * @param tag 标签
     * @param msg 内容
     */
    public static void logE(String tag, String msg) {
        if (LOG_SWITCH) {
            Log.e(tag, msg);
        }
    }

    /**
     * Hugh快捷日志打印方法
     *
     * @param msg 日志内容
     */
    public static void logHugh(String msg) {
        if (LOG_SWITCH) {
            Log.i("hugh-tag", msg);
        }
    }
}
