package com.example.xzrefreshlayout;

import android.content.Context;
import android.view.View;

/**
 * Created by shiwu on 2017/7/29.
 */

public abstract class XZRefreshHolder {
    protected Context context;
    protected View mHeaderView;
    protected View mFooterView;

    public XZRefreshHolder(Context context) {
        this.context = context;
        initHeaderView();
        initFooterView();
    }

    protected abstract void initHeaderView();

    protected abstract void initFooterView();

    public abstract void changeToPullDown();

    public abstract void changeToReleaseRefresh();

    public abstract void changeToRefreshing();

    public abstract void changeToPullUp();

    public abstract void changeToReleaseLoadMore();

    public abstract void changeToLoadingMore();

    public abstract void setFooterLastPage();

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView() {
        return mFooterView;
    }
}
