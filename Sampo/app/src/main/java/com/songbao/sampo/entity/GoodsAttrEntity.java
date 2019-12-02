package com.songbao.sampo.entity;

import java.util.ArrayList;

public class GoodsAttrEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 属性id
	 */
	private int attrId;

	/**
	 * 商品id
	 */
	private int goodsId;

	/**
	 * 首张缩略图
	 */
	private String firstImgUrl;

	/**
	 * 商品结算价
	 */
	private double computePrice;

	/**
	 * 商品库存数
	 */
	private int skuNum;

	/**
	 * 属性面板是否隐藏
	 */
	private boolean isGone;

	/**
	 * 商品属性名称
	 */
	private String attrName;

	/**
	 * 商品属性价值
	 */
	private double attrPrice;

	/**
	 * 商品属性图片
	 */
	private String attrImg;

	/**
	 * 属性集合
	 */
	private ArrayList<GoodsAttrEntity> attrLists;

	/**
	 * 商品库存集合Key
	 */
	private String sku_key;

	/**
	 * 商品库存集合Value
	 */
	private int sku_value;

	/**
	 * 商品库存集合
	 */
	private ArrayList<GoodsAttrEntity> skuLists;


	@Override
	public String getEntityId() {
		return String.valueOf(attrId);
	}

	public int getAttrId() {
		return attrId;
	}


	public void setAttrId(int attrId) {
		this.attrId = attrId;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public String getFirstImgUrl() {
		return firstImgUrl;
	}

	public void setFirstImgUrl(String firstImgUrl) {
		this.firstImgUrl = firstImgUrl;
	}

	public double getComputePrice() {
		return computePrice;
	}

	public void setComputePrice(double computePrice) {
		this.computePrice = computePrice;
	}

	public int getSkuNum() {
		return skuNum;
	}


	public void setSkuNum(int skuNum) {
		this.skuNum = skuNum;
	}

	public boolean isGone() {
		return isGone;
	}

	public void setGone(boolean gone) {
		isGone = gone;
	}

	public String getAttrName() {
		return attrName;
	}


	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}


	public double getAttrPrice() {
		return attrPrice;
	}


	public void setAttrPrice(double attrPrice) {
		this.attrPrice = attrPrice;
	}


	public String getAttrImg() {
		return attrImg;
	}


	public void setAttrImg(String attrImg) {
		this.attrImg = attrImg;
	}


	public ArrayList<GoodsAttrEntity> getAttrLists() {
		return attrLists;
	}


	public void setAttrLists(ArrayList<GoodsAttrEntity> attrLists) {
		this.attrLists = attrLists;
	}


	public String getSku_key() {
		return sku_key;
	}


	public void setSku_key(String sku_key) {
		this.sku_key = sku_key;
	}


	public int getSku_value() {
		return sku_value;
	}


	public void setSku_value(int sku_value) {
		this.sku_value = sku_value;
	}


	public ArrayList<GoodsAttrEntity> getSkuLists() {
		return skuLists;
	}


	public void setSkuLists(ArrayList<GoodsAttrEntity> skuLists) {
		this.skuLists = skuLists;
	}
	
}
