package com.hugh.teatime.models.bill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hugh.teatime.R;

/**
 * Created by Hugh on 2016/4/13 11:34
 */
public class YearPickAdapter extends BaseAdapter {

    private Context context;
    private int[] years;

    public YearPickAdapter(Context context, int[] years) {

        this.context = context;
        this.years = years;
    }

    @Override
    public int getCount() {
        return years.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_year_s, null);
        }

        if (position == years.length - 1) {
            convertView.setPadding(0, 0, 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 1);
        }
        TextView tvMonth = (TextView) convertView.findViewById(R.id.tv_ypa_year);
        tvMonth.setText(years[position] + "年");

        return convertView;
    }

    /**
     * 通过值获取该值在数组中的位置
     *
     * @param value 值
     *
     * @return 位置
     */
    public int getPositionByValue(int value) {

        int position = 0;
        if (years == null || years.length == 0) {
            return position;
        }

        for (int i = 0; i < years.length; i++) {
            if (years[i] == value) {
                position = i;
            }
        }

        return position;
    }

    /**
     * 通过位置获取值
     *
     * @param position 位置
     *
     * @return 值
     */
    public int getValueByPosition(int position) {

        if (years == null || years.length == 0 || position >= years.length) {
            return 0;
        }

        return years[position];
    }
}
