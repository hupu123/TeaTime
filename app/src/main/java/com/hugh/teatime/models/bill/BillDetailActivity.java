package com.hugh.teatime.models.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.listener.DialogListener;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.view.TitlebarView;

/**
 * 账单详情页
 */
public class BillDetailActivity extends BaseActivity {

    private Bill bill;// 账单信息
    private static BillDetailActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        instance = this;
        Intent intent = getIntent();
        bill = (Bill) intent.getSerializableExtra(BillHomeActivity.INTENT_BILL_INFO);

        initView();
    }

    /**
     * 退出BillDetailActivity
     */
    public static void exit() {

        if (instance != null) {
            instance.finish();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        Button btnBdDelete = findViewById(R.id.btn_bd_delet);
        Button btnBdModify = findViewById(R.id.btn_bd_modify);
        ImageView ivBdNoData = findViewById(R.id.iv_bd_no_data);
        ScrollView svBdInfo = findViewById(R.id.sv_bd_info);
        TextView tvBdAmount = findViewById(R.id.tv_bd_amount);
        ImageView ivBdTypeIcon = findViewById(R.id.iv_bd_type_icon);
        TextView tvBdTypeName = findViewById(R.id.tv_bd_type_name);
        TextView tvBdDate = findViewById(R.id.tv_bd_date);
        TextView tvBdNote = findViewById(R.id.tv_bd_note);
        RelativeLayout rlBdAmount = findViewById(R.id.rl_bd_amount);
        RelativeLayout rlBdType = findViewById(R.id.rl_bd_type);
        RelativeLayout rlBdDate = findViewById(R.id.rl_bd_date);
        LinearLayout llBdNote = findViewById(R.id.ll_bd_note);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        btnBdDelete.setOnClickListener(clickListener);
        btnBdModify.setOnClickListener(clickListener);

        if (bill == null) {
            ivBdNoData.setVisibility(View.VISIBLE);
            svBdInfo.setVisibility(View.GONE);
        } else {
            ivBdNoData.setVisibility(View.GONE);
            svBdInfo.setVisibility(View.VISIBLE);

            if (bill.getIoType() == 0) {
                tvBdAmount.setText(String.format(getResources().getString(R.string.bill_expend_amount), bill.getAmount()));

                rlBdAmount.setBackgroundResource(R.color.colorAccent);
                rlBdType.setBackgroundResource(R.color.colorAccent);
                rlBdDate.setBackgroundResource(R.color.colorAccent);
                llBdNote.setBackgroundResource(R.color.colorAccent);
            } else {
                tvBdAmount.setText(String.format(getResources().getString(R.string.bill_income_amount), bill.getAmount()));

                rlBdAmount.setBackgroundResource(R.color.colorGreen);
                rlBdType.setBackgroundResource(R.color.colorGreen);
                rlBdDate.setBackgroundResource(R.color.colorGreen);
                llBdNote.setBackgroundResource(R.color.colorGreen);
            }
            tvBdTypeName.setText(bill.getTypeName());
            ivBdTypeIcon.setImageResource(GlobalVar.BILL_TYPE_ICON_ARRAY[bill.getType()]);
            tvBdDate.setText(String.format(getResources().getString(R.string.bill_date_value), bill.getYear(), bill.getMonth(), bill.getDay()));
            tvBdNote.setText(bill.getNote());
        }
    }

    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_bd_delet:
                    DialogUtil dialogUtil = new DialogUtil(BillDetailActivity.this, R.mipmap.icon_info_g, getResources().getString(R.string.dialog_is_delete_bill), new DialogListener() {
                        @Override
                        public void sure() {
                            MyDBOperater.getInstance(BillDetailActivity.this).deleteBillByID(bill.getId());
                            finish();
                        }

                        @Override
                        public void cancel() {
                            // DO NOTHING
                        }
                    });
                    dialogUtil.showDialog();
                    break;
                case R.id.btn_bd_modify:
                    Intent intent = new Intent(BillDetailActivity.this, RecordBillActivity.class);
                    intent.putExtra(BillHomeActivity.INTENT_BILL_INFO, bill);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
