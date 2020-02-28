package com.songbao.sampo_c.entity;

import java.util.ArrayList;

public class CommentEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String nick; //用户昵称
	private String headUrl; //用户头像
	private String addTime; //创建时间
	private String content; //评论内容
	private String addContent; //追加内容
	private String orderNo; //关联订单
	private String goodsAttr; //商品属性
	private int number; //评论数量
	private int goodStar; //好评率
	private int addDay; //多少天后追加
	private float starNum; //评论星级
	private boolean isAdd; //是否可追加
	private boolean isImg; //是否有图片
	private GoodsEntity goodsEn; //关联商品
	private ArrayList<String> imgList; //评论图片

	public CommentEntity() {
		
	}

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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getGoodsAttr() {
		return goodsAttr;
	}

	public void setGoodsAttr(String goodsAttr) {
		this.goodsAttr = goodsAttr;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getGoodStar() {
		return goodStar;
	}

	public void setGoodStar(int goodStar) {
		this.goodStar = goodStar;
	}

	public int getAddDay() {
		return addDay;
	}

	public void setAddDay(int addDay) {
		this.addDay = addDay;
	}

	public float getStarNum() {
		if (starNum < 1) {
			return 5;
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

	public boolean isImg() {
		return isImg;
	}

	public void setImg(boolean img) {
		isImg = img;
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
