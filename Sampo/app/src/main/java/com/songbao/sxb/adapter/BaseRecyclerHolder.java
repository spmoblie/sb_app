package com.songbao.sxb.adapter;

import android.support.annotation.IdRes;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private SparseArrayCompat<View> mViews;

    public BaseRecyclerHolder(View itemView) {
        super(itemView);
        mViews = new SparseArrayCompat<>();
    }

    public <V extends View> V getView(@IdRes int res){
        View v = mViews.get(res);
        if (v == null){
            v = itemView.findViewById(res);
            mViews.put(res,v);
        }
        return (V)v;
    }

}
