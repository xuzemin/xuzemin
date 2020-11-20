package com.mphotool.whiteboard.view.menuviews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.activity.MainActivity;
import com.mphotool.whiteboard.activity.WhiteBoardApplication;
import com.mphotool.whiteboard.utils.BaseUtils;

public class ExitDialog extends Dialog {
    private MainActivity mActivity;

    public ExitDialog(@NonNull Context context)
    {
        super(context, R.style.XDialog);
        this.mActivity = (MainActivity) context;
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_dialog);
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ExitDialog.this.dismiss();
            }
        });
        findViewById(R.id.button_center).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ExitDialog.this.dismiss();
                ExitDialog.this.mActivity.moveTaskToBack(true);
            }
        });
        findViewById(R.id.button_right).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ExitDialog.this.mActivity.finish();
                WhiteBoardApplication.exitAllActivities();
                System.exit(0);
            }
        });
    }


    public void setMessage(String msg)
    {
        ((TextView) findViewById(R.id.text_msg)).setText(msg);
    }

    @Override public void show()
    {
        super.show();
        BaseUtils.hideNavigationBar(this);
    }

    @Override public void dismiss()
    {
        super.dismiss();
    }
}
