package com.songbao.sampo.entity;

import java.util.ArrayList;

public class CommentEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String id;
	private String nick; //用户昵称
	private String headUrl; //用户头像
	private String addTime; //创建时间
	private String content; //评论内容
	private String addContent; //追加评论内容
	private String goodsAttr; //商品属性
	private int addDay; //多少天后追加评论
	private int type; //0:无图/1:有图
	private float starNum; //评论星级
	private boolean isAdd; //是否可追加评论
	private GoodsEntity goodsEn; //关联商品
	private ArrayList<String> imgList; //评论图片

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

	public String getAddContent() {
		return addContent;
	}

	public void setAddContent(String addContent) {
		this.addContent = addContent;
	}

	public String getGoodsAttr() {
		return goodsAttr;
	}

	public void setGoodsAttr(String goodsAttr) {
		this.goodsAttr = goodsAttr;
	}

	public int getAddDay() {
		return addDay;
	}

	public void setAddDay(int addDay) {
		this.addDay = addDay;
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

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean add) {
		isAdd = add;
	}

	public GoodsEntity getGoodsEn() {
		return goodsEn;
	}

	public void setGoodsEn(GoodsEntity goodsEn) {
		this.goodsEn = goodsEn;
	}

	public ArrayList<String> getImgList() {
		return imgList;
	}

	public void setImgList(ArrayList<String> imgList) {
		this.imgList = imgList;
	}
}
