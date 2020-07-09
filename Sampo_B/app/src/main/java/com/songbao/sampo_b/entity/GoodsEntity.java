package com.songbao.sampo_b.entity;

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
	private String size; //产品尺寸
	private String color; //产品颜色
	private String style; //产品款式
	private String remarks; //产品备注
	private String effectUrl; //3D效果Url
	private int number; //商品数量
	private double onePrice; //申报价格
	private double twoPrice; //审核价格
	private boolean isPicture; //是否无图
	private ArrayList<String> labelList; //商品标签集
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

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getEffectUrl() {
		return effectUrl;
	}

	public void setEffectUrl(String effectUrl) {
		this.effectUrl = effectUrl;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getOnePrice() {
		return onePrice;
	}

	public void setOnePrice(double onePrice) {
		this.onePrice = onePrice;
	}

	public double getTwoPrice() {
		return twoPrice;
	}

	public void setTwoPrice(double twoPrice) {
		this.twoPrice = twoPrice;
	}

	public boolean isPicture() {
		return isPicture;
	}

	public void setPicture(boolean picture) {
		isPicture = picture;
	}

	public ArrayList<String> getLabelList() {
		return labelList;
	}

	public void setLabelList(ArrayList<String> labelList) {
		this.labelList = labelList;
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
