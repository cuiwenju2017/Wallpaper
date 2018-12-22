package com.example.wallpaper.activity;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wallpaper.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 预览
 */
public class PreviewActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv, iv_back;
    private Button btn_set;

    @Override
    protected int getLayoutID() {
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉虚拟按键全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //       设置屏幕始终在前面，不然点击鼠标，重新出现虚拟按键
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                        // bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        return R.layout.activity_preview;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {
        //获取组件
        iv = findViewById(R.id.iv);
        iv_back = findViewById(R.id.iv_back);
        btn_set = findViewById(R.id.btn_set);
        //获取监听
        iv.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_set.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        final String message = bundle.getString("a");
        //创建一个新线程，用于从网络上获取图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从网络上获取图片
                final Bitmap bitmap = getPicture(message);
                try {
                    Thread.sleep(500);//线程休眠半秒钟
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发送一个Runnable对象
                iv.post(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(bitmap);//在ImageView中显示从网络上获取到的图片
                    }
                });
            }
        }).start();//开启线程
    }

    public Bitmap getPicture(String path) {
        Bitmap bm = null;
        URL url;
        try {
            url = new URL(path);//创建URL对象
            URLConnection conn = url.openConnection();//获取URL对象对应的连接
            conn.connect();//打开连接
            InputStream is = conn.getInputStream();//获取输入流对象
            bm = BitmapFactory.decodeStream(is);//根据输入流对象创建Bitmap对象
        } catch (MalformedURLException e1) {
            e1.printStackTrace();//输出异常信息
        } catch (IOException e) {
            e.printStackTrace();//输出异常信息
        }
        return bm;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.btn_set://设为壁纸

                Bundle bundle = getIntent().getExtras();
                final String message = bundle.getString("a");
                //创建一个新线程，用于从网络上获取图片
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //从网络上获取图片
                        final Bitmap bitmap = getPicture(message);
                        try {
                            Thread.sleep(500);//线程休眠半秒钟
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //发送一个Runnable对象
                        iv.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    WallpaperManager wpm = (WallpaperManager) act.getSystemService(Context.WALLPAPER_SERVICE);
                                    wpm.setBitmap(bitmap);
                                    Toast.makeText(act, "壁纸设置成功，返回桌面查看", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Log.e(TAG, "设置壁纸失败: " + e);
                                }
                            }
                        });
                    }
                }).start();//开启线程
                break;
            default:
                break;
        }
    }
}
