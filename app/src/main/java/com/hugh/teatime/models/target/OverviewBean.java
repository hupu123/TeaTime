package com.hugh.teatime.models.target;

import java.math.BigDecimal;

public class OverviewBean {
    private String title;
    private String targetName;
    private int targetTotal;
    private int doneTotal;
    private int oweTotal;
    private int avgNum;
    private int maxNum;
    private int minNum;
    private int daysTotal;
    private int daysPass;
    private int daysNoPass;
    private String passRate;

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

    public int getTargetTotal() {
        return targetTotal;
    }

    public void setTargetTotal(int targetTotal) {
        this.targetTotal = targetTotal;
    }

    public int getDoneTotal() {
        return doneTotal;
    }

    public void setDoneTotal(int doneTotal) {
        this.doneTotal = doneTotal;
    }

    public int getOweTotal() {
        return oweTotal;
    }

    public void setOweTotal(int oweTotal) {
        this.oweTotal = oweTotal;
    }

    public int getDaysTotal() {
        return daysTotal;
    }

    public void setDaysTotal(int daysTotal) {
        this.daysTotal = daysTotal;
    }

    public String getPassRate() {
        if (daysTotal != 0) {
            float rate = new BigDecimal(daysPass).divide(new BigDecimal(daysTotal), 4, BigDecimal.ROUND_HALF_UP).floatValue() * 100;
            passRate = rate + "%";
        }
        return passRate;
    }

    public void setPassRate(String passRate) {
        this.passRate = passRate;
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public int getAvgNum() {
        return avgNum;
    }

    public void setAvgNum(int avgNum) {
        this.avgNum = avgNum;
    }

    public int getDaysPass() {
        return daysPass;
    }

    public void setDaysPass(int daysPass) {
        this.daysPass = daysPass;
    }

    public int getDaysNoPass() {
        this.daysNoPass = this.daysTotal - daysPass;
        return daysNoPass;
    }
}
