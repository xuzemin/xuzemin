
package com.ctv.settings.network.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothInputDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.MapProfile;
import com.android.settingslib.bluetooth.PbapServerProfile;
import com.ctv.settings.R;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.network.adapter.BluetoothSimperAdapter;
import com.ctv.settings.network.utils.BluetoothUtils;
import com.ctv.settings.network.utils.ClsUtils;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.view.BluetoothListView;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.T;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 蓝牙模块
 * @author xuzemin
 * @date 2019/09/19
 */

public class BluetoothViewHolder extends BaseViewHolder implements OnFocusChangeListener, View.OnClickListener {

	private static final String TAG = "BluetoothViewHolder";

	private ProgressDialog mDialog = null;

	private BluetoothSimperAdapter bluetoothSimperAdapter = null;

	private BluetoothSimperAdapter bluetoothSimperAdapter_paired = null;

	private FrameLayout Bluetooth_switch_fl = null;//,tooth_discover_time_fl= null;

	private ImageView Bluetooth_switch_iv = null;

	private Context ctvContext;

	private UUID BTUUID = null;

	private BluetoothSocket mBluetoothSocket;

	private BluetoothListView bluetooth_lv = null;   //发现蓝牙设备 listview

	private BluetoothListView bluetooth_pair_lv = null;   //已匹配蓝牙设备 listview

	private RelativeLayout bluetooth_pb = null;

	private BluetoothAdapter mBluetoothAdapter = null;

	private List<BluetoothDevice> blueToothDeviceList = null;  //发现蓝牙列表

	private List<BluetoothDevice> blueToothDeviceList_paired = null;  //已配对蓝牙列表

	private BluetoothDevice connectDevice = null;    // 当前连接设备

	private boolean isOpenBluetooth = false;

	protected Resources resources;

	private final static int THREAD_WAIT = 500;    //蓝牙状态监听延时

	private final static int BLUETOOTH_STATE = 0;  //蓝牙状态

	private final static int BLUETOOTH_CONNECT = 1;  //蓝牙连接

	private final static int BLUETOOTH_SERVER = 2;  //蓝牙连接

	private final static int BLUETOOTH_OPEN = 3;  //蓝牙连接

	private int currentState = 0;

	private boolean isConnecting = false;

	private LinearLayout ll_on_all = null,ll_no_device_scan = null;

	private LinearLayout ll_bluetooth_lv = null;

	private LinearLayout ll_pair_lv = null;

	private LinearLayout back_bluetooth = null;

	private static BluetoothA2dp mA2dpProfile = null;

	private static BluetoothHeadset headsetProfile = null;

	private static BluetoothInputDevice bluetoothInputDevice = null;

	private static final int REFRESHH_GET_TIME = 2;  //蓝牙配对连接设备刷新

	private static final int REFRESHH_SHOW_TIME = 4;  //蓝牙连接弹窗

	private static final int REFRESHH_DISCOVERY = 600;  //蓝牙可发现

	private static int time = 10;

	private static int discovertime = 0;

	private static int showtime = 0;

	private static boolean isInit = false;

	private CachedBluetoothDevice mCachedDevice;

	private List<BluetoothDevice> hasDevices = null;    //已连接设备

	private BluetoothProfile.ServiceListener listener_Profile = new BluetoothProfile.ServiceListener() {
		@Override
		public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
			L.d("position = "+i);
			L.d("position = "+bluetoothProfile.toString());
			switch (i){
				//外设输入设备
				case NetUtils.DEVICE_INPUT_DEVICE:
					L.d(TAG,"DEVICE_INPUT_DEVICE");
					bluetoothInputDevice = (BluetoothInputDevice) bluetoothProfile;
					if(bluetoothInputDevice.connect(connectDevice)){
						L.e(TAG,"bluetoothInputDevice connect true");
					}else{
						L.e(TAG,"bluetoothInputDevice connect false");
					}
					break;
				//音频耳机设备
				case BluetoothProfile.A2DP:
					L.d(TAG,"A2DP");
					mA2dpProfile = (BluetoothA2dp) bluetoothProfile;
					try {
						ClsUtils.connect_A2dp(mA2dpProfile.getClass(),mA2dpProfile,connectDevice);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case BluetoothProfile.HEADSET:
					L.d(TAG,"HEADSET");
					headsetProfile = (BluetoothHeadset) bluetoothProfile;
					try {
						L.d(TAG,"connect"+ClsUtils.connect_Headset(headsetProfile.getClass(),headsetProfile,connectDevice));
//                        connectDevice.getUuids();
					} catch (Exception e) {
						L.d(TAG,"e"+e.toString());
						e.printStackTrace();
					}
					break;
			}
			isConnecting = false;
			connectDevice = null;
			checkConnectDevice();
			connectDevice = null;
			if(bluetoothSimperAdapter != null) {
				bluetoothSimperAdapter.setConnectDeviceNull();
				bluetoothSimperAdapter.notifyDataSetChanged();
			}
			if(bluetoothSimperAdapter_paired != null) {
				bluetoothSimperAdapter_paired.setConnectDeviceNull();
				bluetoothSimperAdapter_paired.notifyDataSetChanged();
			}

		}

		@Override
		public void onServiceDisconnected(int i) {
		}
	};

	private final BroadcastReceiver mReceiver = new BroadcastReceiver(){

		public void onReceive(Context context,Intent intent){

			String action = intent.getAction();
			Bundle b = intent.getExtras();
			BluetoothDevice device;
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				boolean ishave;
				int typeid = device.getBluetoothClass().getMajorDeviceClass();
				switch (typeid){
					case BluetoothClass.Device.Major.AUDIO_VIDEO:
					case BluetoothClass.Device.Major.PERIPHERAL:
					case BluetoothClass.Device.Major.UNCATEGORIZED:
//					case BluetoothClass.Device.Major.COMPUTER:
//					case BluetoothClass.Device.Major.PHONE:
						ishave = false;
						break;
					default:
						ishave = true;
						break;
				}
				if(!ishave&&blueToothDeviceList_paired !=null && blueToothDeviceList_paired.size() > 0){
					for(int i = 0,length = blueToothDeviceList_paired.size();i<length;i++){
						BluetoothDevice pairedDevice = blueToothDeviceList_paired.get(i);
						if(device.getAddress().equals(pairedDevice.getAddress())){
							ishave = true;
							break;
						}
					}
				}
				if(!ishave){
					if(blueToothDeviceList !=null && blueToothDeviceList.size() > 0){
						for(int i = 0,length = blueToothDeviceList.size();i<length;i++){
							BluetoothDevice hadDevice = blueToothDeviceList.get(i);
							if(device.getAddress().equals(hadDevice.getAddress())){
								ishave = true;
								break;
							}
						}
					}else{
						blueToothDeviceList = new ArrayList<>();
					}
				}
				if(!ishave){
					if(device.getName()!=null && !device.getName().equals("")) {
						blueToothDeviceList.add(device);
						bluetoothSimperAdapter.setDeviceList(blueToothDeviceList);
						bluetoothSimperAdapter.notifyDataSetChanged();
						setListViewHeightBasedOnChildren();
					}
				}

			}else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
					case BluetoothDevice.BOND_BONDING://正在配对
						L.d(TAG, "正在配对......");
						bluetoothSimperAdapter.setConnectDevice(connectDevice);
						break;
					case BluetoothDevice.BOND_BONDED://配对结束
						L.d(TAG, "完成配对");
						if (mDialog != null) {
							mDialog.cancel();
						}
						isConnecting = true;
						mBluetoothAdapter.cancelDiscovery();
						switch (device.getBluetoothClass().getMajorDeviceClass()){
							case BluetoothClass.Device.Major.AUDIO_VIDEO:
								L.e(TAG,"this is a Audio");
								device.createBond();
								mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
										BluetoothProfile.A2DP);
								break;
							case BluetoothClass.Device.Major.PERIPHERAL:
								L.e(TAG,"this is a Input");
								device.createBond();
								mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
										NetUtils.DEVICE_INPUT_DEVICE);
								break;
						}
						checkConnectDevice();
						bluetoothSimperAdapter.setConnectDeviceNull();
						break;
					case BluetoothDevice.BOND_NONE://取消配对/未配对
						L.d(TAG, "取消配对");
						if (mDialog != null) {
							mDialog.cancel();
						}
						bluetoothSimperAdapter.setConnectDeviceNull();
					default:
						break;
				}
			}else if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
				BluetoothDevice connect_Device =
						intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				abortBroadcast();
				L.d(TAG, "pair"+connect_Device.setPairingConfirmation(true));
				//				connect_Device.cancelPairingUserInput();
				int mType = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
				L.d(TAG, "pair mType"+mType);
				boolean ret;
				int pairingKey;
				switch (mType) {
					case BluetoothDevice.PAIRING_VARIANT_PIN:
						pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
								BluetoothDevice.ERROR);
						byte[] pinBytes = String.format(Locale.US, "%06d", pairingKey).getBytes();// BluetoothDevice.convertPinToBytes(value);
//						if (pinBytes == null) {
//							return;
//						}
						if (pinBytes == null) {
							return;
						}
						// 直接调用setPin方法,然后就没有了，等到收到状态改变的广播后就进行dismiss,请看54行的mReceiver
						connect_Device.setPin(pinBytes);
//						break;
					case 1://BluetoothDevice.PAIRING_VARIANT_PASSKEY:
//						pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
//								BluetoothDevice.ERROR);
////						int passkey = Integer.parseInt(value);
////						connect_Device.setPasskey(pairingKey);
//						break;
					case BluetoothDevice.DEVICE_TYPE_DUAL:
					case 6:
					case 7:
						pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
								BluetoothDevice.ERROR);
						L.e(TAG,"pairingKey"+pairingKey);
						try {
							if(pairingKey != 0){
								connect_Device.setPin(String.format(Locale.US, "%06d", pairingKey).getBytes());
//								ClsUtils.setPin(connect_Device.getClass(), connect_Device, String.format(Locale.US, "%06d", pairingKey));
							}else {
								connect_Device.setPin("0000".getBytes());
//								ClsUtils.setPin(connect_Device.getClass(), connect_Device, "0000");
							}
							ret = true;
						} catch (Exception e) {
							e.printStackTrace();
							ret = false;
						}
						break;
					case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:

						pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
								BluetoothDevice.ERROR);
						String mPairingKey2 = String.format(Locale.US, "%06d", pairingKey);
						if (pairingKey == BluetoothDevice.ERROR) {
							L.e(TAG, "Invalid Confirmation Passkey received, not showing any dialog");
							return;
						}
						ret = false;
						L.d(TAG, "pair ret"+ret);
						T.showShort(ctvContext,"mPairingKey"+mPairingKey2);
						connect_Device.setPin(mPairingKey2.getBytes());
						connect_Device.setPairingConfirmation(true);
//						onPair(mPairingView.getText().toString());
//						ShowInputDeviceDialog(mPairingKey2);
						break;
					case 4://BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
					case 5:
						pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
								BluetoothDevice.ERROR);
						String mPairingKey = String.format(Locale.US, "%06d", pairingKey);
						ret = false;
						L.d(TAG, "pair ret"+ret);
						T.showShort(ctvContext,"mPairingKey"+mPairingKey);
						ShowInputDeviceDialog(mPairingKey);
						break;

					default:
						try {
							ret =ClsUtils.setPin(connect_Device.getClass(), connect_Device, "0000");
						} catch (Exception e) {
							e.printStackTrace();
							ret = false;
						}
						break;
				}

				if(ret){
					connect_Device.createBond();
					connectDevice = connect_Device;
					mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
							BluetoothProfile.A2DP);
					mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
							NetUtils.DEVICE_INPUT_DEVICE);
				}
			}
		}
	};

	/**
	 * 蓝牙状态监听
	 */
	@SuppressLint("HandlerLeak")
	private final Handler bluetoothHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case BLUETOOTH_STATE:
					if (mBluetoothAdapter == null) {
						mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					} else {
						currentState = mBluetoothAdapter.getState();
						switch (currentState) {
							case 10:  //蓝牙未开启
								if (mDialog != null) {
									mDialog.cancel();
								}
								isConnecting = false;
								if (mBluetoothAdapter.isDiscovering()) {
									mBluetoothAdapter.cancelDiscovery();
								}
								blueToothDeviceList.clear();
								blueToothDeviceList_paired.clear();
								connectDevice = null;
								mA2dpProfile = null;
								bluetoothInputDevice = null;
								break;
							case 11:  //蓝牙正在开启
								if (mDialog != null) {
									mDialog.cancel();
								}
								isConnecting = false;
								break;
							case 12:   //蓝牙已开启
								L.e(TAG, "bluetooth is isConnecting" + isConnecting);
								if (isOpenBluetooth) {
									if (!isConnecting) {
										if (!mBluetoothAdapter.isDiscovering()) {
											if (mBluetoothAdapter.startDiscovery()) {
												L.e(TAG, "bluetooth is startDiscovery");
											} else {
												L.e(TAG, "bluetooth is not startDiscovery");
											}
										}
									}
								}
								break;
						}
						if (isOpenBluetooth) {
							if (time < REFRESHH_GET_TIME) {
								time++;
							} else {
								time = 0;
								checkConnectDevice();
								getPairedDevices();
							}
						}
					}
					refreshUI(ll_on_all);
					refreshUI(bluetooth_pb);
					refreshUI(Bluetooth_switch_iv);
					refreshUI(ll_bluetooth_lv);
					refreshUI(ll_pair_lv);
					bluetoothHandler.sendEmptyMessageDelayed(BLUETOOTH_STATE, THREAD_WAIT);
					break;
				case BLUETOOTH_CONNECT:
					if (showtime < REFRESHH_SHOW_TIME) {
						showtime++;
						bluetoothHandler.sendEmptyMessageDelayed(BLUETOOTH_CONNECT, THREAD_WAIT);
					} else {
						showtime = 0;
						if (mDialog != null) {
							mDialog.dismiss();
							mDialog.cancel();
							mDialog = null;
						}
					}
				case BLUETOOTH_SERVER:
					if (BTUUID == null) {
						BTUUID = new UUID(111, 1111);
					}
					break;
				case BLUETOOTH_OPEN:
					mBluetoothAdapter.enable();
					mBluetoothAdapter.setName("Android BT");
					try {
						mBluetoothAdapter.getClass().getMethod("setScanMode",int.class).invoke(mBluetoothAdapter,BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
						mBluetoothAdapter.getClass().getMethod("setScanMode",int.class,int.class).invoke(mBluetoothAdapter,BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,30*1000);
					} catch (Exception e) {
						L.d("setScanMode"+e.toString());
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
	};


	public BluetoothViewHolder(Activity activity) {
		super(activity);
		// 注册事件
		initUI(activity);
		initData(activity);
		initListener();
	}


	/**
	 检测已连接蓝牙设备
	 */
	private void checkConnectDevice(){
		hasDevices = new ArrayList<>();
		List<BluetoothDevice> hasDevices_bak;
		boolean isconnect = false;

		if(mA2dpProfile !=null) {
			hasDevices_bak = mA2dpProfile.getConnectedDevices();
			for (BluetoothDevice bluedevice : hasDevices_bak) {
				L.e(TAG, "mA2dpProfile connectedDevices" + bluedevice.getName());
				hasDevices.add(bluedevice);
				if(connectDevice!=null) {
					if (bluedevice.getAddress().equals(connectDevice.getAddress())) {
						isconnect = true;
					}
				}
			}
		}


		if(bluetoothInputDevice !=null) {
			hasDevices_bak = bluetoothInputDevice.getConnectedDevices();
			for (BluetoothDevice bluedevice : hasDevices_bak) {
				L.e(TAG, "bluetoothInputDevice connectedDevices" + bluedevice.getName());
				hasDevices.add(bluedevice);
				if(!isconnect && connectDevice!=null) {
					if (bluedevice.getAddress().equals(connectDevice.getAddress())) {
						isconnect = true;
					}
				}
			}
		}

		if(headsetProfile !=null) {
			hasDevices_bak = headsetProfile.getConnectedDevices();
			for (BluetoothDevice bluedevice : hasDevices_bak) {
				L.e(TAG, "headsetProfile connectedDevices" + bluedevice.getName());
				hasDevices.add(bluedevice);
				if(!isconnect && connectDevice!=null) {
					if (bluedevice.getAddress().equals(connectDevice.getAddress())) {
						isconnect = true;
					}
				}
			}
		}


		bluetoothSimperAdapter_paired.setConnectDevice(hasDevices);
		bluetoothSimperAdapter_paired.notifyDataSetChanged();

		int length = blueToothDeviceList.size();
		for(int i = 0;i<length;i++){
			BluetoothDevice device = blueToothDeviceList.get(i);
			if(hasDevices.contains(device)){
				blueToothDeviceList.remove(device);
				i--;
				length--;
			}
		}
		connectDevice = null;
	}

	/**
	 获取已匹配设备
	 */
	private void getPairedDevices(){
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices != null && pairedDevices.size() > 0) {
			blueToothDeviceList_paired = new ArrayList<>();
			blueToothDeviceList_paired.addAll(pairedDevices);

			for (BluetoothDevice device : pairedDevices) {
				L.d(TAG,"device:"+device.getName());
				L.d(TAG,"device:"+device.getAddress());
				boolean isHaveConnected = false;

				if(hasDevices.contains(device)){
					isHaveConnected = true;
					L.e(TAG,"hasDevices contain");
				}
				if (!isHaveConnected && !isInit){
					L.e(TAG,"isHaveConnected false");
					switch (device.getBluetoothClass().getMajorDeviceClass()) {
						case BluetoothClass.Device.Major.AUDIO_VIDEO:
//							blueToothDeviceList_paired.add(device);
							mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
									BluetoothProfile.A2DP);
//							mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
//									BluetoothProfile.HEADSET);
							break;
						case BluetoothClass.Device.Major.PERIPHERAL:
//							blueToothDeviceList_paired.add(device);
							mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
									NetUtils.DEVICE_INPUT_DEVICE);
							break;
						case BluetoothClass.Device.Major.UNCATEGORIZED:
//							blueToothDeviceList_paired.add(device);
							mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
									NetUtils.DEVICE_INPUT_DEVICE);
							mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
									BluetoothProfile.A2DP);
							break;

						default:
							break;
					}
				}
			}
			isInit = true;
			if(blueToothDeviceList_paired != null && blueToothDeviceList_paired.size()>0){
				ll_pair_lv.setVisibility(View.VISIBLE);
				bluetooth_pb.setVisibility(View.GONE);
			}else{
				ll_pair_lv.setVisibility(View.GONE);
			}
			bluetoothSimperAdapter_paired.setDeviceList(blueToothDeviceList_paired);
			bluetoothSimperAdapter_paired.notifyDataSetChanged();
			setListViewHeightBasedOnChildren();
		}else{
			ll_pair_lv.setVisibility(View.GONE);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				if (back_bluetooth.hasFocus()) {
					return true;
				}else if (Bluetooth_switch_fl.hasFocus()) {
					back_bluetooth.requestFocus();
					return true;
				}else if (bluetooth_pair_lv.hasFocus()) {
					Bluetooth_switch_fl.requestFocus();
					return true;
				}else if (bluetooth_lv.hasFocus()) {
					if (ll_pair_lv.getVisibility() == View.VISIBLE) {
						bluetooth_pair_lv.requestFocus();
						return true;
					} else {
						Bluetooth_switch_iv.requestFocus();
						return true;
					}
				} else {
					Bluetooth_switch_fl.requestFocus();
					return true;
				}
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (back_bluetooth.hasFocus()) {
					Bluetooth_switch_fl.requestFocus();
					return true;
				}else if (Bluetooth_switch_fl.hasFocus()) {
					if(isOpenBluetooth){
						if(ll_pair_lv.getVisibility() == View.VISIBLE) {
							bluetooth_pair_lv.requestFocus();
							bluetooth_pair_lv.setSelection(0);
							return true;
						}
						if(ll_bluetooth_lv.getVisibility() == View.VISIBLE) {
							bluetooth_lv.requestFocus();
							bluetooth_lv.setSelection(0);
							return true;
						}
					}
					return true;
				}
				if (ll_pair_lv.hasFocus()) {
					if(bluetooth_lv.getVisibility() == View.VISIBLE) {
						bluetooth_lv.requestFocus();
						bluetooth_lv.setSelection(0);
						return true;
					}
				}
				return true;
			default:
				break;
		}
		return false;
	}

	@Override
	public void onFocusChange(View view, boolean has_focus) {
		L.i(TAG, "--onFocusChange" + view.getId());
		if (view.getId() == R.id.bluetooth_switch_fl) {
			if (has_focus) {
				Bluetooth_switch_fl.setSelected(true);
			} else {
				Bluetooth_switch_fl.setSelected(false);
			}
		}
	}

	public void onExit() {
		if (bluetoothSimperAdapter != null) {
			bluetoothSimperAdapter.onExit();
		}
		ctvContext.unregisterReceiver(mReceiver);
		bluetoothHandler.removeMessages(BLUETOOTH_STATE);
		bluetoothSimperAdapter = null;
		Bluetooth_switch_fl = null;
		Bluetooth_switch_iv = null;
		bluetooth_lv = null;
		bluetooth_pb = null;
		isOpenBluetooth = false;
		bluetoothHandler.removeCallbacksAndMessages(null);
	}

	public void onResume(){
		if(ctvContext != null) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(BluetoothDevice.ACTION_FOUND);
			filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
			filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
			ctvContext.registerReceiver(mReceiver, filter);
			bluetoothHandler.sendEmptyMessageDelayed(BLUETOOTH_STATE, 2000);
		}
	}

	/**
	 设置双层ListView滑动长度
	 */
	private void setListViewHeightBasedOnChildren() {
		int totalHeight = 0;
		int count = 0;
		if (bluetoothSimperAdapter != null) {
			totalHeight += NetUtils.dip2px(ctvContext,60) * bluetoothSimperAdapter.getCount();
			ViewGroup.LayoutParams params = bluetooth_lv.getLayoutParams();
			params.height = totalHeight+ NetUtils.dip2px(ctvContext,60) + (bluetooth_lv.getDividerHeight() * (count - 1));
			bluetooth_lv.setLayoutParams(params);
		}
		totalHeight = 0;
		if(bluetoothSimperAdapter_paired != null){
			totalHeight += NetUtils.dip2px(ctvContext,60) * bluetoothSimperAdapter_paired.getCount() ;//listItem.getMeasuredHeight();
			ViewGroup.LayoutParams params = bluetooth_pair_lv.getLayoutParams();
			params.height = totalHeight + (bluetooth_pair_lv.getDividerHeight() * (count - 1));
			bluetooth_pair_lv.setLayoutParams(params);
		}
	}

	/**
	 * 接触设备配对
	 * @param bluetoothDevice
	 */
	private void unPaired(final BluetoothDevice bluetoothDevice){
		if(bluetoothDevice == null){
			return;
		}
		DialogInterface.OnClickListener ok = (dialog, which) -> {
			if(blueToothDeviceList_paired.contains(bluetoothDevice)){
				try {
					//解除匹配
					ClsUtils.removeBond(bluetoothDevice.getClass(),bluetoothDevice);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		AlertDialog dialog = new AlertDialog.Builder(mActivity)
				.setCancelable(true)
				.setTitle(R.string.wifi_display_disconnect_title)
				.setMessage(
						Html.fromHtml(resources.getString(
								R.string.wifi_display_disconnect_text,
								bluetoothDevice.getName())))
				.setPositiveButton(android.R.string.ok, ok)
				.setNegativeButton(android.R.string.cancel, null).create();
		dialog.show();
	}

	/**
	 * 修改配对弹框
	 */

	private void ShowSaveStatusDialog() {
		if (mDialog != null) {
			mDialog.cancel();
		}
		mDialog = new ProgressDialog(mActivity);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setTitle(ctvContext.getString(R.string.tip));
		mDialog.setOnKeyListener((dialog, keyCode, event) -> {
			// TODO Auto-generated method stub
			return false;
		});
		mDialog.setMessage(ctvContext.getString(R.string.wifi_ap_submitting));
		mDialog.show();

		bluetoothHandler.sendEmptyMessage(BLUETOOTH_CONNECT);
	}

	/**
	 * 蓝牙键盘设备无法自动匹配，需要提示框显示配对信息
	 * @param password
	 */
	private void ShowInputDeviceDialog(String password) {
		if (mDialog != null) {
			mDialog.cancel();
		}
		mDialog = new ProgressDialog(mActivity);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setTitle(ctvContext.getString(R.string.tip));
		mDialog.setOnKeyListener((dialog, keyCode, event) -> {
			// TODO Auto-generated method stub
			return false;
		});
		mDialog.setMessage(ctvContext.getString(R.string.bluetooth_enter_passkey_other_device)+password);
		mDialog.show();
	}

	/**
	 * 初始化UI
	 *
	 * @param activity
	 */
//	@Override
	public void initUI(Activity activity) {
		this.ctvContext = mActivity.getApplicationContext();
		resources = ctvContext.getResources();

		ll_on_all = (LinearLayout) mActivity.findViewById(R.id.ll_on_all);
		ll_no_device_scan = (LinearLayout) mActivity.findViewById(R.id.no_device_scan);
		ll_bluetooth_lv = (LinearLayout) mActivity.findViewById(R.id.ll_bluetooth_lv);
		ll_pair_lv = (LinearLayout) mActivity.findViewById(R.id.ll_pair_lv);
		Bluetooth_switch_fl = (FrameLayout) mActivity.findViewById(R.id.bluetooth_switch_fl);
		Bluetooth_switch_iv = (ImageView) mActivity.findViewById(R.id.bluetooth_switch_iv);
		bluetooth_pb = (RelativeLayout) mActivity.findViewById(R.id.bluetooth_pb);
		bluetooth_lv = (BluetoothListView)mActivity.findViewById(R.id.bluetooth_lv);
		bluetooth_pair_lv = (BluetoothListView)mActivity.findViewById(R.id.bluetooth_pair_lv);
		back_bluetooth = (LinearLayout) mActivity.findViewById(R.id.back_bluetooth);
		bluetooth_pb.setVisibility(View.GONE);
		ll_pair_lv.setVisibility(View.GONE);
		ll_bluetooth_lv.setVisibility(View.GONE);

	}

	/**
	 * 初始化数据
	 *
	 **/
//	@Override
	public void initData(Activity activity) {



		if(blueToothDeviceList ==null){
			blueToothDeviceList = new ArrayList<>();
		}
		if(blueToothDeviceList_paired ==null){
			blueToothDeviceList_paired = new ArrayList<>();
		}
		bluetoothSimperAdapter = new BluetoothSimperAdapter(mActivity, blueToothDeviceList);
		bluetooth_lv.setAdapter(bluetoothSimperAdapter);
		bluetoothSimperAdapter_paired = new BluetoothSimperAdapter(mActivity, blueToothDeviceList_paired);
		bluetooth_pair_lv.setAdapter(bluetoothSimperAdapter_paired);
		setListViewHeightBasedOnChildren();

	}

	/**
	 * 初始化监听
	 */
//	@Override
	@SuppressLint("MissingPermission")
	public void initListener() {
		Bluetooth_switch_fl.setOnFocusChangeListener(this);
		Bluetooth_switch_fl.setOnClickListener(this);
		bluetooth_lv.setVerticalScrollBarEnabled(true);
		back_bluetooth.setOnClickListener(this);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter != null) {
			mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
					NetUtils.DEVICE_INPUT_DEVICE);
			mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
					BluetoothProfile.A2DP);
			checkConnectDevice();
			L.d("isEnabled"+mBluetoothAdapter.isEnabled());
			if (mBluetoothAdapter.isEnabled()) {
				isOpenBluetooth = true;
				isConnecting = false;
				getPairedDevices();
			}
			L.d("isOpenBluetooth"+isOpenBluetooth);
			if(isOpenBluetooth){
				Bluetooth_switch_iv.setBackgroundResource(R.mipmap.on);
				ll_on_all.setVisibility(View.VISIBLE);
			}else{
				Bluetooth_switch_iv.setBackgroundResource(R.mipmap.off);
				ll_on_all.setVisibility(View.GONE);
			}
		}

		bluetooth_pair_lv.setOnItemClickListener((parent, view, position, id) -> {
			ShowSaveStatusDialog();
			connectDevice = blueToothDeviceList_paired.get(position);
			if(hasDevices.contains(connectDevice)){
				unPaired(connectDevice);
			}else{
				isConnecting = true;
				mBluetoothAdapter.cancelDiscovery();
				bluetoothSimperAdapter_paired.setConnectDevice(connectDevice);
				bluetoothSimperAdapter_paired.notifyDataSetChanged();
				switch (connectDevice.getBluetoothClass().getMajorDeviceClass()){
					case BluetoothClass.Device.Major.AUDIO_VIDEO:
						L.e(TAG,"this is a Audio");
						mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
								BluetoothProfile.A2DP);

						addPreferencesForProfiles(connectDevice);

						break;
					case BluetoothClass.Device.Major.PERIPHERAL:
						L.e(TAG,"this is a Input");
						mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
								NetUtils.DEVICE_INPUT_DEVICE);

						addPreferencesForProfiles(connectDevice);
						break;
					case BluetoothClass.Device.Major.PHONE:
					case BluetoothClass.Device.Major.COMPUTER:
						L.e(TAG,"this is a phone and computer");

						break;
				}
			}
		});
		bluetooth_lv.setOnItemClickListener((parent, view, position, id) -> {
			L.i(TAG, "-----setOnItemClickListener--position:" + position);
			ShowSaveStatusDialog();
			isConnecting = true;
			mBluetoothAdapter.cancelDiscovery();
			connectDevice = blueToothDeviceList.get(position);
			bluetoothSimperAdapter.setConnectDevice(connectDevice);
			bluetoothSimperAdapter.notifyDataSetChanged();
			switch (connectDevice.getBluetoothClass().getMajorDeviceClass()){
				case BluetoothClass.Device.Major.AUDIO_VIDEO:
					L.e(TAG,"this is a Audio");
					connectDevice.createBond();
					mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
							BluetoothProfile.A2DP);
					break;
				case BluetoothClass.Device.Major.PERIPHERAL:
					L.e(TAG,"this is a Input");
					connectDevice.createBond();
					mBluetoothAdapter.getProfileProxy(ctvContext, listener_Profile,
							NetUtils.DEVICE_INPUT_DEVICE);
					break;

				case BluetoothClass.Device.Major.PHONE:
				case BluetoothClass.Device.Major.COMPUTER:
					connectDevice.createBond();
					break;
			}
		});
	}

	private void connect(BluetoothDevice btDev) {
		try {
			if (BTUUID == null)
				BTUUID = new UUID(20123,20123);
			//通过和服务器协商的uuid来进行连接
			mBluetoothSocket = btDev.createRfcommSocketToServiceRecord(BTUUID);
			if (mBluetoothSocket != null)
				//全局只有一个bluetooth，所以我们可以将这个socket对象保存在appliaction中
//                BltAppliaction.bluetoothSocket = mBluetoothSocket;

				L.i(TAG, "开始连接...");
			//在建立之前调用
			if (mBluetoothAdapter.isDiscovering())
				//停止搜索
				mBluetoothAdapter.cancelDiscovery();
			//如果当前socket处于非连接状态则调用连接
			if (!mBluetoothSocket.isConnected()) {
				//你应当确保在调用connect()时设备没有执行搜索设备的操作。
				// 如果搜索设备也在同时进行，那么将会显著地降低连接速率，并很大程度上会连接失败。
				mBluetoothSocket.connect();
			}
			L.i(TAG, "已经连接");
			try {
				//message += "\n";
				OutputStream outputStream = mBluetoothSocket.getOutputStream();
				outputStream.write("connect".getBytes("utf-8"));
				outputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
//            if (handler == null) return;
//            //结果回调
//            Message message = new Message();
//            message.what = 4;
//            message.obj = btDev;
//            handler.sendMessage(message);
		} catch (Exception e) {
			L.i(TAG, "...连接失败"+e.toString());
			try {
				mBluetoothSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				L.i(TAG, "...连接失败"+e1.toString());
			}
		}
	}

	/**
	 * 刷新指定view
	 *
	 */
//	@Override
	public void refreshUI(View view) {
		int id = view.getId();
		if(isOpenBluetooth){
			if(id == R.id.ll_on_all) {
				view.setVisibility(View.VISIBLE);
			}
			if(id == R.id.bluetooth_switch_iv) {
				view.setBackgroundResource(R.mipmap.on);
			}
			ll_no_device_scan.setVisibility(View.VISIBLE);
			if(blueToothDeviceList!=null && blueToothDeviceList.size()> 0){
				if(id == R.id.ll_bluetooth_lv) {
					view.setVisibility(View.VISIBLE);
				}
				if(id == R.id.bluetooth_pb) {
					view.setVisibility(View.GONE);
				}
				ll_no_device_scan.setVisibility(View.GONE);
			}else{
				if(id == R.id.ll_bluetooth_lv) {
					view.setVisibility(View.GONE);
				}
			}
			if(blueToothDeviceList_paired!=null && blueToothDeviceList_paired.size()> 0){
				if(id == R.id.ll_pair_lv) {
					view.setVisibility(View.VISIBLE);
				}
				if(id == R.id.bluetooth_pb) {
					view.setVisibility(View.GONE);
				}
				ll_no_device_scan.setVisibility(View.GONE);
			}else{
				if(id == R.id.ll_pair_lv) {
					view.setVisibility(View.GONE);
				}
			}

			if(id == R.id.bluetooth_pb) {
				if(view.getVisibility() == View.VISIBLE && mBluetoothAdapter.isEnabled()){
					view.setVisibility(View.GONE);
				}
			}


		}else{
			ll_no_device_scan.setVisibility(View.GONE);
			if(id == R.id.ll_on_all) {
				view.setVisibility(View.GONE);
			}

			if(id == R.id.bluetooth_switch_iv) {
				view.setBackgroundResource(R.mipmap.off);
			}
		}
	}
	private  final int MIN_CLICK_DELAY_TIME = 1000;
	private  long lastClickTime;
	public  boolean isFastClick() {
		boolean flag = false;
		long curClickTime = System.currentTimeMillis();
		if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
			flag = true;
		}
		lastClickTime = curClickTime;
		return flag;
	}
	@SuppressLint("MissingPermission")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (isFastClick()){
			if (id == R.id.bluetooth_switch_fl) {
				if (isOpenBluetooth) {
					mBluetoothAdapter.disable();
					isOpenBluetooth = false;
					ll_on_all.setVisibility(View.GONE);
					bluetoothHandler.removeMessages(BLUETOOTH_OPEN);
				} else {
//					mBluetoothAdapter.enable();
					bluetoothHandler.sendEmptyMessageDelayed(BLUETOOTH_OPEN,1000);
					isOpenBluetooth = true;
					ll_on_all.setVisibility(View.VISIBLE);
					bluetooth_pb.setVisibility(View.VISIBLE);
					ll_bluetooth_lv.setVisibility(View.GONE);
					ll_pair_lv.setVisibility(View.GONE);

//					Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//					discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//					mActivity.startActivity(discoverableIntent);
//					mActivity.startActivityForResult(discoverableIntent,100);
				}
			}else if(id == R.id.back_bluetooth){
				mActivity.finish();
			}
		}else {
			L.e("bluetooth onclick more");
		}

	}


	private void setConnectDevice(){
	}

	private void addPreferencesForProfiles(BluetoothDevice Device) {
		LocalBluetoothManager mManager = BluetoothUtils.getLocalBtManager(ctvContext);
		CachedBluetoothDeviceManager deviceManager =mManager.getCachedDeviceManager();
		mCachedDevice = deviceManager.findDevice(Device);
		L.d("profile getName"+ (mCachedDevice == null));
		if (mCachedDevice == null) {
			mCachedDevice = deviceManager.addDevice(mManager.getBluetoothAdapter(),
					mManager.getProfileManager(), Device);
		}
		for (LocalBluetoothProfile profile : mCachedDevice.getConnectableProfiles()) {
			L.d("profile getName"+profile.getClass().getName());
//			mCachedDevice.connectProfile(profile);
		}

		final int pbapPermission = mCachedDevice.getPhonebookPermissionChoice();
		// Only provide PBAP cabability if the client device has requested PBAP.
		if (pbapPermission != CachedBluetoothDevice.ACCESS_UNKNOWN) {
			final PbapServerProfile psp = mManager.getProfileManager().getPbapProfile();
			L.d("PbapServerProfile getName"+psp.getClass().getName());
//			mCachedDevice.connectProfile(psp);
		}

		final MapProfile mapProfile = mManager.getProfileManager().getMapProfile();
		final int mapPermission = mCachedDevice.getMessagePermissionChoice();
		if (mapPermission != CachedBluetoothDevice.ACCESS_UNKNOWN) {
			L.d("mapProfile getName"+mapProfile.getClass().getName());
//			mCachedDevice.connectProfile(mapProfile);
		}

	}


}
