
package com.ctv.welcome.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.SeekBar;
import com.ctv.welcome.R;

public class SavingEditPictureDialog extends Dialog {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SavingEditPictureDialog.this.seekSavingEditPic.setProgress(((Integer) msg.obj)
                    .intValue());
        }
    };

    private boolean isFinished;

    private int progress;

    private SeekBar seekSavingEditPic;

    private class SavingEditPic implements Runnable {
        @Override
        public void run() {
            Message msg;
            while (!SavingEditPictureDialog.this.isFinished) {
                try {
                    Thread.sleep(40);
                    SavingEditPictureDialog.this.progress = SavingEditPictureDialog.this.progress + 2;
                    msg = SavingEditPictureDialog.this.handler.obtainMessage();
                    msg.what = 0;
                    if (SavingEditPictureDialog.this.progress >= 95) {
                        msg.obj = Integer.valueOf(95);
                    } else {
                        msg.obj = Integer.valueOf(SavingEditPictureDialog.this.progress);
                    }
                    msg.sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            msg = SavingEditPictureDialog.this.handler.obtainMessage();
            msg.what = 0;
            msg.obj = Integer.valueOf(100);
            msg.sendToTarget();
        }
    }

    public SavingEditPictureDialog(Context context, int themeResId) {
        super(context, themeResId);
        Window window = getWindow();
        LayoutParams params = window.getAttributes();
        params.width = -2;
        params.height = -2;
        params.gravity = 17;
        window.setAttributes(params);
    }

    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_saving_edit_picture);
        this.seekSavingEditPic = (SeekBar) findViewById(R.id.seek_saving_edit_pic);
        new Thread(new SavingEditPic()).start();
    }
}
