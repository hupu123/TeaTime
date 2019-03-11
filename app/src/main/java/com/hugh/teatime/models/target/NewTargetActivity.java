package com.hugh.teatime.models.target;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.hugh.teatime.R;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class NewTargetActivity extends BaseActivity {

    private int targetType = TargetBean.TYPE_DAILY;
    private Date targetStartTime = new Date();
    private Date targetEndTime = new Date();

    private Spinner sType;
    private EditText etTitle;
    private EditText etTargetName;
    private EditText etTargetNum;
    private Button btnStartTime;
    private Button btnEndTime;
    private LinearLayout llStartTime;
    private LinearLayout llEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_target);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        TitlebarView tbv = findViewById(R.id.tbv);
        sType = findViewById(R.id.s_target_type);
        etTitle = findViewById(R.id.et_target_title);
        etTargetName = findViewById(R.id.et_target_name);
        etTargetNum = findViewById(R.id.et_target_num);
        btnStartTime = findViewById(R.id.btn_time);
        btnEndTime = findViewById(R.id.btn_end_time);
        llStartTime = findViewById(R.id.ll_starttime);
        llEndTime = findViewById(R.id.ll_endtime);

        tbv.setRightBtnText(getResources().getString(R.string.add));
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                add();
            }
        });
        sType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                targetType = i;
                switch (targetType) {
                    case TargetBean.TYPE_DAILY:
                        llStartTime.setVisibility(View.GONE);
                        llEndTime.setVisibility(View.GONE);
                        break;
                    case TargetBean.TYPE_ONETIME:
                        llStartTime.setVisibility(View.VISIBLE);
                        llEndTime.setVisibility(View.VISIBLE);
                        break;
                    default:
                        llStartTime.setVisibility(View.VISIBLE);
                        llEndTime.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnStartTime.setOnClickListener(clickListener);
        btnEndTime.setOnClickListener(clickListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        ArrayList<String> targetTypes = new ArrayList<>(Arrays.asList(GlobalVar.TARGET_TYPE_ARRAY));
        CommonAdapter<String> targetTypeAdapter = new CommonAdapter<String>(this, R.layout.item_type_s, targetTypes) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.tv_type_name, item);
            }
        };
        sType.setAdapter(targetTypeAdapter);
        btnStartTime.setText(StringUtil.formatTimestamp1(targetStartTime.getTime()));
        btnEndTime.setText(StringUtil.formatTimestamp1(targetEndTime.getTime()));
    }

    /**
     * 新建目标
     */
    private void add() {
        String title = etTitle.getText().toString();
        String targetName = etTargetName.getText().toString();
        String targetNum = etTargetNum.getText().toString();
        if (StringUtil.isStrNull(title)) {
            ToastUtil.showInfo(this, R.string.toast_nt_title_null, true);
            return;
        }
        if (StringUtil.isStrNull(targetName)) {
            ToastUtil.showInfo(this, R.string.toast_nt_target_name_null, true);
            return;
        }
        if (StringUtil.isStrNull(targetNum)) {
            ToastUtil.showInfo(this, R.string.toast_nt_target_num_null, true);
            return;
        }
        if (targetType == TargetBean.TYPE_DAILY) {
            DailyTargetBean dailyTargetBean = new DailyTargetBean(title, targetName, Integer.parseInt(targetNum), System.currentTimeMillis());
            MyDBOperater.getInstance(this).addDailyTarget(dailyTargetBean);
        } else {
            TargetBean targetBean = new TargetBean(targetType, title, targetName, Integer.parseInt(targetNum), 0, TargetBean.STATUS_RUNNING, System.currentTimeMillis(), targetStartTime.getTime(), targetEndTime.getTime());
            MyDBOperater.getInstance(this).addTarget(targetBean);
        }
        ToastUtil.showSuccess(this, R.string.save_success, true);
        finish();
    }

    /**
     * 点击事件监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_time:
                    DatePickDialog dpd = new DatePickDialog(NewTargetActivity.this);
                    dpd.setType(DateType.TYPE_ALL);
                    dpd.setStartDate(targetStartTime);
                    dpd.setTitle(getResources().getString(R.string.nt_choose_start_time));
                    dpd.setOnSureLisener(new OnSureLisener() {
                        @Override
                        public void onSure(Date date) {
                            targetStartTime = date;
                            btnStartTime.setText(StringUtil.formatTimestamp1(targetStartTime.getTime()));
                        }
                    });
                    dpd.show();
                    break;
                case R.id.btn_end_time:
                    DatePickDialog dpd1 = new DatePickDialog(NewTargetActivity.this);
                    dpd1.setType(DateType.TYPE_ALL);
                    dpd1.setStartDate(targetEndTime);
                    dpd1.setTitle(getResources().getString(R.string.nt_choose_end_time));
                    dpd1.setOnSureLisener(new OnSureLisener() {
                        @Override
                        public void onSure(Date date) {
                            targetEndTime = date;
                            btnEndTime.setText(StringUtil.formatTimestamp1(targetEndTime.getTime()));
                        }
                    });
                    dpd1.show();
                    break;
                default:
                    break;
            }
        }
    };
}
