package com.example.xzrefreshlayout;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shiwu on 2017/7/29.
 */

public class NormalRefreshHolder extends XZRefreshHolder {
    private TextView mHeaderTv;
    private TextView mFooterTv;

    private ImageView mRefreshArrow;
    private ImageView mRefreshLoadingIv;
    private AnimationDrawable mRefreshingDrawable;

    private ImageView mLoadingIv;
    private AnimationDrawable mLoadingDrawable;

    private Animation mArrowUpAnim;
    private Animation mArrowDownAnim;

    public NormalRefreshHolder(Context context) {
        super(context);
        mArrowUpAnim = new RotateAnimation(0,-180,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mArrowDownAnim = new RotateAnimation(-180,0,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mArrowUpAnim.setDuration(150);
        mArrowDownAnim.setDuration(150);
        mArrowUpAnim.setFillAfter(true);
        mArrowDownAnim.setFillAfter(true);
    }

    @Override
    protected void initHeaderView() {
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.view_normal_header, null);
        mHeaderTv = (TextView) mHeaderView.findViewById(R.id.header_tv);
        mRefreshArrow = (ImageView) mHeaderView.findViewById(R.id.refresh_arrow);
        mRefreshLoadingIv = (ImageView) mHeaderView.findViewById(R.id.refreshing_anim);
        mRefreshingDrawable = (AnimationDrawable) mRefreshLoadingIv.getDrawable();
    }

    @Override
    protected void initFooterView() {
        mFooterView = LayoutInflater.from(context).inflate(R.layout.view_normal_footer, null);
        mFooterTv = (TextView) mFooterView.findViewById(R.id.footer_tv);

        mLoadingIv = (ImageView) mFooterView.findViewById(R.id.loading_iv);
        mLoadingDrawable = (AnimationDrawable) mLoadingIv.getDrawable();
    }

    @Override
    public void changeToPullDown() {
        mHeaderTv.setText("下拉刷新");
        mRefreshingDrawable.stop();
        mRefreshLoadingIv.setVisibility(View.INVISIBLE);
        mRefreshArrow.setVisibility(View.VISIBLE);
        mRefreshArrow.startAnimation(mArrowDownAnim);
    }

    @Override
    public void changeToReleaseRefresh() {
        mHeaderTv.setText("释放刷新");
        mRefreshingDrawable.stop();
        mRefreshLoadingIv.setVisibility(View.INVISIBLE);
        mRefreshArrow.setVisibility(View.VISIBLE);
        mRefreshArrow.startAnimation(mArrowUpAnim);

    }

    @Override
    public void changeToRefreshing() {
        mHeaderTv.setText("正在刷新");
        mRefreshArrow.clearAnimation();
        mRefreshArrow.setVisibility(View.INVISIBLE);
        mRefreshLoadingIv.setVisibility(View.VISIBLE);
        mRefreshingDrawable.start();
    }

    @Override
    public void changeToPullUp() {
        mLoadingDrawable.stop();
        mLoadingIv.setVisibility(View.INVISIBLE);
        mFooterTv.setText("上拉加载更多");
    }

    @Override
    public void changeToReleaseLoadMore() {
        mLoadingDrawable.stop();
        mLoadingIv.setVisibility(View.INVISIBLE);
        mFooterTv.setText("释放加载更多");
    }

    @Override
    public void changeToLoadingMore() {
        mFooterTv.setText("正在加载");
        mLoadingIv.setVisibility(View.VISIBLE);
        mLoadingDrawable.start();
    }

    @Override
    public void setFooterLastPage() {
        mLoadingDrawable.stop();
        mLoadingIv.setVisibility(View.INVISIBLE);
        mFooterTv.setText("已经是最后一页了");
    }
}
