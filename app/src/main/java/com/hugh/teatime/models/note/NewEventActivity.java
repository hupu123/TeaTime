package com.hugh.teatime.models.note;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.hugh.teatime.R;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.Date;

public class NewEventActivity extends BaseActivity {

    private Button btnTime;
    private Button btnLocation;
    private EditText etTitle;
    private EditText etContent;

    private Date eventDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        TitlebarView tbv = findViewById(R.id.tbv_ne);
        btnTime = findViewById(R.id.btn_time);
        btnLocation = findViewById(R.id.btn_location);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

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
        btnTime.setOnClickListener(clickListener);
        btnLocation.setOnClickListener(clickListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        btnTime.setText(StringUtil.formatTimestamp1(eventDate.getTime()));
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
        EventBean eventBean = new EventBean(title, eventDate.getTime());
        eventBean.setContent(content);
        MyDBOperater.getInstance(this).addEvent(eventBean);
        ToastUtil.showSuccess(this, R.string.save_success, true);
        finish();
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
                    dpd.setStartDate(eventDate);
                    dpd.setTitle(getResources().getString(R.string.bill_record_select_date));
                    dpd.setOnSureLisener(sureLisener);
                    dpd.show();
                    break;
                case R.id.btn_location:
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
            eventDate = date;
            btnTime.setText(StringUtil.formatTimestamp1(date.getTime()));
        }
    };
}
