package com.example.carl.orderdishes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.carl.orderdishes.R;
import com.example.carl.orderdishes.util.Content;
import com.example.carl.orderdishes.util.TwitterRestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity {
    // 声明登录、取消按钮
    private Button cancelBtn,loginBtn,exitBtn,registerBtn;
    // 声明用户名、密码输入框
    private EditText userEditText,pwdEditText;

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
        cancelBtn = (Button)findViewById(R.id.cancelButton);
        // 通过findViewById方法实例化组件
        loginBtn = (Button)findViewById(R.id.loginButton);
//        exitBtn = (Button)findViewById(R.id.exitButton);
//        registerBtn = (Button)findViewById(R.id.registerButton);
        // 通过findViewById方法实例化组件
        userEditText = (EditText)findViewById(R.id.userEditText);
        // 通过findViewById方法实例化组件
        pwdEditText = (EditText)findViewById(R.id.pwdEditText);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userEditText.setText("");
                pwdEditText.setText("");
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

    }


    private void login(){
        String url = "AppLogin";
        RequestParams requestParams = new RequestParams();
        final String userName = userEditText.getText().toString().trim();
        final String userPwd = pwdEditText.getText().toString().trim();
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
}
