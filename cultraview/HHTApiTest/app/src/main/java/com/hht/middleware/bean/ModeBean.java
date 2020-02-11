package com.hht.middleware.bean;

/**
 * Author: chenhu
 * Time: 2019/12/18 13:47
 * Description do somethings
 */
public class ModeBean {

    private String modeName;
    private int typeInt;
    private String typeStr;

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public int getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(int typeInt) {
        this.typeInt = typeInt;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public ModeBean(String modeName, String typeStr) {
        this.modeName = modeName;
        this.typeStr = typeStr;
    }

    public ModeBean(String modeName, int typeInt) {
        this.modeName = modeName;
        this.typeInt = typeInt;
    }
}
