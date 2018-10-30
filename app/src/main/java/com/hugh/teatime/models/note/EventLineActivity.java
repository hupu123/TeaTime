package com.hugh.teatime.models.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ListView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.view.TitlebarView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

public class EventLineActivity extends BaseActivity {

    private SmartRefreshLayout srlEvents;
    private ListView lvEvents;

    private EventsAdapter mAdapter;
    private ArrayList<EventBean> eventBeans = new ArrayList<>();
    private int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_line);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData(0);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv_events);
        tbv.setRightBtnText(getResources().getString(R.string.add));
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                Intent intent = new Intent(EventLineActivity.this, NewEventActivity.class);
                startActivity(intent);
            }
        });

        srlEvents = findViewById(R.id.srl_events);
        lvEvents = findViewById(R.id.lv_events);

        srlEvents.setRefreshHeader(new ClassicsHeader(this));
        srlEvents.setEnableLoadMore(false);
        srlEvents.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData(1);
            }
        });
        mAdapter = new EventsAdapter(this, eventBeans);
        lvEvents.setAdapter(mAdapter);
    }

    /**
     * 刷新数据
     *
     * @param type 操作类型，0=刷新当前页，1=加载下一页
     */
    private void refreshData(int type) {
        if (type == 1) {
            pageIndex++;
        } else if (type == 0) {
            pageIndex = 0;
            eventBeans.clear();
        }
        ArrayList<EventBean> events = MyDBOperater.getInstance(this).getEvents(pageIndex, GlobalVar.PAGE_SIZE);
        eventBeans.addAll(0, events);
        mAdapter.notifyDataSetChanged();
        final int selectPosition;
        if (events == null || events.size() == 0) {
            if (type == 1) {
                pageIndex--;
            }
            selectPosition = 0;
        } else {
            if (type == 0) {
                selectPosition = eventBeans.size() - 1;
            } else {
                selectPosition = events.size() - 1;
            }
        }
        lvEvents.post(new Runnable() {
            @Override
            public void run() {
                lvEvents.setSelection(selectPosition);
            }
        });
        srlEvents.finishRefresh();
    }
}
