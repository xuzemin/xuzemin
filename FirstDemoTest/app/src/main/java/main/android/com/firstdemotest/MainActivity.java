package main.android.com.firstdemotest;

import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    public TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.test);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                getLinux();
            }
        });
    }

    public void getLinux()  {
        new Thread(new Runnable() {

            @Override
            public void run() {

                Process mLogcatProc = null;
                BufferedReader reader = null;
                try {
                    //获取logcat日志信息
                    mLogcatProc = Runtime.getRuntime().exec("logcat");
                    reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.indexOf("ActivityManager") > 0) {
                            //logcat打印信息在这里可以监听到
                            // 使用looper 把给界面一个���示
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "监听到log信息", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
