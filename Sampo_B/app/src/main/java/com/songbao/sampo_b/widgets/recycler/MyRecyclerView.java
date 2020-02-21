package com.songbao.sampo_b.widgets.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


public class MyRecyclerView extends RecyclerView {

    //用来存储添加的headerView和footerView
    private ArrayList<View> mHeadView = new ArrayList<>();
    private ArrayList<View> mFootView = new ArrayList<>();

    //RecyclierView的适配器
    private Adapter mAdapter;


    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }



    //添加头部view，仿ListView的源码改造
    public void addHeaderView(View headerView) {
        mHeadView.add(headerView);
        if (mAdapter != null) {
            if (!(mAdapter instanceof MyRecyclerAdapter)) {
                mAdapter = new MyRecyclerAdapter(mHeadView, mFootView, mAdapter);
            }
        }
    }

    //添加脚View
    public void addFooterView(View footView) {
        mFootView.add(footView);
        if (mAdapter != null) {
            if (!(mAdapter instanceof MyRecyclerAdapter)) {
                mAdapter = new MyRecyclerAdapter(mHeadView, mFootView, mAdapter);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        //参考listview方法 setAdapter的源码，对adapter进行更换
        if (mHeadView.size() > 0 || mFootView.size() > 0)
            mAdapter = new MyRecyclerAdapter(mHeadView, mFootView, adapter);
        else {
            mAdapter = adapter;
        }
        super.setAdapter(mAdapter);
    }

    /**
     * 获取第一条展示的位置
     *
     * @return
     */
    public int getFirstVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    public int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

}
