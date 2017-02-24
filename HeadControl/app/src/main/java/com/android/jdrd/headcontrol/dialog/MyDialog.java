package com.android.jdrd.headcontrol.dialog;

/**
 * Created by Administrator on 2017/2/16.
 */

import android.app.Dialog;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.jdrd.headcontrol.R;

/**
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 * @author Tom.Cai
 */
public class MyDialog extends Dialog {
    private EditText editText;
    private Button positiveButton, negativeButton;
    private TextView title;

    public MyDialog(Context context) {
        super(context, R.style.MyDialog);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog, null);
        title = (TextView) mView.findViewById(R.id.title);
        editText = (EditText) mView.findViewById(R.id.number);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);
    }

    public View getEditText(){
        return editText;
    }

    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener){
        positiveButton.setOnClickListener(listener);
    }
    /**
     * 取消键监听器
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}