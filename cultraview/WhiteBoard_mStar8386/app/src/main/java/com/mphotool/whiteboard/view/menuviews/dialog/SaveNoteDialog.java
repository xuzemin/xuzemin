package com.mphotool.whiteboard.view.menuviews.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;

/**
 * 保存文件时，已有同名文件的情况下提示是否覆盖原文件
 */
public class SaveNoteDialog extends AlertDialog {


    public SaveNoteDialog(@NonNull Context context)
    {
        super(context, R.style.XDialog);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_save_dialog);
    }

    public void setLeftListener(View.OnClickListener listener){
        if(listener != null){
            findViewById(R.id.button_no).setOnClickListener(listener);
        }
    }


    public void setRightListener(View.OnClickListener listener){
        if(listener != null){
            findViewById(R.id.button_yes).setOnClickListener(listener);
        }
    }

    @Override public void show()
    {
        super.show();
        BaseUtils.hideNavigationBar(this);
    }
}
