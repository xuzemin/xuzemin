package com.example.carl.orderdishes.entity;

public class Food {
	private int fid;
	private String fname;
	private String ftname;
	private double fprice;
	private double fvipprice;
	private String fimgurl;
	private String fdescipt;
	public Food() {
		super();
	}

	@Override
	public String toString() {
		return "Food{" +
				"fid=" + fid +
				", fname='" + fname + '\'' +
				", ftname='" + ftname + '\'' +
				", fprice=" + fprice +
				", fvipprice=" + fvipprice +
				", fimgurl='" + fimgurl + '\'' +
				", fdescipt='" + fdescipt + '\'' +
				'}';
	}

	public Food(int fid, String fname, String ftname, double fprice,
				double fvipprice, String fimgurl, String fdescipt) {
		super();
		this.fid = fid;
		this.fname = fname;
		this.ftname = ftname;
		this.fprice = fprice;
		this.fvipprice = fvipprice;
		this.fimgurl = fimgurl;
		this.fdescipt = fdescipt;
	}
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getFtname() {
		return ftname;
	}
	public void setFtname(String ftname) {
		this.ftname = ftname;
	}
	public double getFprice() {
		return fprice;
	}
	public void setFprice(double fprice) {
		this.fprice = fprice;
	}
	public double getFvipprice() {
		return fvipprice;
	}
	public void setFvipprice(double fvipprice) {
		this.fvipprice = fvipprice;
	}
	public String getFimgurl() {
		return fimgurl;
	}
	public void setFimgurl(String fimgurl) {
		this.fimgurl = fimgurl;
	}
	public String getFdescipt() {
		return fdescipt;
	}
	public void setFdescipt(String fdescipt) {
		this.fdescipt = fdescipt;
	}
	
	
}
