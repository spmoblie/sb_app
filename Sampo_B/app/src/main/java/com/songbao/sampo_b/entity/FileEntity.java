package com.songbao.sampo_b.entity;

public class FileEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String id;
	private String fileName; //文件名称
	private String fileUrl; //文件路径
	private long fileSize; //文件大小

	public FileEntity() {
		
	}

	@Override
	public String getEntityId() {
		return String.valueOf(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}
