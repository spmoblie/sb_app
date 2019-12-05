package com.songbao.sampo.entity;

import com.songbao.sampo.utils.StringUtil;

import java.util.ArrayList;

public class GoodsAttrEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int attrId; //属性id
	private int goodsId; //商品id
	private String firstImgUrl; //首张缩略图
	private double computePrice; //商品结算价
	private int skuNum; //商品库存数
	private boolean isShow; //属性面板是否展开
	private boolean isSelect; //属性是否选中
	private String attrIdStr; //筛选属性Id字符串
	private String attrName; //属性名称
	private double attrPrice; //属性价值
	private String attrImg; //属性图片
	private ArrayList<GoodsAttrEntity> attrLists; //属性集合
	private String sku_key; //库存集合Key
	private int sku_value; //库存集合Value
	private ArrayList<GoodsAttrEntity> skuLists; //库存集合


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

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean show) {
		isShow = show;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public String getAttrIdStr() {
		if (StringUtil.isNull(attrIdStr)) {
			return "";
		}
		return attrIdStr;
	}

	public void setAttrIdStr(String attrIdStr) {
		this.attrIdStr = attrIdStr;
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
