package com.hugh.teatime.models.note;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class EventBean implements Serializable, Comparable<EventBean> {

    public static final int TYPE_NOTE = 0;
    public static final int TYPE_GASOLINE = 1;

    private String id;              // 事件唯一标识
    private long date;              // 时间戳
    private String title;           // 标题
    private String content;         // 内容
    private double latitude;        // 纬度
    private double longitude;       // 经度
    private String address;         // 地址
    private String cityCode;        // 城市码
    private int eventType;          // 事件类型，0=普通，1=加油记录
    private String gasolineId;      // 关联表加油记录ID

    public EventBean(String id, long date, String title, String content, double latitude, double longitude, String address, String cityCode, int eventType, String gasolineId) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.cityCode = cityCode;
        this.eventType = eventType;
        this.gasolineId = gasolineId;
    }

    public EventBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getGasolineId() {
        return gasolineId;
    }

    public void setGasolineId(String gasolineId) {
        this.gasolineId = gasolineId;
    }

    @Override
    public int compareTo(@NonNull EventBean o) {
        return (int) (this.getDate() - o.getDate());
    }
}
