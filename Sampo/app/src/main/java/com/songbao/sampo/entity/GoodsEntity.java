package com.songbao.sampo.entity;

/**
 * 商品数据结构体
 */
public class GoodsEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String goodsCode; //产品编码
	private String picUrl; //产品图片
	private String name; //产品名称
	private String attribute; //产品规格
	private String color; //产品颜色
	private String material; //产品用料
	private String veneer; //产品饰面
	private int number; //产品数量
	private int status; //0：无事件  1：评价+售后  2：追评+售后  3：售后  4：评价  5：追评
	private double price; //销售价格
	private GoodsAttrEntity attrEn; //已选属性值

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
}
