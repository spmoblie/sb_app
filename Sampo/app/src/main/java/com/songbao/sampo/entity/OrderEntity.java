package com.songbao.sampo.entity;

import java.util.List;

public class OrderEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String title;
	private String picUrl;
	private String name;
	private String cost; //订单价
	private String payType; //支付类型
	private String addTime; //有效期
	private int status; //1:已付款，2:已核销，3:已过期
	private ThemeEntity themeEn; //活动
	private List<OrderEntity> mainLists; //数集

	public OrderEntity() {
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ThemeEntity getThemeEn() {
		return themeEn;
	}

	public void setThemeEn(ThemeEntity themeEn) {
		this.themeEn = themeEn;
	}

	public List<OrderEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<OrderEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
