package com.songbao.sampo_b.entity;

public class AddressEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name; //联系人
	private String phone; //手机
	private String email; //邮箱
	private int countryId; //国入库Id
	private String country; //国
	private int provinceId; //省入库Id
	private String province; //省
	private int cityId; //市入库Id
	private String city; //市
	private int districtId; //区入库Id
	private String district; //区
	private String editAdd; //编辑地址
	private String address; //详细地址
	private boolean isSelect; //是否选择
	private boolean isDefault; //是否默认

	
	public AddressEntity() {
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

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public int getCountryId() {
		return countryId;
	}


	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	

	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public int getProvinceId() {
		return provinceId;
	}


	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}


	public String getProvince() {
		return province;
	}


	public void setProvince(String province) {
		this.province = province;
	}


	public int getCityId() {
		return cityId;
	}


	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	
	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public int getDistrictId() {
		return districtId;
	}


	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}
	

	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	public String getEditAdd() {
		return editAdd;
	}


	public void setEditAdd(String editAdd) {
		this.editAdd = editAdd;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean aDefault) {
		isDefault = aDefault;
	}
}
