package com.hugh.teatime.models.gasoline;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.hugh.teatime.R;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class GasolineRecordEditActivity extends BaseActivity {

    private EditText etTotalAmount;         // 总金额输入框
    private EditText etTotalMileage;        // 总里程输入框
    private EditText etTotalQuantity;       // 总数量输入框
    private Spinner sGasolineType;          // 汽油型号选择
    private Spinner sPayType;               // 支付方式选择
    private EditText etCarNO;               // 车牌号输入框
    private Button btnRecordDate;           // 日期选择按钮
    private EditText etRecordComment;       // 备注信息输入框
    private CheckBox cbIsInvoice;           // 是否已开发票勾选框

    private ArrayList<String> gasolineTypes;
    private ArrayList<String> payTypes;
    private int gasolineTypePosition = 1;
    private int payTypePosition = 1;
    private Date recordDate = new Date();
    private boolean isModifyFlag = false;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasoline_record_edit);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        TitlebarView tbv = findViewById(R.id.tbv);
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
            }
        });

        etTotalAmount = findViewById(R.id.et_total_amount);
        etTotalMileage = findViewById(R.id.et_total_mileage);
        etTotalQuantity = findViewById(R.id.et_total_quantity);
        sGasolineType = findViewById(R.id.s_gasoline_type);
        sPayType = findViewById(R.id.s_pay_type);
        etCarNO = findViewById(R.id.et_car_no);
        btnRecordDate = findViewById(R.id.btn_record_date);
        etRecordComment = findViewById(R.id.et_record_comment);
        cbIsInvoice = findViewById(R.id.cb_is_invoice);
        Button btnConfirm = findViewById(R.id.btn_confirm);

        sGasolineType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gasolineTypePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sPayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payTypePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnRecordDate.setOnClickListener(clickListener);
        btnConfirm.setOnClickListener(clickListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        gasolineTypes = new ArrayList<>(Arrays.asList(GlobalVar.GASOLINE_TYPE_NAME_ARRAY));
        payTypes = new ArrayList<>(Arrays.asList(GlobalVar.PAY_TYPE_NAME_ARRAY));
        CommonAdapter<String> gasolineTypeAdapter = new CommonAdapter<String>(this, R.layout.item_type_s, gasolineTypes) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.tv_type_name, item);
            }
        };
        CommonAdapter<String> payTypeAdapter = new CommonAdapter<String>(this, R.layout.item_type_s, payTypes) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.tv_type_name, item);
            }
        };
        sGasolineType.setAdapter(gasolineTypeAdapter);
        sPayType.setAdapter(payTypeAdapter);

        Intent intent = getIntent();
        GasolineBean gasolineBean = (GasolineBean) intent.getSerializableExtra(GlobalVar.INTENT_GASOLINE_RECORD);
        String carNO = SPUtil.getInstance(this).getLastInputCarNO();
        // 如果为修改记录，则自动填写值
        if (gasolineBean != null) {
            isModifyFlag = true;
            etTotalAmount.setText(StringUtil.formatBigDecimalNum(gasolineBean.getTotalPrice()));
            etTotalMileage.setText(StringUtil.formatDoubleNum(gasolineBean.getMileage()));
            etTotalQuantity.setText(StringUtil.formatDoubleNum(gasolineBean.getQuantity()));
            etRecordComment.setText(gasolineBean.getComment());
            if (gasolineBean.getInvoice() == 0) {
                cbIsInvoice.setChecked(true);
            } else {
                cbIsInvoice.setChecked(false);
            }
            gasolineTypePosition = getPositionOfValue(gasolineBean.getModel(), gasolineTypes);
            payTypePosition = getPositionOfValue(gasolineBean.getPayMethod(), payTypes);
            recordDate = new Date(gasolineBean.getDate());
            carNO = gasolineBean.getCarNO();
            id = gasolineBean.getId();
        }
        sGasolineType.setSelection(gasolineTypePosition);
        sPayType.setSelection(payTypePosition);
        btnRecordDate.setText(StringUtil.formatTimestamp(recordDate.getTime()));
        etCarNO.setText(carNO);
    }

    /**
     * 通过列表值获取游标位置
     *
     * @param value 值
     * @param list  列表
     * @return 游标位置
     */
    private int getPositionOfValue(String value, ArrayList<String> list) {
        int position = 1;
        if (list == null) {
            return position;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(value)) {
                position = i;
            }
        }
        return position;
    }

    /**
     * 保存加油记录
     */
    private void saveRecord() {
        String totalAmountStr = etTotalAmount.getText().toString();
        String totalMileageStr = etTotalMileage.getText().toString();
        String totalQuantityStr = etTotalQuantity.getText().toString();
        String carNO = etCarNO.getText().toString();
        String recordComment = etRecordComment.getText().toString();

        if (StringUtil.isStrNull(totalAmountStr)) {
            ToastUtil.showInfo(this, R.string.toast_gasoline_amount_null, true);
            etTotalAmount.requestFocus();
            return;
        }
        if (StringUtil.isStrNull(totalMileageStr)) {
            ToastUtil.showInfo(this, R.string.toast_gasoline_mileage_null, true);
            etTotalMileage.requestFocus();
            return;
        }
        if (StringUtil.isStrNull(carNO)) {
            ToastUtil.showInfo(this, R.string.toast_gasoline_carno_null, true);
            etCarNO.requestFocus();
            return;
        }

        int invoice;
        if (cbIsInvoice.isChecked()) {
            invoice = 0;
        } else {
            invoice = 1;
        }

        GasolineBean gasolineBean = new GasolineBean(recordDate.getTime(), new BigDecimal(Double.parseDouble(totalAmountStr)), Double.parseDouble(totalMileageStr), gasolineTypes.get(gasolineTypePosition), invoice, payTypes.get(payTypePosition), carNO);
        if (!StringUtil.isStrNull(totalQuantityStr)) {
            gasolineBean.setQuantity(Double.parseDouble(totalQuantityStr));
        }
        if (!StringUtil.isStrNull(recordComment)) {
            gasolineBean.setComment(recordComment);
        }

        SPUtil.getInstance(this).setLastInputCarNO(carNO);
        if (isModifyFlag) {
            gasolineBean.setId(id);
            MyDBOperater.getInstance(this).updateGasolineRecord(gasolineBean);
            Intent intent = new Intent();
            intent.putExtra(GlobalVar.INTENT_GASOLINE_RECORD_EDIT, gasolineBean);
            setResult(RESULT_OK, intent);
        } else {
            MyDBOperater.getInstance(this).addGasolineRecord(gasolineBean);
        }
        ToastUtil.showSuccess(this, R.string.toast_gasoline_save_success, true);
        finish();
    }

    /**
     * 点击事件监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_record_date:
                    DatePickDialog dpd = new DatePickDialog(GasolineRecordEditActivity.this);
                    dpd.setType(DateType.TYPE_YMD);
                    dpd.setStartDate(recordDate);
                    dpd.setTitle(getResources().getString(R.string.bill_record_select_date));
                    dpd.setOnSureLisener(sureLisener);
                    dpd.show();
                    break;
                case R.id.btn_confirm:
                    saveRecord();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 日期选择框确认事件监听
     */
    private OnSureLisener sureLisener = new OnSureLisener() {
        @Override
        public void onSure(Date date) {
            recordDate = date;
            btnRecordDate.setText(StringUtil.formatTimestamp(date.getTime()));
        }
    };
}
