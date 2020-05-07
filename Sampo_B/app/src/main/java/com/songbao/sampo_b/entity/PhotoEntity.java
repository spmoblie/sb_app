package com.songbao.sampo_b.entity;

import java.util.ArrayList;
import java.util.List;


public class PhotoEntity extends BaseEntity {
	
	private static final long serialVersionUID = 4438798104656417389L;
	
	private String name; //相册名称
	private String count; //数量
	private String photoUrl; //相片路径
	private int photoId; //相片Id
	private int firstId; //缩略图Id
	private boolean isSelect; //记录相片的选择状态
	private List<PhotoEntity> bitList = new ArrayList<>(); //打包集合


	public PhotoEntity() {
		super();
	}


	public PhotoEntity(int photoId, String photoUrl) {
		super();
		this.photoId = photoId;
		this.photoUrl = photoUrl;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCount() {
		return count;
	}


	public void setCount(String count) {
		this.count = count;
	}


	public int getPhotoId() {
		return photoId;
	}


	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}


	public String getPhotoUrl() {
		return photoUrl;
	}


	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}


	public int getFirstId() {
		return firstId;
	}


	public void setFirstId(int firstId) {
		this.firstId = firstId;
	}


	public boolean isSelect() {
		return isSelect;
	}


	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}


	public List<PhotoEntity> getBitList() {
		return bitList;
	}


	public void setBitList(List<PhotoEntity> bitList) {
		this.bitList = bitList;
	}

}
