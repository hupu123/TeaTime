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
import com.hugh.teatime.models.gasoline.IllustrationBean;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class ComboLineColumnChartLayoutView extends LinearLayout {

    public ComboLineColumnChartLayoutView(Context context, String title, ComboLineColumnChartData clccd) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_clccv_layout, this);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        ComboLineColumnChartView clccvChart = view.findViewById(R.id.clccv_chart);
        NoScrollGridView nsgv = view.findViewById(R.id.nsgv_illustration);

        // 初始化图标说明
        IllustrationBean illustrationBean1 = new IllustrationBean(ContextCompat.getColor(context, R.color.colorChart_t3), "完成");
        IllustrationBean illustrationBean2 = new IllustrationBean(ContextCompat.getColor(context, R.color.colorChart_t5), "未完成");
        ArrayList<IllustrationBean> illustrationBeans = new ArrayList<>();
        illustrationBeans.add(illustrationBean1);
        illustrationBeans.add(illustrationBean2);

        // 填值
        tvTitle.setText(title);
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
}
