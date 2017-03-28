package com.android.jdrd.headcontrol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.android.jdrd.headcontrol.R;

/**
 * Created by jdrd on 2017/3/28.
 */

public class FaceDialog extends Dialog {
    private Context context;
    private AnimationDrawable animationDrawable;
    private ImageView imageView;
    public FaceDialog(Context context) {
        super(context);
        this.context = context;
        setCustomDialog();
        animationDrawable.start();
    }
    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_face,null);
        imageView = (ImageView) mView.findViewById(R.id.image);

        imageView.setImageResource(R.drawable.animationface);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        super.setContentView(mView);
    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
//    }
}
