package com.hugh.teatime.models.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.hugh.teatime.models.gasoline.GasolineBean;
import com.hugh.teatime.models.gasoline.GasolineDetailActivity;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;

public class EventDetailActivity extends BaseActivity {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvSource;
    private TextView tvTime;
    private TextView tvLocation;
    private MapView mvShowLocation;
    private LinearLayout llNote;
    private Button btnGasoline;

    private EventBean eventBean;
    private AMap aMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

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
        if (resultCode == RESULT_OK) {
            if (requestCode == GlobalVar.REQUEST_CODE_EDIT_EVENT) {
                eventBean = (EventBean) data.getSerializableExtra(GlobalVar.INTENT_EVENT_EDIT);
            } else if (requestCode == GlobalVar.REQUEST_CODE_EDIT_RECORD_IN_EVENT) {
                GasolineBean gasolineBean = (GasolineBean) data.getSerializableExtra(GlobalVar.INTENT_GASOLINE_RECORD);
                eventBean = MyDBOperater.getInstance(this).Gasoline2Event(gasolineBean);
            }
            refreshData();
        }
    }

    /**
     * 初始化控件
     */
    private void initView(Bundle savedInstanceState) {
        TitlebarView tbv = findViewById(R.id.tbv_event_detail);
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });

        tvTitle = findViewById(R.id.tv_ed_title);
        tvContent = findViewById(R.id.tv_ed_content);
        tvSource = findViewById(R.id.tv_ed_source);
        tvTime = findViewById(R.id.tv_ed_time);
        tvLocation = findViewById(R.id.tv_ed_location);
        mvShowLocation = findViewById(R.id.mv_show_location);
        mvShowLocation.onCreate(savedInstanceState);
        Button btnModify = findViewById(R.id.btn_ev_modify);
        Button btnDelete = findViewById(R.id.btn_ev_delete);
        llNote = findViewById(R.id.ll_note);
        btnGasoline = findViewById(R.id.btn_gasoline);
        btnGasoline.setOnClickListener(clickListener);
        btnModify.setOnClickListener(clickListener);
        btnDelete.setOnClickListener(clickListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        eventBean = (EventBean) getIntent().getSerializableExtra(GlobalVar.INTENT_EVENT);
        aMap = mvShowLocation.getMap();
        marker = aMap.addMarker(new MarkerOptions().position(new LatLng(eventBean.getLatitude(), eventBean.getLongitude())).draggable(false).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_selected)));
        UiSettings settings = aMap.getUiSettings();
        settings.setZoomControlsEnabled(false);

        refreshData();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (eventBean == null) {
            return;
        }
        if (StringUtil.isStrNull(eventBean.getContent())) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
        }
        switch (eventBean.getEventType()) {
            case EventBean.TYPE_NOTE:
                tvSource.setText(R.string.event_type_note);
                break;
            case EventBean.TYPE_GASOLINE:
                tvSource.setText(R.string.event_type_gasoline);
                break;
            default:
                break;
        }
        tvTitle.setText(eventBean.getTitle());
        tvContent.setText(eventBean.getContent());
        tvTime.setText(StringUtil.formatTimestamp1(eventBean.getDate()));
        tvLocation.setText(eventBean.getAddress());
        if (eventBean.getEventType() == EventBean.TYPE_NOTE) {
            llNote.setVisibility(View.VISIBLE);
            btnGasoline.setVisibility(View.GONE);
        } else {
            llNote.setVisibility(View.GONE);
            btnGasoline.setVisibility(View.VISIBLE);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(eventBean.getLatitude(), eventBean.getLongitude()), 18));
        marker.setPosition(new LatLng(eventBean.getLatitude(), eventBean.getLongitude()));
    }

    /**
     * 按钮点击事件监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_gasoline:
                    GasolineBean gasolineBean = MyDBOperater.getInstance(EventDetailActivity.this).getGasolineRecordById(eventBean.getGasolineId());
                    if (gasolineBean == null) {
                        ToastUtil.showError(EventDetailActivity.this, getResources().getString(R.string.toast_data_error), true);
                    } else {
                        Intent intent = new Intent(EventDetailActivity.this, GasolineDetailActivity.class);
                        intent.putExtra(GlobalVar.INTENT_GASOLINE_RECORD, gasolineBean);
                        intent.putExtra(GlobalVar.INTENT_IS_FROM_EVENT, true);
                        startActivityForResult(intent, GlobalVar.REQUEST_CODE_EDIT_RECORD_IN_EVENT);
                    }
                    break;
                case R.id.btn_ev_modify:
                    Intent intent = new Intent(EventDetailActivity.this, NewEventActivity.class);
                    intent.putExtra(GlobalVar.INTENT_EVENT, eventBean);
                    startActivityForResult(intent, GlobalVar.REQUEST_CODE_EDIT_EVENT);
                    break;
                case R.id.btn_ev_delete:
                    DialogUtil dialogNotice = new DialogUtil(EventDetailActivity.this, R.mipmap.icon_info_g, getResources().getString(R.string.dialog_is_delete_event), new DialogListener() {
                        @Override
                        public void sure() {
                            MyDBOperater.getInstance(EventDetailActivity.this).deleteEvent(eventBean.getId());
                            finish();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                    dialogNotice.showDialog();
                    break;
                default:
                    break;
            }
        }
    };
}
