package com.example.systemcheck.mananage;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class SkinManager {
    public static final String PROCESSOR_BACKGROUND = "background";
    public static final String PROCESSOR_DIVIDER = "divider";
    public static final String PROCESSOR_SRC = "src";
    private static final String PROCESSOR_STATUS = "processor_enabled:";
    public static final String PROCESSOR_TEXT = "text";
    public static final String PROCESSOR_TEXT_COLOR = "textColor";
    public static final String PROCESSOR_TEXT_SIZE = "textSize";
    public static final String PROCESSOR_TYPEFACE = "typeface";
    public static final String TAG = "SkinManager";
    private List<String> disabledProcessors;
    private boolean isAsyncLoadSkin;
    private List<Activity> mActivities;
    private volatile int mAsyncLoadIndex;
    private Context mContext;
    private volatile boolean mIsChangingSkin;
    private byte[] mLock;


    private int mTotalActivitiesOrViews;
    private List<View> mViewList;

    public static SkinManager getInstance() {
        return a.a;
    }

//    private void injectSkin(Activity activity, View view) {
//        Object tag = view.getTag();
//        String str = tag instanceof String ? (String) tag : null;
//        int i = 0;
//        boolean z = Looper.myLooper() == Looper.getMainLooper();
//        if (str != null) {
//            for (SkinItem skinItem : parseTag(str)) {
//                ISkinProcessor processor = getProcessor(skinItem.resType);
//                if (processor != null) {
//                    processor.process(activity, view, skinItem.resName, this.mResourceManager, z);
//                }
//            }
//        }
//        if (view instanceof ViewGroup) {
//            ViewGroup viewGroup = (ViewGroup) view;
//            int childCount = viewGroup.getChildCount();
//            while (i < childCount) {
//                injectSkin(activity, viewGroup.getChildAt(i));
//                i++;
//            }
//        }
//    }
    private static class a {
        static SkinManager a = new SkinManager();
    }
}
