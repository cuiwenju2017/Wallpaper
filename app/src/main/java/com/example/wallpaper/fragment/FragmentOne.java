package com.example.wallpaper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wallpaper.R;
import com.example.wallpaper.adapter.MobilePhoneFragmentPagerAdapter;

public class FragmentOne extends Fragment {

    private ViewPager mViewPager;
    private MobilePhoneFragmentPagerAdapter mobilePhoneFragmentPagerAdapter;
    private TabLayout mTabLayout;
    private TabLayout.Tab one;
    private TabLayout.Tab two;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mobilePhoneFragmentPagerAdapter = new MobilePhoneFragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mobilePhoneFragmentPagerAdapter);
        //将TabLayout与ViewPager绑定在一起
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        //指定Tab的位置
        one = mTabLayout.getTabAt(0);
        two = mTabLayout.getTabAt(1);
        return view;
    }
}
