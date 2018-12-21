package com.example.wallpaper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.wallpaper.fragment.Choiceness;
import com.example.wallpaper.fragment.Classify;

/**
 * 手机壁纸导航栏适配器
 */
public class MobilePhoneFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"精选", "分类"};

    public MobilePhoneFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Choiceness();
        }
        return new Classify();
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
