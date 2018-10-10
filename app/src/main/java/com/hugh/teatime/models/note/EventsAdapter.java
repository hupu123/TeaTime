package com.hugh.teatime.models.note;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.StringUtil;

import java.util.ArrayList;

public class EventsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<EventBean> events;

    EventsAdapter(Context context, ArrayList<EventBean> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        if (events != null) {
            return events.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (events != null) {
            return events.get(position);
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
        EventBean eventBean = events.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            switch (eventBean.getItemType()) {
                case 0:
                    convertView = inflater.inflate(R.layout.item_note_1, null);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.item_note, null);
                    break;
                default:
                    convertView = inflater.inflate(R.layout.item_note, null);
                    break;
            }
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvDate = convertView.findViewById(R.id.tv_date);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTitle.setText(eventBean.getTitle());
        viewHolder.tvDate.setText(StringUtil.formatTimestamp1(eventBean.getDate()));

        return convertView;
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvDate;
    }
}
