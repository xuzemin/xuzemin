package com.android.jdrd.headcontrol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.util.Constant;

/**
 * Created by xuzemin on 2017/3/28.
 *
 */

public class FaceDialog extends Dialog {
    private AnimationDrawable animationDrawable;
    private ImageView imageView;
    private Context context;
    private static FaceDialog faceDialog;
    public FaceDialog(Context context) {
        super(context);
        this.context = context;
        setCustomDialog();
        animationDrawable.start();
    }
    public static FaceDialog getDialog(Context context){
        if(faceDialog ==null){
            faceDialog = new FaceDialog(context);
        }
        return faceDialog;
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_face,null);
        imageView = (ImageView) mView.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.animationface);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();

        View bv = this.findViewById(android.R.id.title);
        bv.setVisibility(View.GONE);
        setContentView(R.layout.fragment_dialog_face);
        LinearLayout dialog_face =  (LinearLayout)mView.findViewById(R.id.dialog_face);
        dialog_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

        super.setContentView(mView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    private MyDialog dialog ;
    private EditText editText;
    private void dialog() {
        dialog = new MyDialog(context,1);
        dialog.setCanceledOnTouchOutside(false);
        editText = (EditText) dialog.getEditText();
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().trim().equals("")){
                    faceDialog.dismiss();
                    Constant.DIALOG_SHOW = false;
                }else{
                    Toast.makeText(context,"管理员密码错误",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
//        dialog.setOnNegativeListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }
}
