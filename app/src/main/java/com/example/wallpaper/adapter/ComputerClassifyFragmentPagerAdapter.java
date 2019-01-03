package com.example.wallpaper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.wallpaper.fragment.FragmentComputerHottest;
import com.example.wallpaper.fragment.FragmentComputerNewest;
import com.example.wallpaper.fragment.FragmentHottest;
import com.example.wallpaper.fragment.FragmentNewest;

/**
 * 电脑壁纸导航栏适配器
 */
public class ComputerClassifyFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"最新", "热门"};

    public ComputerClassifyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FragmentComputerNewest();
        }
        return new FragmentComputerHottest();
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
