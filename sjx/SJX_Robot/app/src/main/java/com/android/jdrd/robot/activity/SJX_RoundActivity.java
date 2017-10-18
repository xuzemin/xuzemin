package com.android.jdrd.robot.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.service.Protocol;
import com.android.jdrd.robot.service.ServerSocketUtil;
import com.android.jdrd.robot.util.Constant;
import com.android.jdrd.robot.view.RoundView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/9/22
 * 描述: 机器人遥控器
 */
public class SJX_RoundActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    // 数据库帮助类
    private RobotDBHelper robotDBHelper;
    // 机器人id
    private int robotId;
    // 机器人信息
    private Map robotConfig;
    public static Thread thread = new Thread();
    // IP地址
    public static String IP;
    // 字节数组发送命令
    public static byte[] data;

    public boolean flag_start = true;
    public boolean flag_stop = true;

    // 自定义圆形View
    RoundView round_view;
    RoundView.RoundMenu roundMenu;

    // 与系统默认SeekBar对应的TextView
    private TextView mTvDef;
    // 与自定义SeekBar对应的TextView
    private TextView mTvSelf;
    // 系统默认SeekBar
    private SeekBar mSeekBarDef;
    // 自定义SeekBar
    private SeekBar mSeekBarSelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sjx_activity_round);

        //初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        // 获取SJX_RobotActivity传递的Id
        Intent intent = getIntent();
        robotId = intent.getIntExtra("id", 0);

        // 返回
        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        // 遥控器
        round_view = (RoundView) findViewById(R.id.round_view);

        // TextView显示直行速度
        mTvDef = (TextView) findViewById(R.id.tv_def);
        // 直行速度SeekBar
        mSeekBarDef = (SeekBar) findViewById(R.id.seekbar_def);
        mSeekBarDef.setOnSeekBarChangeListener(this);
        mSeekBarDef.setMax(100 * 30);
        mSeekBarDef.setProgress(10 * 200);

        // TextView显示旋转速度
        mTvSelf = (TextView) findViewById(R.id.tv_self);
        // 旋转速度SeekBar
        mSeekBarSelf = (SeekBar) findViewById(R.id.seekbar_self);
        mSeekBarSelf.setOnSeekBarChangeListener(this);
        mSeekBarSelf.setMax(100 * 10);
        mSeekBarSelf.setProgress(10 * 50);
        // 初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    public void initData() {
        // 查询机器人信息
        List<Map> robotList = robotDBHelper.queryListMap("select * from robot where id = '" + robotId + "'", null);
        robotConfig = robotList.get(0);
        Constant.debugLog("robot----->" + robotList.toString());
        Constant.debugLog("robotConfig----->" + robotConfig.toString());
        IP = (String) robotConfig.get("ip");
        Constant.debugLog("IP地址:" + IP);
        // 初始化时发送一条遥控命令
        data = Protocol.getSendData(Protocol.CONTROL_REMOTE, Protocol.getCommandData(Protocol.ROBOT_CONTROL_REMOTE));
        SendControl(data);

        /**
         * selectSolidColor:按下去的颜色
         * strokeColor:外圆外边的边线颜色
         * 下
         */
        roundMenu = new RoundView.RoundMenu();
        roundMenu.selectSolidColor = Color.parseColor("#548CFF");
        roundMenu.strokeColor = Color.parseColor("#F1F6F5");
        roundMenu.icon = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_big_right2);
        roundMenu.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 停止线程
                stop();
                if (flag_stop == false) {
                    flag_start = true;
                    // 速度清0
                    data = Protocol.getSendData(Protocol.CONTROL_SPEND, Protocol.getCommandData(Protocol.ROBOT_CONTROL_CLEAR_SPEND));
                    SendStop(data);
                }
            }
        };
        roundMenu.onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // 启动线程
                start();
                if (flag_start == false) {
                    flag_stop = true;
                    // 后退
                    data = Protocol.getSendData(Protocol.CONTROL_SPEND, Protocol.getCommandData(Protocol.ROBOT_CONTROL_SPEND_DOWN));
                    SendStart(data);
                }
                return false;
            }
        };
        round_view.addRoundMenu(roundMenu);

        /**
         * selectSolidColor:按下去的颜色
         * strokeColor:外圆外边的边线颜色
         * 左
         */
        roundMenu = new RoundView.RoundMenu();
        roundMenu.selectSolidColor = Color.parseColor("#548CFF");
        roundMenu.strokeColor = Color.parseColor("#F1F6F5");
        roundMenu.icon = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_big_right2);
        roundMenu.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 停止线程
                stop();
                if (flag_stop == false) {
                    flag_start = true;
                    // 速度清0
                    data = Protocol.getSendData(Protocol.CONTROL_SPEND, Protocol.getCommandData(Protocol.ROBOT_CONTROL_CLEAR_SPEND));
                    SendStop(data);
                }
            }
        };
        roundMenu.onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // 启动线程
                start();
                if (flag_start == false) {
                    flag_stop = true;
                    // 左旋转
                    data = Protocol.getSendData(Protocol.CONTROL_ROTATE, Protocol.getCommandData(Protocol.ROBOT_CONTROL_ROTATE_LEFT));
                    SendStart(data);
                }
                return false;
            }
        };
        round_view.addRoundMenu(roundMenu);

        /**
         * selectSolidColor:按下去的颜色
         * strokeColor:外圆外边的边线颜色
         * 上
         */
        roundMenu = new RoundView.RoundMenu();
        roundMenu.selectSolidColor = Color.parseColor("#548CFF");
        roundMenu.strokeColor = Color.parseColor("#F1F6F5");
        roundMenu.icon = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_big_right2);
        roundMenu.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 停止线程
                stop();
                if (flag_stop == false) {
                    flag_start = true;
                    // 速度清0
                    data = Protocol.getSendData(Protocol.CONTROL_SPEND, Protocol.getCommandData(Protocol.ROBOT_CONTROL_CLEAR_SPEND));
                    SendStop(data);
                }
            }
        };
        roundMenu.onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // 启动线程
                start();
                if (flag_start == false) {
                    flag_stop = true;
                    // 前进
                    data = Protocol.getSendData(Protocol.CONTROL_SPEND, Protocol.getCommandData(Protocol.ROBOT_CONTROL_SPEND_UP));
                    SendStart(data);
                }
                return false;
            }
        };
        round_view.addRoundMenu(roundMenu);

        /**
         * selectSolidColor:按下去的颜色
         * strokeColor:外圆外边的边线颜色
         * 右
         */
        roundMenu = new RoundView.RoundMenu();
        roundMenu.selectSolidColor = Color.parseColor("#548CFF");
        roundMenu.strokeColor = Color.parseColor("#F1F6F5");
        roundMenu.icon = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_big_right2);
        roundMenu.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 停止线程
                stop();
                if (flag_stop == false) {
                    flag_start = true;
                    // 速度清0
                    data = Protocol.getSendData(Protocol.CONTROL_SPEND, Protocol.getCommandData(Protocol.ROBOT_CONTROL_CLEAR_SPEND));
                    SendStop(data);
                }
            }
        };
        roundMenu.onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // 启动线程
                start();
                if (flag_start == false) {
                    flag_stop = true;
                    // 右转
                    data = Protocol.getSendData(Protocol.CONTROL_ROTATE, Protocol.getCommandData(Protocol.ROBOT_CONTROL_ROTATE_RIGHT));
                    SendStart(data);
                }
                return false;
            }
        };
        round_view.addRoundMenu(roundMenu);

        /**
         * 第一个参数:内圆的颜色
         * 第二个参数:内圆按下去的颜色
         * 第三个参数:内圆外边的边线颜色
         */
        round_view.setCoreMenu(Color.parseColor("#D7E5FA"),
                Color.parseColor("#D7E5FA"), Color.parseColor("#F1F6F5")
                , 10, 0.37, BitmapFactory.decodeResource(getResources(), R.mipmap.round_logo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }, new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回
            case R.id.setting_back:
                flag_start = false;
                flag_stop = false;
                finish();
                break;
            // 返回
            case R.id.back:
                flag_start = false;
                flag_stop = false;
                finish();
                break;
        }
    }

    /**
     * 开始发送
     *
     * @param sendData
     */
    public void SendStart(final byte[] sendData) {
        for (Map map : ServerSocketUtil.socketList) {
            if (map.get("ip").equals(IP)) {
                Constant.debugLog("传递过来的IP:" + map.get("ip").toString());
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (flag_stop) {
                            try {
                                out.write(sendData);
                                // 1000毫秒发一次
                                Thread.sleep(300);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                thread.start();
            }
        }
    }

    /**
     * 停止发送
     */
    public void SendStop(final byte[] sendData) {
        for (Map map : ServerSocketUtil.socketList) {
            if (map.get("ip").equals(IP)) {
                Constant.debugLog("传递过来的IP:" + map.get("ip").toString());
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (flag_start) {
                            try {
                                out.write(sendData);
                                // 1000毫秒发一次
                                Thread.sleep(100);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                thread.start();
            }
        }
    }

    /**
     * 发送遥控器命令
     */
    public void SendControl(final byte[] sendData) {
        for (Map map : ServerSocketUtil.socketList) {
            if (map.get("ip").equals(IP)) {
                Constant.debugLog("传递过来的IP:" + map.get("ip").toString());
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            out.write(sendData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
    }

    // 开启线程
    public void start() {
        if (flag_start) {
            flag_start = false;
        }
    }

    // 停止线程
    public void stop() {
        if (flag_stop) {
            flag_stop = false;
        }
    }

    /**
     * SeekBar滚动时的回调函数
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_def:
                // 直行速度
                Protocol.up_spend = seekBar.getProgress();
                mTvDef.setText("直行速度:" + String.valueOf(seekBar.getProgress()));
                break;
            case R.id.seekbar_self:
                // 旋转速度
                Protocol.rotate_spend = seekBar.getProgress();
                mTvSelf.setText("旋转速度:" + String.valueOf(seekBar.getProgress()));
                break;
        }
    }

    /**
     * SeekBar开始滚动的回调函数
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * SeekBar停止滚动的回调函数
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
