package com.example.wallpaper.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallpaper.R;
import com.example.wallpaper.activity.PreviewActivity;
import com.example.wallpaper.bean.ChoicenessData;
import com.example.wallpaper.utils.HttpUtils;
import com.example.wallpaper.utils.StreamUtils;
import com.squareup.picasso.Picasso;

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
 * 精选
 */
public class Choiceness extends Fragment implements AdapterView.OnItemClickListener {

    private GridView gl;
    private List<ChoicenessData> datas = new ArrayList<ChoicenessData>();

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
                        JSONArray arr = new JSONArray(obj2.getString("vertical"));
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject temp = (JSONObject) arr.get(i);
                            ChoicenessData data = new ChoicenessData();
                            data.setThumb(temp.getString("thumb"));
                            data.setImg(temp.getString("img"));
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
        View view = inflater.inflate(R.layout.fragment_choiceness, container, false);
        initData();
        gl = view.findViewById(R.id.gl);
        gl.setOnItemClickListener(this);
        return view;
    }

    private void initData() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    @SuppressWarnings("deprecation")
                    String PATH = HttpUtils.host + "?limit=30&skip=180&adult=false&first=0&order=hot";
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
        Bundle bundle = new Bundle();
        bundle.putString("a", datas.get(position).getImg());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(getContext(), PreviewActivity.class);
        startActivity(intent);
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

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View news_item_view = null;
            ViewHolder holder = null;
            if (view == null) {
                news_item_view = View.inflate(getContext(), R.layout.item_gl, null);
                holder = new ViewHolder();
                holder.iv = news_item_view.findViewById(R.id.iv);
                news_item_view.setTag(holder);
            } else {
                news_item_view = view;
                holder = (ViewHolder) news_item_view.getTag();
            }

            //使用Picasso图片加载库加载图片
            if (TextUtils.isEmpty(datas.get(position).getThumb().toString())) {
                Picasso.with(getContext()).cancelRequest(holder.iv);
                holder.iv.setImageDrawable(getResources()
                        .getDrawable(R.color.colorLightWhite));//当图片为空时显示
            } else {//图片加载
                Picasso.with(getContext())
                        .load((datas.get(position).getThumb().toString()))
                        .placeholder(R.color.colorLightWhite)//图片加载中显示
                        .into(holder.iv);
            }


            return news_item_view;
        }
    }

    static class ViewHolder {
        public ImageView iv;
    }

}
