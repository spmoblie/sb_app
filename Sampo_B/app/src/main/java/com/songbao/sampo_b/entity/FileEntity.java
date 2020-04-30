package com.songbao.sampo_b.entity;

import java.util.ArrayList;
import java.util.List;


public class FileEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name; //文件名称
	private String fileUrl; //文件路径


	public FileEntity() {
		super();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
}
