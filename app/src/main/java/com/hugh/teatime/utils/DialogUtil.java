package com.hugh.teatime.utils;


import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.listener.DialogListener;
import com.hugh.teatime.listener.TargetDialogListener;
import com.hugh.teatime.models.target.TargetBean;

import java.util.ArrayList;

public class DialogUtil {

    private Dialog dialog;

    /**
     * 创建等待对话框
     *
     * @param context 上下文
     */
    public DialogUtil(Context context) {
        dialog = new Dialog(context, R.style.bottom_dialog_style);
        dialog.setCanceledOnTouchOutside(false);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_wait_view, null);
        dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
        }
    }

    /**
     * 创建底部选择列表对话框
     *
     * @param context  上下文
     * @param items    选项
     * @param listener 选项点击监听
     */
    public DialogUtil(Context context, ArrayList<String> items, AdapterView.OnItemClickListener listener) {
        dialog = new Dialog(context, R.style.bottom_dialog_style);
        dialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_bottom, null);
        dialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.bottom_dialog_anim_style);
        }

        ListView lvItems = contentView.findViewById(R.id.lv_item);
        lvItems.setAdapter(new CommonAdapter<String>(context, R.layout.item_bottom_dialog, items) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.tv_item_name, item);
            }
        });
        lvItems.setOnItemClickListener(listener);
    }

    /**
     * 创建底部提示对话框
     *
     * @param context  上下文
     * @param srcId    图标ID
     * @param title    标题内容
     * @param listener 选择监听
     */
    public DialogUtil(Context context, int srcId, String title, final DialogListener listener) {
        dialog = new Dialog(context, R.style.bottom_dialog_style);
        dialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_view, null);
        dialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.bottom_dialog_anim_style);
        }

        ImageView ivIcon = contentView.findViewById(R.id.iv_cd_icon);
        TextView tvMsg = contentView.findViewById(R.id.tv_cd_msg);
        Button btnConfirm = contentView.findViewById(R.id.btn_cd_confirm);
        Button btnCancel = contentView.findViewById(R.id.btn_cd_cancel);

        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int id = v.getId();
                switch (id) {
                    case R.id.btn_cd_confirm:
                        listener.sure();
                        break;
                    case R.id.btn_cd_cancel:
                        listener.cancel();
                        break;

                    default:
                        break;
                }
                hideDialog();
            }
        };

        ivIcon.setImageResource(srcId);
        tvMsg.setText(title);
        btnConfirm.setOnClickListener(clickListener);
        btnCancel.setOnClickListener(clickListener);
    }

    /**
     * 创建目标修改底部弹框
     *
     * @param context    上下文
     * @param targetBean 目标数据
     * @param listener   按钮监听
     */
    public DialogUtil(final Context context, final TargetBean targetBean, final TargetDialogListener listener) {
        final TargetBean targetTemp = new TargetBean(targetBean);
        dialog = new Dialog(context, R.style.bottom_dialog_style);
        dialog.setCanceledOnTouchOutside(false);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_target_view, null);
        dialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.bottom_dialog_anim_style);
        }

        final TextView tvDone = contentView.findViewById(R.id.tv_done);
        final TextView tvTarget = contentView.findViewById(R.id.tv_target);
        final View vDivider = contentView.findViewById(R.id.v_divider);
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        SeekBar sbProgress = contentView.findViewById(R.id.sb_progress);
        Button btnConfirm = contentView.findViewById(R.id.btn_confirm);
        Button btnDelete = contentView.findViewById(R.id.btn_delete);
        Button btnCancel = contentView.findViewById(R.id.btn_cancel);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                targetTemp.setDoneNum(i);
                tvDone.setText(String.valueOf(i));
                if (i == targetTemp.getTargetNum()) {
                    tvDone.setTextColor(ContextCompat.getColor(context, R.color.colorChart_t3));
                    tvTarget.setTextColor(ContextCompat.getColor(context, R.color.colorChart_t3));
                    vDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorChart_t3));
                } else if (i == 0) {
                    tvDone.setTextColor(ContextCompat.getColor(context, R.color.colorChart_t5));
                    tvTarget.setTextColor(ContextCompat.getColor(context, R.color.colorChart_t5));
                    vDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorChart_t5));
                } else {
                    tvDone.setTextColor(ContextCompat.getColor(context, R.color.colorTWhite));
                    tvTarget.setTextColor(ContextCompat.getColor(context, R.color.colorTWhite));
                    vDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTWhite));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int id = v.getId();
                switch (id) {
                    case R.id.btn_confirm:
                        listener.sure(targetTemp);
                        break;
                    case R.id.btn_delete:
                        listener.delete();
                        break;
                    case R.id.btn_cancel:
                        listener.cancel();
                        break;
                    default:
                        break;
                }
                hideDialog();
            }
        };

        if (targetTemp.getDoneNum() == targetTemp.getTargetNum()) {
            tvDone.setTextColor(ContextCompat.getColor(context, R.color.colorChart_t3));
            tvTarget.setTextColor(ContextCompat.getColor(context, R.color.colorChart_t3));
            vDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorChart_t3));
        } else if (targetTemp.getDoneNum() == 0) {
            tvDone.setTextColor(ContextCompat.getColor(context, R.color.colorChart_t5));
            tvTarget.setTextColor(ContextCompat.getColor(context, R.color.colorChart_t5));
            vDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorChart_t5));
        } else {
            tvDone.setTextColor(ContextCompat.getColor(context, R.color.colorTWhite));
            tvTarget.setTextColor(ContextCompat.getColor(context, R.color.colorTWhite));
            vDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTWhite));
        }
        tvDone.setText(String.valueOf(targetTemp.getDoneNum()));
        tvTarget.setText(String.valueOf(targetTemp.getTargetNum()));
        tvTitle.setText(targetTemp.getTitle());
        sbProgress.setMax(targetTemp.getTargetNum());
        sbProgress.setProgress(targetTemp.getDoneNum());
        sbProgress.setOnSeekBarChangeListener(seekBarChangeListener);
        btnConfirm.setOnClickListener(clickListener);
        btnDelete.setOnClickListener(clickListener);
        btnCancel.setOnClickListener(clickListener);
    }

    /**
     * 打开对话框
     */
    public void showDialog() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    public void hideDialog() {
        if (dialog != null) {
            dialog.hide();
        }
    }
}
