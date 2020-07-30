package com.protruly.floatwindowlib.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Carson_Ho on 16/7/22.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles;

    public MyPagerAdapter(FragmentManager fm, String[] mTitles) {
        super(fm);
        this.mTitles = mTitles;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new VoiceFragment();
        } else if (position == 2) {
            return new OptionsFragment();
        }else if (position==3){
            return new MoreFragment();
        }
        return new DisplayFragment();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }


}
