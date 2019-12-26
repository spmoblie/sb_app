package com.songbao.sampo_b.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 自定义监听滚动的ScrollView
 */
public class ObservableScrollView extends ScrollView {
  
    private ScrollViewListener scrollViewListener = null;  
  
    public ObservableScrollView(Context context) {
        super(context);  
    }  
  
    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);  
    }  
  
    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);  
    }  
  
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {  
        this.scrollViewListener = scrollViewListener;  
    }  
  
    @Override
    protected void onScrollChanged(int new_x, int new_y, int old_x, int old_y) {
        super.onScrollChanged(new_x, new_y, old_x, old_y);
        if (scrollViewListener != null) {  
            scrollViewListener.onScrollChanged(this, new_x, new_y, old_x, old_y);
        }  
    }  
    
    public interface ScrollViewListener {  
    	  
        void onScrollChanged(ObservableScrollView scrollView, int new_x, int new_y, int old_x, int old_y);
      
    } 
  
} 
