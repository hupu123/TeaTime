package com.hugh.teatime.models.bill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;

import java.util.List;

/**
 * Created by Hugh on 2016/4/5 16:47
 */
public class BillListAdapter extends BaseAdapter {

    private Context context;
    private List<Bill> bills;

    BillListAdapter(Context context, List<Bill> bills) {

        this.context = context;
        this.bills = bills;
    }

    @Override
    public int getCount() {
        if (bills != null) {
            return bills.size();
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

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_bills_lv, null);
            holder = new ViewHolder();
            holder.llItem = convertView.findViewById(R.id.ll_bill_item);
            holder.tvAmount = convertView.findViewById(R.id.tv_bill_amount);
            holder.tvDate = convertView.findViewById(R.id.tv_bill_date);
            holder.tvTypeName = convertView.findViewById(R.id.tv_bill_type_name);
            holder.ivTypeIcon = convertView.findViewById(R.id.iv_bill_type_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bill bill = bills.get(position);
        if (position == bills.size() - 1) {
            convertView.setPadding(0, 20, 0, 20);
        } else {
            convertView.setPadding(0, 20, 0, 0);
        }
        if (bill.getIoType() == 0) {
            //            holder.llItem.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.llItem.setBackgroundResource(R.drawable.btn_click_bg1_selector);
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.colorAccent_2));
            holder.tvAmount.setText("-" + bill.getAmount() + "¥");
        } else {
            //            holder.llItem.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            holder.llItem.setBackgroundResource(R.drawable.btn_click_bg_selector);
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.colorGreen_2));
            holder.tvAmount.setText("+" + bill.getAmount() + "¥");
        }
        holder.tvDate.setText(bill.getYear() + "-" + bill.getMonth() + "-" + bill.getDay());
        holder.tvTypeName.setText(bill.getTypeName());
        holder.ivTypeIcon.setImageResource(GlobalVar.BILL_TYPE_ICON_ARRAY[bill.getType()]);

        return convertView;
    }

    private class ViewHolder {

        LinearLayout llItem;
        TextView tvAmount;
        TextView tvDate;
        TextView tvTypeName;
        ImageView ivTypeIcon;
    }
}
