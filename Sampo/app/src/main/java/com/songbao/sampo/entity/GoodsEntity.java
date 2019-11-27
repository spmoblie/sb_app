package com.songbao.sampo.entity;

import java.util.List;

public class GoodsEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String picUrl;
	private String name;
	private String attribute;
	private int number;
	private double price;
	private List<GoodsEntity> mainLists; //数集

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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public List<GoodsEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<GoodsEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
