package com.hugh.teatime.models.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;

/**
 * 重设密码页
 */
public class ResetPswActivity extends BaseActivity {

    private EditText etOldPsw;// 原始密码输入
    private EditText etNewPsw;// 新密码输入
    private EditText etNewPswAgain;// 新密码确认
    private TextView tvTips;// 提示信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_psw);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        Button btnResetConfirm = findViewById(R.id.btn_reset_confirm);
        etOldPsw = findViewById(R.id.et_reset_old_psw);
        etNewPsw = findViewById(R.id.et_reset_new_psw);
        etNewPswAgain = findViewById(R.id.et_reset_new_psw_again);
        tvTips = findViewById(R.id.tv_reset_tips);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        btnResetConfirm.setOnClickListener(clickListener);
    }

    /**
     * 按钮点击事件
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_reset_confirm:
                    String oldPsw = etOldPsw.getText().toString();
                    String newPsw = etNewPsw.getText().toString();
                    String newPswAgain = etNewPswAgain.getText().toString();
                    String psw = SPUtil.getInstance(ResetPswActivity.this).getPsw();

                    if (StringUtil.isStrNull(oldPsw)) {
                        tvTips.setText(R.string.tips_origin_psw_can_not_be_null);
                        etOldPsw.requestFocus();
                        break;
                    }
                    if (StringUtil.isStrNull(newPsw)) {
                        tvTips.setText(R.string.tips_new_psw_can_not_be_null);
                        etNewPsw.requestFocus();
                        break;
                    }
                    if (StringUtil.isStrNull(newPswAgain)) {
                        tvTips.setText(R.string.tips_confirm_psw_can_not_be_null);
                        etNewPswAgain.requestFocus();
                        break;
                    }
                    if (oldPsw.equals(psw)) {
                        if (newPsw.equals(newPswAgain)) {
                            ToastUtil.showInfo(ResetPswActivity.this, R.string.toast_set_new_psw_success, true);
                            SPUtil.getInstance(ResetPswActivity.this).setPsw(newPsw);
                            finish();
                        } else {
                            tvTips.setText(R.string.tips_input_psw_different);
                            etNewPsw.setText("");
                            etNewPswAgain.setText("");
                            etNewPsw.requestFocus();
                        }
                    } else {
                        tvTips.setText(R.string.tips_origin_psw_wrong);
                        etOldPsw.setText("");
                        etOldPsw.requestFocus();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
