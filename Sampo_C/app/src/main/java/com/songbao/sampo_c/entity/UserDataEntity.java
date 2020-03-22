package com.songbao.sampo_c.entity;


public class UserDataEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int messageNum; //用户信息数量
	private int cartGoodsNum; //购物车商品数量


	public UserDataEntity() {
		super();
	}

	public UserDataEntity(int errno, String errmsg) {
		super(errno, errmsg);
	}

	public int getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(int messageNum) {
		this.messageNum = messageNum;
	}

	public int getCartGoodsNum() {
		return cartGoodsNum;
	}

	public void setCartGoodsNum(int cartGoodsNum) {
		this.cartGoodsNum = cartGoodsNum;
	}
}
