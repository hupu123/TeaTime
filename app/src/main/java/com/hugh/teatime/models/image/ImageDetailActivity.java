package com.hugh.teatime.models.image;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.view.FileImageView;

import java.util.List;

/**
 * 图片详情页
 */
public class ImageDetailActivity extends BaseActivity {

    private FileImageView fivImageDetailContent;
    private LinearLayout llImgToolBar;

    private boolean isImgToolBarHide = true;// 下方工具栏的显示状态，true=隐藏中，false=显示中
    private int position = 0;// 查看图片的当前位置
    private List<Image> images;// 图片信息集合
    private GestureDetector mGesture;// 手势监听器
    private float rotateCurrent = 0;// 旋转角度记录
    private float scaleCurrent = 1;// 缩放比例记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_detail);

        // 初始化控件
        initView();

        // 获取数据
        Intent intent = getIntent();
        String folderName = intent.getStringExtra(ImageFolderActivity.INTENT_FOLDER_NAME);
        position = intent.getIntExtra(ImageListActivity.INTENT_IMAGE_POSITION, 0);
        if (!StringUtil.isStrNull(folderName)) {
            images = MyDBOperater.getInstance(this).getImagesByFolder(folderName);
        }

        // 显示图片
        showImageCurrent();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        fivImageDetailContent = findViewById(R.id.fiv_image_detail_content);
        llImgToolBar = findViewById(R.id.ll_img_tool_bar);
        Button btnImgRotate = findViewById(R.id.btn_img_rotate);
        Button btnImgBiger = findViewById(R.id.btn_img_biger);
        Button btnImgSmaller = findViewById(R.id.btn_img_smaller);

        fivImageDetailContent.setOnTouchListener(onTouchListener);
        mGesture = new GestureDetector(this, onGestureListener);
        btnImgRotate.setOnClickListener(clickListener);
        btnImgBiger.setOnClickListener(clickListener);
        btnImgSmaller.setOnClickListener(clickListener);
    }

    /**
     * 隐藏工具栏动画
     */
    private void animHideBar() {

        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, 150);
        ta.setDuration(300);
        ta.setAnimationListener(hideAnimListener);
        llImgToolBar.startAnimation(ta);
    }

    /**
     * 显示工具栏动画
     */
    private void animShowBar() {

        llImgToolBar.setVisibility(View.VISIBLE);
        TranslateAnimation ta = new TranslateAnimation(0, 0, 150, 0);
        ta.setDuration(300);
        llImgToolBar.startAnimation(ta);
    }

    /**
     * 显示当前图片
     */
    private void showImageCurrent() {

        if (images == null || images.size() == 0) {
            return;
        }
        Image image = images.get(position);
        fivImageDetailContent.setImageFile(image.getPath());

        // 重置操作参数
        resetDefaultParams();
    }

    /**
     * 显示下一张图片
     */
    private void showImageNext() {

        if (images == null || images.size() == 0) {
            return;
        }
        position++;
        if (position >= images.size()) {
            position = 0;
        }
        Image image = images.get(position);
        fivImageDetailContent.setImageFile(image.getPath());

        // 重置操作参数
        resetDefaultParams();
    }

    /**
     * 显示上一张图片
     */
    private void showImagePre() {

        if (images == null || images.size() == 0) {
            return;
        }
        position--;
        if (position < 0) {
            position = images.size() - 1;
        }
        Image image = images.get(position);
        fivImageDetailContent.setImageFile(image.getPath());

        // 重置操作参数
        resetDefaultParams();
    }

    /**
     * 重置操作参数
     */
    private void resetDefaultParams() {

        rotateCurrent = 0;
        scaleCurrent = 1;
        fivImageDetailContent.setTranslationX(0);
        fivImageDetailContent.setTranslationY(0);
        fivImageDetailContent.setRotation(rotateCurrent);
        fivImageDetailContent.setScaleX(scaleCurrent);
        fivImageDetailContent.setScaleY(scaleCurrent);
    }

    /**
     * 隐藏工具栏动画监听
     */
    Animation.AnimationListener hideAnimListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            llImgToolBar.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_img_rotate:
                    //                    float rotate = rotateCurrent;
                    rotateCurrent = rotateCurrent + 90;
                    if (rotateCurrent >= 360) {
                        rotateCurrent = rotateCurrent - 360;
                    }
                    //                    RotateAnimation rotateAnimation = new RotateAnimation(rotate, rotateCurrent, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    //                    rotateAnimation.setDuration(500);
                    //                    rotateAnimation.setFillAfter(true);
                    //                    fivImageDetailContent.startAnimation(rotateAnimation);
                    fivImageDetailContent.setRotation(rotateCurrent);
                    break;
                case R.id.btn_img_biger:
                    //                    float scaleBiger = scaleCurrent;
                    scaleCurrent = scaleCurrent * 1.5f;
                    //                    ScaleAnimation scaleBigerAnimation = new ScaleAnimation(scaleBiger, scaleCurrent, scaleBiger, scaleCurrent, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    //                    scaleBigerAnimation.setDuration(500);
                    //                    scaleBigerAnimation.setFillAfter(true);
                    //                    fivImageDetailContent.startAnimation(scaleBigerAnimation);
                    fivImageDetailContent.setScaleX(scaleCurrent);
                    fivImageDetailContent.setScaleY(scaleCurrent);
                    break;
                case R.id.btn_img_smaller:
                    //                    float scaleSmaller = scaleCurrent;
                    scaleCurrent = scaleCurrent / 1.5f;
                    //                    ScaleAnimation scaleSmallerAnimation = new ScaleAnimation(scaleSmaller, scaleCurrent, scaleSmaller, scaleCurrent, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    //                    scaleSmallerAnimation.setDuration(500);
                    //                    scaleSmallerAnimation.setFillAfter(true);
                    //                    fivImageDetailContent.startAnimation(scaleSmallerAnimation);
                    fivImageDetailContent.setScaleX(scaleCurrent);
                    fivImageDetailContent.setScaleY(scaleCurrent);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 触控监听器
     */
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return mGesture.onTouchEvent(event);
        }
    };

    /**
     * 手势监听器
     */
    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            if (isImgToolBarHide) {
                animShowBar();
                isImgToolBarHide = false;
            } else {
                animHideBar();
                isImgToolBarHide = true;
            }

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float xPosition;
            float yPosition;
            float tranX = fivImageDetailContent.getTranslationX();
            float tranY = fivImageDetailContent.getTranslationY();
            float disX = (distanceX / 2) * scaleCurrent;
            float disY = (distanceY / 2) * scaleCurrent;

            switch ((int) rotateCurrent) {
                case 0:
                    xPosition = tranX - disX;
                    yPosition = tranY - disY;
                    break;
                case 90:
                    xPosition = tranX + disY;
                    yPosition = tranY - disX;
                    break;
                case 180:
                    xPosition = tranX + disX;
                    yPosition = tranY + disY;
                    break;
                case 270:
                    xPosition = tranX - disY;
                    yPosition = tranY + disX;
                    break;
                default:
                    xPosition = 0;
                    yPosition = 0;
                    break;
            }

            fivImageDetailContent.setTranslationX(xPosition);
            fivImageDetailContent.setTranslationY(yPosition);

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (velocityX > 2000) {
                showImagePre();
            } else if (velocityX < -2000) {
                showImageNext();
            }

            return false;
        }
    };
}
