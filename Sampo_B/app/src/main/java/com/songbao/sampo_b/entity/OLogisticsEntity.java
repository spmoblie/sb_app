package com.songbao.sampo_b.entity;

/**
 * 订单物流单号数据结构体
 */
public class OLogisticsEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name; //物流公司名称
	private String number; //物流单号
	private String orderNo; //订单编码

	public OLogisticsEntity() {
		
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
}
