package com.hugh.teatime.models.target;

public class TargetBean {

    public static final int TYPE_DAILY = 0;
    public static final int TYPE_ONETIME = 1;
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_UNCOMPLETED = 2;

    private int id;             // 唯一标识
    private int dailyId;        // 每日目标ID
    private int type;           // 目标类型，0=每日目标，1=一次性目标
    private String date;        // 日期，格式：yyyyMMdd
    private String title;       // 目标标题
    private String targetName;  // 目标名称
    private int targetNum;      // 目标数量
    private int doneNum;        // 已完成数量
    private int status;         // 状态，0=进行中，1=已完成，2=未完成
    private long createTime;    // 创建时间
    private long startTime;     // 开始时间
    private long endTime;       // 结束时间

    public TargetBean(int id, int dailyId, int type, String date, String title, String targetName, int targetNum, int doneNum, int status, long createTime, long startTime, long endTime) {
        this.id = id;
        this.dailyId = dailyId;
        this.type = type;
        this.date = date;
        this.title = title;
        this.targetName = targetName;
        this.targetNum = targetNum;
        this.doneNum = doneNum;
        this.status = status;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TargetBean(int dailyId, int type, String date, String title, String targetName, int targetNum, int doneNum, int status, long createTime, long startTime, long endTime) {
        this.dailyId = dailyId;
        this.type = type;
        this.date = date;
        this.title = title;
        this.targetName = targetName;
        this.targetNum = targetNum;
        this.doneNum = doneNum;
        this.status = status;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TargetBean(int type, String title, String targetName, int targetNum, int doneNum, int status, long createTime, long startTime, long endTime) {
        this.type = type;
        this.title = title;
        this.targetName = targetName;
        this.targetNum = targetNum;
        this.doneNum = doneNum;
        this.status = status;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TargetBean(TargetBean targetBean) {
        this.id = targetBean.getId();
        this.dailyId = targetBean.getDailyId();
        this.type = targetBean.getType();
        this.date = targetBean.getDate();
        this.title = targetBean.getTitle();
        this.targetName = targetBean.getTargetName();
        this.targetNum = targetBean.getTargetNum();
        this.doneNum = targetBean.getDoneNum();
        this.status = targetBean.getStatus();
        this.createTime = targetBean.getCreateTime();
        this.startTime = targetBean.getStartTime();
        this.endTime = targetBean.getEndTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDailyId() {
        return dailyId;
    }

    public void setDailyId(int dailyId) {
        this.dailyId = dailyId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getDoneNum() {
        return doneNum;
    }

    public void setDoneNum(int doneNum) {
        this.doneNum = doneNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
