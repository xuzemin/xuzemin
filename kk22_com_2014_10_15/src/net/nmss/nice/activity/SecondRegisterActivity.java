package net.nmss.nice.activity;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.service.DownHeadPicService;
import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.ImageLoaderUtils;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.MD5Utils;
import net.nmss.nice.utils.NiceConstants;
import net.nmss.nice.utils.UrlHelper;
import net.nmss.nice.widget.RoundImageView;
import net.nmss.nice.widget.popupwindow.MyPopupWindow;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SecondRegisterActivity extends BaseActivity implements
		OnClickListener {
	private static final String LOG_TAG = "InformationActivity";
	private RoundImageView header_img;
	private RelativeLayout rl_name;
	private RelativeLayout rl_declaration;
	private RelativeLayout rl_telephone;
	private RelativeLayout rl_gender;
	private RelativeLayout rl_area;
	private RelativeLayout rl_birthday;
	private RelativeLayout rl_height;
	private RelativeLayout rl_target_weight;
	private RelativeLayout rl_target_time;
	private TextView tv_name;
	private TextView tv_declaration;
	private TextView tv_telephone;
	private TextView tv_gender;
	private TextView tv_area;
	private TextView tv_birthday;
	private TextView tv_height;
	private TextView tv_weight;
	private TextView tv_target_weight;
	private TextView tv_target_time;
	private TextView tv_finish;

	private MyPopupWindow myWindow;

	private Handler handler;
	private boolean showSavePic = false;
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_layout);
		initViews();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					showSavePic = true;
					ImageLoaderUtils.getInstance().displayUserHeadPicWithOutCache(
							(String) (msg.obj), header_img);
					setViewData();
					break;
					
				default:
					break;
				}

			}
		};
		
	}

	private void initViews() {
		// TODO 自动生成的方法存根
		header_img = (RoundImageView) this
				.findViewById(R.id.img_header_pic_home_my_yes);
		rl_name = (RelativeLayout) this.findViewById(R.id.rl_new_name);
		rl_declaration = (RelativeLayout) this
				.findViewById(R.id.rl_declaration_of_thin_body);
		rl_telephone = (RelativeLayout) this
				.findViewById(R.id.rl_telephone_number);
		rl_gender = (RelativeLayout) this.findViewById(R.id.rl_gender);
		rl_area = (RelativeLayout) this.findViewById(R.id.rl_area);
		rl_birthday = (RelativeLayout) this.findViewById(R.id.rl_brithday);
		rl_height = (RelativeLayout) this.findViewById(R.id.rl_new_height);
		tv_weight = (TextView) this
				.findViewById(R.id.tv_weight_info);
		rl_target_weight = (RelativeLayout) this
				.findViewById(R.id.rl_new_weight);
		rl_target_time = (RelativeLayout) this
				.findViewById(R.id.rl_target_time);

		tv_name = (TextView) this.findViewById(R.id.tv_name_info);
		tv_declaration = (TextView) this.findViewById(R.id.tv_declaration_info);
		tv_telephone = (TextView) this.findViewById(R.id.tv_telephone_info);
		tv_gender = (TextView) this.findViewById(R.id.tv_gender_info);
		tv_area = (TextView) this.findViewById(R.id.tv_area_info);
		tv_birthday = (TextView) this.findViewById(R.id.tv_brithday_info);
		tv_height = (TextView) this.findViewById(R.id.tv_new_height_info);
		tv_target_weight = (TextView) this
				.findViewById(R.id.tv_new_weight_info);
		tv_target_time = (TextView) this.findViewById(R.id.tv_target_time_info);
		tv_finish = (TextView) this.findViewById(R.id.tv_finish);
		tv_finish.setText("主页面");

		header_img.setOnClickListener(this);
		rl_name.setOnClickListener(this);
		rl_declaration.setOnClickListener(this);
		rl_telephone.setOnClickListener(this);
		rl_gender.setOnClickListener(this);
		rl_area.setOnClickListener(this);
		rl_birthday.setOnClickListener(this);
		rl_height.setOnClickListener(this);
		rl_target_weight.setOnClickListener(this);
		rl_target_time.setOnClickListener(this);
		tv_finish.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		setViewData();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO 自动生成的方法存根
		if (myWindow != null)
			myWindow.dismiss();
		super.onPause();
	}

	private void setViewData() {
		// TODO 自动生成的方法存根
		NiceUserInfo info = NiceUserInfo.getInstance();
		ImageLoaderUtils.getInstance().displayUserHeadPicWithOutCache(
				info.getHead_pic(), header_img);
		setTextView(info.getName(), tv_name);
		setTextView(info.getDeclaration(), tv_declaration);
		setTextView(info.getTelephone(), tv_telephone);
		setTextView(info.getGender(), tv_gender);
		setTextView(info.getArea(), tv_area);
		setTextView(info.getBirthday(), tv_birthday);
		setTextView(info.getHeight(), tv_height);
		setTextView(info.getWeight(), tv_weight);
		setTextView(info.getTargetWeight(), tv_target_weight);
		setTextView(info.getTargetTime(), tv_target_time);
	}

	private void setTextView(String info, TextView tv) {
		if (info == null || "null".equals(info) || "".equals(info)) {
			tv.setText("请填写");
		} else {
			tv.setText(info);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.tv_finish:
			if (showSavePic)
				startDownloadService(NiceUserInfo.getInstance().getHead_pic());
			Intent intent = new Intent(this,HomeActivity.class);
			startActivity(intent);
			break;

		case R.id.img_header_pic_home_my_yes: // 头像
			myWindow = new MyPopupWindow(this, SecondRegisterActivity.this, this,
					true);
			myWindow.setOutsideTouchable(true); // 设置外部可以被点击
			myWindow.setBackgroundDrawable(new BitmapDrawable());
			myWindow.showAtLocation(v, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
			break;

		case R.id.rl_new_name: // 用户昵称
			this.startActivity(new Intent(this, UserNameActivity.class));
			break;

		case R.id.rl_declaration_of_thin_body: // 美丽宣言
			this.startActivity(new Intent(this, UserDeclareActivity.class));
			break;

		case R.id.rl_telephone_number: // 手机
			this.startActivity(new Intent(this, UserTelephoneActivity.class));
			break;

		case R.id.rl_gender: // 性别
			this.startActivity(new Intent(this, UserGenderActivity.class));
			break;

		case R.id.rl_area: // 地区
			this.startActivity(new Intent(this, UserAreaActivity.class));
			break;

		case R.id.rl_brithday: // 生日
			this.startActivity(new Intent(this, UserBirthdayActivity.class));
			break;

		case R.id.rl_new_height: // 身高
			this.startActivity(new Intent(this, UserHeightActivity.class));
			break;

		case R.id.rl_new_weight: // 目标体重
			this.startActivity(new Intent(this, UserWeightActivity.class));
			break;

		case R.id.rl_target_time: // 目标体重时间
			this.startActivity(new Intent(this, UserTargetTimeActivity.class));
			break;

		default:
			break;
		}
	}
	
	/**
	 * 上传头像
	 * 
	 * @param progress
	 * @param url
	 */
	public void getPhoto(boolean progress, final File url) {
		if (progress) {
			RequestParams params = new RequestParams();
			try {
				params.put("pic_file", url);
				params.put(NiceConstants.sign, MD5Utils.getSign());
				params.put(NiceConstants.UID, NiceUserInfo.getInstance()
						.getUId());
				String apiUrl = UrlHelper.getAbsoluteUrl(UrlHelper.HEADPIC);
				LogUtil.i(LOG_TAG, "getPhoto->params:" + params);
				LogUtil.i(LOG_TAG, "getPhoto->apiUrl:" + apiUrl);
				AsyncHttpRequestUtil.post(apiUrl, params,
						new AsyncHttpResponseHandler() {
							@Override
							public void onStart() {
								super.onStart();
							}

							@Override
							public void onSuccess(int statusCode, String content) {
								LogUtil.i(LOG_TAG, "getPhoto->onSuccess:"
										+ content);
								parseJson(content);
							}

							@Override
							public void onFailure(int statusCode,
									Throwable error, String content) {
							}

							@Override
							public void onFinish() {

								super.onFinish();
							}

						});
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void parseJson(String jsonStr) {
		try {
			JSONObject jObject = new JSONObject(jsonStr);
			int rCode = jObject.getInt(NiceConstants.RECODE);
			if (rCode == 0) {
				String url = jObject.getString(NiceConstants.RECONTENT);

				LogUtil.i(LOG_TAG, "上传头像成功后url:" + url);
				NiceUserInfo.getInstance().setHead_pic(url);
				// ImageLoaderUtils.getInstance().displayUserHeadPicWithOutCache(url,userHeadPic);
				ImageLoaderUtils.getInstance().clearDiscCache();
				ImageLoaderUtils.getInstance().clearMemoryCache();
				Message msg = new Message();
				msg.what = 0;
				msg.obj = url;
				handler.sendMessageDelayed(msg, 500);
				Toast.makeText(this, "头像上传成功", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startDownloadService(String url) {
		if (url == null) {
			return;
		}
		// url = url.substring(0, url.indexOf("?"));
		Intent startService = new Intent(this, DownHeadPicService.class);
		startService.putExtra(DownHeadPicService.DOWNLOAD_URL_FLAG, url);
		startService(startService);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		myWindow.onActivityResult(requestCode, resultCode, data);
	}

}
