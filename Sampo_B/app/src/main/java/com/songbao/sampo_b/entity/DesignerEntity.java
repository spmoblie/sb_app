package com.songbao.sampo_b.entity;

/**
 * 设计师数据结构体
 */
public class DesignerEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	private int id;

	private String imgUrl; //设计师图片

	private String name; //设计师名称

	private String phone; //设计师电话

	private String info; //设计师简介

	private boolean isSelect; //选择状态

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

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}
}
