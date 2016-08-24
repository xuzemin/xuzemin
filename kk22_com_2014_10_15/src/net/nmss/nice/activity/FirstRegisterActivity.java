package net.nmss.nice.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.activate.AppActivateTool;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import net.nmss.nice.AppManager;
import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.MD5Utils;
import net.nmss.nice.utils.NiceConstants;
import net.nmss.nice.utils.UrlHelper;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstRegisterActivity extends BaseActivity {
	private final static String LOG_TAG = "FirstRegisterActivity";
	private EditText etPhoneNum;
	private EditText etSMSNum;
	private EditText etPwd;
	private Button btnGetSMSCode;
	private final int SMSCodeTime = 0;
	private int timeCount = 120;
	private RequestHandle getSMSCodeHandler;
	private RequestHandle registerHandler;
	private String phoneNum;
	private Handler mHandler;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_register_layout);
		initViews();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SMSCodeTime:
					if (getString4Res(R.string.get_random_num).equals(
							btnGetSMSCode.getText().toString())) {
						btnGetSMSCode.setEnabled(false);
						btnGetSMSCode.setText(String.valueOf(timeCount--));
						mHandler.sendEmptyMessageDelayed(SMSCodeTime, 1000);
					} else if (timeCount == 0) {
						btnGetSMSCode.setEnabled(true);
						btnGetSMSCode
								.setText(getString4Res(R.string.get_random_num));
						timeCount = 120;
					} else {
						btnGetSMSCode.setText(String.valueOf(timeCount--));
						mHandler.sendEmptyMessageDelayed(SMSCodeTime, 1000);
					}
					break;
				default:
					break;
				}
			}
		};
	}

	private void initViews() {
		etPhoneNum = (EditText) findViewById(R.id.et_register_phone_num);
		etSMSNum = (EditText) findViewById(R.id.et_register_ramdon_num);
		etPwd = (EditText) findViewById(R.id.et_register_pwd);
		btnGetSMSCode = (Button) findViewById(R.id.btn_register_get_sms_code);
	}

	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.img_register_title_back:// 返回按钮
			finish();
			break;
			
		case R.id.btn_register_get_sms_code:// 获取短信验证码
			hideKeyBoard();
			if (chkNetConnect() && !chkEditTextsForSMSCODE()) {
				return;
			}
			if (getSMSCodeHandler != null && !getSMSCodeHandler.isFinished()) {
				Toast.makeText(this, "获取验证码中，请稍后...", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			getSMSCoce();
			break;
			
		case R.id.btn_register:
			if (!super.chkEditText(etPhoneNum, etSMSNum, etPwd)) {
				return;
			}
			if (registerHandler != null && !registerHandler.isFinished()) {
				Toast.makeText(this, "正在注册,请稍后...", Toast.LENGTH_SHORT).show();
			}
			doRegister();
			break;
			
		default:
			break;
		}
	}

	private boolean chkEditTextsForSMSCODE() {
		boolean flag = super.chkEditText(etPhoneNum);
		return flag;
	}

	private void getSMSCoce() {
		RequestParams params = new RequestParams();
		phoneNum = etPhoneNum.getText().toString().trim();
		params.put(NiceConstants.TELEPHONE, phoneNum);
		params.put(NiceConstants.sign, MD5Utils.getSign());
		getSMSCodeHandler = AsyncHttpRequestUtil.get(
				UrlHelper.getAbsoluteUrl(UrlHelper.GETVERIFICATION), params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						LogUtil.i(LOG_TAG, "onStart");
						showProgress();
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "onSuccess:" + content);
						parseGetSMSJson(content);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(LOG_TAG, "onFinish:" + content);
						Toast.makeText(getApplicationContext(), "获取手机验证码失败...", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						hideProgress();
						super.onFinish();
					}
				});
	}

	private void parseGetSMSJson(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			Object obj = jsonObject.get(NiceConstants.RECODE);
			int rCode = -1;
			if (obj instanceof Integer) {
				rCode = jsonObject.getInt(NiceConstants.RECODE);
			} else {
				return;
			}
			if (rCode == 0) {
				mHandler.sendEmptyMessage(SMSCodeTime);
			}
			String rContent = (String) jsonObject.get(NiceConstants.RECONTENT);
			Toast.makeText(this, rContent, Toast.LENGTH_SHORT).show();

		} catch (JSONException e) {
			LogUtil.exception(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doRegister(){
		RequestParams params = new RequestParams();
		params.put("telephone", phoneNum);
		params.put("verification", etSMSNum.getText().toString().trim());
		params.put("pwd", etPwd.getText().toString().trim());
		params.put(NiceConstants.registerChannel, String.valueOf(35));
		
		String metaData = getMetaData("UMENG_CHANNEL");
		if (metaData != null && metaData.contains("_")) {
			String[] channels = metaData.split("_");
			String[] p = { "st", "wf", "sid" };
			for (int i = 0; i < channels.length; i++) {
				params.put(p[i], channels[i]);
			}
		}
		String url = UrlHelper.getAbsoluteUrl(UrlHelper.REGISTER);
		LogUtil.i(LOG_TAG,""+params);
		LogUtil.i(LOG_TAG,""+url);
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler(){
			@Override
			public void onStart() {
				LogUtil.i(LOG_TAG, "doRegister onStart");
				super.onStart();
				showProgress();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				LogUtil.i(LOG_TAG, "doRegister onSuccess:" + content);
				parseRegisterJson(content);
				hideProgress();
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				LogUtil.i(LOG_TAG, "doRegister onFailure:" + content);
				// parseRegisterJson(content);
				hideProgress();
			}
		});
	}
	
	private void parseRegisterJson(String jsonStr){
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			int r_e = jsonObject.getInt("r_e");
			if(r_e == 0){
				Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
				NiceUserInfo info = NiceUserInfo.getInstance();
				JSONObject jobj = jsonObject.getJSONObject("r_c");
				info.setUId(jobj.getString("uid"));
				info.setName(jobj.getString("nickname"));
				info.setHead_pic(jobj.getString("head_pic"));
				info.setDeclaration(jobj.getString("declaration"));

				Intent intent = new Intent(this, SecondRegisterActivity.class);			
				startActivity(intent);
				finish();
				// 统计
				AppManager.getInstance().killActivity(LoginActivity.class);
				AppActivateTool.doDoActivate(this, "act_login");
			}else{
				Toast.makeText(this, jsonObject.getString("r_c"), Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
}
