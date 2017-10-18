package com.android.jdrd.robot.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.android.jdrd.robot.R;
import com.android.jdrd.robot.adapter.ChildAdapter;
import com.android.jdrd.robot.util.Constant;

public class SJX_ShowImage extends Activity {
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;
	private int areaId;
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.sjx_show_image_activity);

		mGridView = (GridView) findViewById(R.id.child_grid);
		Intent intent = getIntent();
		list = intent.getStringArrayListExtra("data");
		areaId = intent.getIntExtra("area", 0);

		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
		findViewById(R.id.setting_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ChildAdapter.Current_INDEX !=-1) {
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(list.get(ChildAdapter.Current_INDEX));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Bitmap bitmap = BitmapFactory.decodeStream(fis);
					saveBitmap(bitmap);
					Toast.makeText(getApplicationContext(), "选择保存图片成功", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});

		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ChildAdapter.Current_INDEX = position;
				adapter.notifyDataSetChanged();
			}
		});
	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//			Intent intent = new Intent(SJX_ShowImage.this,SJX_ImageChoose.class);
//			intent.putExtra("area",areaId);
//			startActivity(intent);
//			finish();
//			return true;
//		}else {
//			return super.onKeyDown(keyCode, event);
//		}
//	}
	public void saveBitmap(Bitmap bitmap) {
		Constant.debugLog("保存图片");
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(),Constant.DIR_NAME);
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = areaId + ".png";
		file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
