package com.protruly.floatwindowlib.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.IDHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作
 *
 * @author wanghang
 * @date 2018/9/20
 */
public class DBHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    private static List<SignalInfo> signalInfoList = new ArrayList<>();
    private static Map<String, SignalInfo> signalInfoMap = new HashMap<>();

    /**
     * 获得当前source信息
     *
     * @param context
     */
    public static SignalInfo querySourceInfo(Context context) {
        int inputSource = AppUtils.getCurrentSource(context);
        int sourceIndex = inputSource;

        // VGA PreVGA
        if (inputSource == 28) { // DTV
            sourceIndex = 0;
        } else if (inputSource == 0) {
            int type = Settings.System.getInt(context.getContentResolver(), CommConst.VGA_INFO, 0);
            sourceIndex = (type == 0) ? 28 : 31;
        } else if (inputSource == 24) { // HDMI PreHdmi DP
            int type = Settings.System.getInt(context.getContentResolver(), CommConst.SOURCE_INFO, 2);
            switch (type) {
                case 0: // 前置HDMI
                    sourceIndex = 30;
                    break;
                case 3: // DP
                    sourceIndex = 29;
                    break;
            }
        } else {
            sourceIndex = inputSource;
        }

        // 获得信号源参数值
        SignalInfo signalInfo = getSourceMap(context).get(sourceIndex + "");
        LogUtils.d("signalInfo->" + signalInfo.toString());
        return signalInfo;
    }

    /**
     * 刷新列表
     *
     * @param context
     */
    public static void refreshSignalInfo(Context context) {
        if (signalInfoList != null) {
            signalInfoList.clear();
        }

        if (signalInfoMap != null) {
            signalInfoMap.clear();
        }

        getSourceMap(context);
    }

    /**
     * 修改source名称
     *
     * @param sourceIndex
     */
    public static SignalInfo querySourceName(Context context, int sourceIndex) {
//		LogUtils.d("querySourceName sourceIndex->" + sourceIndex);
        // 获得信号源参数值
        SignalInfo signalInfo = getSourceMap(context).get(sourceIndex + "");
        return signalInfo;
    }

    /**
     * 获得所有信号信息
     *
     * @param context
     * @return
     */
    public static List<SignalInfo> getSourceNames(Context context) {
        Log.d(TAG,"getSourceNames");
        if (signalInfoList != null && signalInfoList.size() > 0) {
            int currSourceIndex = AppUtils.getCurrentSourceIndex(context);
            LogUtils.d("getSourceMap currSourceIndex->%s", currSourceIndex);

            for (SignalInfo signalInfo : signalInfoList) {
                int sourceIdIndex = signalInfo.getSourceId();
                boolean isLastSelected = signalInfo.isSelected();

                // 上一次选择和当前相同时,不做处理
                if (isLastSelected && (currSourceIndex == sourceIdIndex)) {
                    break;
                }

                // 还原上一次选中
                String resIdStr;
                if (isLastSelected) {
                    resIdStr = signalInfo.getResIdStr().replace("_focus", "_normal");
                    signalInfo.setResIdStr(resIdStr);
                    int resId = IDHelper.getDrawable(context, resIdStr); // 从Drawable中去图片资源ID
                    signalInfo.setImageId(resId);
                    signalInfo.setSelected(false);
                    LogUtils.d("getSourceMap LastSelected lastSourceIndex->%s", signalInfo.getSourceId());
                }

                // 当前为选中状态时
                if (currSourceIndex == sourceIdIndex) {
                    // 获得选中图片
                    resIdStr = signalInfo.getResIdStr().replace("_normal", "_focus");
                    signalInfo.setResIdStr(resIdStr);
                    int resId = IDHelper.getMipmap(context, resIdStr); // 从Mipmap中去图片资源ID
                    signalInfo.setImageId(resId);
                    signalInfo.setSelected(true);
                    LogUtils.d("getSourceMap setSelected currSourceIndex->%s", currSourceIndex);
                }
            }
            return signalInfoList;
        }

        // 获得数据
        if (signalInfoList == null) {
            signalInfoList = new ArrayList<>();
        }
        signalInfoList.clear();
        signalInfoList.addAll(getSourceMap(context).values());
        Collections.sort(signalInfoList);
        return signalInfoList;
    }

    /**
     * 获得所有信号信息
     *
     * @param context
     * @return
     */
    public static List<SignalInfo> getSourceInfoFilter(Context context, String... sourceIds) {
        Map<String, SignalInfo> map = getSourceMap(context);
        if (sourceIds != null) {
            for (String id : sourceIds) {
                if (map.containsKey(id)) {
                    map.remove(id);
                }
            }
        }

        // 获得数据
        List<SignalInfo> dataList = new ArrayList<>(map.values());
        Collections.sort(dataList);
        return dataList;
    }

    /**
     * 获得所有信号信息
     *
     * @param context
     * @return
     */
    public static Map<String, SignalInfo> getSourceMap(Context context) {
        Log.d(TAG,"getSourceMap");
        if (signalInfoMap.size() > 0) {
            return signalInfoMap;
        }

        // 获得信号源参数值
        int[] indexList = {0};
        String[] nameList = null;
        String[] resIdList = null;


        indexList = context.getResources().getIntArray(R.array.input_source_id_list);
        nameList = context.getResources().getStringArray(R.array.input_source_name_list);
        resIdList = context.getResources().getStringArray(R.array.input_source_res_list);

        // 获得数据
        int len = resIdList.length;
        if (signalInfoMap == null) {
            signalInfoMap = new HashMap<>();
        }
        signalInfoMap.clear();
        int currSourceIndex = AppUtils.getCurrentSourceIndex(context);
        // 从XML中获得默认信号源信息
        for (int i = 0; i < len; i++) {
            // 获得资源ID
            String resIdStr = resIdList[i];
            int resId = IDHelper.getDrawable(context, resIdStr);

            int sourceIdIndex = indexList[i];
            SignalInfo signalInfo = new SignalInfo(sourceIdIndex, resId, nameList[i]);
            signalInfo.setXmlIndex(i);
            signalInfo.setResIdStr(resIdStr);
            if (currSourceIndex == sourceIdIndex) {
                signalInfo.setSelected(true);
                LogUtils.d("From setSelected currSourceIndex->%s", currSourceIndex);
                // 获得选中图片
                resIdStr = resIdStr.replace("_normal", "_focus");
                resId = IDHelper.getMipmap(context, resIdStr);
                signalInfo.setImageId(resId);
            }
            signalInfoMap.put(sourceIdIndex + "", signalInfo);
            LogUtils.d("From signalInfo %s", signalInfo.toString());
        }

        String uriString = "content://com.cultraview.ctvmenu/sourcename/query";
        Uri uri = Uri.parse(uriString);
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);

        if (cursor != null && cursor.moveToFirst()) { // 选择自定义名称
            String dtvSourceName = null;
            String vgaSourceName = null;
            do {
                String sourceIndex = cursor.getString(cursor.getColumnIndex("source_id"));
                String sourceName = cursor.getString(cursor.getColumnIndex("editName"));
                if (!TextUtils.isEmpty(sourceIndex)
                        && !TextUtils.isEmpty(sourceName)) { // 编辑名为空，则选择默认名称
                    LogUtils.d("From DB sourceId %s, sourceName %s", sourceIndex, sourceName);
                    SignalInfo signalInfo = signalInfoMap.get(sourceIndex);
                    if (signalInfo == null) {
                        continue;
                    }

                    if (TextUtils.equals(sourceIndex, "0")) { // VGA
                        vgaSourceName = sourceName;
                    } else if (TextUtils.equals(sourceIndex, "28")) { // DTV
                        dtvSourceName = sourceName;
                    } else {
                        signalInfo.setName(sourceName);
                    }
                    LogUtils.d("signalInfo is not null, From DB sourceId %s, sourceName %s", sourceIndex, sourceName);
                }
            } while (cursor.moveToNext());

            // 交换VGA和DTV
            if (!TextUtils.isEmpty(vgaSourceName)) {
                signalInfoMap.get("28").setName(vgaSourceName);
            }

            if (!TextUtils.isEmpty(dtvSourceName)) {
                signalInfoMap.get("0").setName(dtvSourceName);
            }
        }
        return signalInfoMap;
    }
}
