package com.example.carl.orderdishes.entity;

public class Desk {
	private int did;
	private String dname;
	private String dstatus;
	private String ddate;
	public Desk() {
		super();
	}
	public Desk(int did, String dname, String dstatus, String ddate) {
		super();
		this.did = did;
		this.dname = dname;
		this.dstatus = dstatus;
		this.ddate = ddate;
	}
	public int getDid() {
		return did;
	}
	public void setDid(int did) {
		this.did = did;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public String getDstatus() {
		return dstatus;
	}
	public void setDstatus(String dstatus) {
		this.dstatus = dstatus;
	}
	public String getDdate() {
		return ddate;
	}
	public void setDdate(String ddate) {
		this.ddate = ddate;
	}
}
