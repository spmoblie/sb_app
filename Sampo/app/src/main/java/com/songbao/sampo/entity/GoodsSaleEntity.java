package com.songbao.sampo.entity;

import java.util.ArrayList;

/**
 * 商品售后数据结构体
 */
public class GoodsSaleEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private int saleType; //售后类型 1:换货/2:退货
	private int saleStatus; //售后状态 6:审核中/7:审核通过/8:审核拒绝
	private String addTime; //退换时间
	private String saleReason; //退换原因
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

	public ArrayList<String> getImgList() {
		return imgList;
	}

	public void setImgList(ArrayList<String> imgList) {
		this.imgList = imgList;
	}
}
