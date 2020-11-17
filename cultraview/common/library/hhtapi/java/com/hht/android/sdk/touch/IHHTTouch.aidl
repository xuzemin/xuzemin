// IHHTTime.aidl
package com.hht.android.sdk.touch;
import android.graphics.Rect;

// Declare any non-default types here with import statements

interface IHHTTouch {
    boolean controlPcTouchRect(String strPackage, String strWinTitle,
                                                  inout Rect touchRect,
                                                  boolean bEnable);
}
