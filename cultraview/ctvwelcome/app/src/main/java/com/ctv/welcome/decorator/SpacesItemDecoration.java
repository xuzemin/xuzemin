
package com.ctv.welcome.decorator;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int halfSpace;

    public SpacesItemDecoration(int space) {
        this.halfSpace = space / 2;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getPaddingLeft() != this.halfSpace) {
            parent.setPadding(this.halfSpace, this.halfSpace, this.halfSpace, this.halfSpace);
            parent.setClipToPadding(false);
        }
        outRect.top = this.halfSpace;
        outRect.bottom = this.halfSpace;
        outRect.left = this.halfSpace;
        outRect.right = this.halfSpace;
    }
}
