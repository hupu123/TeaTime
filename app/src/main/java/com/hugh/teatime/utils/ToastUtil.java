package com.hugh.teatime.utils;

import android.content.Context;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * Toast消息工具
 * Created by Hugh on 2016/2/4 11:24
 */
public class ToastUtil {

    /**
     * Toast开关，true=开，false=关
     */
    private static final boolean TOAST_SWITCH = true;

    /**
     * Toast提示用户 - 警告
     *
     * @param context 上下文
     * @param msg     提示消息内容
     * @param isShort 是否短暂显示，true=短显示，false=长显示
     */
    public static void showInfo(Context context, String msg, boolean isShort) {

        if (TOAST_SWITCH) {
            if (isShort) {
                Toasty.info(context, msg, Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.info(context, msg, Toast.LENGTH_LONG, true).show();
            }
        }
    }

    /**
     * Toast提示用户 - 警告
     *
     * @param context 上下文
     * @param msgId   消息ID
     * @param isShort 是否短暂显示，true=短显示，false=长显示
     */
    public static void showInfo(Context context, int msgId, boolean isShort) {

        if (TOAST_SWITCH) {
            String msg = context.getResources().getString(msgId);
            if (isShort) {
                Toasty.info(context, msg, Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.info(context, msg, Toast.LENGTH_LONG, true).show();
            }
        }
    }

    /**
     * Toast提示用户 - 成功
     *
     * @param context 上下文
     * @param msg     提示消息内容
     * @param isShort 是否短暂显示，true=短显示，false=长显示
     */
    public static void showSuccess(Context context, String msg, boolean isShort) {

        if (TOAST_SWITCH) {
            if (isShort) {
                Toasty.success(context, msg, Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.success(context, msg, Toast.LENGTH_LONG, true).show();
            }
        }
    }

    /**
     * Toast提示用户 - 成功
     *
     * @param context 上下文
     * @param msgId   消息ID
     * @param isShort 是否短暂显示，true=短显示，false=长显示
     */
    public static void showSuccess(Context context, int msgId, boolean isShort) {

        if (TOAST_SWITCH) {
            String msg = context.getResources().getString(msgId);
            if (isShort) {
                Toasty.success(context, msg, Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.success(context, msg, Toast.LENGTH_LONG, true).show();
            }
        }
    }

    /**
     * Toast提示用户 - 错误
     *
     * @param context 上下文
     * @param msg     提示消息内容
     * @param isShort 是否短暂显示，true=短显示，false=长显示
     */
    public static void showError(Context context, String msg, boolean isShort) {

        if (TOAST_SWITCH) {
            if (isShort) {
                Toasty.error(context, msg, Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.error(context, msg, Toast.LENGTH_LONG, true).show();
            }
        }
    }
}
