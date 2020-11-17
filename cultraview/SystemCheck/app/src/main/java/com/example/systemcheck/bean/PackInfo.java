package com.example.systemcheck.bean;

public class PackInfo {
    long cacheSize;
    long codeSize;
    long dataSize;
    long externalCacheSize;
    long externalCodeSize;
    long externalDataSize;
    String pakName;

    public String getPakName() {
        return this.pakName;
    }

    public void setPakName(String str) {
        this.pakName = str;
    }

    public long getCacheSize() {
        return this.cacheSize;
    }

    public void setCacheSize(long j) {
        this.cacheSize = j;
    }

    public long getCodeSize() {
        return this.codeSize;
    }

    public void setCodeSize(long j) {
        this.codeSize = j;
    }

    public long getDataSize() {
        return this.dataSize;
    }

    public void setDataSize(long j) {
        this.dataSize = j;
    }

    public long getExternalCacheSize() {
        return this.externalCacheSize;
    }

    public void setExternalCacheSize(long j) {
        this.externalCacheSize = j;
    }

    public long getExternalCodeSize() {
        return this.externalCodeSize;
    }

    public void setExternalCodeSize(long j) {
        this.externalCodeSize = j;
    }

    public long getExternalDataSize() {
        return this.externalDataSize;
    }

    public void setExternalDataSize(long j) {
        this.externalDataSize = j;
    }
}
