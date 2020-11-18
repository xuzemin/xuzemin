
package com.ctv.settings.network.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.widget.TextView;
import com.ctv.settings.network.R;
import com.ctv.settings.utils.L;

/**
 * 蓝牙适配器
 * @author xuzemin
 * @date 2019/09/19
 */
public class BluetoothSimperAdapter extends BaseAdapter {
    private static final String TAG = "WirelessAdapter";

    private Context ctvContext;

    private List<BluetoothDevice> blueToothDeviceList;

    private List<BluetoothDevice> ConnectDeviceList = null;

    private ViewHolder mHolder = null;

    public void setConnectDeviceNull() {
        this.connectDevice = null;
    }

    public void setConnectDevice(BluetoothDevice connectDevice) {
        this.connectDevice = connectDevice;
    }

    private BluetoothDevice connectDevice = null;

    public BluetoothSimperAdapter(Context context,List<BluetoothDevice> blueToothDeviceList) {
        ctvContext = context;
        this.blueToothDeviceList = blueToothDeviceList;
    }

    public void setDeviceList(List<BluetoothDevice> blueToothDeviceList) {
        this.blueToothDeviceList = blueToothDeviceList;
    }

    public void setConnectDevice(List<BluetoothDevice> ConnectDeviceList) {
        this.ConnectDeviceList = ConnectDeviceList;
    }

    public void onExit() {
        ctvContext = null;
        if (blueToothDeviceList != null) {
            blueToothDeviceList.clear();
            blueToothDeviceList = null;
        }
        mHolder = null;
    }

    @Override
    public int getCount() {
        if(blueToothDeviceList == null){
            return 0;
        }
        return blueToothDeviceList.size();
    }

    @Override
    public Object getItem(int index) {
        return blueToothDeviceList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mHolder = null;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = LayoutInflater.from(ctvContext).inflate(R.layout.page_bluetooth_item,
                    parent, false);
            mHolder.bluetooth_name_tv = (TextView) convertView.findViewById(R.id.bluetooth_name_tv);
            mHolder.bluetooth_address_tv = (TextView)convertView.findViewById(R.id.bluetooth_address_tv);

            mHolder.bluetooth_connecting_tv = (TextView)convertView
                    .findViewById(R.id.bluetooth_connecting_tv);
            mHolder.bluetooth_type_img = (ImageView) convertView.findViewById(R.id.bluetooth_type_img);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        BluetoothDevice bluetoothDevice = blueToothDeviceList.get(position);
        mHolder.bluetooth_name_tv.setText(bluetoothDevice.getName());
        mHolder.bluetooth_address_tv.setText(bluetoothDevice.getAddress());

        L.e(TAG,"111"+bluetoothDevice.getBluetoothClass().getMajorDeviceClass());
        L.e(TAG,"111"+bluetoothDevice.getBluetoothClass().getDeviceClass());
        L.e(TAG,"222"+bluetoothDevice.getName());
        switch (bluetoothDevice.getBluetoothClass().getMajorDeviceClass()){
            case BluetoothClass.Device.Major.COMPUTER:
                mHolder.bluetooth_type_img.setImageResource(R.mipmap.computer);
                break;
            case BluetoothClass.Device.Major.PHONE:
                mHolder.bluetooth_type_img.setImageResource(R.mipmap.phone);
                break;
            case BluetoothClass.Device.Major.AUDIO_VIDEO://0x400 耳机
                mHolder.bluetooth_type_img.setImageResource(R.mipmap.ear);
                break;
            case BluetoothClass.Device.Major.PERIPHERAL://0x500 外设
                switch (bluetoothDevice.getBluetoothClass().getDeviceClass()) {
                    case 1408://0x580 鼠标//BluetoothClass.Device.PERIPHERAL_POINTING:
                        mHolder.bluetooth_type_img.setImageResource(R.mipmap.mouse);
                        break;

                    case 1344://0x540 键盘//BluetoothClass.Device.PERIPHERAL_KEYBOARD:
                        mHolder.bluetooth_type_img.setImageResource(R.mipmap.keyboard);
                        break;
                }
                break;
            case BluetoothClass.Device.Major.UNCATEGORIZED://0x1F00  未知设备
                mHolder.bluetooth_type_img.setImageResource(R.mipmap.unknown);
                break;
        }

        if(ConnectDeviceList != null && ConnectDeviceList.contains(bluetoothDevice)) {
            mHolder.bluetooth_connecting_tv.setText(ctvContext.getString(R.string.bluetooth_connected));
            mHolder.bluetooth_connecting_tv.setVisibility(View.VISIBLE);
        }else if(connectDevice != null && bluetoothDevice.getAddress().equals(connectDevice.getAddress())){
            mHolder.bluetooth_connecting_tv.setText(ctvContext.getString(R.string.bluetooth_connecting));
            mHolder.bluetooth_connecting_tv.setVisibility(View.VISIBLE);
        }else{
            mHolder.bluetooth_connecting_tv.setText("");
            mHolder.bluetooth_connecting_tv.setVisibility(View.GONE);
        }


//        mHolder.bluetooth_connecting_tv.setText(bluetoothDevice.getAddress());
        return convertView;
    }

    private class ViewHolder {
        TextView bluetooth_name_tv;

        TextView bluetooth_address_tv;

        TextView bluetooth_connecting_tv;

        ImageView bluetooth_type_img;

    }

}
