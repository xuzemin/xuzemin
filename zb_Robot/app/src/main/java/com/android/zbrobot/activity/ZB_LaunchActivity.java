package com.android.zbrobot.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Property;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.zbrobot.R;
import com.android.zbrobot.helper.RobotDBHelper;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/7/27
 * 描述: 闪屏页
 */
public class ZB_LaunchActivity extends AppCompatActivity {
    // Drawable
    private ColorDrawable mBgDrawable;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sjx_activity_launch);
        //初始化
        initWidget();
        initData();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void initWidget() {
        //拿到根布局
        View root = findViewById(R.id.activity_launch);
        //获取颜色
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        //创建一个Drawable
        ColorDrawable drawable = new ColorDrawable(color);
        //设置给背景
        root.setBackground(drawable);
        mBgDrawable = drawable;

    }

    protected void initData() {
        skip();
    }

    /**
     * 在跳转之前需要把剩下的50%进行完成
     */
    private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                //初始化数据库
                RobotDBHelper.getInstance(getApplicationContext());
                reallySkip();
            }
        });
    }

    /**
     * 真实的跳转
     */
    private void reallySkip() {
        Intent intent = new Intent(this, ZB_LoginActivity.class);
        startActivity(intent);
        finish();

    }

    /**
     * 给背景设置一个动画
     *
     * @param endProgress 动画的结束进度
     * @param endCallback 动画结束时触发
     */
    private void startAnim(float endProgress, final Runnable endCallback) {
        // 获取一个最终的颜色
        int finalColor = Resource.Color.WHITE; // UiCompat.getColor(getResources(), R.color.white);
        // 运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        // 构建一个属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(3000); // 时间
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor); // 开始结束值
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 结束时触发
                endCallback.run();
            }
        });
        valueAnimator.start();
    }


    private final Property<ZB_LaunchActivity, Object> property = new Property<ZB_LaunchActivity, Object>(Object.class, "color") {
        @Override
        public void set(ZB_LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(ZB_LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };
}
