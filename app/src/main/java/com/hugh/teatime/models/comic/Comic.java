package com.hugh.teatime.models.comic;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Hugh on 2016/6/28 10:34
 */
public class Comic implements Serializable {

    private int comicId;
    private String name;
    private String path;
    private String coverPath;
    private int pageTotal;
    private int progress;
    private ArrayList<File> fileList = new ArrayList<>();
    private boolean isChecked = false;

    public Comic() {

    }

    public Comic(int comicId, String name, String path, String coverPath, int pageTotal, int progress, ArrayList<File> fileList) {

        this.comicId = comicId;
        this.name = name;
        this.path = path;
        this.coverPath = coverPath;
        this.pageTotal = pageTotal;
        this.progress = progress;
        this.fileList = fileList;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public int getComicId() {
        return comicId;
    }

    public void setComicId(int comicId) {
        this.comicId = comicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<File> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<File> fileList) {
        this.fileList = fileList;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
