package com.hugh.teatime.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugh.teatime.R;

public class TitlebarView extends RelativeLayout {

    private static final int LB_DEFAULT = 0;
    private static final int LB_IB_EXIT = 1;
    private static final int LB_HIDDEN = 2;
    private static final int RB_DEFAULT = 0;
    private static final int RB_IB_ADD = 1;
    private static final int RB_BTN_RIGHT = 2;
    private static final int RB_IB_REFRESH = 3;
    private static final int RB_IB_CLEAN = 4;
    private static final int RB_IB_RESET_PSW = 5;

    private TitlebarListener listener;
    private TitleClickListener titleClickListener;
    private TextView tvTitle;
    private Button btnRight;

    public TitlebarView(Context context) {
        super(context);
    }

    public TitlebarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_titlebar, this);

        tvTitle = view.findViewById(R.id.tv_title);
        RelativeLayout rlLeftBar = view.findViewById(R.id.rl_left_bar);
        ImageButton ibLeftImageBtn = view.findViewById(R.id.ib_left_image_btn);
        Button btnBack = view.findViewById(R.id.btn_back);
        RelativeLayout rlRightBar = view.findViewById(R.id.rl_right_bar);
        ImageButton ibRightImageBtn = view.findViewById(R.id.ib_right_image_btn);
        btnRight = view.findViewById(R.id.btn_right);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitlebarView);
        String titleName = ta.getString(R.styleable.TitlebarView_titleName);
        int leftBarType = ta.getInt(R.styleable.TitlebarView_leftBarType, LB_DEFAULT);
        int rightBarType = ta.getInt(R.styleable.TitlebarView_rightBarType, RB_DEFAULT);
        ta.recycle();

        switch (leftBarType) {
            case LB_DEFAULT:
                rlLeftBar.setVisibility(VISIBLE);
                ibLeftImageBtn.setVisibility(GONE);
                btnBack.setVisibility(VISIBLE);
                btnBack.setOnClickListener(clickListener);
                break;
            case LB_IB_EXIT:
                rlLeftBar.setVisibility(VISIBLE);
                ibLeftImageBtn.setVisibility(VISIBLE);
                btnBack.setVisibility(GONE);
                ibLeftImageBtn.setImageResource(R.mipmap.icon_exit);
                ibLeftImageBtn.setOnClickListener(clickListener);
                break;
            case LB_HIDDEN:
                rlLeftBar.setVisibility(GONE);
                break;
            default:
                break;
        }
        switch (rightBarType) {
            case RB_DEFAULT:
                rlRightBar.setVisibility(GONE);
                break;
            case RB_IB_ADD:
                rlRightBar.setVisibility(VISIBLE);
                ibRightImageBtn.setVisibility(VISIBLE);
                btnRight.setVisibility(GONE);
                ibRightImageBtn.setImageResource(R.mipmap.icon_add);
                ibRightImageBtn.setOnClickListener(clickListener);
                break;
            case RB_IB_REFRESH:
                rlRightBar.setVisibility(VISIBLE);
                ibRightImageBtn.setVisibility(VISIBLE);
                btnRight.setVisibility(GONE);
                ibRightImageBtn.setImageResource(R.mipmap.icon_refresh);
                ibRightImageBtn.setOnClickListener(clickListener);
                break;
            case RB_IB_CLEAN:
                rlRightBar.setVisibility(VISIBLE);
                ibRightImageBtn.setVisibility(VISIBLE);
                btnRight.setVisibility(GONE);
                ibRightImageBtn.setImageResource(R.mipmap.icon_clear);
                ibRightImageBtn.setOnClickListener(clickListener);
                break;
            case RB_IB_RESET_PSW:
                rlRightBar.setVisibility(VISIBLE);
                ibRightImageBtn.setVisibility(VISIBLE);
                btnRight.setVisibility(GONE);
                ibRightImageBtn.setImageResource(R.mipmap.icon_psw);
                ibRightImageBtn.setOnClickListener(clickListener);
                break;
            case RB_BTN_RIGHT:
                rlRightBar.setVisibility(VISIBLE);
                ibRightImageBtn.setVisibility(GONE);
                btnRight.setVisibility(VISIBLE);
                btnRight.setOnClickListener(clickListener);
                break;
            default:
                rlRightBar.setVisibility(GONE);
                break;
        }
        tvTitle.setText(titleName);
        tvTitle.setOnClickListener(clickListener);
    }

    /**
     * 点击事件监听
     */
    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_title:
                    if (titleClickListener != null) {
                        titleClickListener.onTitleClick();
                    }
                    break;
                case R.id.btn_back:
                case R.id.ib_left_image_btn:
                    if (listener != null) {
                        listener.onLeftBtnClick();
                    }
                    break;
                case R.id.btn_right:
                case R.id.ib_right_image_btn:
                    if (listener != null) {
                        listener.onRightBtnClick();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置标题栏右侧按钮文字内容
     *
     * @param str 文字内容
     */
    public void setRightBtnText(String str) {
        if (btnRight != null) {
            btnRight.setText(str);
        }
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    public void setListener(TitlebarListener listener) {
        this.listener = listener;
    }

    /**
     * 设置标题点击监听器
     *
     * @param titleClickListener 标题点击事件监听器
     */
    public void setOnTitleClickListener(TitleClickListener titleClickListener) {
        this.titleClickListener = titleClickListener;
    }

    /**
     * 设置标题名称
     *
     * @param titleName 标题
     */
    public void setTitleName(String titleName) {
        tvTitle.setText(titleName);
    }

    /**
     * 标题栏左右按钮监听器
     */
    public interface TitlebarListener {

        void onLeftBtnClick();

        void onRightBtnClick();
    }

    /**
     * 标题点击事件监听器
     */
    public interface TitleClickListener {
        void onTitleClick();
    }
}
