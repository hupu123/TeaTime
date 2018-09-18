package com.hugh.teatime.models.bill;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.adapter.MyFragmentAdapter;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 账单列表页
 */
public class BillHomeActivity extends BaseActivity {

    private TitlebarView tbv;

    private int currentMonth;
    private int currentYear;
    private BillListFragment billListFragment;
    private ChartFragment chartFragment;
    private DialogUtil dialogUtil;
    private ArrayList<String> monthsOfYear;

    public static final String INTENT_BILL_INFO = "intent_bill_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_home);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateBillData();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        tbv = findViewById(R.id.tbv);
        ViewPager vpBillContent = findViewById(R.id.vp_bill_content);

        // 获取年月日
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        //        int day = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = month + 1;
        currentYear = year;

        // 设置ViewPager
        List<Fragment> fragments = new ArrayList<>();
        billListFragment = new BillListFragment();
        chartFragment = new ChartFragment();
        fragments.add(billListFragment);
        fragments.add(chartFragment);
        MyFragmentAdapter mfa = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        vpBillContent.setAdapter(mfa);
        vpBillContent.addOnPageChangeListener(pageChangeListener);

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
        monthsOfYear = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthsOfYear.add(String.format(getResources().getString(R.string.month_name), i));
        }
        dialogUtil = new DialogUtil(this, monthsOfYear, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentMonth = position + 1;
                updateBillData();
                dialogUtil.hideDialog();
            }
        });
    }

    /**
     * 更新账单信息
     */
    private void updateBillData() {

        if (billListFragment == null || chartFragment == null || monthsOfYear == null || monthsOfYear.size() < 12) {
            return;
        }
        tbv.setRightBtnText(monthsOfYear.get(currentMonth - 1));
        billListFragment.updateData(currentMonth, currentYear);
        chartFragment.updateData(currentMonth, currentYear);
    }

    /**
     * ViewPager page改变事件监听
     */
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            if (position == 0) {
                tbv.setTitleName(getResources().getString(R.string.bill_list));
            } else {
                tbv.setTitleName(getResources().getString(R.string.bill_chart));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
