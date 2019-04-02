package com.hugh.teatime.models.target;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hugh.teatime.R;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.view.ComboLineColumnChartLayoutView;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;

public class TargetOverviewActivity extends BaseActivity {

    private LinearLayout llDailyTargets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_overview);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        TitlebarView tbv = findViewById(R.id.tbv);
        llDailyTargets = findViewById(R.id.ll_daily_targets);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        ArrayList<DailyTargetBean> dailyTargets = MyDBOperater.getInstance(this).getDailyTargets();
        if (dailyTargets == null || dailyTargets.size() == 0) {
            return;
        }
        for (int i = 0; i < dailyTargets.size(); i++) {
            DailyTargetBean dailyTargetBean = dailyTargets.get(i);
            ComboLineColumnChartLayoutView clcclv = new ComboLineColumnChartLayoutView(this, dailyTargetBean);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i == dailyTargets.size() - 1) {
                params.setMargins(20, 20, 20, 20);
            } else {
                params.setMargins(20, 20, 20, 0);
            }
            clcclv.setLayoutParams(params);
            llDailyTargets.addView(clcclv);
        }
    }
}
