package com.hugh.teatime.models.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.AMLocationUtil;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.Date;

public class NewEventActivity extends BaseActivity {

    private TitlebarView tbv;
    private Button btnTime;
    private Button btnLocation;
    private EditText etTitle;
    private EditText etContent;

    private EventBean eventBean;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GlobalVar.REQUEST_CODE_EDIT_LOCATION) {
                EventBean eventBeanLocation = (EventBean) data.getSerializableExtra(GlobalVar.INTENT_LOCATION_EDIT);
                LogUtil.logHugh("onActivityResult latitude=" + eventBeanLocation.getLatitude() + " longitude=" + eventBeanLocation.getLongitude() + " address=" + eventBeanLocation.getAddress());
                eventBean.setLatitude(eventBeanLocation.getLatitude());
                eventBean.setLongitude(eventBeanLocation.getLongitude());
                eventBean.setAddress(eventBeanLocation.getAddress());
                eventBean.setCityCode(eventBeanLocation.getCityCode());
                btnLocation.setText(eventBean.getAddress());
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tbv = findViewById(R.id.tbv_ne);
        btnTime = findViewById(R.id.btn_time);
        ImageView ivAutoTime = findViewById(R.id.iv_auto_time);
        btnLocation = findViewById(R.id.btn_location);
        ImageView ivAutoLocate = findViewById(R.id.iv_auto_locate);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                if (isEdit) {
                    save();
                } else {
                    add();
                }
            }
        });
        btnTime.setOnClickListener(clickListener);
        btnLocation.setOnClickListener(clickListener);
        ivAutoTime.setOnClickListener(clickListener);
        ivAutoLocate.setOnClickListener(clickListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        eventBean = (EventBean) getIntent().getSerializableExtra(GlobalVar.INTENT_EVENT);
        if (eventBean == null) {
            isEdit = false;
            eventBean = new EventBean();
            eventBean.setDate(new Date().getTime());
        } else {
            isEdit = true;
        }

        if (isEdit) {
            tbv.setRightBtnText(getResources().getString(R.string.save));
            etTitle.setText(eventBean.getTitle());
            etContent.setText(eventBean.getContent());
            btnLocation.setText(eventBean.getAddress());
        } else {
            tbv.setRightBtnText(getResources().getString(R.string.add));
            getLocationInfo();
        }
        btnTime.setText(StringUtil.formatTimestamp1(eventBean.getDate()));
    }

    /**
     * 新建
     */
    private void add() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (StringUtil.isStrNull(title)) {
            ToastUtil.showInfo(this, R.string.toast_new_event_title_null, true);
            etTitle.requestFocus();
            return;
        }
        eventBean.setTitle(title);
        eventBean.setContent(content);
        MyDBOperater.getInstance(this).addEvent(eventBean);
        ToastUtil.showSuccess(this, R.string.save_success, true);
        finish();
    }

    /**
     * 保存
     */
    private void save() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (StringUtil.isStrNull(title)) {
            ToastUtil.showInfo(this, R.string.toast_new_event_title_null, true);
            etTitle.requestFocus();
            return;
        }
        eventBean.setTitle(title);
        eventBean.setContent(content);
        MyDBOperater.getInstance(this).updateEvent(eventBean);
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra(GlobalVar.INTENT_EVENT_EDIT, eventBean);
        setResult(RESULT_OK, intent);
        ToastUtil.showSuccess(this, R.string.save_success, true);
        finish();
    }

    /**
     * 获取位置信息
     */
    private void getLocationInfo() {
        final DialogUtil waitDialog = new DialogUtil(this);
        waitDialog.showDialog();
        AMLocationUtil.getInstance(getApplicationContext()).startLocateOnce(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    LogUtil.logHugh("code=" + aMapLocation.getErrorCode() + " msg=" + aMapLocation.getErrorInfo());
                    if (aMapLocation.getErrorCode() == 0) {
                        LogUtil.logHugh("onLocationChanged latitude=" + aMapLocation.getLatitude() + " longitude=" + aMapLocation.getLongitude() + " address=" + aMapLocation.getAddress());
                        eventBean.setLatitude(aMapLocation.getLatitude());
                        eventBean.setLongitude(aMapLocation.getLongitude());
                        eventBean.setAddress(aMapLocation.getAddress());
                        eventBean.setCityCode(aMapLocation.getCityCode());
                        btnLocation.setText(eventBean.getAddress());
                    }
                }
                AMLocationUtil.getInstance(getApplicationContext()).stopLocate();
                waitDialog.hideDialog();
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
                case R.id.btn_time:
                    DatePickDialog dpd = new DatePickDialog(NewEventActivity.this);
                    dpd.setType(DateType.TYPE_ALL);
                    dpd.setStartDate(new Date(eventBean.getDate()));
                    dpd.setTitle(getResources().getString(R.string.bill_record_select_date));
                    dpd.setOnSureLisener(sureLisener);
                    dpd.show();
                    break;
                case R.id.iv_auto_time:
                    Date date = new Date();
                    eventBean.setDate(date.getTime());
                    btnTime.setText(StringUtil.formatTimestamp1(date.getTime()));
                    break;
                case R.id.btn_location:
                    if ((eventBean.getLatitude() == 0 && eventBean.getLongitude() == 0) || StringUtil.isStrNull(eventBean.getAddress())) {
                        ToastUtil.showInfo(NewEventActivity.this, R.string.toast_no_location_info, true);
                        break;
                    }
                    Intent intent = new Intent(NewEventActivity.this, PickLocationActivity.class);
                    intent.putExtra(GlobalVar.INTENT_LOCATION, eventBean);
                    startActivityForResult(intent, GlobalVar.REQUEST_CODE_EDIT_LOCATION);
                    break;
                case R.id.iv_auto_locate:
                    getLocationInfo();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 时间选择确认监听
     */
    private OnSureLisener sureLisener = new OnSureLisener() {
        @Override
        public void onSure(Date date) {
            eventBean.setDate(date.getTime());
            btnTime.setText(StringUtil.formatTimestamp1(date.getTime()));
        }
    };
}
