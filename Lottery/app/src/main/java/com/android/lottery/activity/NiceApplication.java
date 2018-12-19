package com.android.lottery.activity;

import com.android.lottery.bean.NiceUserInfo;
import com.android.lottery.util.ImageLoaderUtils;
import com.android.lottery.util.LogUtil;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import android.util.Log;

public class NiceApplication extends FrontiaApplication {
	private final static String LOG_TAG = "NiceApplication_2";
	private LocationClient mLocationClient = null;
	@Override
	public void onCreate() {
		LogUtil.i(LOG_TAG, "onCreate");
//		initBaiduLocation();
		ImageLoaderUtils.getInstance().initImageLoader(this);
		super.onCreate();
	}

//	public void initBaiduLocation() {
//		LogUtil.i(LOG_TAG, "initBaiduLocation");
//		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
//		mLocationClient.start();
//		mLocationClient.registerLocationListener(new NiceLocationListener()); // 注册监听函数
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(false);
//		option.setAddrType("alls");// 返回的定位结果包含地址信息
//		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
//		option.setScanSpan(0);// 设置发起定位请求的间隔时间为5000ms
//		option.disableCache(false);// 禁止启用缓存定位
//		option.setPoiNumber(0); // 最多返回POI个数
//		// option.setPoiDistance(1000); //poi查询距离
//		option.setPoiExtraInfo(false); // 是否需要POI的电话和地址等详细信息
//		mLocationClient.setLocOption(option);
//		this.requestLocation();
//	}

	int count = 0;

	public void requestLocation() {
		LogUtil.i(LOG_TAG, "requestLocation");
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
			count = 0;
		} else if (mLocationClient == null) {
//			initBaiduLocation();
		}else if (count++ < 5) {
//			mLocationClient.requestLocation();
			LogUtil.i(LOG_TAG, "locClient is null or not started");
		} else {
			count = 0;
			return;
		}
	}

	/**
	 * 61 ： GPS定位结果 
	 * 62 ： 扫描整合定位依据失败。此时定位结果无效。 
	 * 63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
	 * 65 ： 定位缓存的结果。 
	 * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果 
	 * 67 ：离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 
	 * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
	 * 161： 表示网络定位结果 
	 * 162~167： 服务端定位失败 
	 * 502：key参数错误
	 * 505：key不存在或者非法
	 * 601：key服务被开发者自己禁用
	 * 602：key mcode不匹配
	 * 501～700：key验证失败
	 * 
	 * @author Administrator
	 * 
	 */
	class NiceLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(com.baidu.location.BDLocation location) {
			// TODO Auto-generated method stub
			Log.i(LOG_TAG, "MyLocationListener->onReceiveLocation()");
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			int respCode = location.getLocType();
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			if(respCode == 161) {
				NiceUserInfo userInfo = NiceUserInfo.getInstance();
				userInfo.setLatitude(latitude);
				userInfo.setLongitude(longitude);
				userInfo.setLocationSuccess(true);
				LogUtil.i(LOG_TAG, userInfo.toString());
			}
			sb.append("\nerror code : ");// 结果代码
			sb.append(respCode);
			sb.append("\nlatitude : ");// 纬度
			sb.append(latitude);
			sb.append("\nlontitude : ");// 经度
			sb.append(longitude);
			LogUtil.i(LOG_TAG, sb.toString());
		}

		@Override
		public void onReceivePoi(com.baidu.location.BDLocation arg0) {
		}

	}
}
