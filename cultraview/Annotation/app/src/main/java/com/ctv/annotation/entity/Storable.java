package com.ctv.annotation.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Storable {
    boolean readObject(InputStream inputStream) throws IOException, ClassNotFoundException;

    boolean writeObject(OutputStream outputStream) throws IOException;
}
