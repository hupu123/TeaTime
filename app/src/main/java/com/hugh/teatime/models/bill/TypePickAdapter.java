package com.hugh.teatime.models.bill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;

/**
 * Created by Hugh on 2016/4/6 10:41
 */
public class TypePickAdapter extends BaseAdapter {

    private Context context;
    private final String[] types = GlobalVar.BILL_TYPE_NAME_ARRAY;

    public TypePickAdapter(Context context) {

        this.context = context;
    }

    @Override
    public int getCount() {
        return types.length;
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
            convertView = inflater.inflate(R.layout.item_type_s, null);
        }

        if (position == types.length - 1) {
            convertView.setPadding(0, 0, 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 1);
        }
        TextView tvTypeName = (TextView) convertView.findViewById(R.id.tv_type_name);
        tvTypeName.setText(types[position]);

        return convertView;
    }
}
