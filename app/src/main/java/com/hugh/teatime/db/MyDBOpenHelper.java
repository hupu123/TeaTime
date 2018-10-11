package com.hugh.teatime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hugh.teatime.utils.LogUtil;

/**
 * 数据库打开帮助类
 * Created by Hugh on 2016/2/18 9:28
 */
public class MyDBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "teatime.db";
    //    private static final int DB_VERSION = 1;
    private static final int DB_VERSION = 2;// 增加事件表

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    MyDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 创建消息表
        db.execSQL("CREATE TABLE messages (_id INTEGER PRIMARY KEY AUTOINCREMENT, code INTEGER, msg TEXT, url TEXT, type INTEGER, time LONG);");
        // 创建新闻表
        db.execSQL("CREATE TABLE news (_newsid INTEGER PRIMARY KEY AUTOINCREMENT, msgid INTEGER, article TEXT, source TEXT, icon TEXT, detailurl TEXT);");
        // 创建菜谱表
        db.execSQL("CREATE TABLE cookbooks (_cookbookid INTEGER PRIMARY KEY AUTOINCREMENT, msgid INTEGER, name TEXT, info TEXT, icon TEXT, detailurl TEXT);");
        // 创建功能表
        db.execSQL("CREATE TABLE function (_functionid INTEGER PRIMARY KEY AUTOINCREMENT, msgid INTEGER, song TEXT, singer TEXT, name TEXT, author TEXT);");
        // 创建书籍表
        db.execSQL("CREATE TABLE books (_bookid INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, progress INTEGER, size INTEGER, path TEXT, type TEXT);");
        // 创建图片表
        db.execSQL("CREATE TABLE images (_imageid INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, path TEXT, foldername TEXT, folderpath TEXT);");
        // 创建账单表
        db.execSQL("CREATE TABLE bills (_billid INTEGER PRIMARY KEY AUTOINCREMENT, amount DOUBLE, note TEXT, iotype INTEGER, type INTEGER, typename TEXT, year INTEGER, month INTEGER, day INTEGER);");
        // 创建漫画表
        db.execSQL("CREATE TABLE comics (_comicid INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, path TEXT, coverpath TEXT, pagetotal INTEGER, progress INTEGER);");
        // 创建漫画文件列表表
        db.execSQL("CREATE TABLE comic_file_lists (_comicfileid INTEGER PRIMARY KEY AUTOINCREMENT, _comicid INTEGER, path TEXT, position INTEGER);");
        // 创建加油记录表
        db.execSQL("CREATE TABLE gasoline_records (_grecordid INTEGER PRIMARY KEY AUTOINCREMENT, date LONG, totalprice DOUBLE, unitprice DOUBLE, mileage DOUBLE, quantity DOUBLE, comment TEXT, model TEXT, invoice INTEGER, paymethod TEXT, carno TEXT, year INTEGER);");
        // 创建记事本表
        db.execSQL("CREATE TABLE events (_eventid INTEGER PRIMARY KEY AUTOINCREMENT, date LONG, title TEXT, content TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        LogUtil.logHugh("DB onUpgrade oldVersion=" + oldVersion + " newVersion=" + newVersion);
        switch (oldVersion) {
            case 1:
                db.execSQL("DROP TABLE IF EXISTS events");
                db.execSQL("CREATE TABLE events (_eventid INTEGER PRIMARY KEY AUTOINCREMENT, date LONG, title TEXT, content TEXT);");
                break;
            default:
                break;
        }
    }
}
