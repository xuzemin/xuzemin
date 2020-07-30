package com.protruly.floatwindowlib.entity;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Desc:信号信息
 *
 * @author wang
 * @time 2017/7/18.
 */
public class SignalInfo implements Comparable<SignalInfo>{
    private int xmlIndex; // xml中的索引
    private Integer sourceId; // CtvMenu中的索引
    private Integer imageId;
    private String resIdStr;
    private String name;

    private boolean isSelected = false;

    public SignalInfo(Integer sourceId, Integer imageId, String name) {
        this.sourceId = sourceId;
        this.imageId = imageId;
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull SignalInfo o) {
        if (this.xmlIndex >= o.xmlIndex){
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalInfo that = (SignalInfo) o;
        return xmlIndex == that.xmlIndex &&
                Objects.equals(sourceId, that.sourceId) &&
                Objects.equals(imageId, that.imageId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xmlIndex, sourceId, imageId, name);
    }

    public SignalInfo(Integer sourceId, String name) {
        this.sourceId = sourceId;
        this.name = name;
    }

    public int getXmlIndex() {
        return xmlIndex;
    }

    public void setXmlIndex(int xmlIndex) {
        this.xmlIndex = xmlIndex;
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

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getResIdStr() {
        return resIdStr;
    }

    public void setResIdStr(String resIdStr) {
        this.resIdStr = resIdStr;
    }

    @Override
    public String toString() {
        return "SignalInfo{" +
                "xmlIndex=" + xmlIndex +
                ", sourceId=" + sourceId +
                ", imageId=" + imageId +
                ", name='" + name + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
