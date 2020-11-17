
package com.ctv.welcome.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;

import com.ctv.welcome.R;
import com.ctv.welcome.constant.Constants;
import com.ctv.welcome.task.VIPApplication;
import com.ctv.welcome.vo.DaoMaster;
import com.ctv.welcome.vo.DaoSession;

public class DBUtil {
    private static Context mContext;

    public static DaoSession mDaoSession;

    public static void init(Context context) {
        mContext = context;
        instanceDB();
    }

    private static void instanceDB() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "image-db");
        if(helper == null){
            Log.d("chen","helper:"+ null);
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db == null){
            Log.d("chen","db:"+ null);
            return;
        }
        DaoMaster daoMaster = new DaoMaster(db);
        if(daoMaster == null){
            Log.d("chen","daoMaster:"+ null);
            return;
        }
        mDaoSession = daoMaster.newSession();
        if(mDaoSession == null){
            Log.d("chen","mDaoSession:"+ null);
            return;
        }
      //  mDaoSession = new DaoMaster(
       //         new DevOpenHelper(mContext, "image-db", null).getWritableDatabase()).newSession();
    }

    public static String getPicStoreType() {
        String storeType = "";
        storeType = Global.getString(VIPApplication.getContext().getContentResolver(),
                Constants.VIPRECEPTION_STORAGE_PATH);
        if (TextUtils.isEmpty(storeType)) {
            storeType = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + VIPApplication.getContext().getString(R.string.vip_reception);
        }
        LogUtils.d("DBUtil", "getPicStoreType:" + storeType);
        return storeType;
    }

    public static boolean isWindowMode() {
/*        int mWindowMode = Global.getInt(VIPApplication.getContext().getContentResolver(),
                Constants.VIPRECEPTION_WINDOW_MODE, 0);
        LogUtils.d("DBUtil", "isWindowMode:" + mWindowMode);
        if (mWindowMode == 0) {
            return false;
        }*/
        return false;
    }

    public static float getTouchAreaJudge() {
        float mThreshold = Global.getFloat(VIPApplication.getContext().getContentResolver(),
                Constants.VIPRECEPTION_THRESHOLD, 15.0f);
        LogUtils.d("DBUtil", "getTouchAreaJudge:" + mThreshold);
        return mThreshold;
    }
}
