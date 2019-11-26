package com.songbao.sampo.entity;

import java.util.List;

public class CustomizeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String title;
	private String picUrl;
	private String name;
	private String phone;
	private String cost; //订单价
	private String payType; //支付类型
	private String addTime;
	private int status; //1:待付款，2:生产中，3:待收货，4:待评价，5:已完成，6:退换货
	private List<CustomizeEntity> mainLists; //数集

	public CustomizeEntity() {
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public List<CustomizeEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<CustomizeEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
