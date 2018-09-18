package com.hugh.teatime.models.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.view.dsgv.DragAdapter;

import java.util.ArrayList;

public class MyDsgvAdapter extends DragAdapter {

    private ArrayList<ModelBean> datas;
    private LayoutInflater inflater;

    MyDsgvAdapter(Context context, ArrayList<ModelBean> datas) {
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onDataModelMove(int from, int to) {
        if (datas != null && from >= 0 && from < datas.size() && to >= 0 && to < datas.size()) {
            ModelBean modelBean = datas.remove(from);
            datas.add(to, modelBean);
        }
    }

    @Override
    public int getCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    @Override
    public ModelBean getItem(int position) {
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
            convertView = inflater.inflate(R.layout.item_home_model, null);
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = convertView.findViewById(R.id.iv_icon);
            viewHolder.tvName = convertView.findViewById(R.id.tv_name);
            viewHolder.tvDescription = convertView.findViewById(R.id.tv_description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ModelBean modelBean = datas.get(position);
        viewHolder.ivIcon.setImageResource(modelBean.getIcon());
        viewHolder.tvName.setText(modelBean.getName());
        viewHolder.tvDescription.setText(modelBean.getDescription());
        return convertView;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvDescription;
    }
}
