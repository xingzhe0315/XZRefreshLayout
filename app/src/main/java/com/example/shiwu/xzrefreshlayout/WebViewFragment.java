package com.example.shiwu.xzrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.xzrefreshlayout.NormalRefreshHolder;
import com.example.xzrefreshlayout.XZRefreshLayout;
import com.example.xzrefreshlayout.callback.XZRefreshCallback;


public class WebViewFragment extends Fragment implements XZRefreshCallback {
    private XZRefreshLayout refreshLayout;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
//                    initData();
                    refreshLayout.endRefreshing();
                    break;
                case 1:
//                    loadData();
                    refreshLayout.endLoading();
                    refreshLayout.setLastPage(true);
                    break;
            }
        }
    };

    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance() {
        WebViewFragment fragment = new WebViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefreshView(view);
        initView(view);
    }

    private void initView(View view) {
        WebView webView = (WebView) view.findViewById(R.id.web_view);
        webView.loadUrl("https://m.homedo.com");
    }

    private void initRefreshView(View view) {
        refreshLayout = (XZRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshHolder(new NormalRefreshHolder(getContext()));
        refreshLayout.setRefreshCallback(this);
        refreshLayout.setCanLoadMore(false);
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(0,2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.sendEmptyMessageDelayed(1,2000);
    }
}
