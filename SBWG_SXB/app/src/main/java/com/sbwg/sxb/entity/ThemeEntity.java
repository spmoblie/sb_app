package com.sbwg.sxb.entity;

import java.util.List;

public class ThemeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private long endTime; //时效
	private String title; //标题
	private String link; //链接
	private String userName; //用户名称
	private String userHead; //用户头像
	private String imgUrl; //图片
	private String vdoUrl; //视频
	private String number; //报名人数
	private String explain; //活动说明
	private List<ThemeEntity> mainLists; //数集

	public ThemeEntity() {
		super();
	}

	public ThemeEntity(int errno, String errmsg) {
		super(errno, errmsg);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getVdoUrl() {
		return vdoUrl;
	}

	public void setVdoUrl(String vdoUrl) {
		this.vdoUrl = vdoUrl;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public List<ThemeEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<ThemeEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
