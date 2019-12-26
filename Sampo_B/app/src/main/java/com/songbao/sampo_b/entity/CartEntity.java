package com.songbao.sampo_b.entity;

/**
 * 购物车列表数据结构体
 */
public class CartEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private boolean isSelect; //是否选中
	private GoodsEntity goodsEn; //商品实体

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

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public GoodsEntity getGoodsEn() {
		return goodsEn;
	}

	public void setGoodsEn(GoodsEntity goodsEn) {
		this.goodsEn = goodsEn;
	}
}
