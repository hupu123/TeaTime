package com.hugh.teatime.models.target;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;

import com.hugh.teatime.R;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.listener.DialogListener;
import com.hugh.teatime.listener.TargetDialogListener;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TargetHomeActivity extends BaseActivity {

    private TargetListAdapter mAdapter;
    private ArrayList<TargetBean> targets = new ArrayList<>();
    private long choosedTime = System.currentTimeMillis();

    private TitlebarView tbv;
    private StickyListHeadersListView slhlvTargets;
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_home);

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        targets.clear();
        ArrayList<TargetBean> targetBeansTemp = MyDBOperater.getInstance(this).getTargetsByDate(choosedTime);
        LogUtil.logHugh("onResume temp size=" + targetBeansTemp.size());
        targets.addAll(targetBeansTemp);
        mAdapter.notifyDataSetChanged();
        tbv.setTitleName(StringUtil.formatTimestamp(choosedTime));
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tbv = findViewById(R.id.tbv);
        tbv.setRightBtnText("选项");
        tbv.setOnTitleClickListener(new TitlebarView.TitleClickListener() {
            @Override
            public void onTitleClick() {
                Date date = new Date(choosedTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TargetHomeActivity.this, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        LogUtil.logHugh("onDateSet i=" + i + " i1=" + i1 + " i2=" + i2);
                        Calendar calendarTemp = Calendar.getInstance();
                        calendarTemp.set(i, i1, i2);
                        choosedTime = calendarTemp.getTimeInMillis();
                        onResume();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                dialogUtil.showDialog();
            }
        });

        slhlvTargets = findViewById(R.id.slhlv_targets_list);
        slhlvTargets.setDivider(null);
        slhlvTargets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                DialogUtil targetDialog = new DialogUtil(TargetHomeActivity.this, targets.get(i), new TargetDialogListener() {
                    @Override
                    public void sure(TargetBean targetBean) {
                        MyDBOperater.getInstance(TargetHomeActivity.this).updateTarget(targetBean);
                        onResume();
                    }

                    @Override
                    public void delete() {
                        String msg;
                        if (targets.get(i).getType() == 0) {
                            msg = getResources().getString(R.string.dialog_is_delete_daily_target);
                        } else {
                            msg = getResources().getString(R.string.dialog_is_delete_target);
                        }
                        DialogUtil deleteConfirmDialog = new DialogUtil(TargetHomeActivity.this, R.mipmap.icon_info_g, msg, new DialogListener() {
                            @Override
                            public void sure() {
                                MyDBOperater.getInstance(TargetHomeActivity.this).deleteTarget(targets.get(i));
                                onResume();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        deleteConfirmDialog.showDialog();
                    }

                    @Override
                    public void cancel() {

                    }
                });
                targetDialog.showDialog();
            }
        });

        ArrayList<String> items = new ArrayList<>();
        items.add("新建");
        items.add("统计");
        dialogUtil = new DialogUtil(this, items, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case 0:
                        intent = new Intent(TargetHomeActivity.this, NewTargetActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(TargetHomeActivity.this, TargetOverviewActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                dialogUtil.hideDialog();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mAdapter = new TargetListAdapter(this, targets);
        slhlvTargets.setAdapter(mAdapter);
    }
}
