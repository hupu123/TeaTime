package com.hugh.teatime.models.tool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.models.home.FilePickActivity;
import com.hugh.teatime.adapter.common.CommonAdapter;
import com.hugh.teatime.adapter.common.ViewHolder;
import com.hugh.teatime.utils.StringUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.utils.ToolUtil;
import com.hugh.teatime.view.TitlebarView;

import java.io.File;
import java.util.ArrayList;

public class RenameFilesActivity extends BaseActivity {

    private EditText etPrefix;
    private EditText etLocation;
    private ListView lvFilesList;
    private RelativeLayout rlProgressBar;
    private ProgressBar pbProgress;
    private TextView tvProgressTxt;

    private ArrayList<FileBean> filesRename;
    private CommonAdapter<FileBean> mAdapter;

    private final int RENAME_FILE_SELECT_REQUEST = 996;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename_files);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        etPrefix = findViewById(R.id.et_rf_prefix);
        etLocation = findViewById(R.id.et_rf_location);
        lvFilesList = findViewById(R.id.lv_rf_files_list);
        rlProgressBar = findViewById(R.id.rl_rf_progress_bar);
        pbProgress = findViewById(R.id.pb_rf_progress);
        tvProgressTxt = findViewById(R.id.tv_rf_progress_txt);
        Button btnSelect = findViewById(R.id.btn_rf_select);
        Button btnConfirm = findViewById(R.id.btn_rf_confirm);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        btnSelect.setOnClickListener(clickListener);
        btnConfirm.setOnClickListener(clickListener);
        etLocation.setKeyListener(null);
    }

    /**
     * 重命名方法
     */
    private void confirmMethod() {

        String prefix = etPrefix.getText().toString();
        if (StringUtil.isStrNull(prefix)) {
            ToastUtil.showInfo(this, R.string.toast_input_prefix, true);
            etPrefix.requestFocus();
            return;
        }
        if (filesRename == null || filesRename.size() == 0) {
            ToastUtil.showInfo(this, R.string.toast_select_folder, true);
            etLocation.requestFocus();
            return;
        }

        prefix = prefix + "_";
        for (int i = 0; i < filesRename.size(); i++) {
            // 修改文件名称
            if (filesRename.get(i).isSelected()) {
                File file = filesRename.get(i).getFile();
                String parentStr = file.getParent() + "/";
                String timeStr = ToolUtil.getTimeFromTimestamp(file.lastModified(), 3);
                String fileType = "." + ToolUtil.getFileType(file);
                File newNameFile;
                int j = 0;
                String suffix;
                do {
                    if (j == 0) {
                        suffix = "";
                    } else if (j > 0 && j < 100) {
                        suffix = "_" + j;
                    } else {
                        // 如果重命名100次仍失败，则放弃重命名该文件，执行下一文件
                        newNameFile = file;
                        break;
                    }
                    String fileName = parentStr + prefix + timeStr + suffix + fileType;
                    newNameFile = new File(fileName);
                    j++;
                } while (!file.renameTo(newNameFile));

                filesRename.remove(i);
                filesRename.add(i, new FileBean(newNameFile, true));
                mAdapter.notifyDataSetChanged();
                lvFilesList.setSelection(i);
            }
            // 更新进度条
            int position = i + 1;
            int progress = ToolUtil.getProgress(position, filesRename.size());
            pbProgress.setProgress(progress);
            if (progress == 100) {
                tvProgressTxt.setText(R.string.rename_files_done);
            } else {
                tvProgressTxt.setText(String.format(getResources().getString(R.string.rename_files_progress), position, filesRename.size(), progress));
            }
        }
    }

    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_rf_select:
                    Intent intent = new Intent(RenameFilesActivity.this, FilePickActivity.class);
                    startActivityForResult(intent, RENAME_FILE_SELECT_REQUEST);
                    break;
                case R.id.btn_rf_confirm:
                    confirmMethod();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == RENAME_FILE_SELECT_REQUEST) {
            String filePath = data.getStringExtra(FilePickActivity.INTENT_FILE_PATH);
            File fileDir = new File(filePath);
            if (fileDir.isDirectory()) {
                File[] files = fileDir.listFiles();
                filesRename = new ArrayList<>();
                for (File file : files) {
                    if (file.isFile()) {
                        filesRename.add(new FileBean(file, true));
                    }
                }
                etLocation.setText(fileDir.getPath());
                lvFilesList.setVisibility(View.VISIBLE);
                rlProgressBar.setVisibility(View.VISIBLE);
                mAdapter = new CommonAdapter<FileBean>(this, R.layout.item_files_selected_lv, filesRename) {
                    @Override
                    protected void convert(ViewHolder viewHolder, FileBean item, final int position) {

                        viewHolder.setText(R.id.tv_fsi_name, item.getFile().getName());
                        CheckBox cbIsChecked = viewHolder.getView(R.id.cb_fsi_is_checked);
                        cbIsChecked.setChecked(item.isSelected());
                        cbIsChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                if (isChecked) {
                                    filesRename.get(position).setIsSelected(true);
                                } else {
                                    filesRename.get(position).setIsSelected(false);
                                }
                            }
                        });
                    }
                };
                lvFilesList.setAdapter(mAdapter);
                pbProgress.setProgress(0);
                tvProgressTxt.setText(String.format(getResources().getString(R.string.rename_files_progress), 0, filesRename.size(), 0));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
