package net.nmss.nice.activity;

import java.util.Random;

import net.nmss.nice.AppManager;
import net.nmss.nice.R;
import net.nmss.nice.bean.CodeBean;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.interfaces.QQLoginCallback;
import net.nmss.nice.share.Conf;
import net.nmss.nice.thirdpartylogin.QQConnection;
import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.MD5Utils;
import net.nmss.nice.utils.NiceConstants;
import net.nmss.nice.utils.PreferenUtil;
import net.nmss.nice.utils.UrlHelper;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activate.AppActivateTool;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class LoginActivity extends BaseActivity implements QQLoginCallback{

	private final static String LOG_TAG = "LoginActivity";
	private EditText et_login_username;
	private EditText et_login_userpwd;
	private TextView tvBack;

	RequestHandle loginHandle;
	RequestHandle qqLoginHandle;
	RequestHandle weiBoLoginHandle;
	RequestHandle wxLoginHandle;
	private String userName;
	private String userPwd;

	private QQConnection qqConn;
	private IWXAPI api;

	private boolean canBack = true;
	private boolean isClick = false;
	CodeBean bean;
	NiceUserInfo info = NiceUserInfo.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		if (getIntent().getExtras().containsKey("canBack")) {
			canBack = getIntent().getExtras().getBoolean("canBack");
		}
		tvBack = (TextView) findViewById(R.id.tv_back);
		if (canBack) {
			tvBack.setVisibility(View.VISIBLE);
		} else {
			tvBack.setVisibility(View.GONE);
			AppManager.getInstance().killOtherActivity(LoginActivity.class);
		}

		initViews();
		qqConn = new QQConnection(this);
		qqConn.setQqLoginCallback(this);
		
		api = WXAPIFactory.createWXAPI(this, Conf.APP_ID, false);
		api.registerApp(Conf.APP_ID);

	}

	private void initViews() {
		et_login_username = (EditText) findViewById(R.id.et_login_username);
		et_login_userpwd = (EditText) findViewById(R.id.et_login_userpwd);
	}
	
	
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		if(isClick)
			WXLogin();
		super.onResume();
	}

	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.tv_back:// 返回箭头
			// finish();
			onBackPressed();
			break;
		case R.id.btn_login_login:// 登陆按钮
			NiceConstants.LoginType = "会员";
			doLogin();
			break;
		case R.id.tv_login_forget_password:// 忘记密码
			startActivity(new Intent(this, ForgetPasswordActivity.class));
			break;

		case R.id.tv_login_quick_login:// 快速注册
			startActivity(new Intent(this, FirstRegisterActivity.class));
			break;
		case R.id.btn_login_qq:// QQ登陆
			NiceConstants.LoginType = "QQ";
			if (weiBoLoginHandle != null && !weiBoLoginHandle.isFinished()) {
				weiBoLoginHandle.cancel(true);
			}
			qqConn.login();
			break;

		case R.id.btn_login_weixin:// 微信登陆
			NiceConstants.LoginType = "微信";
			isClick = true;
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "wechat_sdk_demo_test";
			api.sendReq(req);
			break;

		case R.id.tv_try_login:
			tryLogin();
		default:
			break;
		}
	}

	private void tryLogin() {
		NiceConstants.LoginType = "游客";
		Random random = new Random();
		NiceUserInfo userInfo = NiceUserInfo.getInstance();
		userInfo.setDeclaration("");
		userInfo.setName("游客" + random.nextInt(1000));
		userInfo.setHead_pic("");
		userInfo.setUId("0");
		startActivity(new Intent(this, HomeActivity.class));
		// 统计
		AppActivateTool.doDoActivate(this, "act_login");
		finish();
		AppManager.getInstance().killActivity(LoginActivity.class);
		Toast.makeText(this, "您当前的登陆状态游客...", Toast.LENGTH_SHORT).show();

	}

	private void doLogin() {
		if (!chkNetConnect() || !chkEditText())
			return;

		if (loginHandle != null && !loginHandle.isFinished()) {
			Toast.makeText(this, "正在登陆，请稍后...", Toast.LENGTH_SHORT).show();
			return;
		}
		if (weiBoLoginHandle != null && !weiBoLoginHandle.isFinished()) {
			weiBoLoginHandle.cancel(true);
		}

		userName = et_login_username.getText().toString().trim();
		userPwd = et_login_userpwd.getText().toString().trim();
		RequestParams params = new RequestParams();
		params.put(NiceConstants.username, userName);
		params.put(NiceConstants.pwd, userPwd);
		NiceUserInfo info = NiceUserInfo.getInstance();
		params.put(NiceConstants.latitude, String.valueOf(info.getLatitude()));
		params.put(NiceConstants.longitude, String.valueOf(info.getLongitude()));
		params.put(NiceConstants.sign, MD5Utils.getSign());
		LogUtil.i(LOG_TAG, UrlHelper.getAbsoluteUrl(UrlHelper.LOGIN));
		Log.i(LOG_TAG, params.toString());
		loginHandle = AsyncHttpRequestUtil.get(
				UrlHelper.getAbsoluteUrl(UrlHelper.LOGIN), params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						LogUtil.i(LOG_TAG, "onStart");
						showProgress();
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						LogUtil.i(LOG_TAG, "onSuccess content:" + content);
						parseLoginResponseJson(content);
						hideProgress();
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(LOG_TAG, "onFailure content:" + content);
						hideProgress();
						Toast.makeText(getApplicationContext(), "服务器繁忙，请稍后再试！",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void parseLoginResponseJson(String jsonStr) {
		try {
			LogUtil.i(LOG_TAG, "jsonStr:" + jsonStr);
			JSONObject jsonObject = new JSONObject(jsonStr);
			Object obj = jsonObject.get(NiceConstants.RECODE);
			if (obj instanceof Integer) {
				int rCode = jsonObject.getInt(NiceConstants.RECODE);
				if (rCode == 200000 || rCode == 0) {
					Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();

					PreferenUtil preferenUtil = new PreferenUtil(
							LoginActivity.this);
					preferenUtil.setUserNmae(userName);
					preferenUtil.setUserPwd(userPwd);
					LogUtil.i(LOG_TAG, "parseLoginResponseJson->userName:"
							+ userName);
					LogUtil.i(LOG_TAG, "parseLoginResponseJson->userPwd:"
							+ userPwd);
					JSONObject rcObj = jsonObject.getJSONObject("r_c");
					NiceUserInfo userInfo = NiceUserInfo.getInstance();
					userInfo.setDeclaration(rcObj.getString("declaration"));
					userInfo.setName(rcObj.getString("nickname"));
					userInfo.setHead_pic(rcObj.getString("head_pic"));
					userInfo.setUId(rcObj.getString("uid"));
					LogUtil.i(LOG_TAG,
							"parseLoginResponseJson:startActivity(new Intent(this, HomeActivity.class));");

					startActivity(new Intent(this, HomeActivity.class));
					// 统计
					AppActivateTool.doDoActivate(this, "act_login");
					finish();
					AppManager.getInstance().killActivity(LoginActivity.class);
				} else if (rCode > 0) {
					Toast.makeText(this,
							jsonObject.getString(NiceConstants.RECONTENT),
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (JSONException e) {
			LogUtil.exception(e);
		}
	}

	private void WXLogin(){
		LogUtil.i(LOG_TAG, "onWXLoginComplete()");
		showProgress();
		Toast.makeText(this, "认证登陆中，请稍后", Toast.LENGTH_SHORT).show();
		RequestParams params = new RequestParams();
		params.put("appid", Conf.APP_ID);
		params.put("secret", Conf.APP_Secret);
		params.put("code", Conf.APP_Code);
		params.put("grant_type", "authorization_code");
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
		LogUtil.i(LOG_TAG, "params:" + params);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler(){
			@Override
			public void onStart() {
				// TODO 自动生成的方法存根
				super.onStart();
				LogUtil.i(LOG_TAG,"getCode->onStart");
			}
			
			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO 自动生成的方法存根
				parseGetCode(content);
				LogUtil.i(LOG_TAG,"getCode->onSuccess");
			}
			
			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				LogUtil.i(LOG_TAG, "getCode->onFailure" + content);
				hideProgress();
				Toast.makeText(getApplicationContext(), "授权失败,请稍后尝试", Toast.LENGTH_SHORT).show();
			}
			
		});
		
	}
	
	private void parseGetCode(String jsonStr){
		try {
			bean = new CodeBean();
			JSONObject obj = new JSONObject(jsonStr);
			bean.setAccess(obj.getString("access_token"));
			bean.setRefresh(obj.getString("refresh_token"));
			bean.setOpenId(obj.getString("openid"));
			getUserInfo();
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	private void getUserInfo(){
		RequestParams params = new RequestParams();
		params.put("access_token", bean.getAccess());
		params.put("openid", bean.getOpenId());
		LogUtil.i(LOG_TAG,""+params);
		String url = "https://api.weixin.qq.com/sns/userinfo";
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler(){
			@Override
			public void onStart() {
				// TODO 自动生成的方法存根
				super.onStart();
				LogUtil.i(LOG_TAG,"getUserInfo->onStart");
			}
			
			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO 自动生成的方法存根
				parseUserInfo(content);
				LogUtil.i(LOG_TAG,"getUserInfo->onSuccess");
			}
			
			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				LogUtil.i(LOG_TAG, "getUserInfo->onFailure" + content);
				hideProgress();
				Toast.makeText(getApplicationContext(), "获取微信用户信息失败，请稍后再试", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void parseUserInfo(String json){
		try {
			JSONObject obj = new JSONObject(json);
			info.setName(obj.getString("nickname"));
			info.setHead_pic(obj.getString("headimgurl"));
			int sex = obj.getInt("sex");
			if(sex == 1){
				info.setGender("男");
			}else{
				info.setGender("女");
			}
			onWXLogin();
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	private void onWXLogin(){
		if (loginHandle != null && !loginHandle.isFinished()) {
			loginHandle.cancel(true);
		}
		if (weiBoLoginHandle != null && !weiBoLoginHandle.isFinished()) {
			weiBoLoginHandle.cancel(true);
		}
		RequestParams params = new RequestParams();
		params.put("sign", MD5Utils.getSign());
		params.put("nickname", info.getName());
		params.put("openid", bean.getOpenId());
		params.put("type", "3");
		params.put(NiceConstants.longitude, info.getLongitude());
		params.put(NiceConstants.latitude, info.getLatitude());
		String metaData = getMetaData("UMENG_CHANNEL");

		if (metaData != null && metaData.contains("_")) {
			String[] channels = metaData.split("_");
			String[] p = { "st", "wf", "sid" };
			for (int i = 0; i < channels.length; i++) {
				params.put(p[i], channels[i]);
			}
		}
		LogUtil.i(LOG_TAG, "params:" + params.toString());
		LogUtil.i(LOG_TAG,"url:" + UrlHelper.getAbsoluteUrl(UrlHelper.OPENLOGIN));
		wxLoginHandle = AsyncHttpRequestUtil.get(
				UrlHelper.getAbsoluteUrl(UrlHelper.OPENLOGIN), params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						showProgress();
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "onWXLogin->onSuccess content:" + content);
						hideProgress();
						parseLoginResponseJson(content);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(LOG_TAG, "onWXLogin->onFailure content:" + content);
						hideProgress();
						Toast.makeText(getApplicationContext(), "服务器繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
					}

				});	
	}
	
	private boolean chkEditText() {
		String userName = et_login_username.getText().toString();
		if ("".equals(userName.trim())) {
			et_login_username.setError("用户名不能为空");
			return false;
		}
		String userPwd = et_login_userpwd.getText().toString();
		if ("".equals(userPwd.trim())) {
			et_login_userpwd.setError("密码不能为空");
			return false;
		}
		return true;
	}

	/**
	 * QQ认证成功后执行。
	 */
	@Override
	public void onQQLoginComplete() {
		LogUtil.i(LOG_TAG, "onQQLoginComplete()");
		// TODO Auto-generated method stub
		// startActivity(new Intent(this, HomeActivity.class));
		if (qqLoginHandle != null && !qqLoginHandle.isFinished()) {
			Toast.makeText(this, "正在登陆中，请稍后", Toast.LENGTH_SHORT).show();
			return;
		}

		if (loginHandle != null && !loginHandle.isFinished()) {
			loginHandle.cancel(true);
		}
		if (weiBoLoginHandle != null && !weiBoLoginHandle.isFinished()) {
			weiBoLoginHandle.cancel(true);
		}

		NiceUserInfo info = NiceUserInfo.getInstance();
		RequestParams params = new RequestParams();
		params.put("sign", MD5Utils.getSign());
		params.put("openid", info.getUId());
		params.put("nickname", info.getName());
		params.put("type", "0");
		params.put(NiceConstants.longitude, info.getLongitude());
		params.put(NiceConstants.latitude, info.getLatitude());
		String metaData = getMetaData("UMENG_CHANNEL");

		if (metaData != null && metaData.contains("_")) {
			String[] channels = metaData.split("_");
			String[] p = { "st", "wf", "sid" };
			for (int i = 0; i < channels.length; i++) {
				params.put(p[i], channels[i]);
			}
		}
		LogUtil.i(LOG_TAG, "params:" + params.toString());
		LogUtil.i(LOG_TAG,
				"url:" + UrlHelper.getAbsoluteUrl(UrlHelper.OPENLOGIN));
		qqLoginHandle = AsyncHttpRequestUtil.get(
				UrlHelper.getAbsoluteUrl(UrlHelper.OPENLOGIN), params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						showProgress();
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "onSuccess content:" + content);
						hideProgress();
						parseLoginResponseJson(content);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(LOG_TAG, "onFailure content:" + content);
						hideProgress();
						Toast.makeText(getApplicationContext(), "服务器繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
					}

				});

	}

}
