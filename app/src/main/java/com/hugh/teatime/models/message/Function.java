package com.hugh.teatime.models.message;

/**
 * Created by Hugh on 2016/2/17 14:13
 */
public class Function {

    private String song;// 歌名
    private String singer;// 歌手名
    private String name;// 作品名
    private String author;// 作者

    public String getSong() {
        return song;
    }

    public String getSinger() {
        return singer;
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setName(String name) {
        this.name = name;
    }
}
