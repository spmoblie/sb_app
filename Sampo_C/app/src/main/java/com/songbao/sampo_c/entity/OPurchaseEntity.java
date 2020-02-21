package com.songbao.sampo_c.entity;

import java.util.List;

/**
 * 成品订单数据结构体
 */
public class OPurchaseEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String orderNo; //订单编号
	private String addTime; //创建时间
	private String sendTime; //发货时间
	private String doneTime; //完成时间
	private String payTime; //支付时间
	private String payNo; //支付交易号
	private String payType; //支付类型
	private double goodsPrice; //商品价格
	private double freightPrice; //物流运费
	private double discountPrice; //活动优惠
	private double totalPrice; //订单总计
	private int goodsNum; //商品总数
	private int status; //1:待付款，2:生产中，3:待收货，4:待评价，5:已完成，6:退换货
	private AddressEntity addEn; //收货地址
	private List<GoodsEntity> goodsLists; //数集

	public OPurchaseEntity() {
		super();
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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getDoneTime() {
		return doneTime;
	}

	public void setDoneTime(String doneTime) {
		this.doneTime = doneTime;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public double getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public double getFreightPrice() {
		return freightPrice;
	}

	public void setFreightPrice(double freightPrice) {
		this.freightPrice = freightPrice;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public AddressEntity getAddEn() {
		return addEn;
	}

	public void setAddEn(AddressEntity addEn) {
		this.addEn = addEn;
	}

	public List<GoodsEntity> getGoodsLists() {
		return goodsLists;
	}

	public void setGoodsLists(List<GoodsEntity> goodsLists) {
		this.goodsLists = goodsLists;
	}

}
