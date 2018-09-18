package com.hugh.teatime.models.robot;

import com.hugh.teatime.models.message.Function;

import java.util.List;

/**
 * 消息实体类
 * Created by Hugh on 2016/2/15 14:51
 */
public class Message {

    private int code;  // 消息状态码
    private String msg;// 消息内容
    private String url;// 消息链接
    private List<News> newsList;// 新闻列表
    private List<CookBook> cookBookList;// 菜谱列表
    private Function function;

    private int type;  // 消息类型，0=机器人消息，1=用户消息
    private long time; // 时间（时间戳形式）

    /**
     * 构造方法
     */
    public Message() {
    }

    /**
     * 构造方法
     *
     * @param type 消息类型
     * @param msg  消息内容
     */
    public Message(int type, String msg, long time) {
        this.type = type;
        this.msg = msg;
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public List<CookBook> getCookBookList() {
        return cookBookList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public void setCookBookList(List<CookBook> cookBookList) {
        this.cookBookList = cookBookList;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
