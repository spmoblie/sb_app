package com.songbao.sampo_b.entity;

import java.util.ArrayList;

/**
 * 订单定制生产进度数据结构体
 */
public class OProgressEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String addTime;
	private String content;
	private int type; //0:无图/1:有图
	private ArrayList<String> imgList;

	public OProgressEntity() {
		
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<String> getImgList() {
		return imgList;
	}

	public void setImgList(ArrayList<String> imgList) {
		this.imgList = imgList;
	}
}
