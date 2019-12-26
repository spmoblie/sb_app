package com.songbao.sampo_b.entity;

/**
 * 课程预约日期时间段数据结构体
 */
public class OptionEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String date; //年月日
	private String time; //时间段
	private boolean isState; //是否可选
	private boolean isSelect; //是否已选
	private boolean isReserve; //预约与否

	public OptionEntity() {
		super();
	}

	public OptionEntity(int errno, String errmsg) {
		super(errno, errmsg);
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isState() {
		return isState;
	}

	public void setState(boolean state) {
		isState = state;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public boolean isReserve() {
		return isReserve;
	}

	public void setReserve(boolean reserve) {
		isReserve = reserve;
	}

}
