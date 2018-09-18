package com.hugh.teatime.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.image.Image;

/**
 * 图片查看器工具
 * Created by Hugh on 2016/3/25 9:39
 */
public class ImageScanerUtil {

    /**
     * 初始化Image信息
     *
     * @param context 上下文
     */
    public static void initImageData(Context context) {

        if (SPUtil.getInstance(context).isInitImageTable()) {
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                Image img = getImageFromPath(path);
                img.setName(name);
                img.setPath(path);

                // 记录到应用数据库
                MyDBOperater.getInstance(context).addImage(img);
            } while (cursor.moveToNext());
            // 保存Image Table表初始化状态
            SPUtil.getInstance(context).setIsInitImageTable(true);
        }
        cursor.close();
    }

    /**
     * 通过Path获取Image信息
     *
     * @param path image path
     *
     * @return Image信息
     */
    private static Image getImageFromPath(String path) {

        Image img = new Image();

        String[] strArray = path.split("/");
        if (strArray.length > 1) {
            String folderName = strArray[strArray.length - 2];
            String name = strArray[strArray.length - 1];
            String folderPath = path.substring(0, path.length() - name.length() - 1);

            img.setFolderName(folderName);
            img.setFolderPath(folderPath);
        }

        return img;
    }
}
