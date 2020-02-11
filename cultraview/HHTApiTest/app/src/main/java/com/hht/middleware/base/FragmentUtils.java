package com.hht.middleware.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Author: chenhu
 * Time: 2019/12/12 11:27
 * Description do somethings
 */
public class FragmentUtils {


    private FragmentManager mFragmentManager;
    private int mContentId;


    private static class Holder {
        static FragmentUtils holder = new FragmentUtils();
    }

    public static FragmentUtils getInstance() {
        return Holder.holder;
    }

    public FragmentUtils config(Activity activity, FragmentManager manager) {
        mFragmentManager = manager;
        return this;
    }

    public Fragment findFragment(Class<?> fragment) {
        if (mFragmentManager != null) {
            return mFragmentManager.findFragmentByTag(fragment.getName());
        }
        return null;
    }


    /**
     * 加载根目录
     *
     * @param contentId
     * @param fragment
     */
    public void loadRootFragment(int contentId, Fragment fragment) {
        mContentId = contentId;
        if (mFragmentManager != null) {
            String tag = fragment.getClass().getName();
            FragmentTransaction ft = mFragmentManager.beginTransaction();

            ft.add(mContentId, fragment, tag);
            //防止Can not perform this action after onSaveInstanceState
            ft.commit();

        }
    }

    public void replaceFragment(Fragment showFragment) {
        if (mFragmentManager != null) {
            String tag = showFragment.getClass().getName();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(mContentId, showFragment, tag);
            ft.commit();
        }
    }


}
