package com.hugh.teatime.models.image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.Folder;
import com.hugh.teatime.view.FileImageView;

import java.util.List;

/**
 * Created by Hugh on 2016/3/28 13:50
 */
public class ImageFolderListAdapter extends BaseAdapter {

    private List<Folder> folders;
    private Context context;

    public ImageFolderListAdapter(Context context, List<Folder> folders) {

        this.context = context;
        this.folders = folders;
    }

    @Override
    public int getCount() {
        if (folders != null) {
            return folders.size();
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

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_folders_gv, null);
            holder.fivF1 = convertView.findViewById(R.id.fiv_gv_f_1);
            holder.fivF2 = convertView.findViewById(R.id.fiv_gv_f_2);
            holder.fivF3 = convertView.findViewById(R.id.fiv_gv_f_3);
            holder.fivF4 = convertView.findViewById(R.id.fiv_gv_f_4);
            holder.tvFName = convertView.findViewById(R.id.tv_gv_folder_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Folder folder = folders.get(position);
        holder.tvFName.setText(folder.getName());
        List<Image> images = MyDBOperater.getInstance(context).getImagesByFolder(folder.getName());
        int size = images.size();
        if (size > 0) {
            holder.fivF1.setImageFile(images.get(0).getPath(), GlobalVar.IMG_THUMBNAIL_SCALE);
            if (size > 1) {
                holder.fivF2.setImageFile(images.get(1).getPath(), GlobalVar.IMG_THUMBNAIL_SCALE);
                if (size > 2) {
                    holder.fivF3.setImageFile(images.get(2).getPath(), GlobalVar.IMG_THUMBNAIL_SCALE);
                    if (size > 3) {
                        holder.fivF4.setImageFile(images.get(3).getPath(), GlobalVar.IMG_THUMBNAIL_SCALE);
                    }
                }
            }
        }

        return convertView;
    }

    private class ViewHolder {

        FileImageView fivF1;
        FileImageView fivF2;
        FileImageView fivF3;
        FileImageView fivF4;
        TextView tvFName;
    }
}
