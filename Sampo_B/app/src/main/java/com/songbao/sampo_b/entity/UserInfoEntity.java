package com.songbao.sampo_b.entity;


public class UserInfoEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String userId; //用户Id
	private String shareId; //推广Id
	private String userName; //用户姓名
	private String userNick; //用户昵称
	private String userHead; //用户头像
	private String userIntro; //用户简介
	private String birthday; //用户生日
	private String userArea; //用户地区
	private String userEmail; //用户邮箱
	private String userPhone; //用户手机
	private String storeStr; //门店数据
	private String money; //账户余额
	private String coupon; //优惠券
	private String appToken; //登录授权码
	private String genderStr; //用户性别
	private int genderCode; //性别代码(0:保密/1:男/2:女)
	private int messageNum; //我的信息数量
	private int roleIds; //角色代码(7:门店设计师/8:工厂设计师/9:经销商/14:门店跟单)
	private long ratios; //经销商销售系数(默认值为1)

	public UserInfoEntity() {
		super();
	}

	public UserInfoEntity(int errno, String errmsg) {
		super(errno, errmsg);
	}

	@Override
	public String getEntityId() {
		return userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getUserHead() {
		return userHead;
	}

	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}

	public String getUserIntro() {
		return userIntro;
	}

	public void setUserIntro(String userIntro) {
		this.userIntro = userIntro;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getUserArea() {
		return userArea;
	}

	public void setUserArea(String userArea) {
		this.userArea = userArea;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getStoreStr() {
		return storeStr;
	}

	public void setStoreStr(String storeStr) {
		this.storeStr = storeStr;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getCoupon() {
		return coupon;
	}

	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}

	public String getGenderStr() {
		return genderStr;
	}

	public void setGenderStr(String genderStr) {
		this.genderStr = genderStr;
	}

	public int getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(int genderCode) {
		this.genderCode = genderCode;
	}

	public int getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(int messageNum) {
		this.messageNum = messageNum;
	}

	public int getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(int roleIds) {
		this.roleIds = roleIds;
	}

	public long getRatios() {
		return ratios;
	}

	public void setRatios(long ratios) {
		this.ratios = ratios;
	}
}
