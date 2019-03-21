package com.hugh.teatime.models.target;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hugh.teatime.R;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.ColorPicker;
import com.hugh.teatime.view.ComboLineColumnChartLayoutView;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;

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
            ComboLineColumnChartData clccd = initChartData(dailyTargetBean);
            if (clccd != null) {
                ComboLineColumnChartLayoutView clcclv = new ComboLineColumnChartLayoutView(this, dailyTargetBean.getTitle(), clccd);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
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

    /**
     * 生成图表数据
     *
     * @param dailyTarget 目标数据
     * @return 图表数据
     */
    private ComboLineColumnChartData initChartData(DailyTargetBean dailyTarget) {
        MyDBOperater mDB = MyDBOperater.getInstance(this);
        ArrayList<TargetBean> targets = mDB.getTargetsByDailyId(dailyTarget.getDailyId());
        if (targets == null) {
            return null;
        }
        ColorPicker colorPicker = new ColorPicker(this);

        List<Column> columns = new ArrayList<>();

        List<Line> lines = new ArrayList<>();
        Line line = new Line();
        List<PointValue> pointValues = new ArrayList<>();

        Axis axisX = new Axis();
        List<AxisValue> axisValuesX = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            TargetBean target = targets.get(i);
            if (target == null) {
                continue;
            }

            Column column = new Column();
            List<SubcolumnValue> subcolumnValues = new ArrayList<>();
            SubcolumnValue subcolumnValue = new SubcolumnValue();
            subcolumnValue.setValue(target.getDoneNum());
            subcolumnValue.setTarget(target.getTargetNum());
            subcolumnValue.setLabel(String.valueOf(target.getDoneNum()));
            if (target.getDoneNum() >= target.getTargetNum()) {
                subcolumnValue.setColor(colorPicker.getColorByColorID(R.color.colorChart_t3));
            } else {
                subcolumnValue.setColor(colorPicker.getColorByColorID(R.color.colorChart_t5));
            }
            subcolumnValues.add(subcolumnValue);
            column.setValues(subcolumnValues);
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);

            PointValue pointValue = new PointValue();
            pointValue.set(i, target.getDoneNum());
            pointValues.add(pointValue);

            AxisValue axisValue = new AxisValue(i);
            axisValue.setLabel(target.getDate());
            axisValuesX.add(axisValue);
        }
        line.setValues(pointValues);
        line.setColor(colorPicker.getColorByColorID(R.color.colorChart_t1));
        lines.add(line);

        axisX.setValues(axisValuesX);

        Axis axisY = new Axis();
        axisY.setHasLines(true);
        axisY.setName(dailyTarget.getTargetName());

        ColumnChartData ccd = new ColumnChartData(columns);
        LineChartData lcd = new LineChartData(lines);

        ComboLineColumnChartData clccd = new ComboLineColumnChartData(ccd, lcd);
        clccd.setAxisXBottom(axisX);
        clccd.setAxisYLeft(axisY);

        return clccd;
    }
}
