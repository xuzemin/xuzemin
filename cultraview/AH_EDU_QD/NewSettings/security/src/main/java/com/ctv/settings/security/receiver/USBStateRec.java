
package com.ctv.settings.security.receiver;

import com.ctv.settings.security.activity.USBLockService;
import com.ctv.settings.security.utils.GreneralUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

public class USBStateRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("zhu...usbstateRec", "action:::" + intent.getAction());
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Intent intent2 = new Intent(context, USBLockService.class);
            intent2.setAction("com.cultraview.settings.action.usblock");
            context.startService(intent2);
        } else {
            if (!"1".equals(SystemProperties.get("sys.boot_completed"))) {
                return;
            }
            if ("on".equals(SystemProperties.get("persist.sys.usbLock.status"))) {
                GreneralUtils.getInstance(context).checkFile(true, false);
            } else {
                GreneralUtils.getInstance(context).checkFile(false, false);
            }
            Intent intent2 = new Intent(context, USBLockService.class);
            intent2.putExtra("usblock", "setting_switch");
            context.startService(intent2);
        }
    }

}
