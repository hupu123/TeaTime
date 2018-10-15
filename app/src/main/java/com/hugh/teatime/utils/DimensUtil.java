package com.hugh.teatime.utils;

import android.content.Context;
import android.util.TypedValue;

public class DimensUtil {

    private static DimensUtil instance;
    private Context context;

    private DimensUtil(Context context) {
        this.context = context;
    }

    public static DimensUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DimensUtil(context);
        }
        return instance;
    }

    public int dp2px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
