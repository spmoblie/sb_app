package com.songbao.sampo.widgets.area;

import android.os.Parcel;
import android.os.Parcelable;

import com.songbao.sampo.entity.BaseEntity;

import java.io.Serializable;

public class AreaEntity extends BaseEntity implements IndexDisplay, Parcelable, Serializable {

	private static final long serialVersionUID = 1L;

	String areaId; //索引Id
	String name; //显示名称
	String sign; //索引标识（A~Z）

	public AreaEntity() {
	}

	public AreaEntity(String areaId, String name, String sign) {
		this.areaId = areaId;
		this.name = name;
		this.sign = sign;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String getFirstCharIndex() {
		try {
			return getSign().substring(0, 1);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public String getValueToBeSort() {
		try {
			return getSign();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/*
	 * Parcelable part
	 */
	// example constructor that takes a Parcel and gives you an object populated
	// with it's values
	private AreaEntity(Parcel in) {
		areaId = in.readString();
		name = in.readString();
		sign = in.readString();
	}

	// write your object's data to the passed-in Parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(areaId);
		out.writeString(name);
		out.writeString(sign);
	}

	// this is used to regenerate your object. All Parcelables must have a
	// CREATOR that implements these two methods
	public static final Parcelable.Creator<AreaEntity> CREATOR = new Parcelable.Creator<AreaEntity>() {
		public AreaEntity createFromParcel(Parcel in) {
			return new AreaEntity(in);
		}

		public AreaEntity[] newArray(int size) {
			return new AreaEntity[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

}
