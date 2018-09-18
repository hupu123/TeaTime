package com.hugh.teatime.models.image;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.List;

/**
 * 图片列表页
 */
public class ImageListActivity extends BaseActivity {

    private TitlebarView tbv;
    private GridView gvImgList;

    private List<Image> images;
    public static final String INTENT_IMAGE_POSITION = "intent_image_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        initView();

        Intent intent = getIntent();
        String folderName = intent.getStringExtra(ImageFolderActivity.INTENT_FOLDER_NAME);
        if (!StringUtil.isStrNull(folderName)) {
            tbv.setTitleName(folderName);
            images = MyDBOperater.getInstance(this).getImagesByFolder(folderName);
            ImageListAdapter ila = new ImageListAdapter(this, images);
            gvImgList.setAdapter(ila);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {

        tbv = findViewById(R.id.tbv);
        gvImgList = findViewById(R.id.gv_img_list);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        gvImgList.setOnItemClickListener(itemClickListener);
    }

    /**
     * item点击事件监听
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(ImageListActivity.this, ImageDetailActivity.class);
            intent.putExtra(INTENT_IMAGE_POSITION, position);
            intent.putExtra(ImageFolderActivity.INTENT_FOLDER_NAME, images.get(position).getFolderName());
            startActivity(intent);
        }
    };
}
