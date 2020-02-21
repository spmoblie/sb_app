package com.songbao.sampo_b.entity;

import com.songbao.sampo_b.utils.StringUtil;

import java.util.ArrayList;

/**
 * 商品属性数据结构体
 */
public class GoodsAttrEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int attrId; //属性Id
	private String attrCode; //属性Code
	private String skuCode; //商品sku码
	private boolean isAdd; //是否添加购物车
	private boolean isShow; //属性面板是否展开
	private boolean isSelect; //属性是否选中
	private int skuNum; //商品库存数
	private int buyNum; //已选商品数
	private int s_id_1; //已选属性id_1
	private int s_id_2; //已选属性id_2
	private int s_id_3; //已选属性id_3
	private String s_name_1; //已选属性名称_1
	private String s_name_2; //已选属性名称_2
	private String s_name_3; //已选属性名称_3
	private String attrIdStr; //筛选属性Id字符串
	private String attrImg; //属性图片
	private String attrName; //属性名称
	private String attrNameStr; //已选属性名称
	private double attrPrice; //属性价值
	private ArrayList<GoodsAttrEntity> attrLists; //属性集合
	private String sku_key; //sku集合Key
	private GoodsAttrEntity sku_value; //sku集合Value
	private ArrayList<GoodsAttrEntity> skuLists; //库存集合


	@Override
	public String getEntityId() {
		return String.valueOf(attrId);
	}

	public int getAttrId() {
		return attrId;
	}

	public void setAttrId(int attrId) {
		this.attrId = attrId;
	}

	public String getAttrCode() {
		return attrCode;
	}

	public void setAttrCode(String attrCode) {
		this.attrCode = attrCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean add) {
		isAdd = add;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean show) {
		isShow = show;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public int getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(int skuNum) {
		this.skuNum = skuNum;
	}

	public int getBuyNum() {
		if (buyNum < 1) {
			buyNum = 1;
		}
		return buyNum;
	}

	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}

	public int getS_id_1() {
		return s_id_1;
	}

	public void setS_id_1(int s_id_1) {
		this.s_id_1 = s_id_1;
	}

	public int getS_id_2() {
		return s_id_2;
	}

	public void setS_id_2(int s_id_2) {
		this.s_id_2 = s_id_2;
	}

	public int getS_id_3() {
		return s_id_3;
	}

	public void setS_id_3(int s_id_3) {
		this.s_id_3 = s_id_3;
	}

	public String getS_name_1() {
		if (StringUtil.isNull(s_name_1)) {
			return "";
		}
		return s_name_1;
	}

	public void setS_name_1(String s_name_1) {
		this.s_name_1 = s_name_1;
	}

	public String getS_name_2() {
		if (StringUtil.isNull(s_name_2)) {
			return "";
		}
		return s_name_2;
	}

	public void setS_name_2(String s_name_2) {
		this.s_name_2 = s_name_2;
	}

	public String getS_name_3() {
		if (StringUtil.isNull(s_name_3)) {
			return "";
		}
		return s_name_3;
	}

	public void setS_name_3(String s_name_3) {
		this.s_name_3 = s_name_3;
	}

	public String getAttrNameStr() {
		return attrNameStr;
	}

	public void setAttrNameStr(String attrNameStr) {
		this.attrNameStr = attrNameStr;
	}

	public String getAttrIdStr() {
		if (StringUtil.isNull(attrIdStr)) {
			return "";
		}
		return attrIdStr;
	}

	public void setAttrIdStr(String attrIdStr) {
		this.attrIdStr = attrIdStr;
	}

	public String getAttrName() {
		return attrName;
	}


	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}


	public double getAttrPrice() {
		return attrPrice;
	}


	public void setAttrPrice(double attrPrice) {
		this.attrPrice = attrPrice;
	}


	public String getAttrImg() {
		return attrImg;
	}


	public void setAttrImg(String attrImg) {
		this.attrImg = attrImg;
	}


	public ArrayList<GoodsAttrEntity> getAttrLists() {
		return attrLists;
	}


	public void setAttrLists(ArrayList<GoodsAttrEntity> attrLists) {
		this.attrLists = attrLists;
	}


	public String getSku_key() {
		return sku_key;
	}


	public void setSku_key(String sku_key) {
		this.sku_key = sku_key;
	}

	public GoodsAttrEntity getSku_value() {
		return sku_value;
	}

	public void setSku_value(GoodsAttrEntity sku_value) {
		this.sku_value = sku_value;
	}

	public ArrayList<GoodsAttrEntity> getSkuLists() {
		return skuLists;
	}


	public void setSkuLists(ArrayList<GoodsAttrEntity> skuLists) {
		this.skuLists = skuLists;
	}
	
}
