package net.nmss.nice.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.loopj.android.http.BinaryHttpResponseHandler;

import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.LogUtil;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

public class DownHeadPicService extends Service {

	private final static String LOG_TAG = "DownHeadPicService";

	public final static String DOWNLOAD_URL_FLAG = "download_url_flag"; 
	public final static String DOWNLOAD_URL_NAME = "user_head_pic.jpg"; 
	private boolean isDowning = false;
	@Override
	public void onCreate() {
		LogUtil.i(LOG_TAG, "onCreate()");
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(isDowning) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		if(!intent.hasExtra(DOWNLOAD_URL_FLAG)) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		
		String url = intent.getStringExtra(DOWNLOAD_URL_FLAG);
		LogUtil.i(LOG_TAG, "pic_url:"+url);
		AsyncHttpRequestUtil.get(url, null, new BinaryHttpResponseHandler(){
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				isDowning = true;
				LogUtil.i(LOG_TAG, "onStart()");
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, final byte[] binaryData) {
				// TODO Auto-generated method stub
				LogUtil.i(LOG_TAG, "onSuccess()");
				if(statusCode != 200) {
					return;
				}
				
				try {
					final File file = new File(Environment.getExternalStoragePublicDirectory(
											Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+DOWNLOAD_URL_NAME);
					if(file.exists() && file.isFile()) {
						file.delete();
					}
					
					LogUtil.i(LOG_TAG, "url:"+file.getAbsolutePath());
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							FileOutputStream out;
							try {
								out = new FileOutputStream(file);
								out.write(binaryData);
								out.close();
								stopSelf();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								stopSelf();
								e.printStackTrace();
							} catch (IOException e) {
								stopSelf();
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
					}).start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onSuccess(statusCode, binaryData);
			}
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				LogUtil.i(LOG_TAG, "onFinish()");
				isDowning = false;
				super.onFinish();
			}
		});
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogUtil.i(LOG_TAG, "onDestroy()");
		super.onDestroy();
	}
}
