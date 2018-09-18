package com.hugh.teatime.models.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.ToolUtil;

import java.util.List;

/**
 * Created by Hugh on 2016/4/15 17:55
 */
public class SMSListAdapter extends BaseAdapter {

    private Context context;
    private List<SMS> smses;

    public SMSListAdapter(Context context, List<SMS> smses) {

        this.context = context;
        this.smses = smses;
    }

    @Override
    public int getCount() {

        if (smses != null) {
            return smses.size();
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
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_all_sms_lv, null);

            viewHolder = new ViewHolder();
            viewHolder.tvSender = convertView.findViewById(R.id.tv_sms_sender);
            viewHolder.tvBody = convertView.findViewById(R.id.tv_sms_body);
            viewHolder.tvDate = convertView.findViewById(R.id.tv_sms_date);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SMS sms = smses.get(position);

        viewHolder.tvSender.setText(sms.getNumber());
        viewHolder.tvBody.setText(sms.getBody());
        viewHolder.tvDate.setText(ToolUtil.getTimeFromTimestamp(sms.getDate(), 2));

        return convertView;
    }

    private class ViewHolder {

        TextView tvSender;
        TextView tvBody;
        TextView tvDate;
    }
}
