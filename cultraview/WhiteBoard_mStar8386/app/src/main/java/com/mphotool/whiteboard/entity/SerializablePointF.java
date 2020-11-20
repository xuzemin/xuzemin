package com.mphotool.whiteboard.entity;

import android.graphics.PointF;

import com.mphotool.whiteboard.utils.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializablePointF extends PointF implements Serializable {
    private static final long serialVersionUID = Constants.serialVersionUID;

    public SerializablePointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.writeFloat(this.x);
        out.writeFloat(this.y);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        this.x = in.readFloat();
        this.y = in.readFloat();
    }
}
