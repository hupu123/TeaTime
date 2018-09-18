package com.hugh.teatime.models.image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.view.FileImageView;

import java.util.List;

/**
 * Created by Hugh on 2016/3/29 9:57
 */
public class ImageListAdapter extends BaseAdapter {

    private List<Image> images;
    private Context context;

    public ImageListAdapter(Context context, List<Image> images) {

        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        if (images != null) {
            return images.size();
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
            convertView = inflater.inflate(R.layout.item_images_gv, null);
            holder = new ViewHolder();
            holder.fivImgContent = (FileImageView) convertView.findViewById(R.id.fiv_image_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fivImgContent.setImageFile(images.get(position).getPath(), GlobalVar.IMG_THUMBNAIL_SCALE);

        return convertView;
    }

    private class ViewHolder {

        FileImageView fivImgContent;
    }
}
