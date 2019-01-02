package com.hugh.teatime.models.gasoline;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToolUtil;
import com.hugh.teatime.view.NoScrollGridView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChartFragment extends Fragment {

    private TextView tvPriceTitle;
    private TextView tvUnitPriceTitle;
    private TextView tvFuelConsumptionTitle;
    private TextView tvMoneyConsumptionTitle;
    private LineChartView lcvPrice;
    private LineChartView lcvUnitPrice;
    private LineChartView lcvFuelConsumption;
    private LineChartView lcvMoneyConsumption;
    private NoScrollGridView nsgvPriceIllustration;
    private NoScrollGridView nsgvUnitPriceIllustration;
    private NoScrollGridView nsgvFuelConsumptionIllustration;
    private NoScrollGridView nsgvMoneyConsumptionIllustration;

    private Context context;
    private int year = 0;
    private CommonAdapter<IllustrationBean> mPriceAdapter;
    private CommonAdapter<IllustrationBean> mUnitPriceAdapter;
    private CommonAdapter<IllustrationBean> mFuelConsumptionAdapter;
    private CommonAdapter<IllustrationBean> mMoneyConsumptionAdapter;
    private ArrayList<IllustrationBean> priceIllustrations = new ArrayList<>();
    private ArrayList<IllustrationBean> unitPriceIllustrations = new ArrayList<>();
    private ArrayList<IllustrationBean> fuelConsumptionIllustrations = new ArrayList<>();
    private ArrayList<IllustrationBean> moneyConsumptionIllustrations = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gasoline_line, container, false);
        initView(view);
        initData();
        return view;
    }

    /**
     * 初始化控件
     *
     * @param view 父控件
     */
    private void initView(View view) {
        tvPriceTitle = view.findViewById(R.id.tv_price_title);
        tvUnitPriceTitle = view.findViewById(R.id.tv_unit_price_title);
        tvFuelConsumptionTitle = view.findViewById(R.id.tv_fuel_consumption_title);
        tvMoneyConsumptionTitle = view.findViewById(R.id.tv_money_consumption_title);
        lcvPrice = view.findViewById(R.id.lcv_gasoline_price);
        lcvUnitPrice = view.findViewById(R.id.lcv_gasoline_unit_price);
        lcvFuelConsumption = view.findViewById(R.id.lcv_gasoline_fuel_consumption);
        lcvMoneyConsumption = view.findViewById(R.id.lcv_gasoline_money_consumption);
        nsgvPriceIllustration = view.findViewById(R.id.nsgv_price_illustration);
        nsgvUnitPriceIllustration = view.findViewById(R.id.nsgv_unit_price_illustration);
        nsgvFuelConsumptionIllustration = view.findViewById(R.id.nsgv_fuel_consumption_illustration);
        nsgvMoneyConsumptionIllustration = view.findViewById(R.id.nsgv_money_consumption_illustration);

        lcvPrice.setInteractive(false);
        lcvUnitPrice.setInteractive(false);
        lcvFuelConsumption.setInteractive(false);
        lcvMoneyConsumption.setInteractive(false);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mPriceAdapter = new CommonAdapter<IllustrationBean>(context, R.layout.item_illustrations, priceIllustrations) {
            @Override
            protected void convert(ViewHolder viewHolder, IllustrationBean item, int position) {
                ImageView ivColor = viewHolder.getView(R.id.iv_color);
                TextView tvName = viewHolder.getView(R.id.tv_name);
                ivColor.setBackgroundColor(item.getColorID());
                tvName.setText(item.getIllustration());
            }
        };
        nsgvPriceIllustration.setAdapter(mPriceAdapter);

        mUnitPriceAdapter = new CommonAdapter<IllustrationBean>(context, R.layout.item_illustrations, unitPriceIllustrations) {
            @Override
            protected void convert(ViewHolder viewHolder, IllustrationBean item, int position) {
                ImageView ivColor = viewHolder.getView(R.id.iv_color);
                TextView tvName = viewHolder.getView(R.id.tv_name);
                ivColor.setBackgroundColor(item.getColorID());
                tvName.setText(item.getIllustration());
            }
        };
        nsgvUnitPriceIllustration.setAdapter(mUnitPriceAdapter);

        mFuelConsumptionAdapter = new CommonAdapter<IllustrationBean>(context, R.layout.item_illustrations, fuelConsumptionIllustrations) {
            @Override
            protected void convert(ViewHolder viewHolder, IllustrationBean item, int position) {
                ImageView ivColor = viewHolder.getView(R.id.iv_color);
                TextView tvName = viewHolder.getView(R.id.tv_name);
                ivColor.setBackgroundColor(item.getColorID());
                tvName.setText(item.getIllustration());
            }
        };
        nsgvFuelConsumptionIllustration.setAdapter(mFuelConsumptionAdapter);

        mMoneyConsumptionAdapter = new CommonAdapter<IllustrationBean>(context, R.layout.item_illustrations, moneyConsumptionIllustrations) {
            @Override
            protected void convert(ViewHolder viewHolder, IllustrationBean item, int position) {
                ImageView ivColor = viewHolder.getView(R.id.iv_color);
                TextView tvName = viewHolder.getView(R.id.tv_name);
                ivColor.setBackgroundColor(item.getColorID());
                tvName.setText(item.getIllustration());
            }
        };
        nsgvMoneyConsumptionIllustration.setAdapter(mMoneyConsumptionAdapter);

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
        tvPriceTitle.setText(String.format(getResources().getString(R.string.line_price_chart_title), year));
        tvUnitPriceTitle.setText(String.format(getResources().getString(R.string.line_unit_price_chart_title), year));
        tvFuelConsumptionTitle.setText(String.format(getResources().getString(R.string.line_fuel_consumption_chart_title), year));
        tvMoneyConsumptionTitle.setText(String.format(getResources().getString(R.string.line_money_consumption_chart_title), year));
        initPriceLineChart(year);
        initUnitPriceLineChart(year);
        initFuelConsumptionLineChart(year);
        initMoneyConsumptionLineChart(year);
    }

    /**
     * 刷新表一
     *
     * @param year 年份
     */
    private void initPriceLineChart(int year) {
        MyDBOperater mDB = MyDBOperater.getInstance(context);
        ArrayList<String> cars = mDB.getCars();
        if (cars == null) {
            return;
        }
        ColorPicker colorPicker = new ColorPicker(context);
        List<Line> lines = new ArrayList<>();
        priceIllustrations.clear();
        for (String car : cars) {
            ArrayList<GasolineBean> datas = mDB.getGasolineRecords(1, car, year);
            ArrayList<PointValue> points = new ArrayList<>();
            for (GasolineBean gasolineBean : datas) {
                if (gasolineBean.getTotalPrice() == null) {
                    continue;
                }
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.setTimeInMillis(gasolineBean.getDate());
                BigDecimal day = new BigDecimal(calendar.get(Calendar.DAY_OF_MONTH));
                BigDecimal dayTotal = new BigDecimal(ToolUtil.getDayNumOfMonth(year, calendar.get(Calendar.MONTH)));
                BigDecimal month = new BigDecimal(calendar.get(Calendar.MONTH));
                float x = month.add(day.divide(dayTotal, 2, BigDecimal.ROUND_HALF_UP)).floatValue();
                PointValue point = new PointValue(x, gasolineBean.getTotalPrice().floatValue());
                point.setLabel(StringUtil.formatBigDecimalNum(gasolineBean.getTotalPrice()));
                points.add(point);
            }

            Line line = new Line(points);
            int colorID = colorPicker.getColor();
            line.setColor(colorID);
            line.setHasLabels(true);
            lines.add(line);
            priceIllustrations.add(new IllustrationBean(colorID, car));
        }

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

        LineChartData lcd = new LineChartData(lines);
        lcd.setAxisXBottom(axisX);
        lcd.setAxisYLeft(axisY);

        lcvPrice.setLineChartData(lcd);
        mPriceAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新表二
     *
     * @param year 年份
     */
    private void initUnitPriceLineChart(int year) {
        MyDBOperater mDB = MyDBOperater.getInstance(context);
        ArrayList<String> models = mDB.getModels();
        if (models == null) {
            return;
        }
        ColorPicker colorPicker = new ColorPicker(context);
        List<Line> lines = new ArrayList<>();
        unitPriceIllustrations.clear();
        for (String model : models) {
            ArrayList<GasolineBean> datas = mDB.getGasolineRecords(2, model, year);
            ArrayList<PointValue> points = new ArrayList<>();
            for (GasolineBean gasolineBean : datas) {
                if (gasolineBean.getUnitPrice() == null) {
                    continue;
                }
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.setTimeInMillis(gasolineBean.getDate());
                BigDecimal day = new BigDecimal(calendar.get(Calendar.DAY_OF_MONTH));
                BigDecimal dayTotal = new BigDecimal(ToolUtil.getDayNumOfMonth(year, calendar.get(Calendar.MONTH)));
                BigDecimal month = new BigDecimal(calendar.get(Calendar.MONTH));
                float x = month.add(day.divide(dayTotal, 2, BigDecimal.ROUND_HALF_UP)).floatValue();
                PointValue point = new PointValue(x, gasolineBean.getUnitPrice().floatValue());
                point.setLabel(StringUtil.formatBigDecimalNum(gasolineBean.getUnitPrice()));
                points.add(point);
            }

            Line line = new Line(points);
            int colorID = colorPicker.getColor();
            line.setColor(colorID);
            line.setHasLabels(true);
            lines.add(line);
            unitPriceIllustrations.add(new IllustrationBean(colorID, model));
        }

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

        LineChartData lcd = new LineChartData(lines);
        lcd.setAxisXBottom(axisX);
        lcd.setAxisYLeft(axisY);

        lcvUnitPrice.setLineChartData(lcd);
        mUnitPriceAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新表三
     *
     * @param year 年份
     */
    private void initFuelConsumptionLineChart(int year) {
        MyDBOperater mDB = MyDBOperater.getInstance(context);
        ArrayList<String> cars = mDB.getCars();
        if (cars == null) {
            return;
        }
        ColorPicker colorPicker = new ColorPicker(context);
        List<Line> lines = new ArrayList<>();
        fuelConsumptionIllustrations.clear();
        for (String car : cars) {
            ArrayList<GasolineBean> datas = mDB.getGasolineRecords(1, car, year);
            ArrayList<PointValue> points = new ArrayList<>();
            for (GasolineBean gasolineBean : datas) {
                if (gasolineBean.getTotalPrice() == null || gasolineBean.getMileage() == 0) {
                    continue;
                }
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.setTimeInMillis(gasolineBean.getDate());
                BigDecimal day = new BigDecimal(calendar.get(Calendar.DAY_OF_MONTH));
                BigDecimal dayTotal = new BigDecimal(ToolUtil.getDayNumOfMonth(year, calendar.get(Calendar.MONTH)));
                BigDecimal month = new BigDecimal(calendar.get(Calendar.MONTH));
                float x = month.add(day.divide(dayTotal, 2, BigDecimal.ROUND_HALF_UP)).floatValue();
                float y = new BigDecimal(gasolineBean.getQuantity()).divide(new BigDecimal(gasolineBean.getMileage()), 2, BigDecimal.ROUND_HALF_UP).floatValue();
                PointValue point = new PointValue(x, y);
                point.setLabel(String.valueOf(y));
                points.add(point);
            }

            Line line = new Line(points);
            int colorID = colorPicker.getColor();
            line.setColor(colorID);
            line.setHasLabels(true);
            lines.add(line);
            fuelConsumptionIllustrations.add(new IllustrationBean(colorID, car));
        }

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

        LineChartData lcd = new LineChartData(lines);
        lcd.setAxisXBottom(axisX);
        lcd.setAxisYLeft(axisY);

        lcvFuelConsumption.setLineChartData(lcd);
        mFuelConsumptionAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新表四
     *
     * @param year 年份
     */
    private void initMoneyConsumptionLineChart(int year) {
        MyDBOperater mDB = MyDBOperater.getInstance(context);
        ArrayList<String> cars = mDB.getCars();
        if (cars == null) {
            return;
        }
        ColorPicker colorPicker = new ColorPicker(context);
        List<Line> lines = new ArrayList<>();
        moneyConsumptionIllustrations.clear();
        for (String car : cars) {
            ArrayList<GasolineBean> datas = mDB.getGasolineRecords(1, car, year);
            ArrayList<PointValue> points = new ArrayList<>();
            for (GasolineBean gasolineBean : datas) {
                if (gasolineBean.getTotalPrice() == null || gasolineBean.getMileage() == 0) {
                    continue;
                }
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.setTimeInMillis(gasolineBean.getDate());
                BigDecimal day = new BigDecimal(calendar.get(Calendar.DAY_OF_MONTH));
                BigDecimal dayTotal = new BigDecimal(ToolUtil.getDayNumOfMonth(year, calendar.get(Calendar.MONTH)));
                BigDecimal month = new BigDecimal(calendar.get(Calendar.MONTH));
                float x = month.add(day.divide(dayTotal, 2, BigDecimal.ROUND_HALF_UP)).floatValue();
                float y = gasolineBean.getTotalPrice().divide(new BigDecimal(gasolineBean.getMileage()), 2, BigDecimal.ROUND_HALF_UP).floatValue();
                PointValue point = new PointValue(x, y);
                point.setLabel(String.valueOf(y));
                points.add(point);
            }

            Line line = new Line(points);
            int colorID = colorPicker.getColor();
            line.setColor(colorID);
            line.setHasLabels(true);
            lines.add(line);
            moneyConsumptionIllustrations.add(new IllustrationBean(colorID, car));
        }

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

        LineChartData lcd = new LineChartData(lines);
        lcd.setAxisXBottom(axisX);
        lcd.setAxisYLeft(axisY);

        lcvMoneyConsumption.setLineChartData(lcd);
        mMoneyConsumptionAdapter.notifyDataSetChanged();
    }
}
