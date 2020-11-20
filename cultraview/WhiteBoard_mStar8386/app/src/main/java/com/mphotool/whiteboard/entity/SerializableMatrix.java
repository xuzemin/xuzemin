package com.mphotool.whiteboard.entity;

import android.graphics.Matrix;
import android.util.Log;

import com.mphotool.whiteboard.utils.BaseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class SerializableMatrix extends Matrix implements Serializable {

    public void writeObject(OutputStream out) throws IOException
    {
        float[] f = new float[9];
        getValues(f);

        for (int i = 0; i < 9; i++)
        {
            out.write(BaseUtils.floatToByte(f[i]));
            Log.d("SerializableMatrix","writeObject -- i=" + i + " , value = " + f[i]);
        }
    }

    public void readObject(InputStream in) throws IOException, ClassNotFoundException
    {
        float[] f = new float[9];
        byte[] b = new byte[4];
        for (int i = 0; i < 9; i++)
        {
            in.read(b);
            f[i] = BaseUtils.byteToFloat(b);
            Log.d("SerializableMatrix","readObject -- i=" + i + " , value = " + f[i]);
        }

        setValues(f);
    }
}
