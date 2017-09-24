package com.example.xzrefreshlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.example.xzrefreshlayout.callback.XZRefreshCallback;
import com.example.xzrefreshlayout.callback.XZRefreshMoveCallback;
import com.example.xzrefreshlayout.enums.XZRefreshStatus;
import com.example.xzrefreshlayout.util.XZJudgeViewStateUtil;

/**
 * Created by shiwu on 2017/7/29.
 */

public class XZRefreshLayout extends ViewGroup {

    private final float MAX_DISTANCE = 1.0F;//最大下拉/上拉高度，mHeaderHeight的倍数

    private final float SCALE_CRITICAL = 0.67F;//达到刷新／加载临界点的高度与头／底部view高度的倍数

    private int mReleaseRefreshHeight;//刷新临界点

    private int mReleaseLoadHeight;//加载临界点

    private int mMaxPullDown;//最大下拉高度

    private int mMaxPullUp;//最大上拉高度

    private XZRefreshStatus mStatus;//当前状态

    private XZRefreshCallback mRefreshCallback;
    private XZRefreshMoveCallback mRefreshMoveCallbacn;

    private ViewDragHelper mDragHelper;
    private ViewDragHelper.Callback mDragCallback;

    private View mContentView;
    private View mHeaderView;
    private View mFooterView;
    private XZRefreshHolder mRefreshHolder;

    private int mHeaderHeight;
    private int mFooterHeight;

    private int left;
    private int top;
    private int right;
    private int bottom;

    private boolean canLoadMore = true;
    private boolean isLastPage;
    private boolean autoLoad = false;
    private boolean canRefresh = true;

    public XZRefreshLayout(Context context) {
        this(context, null);
    }

    public XZRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XZRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.XZRefreshLayout);
        canRefresh = typedArray.getBoolean(R.styleable.XZRefreshLayout_canRefresh,true);
        canLoadMore = typedArray.getBoolean(R.styleable.XZRefreshLayout_canLoadMore,true);
        autoLoad = typedArray.getBoolean(R.styleable.XZRefreshLayout_autoLoad,false);
        typedArray.recycle();
        init();
    }

    private void init() {
        mDragCallback = new DragCallback();
        mDragHelper = ViewDragHelper.create(this, mDragCallback);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(0);
        if (mContentView instanceof AbsListView) {
            setOnScrollListener((AbsListView) mContentView);
        } else if (mContentView instanceof RecyclerView) {
            setOnScrollListener((RecyclerView) mContentView);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //保证事件传递的连贯性，mContentView下滑，当刚好滑动到顶部的时候把事件交由父容器处理，上滑亦然
        if (ev.getAction() == MotionEvent.ACTION_MOVE && (shouldIntercept(ev.getRawX(), ev.getRawY()))) {
            requestDisallowInterceptTouchEvent(false);
            Log.e("XZRefreshLayout", "----dispatchTouchEvent -- true");
        }
        //// TODO: 2017/8/14/0014 这段代码对ScrollView没有影响，但造成RecyclerView无法fling以及AbsListView无法连续fling ，先注释，后期优化
// else {
//            Log.e("XZRefreshLayout", "----dispatchTouchEvent -- false");
//            mContentView.dispatchTouchEvent(ev);//将事件交由子View处理
//        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean initLayout = true;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!initLayout)
            return;
        left = 0;
        top = 0;
        right = getMeasuredWidth();
        bottom = getMeasuredHeight();
        mHeaderView.layout(l, t - mHeaderHeight, r, t);
        mContentView.layout(l, t, r, b);
        mFooterView.layout(l, b, r, b + mFooterHeight);
        initLayout = false;
    }

    public void setRefreshHolder(XZRefreshHolder mRefreshHolder) {
        this.mRefreshHolder = mRefreshHolder;
        mHeaderView = mRefreshHolder.getHeaderView();
        mFooterView = mRefreshHolder.getFooterView();
        addView(mHeaderView, 0);
        addView(mFooterView);
        measureView(mHeaderView);
        measureView(mFooterView);
        getHeaderHeight();
        getFooterHeight();
    }

    private void measureView(View childView) {
        ViewGroup.LayoutParams p = childView.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height = p.height;
        int childHeightSpec;
        if (height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        childView.measure(childWidthSpec, childHeightSpec);
    }

    private void getHeaderHeight() {
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mReleaseRefreshHeight = (int) (SCALE_CRITICAL * mHeaderHeight);
        mMaxPullDown = (int) (MAX_DISTANCE * mHeaderHeight);
    }

    private void getFooterHeight() {
        mFooterHeight = mFooterView.getMeasuredHeight();
        mReleaseLoadHeight = (int) (SCALE_CRITICAL * mFooterHeight);
        mMaxPullUp = (int) (MAX_DISTANCE * mFooterHeight);
    }

    private float originalX;
    private float originalY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                originalX = ev.getRawX();
                originalY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = ev.getRawX();
                float currentY = ev.getRawY();
                shouldIntercept = shouldIntercept(currentX, currentY);
                break;
            case MotionEvent.ACTION_UP:
                originalX = originalY = -1;
                break;
        }
        mDragHelper.shouldInterceptTouchEvent(ev);
        if (shouldIntercept) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean shouldIntercept(float currentX, float currentY) {
        boolean shouldIntercept = Math.abs(currentY - originalY) > Math.abs(currentX - originalX)
                && (mStatus != XZRefreshStatus.REFRESHING && mStatus != XZRefreshStatus.LOADING)
                && ((currentY < originalY && XZJudgeViewStateUtil.isContentViewToBottom(mContentView) && canLoadMore && !autoLoad)
                || (currentY > originalY && XZJudgeViewStateUtil.isContentViewToTop(mContentView) && canRefresh));
        return shouldIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private void onContentMove(int distance) {
        mHeaderView.layout(left, distance - mHeaderHeight, right, distance);
        mFooterView.layout(left, bottom + distance, right, bottom + distance + mFooterHeight);
        if (distance >= 0 && mRefreshMoveCallbacn != null) {
            mRefreshMoveCallbacn.onMove(distance, mMaxPullDown);
        }
        if (mStatus != null && mStatus == XZRefreshStatus.REFRESHING || mStatus == XZRefreshStatus.LOADING) {
            return;
        }
        if (distance > 0 && distance < mReleaseRefreshHeight && mStatus != XZRefreshStatus.PULL_DOWN_TO_REFRESH) {
            mStatus = XZRefreshStatus.PULL_DOWN_TO_REFRESH;
            mRefreshHolder.changeToPullDown();
        } else if (distance >= mReleaseRefreshHeight && mStatus != XZRefreshStatus.RELEASE_TO_REFRESH) {
            mStatus = XZRefreshStatus.RELEASE_TO_REFRESH;
            mRefreshHolder.changeToReleaseRefresh();
        } else if (distance < 0 && distance > -mReleaseLoadHeight && mStatus != XZRefreshStatus.PULL_UP_TO_LOAD_MORE) {
            mStatus = XZRefreshStatus.PULL_UP_TO_LOAD_MORE;
            if (isLastPage)
                return;
            mRefreshHolder.changeToPullUp();
        } else if (distance <= -mReleaseLoadHeight  && mStatus != XZRefreshStatus.RELEASE_TO_LOAD_MORE) {
            mStatus = XZRefreshStatus.RELEASE_TO_LOAD_MORE;
            if (isLastPage)
                return;
            mRefreshHolder.changeToReleaseLoadMore();
        } else if (distance == 0) {
            mStatus = XZRefreshStatus.NORMAL;
        }
    }

    private void onReleaseCallback() {
        if (mStatus == null)
            return;
        switch (mStatus) {
            case PULL_DOWN_TO_REFRESH:
                backToNormal();
                break;
            case RELEASE_TO_REFRESH:
                changeToRefreshing();
                break;
            case PULL_UP_TO_LOAD_MORE:
                backToNormal();
                break;
            case RELEASE_TO_LOAD_MORE:
                changeToLoading();
                break;
        }
    }

    public void beginRefresh() {
        changeToRefreshing();
    }

    public void beginLoading() {
        changeToLoading();
    }

    private void backToNormal() {
        mStatus = XZRefreshStatus.NORMAL;
        if (mDragHelper.smoothSlideViewTo(mContentView, left, top)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void changeToRefreshing() {
        mStatus = XZRefreshStatus.REFRESHING;

        if (mDragHelper.smoothSlideViewTo(mContentView, left, top + mReleaseRefreshHeight)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        mRefreshHolder.changeToRefreshing();
        mRefreshCallback.onRefresh();
    }

    private void changeToLoading() {
        if (isLastPage) {
            backToNormal();
            return;
        }
        mStatus = XZRefreshStatus.LOADING;
        if (mDragHelper.smoothSlideViewTo(mContentView, left, top - mReleaseLoadHeight)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        mRefreshHolder.changeToLoadingMore();
        mRefreshCallback.onLoadMore();
    }

    public void endRefreshing() {
        backToNormal();
    }

    public void endLoading() {
        backToNormal();
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public void setLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
        mRefreshHolder.setFooterLastPage();
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    public void setRefreshCallback(XZRefreshCallback callback) {
        this.mRefreshCallback = callback;
    }

    public void setmRefreshMoveCallbacn(XZRefreshMoveCallback mRefreshMoveCallbacn) {
        this.mRefreshMoveCallbacn = mRefreshMoveCallbacn;
    }

    public class DragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mContentView;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < 0 && !XZJudgeViewStateUtil.isContentViewToBottom(mContentView)
                    || top > 0 && !XZJudgeViewStateUtil.isContentViewToTop(mContentView))
                top = 0;
            if (top > mMaxPullDown) {
                top = mMaxPullDown;
            }
            if (top < -mMaxPullUp)
                top = -mMaxPullUp;
            top = child.getTop() + (top - child.getTop())/2;
            return top;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            onContentMove(top);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            onReleaseCallback();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 1;
        }
    }


    private void setOnScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (XZJudgeViewStateUtil.isContentViewToBottom(recyclerView)
                        && canLoadMore && !isLastPage && autoLoad
                        && mStatus != XZRefreshStatus.LOADING) {
                    beginLoading();
                }
            }
        });
    }

    private void setOnScrollListener(AbsListView listView) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount == 0)
                    return;
                if (firstVisibleItem + visibleItemCount == totalItemCount
                        && XZJudgeViewStateUtil.isContentViewToBottom(view)
                        && canLoadMore && !isLastPage && autoLoad
                        && mStatus != XZRefreshStatus.LOADING) {
                    beginLoading();
                }
            }
        });
    }
}
