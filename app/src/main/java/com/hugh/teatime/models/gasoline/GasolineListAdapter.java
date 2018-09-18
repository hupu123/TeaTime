package com.hugh.teatime.models.gasoline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToolUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class GasolineListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context context;
    private ArrayList<GasolineBean> datas;
    private String filterTag = "";
    private ClickListener listener;

    GasolineListAdapter(Context context, ArrayList<GasolineBean> datas, ClickListener listener) {
        this.context = context;
        this.datas = datas;
        this.listener = listener;
    }

    public void setFilterTag(String str) {
        this.filterTag = str;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        if (convertView == null) {
            headerViewHolder = new HeaderViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_gasoline_header_slhlv, null);
            headerViewHolder.tvMonth = convertView.findViewById(R.id.tv_month);
            headerViewHolder.tvClean = convertView.findViewById(R.id.tv_clean);
            convertView.setTag(headerViewHolder);
        } else {
            headerViewHolder = (HeaderViewHolder) convertView.getTag();
        }
        Date date = new Date(datas.get(position).getDate());
        SimpleDateFormat sdf = new SimpleDateFormat(context.getResources().getString(R.string.gasoline_list_header), Locale.CHINA);
        headerViewHolder.tvMonth.setText(sdf.format(date) + filterTag);
        headerViewHolder.tvClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickClean();
            }
        });
        if (StringUtil.isStrNull(filterTag)) {
            headerViewHolder.tvClean.setVisibility(View.GONE);
        } else {
            headerViewHolder.tvClean.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return ToolUtil.getYearFromTimestamp(datas.get(position).getDate());
    }

    @Override
    public int getCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (datas != null) {
            return datas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_gasoline_slhlv, null);
            viewHolder = new ViewHolder();
            viewHolder.llItem = convertView.findViewById(R.id.ll_item);
            viewHolder.tvTotalPrice = convertView.findViewById(R.id.tv_total_price);
            viewHolder.tvMileage = convertView.findViewById(R.id.tv_mileage);
            viewHolder.tvFuelConsumption = convertView.findViewById(R.id.tv_fuel_consumption);
            viewHolder.tvDate = convertView.findViewById(R.id.tv_date);
            viewHolder.tvCarNO = convertView.findViewById(R.id.tv_car_no);
            viewHolder.tvModel = convertView.findViewById(R.id.tv_model);
            viewHolder.tvInvoiced = convertView.findViewById(R.id.tv_invoiced);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final GasolineBean gasolineBean = datas.get(position);
        BigDecimal fuelConsumption = gasolineBean.getTotalPrice().divide(new BigDecimal(gasolineBean.getMileage()), 2, BigDecimal.ROUND_HALF_UP);
        viewHolder.tvTotalPrice.setText(String.format(context.getResources().getString(R.string.gasoline_total_price), StringUtil.formatBigDecimalNum(gasolineBean.getTotalPrice())));
        viewHolder.tvMileage.setText(String.format(context.getResources().getString(R.string.gasoline_mileage), StringUtil.formatDoubleNum(gasolineBean.getMileage())));
        viewHolder.tvFuelConsumption.setText(String.format(context.getResources().getString(R.string.gasoline_fuel_consumption), StringUtil.formatBigDecimalNum(fuelConsumption)));
        viewHolder.tvDate.setText(String.format(context.getResources().getString(R.string.gasoline_date), StringUtil.formatTimestamp(gasolineBean.getDate())));
        viewHolder.tvCarNO.setText(gasolineBean.getCarNO());
        viewHolder.tvModel.setText(gasolineBean.getModel());
        if (gasolineBean.getInvoice() == 0) {
            viewHolder.tvInvoiced.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvInvoiced.setVisibility(View.GONE);
        }
        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(gasolineBean);
            }
        });
        viewHolder.tvCarNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickCarNO(gasolineBean.getCarNO());
            }
        });
        viewHolder.tvModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickModel(gasolineBean.getModel());
            }
        });
        viewHolder.tvInvoiced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickInvoice(gasolineBean.getInvoice());
            }
        });
        return convertView;
    }

    class HeaderViewHolder {
        TextView tvMonth;
        TextView tvClean;
    }

    class ViewHolder {
        LinearLayout llItem;
        TextView tvTotalPrice;
        TextView tvMileage;
        TextView tvFuelConsumption;
        TextView tvDate;
        TextView tvCarNO;
        TextView tvModel;
        TextView tvInvoiced;
    }

    interface ClickListener {

        void onClickItem(GasolineBean gasolineBean);

        void onClickCarNO(String carNO);

        void onClickModel(String model);

        void onClickInvoice(int invoice);

        void onClickClean();
    }
}
