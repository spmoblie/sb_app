package com.songbao.sampo_c.entity;

import java.util.ArrayList;

/**
 * 商品数据结构体
 */
public class GoodsEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id; //商品主键
	private String goodsCode; //商品编码
	private String skuCode; //商品sku码
	private String picUrl; //商品图片
	private String name; //商品名称
	private String attribute; //商品规格
	private String color; //商品颜色
	private String material; //商品用料
	private String veneer; //商品饰面
	private int number; //商品数量
	private int saleStatus; //0:未售后/1:退款中/2:已退款/3:换货中/4:已换货
	private int commentStatus; //0:未评价/1:已评价/2:已追评
	private double price; //销售价格
	private GoodsAttrEntity attrEn; //已选属性值
	private GoodsSaleEntity saleEn; //商品售后信息
	private ArrayList<String> imageList; //商品图片集
	private ArrayList<String> detailList; //详情图片集

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

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
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

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getVeneer() {
		return veneer;
	}

	public void setVeneer(String veneer) {
		this.veneer = veneer;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(int saleStatus) {
		this.saleStatus = saleStatus;
	}

	public int getCommentStatus() {
		return commentStatus;
	}

	public void setCommentStatus(int commentStatus) {
		this.commentStatus = commentStatus;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public GoodsAttrEntity getAttrEn() {
		return attrEn;
	}

	public void setAttrEn(GoodsAttrEntity attrEn) {
		this.attrEn = attrEn;
	}

	public GoodsSaleEntity getSaleEn() {
		return saleEn;
	}

	public void setSaleEn(GoodsSaleEntity saleEn) {
		this.saleEn = saleEn;
	}

	public ArrayList<String> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<String> imageList) {
		this.imageList = imageList;
	}

	public ArrayList<String> getDetailList() {
		return detailList;
	}

	public void setDetailList(ArrayList<String> detailList) {
		this.detailList = detailList;
	}
}
