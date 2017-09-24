package com.example.shiwu.xzrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.xzrefreshlayout.XZRefreshLayout;
import com.example.xzrefreshlayout.callback.XZRefreshCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements XZRefreshCallback {

    private XZRefreshLayout refreshLayout;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
//                    refreshLayout.endRefreshing();
                    break;
                case 1:
//                    refreshLayout.endLoading();
                    break;
            }
        }
    };
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setContentView(R.layout.activity_main);
        initTabLayout();
        initViewPager();
        initView();
//        refreshLayout.beginRefresh();
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new NestedScrollViewFragment());
        fragments.add(new RecyclerViewFragment());
        fragments.add(new ScrollViewFragment());
        fragments.add(new AbsListViewFragment());
        fragments.add(new WebViewFragment());

        List<String> titles = new ArrayList<>();
        titles.add("NestedScrollView");
        titles.add("RecyclerView");
        titles.add("ScrollView");
        titles.add("AbsListView");
        titles.add("WebView");

        PagerAdapter adapter = new MyPagetAdapter(getSupportFragmentManager(),fragments,titles);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("NestedScrollView"));
        tabLayout.addTab(tabLayout.newTab().setText("RecyclerView"));
        tabLayout.addTab(tabLayout.newTab().setText("ScrollView"));
        tabLayout.addTab(tabLayout.newTab().setText("AbsListView"));
        tabLayout.addTab(tabLayout.newTab().setText("WebView"));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
//        refreshLayout = (XZRefreshLayout) findViewById(R.id.refresh_layout);
//        refreshLayout.setRefreshHolder(new NormalRefreshHolder(this));
//        refreshLayout.setRefreshCallback(this);
    }

    @Override
    public void onRefresh() {
        Log.e("MainActivity","---onRefresh()");
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onLoadMore() {
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }
}
