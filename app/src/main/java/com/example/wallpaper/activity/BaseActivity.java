package com.example.wallpaper.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity {
    protected BaseActivity act;
    protected final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(getLayoutID());
        initView();
        initData();
        initListener();
        // 透明状态栏
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/
    }

    @LayoutRes
    protected abstract int getLayoutID();

    protected abstract void initListener();

    protected abstract void initView();

    protected abstract void initData();

    @SuppressWarnings("unchecked")
    protected <E> E f(int id) {
        return (E) findViewById(id);
    }
}
