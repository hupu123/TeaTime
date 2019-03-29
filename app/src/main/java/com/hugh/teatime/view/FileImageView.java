package com.hugh.teatime.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * 文件图片控件，用于异步加载本地图片
 * Created by Hugh on 2016/3/29 14:36
 */
public class FileImageView extends AppCompatImageView {

    private Context context;

    public FileImageView(Context context) {
        super(context);
        this.context = context;
    }

    public FileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 设置文件图片路径并显示
     *
     * @param path 图片路径
     */
    public void setImageFile(String path) {

        Glide.with(context).load(new File(path)).into(this);
    }

    /**
     * 设置文件图片路径并显示，同时设置缩略大小
     *
     * @param path  图片路径
     * @param scale 缩放比例
     */
    public void setImageFile(String path, float scale) {
        Glide.with(context).load(new File(path)).thumbnail(scale).into(this);
    }
}
