package com.songbao.sxb.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {

	protected Context context;
	protected List<T> mDataList;
	protected List<Integer> resLayout;
	protected AdapterCallback apCallback;

	public BaseRecyclerAdapter(Context context , int resLayout){
		this.context = context;
		this.resLayout = new ArrayList<>();
		this.resLayout.add(resLayout);
		this.mDataList = new ArrayList<>();
	}

	/**
	 * 需要重写getItemViewType方法自行给布局分类
	 */
	public BaseRecyclerAdapter(Context context , List<Integer> resLayouts){
		this.context = context;
		this.resLayout = resLayouts;
		this.mDataList = new ArrayList<>();
	}

	/**
	 * 添加数据
	 */
	public void addData(List<T> data){
		this.mDataList.addAll(data);
		this.notifyDataSetChanged();
	}

	/**
	 * 刷新数据
	 */
	public void updateData(List<T> data){
		this.mDataList.clear();
		this.mDataList.addAll(data);
		this.notifyDataSetChanged();
	}

	/**
	 * 设置回调
	 */
	public void addCallback(AdapterCallback callback) {
		this.apCallback = callback;
	}

	@NonNull
	@Override
	public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(resLayout.get(viewType), parent, false);
		return new BaseRecyclerHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {
		// 子类实现数据绑定
		bindData(holder, position);
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}

	/**
	 * 绑定数据
	 * @param holder  具体的viewHolder
	 * @param position  对应的索引
	 */
	protected abstract void bindData(BaseRecyclerHolder holder, int position);

}
