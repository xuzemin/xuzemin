
package com.mphotool.whiteboard.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnTouchStateChangeListener implements OnTouchListener {
    private static OnTouchStateChangeListener listener = new OnTouchStateChangeListener();

    public static OnTouchStateChangeListener getInstance() {
        return listener;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case 0:
            case 2:
                v.setAlpha(0.3f);
                break;
            case 1:
                v.setAlpha(1.0f);
                break;
            default:
                break;
        }
        return false;
    }
}
