package com.hugh.teatime.models.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.utils.NotificationChannelUtil;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.view.TitlebarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录页（新）
 */
public class Login2Activity extends BaseActivity {

    private TextView tvSkbMsg;
    private EditText etSkbInput;
    private GridLayout glKeyboard;

    private StringBuilder sbInputPsw = new StringBuilder();
    private String initPsw;
    private boolean initPswState = true;// 初始密码设置状态，true=第一次输入，false=第二次输入
    private int wrongPswCount = 0;// 错误密码输入次数
    private Timer timer;
    private boolean isLoginSuccess = false;// 是否登陆成功，用于判断是未登录退出activity还是登陆成功退出activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        initView();
        NotificationChannelUtil.createNotificationChannels(this);

        if (SPUtil.getInstance(this).isFirstLogin()) {
            tvSkbMsg.setVisibility(View.VISIBLE);
            tvSkbMsg.setText(R.string.login_input_init_psw);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 处理是否重置允许输入错误密码次数
        long lastExitTime = SPUtil.getInstance(this).getExitTime();
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String lastExitDateStr = sdf.format(new Date(lastExitTime));
        String currentDateStr = sdf.format(new Date(currentTime));
        if (lastExitDateStr.equals(currentDateStr)) {
            wrongPswCount = SPUtil.getInstance(this).getWrongPswCount();
        } else {
            wrongPswCount = 0;
            SPUtil.getInstance(this).setWrongPswCount(wrongPswCount);
        }

        // 处理是否处于禁止登陆状态
        long disableLoginTime = SPUtil.getInstance(this).getDisableLoginTime();
        if (System.currentTimeMillis() - disableLoginTime >= GlobalVar.REINPUT_PSW_INTERVAL) {
            disableLoginTime = 0;
            SPUtil.getInstance(this).setDisableLoginTime(disableLoginTime);
        }
        if (disableLoginTime > 0) {
            glKeyboard.setVisibility(View.INVISIBLE);
            showRemainderTime(disableLoginTime);
        } else {
            glKeyboard.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (!isLoginSuccess) {
            SPUtil.getInstance(this).setExitTime(System.currentTimeMillis());
        }
    }

    /**
     * 显示禁止登陆后，重新输入密码登陆剩余时间
     *
     * @param disableLoginTime 禁止登陆时间戳
     */
    private void showRemainderTime(final long disableLoginTime) {
        tvSkbMsg.setVisibility(View.VISIBLE);
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long passTime = System.currentTimeMillis() - disableLoginTime;
                long remainderTime = GlobalVar.REINPUT_PSW_INTERVAL - passTime;
                if (remainderTime > 0) {
                    int mins = (int) (remainderTime / (60 * 1000));
                    int secs = (int) ((remainderTime - mins * 60 * 1000) / 1000);
                    tvSkbMsg.setText(String.format(getResources().getString(R.string.login_reinput_time), mins, secs));
                } else {
                    timer.cancel();
                    tvSkbMsg.setText(String.format(getResources().getString(R.string.input_psw_to_login), getResources().getString(R.string.app_name)));
                    SPUtil.getInstance(Login2Activity.this).setDisableLoginTime(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            glKeyboard.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        tvSkbMsg = findViewById(R.id.tv_skb_msg);
        etSkbInput = findViewById(R.id.et_skb_input);
        glKeyboard = findViewById(R.id.gl_keyboard);
        Button btnSkb1 = findViewById(R.id.btn_skb_1);
        Button btnSkb2 = findViewById(R.id.btn_skb_2);
        Button btnSkb3 = findViewById(R.id.btn_skb_3);
        Button btnSkb4 = findViewById(R.id.btn_skb_4);
        Button btnSkb5 = findViewById(R.id.btn_skb_5);
        Button btnSkb6 = findViewById(R.id.btn_skb_6);
        Button btnSkb7 = findViewById(R.id.btn_skb_7);
        Button btnSkb8 = findViewById(R.id.btn_skb_8);
        Button btnSkb9 = findViewById(R.id.btn_skb_9);
        Button btnSkb0 = findViewById(R.id.btn_skb_0);
        Button btnSkbBackspace = findViewById(R.id.btn_skb_backspace);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                Intent intent = new Intent(Login2Activity.this, ResetPswActivity.class);
                startActivity(intent);
            }
        });
        etSkbInput.addTextChangedListener(textWatcher);
        btnSkb1.setOnClickListener(clickListener);
        btnSkb2.setOnClickListener(clickListener);
        btnSkb3.setOnClickListener(clickListener);
        btnSkb4.setOnClickListener(clickListener);
        btnSkb5.setOnClickListener(clickListener);
        btnSkb6.setOnClickListener(clickListener);
        btnSkb7.setOnClickListener(clickListener);
        btnSkb8.setOnClickListener(clickListener);
        btnSkb9.setOnClickListener(clickListener);
        btnSkb0.setOnClickListener(clickListener);
        btnSkbBackspace.setOnClickListener(clickListener);
    }

    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_skb_1:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_1));
                    break;
                case R.id.btn_skb_2:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_2));
                    break;
                case R.id.btn_skb_3:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_3));
                    break;
                case R.id.btn_skb_4:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_4));
                    break;
                case R.id.btn_skb_5:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_5));
                    break;
                case R.id.btn_skb_6:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_6));
                    break;
                case R.id.btn_skb_7:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_7));
                    break;
                case R.id.btn_skb_8:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_8));
                    break;
                case R.id.btn_skb_9:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_9));
                    break;
                case R.id.btn_skb_0:
                    sbInputPsw.append(getResources().getString(R.string.num_pad_0));
                    break;
                case R.id.btn_skb_backspace:
                    if (sbInputPsw.length() > 0) {
                        sbInputPsw.deleteCharAt(sbInputPsw.length() - 1);
                    }
                    break;
                default:
                    break;
            }
            etSkbInput.setText(sbInputPsw);
        }
    };

    /**
     * 输入监听
     */
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void afterTextChanged(Editable s) {
            if (sbInputPsw.length() == 4) {
                if (SPUtil.getInstance(Login2Activity.this).isFirstLogin()) {
                    if (initPswState) {
                        initPsw = sbInputPsw.toString();
                        sbInputPsw = new StringBuilder();
                        etSkbInput.setText("");
                        tvSkbMsg.setText(R.string.login_input_psw_again);
                        initPswState = false;
                    } else {
                        if (initPsw.equals(sbInputPsw.toString())) {
                            sbInputPsw = new StringBuilder();
                            etSkbInput.setText("");
                            tvSkbMsg.setText(String.format(getResources().getString(R.string.input_psw_to_login), getResources().getString(R.string.app_name)));
                            SPUtil.getInstance(Login2Activity.this).setPsw(initPsw);
                            SPUtil.getInstance(Login2Activity.this).setIsFirst(false);
                        } else {
                            sbInputPsw = new StringBuilder();
                            etSkbInput.setText("");
                            tvSkbMsg.setText(R.string.login_input_psw_different);
                            initPsw = "";
                            initPswState = true;
                        }
                    }
                } else {
                    String savePsw = SPUtil.getInstance(Login2Activity.this).getPsw();
                    if (savePsw.equals(sbInputPsw.toString())) {
                        isLoginSuccess = true;
                        Intent intent = new Intent(Login2Activity.this, Home2Activity.class);
                        startActivity(intent);
                        wrongPswCount = 0;
                        SPUtil.getInstance(Login2Activity.this).setWrongPswCount(wrongPswCount);
                        finish();
                    } else {
                        wrongPswCount++;
                        tvSkbMsg.setVisibility(View.VISIBLE);
                        int remainder = GlobalVar.TOTAL_WRONG_PSW_INPUT_COUNT - wrongPswCount;
                        if (remainder == 0) {
                            wrongPswCount = 0;
                            glKeyboard.setVisibility(View.INVISIBLE);
                            sbInputPsw = new StringBuilder();
                            etSkbInput.setText("");
                            long disableLoginTime = System.currentTimeMillis();
                            SPUtil.getInstance(Login2Activity.this).setDisableLoginTime(disableLoginTime);
                            showRemainderTime(disableLoginTime);
                        } else {
                            tvSkbMsg.setText(String.format(getResources().getString(R.string.login_input_psw_wrong), remainder));
                            sbInputPsw = new StringBuilder();
                            etSkbInput.setText("");
                        }
                        SPUtil.getInstance(Login2Activity.this).setWrongPswCount(wrongPswCount);
                    }
                }
            }
        }
    };
}
