package com.hugh.teatime.models.robot;

/**
 * 菜谱实体类
 * Created by Hugh on 2016/2/16 16:36
 */
public class CookBook {

    private String name;// 菜谱名称
    private String icon;// 菜谱图片
    private String info;// 菜谱信息
    private String detailurl;// 菜谱详情链接

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getInfo() {
        return info;
    }

    public String getDetailurl() {
        return detailurl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setDetailurl(String detailurl) {
        this.detailurl = detailurl;
    }
}
