
package com.ctv.welcome.util;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public abstract class PopUtils {
    private ClickListener mCallback;

    private Context mContext;

    private int mHeight;

    private int mlayoutResId;

    private int mwidth;

    public interface ClickListener {
        void setUplistener(PopBuilder popBuilder);
    }

    public static class PopBuilder {
        private static PopupWindow window;

        private ClickListener mCallback;

        private View mItem;

        private SparseArray<View> mViews = new SparseArray();

        private PopBuilder(Context context, View view, ClickListener callback) {
            this.mItem = view;
            this.mCallback = callback;
        }

        public static PopBuilder createPopupWindow(final Context context, int layoutResId,
                int width, int height, View parent, int gravity, int x, int y,
                ClickListener callback) {
            View view = ((LayoutInflater) ((Activity) context).getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(layoutResId, null);
            PopBuilder builder = new PopBuilder(context, view, callback);
            window = new PopupWindow(view, width, height);
            window.setFocusable(true);
            window.setTouchable(true);
            window.setOutsideTouchable(false);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            window.setOnDismissListener(new OnDismissListener() {
                public void onDismiss() {
                    PopBuilder.window = null;
                    LayoutParams params = ((Activity) context).getWindow().getAttributes();
                    params.alpha = 1.0f;
                    ((Activity) context).getWindow().setAttributes(params);
                }
            });
            window.showAtLocation(parent, gravity, x, y);
            if (window != null) {
                callback.setUplistener(builder);
            }
            return builder;
        }

        public <T extends View> View getView(int id) {
            View t = (View) this.mViews.get(id);
            if (t != null) {
                return t;
            }
            T t2 = this.mItem.findViewById(id);
            this.mViews.put(id, t2);
            return t2;
        }

        public PopBuilder dismiss() {
            if (window != null) {
                window.dismiss();
            }
            return this;
        }

        public PopBuilder setVisibility(int id, int visibility) {
            getView(id).setVisibility(visibility);
            return this;
        }

        public PopBuilder setImageResource(int id, int drawableRes) {
            View view = getView(id);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            } else {
                view.setBackgroundResource(drawableRes);
            }
            return this;
        }

        public PopBuilder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }
    }

    public PopUtils(Context context, int layoutResId, int width, int height, View view,
            int gravity, int x, int y, ClickListener callback) {
        this.mContext = context;
        this.mlayoutResId = layoutResId;
        this.mwidth = width;
        this.mHeight = height;
        setCallBack(callback);
        PopBuilder builder = PopBuilder.createPopupWindow(context, layoutResId, width, height,
                view, gravity, x, y, this.mCallback);
    }

    private void setCallBack(ClickListener callBack) {
        this.mCallback = callBack;
    }
}
