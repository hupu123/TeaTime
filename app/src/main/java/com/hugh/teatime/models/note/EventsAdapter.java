package com.hugh.teatime.models.note;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
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
        final EventBean eventBean = events.get(position);
        boolean isShowDateLine = false;
        if (position == 0) {
            isShowDateLine = true;
            eventBean.setItemLeft(true);
        } else {
            EventBean eventBean1 = events.get(position - 1);
            String time = StringUtil.formatTimestamp2(eventBean.getDate());
            String time1 = StringUtil.formatTimestamp2(eventBean1.getDate());
            if (time.equals(time1)) {
                eventBean.setItemLeft(eventBean1.isItemLeft());
            } else {
                isShowDateLine = true;
                if (eventBean1.isItemLeft()) {
                    eventBean.setItemLeft(false);
                } else {
                    eventBean.setItemLeft(true);
                }
            }
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        if (eventBean.isItemLeft()) {
            convertView = inflater.inflate(R.layout.item_note_1, null);
        } else {
            convertView = inflater.inflate(R.layout.item_note, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.llContent = convertView.findViewById(R.id.ll_content);
        viewHolder.rlDateLine = convertView.findViewById(R.id.rl_date_line);
        viewHolder.tvDateLine = convertView.findViewById(R.id.tv_date_line);
        viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
        viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
        viewHolder.tvDate = convertView.findViewById(R.id.tv_date);
        viewHolder.tvLocation = convertView.findViewById(R.id.tv_location);
        convertView.setTag(viewHolder);

        viewHolder.llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra(GlobalVar.INTENT_EVENT, eventBean);
                context.startActivity(intent);
            }
        });
        if (isShowDateLine) {
            viewHolder.rlDateLine.setVisibility(View.VISIBLE);
        } else {
            viewHolder.rlDateLine.setVisibility(View.GONE);
        }
        if (StringUtil.isStrNull(eventBean.getContent())) {
            viewHolder.tvContent.setVisibility(View.GONE);
        } else {
            viewHolder.tvContent.setVisibility(View.VISIBLE);
        }
        if (StringUtil.isStrNull(eventBean.getAddress())) {
            viewHolder.tvLocation.setVisibility(View.GONE);
        } else {
            viewHolder.tvLocation.setVisibility(View.VISIBLE);
        }
        viewHolder.tvDateLine.setText(StringUtil.formatTimestamp(eventBean.getDate()));
        viewHolder.tvTitle.setText(eventBean.getTitle());
        viewHolder.tvContent.setText(eventBean.getContent());
        viewHolder.tvDate.setText(StringUtil.formatTimestamp3(eventBean.getDate()));
        viewHolder.tvLocation.setText(eventBean.getAddress());

        return convertView;
    }

    private class ViewHolder {
        LinearLayout llContent;         // 内容显示区域
        RelativeLayout rlDateLine;      // 日期分割线
        TextView tvDateLine;            // 日期分割线日期
        TextView tvTitle;               // 标题
        TextView tvContent;             // 内容
        TextView tvDate;                // 时间
        TextView tvLocation;            // 位置
    }
}
