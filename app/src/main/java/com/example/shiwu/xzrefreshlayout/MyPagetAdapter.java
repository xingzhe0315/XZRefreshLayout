package com.example.shiwu.xzrefreshlayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by shiwu on 2017/8/7.
 */

public class MyPagetAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private List<String> titles;
    public MyPagetAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyPagetAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
