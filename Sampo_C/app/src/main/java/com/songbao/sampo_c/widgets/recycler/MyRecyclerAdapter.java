package com.songbao.sampo_c.widgets.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter {

    private final int HEADER = 1;
    private final int FOOTER = 2;
    private ArrayList<View> mHeadView;
    private ArrayList<View> mFootView;
    private RecyclerView.Adapter mAdapter;

    public MyRecyclerAdapter(ArrayList<View> HeadView, ArrayList<View> FootView, RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        if (HeadView == null) {
            mHeadView = new ArrayList<>();
        } else {
            mHeadView = HeadView;
        }
        if (FootView == null) {
            mFootView = new ArrayList<>();
        } else {
            mFootView = FootView;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getHeadCount() > 0 && viewType == HEADER) {
            //头部，修改new HeadViewHolder的方法，有几个头，就需要几个HeadViewHolder
            return new HeadViewHolder(mHeadView.get(0));
        } else if (getFootCount() > 0 && viewType == FOOTER) {
            //脚部，修改new FootViewHolder的方法，有几个脚，就需要几个FootViewHolder
            return new FootViewHolder(mFootView.get(0));
        }
        //body部分，暴露出去操作
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        int headcount = getHeadCount();
        //返回头部
        if (position < headcount) {
            //返回头部类型
            return HEADER;
        }

        //body类型
        if (mAdapter != null) {
            int midPosition = position - headcount;
            int itemCount = mAdapter.getItemCount();
            if (midPosition < itemCount) {
                //返回type不要写死了，body的类型可能不一致
                return mAdapter.getItemViewType(midPosition);
            }
        }

        //Footer类型
        return FOOTER;

    }


    /**
     * 数据绑定，只做body部分的绑定
     * 头和脚的数据绑定请在外部绑定
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //头部数据绑定
        int headcount = getHeadCount();
        if (position < headcount) {
            //外部绑定
            return;
        }

        //body数据绑定
        final int midPosition = position - headcount;
        int itemCount;
        if (mAdapter != null) {
            itemCount = mAdapter.getItemCount();
            if (midPosition < itemCount) {
                //暴露出去自由操作,传入的是调整后的位置，而不是算上头脚的位置
                mAdapter.onBindViewHolder(holder, midPosition);
                return;
            }
        }

        //脚部数据绑定
    }

    @Override
    public int getItemCount() {
        //身体部分不为空
        if (mAdapter != null) {
            return getHeadCount() + getFootCount() + mAdapter.getItemCount();
        } else {
            //只有头和脚的情况下
            return getHeadCount() + getFootCount();
        }
    }


    private int getFootCount() {
        return mFootView.size();
    }

    public int getHeadCount() {
        return mHeadView.size();
    }


    class HeadViewHolder extends RecyclerView.ViewHolder {

        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }

}