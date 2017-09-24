package com.example.shiwu.xzrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.shiwu.xzrefreshlayout.adapters.MyListViewAdapter;
import com.example.xzrefreshlayout.NormalRefreshHolder;
import com.example.xzrefreshlayout.XZRefreshLayout;
import com.example.xzrefreshlayout.callback.XZRefreshCallback;

import java.util.ArrayList;
import java.util.List;


public class AbsListViewFragment extends Fragment implements XZRefreshCallback {
    private XZRefreshLayout refreshLayout;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    refreshLayout.endRefreshing();
                    initData();
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
    private MyListViewAdapter adapter;

    public AbsListViewFragment() {
        // Required empty public constructor
    }

    public static AbsListViewFragment newInstance() {
        AbsListViewFragment fragment = new AbsListViewFragment();
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
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListView(view);
        initRefreshView(view);
        initData();
    }

    private void initData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("this is the " + i + " item");
        }
        adapter.setDatas(datas);
    }

    private void initListView(View view) {
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new MyListViewAdapter(getContext());
        listView.setAdapter(adapter);
    }

    private void initRefreshView(View view) {
        refreshLayout = (XZRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshHolder(new NormalRefreshHolder(getContext()));
        refreshLayout.setRefreshCallback(this);
    }

    public void loadData(){
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("this is the " + i + " item");
        }
        adapter.loadData(datas);
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
