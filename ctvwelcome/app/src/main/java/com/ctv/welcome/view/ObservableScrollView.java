
package com.ctv.welcome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;

    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView observableScrollView, int i, int i2, int i3,
                int i4);
    }

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (this.scrollViewListener != null) {
            this.scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
