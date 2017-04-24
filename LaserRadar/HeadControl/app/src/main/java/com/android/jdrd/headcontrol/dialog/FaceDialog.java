package com.android.jdrd.headcontrol.dialog;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
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
    private View view;
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
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_face, null);
        imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.smile);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        setCanceledOnTouchOutside(false);
        getWindow().setWindowAnimations(R.style.dialog_window);
        getWindow().setBackgroundDrawableResource(R.color.vifrification);

        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.x = -50;
        getWindow().setAttributes(wl);

//        Animation mAnimation = AnimationUtils.loadAnimation(context,R.animator.dialog_enter_anim);
//        view.startAnimation(mAnimation);

        View bv = this.findViewById(android.R.id.title);
        bv.setVisibility(View.GONE);
        LinearLayout dialog_face =  (LinearLayout)view.findViewById(R.id.dialog_face);
        dialog_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        super.setContentView(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    private MyDialog dialog ;
    private EditText editText;
    private void dialog() {
        dialog = new MyDialog(context,1);
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
        dialog.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
    }
}
