package com.hugh.teatime.models.image;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.home.Folder;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.ImageScanerUtil;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.utils.ThreadPoolUtil;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地图片文件夹页
 */
public class ImageFolderActivity extends BaseActivity {

    private GridView gvImageFolderList;
    private ImageFolderListAdapter ifla;
    private List<Folder> folders;
    public static final String INTENT_FOLDER_NAME = "intent_folder_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_folder);

        initView();

        // 初始化图片数据
        ImageScanerUtil.initImageData(this);
        folders = MyDBOperater.getInstance(this).getFolder();
        ifla = new ImageFolderListAdapter(this, folders);
        gvImageFolderList.setAdapter(ifla);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        gvImageFolderList = findViewById(R.id.gv_image_folder);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                final DialogUtil dialogUtil = new DialogUtil(ImageFolderActivity.this);
                dialogUtil.showDialog();
                ThreadPoolUtil.getInstance().addThread(new Runnable() {
                    @Override
                    public void run() {

                        MyDBOperater.getInstance(ImageFolderActivity.this).clearImages();
                        SPUtil.getInstance(ImageFolderActivity.this).setIsInitImageTable(false);
                        ImageScanerUtil.initImageData(ImageFolderActivity.this);
                        folders = new ArrayList<>();
                        folders = MyDBOperater.getInstance(ImageFolderActivity.this).getFolder();
                        Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                ifla.notifyDataSetChanged();
                                dialogUtil.hideDialog();
                            }
                        });
                    }
                });
            }
        });
        gvImageFolderList.setOnItemClickListener(itemClickListener);
    }

    /**
     * item点击事件监听
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(ImageFolderActivity.this, ImageListActivity.class);
            intent.putExtra(INTENT_FOLDER_NAME, folders.get(position).getName());
            startActivity(intent);
        }
    };
}
