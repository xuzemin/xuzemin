
package com.ctv.welcome.task;

import android.app.Application;
import android.content.Context;

import com.ctv.welcome.util.DBUtil;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.transfer.TransferManager;

public class VIPApplication extends Application {
    private static Context context;
    private CosXmlService cosXmlService;
    private TransferManager transferManager;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        DBUtil.init(this);
    }

    public static Context getContext() {
        return context;
    }

}
