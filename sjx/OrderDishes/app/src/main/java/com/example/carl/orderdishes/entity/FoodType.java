package com.example.carl.orderdishes.entity;

public class FoodType {
	private int ftid;
	private String ftname;
	public FoodType() {
		super();
	}
	public FoodType(int ftid, String ftname) {
		super();
		this.ftid = ftid;
		this.ftname = ftname;
	}
	public int getFtid() {
		return ftid;
	}
	public void setFtid(int ftid) {
		this.ftid = ftid;
	}
	public String getFtname() {
		return ftname;
	}
	public void setFtname(String ftname) {
		this.ftname = ftname;
	}

	@Override
	public String toString() {
		return "FoodType{" +
				"ftid=" + ftid +
				", ftname='" + ftname + '\'' +
				'}';
	}
}
