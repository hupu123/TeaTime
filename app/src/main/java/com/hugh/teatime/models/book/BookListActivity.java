package com.hugh.teatime.models.book;

import android.content.Intent;
import android.os.Vibrator;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.models.home.FilePickActivity;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.listener.DialogListener;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.ReadBookUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.utils.ToolUtil;
import com.hugh.teatime.view.TitlebarView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 书籍列表页
 */
public class BookListActivity extends BaseActivity {

    public static final String BOOK_DETAIL_DATA = "book_detail_data";
    private ListView lvBookList;
    private final int INTENT_SELECT_BOOKS_CODE = 997;
    private BookListAdapter bla;
    private List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        initView();
        initData();
    }

    @Override
    protected void onResume() {

        super.onResume();
        refreshData();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        lvBookList = findViewById(R.id.lv_book_list);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                Intent intent = new Intent(BookListActivity.this, FilePickActivity.class);
                startActivityForResult(intent, INTENT_SELECT_BOOKS_CODE);
            }
        });
        lvBookList.setOnItemClickListener(itemClickListener);
        lvBookList.setOnItemLongClickListener(itemLongClickListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        books = new ArrayList<>();
        bla = new BookListAdapter(this, books);
        lvBookList.setAdapter(bla);
    }

    /**
     * ListItem点击监听
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            if (ReadBookUtil.isRunning()) {
                DialogUtil dialogUtil = new DialogUtil(BookListActivity.this, R.mipmap.icon_info_g, getResources().getString(R.string.dialog_is_switch_book), new DialogListener() {

                    @Override
                    public void sure() {

                        openBook(position);
                    }

                    @Override
                    public void cancel() {

                        // DO NOTHING
                    }
                });
                dialogUtil.showDialog();
            } else {
                openBook(position);
            }
        }
    };

    /**
     * ListItem长按监听
     */
    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(300);
            final Book book = books.get(position);
            DialogUtil dialogUtil = new DialogUtil(BookListActivity.this, R.mipmap.icon_delet_g, String.format(getResources().getString(R.string.dialog_is_delete_book), book.getName()), new DialogListener() {

                @Override
                public void sure() {

                    books.remove(position);
                    bla.notifyDataSetChanged();
                    // 删除数据库记录
                    MyDBOperater.getInstance(BookListActivity.this).deleteBookById(book.getBookId());
                }

                @Override
                public void cancel() {

                    // DO NOTHING
                }
            });
            dialogUtil.showDialog();

            return true;
        }
    };

    /**
     * 打开书籍
     *
     * @param position 书籍位置
     */
    private void openBook(int position) {

        // 关闭一次听书工具，保证打开新工具
        ReadBookUtil.getInstance(BookListActivity.this).cancel();

        Book book = books.get(position);
        Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
        intent.putExtra(BOOK_DETAIL_DATA, book);
        startActivity(intent);
    }

    /**
     * 刷新书籍信息
     */
    private void refreshData() {

        List<Book> booksTemp = MyDBOperater.getInstance(BookListActivity.this).getBooks();
        if (booksTemp != null && booksTemp.size() > 0) {
            books.clear();
            books.addAll(booksTemp);
            bla.notifyDataSetChanged();
        }
    }

    /**
     * 资源访问回调数据监听
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == INTENT_SELECT_BOOKS_CODE) {
            String filePath = data.getStringExtra(FilePickActivity.INTENT_FILE_PATH);
            File fileDir = new File(filePath);
            if (fileDir.isDirectory()) {
                File[] files = fileDir.listFiles();
                boolean isProblem = false;// 用于判断是否存在已自动过滤的书籍
                for (File file : files) {
                    Book book = ToolUtil.getBookInfo(file.getPath());
                    if (book != null && !MyDBOperater.getInstance(this).isBookExist(book.getName())) {
                        MyDBOperater.getInstance(this).addBook(book);
                    } else {
                        isProblem = true;
                    }
                }
                if (isProblem) {
                    ToastUtil.showInfo(this, R.string.toast_filtrated_book, true);
                }
            } else {
                Book book = ToolUtil.getBookInfo(filePath);
                if (book != null && !MyDBOperater.getInstance(this).isBookExist(book.getName())) {
                    MyDBOperater.getInstance(this).addBook(book);
                } else {
                    ToastUtil.showInfo(this, R.string.toast_not_support_book, true);
                }
            }
            refreshData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
