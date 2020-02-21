package com.songbao.sampo_b.widgets.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 封装了WebView的下拉刷新
 */
public class PullToRefreshWebView extends PullToRefreshBase<WebView> {
    /**
     * 构造方法
     * 
     * @param context context
     */
    public PullToRefreshWebView(Context context) {
        this(context, null);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public PullToRefreshWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullToRefreshWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected WebView createRefreshableView(Context context, AttributeSet attrs) {
        return new WebView(context);
    }

    @Override
    protected boolean isReadyForPullDown() {
        return mRefreshableView.getScrollY() == 0;
    }

	@Override
    protected boolean isReadyForPullUp() {
        float exactContentHeight = (float) Math.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale());
        return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
    }
}
