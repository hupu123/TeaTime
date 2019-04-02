package com.hugh.teatime.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.gasoline.IllustrationBean;
import com.hugh.teatime.models.target.DailyTargetBean;
import com.hugh.teatime.models.target.OverviewBean;
import com.hugh.teatime.models.target.TargetBean;
import com.hugh.teatime.utils.ColorPicker;

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
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class ComboLineColumnChartLayoutView extends LinearLayout {

    public ComboLineColumnChartLayoutView(Context context, DailyTargetBean dailyTargetBean) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_clccv_layout, this);
        LinearLayout llOverviewConent = view.findViewById(R.id.ll_overview_content);
        ImageView ivNoData = view.findViewById(R.id.iv_no_data);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        ComboLineColumnChartView clccvChart = view.findViewById(R.id.clccv_chart);
        NoScrollGridView nsgv = view.findViewById(R.id.nsgv_illustration);
        TextView tvTargetTotal = view.findViewById(R.id.tv_target_total);
        TextView tvDoneTotal = view.findViewById(R.id.tv_done_total);
        TextView tvOweTotal = view.findViewById(R.id.tv_owe_total);
        TextView tvAvgNum = view.findViewById(R.id.tv_avg_num);
        TextView tvMaxNum = view.findViewById(R.id.tv_max_num);
        TextView tvMinNum = view.findViewById(R.id.tv_min_num);
        TextView tvRemark = view.findViewById(R.id.tv_remark);

        ComboLineColumnChartData clccd = initChartData(context, dailyTargetBean);
        tvTitle.setText(dailyTargetBean.getTitle());
        if (clccd == null) {
            llOverviewConent.setVisibility(GONE);
            ivNoData.setVisibility(VISIBLE);
        } else {
            llOverviewConent.setVisibility(VISIBLE);
            ivNoData.setVisibility(GONE);
            // 初始化图标说明
            IllustrationBean illustrationBean1 = new IllustrationBean(ContextCompat.getColor(context, R.color.colorChart_t3), "完成");
            IllustrationBean illustrationBean2 = new IllustrationBean(ContextCompat.getColor(context, R.color.colorChart_t5), "未完成");
            ArrayList<IllustrationBean> illustrationBeans = new ArrayList<>();
            illustrationBeans.add(illustrationBean1);
            illustrationBeans.add(illustrationBean2);
            // 初始化统计数据
            OverviewBean overviewBean = MyDBOperater.getInstance(context).getOverviewInfoByDailyId(dailyTargetBean.getDailyId());
            // 填值
            clccvChart.setComboLineColumnChartData(clccd);
            clccvChart.setZoomEnabled(false);
            clccvChart.setScrollEnabled(true);
            clccvChart.setHorizontalScrollBarEnabled(true);
            clccvChart.setOverScrollMode(ComboLineColumnChartView.OVER_SCROLL_IF_CONTENT_SCROLLS);
            Viewport viewport = new Viewport();
            viewport.set(-1, getMaxYValue(clccd), 4, 0);
            clccvChart.setCurrentViewport(viewport);
            CommonAdapter<IllustrationBean> mAdapter = new CommonAdapter<IllustrationBean>(context, R.layout.item_illustrations, illustrationBeans) {
                @Override
                protected void convert(ViewHolder viewHolder, IllustrationBean item, int position) {
                    ImageView ivColor = viewHolder.getView(R.id.iv_color);
                    TextView tvName = viewHolder.getView(R.id.tv_name);
                    ivColor.setBackgroundColor(item.getColorID());
                    tvName.setText(item.getIllustration());
                }
            };
            nsgv.setAdapter(mAdapter);
            tvTargetTotal.setText(String.format(getResources().getString(R.string.target_total), overviewBean.getTargetTotal()));
            tvDoneTotal.setText(String.format(getResources().getString(R.string.done_total), overviewBean.getDoneTotal()));
            tvOweTotal.setText(String.format(getResources().getString(R.string.owe_total), overviewBean.getOweTotal()));
            tvAvgNum.setText(String.format(getResources().getString(R.string.avg_num), overviewBean.getAvgNum()));
            tvMaxNum.setText(String.format(getResources().getString(R.string.max_num), overviewBean.getMaxNum()));
            tvMinNum.setText(String.format(getResources().getString(R.string.min_num), overviewBean.getMinNum()));
            tvRemark.setText(String.format(getResources().getString(R.string.overview_remark), overviewBean.getDaysTotal(), overviewBean.getDaysPass(), overviewBean.getDaysNoPass(), overviewBean.getPassRate()));
        }
    }

    public ComboLineColumnChartLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 获取Y轴最大值，用于设置ViewPort
     *
     * @param clccd 图表数据
     * @return Y轴最大值
     */
    private float getMaxYValue(ComboLineColumnChartData clccd) {
        List<Column> columns = clccd.getColumnChartData().getColumns();
        float maxValue = 0;
        for (Column column : columns) {
            List<SubcolumnValue> subcolumnValues = column.getValues();
            if (subcolumnValues.size() > 0) {
                SubcolumnValue subcolumnValue = subcolumnValues.get(0);
                if (maxValue < subcolumnValue.getValue()) {
                    maxValue = subcolumnValue.getValue();
                }
            }
        }
        return maxValue;
    }

    /**
     * 生成图表数据
     *
     * @param dailyTarget 目标数据
     * @return 图表数据
     */
    private ComboLineColumnChartData initChartData(Context context, DailyTargetBean dailyTarget) {
        ArrayList<TargetBean> targets = MyDBOperater.getInstance(context).getTargetsByDailyId(dailyTarget.getDailyId());
        if (targets == null) {
            return null;
        }
        ColorPicker colorPicker = new ColorPicker(context);

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
