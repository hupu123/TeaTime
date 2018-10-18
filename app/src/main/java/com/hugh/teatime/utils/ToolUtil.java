package com.hugh.teatime.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hugh.teatime.models.book.Book;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.SliceValue;

/**
 * 常用工具
 * Created by Hugh on 2016/2/17 9:52
 */
public class ToolUtil {

    /**
     * 通过ListView的子控件计算ListView的总高度
     *
     * @param listView 被计算的ListView控件
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 读取文件为字符串
     *
     * @param filePath 文件路径
     * @return 字符串
     */
    public static String getStringFromIS(String filePath) {

        File file = new File(filePath);
        if (file.isDirectory()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        try {
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 获取书籍信息
     *
     * @param filePath 书籍路径
     * @return 书籍信息
     */
    public static Book getBookInfo(String filePath) {

        if (new File(filePath).isDirectory()) {
            return null;
        }
        Book book = new Book();
        String name = "未知文件名";
        String type = "未知文件类型";
        String[] saFilePath = filePath.split("/");
        if (saFilePath.length > 0) {
            String fileName = saFilePath[saFilePath.length - 1];
            String[] saFileName = fileName.split("\\.");
            if (saFileName.length > 0) {
                type = saFileName[saFileName.length - 1];
                if (type.equals("txt")) {
                    name = saFileName[0];
                } else {
                    return null;
                }
            }
        }

        int size;
        String bookContent = getStringFromIS(filePath);
        if (StringUtil.isStrNull(bookContent)) {
            size = 0;
        } else {
            size = bookContent.length() / StrQueueUtil.STR_LENGTH + 1;
        }

        book.setName(name);
        book.setPath(filePath);
        book.setType(type);
        book.setSize(size);

        return book;
    }

    /**
     * 获取百分比
     *
     * @param pro  进度
     * @param size 总量
     * @return 百分比
     */
    public static int getProgress(int pro, int size) {

        if (size <= 0 || pro < 0) {
            return 0;
        }
        double progress = (double) pro / (double) size * 100;
        BigDecimal bd = new BigDecimal(progress);

        return bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    /**
     * 获取百分比
     *
     * @param pro  分子
     * @param size 分母
     * @return 百分比
     */
    public static double getProgress(double pro, double size) {
        if (size <= 0 || pro < 0) {
            return 0;
        }
        double progress = pro / size * 100;
        BigDecimal bd = new BigDecimal(progress);

        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 通过时间戳获取日期
     *
     * @param timestamp 时间戳
     * @param type      日期格式，0=yyyy-MM-dd,1=yyyy-MM-dd HH:mm,2=yyyy-MM-dd hh:mm:ss,5=HH
     * @return 日期字符串
     */
    public static String getTimeFromTimestamp(long timestamp, int type) {

        String timeStyle;
        if (type == 0) {
            timeStyle = "yyyy-MM-dd";
        } else if (type == 1) {
            timeStyle = "yyyy-MM-dd HH:mm";
        } else if (type == 2) {
            timeStyle = "yyyy-MM-dd HH:mm:ss";
        } else if (type == 3) {
            timeStyle = "yyyyMMddHHmmss";
        } else if (type == 5) {
            timeStyle = "HH";
        } else {
            timeStyle = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(timeStyle, Locale.CHINA);
        Date date = new Date(timestamp);

        return sdf.format(date);
    }

    /**
     * 对图表数据进行排序
     * 排序算法：冒泡排序
     * 排序规则：从大到小
     *
     * @param datas 图表原始数据
     * @return 排序后的数据
     */
    public static List<SliceValue> sortChartData(List<SliceValue> datas) {

        for (int i = 0; i < datas.size(); i++) {
            // -i因为最小数已经沉底，无需比较，-1防止get(j+1)越界
            for (int j = 0; j < datas.size() - i - 1; j++) {
                SliceValue value1 = datas.get(j);
                SliceValue value2 = datas.get(j + 1);
                if (value1.getValue() < value2.getValue()) {
                    datas.remove(j);
                    datas.add(j + 1, value1);
                }
            }
        }

        return datas;
    }

    /**
     * 获取文件后缀名
     *
     * @param file 文件
     * @return 后缀名
     */
    public static String getFileType(File file) {

        String fileType;
        String fileName = file.getName();
        String[] fileNames = fileName.split("\\.");
        if (fileNames.length > 1) {
            fileType = fileNames[fileNames.length - 1];
        } else {
            fileType = "";
        }

        return fileType;
    }

    /**
     * 根据时间戳获取年份
     *
     * @param time 时间戳
     * @return 年份
     */
    public static int getYearFromTimestamp(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.CHINA);
        return Integer.parseInt(sdf.format(date));
    }

    /**
     * 根据时间戳获取月份
     *
     * @param time 时间戳
     * @return 月份
     */
    public static int getMonthFromTimestamp(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.CHINA);
        return Integer.parseInt(sdf.format(date));
    }

    /**
     * 根据时间戳获取日期
     *
     * @param time 时间戳
     * @return 日期
     */
    public static int getDayFromTimestamp(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.CHINA);
        return Integer.parseInt(sdf.format(date));
    }

    /**
     * 获取一年当中的某月有多少天
     *
     * @param year  年份
     * @param month 月份
     * @return 天数
     */
    public static int getDayNumOfMonth(int year, int month) {
        int day;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = 30;
                break;
            case 2:
                if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                    day = 29;
                } else {
                    day = 28;
                }
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    /**
     * 获取一天的开始
     *
     * @param time 时间戳
     * @return 开始
     */
    public static long getStartFromTimestamp(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String date = sdf.format(new Date(time));
        long start = time;
        try {
            start = sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return start;
    }

    /**
     * 获取一天的结束
     *
     * @param time 时间戳
     * @return 结束
     */
    public static long getEndFromTimestamp(long time) {
        long end = getStartFromTimestamp(time);
        return end + (23 * 60 * 60 * 1000 + 59 * 60 * 1000 + 59 * 1000);
    }
}
