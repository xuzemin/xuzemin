package com.ctv.ctvlauncher.bean;

import com.ctv.ctvlauncher.compat.LauncherActivityInfoCompat;

import java.util.List;

public class AppPageBean {
    private int column;
    private List<LauncherActivityInfoCompat> compatList;
    private int end;
    private boolean isDirty = false;
    private int row;
    private int start;

    public AppPageBean(List<LauncherActivityInfoCompat> list){
        this.compatList =list;
    }
    public AppPageBean(List<LauncherActivityInfoCompat> list, int i, int i2, int i3, int i4) {
        this.row = i3;
        this.column = i4;
        this.compatList = list;
        this.start = i;
        this.end = i2;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public boolean isFull() {
        return this.end - this.start == this.row * this.column;
    }

    public void addCompat(LauncherActivityInfoCompat launcherActivityInfoCompat) {
        this.end++;
        this.isDirty = true;
    }

    public void removeCompat(LauncherActivityInfoCompat launcherActivityInfoCompat) {
        this.end--;
        this.isDirty = true;
    }

    public void dirty() {
        this.isDirty = true;
    }

    public void undirty() {
        this.isDirty = false;
    }

    public int getCount() {
        if (this.compatList == null) {
            return 0;
        }
        return this.end - this.start;
    }

    public LauncherActivityInfoCompat get(int i) {
        int i2 = this.start;
        i += i2;
        if (i < this.end) {
            if (i >= i2) {
                return (LauncherActivityInfoCompat) this.compatList.get(i);
            }
        }
        return null;
    }
}
