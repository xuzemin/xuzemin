package com.android.jdrd.headcontrol.database;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class HeadControlBean {
    private int id;
    private String function;
    private int dataInt;
    private String dataString;

    public HeadControlBean() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setDataInt(int dataInt) {
        this.dataInt = dataInt;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public int getId() {
        return id;
    }

    public String getFunction() {
        return function;
    }

    public int getDataInt() {
        return dataInt;
    }

    public String getDataString() {
        return dataString;
    }
}