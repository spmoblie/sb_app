package com.songbao.sampo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class AppBaseAdapter<T> extends BaseAdapter {

	protected Context context;
	protected List<T> mDataList;
	protected AdapterCallback apCallback;
	protected WeakReference<Context> weakContext;

	public AppBaseAdapter(Context context) {
		this.context = context;
		this.mDataList = new ArrayList<>();
		this.weakContext = new WeakReference<>(context);
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

	@Override
	public int getCount() {
		if (mDataList != null)
			return mDataList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mDataList != null && mDataList.size() > 0)
			return mDataList.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	public static class SuperHolder {
		public Object obj;
	}

}
