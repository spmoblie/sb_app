package com.songbao.sampo_b.entity;

public class WXEntity {
	
	private int errcode; 
	private String errmsg;
	private String access_token; // 接口调用凭证
	private String expires_in; // access_token接口调用凭证超时时间，单位（秒）
	private String refresh_token; // 用户刷新access_token
	private String openid; // 授权用户唯一标识
	private String scope; // 用户授权的作用域，使用逗号（,）分隔
	private String unionid; // 当且仅当已获得该用户的userinfo授权时，才会出现该字段

	public WXEntity(int errcode, String errmsg) {
		super();
		this.errcode = errcode;
		this.errmsg = errmsg;
	}

	public WXEntity(String access_token, String expires_in,
                    String refresh_token, String openid, String scope, String unionid) {
		super();

		this.access_token = access_token;
		this.expires_in = expires_in;
		this.refresh_token = refresh_token;
		this.openid = openid;
		this.scope = scope;
		this.unionid = unionid;
	}

	public WXEntity(String access_token, String expires_in,
                    String refresh_token, String openid, String scope) {
		super();

		this.access_token = access_token;
		this.expires_in = expires_in;
		this.refresh_token = refresh_token;
		this.openid = openid;
		this.scope = scope;
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

}
