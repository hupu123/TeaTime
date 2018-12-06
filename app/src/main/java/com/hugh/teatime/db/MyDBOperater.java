package com.hugh.teatime.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.models.bill.Bill;
import com.hugh.teatime.models.book.Book;
import com.hugh.teatime.models.comic.Comic;
import com.hugh.teatime.models.gasoline.GasolineBean;
import com.hugh.teatime.models.note.EventBean;
import com.hugh.teatime.models.robot.CookBook;
import com.hugh.teatime.models.home.Folder;
import com.hugh.teatime.models.image.Image;
import com.hugh.teatime.models.robot.Message;
import com.hugh.teatime.models.robot.News;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.StringUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作帮助类
 * Created by Hugh on 2016/2/18 9:55
 */
public class MyDBOperater {

    /**
     * 本身实例
     */
    private static MyDBOperater instance;
    /**
     * 数据库对象
     */
    private SQLiteDatabase db;
    /**
     * 上下文
     */
    private Context context;

    /**
     * 私有的构造函数
     *
     * @param context 上下文
     */
    private MyDBOperater(Context context) {
        MyDBOpenHelper mDBOpenHelper = new MyDBOpenHelper(context);
        db = mDBOpenHelper.getReadableDatabase();
        this.context = context;
    }

    /**
     * 获取一个自身实例
     *
     * @param context 上下文
     * @return 自身实例
     */
    public static MyDBOperater getInstance(Context context) {
        if (instance == null) {
            instance = new MyDBOperater(context);
        }

        return instance;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if (db != null) {
            db.close();
        }

        if (instance != null) {
            instance = null;
        }
    }

    // ------------------------------------------------------ messages table ---------------------------------------------------

    /**
     * 插入一条消息到数据库
     *
     * @param message 消息对象
     */
    public void addMessage(Message message) {
        if (!db.isOpen()) {
            return;
        }

        if (message.getType() == 0) {
            switch (message.getCode()) {
                case GlobalVar.MSG_TYPE_TEXT:
                    db.execSQL("INSERT INTO messages(code, msg, type, time) VALUES(?,?,?,?)", new Object[]{message.getCode(), message.getMsg(), message.getType(), message.getTime()});
                    break;
                case GlobalVar.MSG_TYPE_URL:
                    db.execSQL("INSERT INTO messages(code, msg, url, type, time) VALUES(?,?,?,?,?)", new Object[]{message.getCode(), message.getMsg(), message.getUrl(), message.getType(), message.getTime()});
                    break;
                case GlobalVar.MSG_TYPE_NEWS:
                    db.execSQL("INSERT INTO messages(code, msg, type, time) VALUES(?,?,?,?)", new Object[]{message.getCode(), message.getMsg(), message.getType(), message.getTime()});

                    Cursor cursorNews = db.rawQuery("SELECT * FROM messages ORDER BY _id DESC", null);
                    if (cursorNews.moveToFirst()) {
                        int id = cursorNews.getInt(cursorNews.getColumnIndex("_id"));
                        List<News> newsList = message.getNewsList();
                        for (int i = 0; i < newsList.size(); i++) {
                            News news = newsList.get(i);
                            db.execSQL("INSERT INTO news(msgid, article, source, icon, detailurl) VALUES(?,?,?,?,?)", new Object[]{id, news.getArticle(), news.getSource(), news.getIcon(), news.getDetailurl()});
                        }
                    }
                    cursorNews.close();
                    break;
                case GlobalVar.MSG_TYPE_COOK_BOOK:
                    db.execSQL("INSERT INTO messages(code, msg, type, time) VALUES(?,?,?,?)", new Object[]{message.getCode(), message.getMsg(), message.getType(), message.getTime()});

                    Cursor cursorCB = db.rawQuery("SELECT * FROM messages ORDER BY _id DESC", null);
                    if (cursorCB.moveToFirst()) {
                        int id = cursorCB.getInt(cursorCB.getColumnIndex("_id"));
                        List<CookBook> cookBookList = message.getCookBookList();
                        for (int i = 0; i < cookBookList.size(); i++) {
                            CookBook cookBook = cookBookList.get(i);
                            db.execSQL("INSERT INTO cookbooks(msgid, name, info, icon, detailurl) VALUES(?,?,?,?,?)", new Object[]{id, cookBook.getName(), cookBook.getInfo(), cookBook.getIcon(), cookBook.getDetailurl()});
                        }
                    }
                    cursorCB.close();
                    break;
                case GlobalVar.MSG_TYPE_SONG:
                case GlobalVar.MSG_TYPE_POETRY:
                    db.execSQL("INSERT INTO messages(code, msg, type, time) VALUES(?,?,?,?)", new Object[]{message.getCode(), message.getMsg(), message.getType(), message.getTime()});
                    break;
                default:
                    break;
            }
        } else {
            db.execSQL("INSERT INTO messages(msg, type, time) VALUES(?,?,?)", new Object[]{message.getMsg(), message.getType(), message.getTime()});
        }
        // 增加查询偏移量
        GlobalVar.TEMP_OFF_SET++;
    }

    /**
     * 从数据库获取消息
     *
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return 消息集合
     */
    public List<Message> getMessages(int currentPage, int pageSize) {
        if (!db.isOpen()) {
            return null;
        }

        LogUtil.logIResult("TEMP_OFF_SET=" + GlobalVar.TEMP_OFF_SET + " currentPage=" + currentPage + " pageSize=" + pageSize);

        Cursor cursor = db.rawQuery("SELECT * FROM messages WHERE _id IN (SELECT _id FROM messages ORDER BY _id DESC LIMIT ?,?)", new String[]{(currentPage - 1) * pageSize + GlobalVar.TEMP_OFF_SET + "", pageSize + ""});

        List<Message> messageList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Message message = new Message();
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String msg = cursor.getString(cursor.getColumnIndex("msg"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            if (type == 0) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                int code = cursor.getInt(cursor.getColumnIndex("code"));
                switch (code) {
                    case GlobalVar.MSG_TYPE_TEXT:
                        break;
                    case GlobalVar.MSG_TYPE_URL:
                        String url = cursor.getString(cursor.getColumnIndex("url"));
                        message.setUrl(url);// 超链接
                        break;
                    case GlobalVar.MSG_TYPE_NEWS:
                        Cursor cursorNews = db.rawQuery("select * from news where msgid=?", new String[]{id + ""});
                        List<News> newsList = new ArrayList<>();
                        while (cursorNews.moveToNext()) {
                            News news = new News();
                            String article = cursorNews.getString(cursorNews.getColumnIndex("article"));
                            String source = cursorNews.getString(cursorNews.getColumnIndex("source"));
                            String icon = cursorNews.getString(cursorNews.getColumnIndex("icon"));
                            String detailurl = cursorNews.getString(cursorNews.getColumnIndex("detailurl"));

                            news.setArticle(article);
                            news.setSource(source);
                            news.setIcon(icon);
                            news.setDetailurl(detailurl);

                            newsList.add(news);
                        }
                        cursorNews.close();
                        message.setNewsList(newsList);// 新闻列表
                        break;
                    case GlobalVar.MSG_TYPE_COOK_BOOK:
                        Cursor cursorCB = db.rawQuery("select * from cookbooks where msgid=?", new String[]{id + ""});
                        List<CookBook> cookBookList = new ArrayList<>();
                        while (cursorCB.moveToNext()) {
                            CookBook cookBook = new CookBook();
                            String name = cursorCB.getString(cursorCB.getColumnIndex("name"));
                            String info = cursorCB.getString(cursorCB.getColumnIndex("info"));
                            String icon = cursorCB.getString(cursorCB.getColumnIndex("icon"));
                            String detailurl = cursorCB.getString(cursorCB.getColumnIndex("detailurl"));

                            cookBook.setName(name);
                            cookBook.setInfo(info);
                            cookBook.setIcon(icon);
                            cookBook.setDetailurl(detailurl);

                            cookBookList.add(cookBook);
                        }
                        cursorCB.close();
                        message.setCookBookList(cookBookList);// 菜谱列表
                        break;
                    case GlobalVar.MSG_TYPE_SONG:
                        break;
                    case GlobalVar.MSG_TYPE_POETRY:
                        break;
                    default:
                        break;
                }
                message.setCode(code);// 消息种类
            }
            message.setMsg(msg);// 文字消息
            message.setType(type);// 消息来源
            message.setTime(time);// 消息时间

            messageList.add(message);
        }
        cursor.close();

        return messageList;
    }
    // ------------------------------------------------------ messages table ---------------------------------------------------

    // ------------------------------------------------------ books table ---------------------------------------------------

    /**
     * 添加书籍
     *
     * @param book 书籍
     */
    public void addBook(Book book) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("INSERT INTO books(name,progress,size,path,type) VALUES(?,?,?,?,?)", new String[]{book.getName(), book.getProgress() + "", book.getSize() + "", book.getPath(), book.getType()});
    }

    /**
     * 通过书籍ID删除书籍
     *
     * @param id 书籍ID
     */
    public void deleteBookById(int id) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("DELETE FROM books WHERE _bookid=?", new String[]{id + ""});
    }

    /**
     * 通过书名查看书籍是否已经存在
     *
     * @param name 书名
     * @return true=存在，false=不存在
     */
    public boolean isBookExist(String name) {
        if (!db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM books WHERE name=?", new String[]{name});
        boolean isExist = cursor.moveToFirst();
        cursor.close();

        return isExist;
    }

    /**
     * 更新书籍进度
     *
     * @param bookId   书籍ID
     * @param progress 进度
     */
    public void updateBookProgress(int bookId, int progress) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("UPDATE books SET progress=? WHERE _bookid=?", new String[]{progress + "", bookId + ""});
    }

    //    /**
    //     * 更新书籍大小
    //     *
    //     * @param bookId 书籍ID
    //     * @param size   大小
    //     */
    //    public void updateBookSize(int bookId, int size) {
    //
    //        if (!db.isOpen()) {
    //            return;
    //        }
    //        db.execSQL("UPDATE books SET size=? WHERE _bookid=?", new String[]{size + "", bookId + ""});
    //    }

    /**
     * 获取书籍列表
     *
     * @return 书籍列表
     */
    public List<Book> getBooks() {
        if (!db.isOpen()) {
            return null;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM books", null);
        List<Book> books = new ArrayList<>();

        while (cursor.moveToNext()) {
            Book book = new Book();
            int bookId = cursor.getInt(cursor.getColumnIndex("_bookid"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int progress = cursor.getInt(cursor.getColumnIndex("progress"));
            int size = cursor.getInt(cursor.getColumnIndex("size"));
            String path = cursor.getString(cursor.getColumnIndex("path"));
            String type = cursor.getString(cursor.getColumnIndex("type"));

            book.setBookId(bookId);
            book.setName(name);
            book.setProgress(progress);
            book.setSize(size);
            book.setPath(path);
            book.setType(type);

            books.add(book);
        }
        cursor.close();

        return books;
    }
    // ------------------------------------------------------ books table ---------------------------------------------------

    // ------------------------------------------------------ images table ---------------------------------------------------

    /**
     * 添加图片
     *
     * @param img 图片
     */
    public void addImage(Image img) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("INSERT INTO images(name,path,foldername,folderpath) VALUES(?,?,?,?)", new String[]{img.getName(), img.getPath(), img.getFolderName(), img.getFolderPath()});
    }

    /**
     * 获取图片文件夹列表
     *
     * @return 图片文件夹列表
     */
    public List<Folder> getFolder() {
        if (!db.isOpen()) {
            return null;
        }

        List<Folder> folders = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT foldername,folderpath FROM images GROUP BY foldername", null);
        while (cursor.moveToNext()) {
            String folderName = cursor.getString(cursor.getColumnIndex("foldername"));
            String folderPath = cursor.getString(cursor.getColumnIndex("folderpath"));

            Folder folder = new Folder();
            folder.setName(folderName);
            folder.setPath(folderPath);

            folders.add(folder);
        }
        cursor.close();

        return folders;
    }

    /**
     * 通过文件夹名称获取图片信息
     *
     * @param folderName 文件夹名称
     * @return 图片信息
     */
    public List<Image> getImagesByFolder(String folderName) {
        if (!db.isOpen()) {
            return null;
        }

        List<Image> images = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM images WHERE foldername=? ORDER BY _imageid DESC", new String[]{folderName});
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String path = cursor.getString(cursor.getColumnIndex("path"));
            String folderPath = cursor.getString(cursor.getColumnIndex("folderpath"));

            Image image = new Image();
            image.setName(name);
            image.setPath(path);
            image.setFolderName(folderName);
            image.setFolderPath(folderPath);

            images.add(image);
        }
        cursor.close();

        return images;
    }

    /**
     * 清空图片表数据
     */
    public void clearImages() {
        if (!db.isOpen()) {
            return;
        }

        db.execSQL("DELETE FROM images");
        db.execSQL("UPDATE sqlite_sequence SET seq=0 WHERE name='images'");
    }
    // ------------------------------------------------------ images table ---------------------------------------------------

    // ------------------------------------------------------ bills table ---------------------------------------------------

    /**
     * 添加账单
     *
     * @param bill 账单信息
     */
    public void addBill(Bill bill) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("INSERT INTO bills(amount,note,iotype,type,typename,year,month,day) VALUES(?,?,?,?,?,?,?,?)", new String[]{bill.getAmount() + "", bill.getNote(), bill.getIoType() + "", bill.getType() + "", bill.getTypeName(), bill.getYear() + "", bill.getMonth() + "", bill.getDay() + ""});
    }

    /**
     * 通过ID删除账单
     *
     * @param id 账单ID
     */
    public void deleteBillByID(int id) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("DELETE FROM bills WHERE _billid=?", new String[]{id + ""});
    }

    /**
     * 通过ID修改账单
     *
     * @param bill 账单信息
     */
    public void updateBill(Bill bill) {
        if (!db.isOpen() || bill == null) {
            return;
        }
        db.execSQL("UPDATE bills SET amount=?,note=?,iotype=?,type=?,typename=?,year=?,month=?,day=? WHERE _billid=?", new String[]{bill.getAmount() + "", bill.getNote(), bill.getIoType() + "", bill.getType() + "", bill.getTypeName(), bill.getYear() + "", bill.getMonth() + "", bill.getDay() + "", bill.getId() + ""});
    }

    /**
     * 通过月份查询账单
     * 根据天数倒序排列
     *
     * @param monthForSearch 月份
     * @return 账单集合
     */
    public List<Bill> getBills(int monthForSearch, int yearForSearch) {
        List<Bill> bills = new ArrayList<>();
        if (!db.isOpen()) {
            return bills;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM bills WHERE month=? AND year=? ORDER BY day DESC", new String[]{monthForSearch + "", yearForSearch + ""});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_billid"));
            double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            String note = cursor.getString(cursor.getColumnIndex("note"));
            int ioType = cursor.getInt(cursor.getColumnIndex("iotype"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String typeName = cursor.getString(cursor.getColumnIndex("typename"));
            int year = cursor.getInt(cursor.getColumnIndex("year"));
            int month = cursor.getInt(cursor.getColumnIndex("month"));
            int day = cursor.getInt(cursor.getColumnIndex("day"));

            Bill bill = new Bill(id, amount, note, ioType, type, typeName, year, month, day);
            bills.add(bill);
        }
        cursor.close();

        return bills;
    }

    /**
     * 账单求和函数
     *
     * @param monthForSearch 求和月份
     * @param yearForSearch  求和年份
     * @param iotype         求和收支类型
     * @param type           求和类型
     * @return 和
     */
    public double getBillSum(int monthForSearch, int yearForSearch, int iotype, int type) {
        double amountSum = 0;
        if (!db.isOpen()) {
            return amountSum;
        }

        String sqlStr = "SELECT SUM(amount) AS amountsum FROM bills WHERE month=? AND year=? AND iotype=? AND type=?";
        Cursor cursor = db.rawQuery(sqlStr, new String[]{monthForSearch + "", yearForSearch + "", iotype + "", type + ""});
        if (cursor.moveToFirst()) {
            amountSum = cursor.getDouble(cursor.getColumnIndex("amountsum"));
        }
        cursor.close();

        return amountSum;
    }

    /**
     * 账单求和函数
     *
     * @param monthForSearch 求和月份
     * @param yearForSearch  求和年份
     * @param iotype         求和收支类型
     * @return 和
     */
    public double getBillSum(int monthForSearch, int yearForSearch, int iotype) {
        double amountSum = 0;
        if (!db.isOpen()) {
            return amountSum;
        }

        String sqlStr = "SELECT SUM(amount) AS amountsum FROM bills WHERE month=? AND year=? AND iotype=?";
        Cursor cursor = db.rawQuery(sqlStr, new String[]{monthForSearch + "", yearForSearch + "", iotype + ""});
        if (cursor.moveToFirst()) {
            amountSum = cursor.getDouble(cursor.getColumnIndex("amountsum"));
        }
        cursor.close();

        return amountSum;
    }

    /**
     * 账单求和函数
     *
     * @param dayForSearch   求和日
     * @param monthForSearch 求和月
     * @param yearForSearch  求和年
     * @param iotype         收支类型
     * @return 某日收入/支出总和
     */
    public double getBillSumByDay(int dayForSearch, int monthForSearch, int yearForSearch, int iotype) {
        double amountSum = 0;
        if (!db.isOpen()) {
            return amountSum;
        }

        String sqlStr = "SELECT SUM(amount) AS amountsum FROM bills WHERE day=? AND month=? AND year=? AND iotype=?";
        Cursor cursor = db.rawQuery(sqlStr, new String[]{dayForSearch + "", monthForSearch + "", yearForSearch + "", iotype + ""});
        if (cursor.moveToFirst()) {
            amountSum = cursor.getDouble(cursor.getColumnIndex("amountsum"));
        }
        cursor.close();

        return amountSum;
    }

    /**
     * 获取年份
     *
     * @return 年份数组
     */
    public int[] getYears() {
        if (!db.isOpen()) {
            return null;
        }

        String sqlStr = "SELECT * FROM bills GROUP BY year";
        Cursor cursor = db.rawQuery(sqlStr, null);
        int[] years = new int[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            years[i] = cursor.getInt(cursor.getColumnIndex("year"));
            i++;
        }
        cursor.close();

        return years;
    }
    // ------------------------------------------------------ bills table ---------------------------------------------------

    // ------------------------------------------------------ comics table ---------------------------------------------------

    /**
     * 添加漫画到数据库
     *
     * @param comic 漫画数据
     */
    public void addComic(Comic comic) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("INSERT INTO comics(name,path,coverpath,pagetotal,progress) VALUES(?,?,?,?,?)", new String[]{comic.getName(), comic.getPath(), comic.getCoverPath(), comic.getPageTotal() + "", comic.getProgress() + ""});
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid() FROM comics", null);
        if (cursor.moveToFirst()) {
            int comicId = cursor.getInt(0);
            comic.setComicId(comicId);
            addComicFileList(comic);
        }
        cursor.close();
    }

    /**
     * 通过ID删除漫画数据
     *
     * @param id 漫画ID
     */
    public void deleteComicByID(int id) {
        if (!db.isOpen()) {
            return;
        }
        // 删除漫画表中的漫画数据
        db.execSQL("DELETE FROM comics WHERE _comicid=?", new String[]{id + ""});
        // 删除漫画文件列表中的漫画数据
        db.execSQL("DELETE FROM comic_file_lists WHERE _comicid=?", new String[]{id + ""});
    }

    /**
     * 更新漫画数据
     *
     * @param comic 漫画数据
     */
    public void updateComic(Comic comic) {
        if (!db.isOpen() || comic == null) {
            return;
        }
        db.execSQL("UPDATE comics SET name=?,path=?,coverpath=?,pagetotal=?,progress=? WHERE _comicid=?", new String[]{comic.getName(), comic.getPath(), comic.getCoverPath(), comic.getPageTotal() + "", comic.getProgress() + "", comic.getComicId() + ""});
    }

    /**
     * 通过路径判断漫画是否已经存在
     *
     * @param path 漫画路径
     * @return true=存在，false=不存在
     */
    public boolean isComicExistByPath(String path) {
        if (!db.isOpen() || StringUtil.isStrNull(path)) {
            return true;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM comics WHERE path=?", new String[]{path});
        if (cursor.moveToFirst()) {
            return true;
        }
        cursor.close();

        return false;
    }

    /**
     * 获取漫画列表
     *
     * @return 漫画列表
     */
    public ArrayList<Comic> getComics() {
        if (!db.isOpen()) {
            return null;
        }

        ArrayList<Comic> comics = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM comics", null);
        while (cursor.moveToNext()) {
            int comicId = cursor.getInt(cursor.getColumnIndex("_comicid"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String path = cursor.getString(cursor.getColumnIndex("path"));
            String coverPath = cursor.getString(cursor.getColumnIndex("coverpath"));
            int pageTotal = cursor.getInt(cursor.getColumnIndex("pagetotal"));
            int progress = cursor.getInt(cursor.getColumnIndex("progress"));
            ArrayList<File> fileList = getComicFileListByComicId(comicId);

            Comic comic = new Comic(comicId, name, path, coverPath, pageTotal, progress, fileList);
            comics.add(comic);
        }
        cursor.close();

        return comics;
    }
    // ------------------------------------------------------ comics table ---------------------------------------------------

    // ------------------------------------------------------ comic_file_lists table ---------------------------------------------------

    /**
     * 添加漫画文件列表
     *
     * @param comic 漫画数据
     */
    private void addComicFileList(Comic comic) {
        if (!db.isOpen()) {
            return;
        }
        ArrayList<File> fileList = comic.getFileList();
        for (int i = 0; i < fileList.size(); i++) {
            db.execSQL("INSERT INTO comic_file_lists(_comicid,path,position) VALUES(?,?,?)", new String[]{comic.getComicId() + "", fileList.get(i).getPath(), i + ""});
        }
    }

    /**
     * 获取漫画文件列表
     *
     * @param comicId 漫画ID
     * @return 漫画文件列表
     */
    private ArrayList<File> getComicFileListByComicId(int comicId) {
        if (!db.isOpen()) {
            return null;
        }

        ArrayList<File> fileList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM comic_file_lists WHERE _comicid=?", new String[]{comicId + ""});
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex("path"));
            fileList.add(new File(path));
        }
        cursor.close();

        return fileList;
    }
    // ------------------------------------------------------ comic_file_lists table ---------------------------------------------------

    // ------------------------------------------------------ gasoline_records table ---------------------------------------------------

    /**
     * 添加加油记录
     *
     * @param gasolineBean 加油记录数据
     */
    public void addGasolineRecord(GasolineBean gasolineBean) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("INSERT INTO gasoline_records(date,totalprice,unitprice,mileage,quantity,comment,model,invoice,paymethod,carno,year,latitude,longitude,address,citycode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new String[]{String.valueOf(gasolineBean.getDate()), String.valueOf(gasolineBean.getTotalPrice().doubleValue()), String.valueOf(gasolineBean.getUnitPrice().doubleValue()), String.valueOf(gasolineBean.getMileage()), String.valueOf(gasolineBean.getQuantity()), gasolineBean.getComment(), gasolineBean.getModel(), String.valueOf(gasolineBean.getInvoice()), gasolineBean.getPayMethod(), gasolineBean.getCarNO(), String.valueOf(gasolineBean.getYear()), String.valueOf(gasolineBean.getLatitude()), String.valueOf(gasolineBean.getLongitude()), gasolineBean.getAddress(), gasolineBean.getCityCode()});
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid() FROM gasoline_records", null);
        if (cursor.moveToFirst()) {
            int gasolineId = cursor.getInt(0);
            gasolineBean.setId(String.valueOf(gasolineId));
            addEvent(Gasoline2Event(gasolineBean));
        }
        cursor.close();
    }

    /**
     * 更新加油记录
     *
     * @param gasolineBean 加油记录数据
     */
    public void updateGasolineRecord(GasolineBean gasolineBean) {
        if (!db.isOpen() || gasolineBean == null) {
            return;
        }
        db.execSQL("UPDATE gasoline_records SET date=?,totalprice=?,unitprice=?,mileage=?,quantity=?,comment=?,model=?,invoice=?,paymethod=?,carno=?,year=?,latitude=?,longitude=?,address=?,citycode=? WHERE _grecordid=?", new String[]{String.valueOf(gasolineBean.getDate()), String.valueOf(gasolineBean.getTotalPrice().doubleValue()), String.valueOf(gasolineBean.getUnitPrice().doubleValue()), String.valueOf(gasolineBean.getMileage()), gasolineBean.getQuantity() + "", gasolineBean.getComment(), gasolineBean.getModel(), String.valueOf(gasolineBean.getInvoice()), gasolineBean.getPayMethod(), gasolineBean.getCarNO(), String.valueOf(gasolineBean.getYear()), String.valueOf(gasolineBean.getLatitude()), String.valueOf(gasolineBean.getLongitude()), gasolineBean.getAddress(), gasolineBean.getCityCode(), gasolineBean.getId()});
        updateGasolineInEvent(Gasoline2Event(gasolineBean));
    }

    /**
     * 删除加油记录
     *
     * @param id 记录ID
     */
    public void deleteGasolineRecord(String id) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("DELETE FROM gasoline_records WHERE _grecordid=?", new String[]{id});
        deleteGasolineInEvent(id);
    }

    /**
     * 按条件获取加油记录
     *
     * @param key   条件类型 0=全部，1=按车牌号，2=汽油型号，3=开票状态
     * @param value 条件值
     * @return 加油记录
     */
    public ArrayList<GasolineBean> getGasolineRecords(int key, String value) {
        if (!db.isOpen()) {
            return null;
        }

        String sql;
        String[] values;
        switch (key) {
            case 1:
                sql = "SELECT * FROM gasoline_records WHERE carno=? ORDER BY date DESC";
                values = new String[]{value};
                break;
            case 2:
                sql = "SELECT * FROM gasoline_records WHERE model=? ORDER BY date DESC";
                values = new String[]{value};
                break;
            case 3:
                sql = "SELECT * FROM gasoline_records WHERE invoice=? ORDER BY date DESC";
                values = new String[]{value};
                break;
            default:
                sql = "SELECT * FROM gasoline_records ORDER BY date DESC";
                values = new String[]{};
                break;
        }

        ArrayList<GasolineBean> records = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, values);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_grecordid"));
            long date = cursor.getLong(cursor.getColumnIndex("date"));
            double totalPrice = cursor.getDouble(cursor.getColumnIndex("totalprice"));
            double unitPrice = cursor.getDouble(cursor.getColumnIndex("unitprice"));
            double mileage = cursor.getDouble(cursor.getColumnIndex("mileage"));
            double quantity = cursor.getDouble(cursor.getColumnIndex("quantity"));
            String comment = cursor.getString(cursor.getColumnIndex("comment"));
            String model = cursor.getString(cursor.getColumnIndex("model"));
            int invoice = cursor.getInt(cursor.getColumnIndex("invoice"));
            String payMethod = cursor.getString(cursor.getColumnIndex("paymethod"));
            String carNO = cursor.getString(cursor.getColumnIndex("carno"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String cityCode = cursor.getString(cursor.getColumnIndex("citycode"));

            GasolineBean gasolineBean = new GasolineBean(String.valueOf(id), date, new BigDecimal(totalPrice), new BigDecimal(unitPrice), mileage, quantity, comment, model, invoice, payMethod, carNO, latitude, longitude, address, cityCode);
            records.add(gasolineBean);
        }
        cursor.close();

        return records;
    }

    /**
     * 按条件获取加油记录
     *
     * @param key   条件类型 0=全部，1=按车牌号，2=汽油型号，3=开票状态
     * @param value 条件值
     * @param year  年份
     * @return 加油记录
     */
    public ArrayList<GasolineBean> getGasolineRecords(int key, String value, int year) {
        if (!db.isOpen()) {
            return null;
        }

        String sql;
        String[] values;
        switch (key) {
            case 1:
                sql = "SELECT * FROM gasoline_records WHERE carno=? AND year=? ORDER BY date ASC";
                values = new String[]{value, year + ""};
                break;
            case 2:
                sql = "SELECT * FROM gasoline_records WHERE model=? AND year=? ORDER BY date ASC";
                values = new String[]{value, year + ""};
                break;
            case 3:
                sql = "SELECT * FROM gasoline_records WHERE invoice=? AND year=? ORDER BY date ASC";
                values = new String[]{value, year + ""};
                break;
            default:
                sql = "SELECT * FROM gasoline_records WHERE year=? ORDER BY date ASC";
                values = new String[]{year + ""};
                break;
        }

        ArrayList<GasolineBean> records = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, values);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_grecordid"));
            long date = cursor.getLong(cursor.getColumnIndex("date"));
            double totalPrice = cursor.getDouble(cursor.getColumnIndex("totalprice"));
            double unitPrice = cursor.getDouble(cursor.getColumnIndex("unitprice"));
            double mileage = cursor.getDouble(cursor.getColumnIndex("mileage"));
            double quantity = cursor.getDouble(cursor.getColumnIndex("quantity"));
            String comment = cursor.getString(cursor.getColumnIndex("comment"));
            String model = cursor.getString(cursor.getColumnIndex("model"));
            int invoice = cursor.getInt(cursor.getColumnIndex("invoice"));
            String payMethod = cursor.getString(cursor.getColumnIndex("paymethod"));
            String carNO = cursor.getString(cursor.getColumnIndex("carno"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String cityCode = cursor.getString(cursor.getColumnIndex("citycode"));

            GasolineBean gasolineBean = new GasolineBean(String.valueOf(id), date, new BigDecimal(totalPrice), new BigDecimal(unitPrice), mileage, quantity, comment, model, invoice, payMethod, carNO, latitude, longitude, address, cityCode);
            records.add(gasolineBean);
        }
        cursor.close();

        return records;
    }

    /**
     * 通过记录ID查询加油记录数据
     *
     * @param id 加油记录ID
     * @return 加油记录数据
     */
    public GasolineBean getGasolineRecordById(String id) {
        if (!db.isOpen()) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM gasoline_records WHERE _grecordid=?", new String[]{id});
        if (cursor.moveToFirst()) {
            int grecordId = cursor.getInt(cursor.getColumnIndex("_grecordid"));
            long date = cursor.getLong(cursor.getColumnIndex("date"));
            double totalPrice = cursor.getDouble(cursor.getColumnIndex("totalprice"));
            double unitPrice = cursor.getDouble(cursor.getColumnIndex("unitprice"));
            double mileage = cursor.getDouble(cursor.getColumnIndex("mileage"));
            double quantity = cursor.getDouble(cursor.getColumnIndex("quantity"));
            String comment = cursor.getString(cursor.getColumnIndex("comment"));
            String model = cursor.getString(cursor.getColumnIndex("model"));
            int invoice = cursor.getInt(cursor.getColumnIndex("invoice"));
            String payMethod = cursor.getString(cursor.getColumnIndex("paymethod"));
            String carNO = cursor.getString(cursor.getColumnIndex("carno"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String cityCode = cursor.getString(cursor.getColumnIndex("citycode"));

            return new GasolineBean(String.valueOf(grecordId), date, new BigDecimal(totalPrice), new BigDecimal(unitPrice), mileage, quantity, comment, model, invoice, payMethod, carNO, latitude, longitude, address, cityCode);
        }
        cursor.close();
        return null;
    }

    /**
     * 获取所有车辆
     *
     * @return 所有车牌号
     */
    public ArrayList<String> getCars() {
        if (!db.isOpen()) {
            return null;
        }

        ArrayList<String> cars = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT carno FROM gasoline_records GROUP BY carno", new String[]{});
        while (cursor.moveToNext()) {
            String carNO = cursor.getString(cursor.getColumnIndex("carno"));
            cars.add(carNO);
        }
        cursor.close();

        return cars;
    }

    /**
     * 获取所有汽油类型
     *
     * @return 所有汽油型号
     */
    public ArrayList<String> getModels() {
        if (!db.isOpen()) {
            return null;
        }

        ArrayList<String> models = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT model FROM gasoline_records GROUP BY model", new String[]{});
        while (cursor.moveToNext()) {
            String model = cursor.getString(cursor.getColumnIndex("model"));
            models.add(model);
        }
        cursor.close();

        return models;
    }

    /**
     * 获取所有年份
     *
     * @return 所有年份
     */
    public ArrayList<String> getGRYears() {
        if (!db.isOpen()) {
            return null;
        }

        ArrayList<String> years = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT year FROM gasoline_records GROUP BY year", new String[]{});
        while (cursor.moveToNext()) {
            int year = cursor.getInt(cursor.getColumnIndex("year"));
            years.add(year + "");
        }
        cursor.close();

        return years;
    }

    /**
     * 根据条件求和
     *
     * @param type  条件类型 0=根据车牌号，1=根据开票状态
     * @param year  年份
     * @param value 条件值
     * @return 总和
     */
    public double getSumOfPrice(int type, int year, String value) {
        double sum = 0;
        if (!db.isOpen()) {
            return sum;
        }

        String sql;
        String[] values;
        if (type == 0) {
            sql = "SELECT SUM(totalprice) AS sum_price FROM gasoline_records WHERE year=? AND carno=?";
            values = new String[]{year + "", value};
        } else {
            sql = "SELECT SUM(totalprice) AS sum_price FROM gasoline_records WHERE year=? AND invoice=?";
            values = new String[]{year + "", value};
        }

        Cursor cursor = db.rawQuery(sql, values);
        if (cursor.moveToNext()) {
            sum = cursor.getDouble(cursor.getColumnIndex("sum_price"));
        }
        cursor.close();

        return sum;
    }

    /**
     * Gasoline对象转换为Event对象
     *
     * @param gasolineBean 加油记录数据
     * @return 事件数据
     */
    public EventBean Gasoline2Event(GasolineBean gasolineBean) {
        String invoiceStr = gasolineBean.getInvoice() == 0 ? context.getResources().getString(R.string.have_invoiced) : context.getResources().getString(R.string.have_not_invoiced);
        EventBean eventBean = new EventBean();
        eventBean.setDate(gasolineBean.getDate());
        eventBean.setTitle(String.format(context.getResources().getString(R.string.gasoline_title), gasolineBean.getCarNO(), StringUtil.formatBigDecimalNum(gasolineBean.getTotalPrice())));
        eventBean.setContent(String.format(context.getResources().getString(R.string.gasoline_content), StringUtil.formatBigDecimalNum(gasolineBean.getUnitPrice()), String.valueOf(gasolineBean.getMileage()), String.valueOf(gasolineBean.getQuantity()), gasolineBean.getModel(), gasolineBean.getPayMethod(), invoiceStr, gasolineBean.getComment()));
        eventBean.setLatitude(gasolineBean.getLatitude());
        eventBean.setLongitude(gasolineBean.getLongitude());
        eventBean.setAddress(gasolineBean.getAddress());
        eventBean.setCityCode(gasolineBean.getCityCode());
        eventBean.setEventType(EventBean.TYPE_GASOLINE);
        eventBean.setGasolineId(gasolineBean.getId());
        return eventBean;
    }
    // ------------------------------------------------------ gasoline_records table ---------------------------------------------------

    // ------------------------------------------------------ events table ---------------------------------------------------

    /**
     * 添加事件
     *
     * @param eventBean 事件数据
     */
    public void addEvent(EventBean eventBean) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("INSERT INTO events(date, title, content, latitude, longitude, address, citycode, type, gasolineid) VALUES(?,?,?,?,?,?,?,?,?)", new String[]{String.valueOf(eventBean.getDate()), eventBean.getTitle(), eventBean.getContent(), String.valueOf(eventBean.getLatitude()), String.valueOf(eventBean.getLongitude()), eventBean.getAddress(), eventBean.getCityCode(), String.valueOf(eventBean.getEventType()), eventBean.getGasolineId()});
    }

    /**
     * 更新事件
     *
     * @param eventBean 事件数据
     */
    public void updateEvent(EventBean eventBean) {
        if (!db.isOpen() || eventBean == null) {
            return;
        }
        db.execSQL("UPDATE events SET date=?,title=?,content=?,latitude=?,longitude=?,address=?,citycode=?,type=?,gasolineid=? WHERE _eventid=?", new String[]{String.valueOf(eventBean.getDate()), eventBean.getTitle(), eventBean.getContent(), String.valueOf(eventBean.getLatitude()), String.valueOf(eventBean.getLongitude()), eventBean.getAddress(), eventBean.getCityCode(), String.valueOf(eventBean.getEventType()), eventBean.getGasolineId(), eventBean.getId()});
    }

    /**
     * 更新事件表中的加油记录数据
     *
     * @param eventBean 事件对象
     */
    private void updateGasolineInEvent(EventBean eventBean) {
        if (!db.isOpen() || eventBean == null) {
            return;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM events WHERE gasolineid=?", new String[]{eventBean.getGasolineId()});
        if (cursor.moveToFirst()) {
            db.execSQL("UPDATE events SET date=?,title=?,content=?,latitude=?,longitude=?,address=?,citycode=?,type=? WHERE gasolineid=?", new String[]{String.valueOf(eventBean.getDate()), eventBean.getTitle(), eventBean.getContent(), String.valueOf(eventBean.getLatitude()), String.valueOf(eventBean.getLongitude()), eventBean.getAddress(), eventBean.getCityCode(), String.valueOf(eventBean.getEventType()), eventBean.getGasolineId()});
        } else {
            addEvent(eventBean);
        }
        cursor.close();
    }

    /**
     * 删除事件
     *
     * @param id 事件ID
     */
    public void deleteEvent(String id) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("DELETE FROM events WHERE _eventid=?", new String[]{id});
    }

    /**
     * 删除事件表中的加油记录数据
     *
     * @param id 加油记录ID
     */
    private void deleteGasolineInEvent(String id) {
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("DELETE FROM events WHERE gasolineid=?", new String[]{id});
    }

    /**
     * 获取所有事件
     *
     * @return 事件集合
     */
    public ArrayList<EventBean> getEvents(int pageIndex, int pageSize) {
        if (!db.isOpen()) {
            return null;
        }
        ArrayList<EventBean> eventBeans = new ArrayList<>();
        // 查询事件数据
        Cursor cursor = db.rawQuery("SELECT * FROM (SELECT * FROM events ORDER BY date DESC LIMIT ? OFFSET ?) ORDER BY date ASC", new String[]{String.valueOf(pageSize), String.valueOf(pageIndex * pageSize)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_eventid"));
            long date = cursor.getLong(cursor.getColumnIndex("date"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String citycode = cursor.getString(cursor.getColumnIndex("citycode"));
            int eventType = cursor.getInt(cursor.getColumnIndex("type"));
            String gasolineId = cursor.getString(cursor.getColumnIndex("gasolineid"));

            EventBean eventBean = new EventBean(String.valueOf(id), date, title, content, latitude, longitude, address, citycode, eventType, gasolineId);
            eventBeans.add(eventBean);
        }
        cursor.close();

        return eventBeans;
    }
// ------------------------------------------------------ events table ---------------------------------------------------
}
