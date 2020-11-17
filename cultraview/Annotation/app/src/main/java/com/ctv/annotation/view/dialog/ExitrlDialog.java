package com.ctv.annotation.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ctv.annotation.R;
import com.ctv.annotation.AnnotationService;
import com.ctv.annotation.utils.BaseUtils;

public class ExitrlDialog extends AlertDialog {
    private Save save;
    private static volatile boolean isCloseUSBTouch = false; // 是否改变USBTouch
    private TextView msg;
    public interface Save{
        void save();
        void exit();
    }
    public void setSave(Save save){
        this.save=save;
    }
    public ExitrlDialog(Context context) {
        super(context, R.style.XDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_dialog);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        msg = (TextView) findViewById(R.id.text_msg);
       // msg.setText(R.string.warning_exit);
        msg.setText(getContext().getResources().getText(R.string.warning_exit));

        findViewById(R.id.button_right).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.d("hong", "dialog----onClick:");
//                if (Settings.System.getInt(getContext().getContentResolver(),
//                        "EASY_TOUCH_OPEN", 1) == 0){
//                    setEasyTouchEnable(false);
//                }else {
//                    setEasyTouchEnable(true);
//                }
//                try {
//                    closeUSBTouch(false);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                dismiss();
//
//
//                Intent intent=new Intent(getContext(), AnnotationService.class);
//                getContext().stopService(intent);
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);

                save.exit();
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();

            }
        });


        findViewById(R.id.button_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save.save();
            }
        });
    }

}
