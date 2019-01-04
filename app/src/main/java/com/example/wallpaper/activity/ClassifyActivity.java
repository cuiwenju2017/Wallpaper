package com.example.wallpaper.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.wallpaper.R;
import com.example.wallpaper.adapter.ClassifyFragmentPagerAdapter;

/**
 * 分类详情
 */
public class ClassifyActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ClassifyFragmentPagerAdapter classifyFragmentPagerAdapter;
    private TabLayout mTabLayout;
    private TabLayout.Tab one;
    private TabLayout.Tab two;
    SharedPreferences sprfMain;
    SharedPreferences.Editor editorMain;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_classify;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {
        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager = findViewById(R.id.viewPager);
        classifyFragmentPagerAdapter = new ClassifyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(classifyFragmentPagerAdapter);
        //将TabLayout与ViewPager绑定在一起
        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        //指定Tab的位置
        one = mTabLayout.getTabAt(0);
        two = mTabLayout.getTabAt(1);
    }


    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("id");
        //保存id
        sprfMain = getSharedPreferences("counter", Context.MODE_PRIVATE);
        editorMain = sprfMain.edit();
        editorMain.putString("id", id);
        editorMain.commit();
    }
}
