package com.hugh.teatime.models.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.hugh.teatime.R;
import com.hugh.teatime.listener.DialogListener;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.SPUtil;
import com.hugh.teatime.view.TitlebarView;

import java.io.File;

public class FilePickActivity extends BaseActivity {

    private TitlebarView tbv;
    private ListView lvFiles;

    private FileListAdapter fla;
    private File file;
    private File[] files;

    public static final String INTENT_FILE_PATH = "intent_file_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_pick);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        tbv = findViewById(R.id.tbv);
        lvFiles = findViewById(R.id.lv_fp_file_list);
        Button btnConfirm = findViewById(R.id.btn_fp_confirm);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        btnConfirm.setOnClickListener(clickListener);
        lvFiles.setOnItemClickListener(itemClickListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        file = new File(SPUtil.getInstance(this).getFilePickPath());
        if (!file.exists()) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                file = Environment.getExternalStorageDirectory();
            } else {
                file = new File("/storage/");
            }
        }
        if (file.isDirectory()) {
            fla = new FileListAdapter(this, file.listFiles());
            lvFiles.setAdapter(fla);
            files = fla.getFiles();
        }
        tbv.setTitleName(file.getPath());
    }

    /**
     * 按钮点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_fp_confirm:
                    DialogUtil dialogUtil = new DialogUtil(FilePickActivity.this, R.mipmap.icon_info_g, String.format(getResources().getString(R.string.dialog_is_import_path), file.getPath()), new DialogListener() {
                        @Override
                        public void sure() {
                            SPUtil.getInstance(FilePickActivity.this).setFilePickPath(file.getPath());
                            Intent intent = new Intent();
                            intent.putExtra(INTENT_FILE_PATH, file.getPath());
                            FilePickActivity.this.setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void cancel() {
                            //DO NOTHING
                        }
                    });
                    dialogUtil.showDialog();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ListView item点击事件监听
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            File fileTemp = files[position];
            if (fileTemp.isDirectory()) {
                fla = new FileListAdapter(FilePickActivity.this, fileTemp.listFiles());
                lvFiles.setAdapter(fla);
                file = fileTemp;
                files = fla.getFiles();
            }
            tbv.setTitleName(file.getPath());
        }
    };

    /**
     * 按键监听
     *
     * @param keyCode 按键码
     * @param event   事件
     * @return 处理结果状态
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            File parentFile = file.getParentFile();
            if (parentFile != null && parentFile.exists()) {
                fla = new FileListAdapter(FilePickActivity.this, parentFile.listFiles());
                lvFiles.setAdapter(fla);
                file = parentFile;
                files = fla.getFiles();
            } else {
                finish();
            }
            tbv.setTitleName(file.getPath());

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }
}
