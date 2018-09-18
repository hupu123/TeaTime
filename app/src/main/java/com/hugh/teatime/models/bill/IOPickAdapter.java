package com.hugh.teatime.models.bill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;

/**
 * Created by Hugh on 2016/4/6 10:28
 */
public class IOPickAdapter extends BaseAdapter {

    private Context context;
    private final String[] iotypes = GlobalVar.BILL_IO_TYPE_NAME_ARRAY;

    public IOPickAdapter(Context context) {

        this.context = context;
    }

    @Override
    public int getCount() {
        return iotypes.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_io_type_s, null);
        }

        if (position == iotypes.length - 1) {
            convertView.setPadding(0, 0, 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 1);
        }
        TextView tvIOType = convertView.findViewById(R.id.tv_iopa_iotype_name);
        tvIOType.setText(iotypes[position]);
        if (position == 0) {
            tvIOType.setBackground(context.getResources().getDrawable(R.drawable.btn_click_bg1_selector));
        } else {
            tvIOType.setBackground(context.getResources().getDrawable(R.drawable.btn_click_bg_selector));
        }

        return convertView;
    }
}
