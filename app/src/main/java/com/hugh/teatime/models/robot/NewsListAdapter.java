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
 * Created by Hugh on 2016/2/16 17:10
 */
public class NewsListAdapter extends BaseAdapter {

    private List<News> newsList;
    private Context context;

    public NewsListAdapter(Context context, List<News> newsList) {

        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public int getCount() {
        if (newsList != null) {
            return newsList.size();
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
        News news = newsList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_news_lv, null);

            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_news_icon);
            viewHolder.tvArticle = (TextView) convertView.findViewById(R.id.tv_news_article);
            viewHolder.tvSource = (TextView) convertView.findViewById(R.id.tv_news_source);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!StringUtil.isStrNull(news.getIcon())) {
            VolleyUtil.getInstance(context).requestImageByImageLoader(viewHolder.ivIcon, news.getIcon(), 100, 100);
        }
        viewHolder.tvArticle.setText(news.getArticle());
        viewHolder.tvSource.setText(news.getSource());

        return convertView;
    }

    private class ViewHolder {

        ImageView ivIcon;
        TextView tvArticle;
        TextView tvSource;
    }
}
