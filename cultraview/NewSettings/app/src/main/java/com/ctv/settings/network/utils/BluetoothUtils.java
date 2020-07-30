package com.ctv.settings.network.utils;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager.BluetoothManagerCallback;
import com.android.settingslib.bluetooth.Utils.ErrorListener;
import com.ctv.settings.R;


/**
 * Utils is a helper class that contains constants for various
 * Android resource IDs, debug logging flags, and static methods
 * for creating dialogs.
 */
public final class BluetoothUtils {
    static final boolean V = com.android.settingslib.bluetooth.Utils.V; // verbose logging
    static final boolean D =  com.android.settingslib.bluetooth.Utils.D;  // regular logging

    private BluetoothUtils() {
    }

    public static int getConnectionStateSummary(int connectionState) {
        switch (connectionState) {
            case BluetoothProfile.STATE_CONNECTED:
                return R.string.bluetooth_connected;
            case BluetoothProfile.STATE_CONNECTING:
                return R.string.bluetooth_connecting;
            case BluetoothProfile.STATE_DISCONNECTED:
                return R.string.bluetooth_disconnected;
            case BluetoothProfile.STATE_DISCONNECTING:
                return R.string.bluetooth_disconnecting;
            default:
                return 0;
        }
    }

    // Create (or recycle existing) and show disconnect dialog.
    static AlertDialog showDisconnectDialog(Context context,
                                            AlertDialog dialog,
                                            DialogInterface.OnClickListener disconnectListener,
                                            CharSequence title, CharSequence message) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context)
                    .setPositiveButton(android.R.string.ok, disconnectListener)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            // use disconnectListener for the correct profile(s)
            CharSequence okText = context.getText(android.R.string.ok);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    okText, disconnectListener);
        }
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
        return dialog;
    }

    // TODO: wire this up to show connection errors...
    static void showConnectingError(Context context, String name) {
        // if (!mIsConnectingErrorPossible) {
        //     return;
        // }
        // mIsConnectingErrorPossible = false;

//        showError(context, name, R.string.bluetooth_connecting_error_message);
    }

    static void showError(Context context, String name, int messageResId) {
        String message = context.getString(messageResId, name);
        LocalBluetoothManager manager = getLocalBtManager(context);
        Context activity = manager.getForegroundActivity();
        if(manager.isForegroundActivity()) {
            new AlertDialog.Builder(activity)
//                    .setTitle(R.string.bluetooth_error_title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static LocalBluetoothManager getLocalBtManager(Context context) {
        return LocalBluetoothManager.getInstance(context, mOnInitCallback);
    }

    private static final ErrorListener mErrorListener = new ErrorListener() {
        @Override
        public void onShowError(Context context, String name, int messageResId) {
            showError(context, name, messageResId);
        }
    };

    private static final BluetoothManagerCallback mOnInitCallback = new BluetoothManagerCallback() {
        @Override
        public void onBluetoothManagerInitialized(Context appContext,
                                                  LocalBluetoothManager bluetoothManager) {
            bluetoothManager.getEventManager().registerCallback(
                    new DockBluetoothCallback(appContext));
            com.android.settingslib.bluetooth.Utils.setErrorListener(mErrorListener);
        }
    };

    public static class DockBluetoothCallback implements BluetoothCallback {
        private final Context mContext;

        public DockBluetoothCallback(Context context) {
            mContext = context;
        }

        public void onBluetoothStateChanged(int bluetoothState) { }
        public void onDeviceAdded(CachedBluetoothDevice cachedDevice) { }
        public void onDeviceDeleted(CachedBluetoothDevice cachedDevice) { }
        public void onConnectionStateChanged(CachedBluetoothDevice cachedDevice, int state) { }

        @Override
        public void onScanningStateChanged(boolean started) {
            // TODO: Find a more unified place for a persistent BluetoothCallback to live
            // as this is not exactly dock related.
            LocalBluetoothPreferences.persistDiscoveringTimestamp(mContext);
        }

        @Override
        public void onDeviceBondStateChanged(CachedBluetoothDevice cachedDevice, int bondState) {
            BluetoothDevice device = cachedDevice.getDevice();
            if (bondState == BluetoothDevice.BOND_NONE) {
//                if (device.isBluetoothDock()) {
//                    // After a dock is unpaired, we will forget the settings
//                    LocalBluetoothPreferences
//                            .removeDockAutoConnectSetting(mContext, device.getAddress());
//
//                    // if the device is undocked, remove it from the list as well
//                    if (!device.getAddress().equals(getDockedDeviceAddress(mContext))) {
//                        cachedDevice.setVisible(false);
//                    }
//                }
            }
        }

        // This can't be called from a broadcast receiver where the filter is set in the Manifest.
        private static String getDockedDeviceAddress(Context context) {
            // This works only because these broadcast intents are "sticky"
            Intent i = context.registerReceiver(null, new IntentFilter(Intent.ACTION_DOCK_EVENT));
            if (i != null) {
                int state = i.getIntExtra(Intent.EXTRA_DOCK_STATE, Intent.EXTRA_DOCK_STATE_UNDOCKED);
                if (state != Intent.EXTRA_DOCK_STATE_UNDOCKED) {
                    BluetoothDevice device = i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        return device.getAddress();
                    }
                }
            }
            return null;
        }
    }
}
