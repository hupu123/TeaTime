package com.hugh.teatime.utils;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.listener.DialogListener;

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
