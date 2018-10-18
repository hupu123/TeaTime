package com.hugh.teatime.models.gasoline;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.listener.DialogListener;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.view.TitlebarView;

import java.math.BigDecimal;

public class GasolineDetailActivity extends BaseActivity {

    private TextView tvAmount;
    private TextView tvFuelConsumption;
    private TextView tvMileage;
    private TextView tvPrice;
    private TextView tvQuantity;
    private TextView tvGasolineType;
    private TextView tvPayType;
    private TextView tvDate;
    private TextView tvCarNO;
    private TextView tvIsInvoice;
    private TextView tvComment;
    private TextView tvLocation;
    private MapView mvShowLocation;

    private GasolineBean gasolineBean;
    private AMap aMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasoline_detail);

        initView(savedInstanceState);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mvShowLocation.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mvShowLocation.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvShowLocation.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mvShowLocation.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GlobalVar.REQUEST_CODE_EDIT_RECORD) {
            if (resultCode == RESULT_OK) {
                gasolineBean = (GasolineBean) data.getSerializableExtra(GlobalVar.INTENT_GASOLINE_RECORD_EDIT);
                refreshData();
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView(Bundle savedInstanceState) {
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
        tvAmount = findViewById(R.id.tv_amount);
        tvFuelConsumption = findViewById(R.id.tv_fuel_consumption);
        tvMileage = findViewById(R.id.tv_mileage);
        tvPrice = findViewById(R.id.tv_gasoline_price);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvGasolineType = findViewById(R.id.tv_gasoline_type);
        tvPayType = findViewById(R.id.tv_pay_type);
        tvDate = findViewById(R.id.tv_date);
        tvCarNO = findViewById(R.id.tv_car_no);
        tvIsInvoice = findViewById(R.id.tv_is_invoice);
        tvComment = findViewById(R.id.tv_comment);
        tvLocation = findViewById(R.id.tv_gd_location);
        mvShowLocation = findViewById(R.id.mv_show_location);
        mvShowLocation.onCreate(savedInstanceState);
        Button btnOpenChart = findViewById(R.id.btn_open_chart);
        Button btnModity = findViewById(R.id.btn_modify);
        Button btnDelete = findViewById(R.id.btn_delete);
        btnOpenChart.setOnClickListener(clickListener);
        btnModity.setOnClickListener(clickListener);
        btnDelete.setOnClickListener(clickListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        gasolineBean = (GasolineBean) intent.getSerializableExtra(GlobalVar.INTENT_GASOLINE_RECORD);

        aMap = mvShowLocation.getMap();
        marker = aMap.addMarker(new MarkerOptions().position(new LatLng(gasolineBean.getLatitude(), gasolineBean.getLongitude())).draggable(false).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_selected)));
        UiSettings settings = aMap.getUiSettings();
        settings.setZoomControlsEnabled(false);

        refreshData();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (gasolineBean != null) {
            BigDecimal fuelConsumption = gasolineBean.getTotalPrice().divide(new BigDecimal(gasolineBean.getMileage()), 2, BigDecimal.ROUND_HALF_UP);
            tvAmount.setText(String.format(getResources().getString(R.string.gd_total_amount), StringUtil.formatBigDecimalNum(gasolineBean.getTotalPrice())));
            tvMileage.setText(String.format(getResources().getString(R.string.gd_mileage), StringUtil.formatDoubleNum(gasolineBean.getMileage())));
            tvFuelConsumption.setText(String.format(getResources().getString(R.string.gd_fuel_consumption), StringUtil.formatBigDecimalNum(fuelConsumption)));
            tvPrice.setText(String.format(getResources().getString(R.string.gd_price), StringUtil.formatBigDecimalNum(gasolineBean.getUnitPrice())));
            tvQuantity.setText(String.format(getResources().getString(R.string.gd_quantity), StringUtil.formatDoubleNum(gasolineBean.getQuantity())));
            tvGasolineType.setText(String.format(getResources().getString(R.string.gd_gasoline_type), gasolineBean.getModel()));
            tvPayType.setText(String.format(getResources().getString(R.string.gd_pay_type), gasolineBean.getPayMethod()));
            tvDate.setText(String.format(getResources().getString(R.string.gd_date), StringUtil.formatTimestamp(gasolineBean.getDate())));
            tvCarNO.setText(String.format(getResources().getString(R.string.gd_car_no), gasolineBean.getCarNO()));
            if (gasolineBean.getInvoice() == 0) {
                tvIsInvoice.setText(R.string.gd_have_invoiced);
            } else {
                tvIsInvoice.setText(R.string.gd_havent_invoiced);
            }
            tvComment.setText(gasolineBean.getComment());
            tvLocation.setText(String.format(getResources().getString(R.string.gd_location), gasolineBean.getAddress()));

            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gasolineBean.getLatitude(), gasolineBean.getLongitude()), 18));
            marker.setPosition(new LatLng(gasolineBean.getLatitude(), gasolineBean.getLongitude()));
        }
    }

    /**
     * 按钮点击事件监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_open_chart:
                    Intent intent = new Intent(GasolineDetailActivity.this, GasolineChartActivity.class);
                    intent.putExtra(GlobalVar.INTENT_GASOLINE_RECORD, gasolineBean);
                    startActivity(intent);
                    break;
                case R.id.btn_modify:
                    Intent intent1 = new Intent(GasolineDetailActivity.this, GasolineRecordEditActivity.class);
                    intent1.putExtra(GlobalVar.INTENT_GASOLINE_RECORD, gasolineBean);
                    startActivityForResult(intent1, GlobalVar.REQUEST_CODE_EDIT_RECORD);
                    break;
                case R.id.btn_delete:
                    DialogUtil deleteDialog = new DialogUtil(GasolineDetailActivity.this, R.mipmap.icon_info_g, getResources().getString(R.string.dialog_is_delete_record), new DialogListener() {
                        @Override
                        public void sure() {
                            MyDBOperater.getInstance(GasolineDetailActivity.this).deleteGasolineRecord(gasolineBean.getId());
                            finish();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                    deleteDialog.showDialog();
                    break;
                default:
                    break;
            }
        }
    };
}
