package com.mphotool.whiteboard.view.menuviews.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;

public class SavePanelDialog extends AlertDialog {
    private Context ctx;
    private EditText mEditText;
    private TextView mFloderName;
    WindowManager.LayoutParams wlp = getWindow().getAttributes();
    Display display = getWindow().getWindowManager().getDefaultDisplay();

    public SavePanelDialog(@NonNull Context context)
    {
        super(context, R.style.XDialog);
        ctx = context;
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_dialog);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mEditText = (EditText) findViewById(R.id.edit_file_name);
        mFloderName = (TextView) findViewById(R.id.floder);

//        Window win = getWindow();
//        WindowManager.LayoutParams lp = win.getAttributes();
//        lp.eraseWidth = 750;
//        lp.height = 900;
//        lp.dimAmount = 0.2f;
//        win.setAttributes(lp);
    }

    @Override public void show()
    {
        super.show();
        BaseUtils.hideNavigationBar(this);
    }

    private void setViewWidth()
    {
        int dialogWidth = (int) (display.getWidth() * 0.4);
        int minWidth = dip2px(ctx, 300);

        int dialogHeight = (int) (display.getHeight() * 0.3);
        int minHeight = dip2px(ctx, 220);
        wlp.width = dialogWidth > minWidth ? dialogWidth : minWidth;
        wlp.height = dialogHeight > minHeight ? dialogHeight : minHeight;
    }

    public void setFloderName(String floderName)
    {
        mFloderName.setText(floderName);
    }

    public String getFloderName()
    {
        return mFloderName.getText().toString();
    }

    public void setFileName(String defaultName)
    {
        mEditText.setText(defaultName);
    }

    public String getFileName()
    {
        return mEditText.getText().toString();
    }

    public static int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public ViewGroup getRootView()
    {
        return (ViewGroup) findViewById(R.id.root);
    }
}
