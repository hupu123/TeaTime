package com.hugh.teatime.models.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hugh.teatime.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Hugh on 2016/6/27 16:23
 */
public class FileListAdapter extends BaseAdapter {

    private File[] files;
    private Context context;

    public FileListAdapter(Context context, File[] files) {

        this.context = context;
        this.files = resortFileList(files);
    }

    @Override
    public int getCount() {

        if (files != null) {
            return files.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_files_lv, null);
            holder = new ViewHolder();

            holder.ivIcon = convertView.findViewById(R.id.iv_fi_icon);
            holder.tvName = convertView.findViewById(R.id.tv_fi_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File file = files[position];
        if (file.isDirectory()) {
            holder.ivIcon.setImageResource(R.mipmap.icon_folder);
        } else {
            holder.ivIcon.setImageResource(R.mipmap.icon_file);
        }
        holder.tvName.setText(file.getName());

        return convertView;
    }

    private class ViewHolder {

        ImageView ivIcon;
        TextView tvName;
    }

    /**
     * 获取文件列表
     *
     * @return 排序后的文件列表
     */
    public File[] getFiles() {
        return files;
    }

    /**
     * 对文件列表重新排序
     * 排序策略为：文件夹>文件，文件名升序
     *
     * @param files 文件列表
     * @return 排序后文件列表
     */
    private File[] resortFileList(File[] files) {

        if (files == null || files.length == 0) {
            return new File[0];
        }
        ArrayList<File> fileList = new ArrayList<>();
        ArrayList<File> dirList = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                dirList.add(file);
            } else {
                fileList.add(file);
            }
        }

        Collections.sort(fileList, new MyFileComparetor());
        Collections.sort(dirList, new MyFileComparetor());
        dirList.addAll(fileList);

        return dirList.toArray(new File[dirList.size()]);
    }

    /**
     * 自定义文件名称排序对比器
     */
    private class MyFileComparetor implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
