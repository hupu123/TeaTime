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
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.models.bill.BillHomeActivity;
import com.hugh.teatime.models.book.BookListActivity;
import com.hugh.teatime.models.comic.ComicShelfActivity;
import com.hugh.teatime.models.face.FaceDetectActivity;
import com.hugh.teatime.models.gasoline.GasolineHomeActivity;
import com.hugh.teatime.models.image.ImageFolderActivity;
import com.hugh.teatime.models.message.MsgHomeActivity;
import com.hugh.teatime.models.note.EventLineActivity;
import com.hugh.teatime.models.robot.RobotActivity;
import com.hugh.teatime.models.tool.ToolListActivity;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.dsgv.DragSortGridView;

import java.util.ArrayList;

public class Home2Activity extends BaseActivity {

    private FrameLayout dragFrame;
    private DragSortGridView dsgvHome;

    // 双击返回按钮退出应用时间记录
    private long doubleClickBackBtnToExitTime;
    private ArrayList<ModelBean> models;
    private final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPUtil.getInstance(this).setExitTime(System.currentTimeMillis());
    }

    /**
     * 初始化控件
     */
    private void initView() {
        dragFrame = findViewById(R.id.fl_drag_frame);
        dsgvHome = findViewById(R.id.dsgv_home);
        dsgvHome.setDragModel(DragSortGridView.DRAG_BY_LONG_CLICK);
        dsgvHome.setNumColumns(3);
        dsgvHome.setAnimFrame(dragFrame);
        dsgvHome.setDragLongPressTime(1000);
        dsgvHome.setOnDragSelectListener(new DragSortGridView.OnDragSelectListener() {
            @Override
            public void onDragSelect(View mirror) {
                dragFrame.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPutDown(View itemView) {
                dragFrame.setVisibility(View.GONE);
                saveIconOrder();
            }
        });
        dsgvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = models.get(position).getTargetIntent();
                if (intent != null) {
                    startActivity(models.get(position).getTargetIntent());
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        models = getIconOrder();
        MyDsgvAdapter mAdpter = new MyDsgvAdapter(this, models);
        dsgvHome.setAdapter(mAdpter);
    }

    /**
     * 保存图标顺序
     */
    private void saveIconOrder() {
        if (models == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (ModelBean modelBean : models) {
            sb.append(modelBean.getPosition()).append(",");
        }
        SPUtil.getInstance(this).setHomeIconOrder(sb.toString());
    }

    /**
     * 根据设置排序图标
     *
     * @return 排序后的图标
     */
    private ArrayList<ModelBean> getIconOrder() {

        // 注意：添加新模块时，请依次添加到末尾，ModelBean的position尽量保持连续且递增
        ArrayList<ModelBean> models = new ArrayList<>();
        models.add(new ModelBean(0, R.mipmap.icon_book, getResources().getString(R.string.model_book), "", new Intent(this, BookListActivity.class)));
        models.add(new ModelBean(1, R.mipmap.icon_face, getResources().getString(R.string.model_face_detect), "", new Intent(this, FaceDetectActivity.class)));
        models.add(new ModelBean(2, R.mipmap.icon_robot, getResources().getString(R.string.model_robot), "", new Intent(this, RobotActivity.class)));
        models.add(new ModelBean(3, R.mipmap.icon_image, getResources().getString(R.string.model_picture), "", new Intent(this, ImageFolderActivity.class)));
        models.add(new ModelBean(4, R.mipmap.icon_bill, getResources().getString(R.string.model_bill), "", new Intent(this, BillHomeActivity.class)));
        models.add(new ModelBean(5, R.mipmap.icon_sms, getResources().getString(R.string.model_sms), "", new Intent(this, MsgHomeActivity.class)));
        models.add(new ModelBean(6, R.mipmap.icon_comic, getResources().getString(R.string.model_comic), "", new Intent(this, ComicShelfActivity.class)));
        models.add(new ModelBean(7, R.mipmap.icon_tool, getResources().getString(R.string.model_tools), "", new Intent(this, ToolListActivity.class)));
        models.add(new ModelBean(8, R.mipmap.icon_gasoline, getResources().getString(R.string.model_gasoline), "", new Intent(this, GasolineHomeActivity.class)));
        models.add(new ModelBean(9, R.mipmap.icon_note, getResources().getString(R.string.model_note), "", new Intent(this, EventLineActivity.class)));
        //TODO 添加新模块入口

        String iconOrderStr = SPUtil.getInstance(this).getHomeIconOrder();
        if (!StringUtil.isStrNull(iconOrderStr)) {
            String[] iconOrder = iconOrderStr.split(",");
            if (iconOrder.length > 0) {
                ArrayList<ModelBean> modelBeans = new ArrayList<>();
                for (String positionStr : iconOrder) {
                    int position = Integer.parseInt(positionStr);
                    if (position < models.size()) {
                        modelBeans.add(models.get(position));
                    }
                }
                if (models.size() > modelBeans.size()) {
                    modelBeans.addAll(modelBeans.size(), models);
                }
                models = modelBeans;
            }
        }

        return models;
    }

    /**
     * 动态申请权限
     */
    @SuppressLint("ObsoleteSdkInt")
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            //TODO 处理权限请求结果
            LogUtil.logHugh("permissions size:" + permissions.length);
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
