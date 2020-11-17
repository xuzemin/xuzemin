
package com.ctv.welcome.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class RichEditorInfo implements Parcelable {
    public static final Creator<RichEditorInfo> CREATOR = new Creator<RichEditorInfo>() {
        public RichEditorInfo createFromParcel(Parcel in) {
            return new RichEditorInfo(in);
        }

        public RichEditorInfo[] newArray(int size) {
            return new RichEditorInfo[size];
        }
    };

    private int curFontColor;

    private String curFontSize;

    private String curFontType;

    private String htmlText;

    private boolean isFirstEdit = true;

    private int lastFontColor;

    private String lastFontSize;

    private String lastFontType;

    private String text;

    private float x;

    private float y;
    public RichEditorInfo() {
    }
    public RichEditorInfo(float x, float y, String htmlText) {
        this.x = x;
        this.y = y;
        this.htmlText = htmlText;
    }

    protected RichEditorInfo(Parcel in) {
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.htmlText = in.readString();
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getHtmlText() {
        return this.htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
        parcel.writeString(this.htmlText);
    }

    public String getLastFontType() {
        return this.lastFontType;
    }

    public void setLastFontType(String lastFontType) {
        this.lastFontType = lastFontType;
    }

    public String getCurFontType() {
        return this.curFontType;
    }

    public void setCurFontType(String curFontType) {
        this.curFontType = curFontType;
    }

    public int getLastFontColor() {
        return this.lastFontColor;
    }

    public void setLastFontColor(int lastFontColor) {
        this.lastFontColor = lastFontColor;
    }

    public int getCurFontColor() {
        return this.curFontColor;
    }

    public void setCurFontColor(int curFontColor) {
        this.curFontColor = curFontColor;
    }

    public String getLastFontSize() {
        return this.lastFontSize;
    }

    public void setLastFontSize(String lastFontSize) {
        this.lastFontSize = lastFontSize;
    }

    public String getCurFontSize() {
        return this.curFontSize;
    }

    public void setCurFontSize(String curFontSize) {
        this.curFontSize = curFontSize;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFirstEdit() {
        return this.isFirstEdit;
    }

    public void setFirstEdit(boolean firstEdit) {
        this.isFirstEdit = firstEdit;
    }
}
