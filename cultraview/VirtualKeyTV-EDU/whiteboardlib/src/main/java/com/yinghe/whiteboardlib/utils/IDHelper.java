package com.yinghe.whiteboardlib.utils;

import android.content.Context;

/**
 * 反射调用资源id
 * Created by wang on 2016/5/23 11:43
 */
public class IDHelper {
    public static int getLayout(Context mContext, String layoutName) {
        return ResourceHelper.getInstance(mContext).getLayoutId(layoutName);
    }

    public static int getViewID(Context mContext, String IDName) {
        return ResourceHelper.getInstance(mContext).getId(IDName);
    }

    public static int getDrawable(Context context, String drawableName) {
        return ResourceHelper.getInstance(context).getDrawableId(drawableName);
    }

    public static int getMipmap(Context context, String mipmap) {
        return ResourceHelper.getInstance(context).getMipmap(mipmap);
    }

    public static int getAttr(Context context, String attrName) {
        return ResourceHelper.getInstance(context).getAttrId(attrName);
    }

    public static int getString(Context context, String stringName) {
        return ResourceHelper.getInstance(context).getStringId(stringName);
    }
}
