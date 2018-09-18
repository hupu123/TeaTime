package com.hugh.teatime.models.tool;

import java.io.File;

/**
 * Created by Hugh on 2016/8/17 14:25
 */
public class FileBean {

    private File file;
    private boolean isSelected;

    public FileBean(File file, boolean isSelected) {
        this.file = file;
        this.isSelected = isSelected;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
