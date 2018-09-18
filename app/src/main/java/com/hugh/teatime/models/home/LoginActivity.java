package com.hugh.teatime.models.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.view.TitlebarView;

/**
 * 登录页（旧）
 */
public class LoginActivity extends BaseActivity {

    private TextView tvTips;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;

    private int currentInputPosition = 1;
    private StringBuilder pswSB = new StringBuilder();
    private String initPsw;
    private int initPswState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        if (SPUtil.getInstance(this).isFirstLogin()) {
            tvTips.setText(R.string.login_input_init_psw);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        tvTips = findViewById(R.id.tv_login_tips);
        et1 = findViewById(R.id.et_psw_1);
        et2 = findViewById(R.id.et_psw_2);
        et3 = findViewById(R.id.et_psw_3);
        et4 = findViewById(R.id.et_psw_4);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                Intent intent = new Intent(LoginActivity.this, ResetPswActivity.class);
                startActivity(intent);
            }
        });
        et1.setEnabled(true);
        et1.requestFocus();
        currentInputPosition = 1;
        et1.addTextChangedListener(textWatcher);
        et2.addTextChangedListener(textWatcher);
        et3.addTextChangedListener(textWatcher);
        et4.addTextChangedListener(textWatcher);
    }

    /**
     * 弹出软键盘
     */
    private void showSoftInput() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
    }

    /**
     * EditText输入监听
     */
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String inputStr = s.toString();
            if (StringUtil.isStrNull(inputStr)) {
                return;
            }
            pswSB.append(inputStr);
            switch (currentInputPosition) {
                case 1:
                    et1.setEnabled(false);
                    et2.setEnabled(true);
                    et2.requestFocus();
                    currentInputPosition = 2;
                    showSoftInput();
                    break;
                case 2:
                    et2.setEnabled(false);
                    et3.setEnabled(true);
                    et3.requestFocus();
                    currentInputPosition = 3;
                    showSoftInput();
                    break;
                case 3:
                    et3.setEnabled(false);
                    et4.setEnabled(true);
                    et4.requestFocus();
                    currentInputPosition = 4;
                    showSoftInput();
                    break;
                case 4:
                    String inputPsw = pswSB.toString();
                    if (SPUtil.getInstance(LoginActivity.this).isFirstLogin()) {
                        if (initPswState == 0) {
                            initPsw = inputPsw;
                            initPswState = 1;
                            tvTips.setText(R.string.login_input_psw_again);
                        } else if (initPswState == 1) {
                            if (initPsw.equals(inputPsw)) {
                                tvTips.setText(R.string.login_input_psw_success);
                                SPUtil.getInstance(LoginActivity.this).setPsw(initPsw);
                                SPUtil.getInstance(LoginActivity.this).setIsFirst(false);
                            } else {
                                tvTips.setText(R.string.login_input_psw_different);
                                initPswState = 0;
                                initPsw = null;
                            }
                        }
                        et1.setText("");
                        et2.setText("");
                        et3.setText("");
                        et4.setText("");

                        et4.setEnabled(false);
                        et1.setEnabled(true);
                        et1.requestFocus();
                        currentInputPosition = 1;

                        pswSB = new StringBuilder();
                        showSoftInput();
                    } else {
                        String psw = SPUtil.getInstance(LoginActivity.this).getPsw();
                        if (psw.equals(inputPsw)) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            et1.setText("");
                            et2.setText("");
                            et3.setText("");
                            et4.setText("");

                            et4.setEnabled(false);
                            et1.setEnabled(true);
                            et1.requestFocus();
                            currentInputPosition = 1;

                            pswSB = new StringBuilder();
                            tvTips.setText(R.string.login_input_psw_wrong);
                            showSoftInput();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
