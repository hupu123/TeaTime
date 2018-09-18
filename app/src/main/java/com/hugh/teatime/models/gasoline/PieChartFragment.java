package com.hugh.teatime.models.gasoline;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.ColorPicker;
import com.hugh.teatime.utils.ToolUtil;
import com.hugh.teatime.view.NoScrollGridView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChartFragment extends Fragment {

    private TextView tvCarTitle;
    private TextView tvInvoiceTitle;
    private PieChartView pcvCar;
    private PieChartView pcvInvoice;
    private NoScrollGridView nsgvCarIllustration;
    private NoScrollGridView nsgvInvoiceIllustration;

    private Context context;
    private int year = 0;
    private CommonAdapter<IllustrationBean> mCarAdapter;
    private CommonAdapter<IllustrationBean> mInvoiceAdapter;
    private ArrayList<IllustrationBean> carIllustrations = new ArrayList<>();
    private ArrayList<IllustrationBean> invoiceIllustrations = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gasoline_pie, container, false);
        initView(view);
        initData();
        return view;
    }

    /**
     * 初始化控件
     *
     * @param view 根容器
     */
    private void initView(View view) {
        tvCarTitle = view.findViewById(R.id.tv_car_proportion_title);
        tvInvoiceTitle = view.findViewById(R.id.tv_invoice_proportion_title);
        pcvCar = view.findViewById(R.id.pcv_car_proportion);
        pcvInvoice = view.findViewById(R.id.pcv_invoice_proportion);
        nsgvCarIllustration = view.findViewById(R.id.nsgv_car_illustration);
        nsgvInvoiceIllustration = view.findViewById(R.id.nsgv_invoice_illustration);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mCarAdapter = new CommonAdapter<IllustrationBean>(context, R.layout.item_illustrations, carIllustrations) {
            @Override
            protected void convert(ViewHolder viewHolder, IllustrationBean item, int position) {
                ImageView ivColor = viewHolder.getView(R.id.iv_color);
                TextView tvName = viewHolder.getView(R.id.tv_name);
                ivColor.setBackgroundColor(item.getColorID());
                tvName.setText(item.getIllustration());
            }
        };
        nsgvCarIllustration.setAdapter(mCarAdapter);

        mInvoiceAdapter = new CommonAdapter<IllustrationBean>(context, R.layout.item_illustrations, invoiceIllustrations) {
            @Override
            protected void convert(ViewHolder viewHolder, IllustrationBean item, int position) {
                ImageView ivColor = viewHolder.getView(R.id.iv_color);
                TextView tvName = viewHolder.getView(R.id.tv_name);
                ivColor.setBackgroundColor(item.getColorID());
                tvName.setText(item.getIllustration());
            }
        };
        nsgvInvoiceIllustration.setAdapter(mInvoiceAdapter);

        if (year != 0) {
            onDataRefresh(year);
        }
    }

    /**
     * 设置年份
     *
     * @param year 年份
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * 刷新数据
     *
     * @param year 年份
     */
    public void onDataRefresh(int year) {
        this.year = year;
        tvCarTitle.setText(String.format(getResources().getString(R.string.pie_car_chart_title), year));
        tvInvoiceTitle.setText(String.format(getResources().getString(R.string.pie_invoice_chart_title), year));
        initCarPieChart(year);
        initInvoicePieChart(year);
    }

    /**
     * 刷新表一
     *
     * @param year 年份
     */
    private void initCarPieChart(int year) {
        MyDBOperater mDB = MyDBOperater.getInstance(context);
        ArrayList<String> cars = mDB.getCars();
        if (cars == null) {
            return;
        }
        ColorPicker colorPicker = new ColorPicker(context);
        List<SliceValue> sliceValues = new ArrayList<>();
        carIllustrations.clear();
        for (String car : cars) {
            int colorID = colorPicker.getColor();
            double sum = mDB.getSumOfPrice(0, year, car);
            float sumF = new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            SliceValue sliceValue = new SliceValue();
            sliceValue.setValue(sumF);
            sliceValue.setColor(colorID);
            sliceValue.setLabel(sumF + "");

            sliceValues.add(sliceValue);
            carIllustrations.add(new IllustrationBean(colorID, car));
        }
        PieChartData pieChartData = new PieChartData(ToolUtil.sortChartData(sliceValues));
        pieChartData.setHasLabels(true);
        pieChartData.setValueLabelsTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        pieChartData.setHasCenterCircle(false);

        pcvCar.setPieChartData(pieChartData);
        pcvCar.setChartRotationEnabled(false);
        mCarAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新表二
     *
     * @param year 年份
     */
    private void initInvoicePieChart(int year) {
        MyDBOperater mDB = MyDBOperater.getInstance(context);
        String[] invoices = new String[]{"0", "1"};
        ColorPicker colorPicker = new ColorPicker(context);
        List<SliceValue> sliceValues = new ArrayList<>();
        invoiceIllustrations.clear();
        for (String invoice : invoices) {
            int colorID = colorPicker.getColor();
            double sum = mDB.getSumOfPrice(1, year, invoice);
            float sumF = new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            String invoiceStr;
            if (invoice.equals("0")) {
                invoiceStr = getResources().getString(R.string.have_invoiced);
            } else {
                invoiceStr = getResources().getString(R.string.have_not_invoiced);
            }
            SliceValue sliceValue = new SliceValue();
            sliceValue.setValue(sumF);
            sliceValue.setColor(colorID);
            sliceValue.setLabel(sumF + "");

            sliceValues.add(sliceValue);
            invoiceIllustrations.add(new IllustrationBean(colorID, invoiceStr));
        }
        PieChartData pieChartData = new PieChartData(ToolUtil.sortChartData(sliceValues));
        pieChartData.setHasLabels(true);
        pieChartData.setValueLabelsTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        pieChartData.setHasCenterCircle(false);

        pcvInvoice.setPieChartData(pieChartData);
        pcvInvoice.setChartRotationEnabled(false);
        mInvoiceAdapter.notifyDataSetChanged();
    }
}
