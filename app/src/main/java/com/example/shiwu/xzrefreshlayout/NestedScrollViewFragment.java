package com.example.shiwu.xzrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xzrefreshlayout.NormalRefreshHolder;
import com.example.xzrefreshlayout.XZRefreshLayout;
import com.example.xzrefreshlayout.callback.XZRefreshCallback;


public class NestedScrollViewFragment extends Fragment implements XZRefreshCallback {

    private XZRefreshLayout refreshLayout;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    refreshLayout.endRefreshing();
                    break;
                case 1:
                    refreshLayout.endLoading();
                    break;
            }
        }
    };


    public NestedScrollViewFragment() {
        // Required empty public constructor
    }

    public static NestedScrollViewFragment newInstance() {
        NestedScrollViewFragment fragment = new NestedScrollViewFragment();
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
        return inflater.inflate(R.layout.fragment_nested_scroll_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        refreshLayout = (XZRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshHolder(new NormalRefreshHolder(getContext()));
        refreshLayout.setCanLoadMore(false);
        refreshLayout.setRefreshCallback(this);
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }
}
