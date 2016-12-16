package com.android.jdrd.random.random;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class TestRandomActivity extends AppCompatActivity {
    private TextView textView;
    private Button button,button1,button2;
    private String TAG = "Random";
    private int number = 0;
    private int lucky;
    private boolean start = false;
    private Thread thread;
    private Random random=new Random();
    private databaseDao databaseDao = new databaseDao(this);
    private databaseDao2 databaseDao2 = new databaseDao2(this);
    private HashSet<Integer> integerHashSet,integerHashSet2;
    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    number = (int) msg.obj;
                    String name = databaseDao.selectData(number);
                    textView.setText(""+name+"");
                    break;
                case 2:
                    number = (int) msg.obj;
                    String name2 = databaseDao2.selectData(number);
                    textView.setText(""+name2+"");
                    break;
                case 3:
                    Log.e(TAG, "收到message"+3);
                    String str = (String) msg.obj;
                    Log.e(TAG, "message3"+str);
                    textView.setText("所有参与者已选出");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_random);
        textView = (TextView) this.findViewById(R.id.Randowm);
        button = (Button) this.findViewById(R.id.button);
        button1 = (Button) this.findViewById(R.id.button1);
        button2 = (Button) this.findViewById(R.id.button2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!start){
                    startRandom();
                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start){
                    start = false;
                    Message message=new Message();
                    int a = getNumber();
                    if(a !=0) {
                        Log.e(TAG, "message"+1);
                        message.what=1;
                        message.obj = a;
                        mHandler.sendMessage(message);
                    }else{
                        Log.e(TAG, "message"+3);
                        message.what=3;
                        String str = "所有参与者都已选出";
                        message.obj = str;
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start){
                    start = false;
                    Message message=new Message();
                    int a = getNumber2();
                    if(a !=0) {
                        Log.e(TAG, "message"+2);

                        message.what=2;
                        message.obj = a;
                        mHandler.sendMessage(message);
                    }else{
                        Log.e(TAG, "message"+3);
                        message.what=3;
                        message.obj = "所有参与者都已选出";
                        Log.e(TAG, "message3   "+message.obj);
                        mHandler.sendMessage(message);
                    }
                }
            }
        });

        databaseDao.selectData();
        databaseDao2.selectData();
        integerHashSet=new HashSet<Integer>();
        integerHashSet2=new HashSet<Integer>();
        integerHashSet.add(0);
        integerHashSet2.add(0);
    }

    private void startRandom(){
        Log.e(TAG,"开始");
        start = true;
        thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(start){
                    Log.e(TAG,"随机数生成");
                    Message message=new Message();
                    message.what=1;
                    lucky = random.nextInt(25);
                    message.obj = lucky;
                    mHandler.sendMessage(message);
                    try {
                        thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    public int getNumber() {
        int number = random.nextInt(26);
        Log.e(TAG, "1获取数字"+number);
        if (integerHashSet.size() <= 26) {
            if (!integerHashSet.contains(number)) {
                integerHashSet.add(number);
                System.out.println("添加进HashSet的randomInt=" + number);
                Log.e(TAG, "添加进HashSet的randomInt1"+number);
                return number;
            } else {
                Log.e(TAG, "已添加number");
                return getNumber();
            }
        }
        return 0;
    }
    public int getNumber2() {
        int number = random.nextInt(6);
        Log.e(TAG, "2获取数字"+number);
        if (integerHashSet2.size() <= 5) {
            if (!integerHashSet2.contains(number)) {
                integerHashSet2.add(number);
                Log.e(TAG, "添加进HashSet的randomInt2"+number);
                return number;
            } else {
                Log.e(TAG, "已添加number2");
                return getNumber2();
            }
        }
        return 0;
    }
}
