package com.hugh.teatime.models.note;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.view.TitlebarView;

public class EventDetailActivity extends BaseActivity {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvLocation;
    private TextView tvTime;

    private EventBean eventBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GlobalVar.REQUEST_CODE_EDIT_EVENT) {
                eventBean = (EventBean) data.getSerializableExtra(GlobalVar.INTENT_EVENT_EDIT);
                refreshData();
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        TitlebarView tbv = findViewById(R.id.tbv_event_detail);
        tbv.setRightBtnText(getResources().getString(R.string.modify));
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                Intent intent = new Intent(EventDetailActivity.this, NewEventActivity.class);
                intent.putExtra(GlobalVar.INTENT_EVENT, eventBean);
                startActivityForResult(intent, GlobalVar.REQUEST_CODE_EDIT_EVENT);
            }
        });

        tvTitle = findViewById(R.id.tv_ed_title);
        tvContent = findViewById(R.id.tv_ed_content);
        tvLocation = findViewById(R.id.tv_ed_location);
        tvTime = findViewById(R.id.tv_ed_time);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        eventBean = (EventBean) getIntent().getSerializableExtra(GlobalVar.INTENT_EVENT);
        refreshData();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (eventBean == null) {
            return;
        }
        tvTitle.setText(eventBean.getTitle());
        tvContent.setText(eventBean.getContent());
        tvTime.setText(StringUtil.formatTimestamp1(eventBean.getDate()));
    }
}
