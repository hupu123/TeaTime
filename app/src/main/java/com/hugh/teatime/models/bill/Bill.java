package com.hugh.teatime.models.bill;

import java.io.Serializable;

/**
 * 账单实体
 * Created by Hugh on 2016/4/5 16:48
 */
public class Bill implements Serializable {

    private int id;// 账单ID
    private double amount;// 金额
    private String note;// 描述
    private int ioType;// 收支类型，0=支出，1=收入
    private int type;// 账单类型,0=其他，1=交通，2=饮食....
    private String typeName;// 账单类型名称
    private int year;// 年
    private int month;// 月
    private int day;// 日

    /**
     * 构造函数1
     *
     * @param id       ID
     * @param amount   金额
     * @param note     备注
     * @param ioType   收支类型
     * @param type     账单类型
     * @param typeName 账单类型名称
     * @param year     年
     * @param month    月
     * @param day      日
     */
    public Bill(int id, double amount, String note, int ioType, int type, String typeName, int year, int month, int day) {

        this.id = id;
        this.amount = amount;
        this.note = note;
        this.ioType = ioType;
        this.type = type;
        this.typeName = typeName;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * 构造函数2
     *
     * @param amount   金额
     * @param note     备注
     * @param ioType   收支类型
     * @param type     账单类型
     * @param typeName 账单类型名称
     * @param year     年
     * @param month    月
     * @param day      日
     */
    public Bill(double amount, String note, int ioType, int type, String typeName, int year, int month, int day) {

        this.amount = amount;
        this.note = note;
        this.ioType = ioType;
        this.type = type;
        this.typeName = typeName;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIoType() {
        return ioType;
    }

    public void setIoType(int ioType) {
        this.ioType = ioType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
