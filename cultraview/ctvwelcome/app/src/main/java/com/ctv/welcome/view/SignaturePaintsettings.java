
package com.ctv.welcome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class SignaturePaintsettings extends RelativeLayout {
    private float x1;

    private float y1;

    public SignaturePaintsettings(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.x1 = event.getX();
                this.y1 = event.getY();
                break;
            case 2:
                float x2 = event.getX();
                int offsetX = (int) (x2 - this.x1);
                int offsetY = (int) (event.getY() - this.y1);
                LayoutParams params = new LayoutParams(-2, -2);
                params.leftMargin = getLeft() + offsetX;
                params.topMargin = getTop() + offsetY;
                setLayoutParams(params);
                break;
        }
        return true;
    }
}
