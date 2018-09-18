package com.hugh.teatime.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugh.teatime.R;

/**
 * Created by Hugh on 2016/8/15 10:56
 */
public class ItemBtnView extends LinearLayout {

    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvValue;

    public ItemBtnView(Context context) {
        super(context);
    }

    public ItemBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_item_btn, this);

        ivIcon = view.findViewById(R.id.iv_ib_icon);
        tvName = view.findViewById(R.id.tv_ib_txt_name);
        tvValue = view.findViewById(R.id.tv_ib_txt_value);
    }

    public void setItemIcon(Bitmap bitmap) {

        ivIcon.setVisibility(VISIBLE);
        ivIcon.setImageBitmap(bitmap);
    }

    public void setItemName(String name) {

        tvName.setVisibility(VISIBLE);
        tvName.setText(name);
    }

    public void setItemValue(String value) {

        tvValue.setVisibility(VISIBLE);
        tvValue.setText(value);
    }
}
