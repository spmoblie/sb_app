package com.songbao.sampo_b.entity;

import java.util.List;

public class CouponEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String termTime; //有效期
	private double priceNew; //促销价
	private double priceOld; //销售价
	private double discount; //折扣价
	private int status; //1:生效中，2:已核销，3:已过期
	private List<CouponEntity> mainLists; //数集

	public CouponEntity() {
		super();
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTermTime() {
		return termTime;
	}

	public void setTermTime(String termTime) {
		this.termTime = termTime;
	}

	public double getPriceNew() {
		return priceNew;
	}

	public void setPriceNew(double priceNew) {
		this.priceNew = priceNew;
	}

	public double getPriceOld() {
		return priceOld;
	}

	public void setPriceOld(double priceOld) {
		this.priceOld = priceOld;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<CouponEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<CouponEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
