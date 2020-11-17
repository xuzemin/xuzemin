package com.hht.android.sdk.time.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2020/1/10 11:10
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/1/10 11:10
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TimeUtil implements Parcelable {
    public int id;
    public boolean enable;
    public int hour;
    public int min;
    public List<String> week = new ArrayList<>();

    public enum EnumWeek {
        SUN,
        MON,
        TUE,
        WED,
        THU,
        FRI,
        SAT
    }
    public TimeUtil() {

    }

    public TimeUtil(List<String> w, int h, int m) {
        this.id = 0;
        this.enable = true;
        if (w != null){
            this.week = w;
        }
        this.hour = h;
        this.min = m;
    }

    public TimeUtil(int index, boolean _enable, List<String> w, int h, int m) {
        this.id = index;
        this.enable = _enable;
        if (w != null){
            this.week = w;
        }
        this.hour = h;
        this.min = m;
    }

    protected TimeUtil(Parcel in) {
        this.id = in.readInt();
        this.enable = in.readByte() != 0;
        this.hour = in.readInt();
        this.min = in.readInt();
        this.week = in.createStringArrayList();
    }

    public static final Creator<TimeUtil> CREATOR = new Creator<TimeUtil>() {
        @Override
        public TimeUtil createFromParcel(Parcel in) {
            return new TimeUtil(in);
        }

        @Override
        public TimeUtil[] newArray(int size) {
            return new TimeUtil[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.hour);
        dest.writeInt(this.min);
        dest.writeStringList(this.week);
    }

    public void readFromParcel(Parcel in) {
        boolean z = true;
        this.id = in.readInt();
        if (in.readInt() != 1) {
            z = false;
        }
        this.enable = z;
        in.readStringList(this.week);
        this.hour = in.readInt();
        this.min = in.readInt();
    }

    @Override
    public String toString() {
        return "TimeUtil{" +
                "id=" + id +
                ", enable=" + enable +
                ", hour=" + hour +
                ", min=" + min +
                ", week=" + week +
                '}';
    }
}
