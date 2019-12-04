package com.songbao.sampo.entity;

import java.util.List;

public class SortEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	private int id;

	/**
	 * 父级id
	 */
	private int parentId;

	/**
	 * 分类名称
	 */
	private String name;

	/**
	 * 分类图片
	 */
	private String picUrl;

	/**
	 * 商品数集
	 */
	private List<GoodsEntity> goodsLists;

	/**
	 * 子级分类
	 */
	private List<SortEntity> childLists;

	/**
	 * 打包数集
	 */
	private List<SortEntity> mainLists;


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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
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

	public List<GoodsEntity> getGoodsLists() {
		return goodsLists;
	}

	public void setGoodsLists(List<GoodsEntity> goodsLists) {
		this.goodsLists = goodsLists;
	}

	public List<SortEntity> getChildLists() {
		return childLists;
	}

	public void setChildLists(List<SortEntity> childLists) {
		this.childLists = childLists;
	}

	public List<SortEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<SortEntity> mainLists) {
		this.mainLists = mainLists;
	}
}