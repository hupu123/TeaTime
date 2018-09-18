package com.hugh.teatime.models.book;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.listener.DialogListener;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.ReadBookUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ThreadPoolUtil;
import com.hugh.teatime.utils.ToolUtil;
import com.hugh.teatime.view.TitlebarView;

/**
 * 书籍详情页
 */
public class BookDetailActivity extends BaseActivity {

    private Book book;// 书籍信息
    private String bookContent;// 书籍内容
    private GestureDetector mGestureDetector;// 手势监听器
    private int progressPick;// 进度选择
    private boolean isBtnStart = true;// 按钮是否为开始播放状态
    private boolean isFullScreen = false;// 是否全屏状态，true=全屏,false=非全屏
    private boolean isNightModel = false;// 是否夜间模式，true=是，false=不是
    private boolean isProgressBarShow = false;// 进度选择栏是否显示，true=显示中，false=未显示
    private Handler handler = new Handler();

    private TitlebarView tbv;
    private LinearLayout llBtnBar;
    private LinearLayout llProgressBar;
    private TextView tvBookContent;
    private Button btnBdControler;
    private Button btnBdModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra(BookListActivity.BOOK_DETAIL_DATA);

        initView();
        loadBook();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        tbv = findViewById(R.id.tbv);
        llBtnBar = findViewById(R.id.ll_book_detail_btn_bar);
        llProgressBar = findViewById(R.id.ll_book_detail_progress_bar);
        SeekBar sbProgressPick = findViewById(R.id.sb_progress_pick);
        tvBookContent = findViewById(R.id.tv_book_content);
        btnBdControler = findViewById(R.id.btn_bd_controler);
        btnBdModel = findViewById(R.id.btn_bd_model);
        Button btnBdProgress = findViewById(R.id.btn_bd_progress);
        Button btnBdSetting = findViewById(R.id.btn_bd_setting);

        progressPick = book.getProgress() * 100 / book.getSize();
        sbProgressPick.setProgress(progressPick);
        sbProgressPick.setOnSeekBarChangeListener(seekBarChangeListener);
        btnBdControler.setOnClickListener(clickListener);
        btnBdModel.setOnClickListener(clickListener);
        btnBdProgress.setOnClickListener(clickListener);
        btnBdSetting.setOnClickListener(clickListener);
        tvBookContent.setOnTouchListener(touchListener);
        mGestureDetector = new GestureDetector(this, gestureListener);
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                exitBookListen();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
    }

    /**
     * 加载书籍
     */
    private void loadBook() {

        if (book == null) {
            return;
        }
        final DialogUtil dialogUtil = new DialogUtil(this);
        dialogUtil.showDialog();
        ThreadPoolUtil.getInstance().addThread(new Runnable() {
            @Override
            public void run() {

                // 读取本地文本文件为String
                bookContent = ToolUtil.getStringFromIS(book.getPath());
                if (bookContent == null) {
                    return;
                }
                // 加载书籍到内存
                //                ReadBookUtil.getInstance(BookDetailActivity.this).loadBook(book, bookContent);
                ReadBookUtil.getInstance(BookDetailActivity.this).cancel();
                bookContent = ReadBookUtil.getInstance(BookDetailActivity.this).loadBookPart(book, bookContent);
                // 将String转化为全角String
                bookContent = StringUtil.ToDBC(bookContent);
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        tbv.setTitleName(book.getName());
                        tvBookContent.setText(bookContent);
                        dialogUtil.hideDialog();
                    }
                });
            }
        });
    }

    /**
     * 开启全屏动画
     */
    private void animHideBar() {

        TranslateAnimation taTop = new TranslateAnimation(0, 0, 0, -150);
        taTop.setDuration(300);
        taTop.setAnimationListener(llTopAnimListener);

        TranslateAnimation taBottom = new TranslateAnimation(0, 0, 0, 150);
        taBottom.setDuration(300);
        taBottom.setAnimationListener(llBottomAnimListener);

        tbv.startAnimation(taTop);
        llBtnBar.startAnimation(taBottom);
    }

    /**
     * 退出全屏动画
     */
    private void animShowBar() {

        tbv.setVisibility(View.VISIBLE);
        llBtnBar.setVisibility(View.VISIBLE);

        TranslateAnimation taTop = new TranslateAnimation(0, 0, -150, 0);
        taTop.setDuration(300);
        tbv.startAnimation(taTop);

        TranslateAnimation taBottom = new TranslateAnimation(0, 0, 150, 0);
        taBottom.setDuration(300);
        llBtnBar.startAnimation(taBottom);
    }

    /**
     * 顶部标题栏动画监听
     */
    Animation.AnimationListener llTopAnimListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            tbv.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 底部按钮栏动画监听
     */
    Animation.AnimationListener llBottomAnimListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            llBtnBar.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * SeekBar动画监听
     */
    Animation.AnimationListener progressBarAnimListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            llProgressBar.setVisibility(View.GONE);
            loadBook();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 触屏事件监听
     */
    View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return mGestureDetector.onTouchEvent(event);
        }
    };

    /**
     * 手势监听
     */
    GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            if (isFullScreen) {
                animShowBar();
                isFullScreen = false;
            } else {
                animHideBar();
                isFullScreen = true;
            }

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_bd_controler:
                    if (isBtnStart) {
                        ReadBookUtil.getInstance(BookDetailActivity.this).speakBook();
                        isBtnStart = false;
                        btnBdControler.setText(R.string.book_pause);
                    } else {
                        ReadBookUtil.getInstance(BookDetailActivity.this).pauseBook();
                        isBtnStart = true;
                        btnBdControler.setText(R.string.book_play);
                    }
                    break;
                case R.id.btn_bd_progress:
                    if (isProgressBarShow) {
                        isProgressBarShow = false;

                        TranslateAnimation taProgress = new TranslateAnimation(0, 0, 0, 150);
                        taProgress.setDuration(300);
                        taProgress.setAnimationListener(progressBarAnimListener);
                        llProgressBar.startAnimation(taProgress);

                        int progress = book.getSize() * progressPick / 100;
                        book.setProgress(progress);
                        MyDBOperater.getInstance(BookDetailActivity.this).updateBookProgress(book.getBookId(), progress);
                        if (!isBtnStart) {
                            ReadBookUtil.getInstance(BookDetailActivity.this).pauseBook();
                            isBtnStart = true;
                            btnBdControler.setText(R.string.book_play);
                        }
                    } else {
                        isProgressBarShow = true;
                        llProgressBar.setVisibility(View.VISIBLE);

                        TranslateAnimation taProgress = new TranslateAnimation(0, 0, 150, 0);
                        taProgress.setDuration(300);
                        llProgressBar.startAnimation(taProgress);
                    }
                    break;
                case R.id.btn_bd_model:
                    if (isNightModel) {
                        btnBdModel.setText(R.string.book_night_mode);
                        tvBookContent.setBackgroundResource(R.color.colorGray_1);
                        tvBookContent.setTextColor(ContextCompat.getColor(BookDetailActivity.this, R.color.colorGray_2));
                        isNightModel = false;
                    } else {
                        btnBdModel.setText(R.string.book_day_mode);
                        tvBookContent.setBackgroundResource(R.color.colorPrimaryDark);
                        tvBookContent.setTextColor(ContextCompat.getColor(BookDetailActivity.this, R.color.colorPrimary));
                        isNightModel = true;
                    }
                    break;
                case R.id.btn_bd_setting:
                    ReadBookUtil.openSetting();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * SeekBar进度改变监听
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            progressPick = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * 用户按键监听
     *
     * @param keyCode 按键码
     * @param event   事件
     * @return 处理结果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBookListen();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出听书
     */
    private void exitBookListen() {

        if (!isBtnStart) {
            DialogUtil dialogUtil = new DialogUtil(this, R.mipmap.icon_info_g, getResources().getString(R.string.dialog_is_listen_background), new DialogListener() {

                @Override
                public void sure() {
                    finish();
                }

                @Override
                public void cancel() {
                    ReadBookUtil.getInstance(BookDetailActivity.this).stopBook();
                    ReadBookUtil.getInstance(BookDetailActivity.this).cancel();
                    finish();
                }
            });
            dialogUtil.showDialog();
        } else {
            ReadBookUtil.getInstance(this).cancel();
            finish();
        }
    }
}
