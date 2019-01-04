package com.example.wallpaper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.wallpaper.R;
import com.example.wallpaper.utils.ImgDonwload;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 预览手机壁纸
 */
public class PreviewActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv, iv_back;
    private Button btn_set;
    private static ProgressDialog mSaveDialog = null;

    @Override
    protected int getLayoutID() {
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        mSaveDialog = ProgressDialog.show(this, "", "加载中，请稍等...", true);
        Bundle bundle = getIntent().getExtras();
        final String img = bundle.getString("img");
        //创建一个新线程，用于从网络上获取图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从网络上获取图片
                final Bitmap bitmap = getPicture(img);
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
                        mSaveDialog.dismiss();
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

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    public void onClick(View v) {
        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("id");
        final String img = bundle.getString("img");
        final int permission = ActivityCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        switch (v.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.btn_set://下载并设为壁纸
                //检测是否有写的权限
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                } else {
                    ImgDonwload.donwloadImg(PreviewActivity.this, img, id);
                    Intent chooseIntnet = new Intent(Intent.ACTION_SET_WALLPAPER);
                    Intent chooser = Intent.createChooser(chooseIntnet, getText(R.string.app_name));
                    startActivity(chooser);
                }
                break;
            default:
                break;
        }
    }
}
