package com.hugh.teatime.models.comic;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.DimensUtil;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.FileImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;


public class ComicDetailActivity extends BaseActivity {

    private RelativeLayout rlComicBrowser;
    private SmartRefreshLayout srlComicContainer;
    private ListView lvComicList;
    private TextView tvProgress;
    private ImageView ivNoData;

    private ComicsData comicsData;                          // 漫画集
    private Comic comic;                                    // 当前正在浏览的漫画
    private int progress;                                   // 当前漫画阅读进度
    private CommonAdapter<File> mAdapter;                   // 列表适配器
    private ArrayList<File> pageData = new ArrayList<>();   // 漫画文件数据集

    private final long POST_DELAY_TIME = 500;               // 延迟跳转时间（用于跳转到指定阅读进度，不延迟跳转可能导致跳转失败）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_comic_detail);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rlComicBrowser = findViewById(R.id.rl_comic_browser);
        srlComicContainer = findViewById(R.id.srl_comic_container);
        lvComicList = findViewById(R.id.lv_comic_list);
        tvProgress = findViewById(R.id.tv_progress);
        ivNoData = findViewById(R.id.iv_no_data);

        srlComicContainer.setEnableRefresh(true);
        srlComicContainer.setEnableLoadMore(true);
        srlComicContainer.setEnableAutoLoadMore(false);
        srlComicContainer.setEnableLoadMoreWhenContentNotFull(true);
        srlComicContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
                srlComicContainer.finishRefresh();
            }
        });
        srlComicContainer.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
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
                progress = firstVisibleItem + visibleItemCount;
                if (comic != null) {
                    tvProgress.setText(String.format(getResources().getString(R.string.comic_progress), progress, comic.getPageTotal()));
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        comicsData = (ComicsData) intent.getSerializableExtra(GlobalVar.INTENT_COMICS_DATA);
        if (comicsData == null || comicsData.getComics() == null || comicsData.getComics().size() == 0 || comicsData.getComicPosition() < 0) {
            rlComicBrowser.setVisibility(View.GONE);
            ivNoData.setVisibility(View.VISIBLE);
            return;
        }
        comic = comicsData.getCurrentComic();
        if (comic == null || comic.getPageTotal() == 0 || comic.getFileList() == null) {
            rlComicBrowser.setVisibility(View.GONE);
            ivNoData.setVisibility(View.VISIBLE);
            return;
        }
        rlComicBrowser.setVisibility(View.VISIBLE);
        ivNoData.setVisibility(View.GONE);

        tvProgress.setText(String.format(getResources().getString(R.string.comic_progress), comic.getProgress(), comic.getPageTotal()));
        pageData = comic.getFileList();
        mAdapter = new CommonAdapter<File>(this, R.layout.item_comic_browse, pageData) {
            @Override
            protected void convert(ViewHolder viewHolder, File item, int position) {
                FileImageView fivComic = viewHolder.getView(R.id.fiv_comic);
                SubsamplingScaleImageView ssiv = viewHolder.getView(R.id.ssiv_comic);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(item.getPath(), options);
                int sWidth = DimensUtil.getInstance(ComicDetailActivity.this).getScreenWidth();
                int sHeight = DimensUtil.getInstance(ComicDetailActivity.this).getScreenHeight();
                int fWidth = options.outWidth;
                int fHeight = options.outHeight;
                fHeight = sWidth * fHeight / fWidth;
                // 如果图片高度大于4倍屏幕高度，则视为长图，使用大图控件显示
                if (fHeight > sHeight * 4) {
                    fivComic.setVisibility(View.GONE);
                    ssiv.setVisibility(View.VISIBLE);
                    ssiv.setImage(ImageSource.uri(item.getPath()));
                } else {
                    fivComic.setVisibility(View.VISIBLE);
                    ssiv.setVisibility(View.GONE);
                    fivComic.setImageFile(item.getPath());
                }
            }
        };
        lvComicList.setAdapter(mAdapter);
        lvComicList.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvComicList.setSelection(comic.getProgress());
                LogUtil.logHugh("initData progress=" + comic.getProgress());
            }
        }, POST_DELAY_TIME);
    }

    /**
     * 下拉刷新事件
     */
    private void refresh() {
        if (comicsData.getComicPosition() == 0) {
            ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_no_more_data, true);
        } else {
            ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_on_the_top, true);
            comicsData.setComicPosition(comicsData.getComicPosition() - 1);
            comic = comicsData.getCurrentComic();
            ArrayList<File> dataTemp = comic.getFileList();
            if (dataTemp != null && dataTemp.size() > 0) {
                pageData.clear();
                pageData.addAll(dataTemp);
                mAdapter.notifyDataSetChanged();
                lvComicList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lvComicList.setSelection(comic.getProgress());
                        LogUtil.logHugh("refresh progress=" + comic.getProgress());
                    }
                }, POST_DELAY_TIME);
            } else {
                ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_no_data, true);
            }
        }
    }

    /**
     * 上拉加载更多事件
     */
    private void loadMore() {
        if (comicsData.getComicPosition() == comicsData.getComics().size() - 1) {
            ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_no_more_data, true);
        } else {
            ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_on_the_bottom, true);
            comicsData.setComicPosition(comicsData.getComicPosition() + 1);
            comic = comicsData.getCurrentComic();
            ArrayList<File> dataTemp = comic.getFileList();
            if (dataTemp != null && dataTemp.size() > 0) {
                pageData.clear();
                pageData.addAll(dataTemp);
                mAdapter.notifyDataSetChanged();
                lvComicList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lvComicList.setSelection(comic.getProgress());
                        LogUtil.logHugh("loadMore progress=" + comic.getProgress());
                    }
                }, POST_DELAY_TIME);
            } else {
                ToastUtil.showInfo(ComicDetailActivity.this, R.string.toast_comic_no_data, true);
            }
        }
    }
}
