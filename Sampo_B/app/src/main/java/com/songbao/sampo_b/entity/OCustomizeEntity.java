package com.songbao.sampo_b.entity;

import java.util.ArrayList;

/**
 * 定制订单数据结构体
 */
public class OCustomizeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String orderNo; //订单编号
	private String termTime; //预计交期
	private String nodeTime1; //创建时间
	private String nodeTime2; //审核时间
	private String nodeTime3; //核价时间
	private String nodeTime4; //发货时间
	private String nodeTime5; //收货时间
	private String statusDesc; //状态描述
	private String orderRemarks; //订单备注
	private String checkRemarks; //审核备注
	private String priceRemarks; //核价备注
	private int status; //订单状态
	private double priceOne; //提交报价
	private double priceTwo; //修改报价
	private boolean isReceive; //是否确认收货
	private ArrayList<String> filesList; //文件备注集
	private ArrayList<String> imageList; //图片备注集
	private ArrayList<GoodsEntity> goodsList; //定制商品数集

	public OCustomizeEntity() {

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

	public String getTermTime() {
		return termTime;
	}

	public void setTermTime(String termTime) {
		this.termTime = termTime;
	}

	public String getNodeTime1() {
		return nodeTime1;
	}

	public void setNodeTime1(String nodeTime1) {
		this.nodeTime1 = nodeTime1;
	}

	public String getNodeTime2() {
		return nodeTime2;
	}

	public void setNodeTime2(String nodeTime2) {
		this.nodeTime2 = nodeTime2;
	}

	public String getNodeTime3() {
		return nodeTime3;
	}

	public void setNodeTime3(String nodeTime3) {
		this.nodeTime3 = nodeTime3;
	}

	public String getNodeTime4() {
		return nodeTime4;
	}

	public void setNodeTime4(String nodeTime4) {
		this.nodeTime4 = nodeTime4;
	}

	public String getNodeTime5() {
		return nodeTime5;
	}

	public void setNodeTime5(String nodeTime5) {
		this.nodeTime5 = nodeTime5;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getOrderRemarks() {
		return orderRemarks;
	}

	public void setOrderRemarks(String orderRemarks) {
		this.orderRemarks = orderRemarks;
	}

	public String getCheckRemarks() {
		return checkRemarks;
	}

	public void setCheckRemarks(String checkRemarks) {
		this.checkRemarks = checkRemarks;
	}

	public String getPriceRemarks() {
		return priceRemarks;
	}

	public void setPriceRemarks(String priceRemarks) {
		this.priceRemarks = priceRemarks;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getPriceOne() {
		return priceOne;
	}

	public void setPriceOne(double priceOne) {
		this.priceOne = priceOne;
	}

	public double getPriceTwo() {
		return priceTwo;
	}

	public void setPriceTwo(double priceTwo) {
		this.priceTwo = priceTwo;
	}

	public boolean isReceive() {
		return isReceive;
	}

	public void setReceive(boolean receive) {
		isReceive = receive;
	}

	public ArrayList<String> getFilesList() {
		return filesList;
	}

	public void setFilesList(ArrayList<String> filesList) {
		this.filesList = filesList;
	}

	public ArrayList<String> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<String> imageList) {
		this.imageList = imageList;
	}

	public ArrayList<GoodsEntity> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(ArrayList<GoodsEntity> goodsList) {
		this.goodsList = goodsList;
	}
}
