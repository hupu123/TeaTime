package com.hugh.teatime.models.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.hugh.teatime.R;
import com.hugh.teatime.models.bill.BillHomeActivity;
import com.hugh.teatime.models.book.BookListActivity;
import com.hugh.teatime.models.comic.ComicShelfActivity;
import com.hugh.teatime.models.face.FaceDetectActivity;
import com.hugh.teatime.models.image.ImageFolderActivity;
import com.hugh.teatime.models.message.MsgHomeActivity;
import com.hugh.teatime.models.robot.RobotActivity;
import com.hugh.teatime.models.tool.ToolListActivity;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.utils.ToastUtil;

import java.util.ArrayList;

/**
 * 主页
 */
public class HomeActivity extends BaseActivity {

    private final int REQUEST_PERMISSION_CODE = 1;
    // 双击返回按钮退出应用时间记录
    private long doubleClickBackBtnToExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
        requestPermissions();
    }

    /**
     * 初始化控件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        Button btn1 = findViewById(R.id.btn_1);//听书
        Button btn2 = findViewById(R.id.btn_2);//人脸识别
        Button btn3 = findViewById(R.id.btn_3);//图灵机器人
        Button btn4 = findViewById(R.id.btn_4);//看图
        Button btn5 = findViewById(R.id.btn_5);//记账
        Button btn6 = findViewById(R.id.btn_6);//短信助手
        Button btn7 = findViewById(R.id.btn_7);//看漫画
        Button btn8 = findViewById(R.id.btn_8);//工具箱

        btn1.setOnClickListener(clickListener);
        btn2.setOnClickListener(clickListener);
        btn3.setOnClickListener(clickListener);
        btn4.setOnClickListener(clickListener);
        btn5.setOnClickListener(clickListener);
        btn6.setOnClickListener(clickListener);
        btn7.setOnClickListener(clickListener);
        btn8.setOnClickListener(clickListener);

        btn1.setOnTouchListener(touchListener);
        btn2.setOnTouchListener(touchListener);
        btn3.setOnTouchListener(touchListener);
        btn4.setOnTouchListener(touchListener);
        btn5.setOnTouchListener(touchListener);
        btn6.setOnTouchListener(touchListener);
        btn7.setOnTouchListener(touchListener);
        btn8.setOnTouchListener(touchListener);
    }

    /**
     * 动态申请权限
     */
    private void requestPermissions() {

        // 如果为Android6.0以下系统，则不需要动态获取权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        ArrayList<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CONTACTS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECEIVE_SMS);
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), REQUEST_PERMISSION_CODE);
        }
    }

    /**
     * 按钮点击事件
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent;
            switch (v.getId()) {
                case R.id.btn_1:
                    intent = new Intent(HomeActivity.this, BookListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_2:
                    intent = new Intent(HomeActivity.this, FaceDetectActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_3:
                    intent = new Intent(HomeActivity.this, RobotActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_4:
                    intent = new Intent(HomeActivity.this, ImageFolderActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_5:
                    intent = new Intent(HomeActivity.this, BillHomeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_6:
                    intent = new Intent(HomeActivity.this, MsgHomeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_7:
                    intent = new Intent(HomeActivity.this, ComicShelfActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_8:
                    intent = new Intent(HomeActivity.this, ToolListActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 按钮触控事件
     */
    View.OnTouchListener touchListener = new View.OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ScaleAnimation sAnim = new ScaleAnimation(1, 1.4f, 1, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sAnim.setDuration(50);
            sAnim.setFillAfter(true);

            v.bringToFront();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.startAnimation(sAnim);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.clearAnimation();
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.clearAnimation();
            }

            return false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            //TODO 处理权限请求结果
        }
    }

    /**
     * 按键监听
     *
     * @param keyCode 按键码
     * @param event   事件
     * @return 处理结果状态
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - doubleClickBackBtnToExitTime <= GlobalVar.DOUBLE_CLICK_BACKBTN_TO_EXIT_INTERVAL) {
                finish();
            } else {
                ToastUtil.showInfo(this, R.string.toast_double_click_to_exit, true);
                doubleClickBackBtnToExitTime = System.currentTimeMillis();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
