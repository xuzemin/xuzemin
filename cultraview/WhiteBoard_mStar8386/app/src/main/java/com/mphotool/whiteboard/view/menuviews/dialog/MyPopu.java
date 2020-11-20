
package com.mphotool.whiteboard.view.menuviews.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MyPopu {
    private static final String TAG = "MyPopu.java";

    private View contentView = null;

    private PopupWindow popupWindow = null;

    public MyPopu(Context ctx, int width, int height, int resource) {
        this.contentView = LayoutInflater.from(ctx).inflate(resource, null);
        if (this.contentView.getBackground() != null) {
            this.contentView.getBackground().setAlpha(100);
        }
        this.popupWindow = new PopupWindow(this.contentView, width, height);
        this.popupWindow.setContentView(this.contentView);
        this.popupWindow.setFocusable(true);
        this.popupWindow.setOutsideTouchable(true);
    }

    public void setAlpha(int alpha) {
        this.contentView.getBackground().setAlpha(alpha);
    }

    public void getPopupWindowByFocusable() {
        this.popupWindow.setFocusable(false);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.popupWindow.setOnDismissListener(onDismissListener);
    }

    public void dismiss() {
        if (this.popupWindow != null) {
            this.popupWindow.dismiss();
        }
    }

    public void showAtLocation(View view, int gravity, int x, int y) {
        if (this.popupWindow != null) {
            this.popupWindow.showAtLocation(view, gravity, x, y);
        }
    }

    public Button getButton(int paramInt) {
        return (Button) this.contentView.findViewById(paramInt);
    }

    public EditText getEditText(int paramInt) {
        return (EditText) this.contentView.findViewById(paramInt);
    }

    public ImageView getImageView(int paramInt) {
        return (ImageView) this.contentView.findViewById(paramInt);
    }

    public DatePicker getDatePicker(int paramInt) {
        return (DatePicker) this.contentView.findViewById(paramInt);
    }

    public LinearLayout getLinearLayout(int paramInt) {
        return (LinearLayout) this.contentView.findViewById(paramInt);
    }

    public ListView getListView(int paramInt) {
        return (ListView) this.contentView.findViewById(paramInt);
    }

    public RelativeLayout getRelativeLayout(int paramInt) {
        return (RelativeLayout) this.contentView.findViewById(paramInt);
    }

    public TextView getTextView(int paramInt) {
        return (TextView) this.contentView.findViewById(paramInt);
    }

    public GridView getGridView(int paramInt) {
        return (GridView) this.contentView.findViewById(paramInt);
    }

    public View getView(int paramInt) {
        return this.contentView.findViewById(paramInt);
    }

    public Spinner getSpinner(int paramInt) {
        return (Spinner) this.contentView.findViewById(paramInt);
    }

    public SeekBar getSeekBar(int paramInt) {
        return (SeekBar) this.contentView.findViewById(paramInt);
    }

    public CheckBox getCheckBox(int paramInt) {
        return (CheckBox) this.contentView.findViewById(paramInt);
    }

    public ImageButton getImageButton(int paramInt) {
        return (ImageButton) this.contentView.findViewById(paramInt);
    }

    public ProgressBar getProgressBar(int paramInt) {
        return (ProgressBar) this.contentView.findViewById(paramInt);
    }

    public RadioButton getRadioButton(int paramInt) {
        return (RadioButton) this.contentView.findViewById(paramInt);
    }
}
