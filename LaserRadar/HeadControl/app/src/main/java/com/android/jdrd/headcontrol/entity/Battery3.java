package com.android.jdrd.headcontrol.entity;

/**
 * Created by Administrator on 2016/11/11 0011.
 */

public class Battery3 {
    /**
     * lowpower : 20
     */

    private DataBean data;
    /**
     * data : {"lowpower":20}
     * function : power
     * type : param
     */

    private String function;
    private String type;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class DataBean {
        private int lowpower;

        public int getLowpower() {
            return lowpower;
        }

        public void setLowpower(int lowpower) {
            this.lowpower = lowpower;
        }
    }

}
