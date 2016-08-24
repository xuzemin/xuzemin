package net.nmss.nice.activity;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.MD5Utils;
import net.nmss.nice.utils.NiceConstants;
import net.nmss.nice.utils.PreferenUtil;
import net.nmss.nice.utils.UrlHelper;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import cn.jpush.android.api.JPushInterface;

import com.activate.AppActivateTool;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class SplashActivity extends BaseActivity {
	private final static String LOG_TAG = "SplashActivity";
	Handler mHandler;
	public static final int GOTOWELCOME = 1;
	public static final int GOTOFIRST = 2;
	public static final int CHK_LOGIN = 3;
	boolean loginSuccess = false;
	RequestHandle requestHandle;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 统计启动
		AppActivateTool.doStartActivate(this);
		View view = View.inflate(this, R.layout.splash_view, null);
		getResolution();
		setContentView(view);
		startAnimation(view);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case GOTOWELCOME:
					startActivity(new Intent(SplashActivity.this,
							WelcomeActivity.class));
					break;
				case GOTOFIRST:
					Intent login = new Intent(SplashActivity.this,HomeActivity.class);
					startActivity(login);
					break;
				case CHK_LOGIN:
					PreferenUtil util = new PreferenUtil(SplashActivity.this);
					NiceUserInfo info = NiceUserInfo.getInstance();
			        if(!chkNetConnect() || !loginSuccess){
			        	NiceConstants.LoginType = "游客";
			        	info.setName(util.getVisitName());
			        	info.setUId(util.getVisitUid());
			        	info.setPwd(util.getVisitPwd());
			        	LogUtil.i(LOG_TAG, "游客登录");
			        }else{
			        	NiceConstants.LoginType = "会员";
			        	info.setName(util.getUserName());
			        	info.setUId(util.getUID());
			        	info.setPwd(util.getUserPwd());
			        	LogUtil.i(LOG_TAG, "会员登录");
			        }
					if (requestHandle != null && !requestHandle.isFinished()) {
						requestHandle.cancel(true);
					}
					Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
					startActivity(intent);
					// 统计
					AppActivateTool.doDoActivate(SplashActivity.this,
							"act_login");
					SplashActivity.this.finish();
					break;
				default:
					break;
				}
				SplashActivity.this.finish();
			}
		};
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	private void startAnimation(View view) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		view.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				PreferenUtil preferenUtil = new PreferenUtil(
						SplashActivity.this);
				String userName = preferenUtil.getUserName();
				String userPwd = preferenUtil.getUserPwd();
				if (!"".equals(userName) && !"".equals(userPwd)) {
					LogUtil.i(LOG_TAG, "onAnimationStart->doLogin");
					LogUtil.i(LOG_TAG, "userName:" + userName);
					LogUtil.i(LOG_TAG, "userPwd:" + userPwd);
					doLogin(userName, userPwd);
				}
				LogUtil.i(LOG_TAG, "userName:" + userName);
				LogUtil.i(LOG_TAG, "userPwd:" + userPwd);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				int newVersion = chkVersion();
				if (newVersion == SplashActivity.GOTOWELCOME) {
					mHandler.sendEmptyMessage(newVersion);
					if (requestHandle != null && !requestHandle.isFinished()
							&& !requestHandle.isCancelled()) {
						requestHandle.cancel(true);
					}
					return;
				}
				if (loginSuccess) {
					mHandler.sendEmptyMessage(CHK_LOGIN);
				} else {
					mHandler.sendEmptyMessageDelayed(CHK_LOGIN, 2000);
				}

			}
		});
	}

	private void doLogin(String userName, String userPwd) {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.username, userName);
		params.put(NiceConstants.pwd, userPwd);
		NiceUserInfo info = NiceUserInfo.getInstance();
		params.put(NiceConstants.latitude, String.valueOf(info.getLatitude()));
		params.put(NiceConstants.longitude, String.valueOf(info.getLongitude()));
		params.put(NiceConstants.sign, MD5Utils.getSign());
		LogUtil.i(LOG_TAG, UrlHelper.getAbsoluteUrl(UrlHelper.LOGIN));
		LogUtil.i(LOG_TAG, params.toString());
		requestHandle = AsyncHttpRequestUtil.get(
				UrlHelper.getAbsoluteUrl(UrlHelper.LOGIN), params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						LogUtil.i(
								LOG_TAG,
								"doLogin->onStart:"
										+ System.currentTimeMillis());
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						// LogUtil.i(LOG_TAG, "onSuccess content:" + content);
						LogUtil.i(LOG_TAG, "doLogin->onSuccess:" + content);
						LogUtil.i(
								LOG_TAG,
								"doLogin->onSuccess:"
										+ System.currentTimeMillis());
						parseResponseJson(content);
						// hideProgress();
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(
								LOG_TAG,
								"doLogin->onFailure:"
										+ System.currentTimeMillis());
						// LogUtil.i(LOG_TAG, "onFailure content:" + content);
						// hideProgress();
						// startActivity(new Intent(SplashActivity.this,
						// FirstActivity.class));
					}
				});
	}

	private void parseResponseJson(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			Object obj = jsonObject.get(NiceConstants.RECODE);
			if (obj instanceof Integer) {
				int rCode = jsonObject.getInt(NiceConstants.RECODE);
				if (rCode == 0) {
					// Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
					JSONObject rcObj = jsonObject.getJSONObject("r_c");
					NiceUserInfo userInfo = NiceUserInfo.getInstance();
					userInfo.setDeclaration(rcObj.getString("declaration"));
					userInfo.setName(rcObj.getString("nickname"));
					userInfo.setHead_pic(rcObj.getString("head_pic"));
					userInfo.setUId(rcObj.getString("uid"));

					// PreferenUtil preferenUtil = new PreferenUtil(
					// SplashActivity.this);
					// preferenUtil.setUserNmae(rcObj.getString("nickname"));
					// preferenUtil.setUserNmae(rcObj.getString("uid"));

					LogUtil.i(LOG_TAG, "NiceUserInfo:" + userInfo.toString());
					loginSuccess = true;
				} else if (rCode > 0) {
					// Toast.makeText(this,
					// jsonObject.getString(NiceConstants.RECONTENT),
					// Toast.LENGTH_SHORT).show();
					loginSuccess = false;
				}
			}
		} catch (JSONException e) {
			LogUtil.exception(e);
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
	}
}
