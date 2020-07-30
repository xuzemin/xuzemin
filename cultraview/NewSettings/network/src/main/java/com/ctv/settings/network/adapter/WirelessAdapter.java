
package com.ctv.settings.network.adapter;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctv.settings.network.R;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.utils.Tools;


public class WirelessAdapter extends BaseAdapter {
    private static final String TAG = "WirelessAdapter";

    private Context ctvContext;

    private List<ScanResult> scanResults;

    private int mConnectState;

    private String mSsid;

    private ViewHolder mHolder = null;

    private static final int[][] WIFI_SIGNAL_IMG = {
            {
                    R.mipmap.wifi_state_pwd_1, R.mipmap.wifi_state_pwd_2,
                    R.mipmap.wifi_state_pwd_3, R.mipmap.wifi_state_pwd_4
            },
            {
                    R.mipmap.wifi_state_1, R.mipmap.wifi_state_2, R.mipmap.wifi_state_3,
                    R.mipmap.wifi_state_4
            }
    };

    public WirelessAdapter(Context context, List<ScanResult> scanResults) {
        ctvContext = context;
        this.scanResults = scanResults;
    }

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    // 0:disable ,1:connected ,2:connectting ,3:password error
    public void setConnectState(int state) {
        mConnectState = state;
        notifyDataSetChanged();
    }

    public int getmConnectState() {
        return mConnectState;
    }

    public void setmSsid(String mSsid) {
        // Log.i(TAG, "---mSsid:" + mSsid);
        if (!TextUtils.isEmpty(mSsid)) {
            this.mSsid = mSsid;
        }
    }

    public void updateTheTop(String mSsid) {
        this.mSsid = mSsid;
        // use the ssid to find the index in the list
        for (ScanResult sr : scanResults) {
            if (sr.SSID.equals(mSsid.replaceAll("\"", ""))) {
                scanResults.remove(sr);
                scanResults.add(0, sr);
                break;
            }

        }
    }

    public void onExit() {
        ctvContext = null;
        if (scanResults != null) {
            scanResults.clear();
            scanResults = null;
        }
        mHolder = null;
    }

    @Override
    public int getCount() {
        if(scanResults == null){
            return 0;
        }
        return scanResults.size();
    }

    @Override
    public Object getItem(int index) {
        return scanResults.get(index);
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
            convertView = LayoutInflater.from(ctvContext).inflate(R.layout.page_wireless_item,
                    parent, false);
            mHolder.wifi_name_tv = (TextView) convertView.findViewById(R.id.wifi_name_tv);
            mHolder.wifi_connecting_tv = (TextView) convertView
                    .findViewById(R.id.wifi_connecting_tv);
            mHolder.wifi_state_iv = (ImageView) convertView.findViewById(R.id.wifi_state_iv);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        ScanResult scanResult = scanResults.get(position);
        mHolder.wifi_name_tv.setText(scanResult.SSID);
        if (mSsid != null && scanResult.SSID.equals(mSsid.replaceAll("\"", ""))) {
            // Log.i(TAG, "mConnectState:" + mConnectState);
            if (mConnectState == 1) {
                mHolder.wifi_connecting_tv.setText(R.string.wifi_display_status_connected);
            } else if (mConnectState == 2) {
                mHolder.wifi_connecting_tv.setText(R.string.wifi_display_status_connecting);
            } else if (mConnectState == 3) {
                mHolder.wifi_connecting_tv.setText(R.string.password_error);
            } else {
                mHolder.wifi_connecting_tv.setText("");
            }
        } else {
            mHolder.wifi_connecting_tv.setText("");
        }
        int mSecurity = Tools.getSecurity(scanResult);
        int level = WifiManager.calculateSignalLevel(scanResult.level, 4);
        if (level == Integer.MAX_VALUE) {
            mHolder.wifi_state_iv.setImageDrawable(null);
        } else {
            mHolder.wifi_state_iv
                    .setImageResource(WIFI_SIGNAL_IMG[(mSecurity == NetUtils.SECURITY_NONE ? 1 : 0)][level]);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView wifi_name_tv;

        TextView wifi_connecting_tv;

        ImageView wifi_state_iv;
    }

}
