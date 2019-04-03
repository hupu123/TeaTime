package com.hugh.teatime.models.comic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.geek.thread.GeekThreadManager;
import com.geek.thread.ThreadPriority;
import com.geek.thread.ThreadType;
import com.geek.thread.task.GeekRunnable;
import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.models.home.FilePickActivity;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.view.FileImageView;
import com.hugh.teatime.view.TitlebarView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class ComicShelfActivity extends BaseActivity {

    private ListView lvComicList;
    private LinearLayout llEditeBar;

    private ArrayList<Comic> comics = new ArrayList<>();
    private CommonAdapter<Comic> mAdapter;
    private boolean isEditeMode = false;// 是否编辑模式，true=编辑模式，false=浏览模式
    private boolean isRefreshable = true;// 是否可以刷新，true=可以，false=不可以

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_shelf);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshData();
    }

    /**
     * activity结果返回监听
     *
     * @param requestCode 请求码
     * @param resultCode  结果状态码
     * @param data        返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == GlobalVar.REQUEST_CODE_COMIC_PATH && resultCode == RESULT_OK) {
            isRefreshable = false;// 当选择完漫画路径时，禁止在onResume中刷新数据，防止多线程导致的数据混乱
            final DialogUtil dialogUtil = new DialogUtil(this);
            dialogUtil.showDialog();
            GeekThreadManager.getInstance().execute(new GeekRunnable(ThreadPriority.NORMAL) {
                @Override
                public void run() {
                    String filePath = data.getStringExtra(FilePickActivity.INTENT_FILE_PATH);
                    if (!StringUtil.isStrNull(filePath)) {
                        // 判断选择路径是否存在漫画
                        Comic comic = getComicDataFromFile(new File(filePath));
                        if (comic != null && !MyDBOperater.getInstance(ComicShelfActivity.this).isComicExistByPath(comic.getPath())) {
                            // 保存漫画信息到数据库
                            MyDBOperater.getInstance(ComicShelfActivity.this).addComic(comic);
                        }
                        // 查询子目录是否存在漫画
                        searchDirForComicFolder(new File(filePath));
                    }
                    // 刷新数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isRefreshable = true;
                            refreshData();
                            dialogUtil.hideDialog();
                        }
                    });
                }
            }, ThreadType.NORMAL_THREAD);
        }
    }

    /**
     * 初始化数据
     */
    private void initView() {
        TitlebarView tbv = findViewById(R.id.tbv);
        tbv.setRightBtnText(getResources().getString(R.string.import_comic));
        lvComicList = findViewById(R.id.lv_comic_list);
        llEditeBar = findViewById(R.id.ll_edite_bar);
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnResetProgress = findViewById(R.id.btn_reset_progress);
        CheckBox cbSelectAll = findViewById(R.id.cb_select_all);

        btnCancel.setOnClickListener(clickListener);
        btnDelete.setOnClickListener(clickListener);
        btnResetProgress.setOnClickListener(clickListener);
        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (Comic comic : comics) {
                    comic.setChecked(isChecked);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                Intent intent = new Intent(ComicShelfActivity.this, FilePickActivity.class);
                startActivityForResult(intent, GlobalVar.REQUEST_CODE_COMIC_PATH);
            }
        });
        mAdapter = new CommonAdapter<Comic>(this, R.layout.item_comic_list, comics) {
            @Override
            protected void convert(ViewHolder viewHolder, Comic item, final int position) {
                CheckBox cbCheck = viewHolder.getView(R.id.cb_check);
                FileImageView fivCover = viewHolder.getView(R.id.fiv_cover);
                cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        comics.get(position).setChecked(isChecked);
                    }
                });
                if (item != null && !StringUtil.isStrNull(item.getCoverPath())) {
                    fivCover.setImageFile(item.getCoverPath(), 0.5f);
                    viewHolder.setText(R.id.tv_name, item.getName());
                    viewHolder.setText(R.id.tv_progress, String.format(getResources().getString(R.string.display_comic_read_progress), item.getProgress(), item.getPageTotal()));
                } else {
                    fivCover.setImageResource(R.mipmap.ic_launcher);
                    viewHolder.setText(R.id.tv_name, getResources().getString(R.string.display_unknown_name));
                    viewHolder.setText(R.id.tv_progress, getResources().getString(R.string.display_unknown_progress));
                    cbCheck.setChecked(false);
                }
                if (isEditeMode) {
                    cbCheck.setVisibility(View.VISIBLE);
                    if (item == null) {
                        cbCheck.setChecked(false);
                    } else {
                        cbCheck.setChecked(item.isChecked());
                    }
                } else {
                    cbCheck.setVisibility(View.GONE);
                }
                if (position == SPUtil.getInstance(ComicShelfActivity.this).getComicPosition()) {
                    viewHolder.setBackgroundColor(R.id.ll_bg, ContextCompat.getColor(ComicShelfActivity.this, R.color.reading_bg));
                } else if (item != null && item.getProgress() == item.getPageTotal()) {
                    viewHolder.setBackgroundColor(R.id.ll_bg, ContextCompat.getColor(ComicShelfActivity.this, R.color.readed_bg));
                } else {
                    viewHolder.setBackgroundColor(R.id.ll_bg, ContextCompat.getColor(ComicShelfActivity.this, R.color.colorWhite));
                }
                LinearLayout llBg = viewHolder.getView(R.id.ll_bg);
                int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_comic_height);
                int margin = getResources().getDimensionPixelSize(R.dimen.item_margin_lr_width);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
                if (position == comics.size() - 1) {
                    params.setMargins(margin, margin, margin, margin);
                } else {
                    params.setMargins(margin, margin, margin, 0);
                }
                llBg.setLayoutParams(params);
            }
        };
        lvComicList.setAdapter(mAdapter);
        lvComicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isEditeMode) {
                    // 更新当前正在阅读漫画位置
                    SPUtil.getInstance(ComicShelfActivity.this).setComicPosition(position);
                    // 跳转漫画阅读页
                    Intent intent = new Intent(ComicShelfActivity.this, ComicDetailActivity.class);
                    intent.putExtra(GlobalVar.INTENT_COMIC_POSITION, position);
                    startActivity(intent);
                }
            }
        });
        lvComicList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isEditeMode) {
                    comics.get(position).setChecked(true);
                    isEditeMode = true;
                    llEditeBar.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    /**
     * 点击事件监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    for (Comic comic : comics) {
                        comic.setChecked(false);
                    }
                    break;
                case R.id.btn_delete:
                    Iterator<Comic> iterator = comics.iterator();
                    while (iterator.hasNext()) {
                        Comic comic = iterator.next();
                        if (comic.isChecked()) {
                            MyDBOperater.getInstance(ComicShelfActivity.this).deleteComicByID(comic.getComicId());
                            iterator.remove();
                        }
                    }
                    break;
                case R.id.btn_reset_progress:
                    for (Comic comic : comics) {
                        if (comic.isChecked()) {
                            comic.setProgress(0);
                            comic.setChecked(false);
                            MyDBOperater.getInstance(ComicShelfActivity.this).updateComic(comic);
                        }
                    }
                    break;
                default:
                    break;
            }
            isEditeMode = false;
            llEditeBar.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isEditeMode) {
                for (Comic comic : comics) {
                    comic.setChecked(false);
                }
                isEditeMode = false;
                llEditeBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (!isRefreshable) {
            return;
        }
        comics.clear();
        ArrayList<Comic> comicsDB = MyDBOperater.getInstance(this).getComics();
        if (comicsDB != null && comicsDB.size() > 0) {
            for (Comic comic : comicsDB) {
                File file = new File(comic.getPath());
                if (file.exists()) {
                    comics.add(comic);
                } else {
                    MyDBOperater.getInstance(this).deleteComicByID(comic.getComicId());
                }
            }
            // 判断下标是否越界
            int currentPosition = SPUtil.getInstance(this).getComicPosition();
            if (currentPosition >= comics.size()) {
                currentPosition = comics.size() - 1;
            }
            lvComicList.setSelection(currentPosition);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 遍历文件夹搜索漫画文件夹
     *
     * @param file 文件夹
     */
    private void searchDirForComicFolder(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileChild : files) {
                Comic comic = getComicDataFromFile(fileChild);
                if (comic == null) {
                    searchDirForComicFolder(fileChild);
                } else {
                    if (!MyDBOperater.getInstance(this).isComicExistByPath(comic.getPath())) {
                        // 保存漫画信息到数据库
                        MyDBOperater.getInstance(this).addComic(comic);
                    }
                }
            }
        }
    }

    /**
     * 获取漫画信息
     *
     * @param file 漫画文件夹
     * @return 漫画信息
     */
    private Comic getComicDataFromFile(File file) {
        // 判断是否为文件夹且文件夹是否存在
        if (file == null || !file.exists() || !file.isDirectory()) {
            return null;
        }
        File[] files = file.listFiles();
        // 判断文件夹是否为空
        if (files == null || files.length == 0) {
            return null;
        }
        // 判断文件类型是否为图片
        ArrayList<File> fileList = new ArrayList<>();
        for (File fileTemp : files) {
            String fileName = fileTemp.getName();
            String[] fileNames = fileName.split("\\.");
            if (fileNames.length >= 2) {
                String fileType = fileNames[fileNames.length - 1];
                if (fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("png")) {
                    fileList.add(fileTemp);
                }
            }
        }
        if (fileList.size() == 0) {
            return null;
        }

        Comic comic = new Comic();
        comic.setName(file.getName());
        comic.setPath(file.getPath());
        comic.setCoverPath(fileList.get(0).getPath());
        comic.setProgress(0);
        comic.setPageTotal(fileList.size());
        comic.setFileList(fileList);

        return comic;
    }
}
