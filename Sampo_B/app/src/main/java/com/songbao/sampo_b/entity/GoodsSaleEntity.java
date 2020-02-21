package com.songbao.sampo_b.entity;

import java.util.ArrayList;

/**
 * 商品售后数据结构体
 */
public class GoodsSaleEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private int saleType; //售后类型 1:换货/2:退货
	private int saleStatus; //售后状态 6:审核中/7:审核通过/8:审核拒绝/9:退款中/10:退款完成
	private double refundPrice; //退款金额
	private String refundNo; //退款编号
	private String addTime; //申请时间
	private String endTime; //完成时间
	private String saleReason; //退换原因
	private String expressNo; //快递单号
	private ArrayList<String> imgList; //凭证图片

	@Override
	public String getEntityId() {
		return String.valueOf(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSaleType() {
		return saleType;
	}

	public void setSaleType(int saleType) {
		this.saleType = saleType;
	}

	public int getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(int saleStatus) {
		this.saleStatus = saleStatus;
	}

	public double getRefundPrice() {
		return refundPrice;
	}

	public void setRefundPrice(double refundPrice) {
		this.refundPrice = refundPrice;
	}

	public String getRefundNo() {
		return refundNo;
	}

	public void setRefundNo(String refundNo) {
		this.refundNo = refundNo;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getSaleReason() {
		return saleReason;
	}

	public void setSaleReason(String saleReason) {
		this.saleReason = saleReason;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public ArrayList<String> getImgList() {
		return imgList;
	}

	public void setImgList(ArrayList<String> imgList) {
		this.imgList = imgList;
	}
}
