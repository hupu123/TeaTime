package com.hugh.teatime.models.robot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.listener.OnVolleyResponseListener;
import com.hugh.teatime.utils.InterfaceUtil;
import com.hugh.teatime.utils.JsonParseUtil;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.MSCUtil;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.utils.VolleyUtil;
import com.hugh.teatime.view.TitlebarView;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import java.util.ArrayList;
import java.util.List;

/**
 * 机器人页
 */
public class RobotActivity extends BaseActivity {

    private ListView lvChatRecord;
    private ImageButton ibInputTypeSwitch;
    private LinearLayout llTextInput;
    private Button btnVoiceInput;
    private EditText etTextInput;

    private boolean textVoiceSwitch = true; // 文字输入语音输入开关，true=文字输入，false=语音输入
    private List<Message> messages = new ArrayList<>();// 聊天消息（显示在界面上）
    private ChatListAdapter cla;
    private int currentPage = 1;// 当前页
    private final int pageSize = 20;// 每页条数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭数据库
        MyDBOperater.getInstance(this).close();
        // 偏移量置0
        GlobalVar.TEMP_OFF_SET = 0;
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        lvChatRecord = findViewById(R.id.lv_chat_record);
        ibInputTypeSwitch = findViewById(R.id.ib_input_type_switch);
        llTextInput = findViewById(R.id.ll_text_input);
        btnVoiceInput = findViewById(R.id.btn_voice_input);
        etTextInput = findViewById(R.id.et_text_input);
        ImageButton ibTextSend = findViewById(R.id.ib_text_send);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                messages.clear();
                cla.notifyDataSetChanged();
                GlobalVar.TEMP_OFF_SET = 0;
                currentPage = 1;
            }
        });
        cla = new ChatListAdapter(this, messages);
        lvChatRecord.setAdapter(cla);
        lvChatRecord.setOnItemClickListener(itemClickListener);
        lvChatRecord.setOnScrollListener(scrollListener);
        ibInputTypeSwitch.setOnClickListener(clickListener);
        ibTextSend.setOnClickListener(clickListener);
        btnVoiceInput.setOnTouchListener(touchListener);
    }

    /**
     * 触控时间监听
     */
    View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    MSCUtil.getInstance(RobotActivity.this).startListen(mRecoListener);
                    break;
                case MotionEvent.ACTION_UP:
                    MSCUtil.getInstance(RobotActivity.this).stopListen();
                    break;
                default:
                    LogUtil.logIResult("action:" + action);
                    break;
            }

            return false;
        }
    };

    /**
     * 点击事件
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            int id = v.getId();
            switch (id) {
                case R.id.ib_input_type_switch:
                    if (textVoiceSwitch) {
                        ibInputTypeSwitch.setImageResource(R.mipmap.icon_text);
                        llTextInput.setVisibility(View.GONE);
                        btnVoiceInput.setVisibility(View.VISIBLE);
                        textVoiceSwitch = false;
                    } else {
                        ibInputTypeSwitch.setImageResource(R.mipmap.icon_sound);
                        llTextInput.setVisibility(View.VISIBLE);
                        btnVoiceInput.setVisibility(View.GONE);
                        textVoiceSwitch = true;
                    }
                    break;
                case R.id.ib_text_send:
                    textSendMethod();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * ListView item点击事件
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int code = messages.get(position).getCode();
            if (code == GlobalVar.MSG_TYPE_URL) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(messages.get(position).getUrl())));
            }
        }
    };

    /**
     * ListView 滑动监听
     */
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            switch (scrollState) {
                // 当不滚动时
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    // 判断滚动到底部
                    //                    if (lvChatRecord.getLastVisiblePosition() == (lvChatRecord.getCount() - 1)) {
                    //
                    //                    }

                    // 判断滚动到顶部
                    if (lvChatRecord.getFirstVisiblePosition() == 0) {

                        List<Message> tempMessages = MyDBOperater.getInstance(RobotActivity.this).getMessages(currentPage, pageSize);
                        messages.addAll(0, tempMessages);
                        cla.notifyDataSetChanged();
                        if (tempMessages.size() == 0) {
                            ToastUtil.showInfo(RobotActivity.this, R.string.toast_no_more_data, true);
                            scrollLVToPosition(0);
                        } else {
                            currentPage++;
                            scrollLVToPosition(tempMessages.size() - 1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    /**
     * 语音听写监听
     */
    RecognizerListener mRecoListener = new RecognizerListener() {

        StringBuilder sb = new StringBuilder();

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

            LogUtil.logIResult("onEndOfSpeech:" + sb.toString());

            if (sb == null || StringUtil.isStrNull(sb.toString())) {
                return;
            }

            Message msg = new Message(1, sb.toString(), System.currentTimeMillis());
            MyDBOperater.getInstance(RobotActivity.this).addMessage(msg);
            messages.add(msg);
            cla.notifyDataSetChanged();
            scrollLVToPosition(cla.getCount() - 1);
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {

            sb.append(recognizerResult.getResultString());
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 发送按钮点击事件
     */
    private void textSendMethod() {

        String inputStr = etTextInput.getText().toString();
        if (StringUtil.isStrNull(inputStr)) {
            ToastUtil.showInfo(this, R.string.toast_input_content, true);
        } else {
            // 记录消息
            Message msg = new Message(1, inputStr, System.currentTimeMillis());
            MyDBOperater.getInstance(this).addMessage(msg);
            messages.add(msg);
            cla.notifyDataSetChanged();
            scrollLVToPosition(cla.getCount() - 1);

            List<KeyValuePair> params = new ArrayList<>();
            params.add(new KeyValuePair("key", GlobalVar.TURING_API_KEY));
            params.add(new KeyValuePair("info", inputStr));
            params.add(new KeyValuePair("userid", GlobalVar.USER_ID));
            VolleyUtil.getInstance(this).requestByPost(InterfaceUtil.TURING_URL, params, new OnVolleyResponseListener() {

                @Override
                public void onVolleyResponse(String result) {

                    Message message = JsonParseUtil.parseMessage(result);
                    MyDBOperater.getInstance(RobotActivity.this).addMessage(message);
                    messages.add(message);
                    cla.notifyDataSetChanged();
                    scrollLVToPosition(cla.getCount() - 1);
                    // 播放语音
                    if (message != null) {
                        MSCUtil.getInstance(RobotActivity.this).speak(message.getMsg());
                    }
                }

                @Override
                public void onVolleyResponseError(String msg) {

                }
            });
        }

        etTextInput.setText("");
        etTextInput.requestFocus();
    }

    /**
     * 滚动ListView到指定位置
     *
     * @param position 要滚动到的位置
     */
    private void scrollLVToPosition(final int position) {

        if (position >= cla.getCount() || position < 0) {
            return;
        }

        lvChatRecord.post(new Runnable() {
            @Override
            public void run() {
                lvChatRecord.setSelection(position);
            }
        });
    }
}
