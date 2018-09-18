package com.hugh.teatime.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hugh.teatime.models.message.SMS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Hugh on 2016/4/15 15:02
 */
public class SMSUtil {

    /**
     * 所有短信
     */
    private final static Uri SMS_ALL = Uri.parse("content://sms/");

    /**
     * 获取手机中所有短信
     *
     * @param context      上下文
     * @param smsBlackList 黑名单
     * @param searchType   查询类型，0=除去黑名单的短信，1=黑名单短信，其他=所有短信
     *
     * @return 短信列表
     */
    public static List<SMS> getSmsFromPhone(Context context, Set<String> smsBlackList, int searchType) {

        List<SMS> smses = new ArrayList<>();

        // 查询参数组装
        StringBuilder searchKey = new StringBuilder();
        String[] searchValue = new String[smsBlackList.size()];
        Iterator<String> iterator = smsBlackList.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            if (i == smsBlackList.size() - 1) {
                searchKey.append("?");
            } else {
                searchKey.append("?,");
            }
            searchValue[i] = iterator.next();
            i++;
        }

        // 查询条件语句
        String searchSqlStr;
        if (searchType == 0) {
            searchSqlStr = "address not in (" + searchKey.toString() + ")";
        } else if (searchType == 1) {
            searchSqlStr = "address in (" + searchKey.toString() + ")";
        } else {
            searchSqlStr = null;
            searchValue = null;
        }

        // 查询
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(SMS_ALL, null, searchSqlStr, searchValue, "date desc");
        if (cursor == null) {
            return smses;
        }

        // 赋值
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex("address"));//手机号
            String name = cursor.getString(cursor.getColumnIndex("person"));//联系人姓名
            String body = cursor.getString(cursor.getColumnIndex("body"));//短信内容
            long date = cursor.getLong(cursor.getColumnIndex("date"));// 日期
            int type = cursor.getInt(cursor.getColumnIndex("type"));//类型

            SMS sms = new SMS(number, body, name, date, type);
            smses.add(sms);
        }
        cursor.close();

        return smses;
    }
}
