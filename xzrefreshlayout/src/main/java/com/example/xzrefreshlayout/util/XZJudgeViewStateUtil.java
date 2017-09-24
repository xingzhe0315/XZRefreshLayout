package com.example.xzrefreshlayout.util;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import java.lang.reflect.Field;

/**
 * Created by shiwu on 2017/7/29.
 */

public class XZJudgeViewStateUtil {
    public static boolean isContentViewToTop(View contentView) {
        if (contentView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) contentView;
            return scrollView.getScrollY() == 0;
        } else if (contentView instanceof WebView) {
            WebView webView = (WebView) contentView;
            return webView.getScrollY() == 0;
        } else if (contentView instanceof AbsListView) {
            return isListViewReachTopEdge((AbsListView) contentView);
        } else if (contentView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) contentView;
            return judgeRecyclerViewToTop(recyclerView);
        } else if (contentView instanceof NestedScrollView) {
            return ((NestedScrollView) contentView).getScrollY() == 0;
        }
            return false;
    }

    public static boolean isContentViewToBottom(View contentView) {
        if (contentView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) contentView;
            int scrollContentHeight = scrollView.getScrollY() + scrollView.getMeasuredHeight() - scrollView.getPaddingTop() - scrollView.getPaddingBottom();
            int realContentHeight = scrollView.getChildAt(0).getMeasuredHeight();
            if (scrollContentHeight == realContentHeight) {
                return true;
            }
        } else if (contentView instanceof WebView) {
            WebView webView = (WebView) contentView;
            if(webView.getContentHeight()*webView.getScale()-(webView.getHeight()+webView.getScrollY())==0){
                return true;
            }
        } else if (contentView instanceof AbsListView) {
            AbsListView listView = (AbsListView) contentView;
            return isListViewReachBottomEdge(listView);
        } else if (contentView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) contentView;
            return judgeRecyclerViewToBottom(recyclerView);
        } else if (contentView instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) contentView;
            int scrollContentHeight = scrollView.getScrollY() + scrollView.getMeasuredHeight() - scrollView.getPaddingTop() - scrollView.getPaddingBottom();
            int realContentHeight = scrollView.getChildAt(0).getMeasuredHeight();
            if (scrollContentHeight == realContentHeight) {
                return true;
            }
        }
        return false;
    }

    public static boolean isListViewReachTopEdge(final AbsListView listView) {
        boolean result=false;
        if(listView.getFirstVisiblePosition()==0){
            final View topChildView = listView.getChildAt(0);
            result=topChildView.getTop()==0;
        }
        return result ;
    }

    public static boolean isListViewReachBottomEdge(final AbsListView listView) {
        boolean result=false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result= (listView.getHeight()>=bottomChildView.getBottom());
        };
        return  result;
    }

    private static boolean judgeRecyclerViewToTop(RecyclerView recyclerView){
        if(recyclerView != null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager == null) {
                return true;
            }
            if (manager.getItemCount() == 0) {
                return true;
            }

            if (manager instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) manager;

                int firstChildTop = 0;
                if (recyclerView.getChildCount() > 0) {
                    // 处理item高度超过一屏幕时的情况
                    View firstVisibleChild = recyclerView.getChildAt(0);
                    if (firstVisibleChild != null && firstVisibleChild.getMeasuredHeight() >= recyclerView.getMeasuredHeight()) {
                        if (android.os.Build.VERSION.SDK_INT < 14) {
                            return !(ViewCompat.canScrollVertically(recyclerView, -1) || recyclerView.getScrollY() > 0);
                        } else {
                            return !ViewCompat.canScrollVertically(recyclerView, -1);
                        }
                    }

                    // 如果RecyclerView的子控件数量不为0，获取第一个子控件的top

                    // 解决item的topMargin不为0时不能触发下拉刷新
                    View firstChild = recyclerView.getChildAt(0);
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) firstChild.getLayoutParams();
                    firstChildTop = firstChild.getTop() - layoutParams.topMargin - getRecyclerViewItemTopInset(layoutParams) - recyclerView.getPaddingTop();
                }

                if (layoutManager.findFirstCompletelyVisibleItemPosition() < 1 && firstChildTop == 0) {
                    return true;
                }
            } else if (manager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;

                int[] out = layoutManager.findFirstCompletelyVisibleItemPositions(null);
                if (out[0] < 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean judgeRecyclerViewToBottom(RecyclerView recyclerView){
        if (recyclerView != null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager == null || manager.getItemCount() == 0) {
                return false;
            }

            if (manager instanceof LinearLayoutManager) {
                // 处理item高度超过一屏幕时的情况
                View lastVisibleChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                if (lastVisibleChild != null && lastVisibleChild.getMeasuredHeight() >= recyclerView.getMeasuredHeight()) {
                    if (android.os.Build.VERSION.SDK_INT < 14) {
                        return !(ViewCompat.canScrollVertically(recyclerView, 1) || recyclerView.getScrollY() < 0);
                    } else {
                        return !ViewCompat.canScrollVertically(recyclerView, 1);
                    }
                }

                LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
                if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                    return true;
                }
            } else if (manager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;

                int[] out = layoutManager.findLastCompletelyVisibleItemPositions(null);
                int lastPosition = layoutManager.getItemCount() - 1;
                for (int position : out) {
                    if (position == lastPosition) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 通过反射获取RecyclerView的item的topInset
     *
     * @param layoutParams
     * @return
     */
    private static int getRecyclerViewItemTopInset(RecyclerView.LayoutParams layoutParams) {
        try {
            Field field = RecyclerView.LayoutParams.class.getDeclaredField("mDecorInsets");
            field.setAccessible(true);
            // 开发者自定义的滚动监听器
            Rect decorInsets = (Rect) field.get(layoutParams);
            return decorInsets.top;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
