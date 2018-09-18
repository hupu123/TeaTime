package com.hugh.teatime.models.bill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.ToolUtil;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Hugh on 2016/4/8 11:44
 */
public class ChartFragment extends Fragment {

    private ImageView ivChartNoData;
    private ScrollView svChartData;
    private ColumnChartView ccvTotalInOut;
    private PieChartView pcvOutDetail;
    private PieChartView pcvInDetail;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_chart, container, false);

        initView(contentView);

        return contentView;
    }

    /**
     * 初始化控件
     *
     * @param contentView 控件根容器
     */
    private void initView(View contentView) {

        ivChartNoData = contentView.findViewById(R.id.iv_chart_no_data);
        svChartData = contentView.findViewById(R.id.sv_chart_data);
        ccvTotalInOut = contentView.findViewById(R.id.ccv_total_in_out);
        pcvOutDetail = contentView.findViewById(R.id.pcv_out_detail);
        pcvInDetail = contentView.findViewById(R.id.pcv_in_detail);
        Button btnMonthTrendChart = contentView.findViewById(R.id.btn_month_trend_chart);
        Button btnYearTrendChart = contentView.findViewById(R.id.btn_year_trend_chart);

        btnMonthTrendChart.setOnClickListener(clickListener);
        btnYearTrendChart.setOnClickListener(clickListener);
    }

    /**
     * 更新数据
     *
     * @param month 月份
     */
    public void updateData(int month, int year) {

        if (context == null) {
            return;
        }

        List<Bill> bills = MyDBOperater.getInstance(context).getBills(month, year);
        if (bills == null || bills.size() == 0) {
            ivChartNoData.setVisibility(View.VISIBLE);
            svChartData.setVisibility(View.GONE);
            return;
        } else {
            ivChartNoData.setVisibility(View.GONE);
            svChartData.setVisibility(View.VISIBLE);
        }

        // ----------------------------------------收支总额条形统计图--------------------------------
        List<Column> columns = new ArrayList<>();

        double amountSumOut = MyDBOperater.getInstance(context).getBillSum(month, year, 0);
        List<SubcolumnValue> subcolumnValuesOut = new ArrayList<>();
        SubcolumnValue subcolumnValueOut = new SubcolumnValue((float) amountSumOut, ContextCompat.getColor(context, R.color.colorAccent));
        subcolumnValuesOut.add(subcolumnValueOut);
        Column columnOut = new Column(subcolumnValuesOut);
        columnOut.setHasLabels(true);

        double amountSumIn = MyDBOperater.getInstance(context).getBillSum(month, year, 1);
        List<SubcolumnValue> subcolumnValuesIn = new ArrayList<>();
        SubcolumnValue subcolumnValueIn = new SubcolumnValue((float) amountSumIn, ContextCompat.getColor(context, R.color.colorGreen));
        subcolumnValuesOut.add(subcolumnValueIn);
        Column columnIn = new Column(subcolumnValuesIn);
        columnIn.setHasLabels(true);

        columns.add(columnOut);
        columns.add(columnIn);

        ColumnChartData ccd = new ColumnChartData(columns);

        Axis axisX = new Axis();
        List<AxisValue> axisValuesX = new ArrayList<>();
        AxisValue axisValueOut = new AxisValue(-0.2f);
        axisValueOut.setLabel("支出");
        AxisValue axisValueIn = new AxisValue(0.2f);
        axisValueIn.setLabel("收入");
        axisValuesX.add(axisValueOut);
        axisValuesX.add(axisValueIn);
        axisX.setValues(axisValuesX);

        Axis axisY = new Axis();
        axisY.setHasLines(true);
        axisY.setName("总额（元）");

        ccd.setAxisXBottom(axisX);
        ccd.setAxisYLeft(axisY);

        ccvTotalInOut.setColumnChartData(ccd);
        ccvTotalInOut.setScrollEnabled(false);

        // -----------------------------------------支出类型占比-------------------------------------
        double sumValueOut = MyDBOperater.getInstance(context).getBillSum(month, year, 0);
        List<SliceValue> sliceValuesOut = new ArrayList<>();
        for (int i = 0; i < GlobalVar.BILL_TYPE_NAME_ARRAY.length; i++) {
            double value = MyDBOperater.getInstance(context).getBillSum(month, year, 0, i);
            if (value > 0) {
                SliceValue sliceValue = new SliceValue();
                sliceValue.setValue((float) value);
                sliceValue.setLabel(GlobalVar.BILL_TYPE_NAME_ARRAY[i] + ToolUtil.getProgress(value, sumValueOut) + "%");
                sliceValue.setColor(ContextCompat.getColor(context, GlobalVar.BILL_TYPE_COLOR_ARRAY[i]));

                sliceValuesOut.add(sliceValue);
            }
        }

        PieChartData pieChartDataOut = new PieChartData(ToolUtil.sortChartData(sliceValuesOut));
        pieChartDataOut.setHasLabels(true);
        pieChartDataOut.setValueLabelsTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        pieChartDataOut.setHasCenterCircle(false);

        pcvOutDetail.setPieChartData(pieChartDataOut);
        pcvOutDetail.setChartRotationEnabled(false);

        // -----------------------------------------收入类型占比-------------------------------------
        double sumValueIn = MyDBOperater.getInstance(context).getBillSum(month, year, 1);
        List<SliceValue> sliceValuesIn = new ArrayList<>();
        for (int i = 0; i < GlobalVar.BILL_TYPE_NAME_ARRAY.length; i++) {
            double value = MyDBOperater.getInstance(context).getBillSum(month, year, 1, i);
            if (value > 0) {
                SliceValue sliceValue = new SliceValue();
                sliceValue.setValue((float) value);
                sliceValue.setLabel(GlobalVar.BILL_TYPE_NAME_ARRAY[i] + ToolUtil.getProgress(value, sumValueIn) + "%");
                sliceValue.setColor(ContextCompat.getColor(context, GlobalVar.BILL_TYPE_COLOR_ARRAY[i]));

                sliceValuesIn.add(sliceValue);
            }
        }

        PieChartData pieChartDataIn = new PieChartData(ToolUtil.sortChartData(sliceValuesIn));
        pieChartDataIn.setHasLabels(true);
        pieChartDataIn.setValueLabelsTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        pieChartDataIn.setHasCenterCircle(false);

        pcvInDetail.setPieChartData(pieChartDataIn);
        pcvInDetail.setChartRotationEnabled(false);
    }

    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent;
            switch (v.getId()) {
                case R.id.btn_month_trend_chart:
                    intent = new Intent(context, BillMonthTrendActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_year_trend_chart:
                    intent = new Intent(context, BillYearTrendActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
