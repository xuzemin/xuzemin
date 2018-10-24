package com.android.androidlauncher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.android.androidlauncher.R;

public class VideoPlayDialog extends Dialog {

    Context context;
    private View dialogView;

    public VideoPlayDialog(Context context) {
        super(context);
        this.context = context;
    }

    public VideoPlayDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
        LayoutInflater inflater= LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_surfaceview, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(dialogView);
    }

    @Override
    public View findViewById(int id) {
        //重写findViewById方法获取对话框中控件
        return super.findViewById(id);
    }

    public View getDialogView() {
        //获得对话框view
        return dialogView;
    }

}