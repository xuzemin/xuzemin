package com.android.jdrd.headcontrol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
    private static AnimationDrawable animationDrawable;
    private static ImageView imageView;
    private Context context;
    private View view;
    private Handler handler = null;
    public static int Current_Type = 0;
    private static FaceDialog faceDialog;
    public FaceDialog(Context context, Handler handler) {
        super(context);
        this.context = context;
        this.handler = handler;
        setCustomDialog();
        animationDrawable.start();
    }

    public static void setAnimationDrawable() {
        animationDrawable.stop();
        switch (Current_Type){
            case 0 :
                imageView.setImageResource(R.drawable.smile);
                break;
            case 1 :
                imageView.setImageResource(R.drawable.speak);
                break;
            case 2 :
                imageView.setImageResource(R.drawable.electric);
                break;
            case 3 :
                imageView.setImageResource(R.drawable.electric);
                break;
            default:
                break;
        }
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        if(!faceDialog.isShowing()){
            animationDrawable.start();
        }
    }

    public static FaceDialog getDialog(Context context, Handler handler){
        if(faceDialog ==null){
            faceDialog = new FaceDialog(context,handler);
        }
        return faceDialog;
    }

    private void setCustomDialog() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_face, null);
        imageView = (ImageView) view.findViewById(R.id.image);
//        Current_Type = Constant.ELECTRIC;
        switch (Current_Type){
            case 0 :
                imageView.setImageResource(R.drawable.smile);
                break;
            case 1 :
                imageView.setImageResource(R.drawable.speak);
                break;
            case 2 :
                imageView.setImageResource(R.drawable.electric);
            default:
                break;
        }
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
                    if(handler !=null){
                        handler.sendEmptyMessage(3);
                    }
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
