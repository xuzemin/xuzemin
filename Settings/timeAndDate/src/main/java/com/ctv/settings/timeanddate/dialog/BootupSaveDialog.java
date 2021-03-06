package com.ctv.settings.timeanddate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.activity.AutoBootupActivity;
import com.ctv.settings.timeanddate.activity.AutoShutdownActivity;
import com.ctv.settings.timeanddate.holder.AutoBootupViewHolder;
import com.ctv.settings.timeanddate.holder.AutoShutdownViewHolder;

public class BootupSaveDialog extends Dialog {
    private AutoBootupActivity mAutoBootupActivity;
    private AutoBootupViewHolder mAutoBootupViewHolder;
    public BootupSaveDialog(Context context, AutoBootupViewHolder holder) {
        super(context);
        this.mAutoBootupActivity=(AutoBootupActivity)context;
        this.mAutoBootupViewHolder=holder;
        setWindowStyle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bootup_layout);
        findViews();
    }

    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = mAutoBootupActivity.getResources();
        Drawable drab = res.getDrawable(R.drawable.button_save_shape);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.width = 680;
        lp.height =480;
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        w.setDimAmount(0.0f);
        w.setAttributes(lp);
    }
    public void findViews(){
        Button mBtnCancel=(Button)findViewById(R.id.btn_cancel);
        Button mBtnOk=(Button)findViewById(R.id.btn_ok);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAutoBootupViewHolder.hintDialog();
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAutoBootupViewHolder.save();
            }
        });
    }

}
