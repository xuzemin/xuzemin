package com.ctv.settings.about.Bean;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.ctv.settings.about.R;
import com.ctv.settings.about.ViewHolder.AboutViewHolder;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.DataTool;
import com.mstar.android.tvapi.common.vo.Constants;

import static com.ctv.settings.about.R.id.device_name_et;

public class DeviceNameDialog extends Dialog implements View.OnClickListener , View.OnFocusChangeListener {
    private static final String TAG = "DeviceNameDialog";

    private EditText device_name_et;

    private Button device_name_save;

    private Button device_name_cancel;
    private final Context context;
    private String deviceName;

    public DeviceNameDialog(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.divice_name);
        setWindowStyle();
        findViews();
    }
    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = context.getResources();
        Drawable drab = res.getDrawable(R.drawable.transparency_bg);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.y = (int) (-36 * scale + 0.5f);
        lp.width = (int) (680 * scale + 0.5f);
        lp.height = (int) (408 * scale + 0.5f);
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        w.setDimAmount(0.0f);
        w.setAttributes(lp);
    }
    private void findViews() {
        device_name_et = (EditText) findViewById(R.id.device_name_et);
        device_name_save = (Button) findViewById(R.id.device_name_save);
        device_name_cancel = (Button) findViewById(R.id.device_name_cancel);
        device_name_et.setOnFocusChangeListener(this);
        device_name_save.setOnFocusChangeListener(this);
        device_name_cancel.setOnFocusChangeListener(this);
        device_name_save.setOnClickListener(this);
        device_name_cancel.setOnClickListener(this);
        // String deviceName = (String)SPUtil.getData(ctvContext,
        // Constants.DEVICE_TITLE, "DavinciBoard");
        String deviceName = DataTool.getDeviceName(context);
        this.deviceName = deviceName;
        // DataTool.getDeviceName(ctvContext);
        device_name_et.setText(deviceName);
        device_name_et.selectAll();
    }
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.device_name_save) {
            saveDate();
        } else if (i == R.id.device_name_cancel) {
            dismiss();
        }

    }
    private void saveDate() {
        String name = device_name_et.getText().toString();
        if (TextUtils.isEmpty(name)) {
            device_name_et.setHint(R.string.correct_device_name);
            device_name_et.requestFocus();
        } else {
            if (!deviceName.equals(name)) {
                setDeviceName(name);
                Message msg = AboutViewHolder.aboutTvHandler.obtainMessage();
                msg.what = CommonConsts.DEVICE_NAME_CHANGE;
                msg.obj = name;
                AboutViewHolder.aboutTvHandler.sendMessage(msg);
            }
            dismiss();
        }
    }
    private void setDeviceName(String name) {
        DataTool.setDeviceName(context, name);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.setName(name);
        Settings.Global.putString(context.getContentResolver(),
                "wifi_p2p_device_name", name);
        // send Broadcast
        final Intent intent = new Intent(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intent.addFlags( 67108864);
        intent.setAction("android.settings.CTVDEVICE_NAME_CHANGE");
        intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE,
                new WifiP2pDevice(new WifiP2pDevice()));
        //context.sendStickyBroadcastAsUser(intent,  UserHandle.ALL);
    }
    @Override
    public void onFocusChange(View view, boolean b) {
        int i = view.getId();
        if (i == R.id.device_name_et) {
            if (b) {
                device_name_et.setSelected(true);
                device_name_et.selectAll();
            } else {
                device_name_et.setSelected(false);
            }
        } else if (i == R.id.device_name_save) {
            if (b) {
                device_name_save.setSelected(true);
            } else {
                device_name_save.setSelected(false);
            }
        } else if (i == R.id.device_name_cancel) {
            if (b) {
                device_name_cancel.setSelected(true);
            } else {
                device_name_cancel.setSelected(false);
            }
        }
    }
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
        AboutViewHolder.aboutTvHandler.sendEmptyMessage(CommonConsts.DIALOG_DISMISS);
    }

}
