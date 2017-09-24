package com.example.shiwu.xzrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shiwu.xzrefreshlayout.adapters.MyRecyclerAdapter;
import com.example.xzrefreshlayout.NormalRefreshHolder;
import com.example.xzrefreshlayout.XZRefreshLayout;
import com.example.xzrefreshlayout.callback.XZRefreshCallback;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewFragment extends Fragment implements XZRefreshCallback {
    private XZRefreshLayout refreshLayout;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    initData();
                    refreshLayout.endRefreshing();
                    refreshLayout.setLastPage(false);
                    break;
                case 1:
                    loadData();
                    refreshLayout.endLoading();
                    refreshLayout.setLastPage(true);
                    break;
            }
        }
    };

    private MyRecyclerAdapter mAdapter;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    public static RecyclerViewFragment newInstance() {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
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
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefreshView(view);
        initRecyclerView(view);
        initData();
    }

    private void initData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("this is the " + i + " item");
        }
        mAdapter.setDatas(datas);
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MyRecyclerAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
    }

    private void initRefreshView(View view) {
        refreshLayout = (XZRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshHolder(new NormalRefreshHolder(getContext()));
        refreshLayout.setRefreshCallback(this);
//        refreshLayout.setAutoLoad(true);
    }

    private void loadData(){
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("this is the " + i + " item");
        }
        mAdapter.addData(datas);
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
