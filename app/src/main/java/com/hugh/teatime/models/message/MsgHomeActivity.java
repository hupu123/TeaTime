package com.hugh.teatime.models.message;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.adapter.MyFragmentAdapter;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;
import java.util.List;

public class MsgHomeActivity extends BaseActivity {

    private ViewPager vpMsgContent;
    private RadioButton rbMsgList;
    private RadioButton rbMsgBlackList;

    public static String INTENT_SMS_INFO = "intent_sms_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_home);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        RadioGroup rgTopSelectBar = findViewById(R.id.rg_top_select_bar);
        rbMsgList = findViewById(R.id.rb_msg_list);
        rbMsgBlackList = findViewById(R.id.rb_msg_black_list);
        vpMsgContent = findViewById(R.id.vp_msg_content);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        rgTopSelectBar.setOnCheckedChangeListener(checkedChangeListener);
        rbMsgList.setChecked(true);

        List<Fragment> fragments = new ArrayList<>();
        MsgListFragment msgListFragment = new MsgListFragment();//短信列表页
        MsgBlackListFragment msgBlackListFragment = new MsgBlackListFragment();//黑名单页
        fragments.add(msgListFragment);
        fragments.add(msgBlackListFragment);

        MyFragmentAdapter mfa = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        vpMsgContent.setAdapter(mfa);
        vpMsgContent.addOnPageChangeListener(pageChangeListener);
        vpMsgContent.setCurrentItem(0);
    }

    /**
     * RadioButton选择改变监听
     */
    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {
                case R.id.rb_msg_list:
                    vpMsgContent.setCurrentItem(0);
                    break;
                case R.id.rb_msg_black_list:
                    vpMsgContent.setCurrentItem(1);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ViewPager页签改变监听
     */
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            switch (position) {
                case 0:
                    rbMsgList.setChecked(true);
                    break;
                case 1:
                    rbMsgBlackList.setChecked(true);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
