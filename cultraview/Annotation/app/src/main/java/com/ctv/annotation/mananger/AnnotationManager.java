package com.ctv.annotation.mananger;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ctv.annotation.AnnotationService;
import com.ctv.annotation.R;
import com.ctv.annotation.WhiteBoardApplication;
import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.view.AnnotationView;

public class AnnotationManager {


    private  static Context context;
    private static WindowManager mWindowManager;
    private static WindowManager windowManager;
    public static AnnotationView annotationView;
    private static WindowManager.LayoutParams menuParams;
    private static View inflate;


    public static void creatView(Context context){
//        Settings.System.putInt(context.getContentResolver(),
//                "annotate.start", 1);
        context =context;
        BaseUtils.dbg("hong","creatview:annatationview ");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        annotationView = new AnnotationView(context);
        initMenuLeftParams(context, screenWidth, screenHeight);
       // inflate = LayoutInflater.from(context).inflate(R.layout.activity_main, null, false);
        windowManager.addView(annotationView.inflate, menuParams);
       // initView();

    }

    private static void initView(){
        TextView back = inflate.findViewById(R.id.tv_main);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg("hong","back--onclick");
                stopservice();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });

    }
    private static void stopservice(){

        Intent intent = new Intent(context, AnnotationService.class);
        context.stopService(intent);

    }
    private static WindowManager getWindowManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        return mWindowManager;

    }
    private static void initMenuLeftParams(Context context, int screenWidth, int screenHeight){
        menuParams = new WindowManager.LayoutParams();
        menuParams.x=0;
        menuParams.y=0;
        menuParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        // 所有其它程序是可点击的，悬浮窗不获取焦点
        menuParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        menuParams.format = PixelFormat.RGBA_8888;

        menuParams.gravity = Gravity.LEFT | Gravity.TOP;

        Display display = getWindowManager(context).getDefaultDisplay();
        menuParams.width=  display.getWidth();
        menuParams.height =display.getHeight();
    }

}
