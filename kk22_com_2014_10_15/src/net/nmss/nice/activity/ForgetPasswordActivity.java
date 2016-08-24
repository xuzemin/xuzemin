package net.nmss.nice.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import net.nmss.nice.R;
import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.NiceConstants;
import net.nmss.nice.utils.UrlHelper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPasswordActivity extends BaseActivity {

	private final static String LOG_TAG = "ForgetPasswordActivity";
	private EditText phone_email;
	private TextView tv_title;
	//第二部分
	private TextView tv_text;
	private EditText et_telephone;
	private EditText et_nickname;
	private EditText et_smsCode;
	private EditText et_newPwd;
	//控制视图切换
	private LinearLayout ll_reset_pwd1;
	private LinearLayout ll_reset_pwd2;
	
	private String phoneNum;
	
	private RequestHandle sendRequestHandle;
	private RequestHandle sendNewPwdHandle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgetpwd_layout);
		initViews();
		changeToSendRequest();
	}

	private void initViews() {
		tv_title = (TextView) this.findViewById(R.id.title_center_content);
		tv_title.setText("忘记密码?");
		phone_email = (EditText) findViewById(R.id.et_forgetpwd_input_num);
		tv_text = (TextView) findViewById(R.id.tv_forgetpwd_text);
		et_telephone = (EditText)findViewById(R.id.et_forgetpwd_telephone_2);
		et_nickname = (EditText) findViewById(R.id.et_forgetpwd_sure_pwd);
		et_smsCode = (EditText) findViewById(R.id.et_forgetpwd_smscode);
		et_newPwd = (EditText) findViewById(R.id.et_forgetpwd_new_pwd);
		ll_reset_pwd1 = (LinearLayout) findViewById(R.id.ll_reset_pwd_layout);
		ll_reset_pwd2 = (LinearLayout) findViewById(R.id.ll_reset_2_pwd_layout);
	}

	
	/**
	 * 切换到发短信或发邮件请求重置密码界面
	 */
	private void changeToSendRequest() {
		ll_reset_pwd1.setVisibility(View.VISIBLE);
		ll_reset_pwd2.setVisibility(View.GONE);
		tv_text.setVisibility(View.GONE);
	}
	
	/**
	 * 切换到发送新密码界面
	 */
	private void changeToSendNewPwd() {
		ll_reset_pwd1.setVisibility(View.GONE);
		ll_reset_pwd2.setVisibility(View.VISIBLE);
		tv_text.setVisibility(View.VISIBLE);
		et_telephone.setText(phoneNum);
	}
	
	
	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.btn_forgetpwd_reset_pwd:// 重置密码
			if (!chkNetConnect() || !chkEditText()) 
				return;
			if (sendRequestHandle != null && !sendRequestHandle.isFinished()) {
				Toast.makeText(this, "正在发送重置密码请求。请稍后....", Toast.LENGTH_LONG).show();
				return;
			}
			String str = phone_email.getText().toString().trim();
			sendReSetPwdRequest(str);
			break;
		case R.id.img_forget_pwd_title_left_img_back:// 返回
			finish();
			break;
			
		case R.id.btn_forgetpwd_reset_pwd_2://发送确认修改密码
			if (!chkNetConnect() || !chkEditText2()) 
				return;
			if(sendNewPwdHandle != null && !sendNewPwdHandle.isFinished()) {
				Toast.makeText(this, "正在修改密码中，请稍后...", Toast.LENGTH_SHORT).show();
				return;
			}
			sendNewPwdRequest();
			break;
		case R.id.tv_forgetpwd_is_get_sms_code:
			changeToSendNewPwd();
			break;
		case R.id.tv_forgetpwd_resend_sms_code:
			changeToSendRequest();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(ll_reset_pwd2.getVisibility() == View.VISIBLE) {
			changeToSendRequest();
			return;
		}
		super.onBackPressed();
	}
	
	private void sendReSetPwdRequest(String str) {
		RequestParams requestParams = new RequestParams();
		requestParams.add(NiceConstants.username, str);
		if (str.contains("@")) {
			requestParams.add(NiceConstants.TYPE, "1");
		} else {
			requestParams.add(NiceConstants.TYPE, "0");
			phoneNum = str;
		}
		
		LogUtil.i(LOG_TAG, UrlHelper.getAbsoluteUrl(UrlHelper.RESETPWD));
		LogUtil.i(LOG_TAG, "requestParams:"+requestParams.toString());
		sendRequestHandle = AsyncHttpRequestUtil.get(
				UrlHelper.getAbsoluteUrl(UrlHelper.RESETPWD), requestParams,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						showProgress();
						LogUtil.i(LOG_TAG, "onStart");
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "onSuccess content:" + content);
						hideProgress();
						parseGetSMSCodeJson(content);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(LOG_TAG, "onFailure content:" + content);
						hideProgress();
						parseGetSMSCodeJson(content);
					}

				});
	}

	private void sendNewPwdRequest() {
		
		RequestParams requestParams = new RequestParams();
//		requestParams.put(NiceConstants.NICKNAME, getETText(et_nickname));
		requestParams.put(NiceConstants.TELEPHONE, getETText(et_telephone));
		requestParams.put(NiceConstants.CODE, getETText(et_smsCode));
		requestParams.put(NiceConstants.NEW_PWD, getETText(et_newPwd));
		
		sendNewPwdHandle = AsyncHttpRequestUtil.get(
				UrlHelper.getAbsoluteUrl(UrlHelper.AUTHENTICATION), 
				requestParams, new AsyncHttpResponseHandler(){
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						LogUtil.i(LOG_TAG, "sendNewPwdRequest onStart");
						showProgress();
						super.onStart();
					}
					
					@Override
					public void onSuccess(int statusCode, String content) {
						LogUtil.i(LOG_TAG, "onSuccess:"+content);
						hideProgress();
						parseSendNewPwdJson(content);
					}
					
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						LogUtil.i(LOG_TAG, "onFailure:"+content);
						hideProgress();
						parseSendNewPwdJson(content);
					}
					
				});
	}
	
	
	private boolean chkEditText() {
		if ("".equals(phone_email.getText().toString().trim())) {
			phone_email.setError("手机或邮箱不能为空");
			phone_email.setText("");
			return false;
		}
		return true;
	}

	private boolean chkEditText2() {
		if ("".equals(et_telephone.getText().toString().trim())) {
			phone_email.setError("手机或邮箱不能为空");
			phone_email.setText("");
			return false;
		}
//		if ("".equals(et_nickname.getText().toString().trim())) {
//			et_nickname.setError("用户名不能为空");
//			et_nickname.setText("");
//			return false;
//		}
		if ("".equals(et_smsCode.getText().toString().trim())) {
			et_smsCode.setError("手机验证码不能为空");
			et_smsCode.setText("");
			return false;
		}
		if ("".equals(et_newPwd.getText().toString().trim())) {
			et_newPwd.setError("新密码不能为空");
			et_newPwd.setText("");
			return false;
		}
		if(et_newPwd.getText().toString().trim().length() < 6) {
			et_newPwd.setError("新密码不能小于6个字符哦");
			et_newPwd.setText("");
			return false;
		}
		if(!et_nickname.getText().toString().trim()
				.equals(et_newPwd.getText().toString().trim())) {
			et_nickname.setError("输入密码不一致");
			et_nickname.setText("");
			
			return false;
		}
		return true;
	}
	
	private void parseGetSMSCodeJson(String str) {
		try {
			JSONObject jsonObject = new JSONObject(str);

			Object reCode = jsonObject.get(NiceConstants.RECODE);
			if (reCode instanceof Integer) {
				
			}else {
				Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
				return;
			}
			
			int rcode = (Integer)reCode;
			String rcontent = (String) jsonObject.get(NiceConstants.RECONTENT);
			LogUtil.i(LOG_TAG, "rcode:" + rcode);
			LogUtil.i(LOG_TAG, "rcnotent:" + rcontent);
			Toast.makeText(this, rcontent, Toast.LENGTH_LONG).show();
			if(rcode==0 && rcontent.contains("短信")) {
				changeToSendNewPwd();
			}else if(rcode >= 1){
				Toast.makeText(this, rcontent, Toast.LENGTH_SHORT).show();
			}
			
		} catch (JSONException e) {
			LogUtil.exception(e);
		}
	}

	private void parseSendNewPwdJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			Object object = jsonObject.get(NiceConstants.RECODE);
			boolean r = object instanceof Integer;
			if(!r) {
				return;
			}
			
			int rCode = (Integer) jsonObject.get(NiceConstants.RECODE);
			String rContent = (String) jsonObject.get(NiceConstants.RECONTENT);
			LogUtil.i(LOG_TAG, String.valueOf(rCode));
			LogUtil.i(LOG_TAG, rContent);
			if(rCode == 0) {
				Toast.makeText(this, rContent, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this,LoginActivity.class);
				intent.putExtra(NiceConstants.TELEPHONE, phoneNum);
				LogUtil.i(LOG_TAG, "startActivity:"+phoneNum);
				startActivity(intent);
				finish();
			}else if(rCode >= 1) {
				Toast.makeText(this, rContent, Toast.LENGTH_SHORT).show();
			}
 		} catch (JSONException e) {
 			LogUtil.exception(e);
		}
	}
}
