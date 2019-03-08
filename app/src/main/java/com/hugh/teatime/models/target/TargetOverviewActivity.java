package com.hugh.teatime.models.target;

import android.os.Bundle;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.view.TitlebarView;

public class TargetOverviewActivity extends BaseActivity {

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

    }
}
