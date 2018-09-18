package com.hugh.teatime.models.comic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.FileImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;


public class ComicDetailActivity extends BaseActivity {

    private SmartRefreshLayout srlComicContainer;
    private ListView lvComicList;
    private ImageView ivNoData;

    private ArrayList<Comic> comics = new ArrayList<>();// 漫画列表
    private int comicPosition;// 当前正在浏览漫画在漫画列表中的位置
    private Comic comic;// 当前正在浏览的漫画
    private int pageNum;// 当前页码
    private final int pageSize = 10;// 每页大小
    private int progress;// 当前漫画阅读进度
    private CommonAdapter<File> mAdapter;// 列表适配器
    private ArrayList<File> pageData = new ArrayList<>();// 漫画文件数据集

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_comic_detail2);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        srlComicContainer = findViewById(R.id.srl_comic_container);
        lvComicList = findViewById(R.id.lv_comic_list);
        ivNoData = findViewById(R.id.iv_no_data);

        srlComicContainer.setEnableRefresh(true);
        srlComicContainer.setEnableLoadMore(true);
        srlComicContainer.setEnableAutoLoadMore(false);
        srlComicContainer.setEnableLoadMoreWhenContentNotFull(true);
        srlComicContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh(false);
                srlComicContainer.finishRefresh();
            }
        });
        srlComicContainer.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore(false);
                srlComicContainer.finishLoadMore();
            }
        });
        lvComicList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    comic.setProgress(progress);
                    MyDBOperater.getInstance(ComicDetailActivity.this).updateComic(comic);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                progress = pageNum * pageSize + firstVisibleItem + visibleItemCount;
            }
        });
    }

    /**
     * 下拉刷新事件
     *
     * @param isOpenNewComic 是否打开新漫画，true=是，false=不是
     */
    private void refresh(boolean isOpenNewComic) {
        ArrayList<File> dataTemp;
        if (isOpenNewComic) {
            dataTemp = getDataByPage(comic.getFileList(), pageNum, pageSize);
        } else {
            dataTemp = getDataByPage(comic.getFileList(), pageNum - 1, pageSize);
        }
        if (dataTemp == null || dataTemp.size() == 0) {
            if (comicPosition == 0) {
                ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_no_more_data, true);
            } else {
                ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_on_the_top, true);
                comicPosition--;
                comic = comics.get(comicPosition);
                pageNum = (comic.getProgress() - comic.getProgress() % pageSize) / pageSize;
                refresh(true);
            }
        } else {
            if (!isOpenNewComic) {
                pageNum--;
            }
            pageData.clear();
            pageData.addAll(dataTemp);
            mAdapter.notifyDataSetChanged();
            lvComicList.post(new Runnable() {
                @Override
                public void run() {
                    lvComicList.setSelection(pageSize - 1);
                }
            });
        }
    }

    /**
     * 上拉加载更多事件
     *
     * @param isOpenNewComic 是否打开新漫画，true=是，false=不是
     */
    private void loadMore(boolean isOpenNewComic) {
        ArrayList<File> dataTemp;
        if (isOpenNewComic) {
            dataTemp = getDataByPage(comic.getFileList(), pageNum, pageSize);
        } else {
            dataTemp = getDataByPage(comic.getFileList(), pageNum + 1, pageSize);
        }
        if (dataTemp == null || dataTemp.size() == 0) {
            if (comicPosition == comics.size() - 1) {
                ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_no_more_data, true);
            } else {
                ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_on_the_bottom, true);
                comicPosition++;
                comic = comics.get(comicPosition);
                pageNum = (comic.getProgress() - comic.getProgress() % pageSize) / pageSize;
                loadMore(true);
            }
        } else {
            if (!isOpenNewComic) {
                pageNum++;
            }
            pageData.clear();
            pageData.addAll(dataTemp);
            mAdapter.notifyDataSetChanged();
            lvComicList.post(new Runnable() {
                @Override
                public void run() {
                    lvComicList.setSelection(0);
                }
            });
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        comics = (ArrayList<Comic>) intent.getSerializableExtra(GlobalVar.INTENT_COMIC_DATA_LIST);
        comicPosition = intent.getIntExtra(GlobalVar.INTENT_COMIC_POSITION, 0);
        if (comics == null || comics.size() == 0 || comicPosition < 0) {
            srlComicContainer.setVisibility(View.GONE);
            ivNoData.setVisibility(View.VISIBLE);
            return;
        }
        comic = comics.get(comicPosition);
        if (comic == null || comic.getPageTotal() == 0 || comic.getFileList() == null) {
            srlComicContainer.setVisibility(View.GONE);
            ivNoData.setVisibility(View.VISIBLE);
            return;
        }
        srlComicContainer.setVisibility(View.VISIBLE);
        ivNoData.setVisibility(View.GONE);

        pageNum = (comic.getProgress() - comic.getProgress() % pageSize) / pageSize;
        pageData = getDataByPage(comic.getFileList(), pageNum, pageSize);
        mAdapter = new CommonAdapter<File>(this, R.layout.item_comic_browse, pageData) {
            @Override
            protected void convert(ViewHolder viewHolder, File item, int position) {
                FileImageView fivComic = viewHolder.getView(R.id.fiv_comic);
                fivComic.setImageFile(item.getPath());
            }
        };
        lvComicList.setAdapter(mAdapter);
    }

    /**
     * 通过页码获取数据
     *
     * @param allData  所有数据
     * @param pageNum  页数
     * @param pageSize 每页条数
     * @return 单页数据
     */
    private ArrayList<File> getDataByPage(ArrayList<File> allData, int pageNum, int pageSize) {
        ArrayList<File> pageData = new ArrayList<>();
        if (allData == null || allData.size() == 0 || pageNum < 0 || pageSize < 0) {
            return pageData;
        }
        int total = allData.size();
        int start;
        int end;
        if (pageNum * pageSize < 0) {
            start = 0;
        } else {
            start = pageNum * pageSize;
        }
        if ((pageNum + 1) * pageSize > total) {
            end = total;
        } else {
            end = (pageNum + 1) * pageSize;
        }
        for (int i = 0; i < total; i++) {
            if (i >= start && i < end) {
                pageData.add(allData.get(i));
            }
        }
        return pageData;
    }
}
