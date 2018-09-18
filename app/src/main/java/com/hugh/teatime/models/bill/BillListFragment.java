package com.hugh.teatime.models.bill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.hugh.teatime.R;
import com.hugh.teatime.db.MyDBOperater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hugh on 2016/4/8 9:52
 */
public class BillListFragment extends Fragment {

    private List<Bill> bills;
    private Context context;
    private BillListAdapter bla;

    private ImageView ivBillNoData;
    private ListView lvBillList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_bill_list, container, false);

        lvBillList = contentView.findViewById(R.id.lv_bill_list);
        ivBillNoData = contentView.findViewById(R.id.iv_bill_no_data);
        Button btnAddBill = contentView.findViewById(R.id.btn_add_bill);

        bills = new ArrayList<>();
        bla = new BillListAdapter(context, bills);
        lvBillList.setAdapter(bla);
        lvBillList.setOnItemClickListener(itemClickListener);
        btnAddBill.setOnClickListener(clickListener);

        return contentView;
    }

    /**
     * 更新列表数据
     *
     * @param month 月份
     */
    public void updateData(int month, int year) {

        if (context == null || lvBillList == null || ivBillNoData == null || bills == null || bla == null) {
            return;
        }

        List<Bill> billsTemp = MyDBOperater.getInstance(context).getBills(month, year);
        if (billsTemp != null && billsTemp.size() > 0) {
            lvBillList.setVisibility(View.VISIBLE);
            ivBillNoData.setVisibility(View.GONE);
            bills.clear();
            bills.addAll(billsTemp);
            bla.notifyDataSetChanged();
        } else {
            lvBillList.setVisibility(View.GONE);
            ivBillNoData.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_add_bill:
                    Intent intent = new Intent(context, RecordBillActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ListView item点击事件监听
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(context, BillDetailActivity.class);
            intent.putExtra(BillHomeActivity.INTENT_BILL_INFO, bills.get(position));
            startActivity(intent);
        }
    };
}
