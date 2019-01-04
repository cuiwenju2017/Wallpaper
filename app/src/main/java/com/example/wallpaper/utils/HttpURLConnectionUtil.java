package com.example.wallpaper.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpURLConnectionUtil {

    private static ProgressDialog mSaveDialog = null;

    /**
     * 设置系统壁纸
     * 1、把网络图片设置系统壁纸
     * 2、因为谷歌不维护其他框架了，所以使用HttpURLConnection来下载和配置
     *
     * @param activity
     * @param imgUrl
     */
    public static void setWallpaper(final Activity activity, final String imgUrl) {
        //Log.e("壁纸", "链接：" + imgUrl);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL httpUrl = new URL(imgUrl);//获取传入进来的url地址  并捕获解析过程产生的异常
                    //使用是Http访问  所以用HttpURLConnection  同理如果使用的是https  则用HttpsURLConnection
                    try {
                        HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();//通过httpUrl开启一个HttpURLConnection对象
                        conn.setReadTimeout(5000);//设置显示超市时间为5秒
                        conn.setRequestMethod("GET");//设置访问方式
                        conn.setDoInput(true);//设置可以获取输入流

                        InputStream in = conn.getInputStream();//获取输入流

                        //创建一个写入ID卡的文件对象
                        FileOutputStream out = null;
                        File download = null;
                        String filename = String.valueOf(System.currentTimeMillis());//获取系统时间
                        //判断文件是否存在   Environment.MEDIA_MOUNTEDID卡是否挂载  如果是则创建文件对象
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            File parent = Environment.getExternalStorageDirectory();//获取ID卡目录
                            download = new File(parent, filename);//在父类的目录下创建一个以当前下载的系统时间为文件名的文件
                            out = new FileOutputStream(download);
                        }

                        byte[] b = new byte[2 * 1024];
                        int len;
                        if (out != null) {//id卡如果存在  则写入
                            while ((len = in.read(b)) != -1) {
                                out.write(b, 0, len);
                            }
                        }

                        //读取该文件中的内容
                        final Bitmap bitmap = BitmapFactory.decodeFile(download.getAbsolutePath());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //设置图片为壁纸
                                //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.bg_user_top);//设置项目res中的图片
                                WallpaperManager manager = WallpaperManager.getInstance(activity);
                                try {
                                    manager.setBitmap(bitmap);
                                    Toast.makeText(activity, "壁纸设置成功，请在桌面上查看", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Toast.makeText(activity, "壁纸设置失败", Toast.LENGTH_SHORT).show();
                                    mSaveDialog.dismiss();
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
