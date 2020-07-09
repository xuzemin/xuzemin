package mstar.factorymenu.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import mstar.factorymenu.ui.adapter.SubTitleAdapter;
import mstar.factorymenu.ui.utils.LogUtils;

public class CustomTitleListView extends ListView {
    public CustomTitleListView(Context context) {
        super(context);
    }

    public CustomTitleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTitleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View focusSearch(int direction) {
        if (direction==View.FOCUS_DOWN){
            return null;
        }
        return super.focusSearch(direction);
    }
}
