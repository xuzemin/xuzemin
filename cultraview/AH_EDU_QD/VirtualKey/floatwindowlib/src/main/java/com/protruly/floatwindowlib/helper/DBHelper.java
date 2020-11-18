package com.protruly.floatwindowlib.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.entity.NewSignalInfo;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.IDHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作
 * @author wanghang
 * @date 2018/9/20
 */
public class DBHelper {
	private static final String TAG = DBHelper.class.getSimpleName();

	/**
	 * 获得当前source信息
	 * @param sourceIndex
	 */
	public static NewSignalInfo queryCurrentSourceName(Context context, int sourceIndex) {
		// 获得信号源参数值
		NewSignalInfo signalInfo = getSourceMap(context).get(sourceIndex + "");
		LogUtils.d("signalInfo->" + signalInfo.toString());
		return signalInfo;
	}

	/**
	 * 修改source名称
	 * @param sourceIndex
	 */
	public static NewSignalInfo querySourceName(Context context, int sourceIndex) {
		// 获得信号源参数值
		NewSignalInfo signalInfo = getSourceMap(context).get(sourceIndex + "");
		LogUtils.d("signalInfo->" + signalInfo.toString());
		return signalInfo;
	}

	/**
	 * 获得所有信号信息
	 * @param context
	 * @return
	 */
	public static List<NewSignalInfo> getSourceNames(Context context){
		// 获得数据
		List<NewSignalInfo> dataList = new ArrayList<>(getSourceMap(context).values());
		Collections.sort(dataList);
		return dataList;
	}

	/**
	 * 获得所有信号信息
	 * @param context
	 * @return
	 */
	public static Map<String, NewSignalInfo> getSourceMap(Context context){
		// 获得信号源参数值
		int[] indexList = context.getResources().getIntArray(R.array.input_source_id_list);
		String[] nameList ;
		if(MyUtils.getBoard().equals("CV8386H_MH")){
			nameList = context.getResources().getStringArray(R.array.input_source_name_list_mh);
		}else{
			nameList = context.getResources().getStringArray(R.array.input_source_name_list_ah);
		}
		String[] resIdList = context.getResources().getStringArray(R.array.input_source_res_list);
		String sourcelist = SystemProperties.get("ro.build.source.list");
		Log.e("sourcelist","sourcelist = " + sourcelist);
		int[] sourceChannel = new int[0];
		if(sourcelist != null && !sourcelist.equals("")) {
			String[] list = sourcelist.split(",");
			sourceChannel = new int[list.length];
			for (int m = 0;m < list.length;m++) {
				sourceChannel[m] = Integer.parseInt(list[m]);
			}
		}
		Log.e("sourcelist","sourcelist = " + sourceChannel.toString());
		// 获得数据
		int len = resIdList.length;
		Map<String, NewSignalInfo> sourceMap = new HashMap<>();
		// 从XML中获得默认信号源信息
		if(sourceChannel.length <= 0){
			Log.e("sourcelist","sourcelist =  reset" );
			sourceChannel = new int[]{0, 23, 24, 25, 26};
		}

		for(int j = 0;j < sourceChannel.length;j++){
			for(int k =0;k < len;k++){
				if(sourceChannel[j] == indexList[k]){
					String resIdStr = resIdList[k] ;
					int resId = IDHelper.getDrawable(context, resIdStr);
					int sourceIdIndex = indexList[k];
					NewSignalInfo signalInfo = new NewSignalInfo(sourceIdIndex, resId, nameList[k]);
					signalInfo.setXmlIndex(j);
					signalInfo.setSelected(false);
					sourceMap.put(sourceIdIndex + "", signalInfo);
					LogUtils.d("From signalInfo %s", signalInfo.toString());
				}
			}
		}

		String uriString = "content://com.cultraview.ctvmenu/sourcename/query";
		Uri uri = Uri.parse(uriString);
		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, null);

		String dtvSourceName = null;
		String vgaSourceName = null;
		if (cursor != null && cursor.moveToFirst()){ // 选择自定义名称
			do {
				String sourceIndex = cursor.getString(cursor.getColumnIndex("source_id"));
				String sourceName = cursor.getString(cursor.getColumnIndex("editName"));
				if (!TextUtils.isEmpty(sourceIndex)
						&& !TextUtils.isEmpty(sourceName)){ // 编辑名为空，则选择默认名称
					LogUtils.d("From DB sourceId %s, sourceName %s", sourceIndex, sourceName);
					NewSignalInfo signalInfo = sourceMap.get(sourceIndex);
					if (signalInfo == null){
						continue ;
					}

					if (TextUtils.equals(sourceIndex, "0")){ // VGA
//						sourceMap.get("28").setName(sourceName);
						vgaSourceName = sourceName;
					}
					else if (TextUtils.equals(sourceIndex, "28")){ // DTV
//						sourceMap.get("0").setName(sourceName);
						dtvSourceName = sourceName;
					} else {
						signalInfo.setName(sourceName);
					}
					LogUtils.d("signalInfo is not null, From DB sourceId %s, sourceName %s", sourceIndex, sourceName);
				}
			} while (cursor.moveToNext());
		}

		// 交换VGA和DTV
		if (!TextUtils.isEmpty(vgaSourceName)){
			sourceMap.get("28").setName(vgaSourceName);
		}

		if (!TextUtils.isEmpty(dtvSourceName)){
			sourceMap.get("0").setName(dtvSourceName);
		}

		return sourceMap;
	}
}
