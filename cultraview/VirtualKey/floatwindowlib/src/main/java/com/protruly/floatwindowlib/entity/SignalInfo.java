package com.protruly.floatwindowlib.entity;

/**
 * Desc:信号信息
 *
 * @author wang
 * @time 2017/7/18.
 */
public class SignalInfo {
    private Integer imageId;
    private String name;

    public SignalInfo(Integer imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
