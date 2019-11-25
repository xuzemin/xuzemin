
package com.ctv.welcome.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ctv.welcome.R;

public class AddThemeDialog extends Dialog implements OnClickListener {
    private EditText edtInputTheme;

    private DialogCallBackListener mCallbackListener;

    private TextView txtCancel;

    private TextView txtConfirm;

    public interface DialogCallBackListener {
        void refreshParentActivityUI(String str);
    }

    public AddThemeDialog(Context context, int themeResId, DialogCallBackListener listener) {
        super(context, themeResId);
        this.mCallbackListener = listener;
        Window window = getWindow();
        LayoutParams params = window.getAttributes();
        params.width = -2;
        params.height = -2;
        params.gravity = 17;
        window.setAttributes(params);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_category);
        initView();
        initListener();
    }

    private void initView() {
        this.edtInputTheme = (EditText) findViewById(R.id.et_input_theme);
        this.txtCancel = (TextView) findViewById(R.id.txt_cancel_theme);
        this.txtConfirm = (TextView) findViewById(R.id.txt_confirm_theme);
    }

    private void initListener() {
        this.txtCancel.setOnClickListener(this);
        this.txtConfirm.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_cancel_theme:
                dismiss();
                return;
            case R.id.txt_confirm_theme:
                String theme_name = this.edtInputTheme.getText().toString();
                if (TextUtils.isEmpty(theme_name)) {
                    Toast.makeText(getContext(), R.string.not_input, Toast.LENGTH_SHORT).show();
                    return;
                }
                this.mCallbackListener.refreshParentActivityUI(theme_name);
                dismiss();
                return;
            default:
                return;
        }
    }
}
