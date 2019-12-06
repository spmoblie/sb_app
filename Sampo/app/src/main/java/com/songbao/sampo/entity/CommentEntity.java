package com.songbao.sampo.entity;

import java.util.ArrayList;

public class CommentEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String id;
	private String nick;
	private String headUrl;
	private String addTime;
	private String content;
	private String goodsAttr;
	private int type; //0:无图/1:有图
	private float starNum;
	private ArrayList<String> imgList;

	public CommentEntity() {
		
	}

	@Override
	public String getEntityId() {
		return id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getGoodsAttr() {
		return goodsAttr;
	}

	public void setGoodsAttr(String goodsAttr) {
		this.goodsAttr = goodsAttr;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getStarNum() {
		if (starNum < 1) {
			return 1;
		}
		return starNum;
	}

	public void setStarNum(float starNum) {
		this.starNum = starNum;
	}

	public ArrayList<String> getImgList() {
		return imgList;
	}

	public void setImgList(ArrayList<String> imgList) {
		this.imgList = imgList;
	}
}
