/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.ctv.settings.network.mcast;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplayStatus;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.ctv.settings.network.R;
import com.ctv.settings.network.utils.NetUtils;

/**
 * The Settings screen for WifiDisplay configuration and connection management.
 */
@SuppressLint("NewApi") public final class WifiDisplaySettings extends SettingsPreferenceFragment {
    private static final String TAG = "WifiDisplaySettings";

    private static final int MENU_ID_SCAN = Menu.FIRST;

    private DisplayManager mDisplayManager;

    private WifiManager mWifiManager;

    private boolean mWifiDisplayOnSetting;

    private WifiDisplayStatus mWifiDisplayStatus;

    private PreferenceGroup mPairedDevicesCategory;

    private ProgressCategory mAvailableDevicesCategory;

    private TextView mEmptyView;

    private Switch mActionBarSwitch;

    private boolean isRegisted = false;

    public WifiDisplaySettings() {
    }

    @SuppressLint("WifiManagerLeak")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mDisplayManager = (DisplayManager) getActivity().getSystemService(Context.DISPLAY_SERVICE);
//        mWifiDisplayStatus = mDisplayManager.getWifiDisplayStatus();

        try {
            mWifiDisplayStatus = NetUtils.getWifiDisplayStatus(mDisplayManager);//mDisplayManager.getWifiDisplayStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "--onCreate--mWifiDisplayStatus:" + mWifiDisplayStatus);
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        addPreferencesFromResource(R.xml.wifi_display_settings);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        mActionBarSwitch = new Switch(activity);
        if (activity instanceof PreferenceActivity) {
            PreferenceActivity preferenceActivity = (PreferenceActivity) activity;
            if (preferenceActivity.onIsHidingHeaders() || !preferenceActivity.onIsMultiPane()) {
                final int padding = activity.getResources().getDimensionPixelSize(
                        R.dimen.action_bar_switch_padding);
                mActionBarSwitch.setPadding(0, 0, padding, 0);
                activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                        ActionBar.DISPLAY_SHOW_CUSTOM);
                activity.getActionBar().setCustomView(
                        mActionBarSwitch,
                        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                                        | Gravity.END));
            }
        }

        mActionBarSwitch.setOnCheckedChangeListener(mSwitchOnCheckedChangedListener);

        // add by Jackie.he begin 2013/04/03
        // comment:
        Settings.Global.putInt(getContentResolver(), NetUtils.WIFI_DISPLAY_ON, 1);
        // add by Jackie.he end

        mEmptyView = getView().findViewById(android.R.id.empty);

        try {
            NetUtils.getListView(this).setEmptyView(mEmptyView);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        getListView().setEmptyView(mEmptyView);
        Log.i(TAG, "--onActivityCreated:");
        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mWifiDisplayStatus.getFeatureState() == WifiDisplayStatus.FEATURE_STATE_UNAVAILABLE) {
            // activity.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getActivity();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetUtils.ACTION_WIFI_DISPLAY_STATUS_CHANGED);
        context.registerReceiver(mReceiver, filter);
        getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(NetUtils.WIFI_DISPLAY_ON), false,
                mSettingsObserver);

        // mDisplayManager.scanWifiDisplays(); //del by fengcf
        isRegisted = true;
        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Context context = getActivity();
        if (isRegisted) {
            context.unregisterReceiver(mReceiver);
            getContentResolver().unregisterContentObserver(mSettingsObserver);
            isRegisted = false;
        }

    }

    // add by Jackie.he begin 2013/04/03
    // comment:
    @Override
    public void onStop() {
        super.onStop();
//        mDisplayManager.disconnectWifiDisplay();
        try {
            NetUtils.disconnectWifiDisplay(mDisplayManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*****
         * int wifiState = mWifiManager.getWifiState(); if ((wifiState ==
         * WifiManager.WIFI_STATE_ENABLING) || (wifiState ==
         * WifiManager.WIFI_STATE_ENABLED)) {
         * mWifiManager.setWifiEnabled(false);
         * mWifiManager.setWifiEnabled(true); }
         *****/
    }

    // add by Jackie.he end

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu
                .add(Menu.NONE,
                        MENU_ID_SCAN,
                        0,
                        mWifiDisplayStatus.getScanState() == WifiDisplayStatus.SCAN_STATE_SCANNING ? R.string.wifi_display_searching_for_devices
                                : R.string.wifi_display_search_for_devices);
        item.setEnabled(mWifiDisplayStatus.getFeatureState() == WifiDisplayStatus.FEATURE_STATE_ON
                && mWifiDisplayStatus.getScanState() == WifiDisplayStatus.SCAN_STATE_NOT_SCANNING);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_SCAN:
                if (mWifiDisplayStatus.getFeatureState() == WifiDisplayStatus.FEATURE_STATE_ON) {
                    // mDisplayManager.scanWifiDisplays();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof WifiDisplayPreference) {
            WifiDisplayPreference p = (WifiDisplayPreference) preference;
            WifiDisplay display = p.getDisplay();

            if (display.equals(mWifiDisplayStatus.getActiveDisplay())) {
                showDisconnectDialog(display);
            } else {
                try {
                    NetUtils.connectWifiDisplay(mDisplayManager,display.getDeviceAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                mDisplayManager.connectWifiDisplay(display.getDeviceAddress());
            }
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void update() throws Exception {
        Log.i(TAG, "---update-00--featureState: " + mWifiDisplayStatus.getFeatureState());
        mWifiDisplayOnSetting = Settings.Global.getInt(getContentResolver(),
                NetUtils.WIFI_DISPLAY_ON, 0) != 0;
        Log.i(TAG, "--update-111-mWifiDisplayOnSetting:" + mWifiDisplayOnSetting);
//        mWifiDisplayStatus = mDisplayManager.getWifiDisplayStatus();
        mWifiDisplayStatus = NetUtils.getWifiDisplayStatus(mDisplayManager);
        Log.i(TAG, "---update-222--featureState: " + mWifiDisplayStatus.getFeatureState());
        applyState();
    }

    private void applyState() {
        final int featureState = mWifiDisplayStatus.getFeatureState();
        Log.i(TAG, "featureState: " + featureState);
        mActionBarSwitch.setEnabled(featureState != WifiDisplayStatus.FEATURE_STATE_DISABLED);
        mActionBarSwitch.setChecked(mWifiDisplayOnSetting);

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        getActivity().invalidateOptionsMenu();
    }

    private Preference createWifiDisplayPreference(final WifiDisplay d, boolean paired) {
        WifiDisplayPreference p = new WifiDisplayPreference(getActivity(), d);
        if (d.equals(mWifiDisplayStatus.getActiveDisplay())) {
            switch (mWifiDisplayStatus.getActiveDisplayState()) {
                case WifiDisplayStatus.DISPLAY_STATE_CONNECTED:
                    p.setSummary(R.string.wifi_display_status_connected);
                    break;
                case WifiDisplayStatus.DISPLAY_STATE_CONNECTING:
                    p.setSummary(R.string.wifi_display_status_connecting);
                    break;
            }
        } /*
           * else if (paired &&
           * contains(mWifiDisplayStatus.getAvailableDisplays(),
           * d.getDeviceAddress())) {
           * p.setSummary(R.string.wifi_display_status_available); }
           */// del by fengcf
        if (paired) {
            p.setWidgetLayoutResource(R.layout.wifi_display_preference);
        }
        return p;
    }

    private void showDisconnectDialog(final WifiDisplay display) {
        DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (display.equals(mWifiDisplayStatus.getActiveDisplay())) {
//                    mDisplayManager.disconnectWifiDisplay();
                    try {
                        NetUtils.disconnectWifiDisplay(mDisplayManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle(R.string.wifi_display_disconnect_title)
                .setMessage(
                        Html.fromHtml(getResources().getString(
                                R.string.wifi_display_disconnect_text,
                                display.getFriendlyDisplayName())))
                .setPositiveButton(android.R.string.ok, ok)
                .setNegativeButton(android.R.string.cancel, null).create();
        dialog.show();
    }

    private void showOptionsDialog(final WifiDisplay display) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.wifi_display_options, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.name);
        nameEditText.setText(display.getFriendlyDisplayName());

        DialogInterface.OnClickListener done = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                if (name.isEmpty() || name.equals(display.getDeviceName())) {
                    name = null;
                }
                try {
                    NetUtils.renameWifiDisplay(mDisplayManager,display.getDeviceAddress(), name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                mDisplayManager.renameWifiDisplay(display.getDeviceAddress(), name);
            }
        };
        DialogInterface.OnClickListener forget = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                mDisplayManager.forgetWifiDisplay(display.getDeviceAddress());
                try {
                    NetUtils.forgetWifiDisplay(mDisplayManager,display.getDeviceAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setCancelable(true)
                .setTitle(R.string.wifi_display_options_title).setView(view)
                .setPositiveButton(R.string.wifi_display_options_done, done)
                .setNegativeButton(R.string.wifi_display_options_forget, forget).create();
        dialog.show();
    }

    private static boolean contains(WifiDisplay[] displays, String address) {
        for (WifiDisplay d : displays) {
            if (d.getDeviceAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    private final CompoundButton.OnCheckedChangeListener mSwitchOnCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mWifiDisplayOnSetting = isChecked;
            Settings.Global.putInt(getContentResolver(), NetUtils.WIFI_DISPLAY_ON,
                    isChecked ? 1 : 0);
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(NetUtils.ACTION_WIFI_DISPLAY_STATUS_CHANGED)) {
                WifiDisplayStatus status = intent
                        .getParcelableExtra(NetUtils.EXTRA_WIFI_DISPLAY_STATUS);
                mWifiDisplayStatus = status;
                applyState();
            }
        }
    };

    private final ContentObserver mSettingsObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final class WifiDisplayPreference extends Preference implements View.OnClickListener {
        private final WifiDisplay mDisplay;

        public WifiDisplayPreference(Context context, WifiDisplay display) {
            super(context);

            mDisplay = display;
            setTitle(display.getFriendlyDisplayName());
        }

        public WifiDisplay getDisplay() {
            return mDisplay;
        }

        @Override
        protected void onBindView(View view) {
            super.onBindView(view);

            ImageView deviceDetails = (ImageView) view.findViewById(R.id.deviceDetails);
            if (deviceDetails != null) {
                deviceDetails.setOnClickListener(this);

                if (!isEnabled()) {
                    TypedValue value = new TypedValue();
                    getContext().getTheme().resolveAttribute(android.R.attr.disabledAlpha, value,
                            true);
                    deviceDetails.setImageAlpha((int) (value.getFloat() * 255));
                }
            }
        }

        @Override
        public void onClick(View v) {
            showOptionsDialog(mDisplay);
        }
    }
}
