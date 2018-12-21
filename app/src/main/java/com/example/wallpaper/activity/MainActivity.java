package com.example.wallpaper.activity;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.wallpaper.R;
import com.example.wallpaper.adapter.PagerMainAdapter;
import com.example.wallpaper.fragment.FragmentOne;
import com.example.wallpaper.fragment.FragmentThree;
import com.example.wallpaper.fragment.FragmentTwo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private ViewPager vp;
    private RadioGroup rg;
    private int[] rbs = {R.id.rb_home, R.id.rb_system, R.id.rb_nav};
    private List<Fragment> mFragments;
    private boolean mBackKeyPressed = false;//记录是否有首次按键

    //简化后的方法
    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        vp = f(R.id.vp);
        rg = f(R.id.rg);
    }

    @Override
    protected void initData() {
        mFragments = new ArrayList<>();
        FragmentOne one = new FragmentOne();
        FragmentTwo two = new FragmentTwo();
        FragmentThree three = new FragmentThree();
        mFragments.add(one);
        mFragments.add(two);
        mFragments.add(three);
        // 设置填充器
        vp.setAdapter(new PagerMainAdapter(getSupportFragmentManager(), mFragments));
        // 设置缓存页面数
        vp.setOffscreenPageLimit(2);
    }

    @Override
    protected void initListener() {
        //radioGroup的点击事件
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i = 0; i < rbs.length; i++) {
                    if (rbs[i] != checkedId) continue;
                    //加载滑动
                    vp.setCurrentItem(i);
                }
            }
        });
        //ViewPager的点击事件 vp-rg互相监听：vp
        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                rg.check(rbs[position]);
            }
        });
        //设置一个默认页
        rg.check(rbs[0]);
    }

    public void onBackPressed() {
        if (!mBackKeyPressed) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {//延时两秒，如果超出则清除第一次记录

                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }
}

