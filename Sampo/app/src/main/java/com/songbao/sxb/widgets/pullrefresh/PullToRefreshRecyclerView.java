package com.songbao.sxb.widgets.pullrefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.View;

import com.songbao.sxb.widgets.pullrefresh.ILoadingLayout.State;
import com.songbao.sxb.widgets.recycler.MyRecyclerView;

/**
 * 这个类实现了ListView下拉刷新，上加载更多和滑到底部自动加载
 */
public class PullToRefreshRecyclerView extends PullToRefreshBase<MyRecyclerView> {
    
    /**RecyclerView*/
    private MyRecyclerView mRecyclerView;
    /**用于滑到底部自动加载的Footer*/
    private LoadingLayout mLoadMoreFooterLayout;
    /**滚动的监听器*/
    private OnScrollListener mScrollListener;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public PullToRefreshRecyclerView(Context context) {
        this(context, null);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        setPullLoadEnabled(false);
    }

    @Override
    protected MyRecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        MyRecyclerView recyclerView = new MyRecyclerView(context, attrs);
        mRecyclerView = recyclerView;
        if (mScrollListener == null) {
            recyclerView.addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (isScrollLoadEnabled() && hasMoreData()) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                            if (isReadyForPullUp()) {
                                startLoading();
                            }
                        }
                    }
                    if (null != mScrollListener) {
                        mScrollListener.onScrollStateChanged(recyclerView, newState);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (null != mScrollListener) {
                        mScrollListener.onScrolled(recyclerView, dx, dy);
                    }
                }
            });
        } else {
            recyclerView.addOnScrollListener(mScrollListener);
        }
        return recyclerView;
    }
    
    /**
     * 设置是否有更多数据的标志
     * 
     * @param hasMoreData true表示还有更多的数据，false表示没有更多数据了
     */
    public void setHasMoreData(boolean hasMoreData) {
        State state = State.NONE;
        if (!hasMoreData) {
            state = State.NO_MORE_DATA;
        }
        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(state);
        }

        LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
        if (null != footerLoadingLayout) {
            footerLoadingLayout.setState(state);
        }
    }

    /**
     * 设置滑动的监听器
     * 
     * @param l 监听器
     */
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }
    
    @Override
    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    @Override
    protected void startLoading() {
        super.startLoading();
        
        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(State.REFRESHING);
        }
    }
    
    @Override
    public void onPullUpRefreshComplete() {
        super.onPullUpRefreshComplete();
        
        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(State.RESET);
        }
    }
    
    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        super.setScrollLoadEnabled(scrollLoadEnabled);

        if (scrollLoadEnabled) {
            // 设置Footer
            if (null == mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout = new FooterLoadingLayout(getContext());
            }
            
            if (null == mLoadMoreFooterLayout.getParent()) {
                mRecyclerView.addFooterView(mLoadMoreFooterLayout);
            }
            mLoadMoreFooterLayout.show(true);
        } else {
            if (null != mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout.show(false);
            }
        }
    }
    
    @Override
    public LoadingLayout getFooterLoadingLayout() {
        if (isScrollLoadEnabled()) {
            return mLoadMoreFooterLayout;
        }

        return super.getFooterLoadingLayout();
    }

    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new RotateLoadingLayout(context);
    }
    
    /**
     * 表示是否还有更多数据
     * 
     * @return true表示还有更多数据
     */
    private boolean hasMoreData() {
        if ((null != mLoadMoreFooterLayout) && (mLoadMoreFooterLayout.getState() == State.NO_MORE_DATA)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 判断第一个child是否完全显示出来
     * 
     * @return true完全显示出来，否则false
     */
    private boolean isFirstItemVisible() {
        final RecyclerView.Adapter adapter = mRecyclerView.getAdapter();

        if (null == adapter) {
            return true;
        }

        int mostTop = (mRecyclerView.getChildCount() > 0) ? mRecyclerView.getChildAt(0).getTop() : 0;
        if (mostTop >= 0) {
            return true;
        }

        return false;
    }

    /**
     * 判断最后一个child是否完全显示出来
     * 
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible() {
        final RecyclerView.Adapter adapter = mRecyclerView.getAdapter();

        if (null == adapter) {
            return true;
        }

        final int lastItemPosition = adapter.getItemCount() - 1;
        final int lastVisiblePosition = mRecyclerView.getLastVisiblePosition();

        /**
         * This check should really just be: lastVisiblePosition == lastItemPosition, but ListView
         * internally uses a FooterView which messes the positions up. For me we'll just subtract
         * one to account for it and rely on the inner condition which checks getBottom().
         */
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - mRecyclerView.getFirstVisiblePosition();
            final int childCount = mRecyclerView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = mRecyclerView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= mRecyclerView.getBottom();
            }
        }

        return false;
    }
}
