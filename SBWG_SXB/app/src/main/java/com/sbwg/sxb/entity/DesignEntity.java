package com.sbwg.sxb.entity;

import java.util.List;

public class DesignEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 设计图id
	 */
	private int id;

	/**
	 * 设计图名称
	 */
	private String name;
	
	/**
	 * 设计图Url
	 */
	private String imgUrl;

	/**
	 * 设计图集
	 */
	private List<DesignEntity> mainLists;

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public List<DesignEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<DesignEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
