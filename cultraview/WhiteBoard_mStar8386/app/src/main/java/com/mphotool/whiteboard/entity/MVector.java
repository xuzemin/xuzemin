package com.mphotool.whiteboard.entity;

import java.io.Serializable;

/**
 * @Description: 二维平面向量
 * @Author: wanghang
 * @CreateDate: 2019/10/23 20:04
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/23 20:04
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MVector implements Serializable {
    public float x;
    public float y;

    public MVector(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float dot(MVector v){
        return this.x * v.x + this.y * v.y;
    }

    public MVector sub(MVector v){
        return new MVector(this.x - v.x,this.y - v.y);
    }

    public MVector add(MVector v){
        return new MVector(this.x + v.x,this.y + v.y);
    }
}
