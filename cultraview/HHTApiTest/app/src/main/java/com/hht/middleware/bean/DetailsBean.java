package com.hht.middleware.bean;

/**
 * Author: chenhu
 * Time: 2019/12/16 10:35
 * Description do somethings
 */
public class DetailsBean {
    private String title;
    private String type;

    public DetailsBean() {
    }

    public DetailsBean(String title) {
        this.title = title;
    }

    public DetailsBean(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
