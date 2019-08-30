package com.sbwg.sxb.entity;

import java.io.Serializable;


public class HomeListEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private int billId;

    private String billName;

    private int billNum;

    private double billPrice;

    private double subtotal;

    private double allTotal;

    public HomeListEntity() {

    }

    public HomeListEntity(int billId, String billName, int billNum, double billPrice, double subtotal, double allTotal) {
        this.billId = billId;
        this.billName = billName;
        this.billNum = billNum;
        this.billPrice = billPrice;
        this.subtotal = subtotal;
        this.allTotal = allTotal;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public int getBillNum() {
        return billNum;
    }

    public void setBillNum(int billNum) {
        this.billNum = billNum;
    }

    public double getBillPrice() {
        return billPrice;
    }

    public void setBillPrice(double billPrice) {
        this.billPrice = billPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getAllTotal() {
        return allTotal;
    }

    public void setAllTotal(double allTotal) {
        this.allTotal = allTotal;
    }

    @Override
    public String toString() {
        return "HomeListEntity{" +
                "billId=" + billId +
                ", billName='" + billName + '\'' +
                ", billNum=" + billNum +
                ", billPrice=" + billPrice +
                ", subtotal=" + subtotal +
                ", allTotal=" + allTotal +
                '}';
    }
}
