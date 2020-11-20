package com.mphotool.whiteboard.view.menuviews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mphotool.whiteboard.R;

public class TrasparentDialog extends Dialog {

    public TrasparentDialog(@NonNull Context context)
    {
        super(context, R.style.trasparentDialogTheme);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transparent_dialog);
    }

    @Override public void dismiss()
    {
        super.dismiss();
    }
}
