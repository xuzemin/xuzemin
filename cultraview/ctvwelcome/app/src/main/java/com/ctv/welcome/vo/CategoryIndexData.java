
package com.ctv.welcome.vo;

import java.util.List;

public class CategoryIndexData extends IndexData {
    private List<CategoryContentData> contentData;

    private int id;

    public List<CategoryContentData> getContentData() {
        return this.contentData;
    }

    public void setContentDatas(List<CategoryContentData> contentData) {
        this.contentData = contentData;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
