package com.sbwg.sxb.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;


public class MyViewPagerAdapter extends PagerAdapter{

    private boolean isLoop;
    private ViewPager viewPager;
    private ArrayList<ImageView> mViewList;

    public MyViewPagerAdapter(ViewPager viewPager, ArrayList<ImageView> mViewList) {
        this.viewPager = viewPager;
        this.mViewList = mViewList;
        this.isLoop = mViewList.size() > 3 ? true : false;
    }

    /*public void resetData(int position) {
        if (mViewList.size() > 1) {
            int leftPos = (mViewList.size() + position - 1) % mViewList.size();
            int rightPos = (position + 1) % mViewList.size();
            mDataList.clear();
            mDataList.add(mViewList.get(leftPos));
            mDataList.add(mViewList.get(position));
            mDataList.add(mViewList.get(rightPos));
        } else if (mViewList.size() == 1){
            mDataList.add(mViewList.get(position));
        }
    }

    public void updateDatas(ArrayList<ImageView> mViewList) {
        this.mViewList = mViewList;
        if (mDataList.size() > 1) {
            resetData(mViewList.indexOf(mDataList.get(viewPager.getCurrentItem())));
            notifyDataSetChanged();
        }
    }*/

    @Override
    public int getCount() {
        if (isLoop) {
            return Integer.MAX_VALUE;
        } else {
            return mViewList.size();
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    // 创建
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View layout;
        if (isLoop) {
            layout = mViewList.get(position % mViewList.size());
        } else {
            layout = mViewList.get(position);
        }
        container.addView(layout);
        return layout;
    }

    // 销毁
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View layout;
        if (isLoop) {
            layout = mViewList.get(position % mViewList.size());
        } else {
            layout = mViewList.get(position);
        }
        container.removeView(layout);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (mViewList.contains(object)) {
            return mViewList.indexOf(object);
        } else {
            return POSITION_NONE;
        }
    }
}
