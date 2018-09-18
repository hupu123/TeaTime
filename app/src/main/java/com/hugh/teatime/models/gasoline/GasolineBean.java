package com.hugh.teatime.models.gasoline;

import android.support.annotation.NonNull;

import com.hugh.teatime.utils.ToolUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

public class GasolineBean implements Serializable, Comparable<GasolineBean> {
    private String id;              // 记录ID
    private long date;              // 日期 *
    private BigDecimal totalPrice;  // 总价 *
    private BigDecimal unitPrice;   // 单价
    private double mileage;         // 里程 *
    private double quantity;        // 数量
    private String comment;         // 备注
    private String model;           // 汽油型号 *
    private int invoice;            // 是否开发票 *      0=已开票,1=未开票
    private String payMethod;       // 支付方式 *
    private String carNO;           // 车牌号 *
    private int year;               // 年份

    public GasolineBean(long date, BigDecimal totalPrice, double mileage, String model, int invoice, String payMethod, String carNO) {
        this.date = date;
        this.totalPrice = totalPrice;
        this.mileage = mileage;
        this.model = model;
        this.invoice = invoice;
        this.payMethod = payMethod;
        this.carNO = carNO;

        // 初始化非必填字段
        this.unitPrice = new BigDecimal(0);
        this.comment = "";
        this.quantity = 0;
        this.year = getYearFromDate(date);
    }

    public GasolineBean(String id, long date, BigDecimal totalPrice, BigDecimal unitPrice, double mileage, double quantity, String comment, String model, int invoice, String payMethod, String carNO) {
        this.id = id;
        this.date = date;
        this.totalPrice = totalPrice;
        this.unitPrice = unitPrice;
        this.mileage = mileage;
        this.quantity = quantity;
        this.comment = comment;
        this.model = model;
        this.invoice = invoice;
        this.payMethod = payMethod;
        this.carNO = carNO;
        this.year = getYearFromDate(date);
    }

    private int getYearFromDate(long date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(date);
        return calendar.get(Calendar.YEAR);
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getUnitPrice() {
        // 如果单价未赋值且总数量有值，则自动计算单价
        if ((unitPrice == null || unitPrice.doubleValue() == 0) && quantity != 0) {
            unitPrice = totalPrice.divide(new BigDecimal(quantity), 2, BigDecimal.ROUND_HALF_UP);
        }
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCarNO() {
        return carNO;
    }

    public void setCarNO(String carNO) {
        this.carNO = carNO;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public int getInvoice() {
        return invoice;
    }

    public void setInvoice(int invoice) {
        this.invoice = invoice;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int compareTo(@NonNull GasolineBean o) {
        int tYear = ToolUtil.getYearFromTimestamp(this.getDate());
        int tMonth = ToolUtil.getMonthFromTimestamp(this.getDate());
        int tDay = ToolUtil.getDayFromTimestamp(this.getDate());
        int oYear = ToolUtil.getYearFromTimestamp(o.getDate());
        int oMonth = ToolUtil.getMonthFromTimestamp(o.getDate());
        int oDay = ToolUtil.getDayFromTimestamp(o.getDate());
        // 排序规则：从大到小，年>月>日
        int result = oYear - tYear;
        if (result == 0) {
            result = oMonth - tMonth;
            if (result == 0) {
                result = oDay - tDay;
            }
        }
        return result;
    }
}
