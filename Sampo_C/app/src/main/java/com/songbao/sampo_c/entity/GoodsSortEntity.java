package com.songbao.sampo_c.entity;

import java.util.List;

/**
 * 商品分类数据结构体
 */
public class GoodsSortEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	private int id;
	private int parentId; //父级id
	private String name; //分类名称
	private String sortCode; //分类编码
	private List<GoodsEntity> goodsLists; //商品数集
	private List<GoodsSortEntity> childLists; //子级分类


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

	public String getSortCode() {
		return sortCode;
	}

	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
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

	public List<GoodsSortEntity> getChildLists() {
		return childLists;
	}

	public void setChildLists(List<GoodsSortEntity> childLists) {
		this.childLists = childLists;
	}

}
