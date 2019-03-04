package com.hugh.teatime.models.target;

public class DailyTargetBean {
    private int dailyId;        // 每日目标ID
    private String title;       // 目标标题
    private String targetName;  // 目标名称
    private int targetNum;      // 目标数量
    private long createTime;    // 创建时间

    public DailyTargetBean(int dailyId, String title, String targetName, int targetNum, long createTime) {
        this.dailyId = dailyId;
        this.title = title;
        this.targetName = targetName;
        this.targetNum = targetNum;
        this.createTime = createTime;
    }

    public DailyTargetBean(String title, String targetName, int targetNum, long createTime) {
        this.title = title;
        this.targetName = targetName;
        this.targetNum = targetNum;
        this.createTime = createTime;
    }

    public int getDailyId() {
        return dailyId;
    }

    public void setDailyId(int dailyId) {
        this.dailyId = dailyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public int getTargetNum() {
        return targetNum;
    }

    public void setTargetNum(int targetNum) {
        this.targetNum = targetNum;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
