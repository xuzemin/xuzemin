package com.android.jdrd.headcontrol.database;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class HeadControlBean {
    public  String name;
    public String phone;

    public int id;
    public String time01;
    public String time02;
    public String kaiguan;

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }

    public String getTime01() {
        return time01;
    }

    public String getTime02() {
        return time02;
    }

    public String getKaiguan() {
        return kaiguan;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime01(String time01) {
        this.time01 = time01;
    }

    public void setTime02(String time02) {
        this.time02 = time02;
    }

    public void setKaiguan(String kaiguan) {
        this.kaiguan = kaiguan;
    }

    public HeadControlBean(String time01, String time02, String kaiguan) {
        this.time01 = time01;
        this.time02 = time02;
        this.kaiguan = kaiguan;
    }

    public HeadControlBean() {
    }
}