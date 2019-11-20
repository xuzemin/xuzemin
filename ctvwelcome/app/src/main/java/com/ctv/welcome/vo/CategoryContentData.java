
package com.ctv.welcome.vo;

public class CategoryContentData extends IndexData {
    private Object imageRes;

    private boolean isAddModule;

    private String mainTitleHtml;

    private float mainTitleX;

    private float mainTitleY;

    private String subTitle2Html;

    private float subTitle2X;

    private float subTitle2Y;

    private String subTitleHtml;

    private float subTitleX;

    private float subTitleY;

    private String text;

    public boolean isAddModule() {
        return this.isAddModule;
    }

    public void setAddModule(boolean addModule) {
        this.isAddModule = addModule;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getImageRes() {
        return this.imageRes;
    }

    public void setImageRes(Object imageRes) {
        this.imageRes = imageRes;
    }

    public String getMainTitleHtml() {
        return this.mainTitleHtml;
    }

    public void setMainTitleHtml(String mainTitleHtml) {
        this.mainTitleHtml = mainTitleHtml;
    }

    public String getSubTitleHtml() {
        return this.subTitleHtml;
    }

    public void setSubTitleHtml(String subTitleHtml) {
        this.subTitleHtml = subTitleHtml;
    }

    public float getMainTitleX() {
        return this.mainTitleX;
    }

    public void setMainTitleX(float mainTitleX) {
        this.mainTitleX = mainTitleX;
    }

    public float getMainTitleY() {
        return this.mainTitleY;
    }

    public void setMainTitleY(float mainTitleY) {
        this.mainTitleY = mainTitleY;
    }

    public float getSubTitleX() {
        return this.subTitleX;
    }

    public void setSubTitleX(float subTitleX) {
        this.subTitleX = subTitleX;
    }

    public float getSubTitleY() {
        return this.subTitleY;
    }

    public void setSubTitleY(float subTitleY) {
        this.subTitleY = subTitleY;
    }

    public float getSubTitle2Y() {
        return this.subTitle2Y;
    }

    public void setSubTitle2Y(float subTitle2Y) {
        this.subTitle2Y = subTitle2Y;
    }

    public float getSubTitle2X() {
        return this.subTitle2X;
    }

    public void setSubTitle2X(float subTitle2X) {
        this.subTitle2X = subTitle2X;
    }

    public String getSubTitle2Html() {
        return this.subTitle2Html;
    }

    public void setSubTitle2Html(String subTitle2Html) {
        this.subTitle2Html = subTitle2Html;
    }
}
