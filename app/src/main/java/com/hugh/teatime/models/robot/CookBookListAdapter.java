package com.hugh.teatime.models.robot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.VolleyUtil;

import java.util.List;

/**
 * Created by Hugh on 2016/2/16 17:12
 */
public class CookBookListAdapter extends BaseAdapter {

    private Context context;
    private List<CookBook> cookBookList;

    public CookBookListAdapter(Context context, List<CookBook> cookBookList) {

        this.context = context;
        this.cookBookList = cookBookList;
    }

    @Override
    public int getCount() {
        if (cookBookList != null) {
            return cookBookList.size();
        }
        return 0;
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

        ViewHolder viewHolder;
        CookBook cookBook = cookBookList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_cookbook_lv, null);
            viewHolder = new ViewHolder();

            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_cookbook_icon);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_cookbook_name);
            viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.tv_cookbook_info);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!StringUtil.isStrNull(cookBook.getIcon())) {
            VolleyUtil.getInstance(context).requestImageByImageLoader(viewHolder.ivIcon, cookBook.getIcon(), 100, 100);
        }
        viewHolder.tvName.setText(cookBook.getName());
        viewHolder.tvInfo.setText(cookBook.getInfo());

        return convertView;
    }

    private class ViewHolder {

        ImageView ivIcon;
        TextView tvName;
        TextView tvInfo;
    }
}
