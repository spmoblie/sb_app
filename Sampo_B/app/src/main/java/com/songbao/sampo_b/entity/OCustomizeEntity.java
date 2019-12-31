package com.songbao.sampo_b.entity;

import java.util.ArrayList;

/**
 * 定制订单数据结构体
 */
public class OCustomizeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private String orderNo; //订单编号
	private String payType; //支付方式
	private String nodeTime1; //订单创建时间
	private String nodeTime2;
	private String nodeTime3;
	private String nodeTime4;
	private String nodeTime5;
	private String nodeTime6;
	private String nodeTime7;
	private String nodeTime8;
	private String nodeTime9;
	private String statusDesc; //状态描述
	private int status;
	private int nodeNo;  //1:提交预约，2:上门量尺，3:效果图，4:支付信息，5:收货信息，6:生产进度，7:产品发货，8:产品安装，9:订单完成
	private int cycle; //计划生产周期
	private double price; //订单报价
	private boolean isPayment; //是否确认支付
	private boolean isDesigns; //是否确认图片
	private boolean isReceipt; //是否确认收货
	private boolean isInstall; //是否确认安装
	private AddressEntity adEn; //收货地址
	private GoodsEntity gdEn; //定制商品信息
	private DesignerEntity dgEn; //设计师信息

	private ArrayList<String> imgList; //效果图图片地址集
	private ArrayList<OProgressEntity> opList; //生产进度数集
	private ArrayList<OLogisticsEntity> olList; //物流单号数集

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

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
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

	public String getNodeTime6() {
		return nodeTime6;
	}

	public void setNodeTime6(String nodeTime6) {
		this.nodeTime6 = nodeTime6;
	}

	public String getNodeTime7() {
		return nodeTime7;
	}

	public void setNodeTime7(String nodeTime7) {
		this.nodeTime7 = nodeTime7;
	}

	public String getNodeTime8() {
		return nodeTime8;
	}

	public void setNodeTime8(String nodeTime8) {
		this.nodeTime8 = nodeTime8;
	}

	public String getNodeTime9() {
		return nodeTime9;
	}

	public void setNodeTime9(String nodeTime9) {
		this.nodeTime9 = nodeTime9;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public int getNodeNo() {
		return nodeNo;
	}

	public void setNodeNo(int nodeNo) {
		this.nodeNo = nodeNo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isPayment() {
		return isPayment;
	}

	public void setPayment(boolean payment) {
		isPayment = payment;
	}

	public boolean isDesigns() {
		return isDesigns;
	}

	public void setDesigns(boolean designs) {
		isDesigns = designs;
	}

	public boolean isReceipt() {
		return isReceipt;
	}

	public void setReceipt(boolean receipt) {
		isReceipt = receipt;
	}

	public boolean isInstall() {
		return isInstall;
	}

	public void setInstall(boolean install) {
		isInstall = install;
	}

	public AddressEntity getAdEn() {
		return adEn;
	}

	public void setAdEn(AddressEntity adEn) {
		this.adEn = adEn;
	}

	public GoodsEntity getGdEn() {
		return gdEn;
	}

	public void setGdEn(GoodsEntity gdEn) {
		this.gdEn = gdEn;
	}

	public DesignerEntity getDgEn() {
		return dgEn;
	}

	public void setDgEn(DesignerEntity dgEn) {
		this.dgEn = dgEn;
	}

	public ArrayList<String> getImgList() {
		return imgList;
	}

	public void setImgList(ArrayList<String> imgList) {
		this.imgList = imgList;
	}

	public ArrayList<OProgressEntity> getOpList() {
		return opList;
	}

	public void setOpList(ArrayList<OProgressEntity> opList) {
		this.opList = opList;
	}

	public ArrayList<OLogisticsEntity> getOlList() {
		return olList;
	}

	public void setOlList(ArrayList<OLogisticsEntity> olList) {
		this.olList = olList;
	}
}
