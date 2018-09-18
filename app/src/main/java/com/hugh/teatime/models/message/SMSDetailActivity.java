package com.hugh.teatime.models.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.utils.ToolUtil;
import com.hugh.teatime.view.TitlebarView;

public class SMSDetailActivity extends BaseActivity {

    private SMS sms;
    private boolean clickFlag = true;

    private Button btnMdBl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsdetail);

        Intent intent = getIntent();
        sms = (SMS) intent.getSerializableExtra(MsgHomeActivity.INTENT_SMS_INFO);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        TextView tvMdContent = findViewById(R.id.tv_md_content);
        TextView tvMdDate = findViewById(R.id.tv_md_date);
        btnMdBl = findViewById(R.id.btn_md_bl);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        if (sms == null) {
            return;
        }
        tbv.setTitleName(sms.getNumber());
        tvMdContent.setText(sms.getBody());
        tvMdDate.setText(ToolUtil.getTimeFromTimestamp(sms.getDate(), 2));
        if (sms.getClassType() == 0) {
            btnMdBl.setText(R.string.add_to_blacklist);
        } else if (sms.getClassType() == 1) {
            btnMdBl.setText(R.string.remove_from_blacklist);
        } else {
            btnMdBl.setText(R.string.unknown_type);
            btnMdBl.setClickable(false);
        }
        btnMdBl.setOnClickListener(clickListener);
    }

    /**
     * 按钮点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_md_bl:
                    if (sms.getClassType() == 0) {
                        if (clickFlag) {
                            clickFlag = false;
                            SPUtil.getInstance(SMSDetailActivity.this).addSMSToBlackList(sms.getNumber());
                            btnMdBl.setText(R.string.remove_from_blacklist);
                        } else {
                            clickFlag = true;
                            SPUtil.getInstance(SMSDetailActivity.this).removeSMSFromBlackList(sms.getNumber());
                            btnMdBl.setText(R.string.add_to_blacklist);
                        }
                    } else if (sms.getClassType() == 1) {
                        if (clickFlag) {
                            clickFlag = false;
                            SPUtil.getInstance(SMSDetailActivity.this).removeSMSFromBlackList(sms.getNumber());
                            btnMdBl.setText(R.string.add_to_blacklist);
                        } else {
                            clickFlag = true;
                            SPUtil.getInstance(SMSDetailActivity.this).addSMSToBlackList(sms.getNumber());
                            btnMdBl.setText(R.string.remove_from_blacklist);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
