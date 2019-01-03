package com.example.wallpaper.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wallpaper.R;
import com.example.wallpaper.adapter.ClassifyFragmentPagerAdapter;
import com.example.wallpaper.adapter.ComputerClassifyFragmentPagerAdapter;

public class ComputerClassifyActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ComputerClassifyFragmentPagerAdapter computerClassifyFragmentPagerAdapter;
    private TabLayout mTabLayout;
    private TabLayout.Tab one;
    private TabLayout.Tab two;
    SharedPreferences sprfMain;
    SharedPreferences.Editor editorMain;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_computer_classify;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {
        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager = findViewById(R.id.viewPager);
        computerClassifyFragmentPagerAdapter = new ComputerClassifyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(computerClassifyFragmentPagerAdapter);
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
        sprfMain = getSharedPreferences("counter", Context.MODE_PRIVATE);
        editorMain = sprfMain.edit();
        editorMain.putString("id", id);
        editorMain.commit();
    }
}
