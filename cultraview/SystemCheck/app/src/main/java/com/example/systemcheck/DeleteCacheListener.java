package com.example.systemcheck;

public interface DeleteCacheListener {
    void failed();

    void success(Long l);
}
