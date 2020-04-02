package com.songbao.sampo_c.entity;

/**
 * 门店数据结构体
 */
public class StoreEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	private int id;

	private String imgUrl; //门店图片

	private String name; //门店名称

	private String area; //门店区域

	private String phone; //门店电话

	private String info; //门店简介

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
