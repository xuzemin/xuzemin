package com.ctv.annotation.element;

public class PointEntity {
    public PencilInk pencil;
    public int pencilId;
    public Point point;
    public int pointType = 0; //点的位置： 0.开始点   1.中间点  2.结束点

    public PointEntity(PencilInk pen, int id, Point p, int type){
        pencil = pen;
        pencilId = id;
        point = p;
        pointType = type;
    }
}
