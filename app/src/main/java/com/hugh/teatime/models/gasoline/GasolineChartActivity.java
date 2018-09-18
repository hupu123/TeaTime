package com.hugh.teatime.models.gasoline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.hugh.teatime.R;
import com.hugh.teatime.adapter.MyFragmentAdapter;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.view.NoScrollViewPager;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GasolineChartActivity extends BaseActivity {

    private TitlebarView tbv;
    private NoScrollViewPager nsvpGasolineChart;
    private DialogUtil dialogUtil;

    private int year;
    private List<Fragment> fragments = new ArrayList<>();
    private LineChartFragment lineChartFragment;
    private PieChartFragment pieChartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasoline_chart);

        Intent intent = getIntent();
        GasolineBean gasolineBean = (GasolineBean) intent.getSerializableExtra(GlobalVar.INTENT_GASOLINE_RECORD);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(gasolineBean.getDate());
        year = calendar.get(Calendar.YEAR);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tbv = findViewById(R.id.tbv);
        tbv.setRightBtnText(String.valueOf(year));
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                dialogUtil.showDialog();
            }
        });

        RadioButton rbLine = findViewById(R.id.rb_line_chart);
        RadioButton rbPie = findViewById(R.id.rb_pie_chart);
        rbLine.setOnCheckedChangeListener(checkedChangeListener);
        rbPie.setOnCheckedChangeListener(checkedChangeListener);

        nsvpGasolineChart = findViewById(R.id.nsvp_gasoline_chart);
        nsvpGasolineChart.setIsCanScroll(false);

        final ArrayList<String> years = MyDBOperater.getInstance(GasolineChartActivity.this).getGRYears();
        dialogUtil = new DialogUtil(this, years, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                year = Integer.parseInt(years.get(position));
                tbv.setRightBtnText(years.get(position));
                refreshData();
                dialogUtil.hideDialog();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        lineChartFragment = new LineChartFragment();
        pieChartFragment = new PieChartFragment();
        lineChartFragment.setYear(year);
        pieChartFragment.setYear(year);
        fragments.add(lineChartFragment);
        fragments.add(pieChartFragment);
        MyFragmentAdapter mAdapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        nsvpGasolineChart.setAdapter(mAdapter);
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        lineChartFragment.onDataRefresh(year);
        pieChartFragment.onDataRefresh(year);
    }

    /**
     * 单选按钮选择监听
     */
    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                switch (buttonView.getId()) {
                    case R.id.rb_line_chart:
                        nsvpGasolineChart.setCurrentItem(0, true);
                        break;
                    case R.id.rb_pie_chart:
                        nsvpGasolineChart.setCurrentItem(1, true);
                        break;
                    default:
                        break;
                }
            }
        }
    };
}
