package com.ctv.settings.about.Bean;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.ctv.settings.about.R;

public class TipDialog extends Dialog implements View.OnClickListener {
    private final Context context;

    private Button click_no, click_ok;

   private int layoutId;

    private boolean isFocusNo;

    private TextView tv_dlg_tip_title;

    private String content;
    public TipDialog( Context context) {
        super(context);
        this.context=context;
    }
    public TipDialog(Context context, boolean isFocusNo) {
        super(context, R.style.ctv_dialog_style);
        this.layoutId = R.layout.dlg_tip;
        this.context = context;
        this.isFocusNo = true;
    }
    public TipDialog(Context context, int layoutId) {
        super(context, R.style.ctv_dialog_style);
        this.layoutId=layoutId;
        this.context = context;
    }


    public TipDialog(Context context, String content) {
        super(context, R.style.ctv_dialog_style);
        this.layoutId = R.layout.dlg_tip;
        this.context = context;
        this.content = content;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutId);
        setWindowStyle();
        initViews();
    }
    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = context.getResources();
        Drawable drab = res.getDrawable(R.drawable.transparent);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.width = (int) (482 * scale + 0.5f);
        lp.height = (int) (256 * scale + 0.5f);
        w.setAttributes(lp);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onDialogClick(false);
            this.dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onDialogClick(boolean isOk) {
    }

    private void initViews() {
        click_no = (Button) findViewById(R.id.click_no);
        click_ok = (Button) findViewById(R.id.click_ok);
        click_ok.setOnClickListener(this);
        click_no.setOnClickListener(this);
        if (content != null && !content.isEmpty()) {
            tv_dlg_tip_title = (TextView) findViewById(R.id.tv_dlg_tip_title);
            tv_dlg_tip_title.setText(content);
            tv_dlg_tip_title.setGravity(Gravity.CENTER);
        }
        if (isFocusNo) {
            click_no.requestFocus();
        }
    }
    public void setContent(String content) {
        this.content = content;
        tv_dlg_tip_title.setText(content);
    }

    public String getContent() {
        return content;
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        int i = view.getId();
        if (i == R.id.click_ok) {
            onDialogClick(true);
            this.cancel();
            this.dismiss();
        } else if (i == R.id.click_no) {
            onDialogClick(false);
            this.cancel();
            this.dismiss();
        }
    }
}
