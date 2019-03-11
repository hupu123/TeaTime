package com.hugh.teatime.models.target;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.DimensUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.view.ProgressItemView;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class TargetListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private static final long DAY = 24 * 60 * 60 * 1000;
    private static final long HOUR = 60 * 60 * 1000;
    private static final long MINUTE = 60 * 1000;

    private Context mContext;
    private ArrayList<TargetBean> datas;

    TargetListAdapter(Context context, ArrayList<TargetBean> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_targets_header, null);
            holder.tvType = convertView.findViewById(R.id.tv_target_type);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        int type = datas.get(position).getType();
        switch (type) {
            case TargetBean.TYPE_DAILY:
                holder.tvType.setText("每日目标");
                break;
            case TargetBean.TYPE_ONETIME:
                holder.tvType.setText("一次性目标");
                break;
            default:
                break;
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return datas.get(position).getType();
    }

    @Override
    public int getCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (datas != null) {
            return datas.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_targets_list, null);
            holder.rlContent = view.findViewById(R.id.rl_content);
            holder.pivProgress = view.findViewById(R.id.piv_progress);
            holder.tvTitle = view.findViewById(R.id.tv_target_title);
            holder.tvName = view.findViewById(R.id.tv_target_name);
            holder.ivStatus = view.findViewById(R.id.iv_status);
            holder.llTimeBar = view.findViewById(R.id.ll_target_time);
            holder.tvStartTime = view.findViewById(R.id.tv_target_start_time);
            holder.tvEndTime = view.findViewById(R.id.tv_target_end_time);
            holder.tvRemainingTime = view.findViewById(R.id.tv_target_remaining_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvTitle.setText(datas.get(i).getTitle());
        holder.tvName.setText(String.format(mContext.getResources().getString(R.string.th_tn), datas.get(i).getTargetName(), datas.get(i).getDoneNum(), datas.get(i).getTargetNum()));
        int progress = 100 * datas.get(i).getDoneNum() / datas.get(i).getTargetNum();
        if (progress >= 100) {
            datas.get(i).setStatus(TargetBean.STATUS_COMPLETED);
        } else {
            if (System.currentTimeMillis() >= datas.get(i).getEndTime()) {
                datas.get(i).setStatus(TargetBean.STATUS_UNCOMPLETED);
            } else {
                datas.get(i).setStatus(TargetBean.STATUS_RUNNING);
            }
        }
        holder.pivProgress.setMax(datas.get(i).getTargetNum());
        holder.pivProgress.setProgress(datas.get(i).getDoneNum());
        switch (datas.get(i).getStatus()) {
            case TargetBean.STATUS_RUNNING:
                holder.ivStatus.setImageResource(R.mipmap.icon_running);
                break;
            case TargetBean.STATUS_COMPLETED:
                holder.ivStatus.setImageResource(R.mipmap.icon_completed);
                break;
            case TargetBean.STATUS_UNCOMPLETED:
                holder.ivStatus.setImageResource(R.mipmap.icon_uncompleted);
                break;
            default:
                break;
        }
        if (datas.get(i).getType() == TargetBean.TYPE_DAILY) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensUtil.getInstance(mContext).dp2px(80));
            params.setMargins(DimensUtil.getInstance(mContext).dp2px(10), DimensUtil.getInstance(mContext).dp2px(5), DimensUtil.getInstance(mContext).dp2px(10), DimensUtil.getInstance(mContext).dp2px(5));
            holder.rlContent.setLayoutParams(params);
            holder.llTimeBar.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensUtil.getInstance(mContext).dp2px(100));
            params.setMargins(DimensUtil.getInstance(mContext).dp2px(10), DimensUtil.getInstance(mContext).dp2px(5), DimensUtil.getInstance(mContext).dp2px(10), DimensUtil.getInstance(mContext).dp2px(5));
            holder.rlContent.setLayoutParams(params);
            holder.llTimeBar.setVisibility(View.VISIBLE);
            holder.tvStartTime.setText(String.format(mContext.getResources().getString(R.string.th_st), StringUtil.formatTimestamp1(datas.get(i).getStartTime())));
            holder.tvEndTime.setText(String.format(mContext.getResources().getString(R.string.th_et), StringUtil.formatTimestamp1(datas.get(i).getEndTime())));
        }
        holder.tvRemainingTime.setText(getTimeDiff(datas.get(i).getType(), datas.get(i).getEndTime(), System.currentTimeMillis()));
        return view;
    }

    class HeaderViewHolder {
        TextView tvType;
    }

    class ViewHolder {
        RelativeLayout rlContent;
        ProgressItemView pivProgress;
        TextView tvTitle;
        TextView tvName;
        ImageView ivStatus;
        LinearLayout llTimeBar;
        TextView tvStartTime;
        TextView tvEndTime;
        TextView tvRemainingTime;
    }

    /**
     * 计算两个时间戳之间的时间差
     *
     * @param type  目标类型
     * @param timeA A时间
     * @param timeB B时间
     * @return 时间差
     */
    private static String getTimeDiff(int type, long timeA, long timeB) {
        StringBuilder sb = new StringBuilder();
        long timeDiff = timeA - timeB;
        if (timeDiff <= 0) {
            return "已结束";
        }
        long timeDiff1 = timeDiff % DAY;
        long timeDiff2 = timeDiff1 % HOUR;
        long day = (timeDiff - timeDiff % DAY) / DAY;
        long hour = (timeDiff1 - timeDiff1 % HOUR) / HOUR;
        long minute = (timeDiff2 - timeDiff2 % MINUTE) / MINUTE;
        if (day != 0) {
            if (type == TargetBean.TYPE_DAILY) {
                return "未开始";
            } else {
                sb.append(day);
                sb.append("天");
            }
        }
        if (hour != 0) {
            sb.append(hour);
            sb.append("小时");
        }
        if (minute != 0) {
            sb.append(minute);
            sb.append("分钟");
        }
        return sb.toString();
    }
}
