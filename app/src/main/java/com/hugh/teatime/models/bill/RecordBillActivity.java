package com.hugh.teatime.models.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.Calendar;
import java.util.Date;

/**
 * 记账页
 */
public class RecordBillActivity extends BaseActivity {

    private EditText etRbAmount;
    private EditText etRbNote;
    private Button btnRbDatePicker;

    private Bill billIntentGet;
    private int ioType = 0;
    private int type = 0;
    private int year;   // 年
    private int month;  // 月
    private int day;    // 日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_bill);

        Intent intent = getIntent();
        billIntentGet = (Bill) intent.getSerializableExtra(BillHomeActivity.INTENT_BILL_INFO);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        Spinner sRbIO = findViewById(R.id.s_rb_io);
        etRbAmount = findViewById(R.id.et_rb_amount);
        Spinner sRbType = findViewById(R.id.s_rb_type);
        btnRbDatePicker = findViewById(R.id.btn_rb_date_picker);
        etRbNote = findViewById(R.id.et_rb_note);
        Button btnRbSubmit = findViewById(R.id.btn_rb_submit);

        // 获取当前年月日
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        btnRbDatePicker.setOnClickListener(clickListener);
        btnRbDatePicker.setText(String.format(getResources().getString(R.string.bill_date_value), year, (month + 1), day));
        btnRbSubmit.setOnClickListener(clickListener);
        sRbIO.setAdapter(new IOPickAdapter(this));
        sRbIO.setOnItemSelectedListener(ioTypeItemSelectedListener);
        sRbType.setAdapter(new TypePickAdapter(this));
        sRbType.setOnItemSelectedListener(typeItemSelectedListener);

        if (billIntentGet != null) {
            ioType = billIntentGet.getIoType();
            type = billIntentGet.getType();
            year = billIntentGet.getYear();
            month = billIntentGet.getMonth() - 1;
            day = billIntentGet.getDay();

            sRbIO.setSelection(ioType);
            etRbAmount.setText(String.valueOf(billIntentGet.getAmount()));
            sRbType.setSelection(type);
            btnRbDatePicker.setText(String.format(getResources().getString(R.string.bill_date_value), year, (month + 1), day));
            etRbNote.setText(billIntentGet.getNote());
        }
    }

    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_rb_date_picker:
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    DatePickDialog dpd = new DatePickDialog(RecordBillActivity.this);
                    dpd.setType(DateType.TYPE_YMD);
                    dpd.setStartDate(calendar.getTime());
                    dpd.setTitle(getResources().getString(R.string.bill_record_select_date));
                    dpd.setOnSureLisener(onSureLisener);
                    dpd.show();
                    break;
                case R.id.btn_rb_submit:
                    if (StringUtil.isStrNull(etRbAmount.getText().toString())) {
                        ToastUtil.showInfo(RecordBillActivity.this, R.string.toast_amount_not_null, true);
                        etRbAmount.requestFocus();
                        break;
                    }
                    double amount = Double.parseDouble(etRbAmount.getText().toString());
                    String note;
                    if (StringUtil.isStrNull(etRbNote.getText().toString())) {
                        note = getResources().getString(R.string.bill_record_no_comment);
                    } else {
                        note = etRbNote.getText().toString();
                    }
                    String typeName = GlobalVar.BILL_TYPE_NAME_ARRAY[type];

                    Bill bill = new Bill(amount, note, ioType, type, typeName, year, month + 1, day);
                    if (billIntentGet == null) {
                        // 添加账单到数据库
                        MyDBOperater.getInstance(RecordBillActivity.this).addBill(bill);
                    } else {
                        // 退出修改前的账单详情页，重新启动新的
                        BillDetailActivity.exit();
                        bill.setId(billIntentGet.getId());
                        // 修改账单
                        MyDBOperater.getInstance(RecordBillActivity.this).updateBill(bill);
                        Intent intent = new Intent(RecordBillActivity.this, BillDetailActivity.class);
                        intent.putExtra(BillHomeActivity.INTENT_BILL_INFO, bill);
                        startActivity(intent);
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 日期设置事件监听
     */
    OnSureLisener onSureLisener = new OnSureLisener() {
        @Override
        public void onSure(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            btnRbDatePicker.setText(String.format(getResources().getString(R.string.bill_date_value), year, (month + 1), day));
        }
    };

    /**
     * 收支选着事件监听
     */
    AdapterView.OnItemSelectedListener ioTypeItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            ioType = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 账单类型选择事件监听
     */
    AdapterView.OnItemSelectedListener typeItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            type = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
