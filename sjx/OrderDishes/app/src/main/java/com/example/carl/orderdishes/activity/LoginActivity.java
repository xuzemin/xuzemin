package com.example.carl.orderdishes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.carl.orderdishes.R;
import com.example.carl.orderdishes.util.Content;
import com.example.carl.orderdishes.util.TwitterRestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    // 声明登录、取消按钮
    private Button cancelBtn,loginBtn;
    // 声明用户名、密码输入框
    private EditText userEditText,pwdEditText;
    private RelativeLayout login_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        // 通过findViewById方法实例化组件
        cancelBtn = findViewById(R.id.resetButton);

        login_layout = findViewById(R.id.login_layout);
        // 通过findViewById方法实例化组件
        loginBtn = findViewById(R.id.loginButton);
        // 通过findViewById方法实例化组件
        userEditText = findViewById(R.id.userEditText);
        // 通过findViewById方法实例化组件
        pwdEditText = findViewById(R.id.pwdEditText);
        cancelBtn.setOnClickListener(this);
        login_layout.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Content.dm = dm;
    }


    private void login(){
        String url = "AppLogin";
        RequestParams requestParams = new RequestParams();
//        final String userName = userEditText.getText().toString().trim();
//        final String userPwd = pwdEditText.getText().toString().trim();
        final String userName = "111";
        final String userPwd = "123";
        requestParams.add("userName",userName);
        requestParams.add("userPw",userPwd);
        TwitterRestClient.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                LOG_e(new String(responseBody));
                LOG_e(new String(statusCode+""));
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    int Result = jsonObject.getInt("ResultCode");
                    if(Result == Content.OK){
                        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(Content.Model,0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("user_id", jsonObject.getInt("id"));
                        editor.putInt("sohp_id", jsonObject.getInt("sohpid"));
                        editor.putString("userName",userName);
                        editor.putString("userPw",userPwd);
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this,DeskChooseActivity.class));
                    }
                    Toast.makeText(getApplicationContext(),jsonObject.getString("Result"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"数据解析有误，请重试",Toast.LENGTH_SHORT).show();
                }
                userEditText.setText("");
                pwdEditText.setText("");
                hideProgress();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"连接失败，请检查网络",Toast.LENGTH_SHORT).show();
                hideProgress();
            }
    });
        showProgress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_layout:
            case R.id.loginButton:
                login();
                break;
            case R.id.resetButton:
                userEditText.setText("");
                pwdEditText.setText("");
                break;
        }
    }
}
