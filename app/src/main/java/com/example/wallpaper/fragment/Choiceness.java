package com.example.wallpaper.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wallpaper.R;
import com.example.wallpaper.activity.PreviewActivity;
import com.example.wallpaper.bean.ChoicenessData;
import com.example.wallpaper.utils.HttpUtils;
import com.example.wallpaper.utils.StreamUtils;
import com.example.wallpaper.view.SwipeRefreshView;
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
public class Choiceness extends Fragment {

    private List<ChoicenessData> datas = new ArrayList<>();
    public RecyclerView recyclerview;

    int skip = 0;
    private String data;
    private final static int TIME_OUT = 1000;//超时时间
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
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
                            data.setId(temp.getString("id"));
                            data.setThumb(temp.getString("thumb"));
                            data.setImg(temp.getString("img"));
                            data.setPreview(temp.getString("preview"));
                            datas.add(data);
                        }
                        //布局方式
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                        recyclerview.setLayoutManager(layoutManager);
                        recyclerview.setAdapter(new RecyclerViewAdapter(datas, getActivity()));
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
        recyclerview = view.findViewById(R.id.recycler_view);
        initData();
        return view;
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    @SuppressWarnings("deprecation")
                    String PATH = HttpUtils.host + "/vertical/vertical?limit=30&skip=" + skip + "&adult=false&first=0&order=hot";
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

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        public List<ChoicenessData> list;
        public Context con;
        public LayoutInflater inflater;

        public RecyclerViewAdapter(List<ChoicenessData> list, Context con) {
            this.con = con;
            this.list = list;
            inflater = LayoutInflater.from(con);
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_rv, null);
            final RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(view);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//带参传值
                    int position = viewHolder.getAdapterPosition();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", list.get(position).getId());
                    bundle.putString("img", list.get(position).getImg());
                    bundle.putString("preview", list.get(position).getPreview());
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(getContext(), PreviewActivity.class);
                    startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
            //使用Picasso图片加载库加载图片
            if (TextUtils.isEmpty(list.get(position).getThumb().toString())) {
                Picasso.with(getContext()).cancelRequest(holder.iv);
                holder.iv.setImageDrawable(getResources()
                        .getDrawable(R.color.colorLightWhite));//当图片为空时显示
            } else {//图片加载
                Picasso.with(getContext())
                        .load((list.get(position).getThumb().toString()))
                        .placeholder(R.color.colorLightWhite)//图片加载中显示
                        .into(holder.iv);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView iv;
            View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                iv = itemView.findViewById(R.id.iv);
            }
        }
    }
}
