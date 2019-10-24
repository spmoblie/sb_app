package com.songbao.sxb.entity;

public class DayFinish {

	int day;
	int all;
	int finish;

	public DayFinish(int day, int finish, int all) {
		this.day = day;
		this.all = all;
		this.finish = finish;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getAll() {
		return all;
	}

	public void setAll(int all) {
		this.all = all;
	}

	public int getFinish() {
		return finish;
	}

	public void setFinish(int finish) {
		this.finish = finish;
	}
}
