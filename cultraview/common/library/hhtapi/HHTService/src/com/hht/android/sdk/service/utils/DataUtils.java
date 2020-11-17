package com.hht.android.sdk.service.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.util.Map;

/**
 * @Description: 获取数据工具
 * @Author: wanghang
 * @CreateDate: 2019/12/13 14:12
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/12/13 14:12
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class DataUtils {
    private final static String TAG = DataUtils.class.getSimpleName();

    // sourcename table
    public static final String TABLE_NAME = "sourcename";
    public static final String TB_COLUMN_SBNAME = "SBName";
    public static final String TB_COLUMN_EDITNAME = "editName";

    public static final String AUTHORITY = "com.cultraview.ctvmenu";


    //对外提供的匹配规则
    public static final Uri URI_INSERT = Uri.parse("content://"+AUTHORITY+"/sourcename/insert");
    public static final Uri URI_DELETE = Uri.parse("content://"+AUTHORITY+"/sourcename/delete");
    public static final Uri URI_QUERY = Uri.parse("content://"+AUTHORITY+"/sourcename/query");
    public static final Uri URI_UPDATE = Uri.parse("content://"+AUTHORITY+"/sourcename/update");

    private static DataUtils INSTANCE;

    private DataUtils(){
    }

    public static DataUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (DataUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataUtils();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 修改source名称
     * @param sourceNameKey
     */
    public String querySourceName(Context context, String sourceNameKey) {
        String tmpName = "";
        ContentResolver contentResolver = context.getContentResolver();
//        String uriString = "content://com.cultraview.ctvmenu/sourcename/query";
        Uri uri = URI_QUERY; //Uri.parse(uriString);
        String where = TB_COLUMN_SBNAME + "=?";
        String[] where_args = {sourceNameKey};
        Cursor cursor = contentResolver.query(uri, null, where,
                where_args, null);
        if (cursor != null && cursor.moveToFirst()){ // 选择自定义名称
            do {
                String sourceEditName = cursor.getString(cursor.getColumnIndex(TB_COLUMN_EDITNAME));
                L.d(TAG, "querySourceName, sourceEditName:" + sourceEditName);
                if (!TextUtils.isEmpty(sourceEditName)){ // 编辑名为空，则选择默认名称
                    tmpName = sourceEditName;
                    break;
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return tmpName;
    }

    /**
     * 获得source名称
     * @param srcMap
     */
    public void querySourceInfos(Context context, Map<String, String> srcMap) {
        L.d(TAG, "querySourceInfos, start");
        ContentResolver contentResolver = context.getContentResolver();
//        String uriString = "content://com.cultraview.ctvmenu/sourcename/query";
        Uri uri = URI_QUERY; //Uri.parse(uriString);
        Cursor cursor = contentResolver.query(uri, null, null,
                null, null);
        if (cursor != null && cursor.moveToFirst()){ // 选择自定义名称
            if (HHTConstant.DEBUG) L.d(TAG, "querySourceInfos, cursor is not null");
            do {
                String sourceNameKey = cursor.getString(cursor.getColumnIndex(TB_COLUMN_SBNAME));
                String sourceEditName = cursor.getString(cursor.getColumnIndex(TB_COLUMN_EDITNAME));
                if (HHTConstant.DEBUG) {
                    String tmp = String.format("querySourceInfos from db, sourceNameKey:%s,sourceEditName:%s",
                            sourceNameKey, sourceEditName);
                    L.d(TAG, tmp);
                }
                if (!TextUtils.isEmpty(sourceEditName)
                        && !TextUtils.isEmpty(sourceEditName)){
                    L.d(TAG, "querySourceInfos, sourceNameKey:" + sourceNameKey + ", sourceEditName:" + sourceEditName);
                    // 将 editName 不为空的保存在map中
                    if (srcMap.containsKey(sourceNameKey)){
                        srcMap.put(sourceNameKey, sourceEditName);
                    }
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        if (HHTConstant.DEBUG) L.d(TAG, "querySourceInfos, end");
    }

    /**
     * 修改source名称
     * @param sourceNameKey
     * @param sourceEdit
     */
    public void updateSourceName(Context context, String sourceNameKey, String sourceEdit) {
        L.d(TAG, "sourceNameKey->" + sourceNameKey + " sourceEdit->" + sourceEdit);

        ContentResolver contentResolver = context.getContentResolver();
//        String uriString = "content://com.cultraview.ctvmenu/sourcename/update";
        Uri uri = URI_UPDATE ;//Uri.parse(uriString);
        ContentValues values = new ContentValues();
        values.put(TB_COLUMN_EDITNAME, sourceEdit);

        // 条件
        String where = TB_COLUMN_SBNAME + "=?";
        String[] where_args = {sourceNameKey};
        contentResolver.update(uri, values, where, where_args);
    }

    /**
     * get mstar Environment
     * @param key
     * @return
     */
    public String getEnvironment(String key){
        try {
            return TvManager.getInstance().getEnvironment(key);
        } catch (TvCommonException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * get mstar Environment
     * @param key
     * @return
     */
    public boolean setEnvironment(String key, String value){
        try {
             return TvManager.getInstance().setEnvironment(key, value);
        } catch (TvCommonException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 切换信号跳转开关
     *
     * @param context
     * @param bAutoSourceSwitch
     */
    public void updateSignalSwitchMode(Context context, int bAutoSourceSwitch) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("bAutoSourceSwitch", bAutoSourceSwitch);
        // vals.put("bSourceDetectEnable", bSourceDetectEnable);
        try {
            ret = context.getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            L.d("chen", "updateSignalSwitchMode failed");
        }

        try {
            TvManager.getInstance().getDatabaseManager().setDatabaseDirtyByApplication((short) 0x19);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得信号跳转模式
     *
     * @param context
     * @return
     */
    public int getSignalSwitchMode(Context context) {
        long ret = -1;
        int bAutoSourceSwitch = -1;
        int bSourceDetectEnable = -1;
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            bAutoSourceSwitch = cursor.getInt(cursor.getColumnIndex("bAutoSourceSwitch"));
            bSourceDetectEnable = cursor.getInt(cursor.getColumnIndex("bSourceDetectEnable"));
        }
        cursor.close();
/*        if (bAutoSourceSwitch == 0 && bSourceDetectEnable == 0) {
            L.d("chen", "getSignalSwitchMode:state:close");
            return 0;
        } else if (bAutoSourceSwitch == 1 && bSourceDetectEnable == 1) {
            L.d("chen", "getSignalSwitchMode:state:open");
            return 1;
        }*/
        if (bAutoSourceSwitch == 1) {
            L.d("chen", "getSignalSwitchMode:state:close");
            return 1;
        }
        return 0;

    }
}
