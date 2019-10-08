package com.sbwg.sxb.entity;

public class MessageEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String id;
	private String title;
	private String content;
	private String time;
	private boolean isRead;

	public MessageEntity() {
		
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean read) {
		isRead = read;
	}
}
