package net.nmss.nice.thirdpartylogin;

import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.interfaces.QQLoginCallback;
import net.nmss.nice.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * userInfo
 * 
 * {
	"ret": 0,
	"pay_token": "824CF32BBF0B7FAD0933EA250E082738",
	"pf": "desktop_m_qq-10000144-android-2002-",
	"openid": "BF651AE6A17903578A66AFFDAFECF0B3",
	"expires_in": 7776000,
	"pfkey": "ff1b65c94586c28023fbe2065d4abba7",
	"msg": "",
	"access_token": "234BBC61D9C69A4D9E1C199E85D35D6F"
}

 * {
	"is_yellow_year_vip": "0",
	"ret": 0,
	"figureurl_qq_1": "http:\/\/q.qlogo.cn\/qqapp\/222222\/BF651AE6A17903578A66AFFDAFECF0B3\/40",
	"figureurl_qq_2": "http:\/\/q.qlogo.cn\/qqapp\/222222\/BF651AE6A17903578A66AFFDAFECF0B3\/100",
	"nickname": "Mr.Boolean",
	"yellow_vip_level": "0",
	"is_lost": 0,
	"msg": "",
	"figureurl_1": "http:\/\/qzapp.qlogo.cn\/qzapp\/222222\/BF651AE6A17903578A66AFFDAFECF0B3\/50",
	"vip": "0",
	"level": "0",
	"figureurl_2": "http:\/\/qzapp.qlogo.cn\/qzapp\/222222\/BF651AE6A17903578A66AFFDAFECF0B3\/100",
	"is_yellow_vip": "0",
	"gender": "男",
	"figureurl": "http:\/\/qzapp.qlogo.cn\/qzapp\/222222\/BF651AE6A17903578A66AFFDAFECF0B3\/30"
}
 * 


 *
 */
public class QQConnection {
	private final static String LOG_TAG = "QQConnection";
	private final String mAppid = "1102001493";
	private Tencent mTencent;
	private static QQAuth mQQAuth;
	private Activity mActivity;
	private UserInfo mInfo;
	private QQLoginCallback qqLoginCallback;

	public QQConnection(Activity activity) {
		mQQAuth = QQAuth.createInstance(mAppid,
				activity);
		mTencent = Tencent.createInstance(mAppid, activity);
		this.mActivity = activity;
	}

	
	public QQLoginCallback getQqLoginCallback() {
		return qqLoginCallback;
	}


	public void setQqLoginCallback(QQLoginCallback qqLoginCallback) {
		this.qqLoginCallback = qqLoginCallback;
	}


	public void login() {
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					LogUtil.i(LOG_TAG,
							"login->doComplete:json=" + values.toString());
					updateUserInfo();
					NiceUserInfo userInfo = NiceUserInfo.getInstance();
					try {
						userInfo.setUId(values.getString("openid"));
					} catch (JSONException e) {
						LogUtil.exception(e);
						LogUtil.i(LOG_TAG, "JSONException:"+e.getMessage());
					}
					//updateLoginButton();
				}
			};
			// mQQAuth.login(this, "all", listener);
			// mTencent.loginWithOEM(this, "all",
			// listener,"10000144","10000144","xxxx");
			mTencent.login(mActivity, "all", listener);
			LogUtil.i(LOG_TAG, "mTencent.login(mActivity, \"all\", listener);");
		} else {
			mQQAuth.logout(mActivity);
			updateUserInfo();
			// updateLoginButton();
			LogUtil.i(LOG_TAG, "mQQAuth.logout(mActivity);");
		}
	}

	/**
	 * 获取用户信息
	 */
	public void updateUserInfo() {
		LogUtil.i(LOG_TAG, "updateUserInfo");
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				@Override
				public void onError(UiError e) {
					LogUtil.i(LOG_TAG, "updateUserInfo->IUiListener->onError:"
							+ e.errorDetail);
				}

				@Override
				public void onComplete(final Object response) {
					LogUtil.i(LOG_TAG, "updateUserInfo->IUiListener->onComplete:"
							+ ((JSONObject) response).toString());
					JSONObject jsonObject = (JSONObject)response;
					NiceUserInfo userInfo = NiceUserInfo.getInstance();
					try {
						userInfo.setName(jsonObject.getString("nickname"));
						userInfo.setHead_pic(jsonObject.getString("figureurl_qq_2"));
						userInfo.setLoginType(NiceUserInfo.qqIn);
						String gender = jsonObject.getString("gender");
						if("男".equals(gender)) {
							userInfo.setGender(NiceUserInfo.MAN);
						}else {
							userInfo.setGender(NiceUserInfo.LADY);
						}
						qqLoginCallback.onQQLoginComplete();
					} catch (JSONException e) {
						LogUtil.exception(e);
						LogUtil.i(LOG_TAG, e.getMessage());
					}
				}
				
				@Override
				public void onCancel() {
					LogUtil.i(LOG_TAG, "updateUserInfo->IUiListener->onCancel");
				}
			};
			// MainActivity.mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO,
			// null,
			// Constants.HTTP_GET, requestListener, null);
			mInfo = new UserInfo(mActivity.getApplicationContext(),mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);
			LogUtil.i(LOG_TAG, mInfo.toString());
		} else {
			
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mTencent.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @author Administrator
	 * 登陆用的回调
	 *
	 */
	class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
//			Toast.makeText(mActivity, "登陆成功：", Toast.LENGTH_SHORT)
//					.show();
			// 对收到的信息进行处理
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Toast.makeText(mActivity, "登陆失败",
					Toast.LENGTH_SHORT).show();
			LogUtil.i(LOG_TAG, "onError:"+e.errorMessage);
		}

		@Override
		public void onCancel() {
//			Toast.makeText(mActivity, "onCancel", Toast.LENGTH_SHORT).show();
		}
	}
}
