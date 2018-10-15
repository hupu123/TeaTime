package com.hugh.teatime.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugh.teatime.R;

public class MarkerView extends LinearLayout {

    private TextView tvLabel;
    private ImageView ivIcon;

    public MarkerView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_marker, this);
        tvLabel = view.findViewById(R.id.tv_label);
        ivIcon = view.findViewById(R.id.iv_icon);
    }

    /**
     * 设置标签说明
     *
     * @param label 标签内容
     */
    public void setLabel(String label) {
        tvLabel.setText(label);
    }

    /**
     * 设置图标
     *
     * @param bitmap 图标位图
     */
    public void setIcon(Bitmap bitmap) {
        ivIcon.setImageBitmap(bitmap);
    }

    /**
     * 设置标签文字是否可见
     *
     * @param isVisibal true=可见，false=隐藏
     */
    public void setLabelVisibal(boolean isVisibal) {
        if (isVisibal) {
            tvLabel.setVisibility(VISIBLE);
        } else {
            tvLabel.setVisibility(GONE);
        }
    }
}
