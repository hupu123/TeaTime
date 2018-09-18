package com.hugh.teatime.models.tool;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.View;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.view.ItemBtnView;
import com.hugh.teatime.view.TitlebarView;

import java.io.File;

public class ToolListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        initView();
    }

    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        ItemBtnView ibvRenameFiles = findViewById(R.id.ibv_rename_files);
        ItemBtnView ibvRefreshImages = findViewById(R.id.ibv_refresh_images);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        ibvRenameFiles.setOnClickListener(clickListener);
        ibvRenameFiles.setItemIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_rename_files));
        ibvRenameFiles.setItemName(getResources().getString(R.string.files_name_batch_processing_tool));
        ibvRefreshImages.setOnClickListener(clickListener);
        ibvRefreshImages.setItemIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_refresh_image));
        ibvRefreshImages.setItemName(getResources().getString(R.string.refresh_system_image_db_tool));
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent;
            switch (v.getId()) {
                case R.id.ibv_rename_files:
                    intent = new Intent(ToolListActivity.this, RenameFilesActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ibv_refresh_images:
                    File fileSD_file = new File("/storage/");
                    MediaScannerConnection.scanFile(ToolListActivity.this, new String[]{fileSD_file.toString()}, null, null);
                    break;
                default:
                    break;
            }
        }
    };
}
