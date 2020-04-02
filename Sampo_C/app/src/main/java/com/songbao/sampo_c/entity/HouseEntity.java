package com.songbao.sampo_c.entity;

/**
 * 门店数据结构体
 */
public class HouseEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	private int id;

	private String imgUrl; //案例图片

	private String webUrl; //案例链接

	private String name; //案例标题

	private String info; //案例简介

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
