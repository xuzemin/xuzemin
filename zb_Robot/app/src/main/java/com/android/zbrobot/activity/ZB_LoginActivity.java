package com.android.zbrobot.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zbrobot.R;
import com.android.zbrobot.util.Constant;
import com.android.zbrobot.util.PreferencesUtils;

public class ZB_LoginActivity extends AppCompatActivity implements View.OnClickListener {
    // 密码
    private EditText edit_name;
    // 登陆
    private Button btn_login;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sjx__login);
        // 初始化
        initView();
        boolean isLogin = PreferencesUtils.getBoolean(this, "save_name", true);
        if (isLogin)
            return;

        name = PreferencesUtils.getString(this, "pass", "");
        PreferencesUtils.putBoolean(this, "save_name", false);
        // 获取文本框内容
        Intent intent = new Intent(this, ZB_MainActivity.class);
        // 传递文本框内容
        intent.putExtra("name", name);
        startActivity(intent);
        finish();
    }

    private void initView() {
        // 文本输入框 密码
        edit_name = (EditText) findViewById(R.id.edit_name);
        // 登录按钮
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                name = edit_name.getText().toString();
                // 验证
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO 密码判断
                    if (name.equals(Constant.ADMIN)
                            || name.equals(Constant.SINGLE)
                            || name.equals(Constant.MANY)) {
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        PreferencesUtils.putBoolean(this, "save_name", false);
                        // 存储密码
                        PreferencesUtils.putString(this, "pass", name);
                        // 成功
                        Intent intent = new Intent(this, ZB_MainActivity.class);
                        intent.putExtra("name", name);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "抱歉，密码输入错误！~", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }
}
