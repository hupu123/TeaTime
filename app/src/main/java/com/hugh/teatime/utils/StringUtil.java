package com.hugh.teatime.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Hugh on 2016/2/4 11:19
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return true=为空，false=不为空
     */
    public static boolean isStrNull(String str) {

        return str == null || str.trim().equals("");

    }

    /**
     * 半角字符转化为全角字符
     *
     * @param input 含有半角字符的字符串
     * @return 全角字符串
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 格式化BigDecimal数据
     *
     * @param num BigDecimal类型数字
     * @return 四舍五入保留两位小数后的double型数字
     */
    public static String formatBigDecimalNum(BigDecimal num) {
        if (num == null) {
            return "null";
        }
        return String.valueOf(num.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    /**
     * 格式化double数据
     *
     * @param num double类型数字
     * @return 四舍五入保留两位小数后的字符串
     */
    public static String formatDoubleNum(double num) {
        return String.valueOf(new BigDecimal(num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    /**
     * 格式化时间戳
     *
     * @param time 时间戳
     * @return yyyy-MM-dd格式字符串
     */
    public static String formatTimestamp(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(new Date(time));
    }

    /**
     * 格式化时间戳
     *
     * @param time 时间戳
     * @return yyyy-MM-dd HH:mm格式字符串
     */
    public static String formatTimestamp1(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return sdf.format(new Date(time));
    }

    /**
     * 格式化时间戳
     *
     * @param time 时间戳
     * @return yyyyMMdd格式字符串
     */
    public static String formatTimestamp2(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        return sdf.format(new Date(time));
    }
}
