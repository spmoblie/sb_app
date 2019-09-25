package com.sbwg.sxb.entity;

import java.util.List;

public class ThemeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String title; //标题
	private String picUrl; //图片
	private String linkUrl; //链接
	private String userId; //用户Id
	private String userName; //用户名称
	private String userHead; //用户头像
	private String synopsis; //简介
	private String description; //活动说明
	private String address; //活动地址
	private String startTime; //活动开始时间
	private String endTime; //活动结束时间
	private int quantity; //限制报名数量
	private int people; //报名人数
	private int status; //1:报名中,2:已截止
	private double fees; //费用
	private UserInfoEntity userData; //报名信息
	private List<ThemeEntity> headLists; //首页头部数集
	private List<ThemeEntity> mainLists; //首页列表数集

	public ThemeEntity() {
		super();
	}

	public ThemeEntity(int errno, String errmsg) {
		super(errno, errmsg);
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

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserHead() {
		return userHead;
	}

	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getFees() {
		return fees;
	}

	public void setFees(double fees) {
		this.fees = fees;
	}

	public UserInfoEntity getUserData() {
		return userData;
	}

	public void setUserData(UserInfoEntity userData) {
		this.userData = userData;
	}

	public List<ThemeEntity> getHeadLists() {
		return headLists;
	}

	public void setHeadLists(List<ThemeEntity> headLists) {
		this.headLists = headLists;
	}

	public List<ThemeEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<ThemeEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
