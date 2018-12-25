package com.example.wallpaper.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallpaper.R;
import com.example.wallpaper.bean.ClassifyData;
import com.example.wallpaper.utils.HttpUtils;
import com.example.wallpaper.utils.StreamUtils;
import com.example.wallpaper.view.SwipeRefreshView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * 分类
 */
public class Classify extends Fragment implements AdapterView.OnItemClickListener {

    private GridView gl;
    private List<ClassifyData> datas = new ArrayList<ClassifyData>();
    private SwipeRefreshView mSwipeRefreshView;

    private String data;
    private final static int TIME_OUT = 1000;//超时时间
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RESULT_OK:
                    String str = msg.obj.toString();
                    try {
                        //解析服务器端返回的数据
                        JSONObject obj = new JSONObject(str);
                        JSONObject obj2 = new JSONObject(obj.getString("res"));
                        JSONArray arr = new JSONArray(obj2.getString("category"));
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject temp = (JSONObject) arr.get(i);
                            ClassifyData data = new ClassifyData();
                            data.setId(temp.getString("id"));
                            data.setCover(temp.getString("cover"));
                            data.setName(temp.getString("name"));
                            datas.add(data);
                        }
                        gl.setAdapter(new MyAdapter());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getActivity(), "服务器繁忙……", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classify, container, false);
        gl = view.findViewById(R.id.gl);
        mSwipeRefreshView = view.findViewById(R.id.srl);
        gl.setOnItemClickListener(this);
        initData();
        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        mSwipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        mSwipeRefreshView.setColorSchemeResources(R.color.colorRed, R.color.colorPrimaryDark, R.color.colorRed);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datas.clear();
                initData();
                // 加载完数据设置为不刷新状态，将下拉进度收起来
                mSwipeRefreshView.setRefreshing(false);
            }
        });
        return view;
    }

    private void initData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    @SuppressWarnings("deprecation")
                    String PATH = HttpUtils.host + "/vertical/category?adult=false&first=1";
                    URL url = new URL(PATH);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //配置参数
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(TIME_OUT);
                    connection.setReadTimeout(TIME_OUT);
                    //打开链接
                    connection.connect();
                    //获取状态码
                    int responseCode = connection.getResponseCode();
                    if (200 == responseCode) {
                        //获取返回值
                        InputStream inputStream = connection.getInputStream();
                        //将字节流输入流转换为字符串
                        data = StreamUtils.inputSteam2String(inputStream);
                        handler.obtainMessage(RESULT_OK, data).sendToTarget();
                    } else {
                        handler.obtainMessage(RESULT_CANCELED, responseCode).sendToTarget();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    handler.obtainMessage(RESULT_CANCELED, e.getMessage()).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.obtainMessage(RESULT_CANCELED, e.getMessage()).sendToTarget();
                }
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View news_item_view = null;
            ViewHolder holder = null;
            if (view == null) {
                news_item_view = View.inflate(getContext(), R.layout.item_gl_classify, null);
                holder = new ViewHolder();
                holder.iv = news_item_view.findViewById(R.id.iv);
                holder.tv = news_item_view.findViewById(R.id.tv);
                news_item_view.setTag(holder);
            } else {
                news_item_view = view;
                holder = (ViewHolder) news_item_view.getTag();
            }
            //使用Picasso图片加载库加载图片
            if (TextUtils.isEmpty(datas.get(position).getCover().toString())) {
                Picasso.with(getContext()).cancelRequest(holder.iv);
                holder.iv.setImageDrawable(getResources()
                        .getDrawable(R.color.colorLightWhite));//当图片为空时显示
            } else {//图片加载
                Picasso.with(getContext())
                        .load((datas.get(position).getCover().toString()))
                        .placeholder(R.color.colorLightWhite)//图片加载中显示
                        .into(holder.iv);
            }
            holder.tv.setText(datas.get(position).getName());//中文名
            return news_item_view;
        }
    }

    static class ViewHolder {
        public ImageView iv;
        public TextView tv;
    }

}
