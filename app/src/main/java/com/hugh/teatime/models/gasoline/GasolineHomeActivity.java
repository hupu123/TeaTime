package com.hugh.teatime.models.gasoline;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class GasolineHomeActivity extends BaseActivity {

    private GasolineListAdapter mAdapter;
    private ArrayList<GasolineBean> datas = new ArrayList<>();
    private int searchType = 0;
    private String searchValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasoline_home);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<GasolineBean> tempDatas = MyDBOperater.getInstance(this).getGasolineRecords(searchType, searchValue);
        datas.clear();
        datas.addAll(tempDatas);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        tbv.setRightBtnText(getResources().getString(R.string.add));
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                Intent intent = new Intent(GasolineHomeActivity.this, GasolineRecordEditActivity.class);
                startActivity(intent);
            }
        });
        StickyListHeadersListView slhlvGasoline = findViewById(R.id.slhlv_gasoline_list);
        mAdapter = new GasolineListAdapter(this, datas, new GasolineListAdapter.ClickListener() {
            @Override
            public void onClickItem(GasolineBean gasolineBean) {
                Intent intent = new Intent(GasolineHomeActivity.this, GasolineDetailActivity.class);
                intent.putExtra(GlobalVar.INTENT_GASOLINE_RECORD, gasolineBean);
                startActivity(intent);
            }

            @Override
            public void onClickCarNO(String carNO) {
                mAdapter.setFilterTag("-" + carNO);
                searchType = 1;
                searchValue = carNO;
                onResume();
            }

            @Override
            public void onClickModel(String model) {
                mAdapter.setFilterTag("-" + model);
                searchType = 2;
                searchValue = model;
                onResume();
            }

            @Override
            public void onClickInvoice(int invoice) {
                if (invoice == 0) {
                    mAdapter.setFilterTag("-已开票");
                } else {
                    mAdapter.setFilterTag("-未开票");
                }
                searchType = 3;
                searchValue = String.valueOf(invoice);
                onResume();
            }

            @Override
            public void onClickClean() {
                mAdapter.setFilterTag("");
                searchType = 0;
                searchValue = "";
                onResume();
            }
        });
        slhlvGasoline.setAdapter(mAdapter);
        slhlvGasoline.setDivider(null);
        slhlvGasoline.setOnStickyHeaderChangedListener(new StickyListHeadersListView.OnStickyHeaderChangedListener() {
            @Override
            public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
                LogUtil.logHugh("itemPosition=" + itemPosition + " headerId=" + headerId);
            }
        });
    }
}
