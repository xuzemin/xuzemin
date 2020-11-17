package com.hht.android.sdk.network;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2020/3/10 18:25
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/10 18:25
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class LinkConfig implements Parcelable {
    /** ip获取方式是dhcp还是静态设置 */
    public boolean isDhcp;
    /** ipv4 ip 地址 */
    public String ipAddress;
    /** ipv4 子网掩码 */
    public String netmask;
    /** ipv4 网关*/
    public String gateway;
    /** ipv4 dns1*/
    public String dns1;
    /** ipv4 dns2*/
    public String dns2;


    public LinkConfig() {

    }
    protected LinkConfig(Parcel in) {
        isDhcp = in.readByte() != 0;
        ipAddress = in.readString();
        netmask = in.readString();
        gateway = in.readString();
        dns1 = in.readString();
        dns2 = in.readString();
    }

    public static final Creator<LinkConfig> CREATOR = new Creator<LinkConfig>() {
        @Override
        public LinkConfig createFromParcel(Parcel in) {
            return new LinkConfig(in);
        }

        @Override
        public LinkConfig[] newArray(int size) {
            return new LinkConfig[size];
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
        dest.writeByte((byte) (isDhcp ? 1 : 0));
        dest.writeString(ipAddress);
        dest.writeString(netmask);
        dest.writeString(gateway);
        dest.writeString(dns1);
        dest.writeString(dns2);
    }

    public void readFromParcel(Parcel in) {
        isDhcp = in.readByte() != 0;
        ipAddress = in.readString();
        netmask = in.readString();
        gateway = in.readString();
        dns1 = in.readString();
        dns2 = in.readString();
    }

    @Override
    public String toString() {
        return "LinkConfig{" +
                "isDhcp=" + isDhcp +
                ", ipAddress='" + ipAddress + '\'' +
                ", netmask='" + netmask + '\'' +
                ", gateway='" + gateway + '\'' +
                ", dns1='" + dns1 + '\'' +
                ", dns2='" + dns2 + '\'' +
                '}';
    }
}
