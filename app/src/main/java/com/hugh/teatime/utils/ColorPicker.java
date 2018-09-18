package com.hugh.teatime.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;

public class ColorPicker {

    private int position;
    private Context context;

    public ColorPicker(Context context) {
        this.context = context;
        this.position = 0;
    }

    /**
     * 获取颜色ID
     *
     * @return 颜色ID
     */
    public int getColor() {
        int colorID;
        if (position < GlobalVar.BILL_TYPE_COLOR_ARRAY.length) {
            colorID = GlobalVar.BILL_TYPE_COLOR_ARRAY[position];
            position++;
        } else {
            colorID = R.color.colorGreen;
        }
        return ContextCompat.getColor(context, colorID);
    }
}
