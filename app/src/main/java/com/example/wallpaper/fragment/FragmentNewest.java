package com.example.wallpaper.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wallpaper.R;
import com.example.wallpaper.activity.PreviewActivity;
import com.example.wallpaper.bean.ChoicenessData;
import com.example.wallpaper.listener.EndlessRecyclerOnScrollListener;
import com.example.wallpaper.utils.HttpUtils;
import com.example.wallpaper.utils.StreamUtils;
import com.example.wallpaper.wrapper.LoadMoreWrapper;
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
 * 手机壁纸最新
 */
public class FragmentNewest extends Fragment {

    private List<ChoicenessData> dataList = new ArrayList<>();
    public RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LoadMoreWrapper loadMoreWrapper;
    private LoadMoreWrapperAdapter loadMoreWrapperAdapter;
    SharedPreferences sprfMain;
    SharedPreferences.Editor editorMain;

    int skip = 0;
    private String data;
    private final static int TIME_OUT = 1000;//超时时间
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESULT_OK:
                    try {
                        //解析服务器端返回的数据
                        JSONObject obj = new JSONObject(data);
                        JSONObject obj2 = new JSONObject(obj.getString("res"));
                        JSONArray arr = new JSONArray(obj2.getString("vertical"));
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject temp = (JSONObject) arr.get(i);
                            ChoicenessData map = new ChoicenessData();
                            map.setId(temp.getString("id"));
                            map.setThumb(temp.getString("thumb"));
                            map.setImg(temp.getString("img"));
                            map.setPreview(temp.getString("preview"));
                            dataList.add(map);
                        }
                        loadMoreWrapperAdapter = new LoadMoreWrapperAdapter(dataList);
                        loadMoreWrapper = new LoadMoreWrapper(loadMoreWrapperAdapter);
                        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                        recyclerview.setAdapter(loadMoreWrapper);
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
        swipeRefreshLayout = view.findViewById(R.id.swiperefreshlayout);
        initData();

        // 设置刷新控件颜色
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#d81e06"));
        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新数据
                dataList.clear();
                skip = skip + 30;
                initData();
                // 延时1s关闭下拉刷新
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });
        // 设置加载更多监听
        recyclerview.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                // 显示加载到底的提示
                loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
            }
        });
        return view;
    }

    private void initData() {
        //取出上个页面保存的值
        sprfMain = getActivity().getSharedPreferences("counter", Context.MODE_PRIVATE);
        final String id = sprfMain.getString("id", "");
        new Thread(new Runnable() {
            @SuppressLint("HandlerLeak")
            @Override
            public void run() {
                try {
                    @SuppressWarnings("deprecation")
                    String PATH = HttpUtils.host + "/vertical/category/" + id + "/vertical?limit=30&skip=" + skip + "&adult=false&first=1&order=new";
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

    public class LoadMoreWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ChoicenessData> dataList;

        public LoadMoreWrapperAdapter(List<ChoicenessData> dataList) {
            this.dataList = dataList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, parent, false);
            final LoadMoreWrapperAdapter.RecyclerViewHolder viewHolder = new LoadMoreWrapperAdapter.RecyclerViewHolder(view);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//带参传值
                    int position = viewHolder.getAdapterPosition();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", dataList.get(position).getId());
                    bundle.putString("img", dataList.get(position).getImg());
                    bundle.putString("preview", dataList.get(position).getPreview());
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(getContext(), PreviewActivity.class);
                    startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LoadMoreWrapperAdapter.RecyclerViewHolder recyclerViewHolder = (LoadMoreWrapperAdapter.RecyclerViewHolder) holder;
            //使用Picasso图片加载库加载图片
            if (TextUtils.isEmpty(dataList.get(position).getThumb().toString())) {
                Picasso.with(getContext()).cancelRequest(recyclerViewHolder.iv);
                recyclerViewHolder.iv.setImageDrawable(getResources()
                        .getDrawable(R.color.colorLightWhite));//当图片为空时显示
            } else {//图片加载
                Picasso.with(getContext())
                        .load((dataList.get(position).getThumb().toString()))
                        .placeholder(R.color.colorLightWhite)//图片加载中显示
                        .into(recyclerViewHolder.iv);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            ImageView iv;
            View view;

            RecyclerViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                iv = itemView.findViewById(R.id.iv);
            }
        }
    }
}
