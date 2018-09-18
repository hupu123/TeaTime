package com.hugh.teatime.models.bill;

import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.DialogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class BillMonthTrendActivity extends BaseActivity {

    private Button btnMonthPicker;
    private TextView tvBmtTitle;
    private LineChartView lcvBmt;

    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bill_month_trend);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        // 获取年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Button btnBmtBack = findViewById(R.id.btn_bmt_back);
        tvBmtTitle = findViewById(R.id.tv_bmt_title);
        btnMonthPicker = findViewById(R.id.btn_bmt_month_picker);
        lcvBmt = findViewById(R.id.lcv_bmt);

        btnBmtBack.setOnClickListener(clickListener);
        btnMonthPicker.setOnClickListener(clickListener);

        // ------------------------------------年份选择器--------------------------------------------
        ArrayList<String> monthsOfYear = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthsOfYear.add(String.format(getResources().getString(R.string.month_name), i));
        }
        dialogUtil = new DialogUtil(this, monthsOfYear, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calendar calendar = Calendar.getInstance();
                calendar.clear(Calendar.MONTH);
                calendar.set(Calendar.MONTH, position + 1);
                int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);

                refreshData(maxDays, position + 1, year);
                dialogUtil.hideDialog();
            }
        });

        refreshData(maxDays, month, year);
    }

    /**
     * 刷新月度收支折现走势图
     *
     * @param month 月份
     */
    private void refreshData(int maxDays, int month, int year) {

        tvBmtTitle.setText(String.format(getResources().getString(R.string.bill_month_chart_name), month));
        btnMonthPicker.setText(String.format(getResources().getString(R.string.month_name), month));

        List<PointValue> pointsOut = new ArrayList<>();
        List<PointValue> pointsIn = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int j = 1; j <= maxDays; j++) {
                double value = MyDBOperater.getInstance(this).getBillSumByDay(j, month, year, i);
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
        lines.add(lineIn);
        lines.add(lineOut);

        Axis axisX = new Axis();
        List<AxisValue> axisValuesX = new ArrayList<>();
        for (int i = 1; i <= maxDays; i++) {
            AxisValue axisValue = new AxisValue(i);
            axisValue.setLabel(String.format(getResources().getString(R.string.day_name), i));
            axisValuesX.add(axisValue);
        }
        axisX.setValues(axisValuesX);

        Axis axisY = new Axis();
        axisY.setHasLines(true);
        axisY.setName(getResources().getString(R.string.bill_unit));

        LineChartData lcd = new LineChartData(lines);
        lcd.setAxisXBottom(axisX);
        lcd.setAxisYLeft(axisY);

        lcvBmt.setLineChartData(lcd);
    }

    /**
     * 按钮点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_bmt_back:
                    finish();
                    break;
                case R.id.btn_bmt_month_picker:
                    dialogUtil.showDialog();
                    break;
                default:
                    break;
            }
        }
    };
}
