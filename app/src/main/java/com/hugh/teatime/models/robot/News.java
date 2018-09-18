package com.hugh.teatime.models.robot;

/**
 * 新闻实体类
 * Created by Hugh on 2016/2/16 16:33
 */
public class News {

    private String article;// 新闻标题
    private String source;// 新闻来源
    private String icon;// 新闻图标
    private String detailurl;// 详情链接

    public String getArticle() {
        return article;
    }

    public String getSource() {
        return source;
    }

    public String getIcon() {
        return icon;
    }

    public String getDetailurl() {
        return detailurl;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setDetailurl(String detailurl) {
        this.detailurl = detailurl;
    }
}
