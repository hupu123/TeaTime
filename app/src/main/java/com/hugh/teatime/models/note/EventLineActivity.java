package com.hugh.teatime.models.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.DimensUtil;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

public class EventLineActivity extends BaseActivity {

    private SmartRefreshLayout srlEvents;
    private ListView lvEvents;
    private SearchView svSearch;

    private EventsAdapter mAdapter;
    private ArrayList<EventBean> eventBeans = new ArrayList<>();
    private int pageIndex = 0;
    private String searchStr = "";

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

        svSearch = findViewById(R.id.sv_search);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStr = query;
                refreshData(0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        svSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logHugh("setOnSearchClickListener");
                int marginSize = DimensUtil.getInstance(EventLineActivity.this).dp2px(20);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, svSearch.getHeight());
                params.setMargins(marginSize, marginSize, marginSize, marginSize);
                svSearch.setLayoutParams(params);
            }
        });
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                LogUtil.logHugh("setOnCloseListener");
                int marginSize = DimensUtil.getInstance(EventLineActivity.this).dp2px(20);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(svSearch.getHeight(), svSearch.getHeight());
                params.setMargins(marginSize, marginSize, marginSize, marginSize);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                svSearch.setLayoutParams(params);
                searchStr = "";
                refreshData(0);
                return false;
            }
        });
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
        ArrayList<EventBean> events;
        if (StringUtil.isStrNull(searchStr)) {
            events = MyDBOperater.getInstance(this).getEvents(pageIndex, GlobalVar.PAGE_SIZE);
        } else {
            events = MyDBOperater.getInstance(this).getEventsBySearch(pageIndex, GlobalVar.PAGE_SIZE, searchStr);
        }
        eventBeans.addAll(0, events);
        mAdapter.notifyDataSetChanged();
        final int selectPosition;
        if (events.size() == 0) {
            if (type == 1) {
                pageIndex--;
            }
            selectPosition = 0;
            ToastUtil.showInfo(this, R.string.toast_no_more_data, true);
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
