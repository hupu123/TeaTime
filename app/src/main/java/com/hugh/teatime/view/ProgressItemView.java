package com.hugh.teatime.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.LogUtil;

public class ProgressItemView extends View {

    private Paint mPaint;
    private int width;
    private int height;
    private int color;
    private int progress;
    private int max;

    public ProgressItemView(Context context) {
        super(context);
        mPaint = new Paint();
    }

    public ProgressItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressItemView);
        color = ta.getColor(R.styleable.ProgressItemView_bgColor, getResources().getColor(R.color.colorGreen));
        max = ta.getInt(R.styleable.ProgressItemView_max, 100);
        if (max < 1) {
            max = 1;
        }
        progress = ta.getInt(R.styleable.ProgressItemView_progress, 0);
        if (progress > max) {
            progress = max;
        }
        if (progress < 0) {
            progress = 0;
        }
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        this.height = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        LogUtil.logHugh("ProgressItemView onMeasure width=" + this.width + " height=" + this.height + " wMode=" + widthMode + " hMode=" + heightMode);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(getResources().getColor(R.color.colorWhite));
        canvas.drawRoundRect(0, 0, width, height, 5, 5, mPaint);
        mPaint.setColor(color);
        float widthP = (float) width * progress / max;
        canvas.drawRoundRect(0, 0, widthP, height, 5, 5, mPaint);
    }

    public void setMax(int max) {
        if (max < 1) {
            max = 1;
        }
        this.max = max;
    }

    public void setProgress(int progress) {
        if (progress > max) {
            progress = max;
        }
        if (progress < 0) {
            progress = 0;
        }
        this.progress = progress;
        invalidate();
    }
}
