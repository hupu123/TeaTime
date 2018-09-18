package com.hugh.teatime.models.message;

import java.io.Serializable;

/**
 * Created by Hugh on 2016/4/15 15:30
 */
public class SMS implements Serializable {

    private String number;// 发件人号码
    private String body;//短信内容
    private String person;//发件人姓名
    private long date;//收发日期（时间戳形式）
    private int type;//收发类型，0=发出，1=接收
    private int classType;//分类类别，0=普通短信，1=黑名单短信

    /**
     * 构造函数
     *
     * @param number 号码
     * @param body   内容
     * @param person 名称
     * @param date   日期
     * @param type   类型
     */
    public SMS(String number, String body, String person, long date, int type) {

        this.number = number;
        this.body = body;
        this.person = person;
        this.date = date;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }
}
