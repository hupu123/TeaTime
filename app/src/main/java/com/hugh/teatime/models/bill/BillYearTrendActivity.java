package com.hugh.teatime.models.bill;

import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.db.MyDBOperater;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class BillYearTrendActivity extends BaseActivity {

    private LineChartView lcvBillTrend;

    private YearPickAdapter ypa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bill_year_trend);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        // 获取年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        //        int month = calendar.get(Calendar.MONTH);

        Button btnBillTrendBack = findViewById(R.id.btn_bill_trend_back);
        TextView tvBillTrendTitle = findViewById(R.id.tv_bill_trend_title);
        Spinner sYearPicker = findViewById(R.id.s_year_picker);
        lcvBillTrend = findViewById(R.id.lcv_bill_trend);

        btnBillTrendBack.setOnClickListener(clickListener);
        tvBillTrendTitle.setText(String.format(getResources().getString(R.string.bill_year_chart_name), year));

        // ------------------------------------年份选择器--------------------------------------------
        int[] years = MyDBOperater.getInstance(this).getYears();
        ypa = new YearPickAdapter(this, years);
        sYearPicker.setAdapter(ypa);
        sYearPicker.setOnItemSelectedListener(itemSelectedListener);
        sYearPicker.setSelection(ypa.getPositionByValue(year));

        refreshData(year);
    }

    /**
     * 刷新年度收支折现走势图
     *
     * @param year 年份
     */
    private void refreshData(int year) {

        List<PointValue> pointsOut = new ArrayList<>();
        List<PointValue> pointsIn = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int j = 1; j <= 12; j++) {
                double value = MyDBOperater.getInstance(this).getBillSum(j, year, i);
                PointValue pointValue = new PointValue(j, (float) value);
                if (i == 0) {
                    pointsOut.add(pointValue);
                } else {
                    pointsIn.add(pointValue);
                }
            }
        }

        Line lineOut = new Line(pointsOut);
        lineOut.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        lineOut.setHasLabels(true);

        Line lineIn = new Line(pointsIn);
        lineIn.setColor(ContextCompat.getColor(this, R.color.colorGreen));
        lineIn.setHasLabels(true);

        List<Line> lines = new ArrayList<>();
        lines.add(lineOut);
        lines.add(lineIn);

        Axis axisX = new Axis();
        List<AxisValue> axisValuesX = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            AxisValue axisValue = new AxisValue(i);
            axisValue.setLabel(String.format(getResources().getString(R.string.month_name), i));
            axisValuesX.add(axisValue);
        }
        axisX.setValues(axisValuesX);

        Axis axisY = new Axis();
        axisY.setHasLines(true);
        axisY.setName(getResources().getString(R.string.bill_unit));

        LineChartData lcd = new LineChartData(lines);
        lcd.setAxisXBottom(axisX);
        lcd.setAxisYLeft(axisY);

        lcvBillTrend.setLineChartData(lcd);
    }

    /**
     * 按钮点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_bill_trend_back:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int year = ypa.getValueByPosition(position);
            if (year == 0) {
                return;
            }
            refreshData(year);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
