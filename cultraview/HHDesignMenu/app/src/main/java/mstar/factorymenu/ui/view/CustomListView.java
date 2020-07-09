package mstar.factorymenu.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import mstar.factorymenu.ui.adapter.SubTitleAdapter;
import mstar.factorymenu.ui.utils.LogUtils;

public class CustomListView extends ListView {
    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View focusSearch(int direction) {
        if (direction==View.FOCUS_DOWN){
            return null;
        }
        return super.focusSearch(direction);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        int lastSelectItem = getSelectedItemPosition();
        LogUtils.d("onFocusChanged---->"+gainFocus+"| lastSelectItem:"+lastSelectItem);
        if (gainFocus) {
            setSelection(lastSelectItem);
            SubTitleAdapter adapter = (SubTitleAdapter) getAdapter();
            adapter.changeSelected(lastSelectItem);
        }
    }
}
