package com.mphotool.whiteboard.view.menuviews.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;

/**
 * 保存文件时，文件数量已满的情况下提示删除
 */
public class FileCountDialog extends AlertDialog {


    public FileCountDialog(@NonNull Context context)
    {
        super(context, R.style.XDialog);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_count_dialog);
    }

    public void setLeftListener(View.OnClickListener listener){
        if(listener != null){
            findViewById(R.id.button_cancel).setOnClickListener(listener);
        }
    }


    public void setRightListener(View.OnClickListener listener){
        if(listener != null){
            findViewById(R.id.button_confirm).setOnClickListener(listener);
        }
    }

    public void setMessage(String msg){
        ((TextView)findViewById(R.id.text_msg)).setText(msg);
    }

    @Override public void show()
    {
        super.show();
        BaseUtils.hideNavigationBar(this);
    }
}
