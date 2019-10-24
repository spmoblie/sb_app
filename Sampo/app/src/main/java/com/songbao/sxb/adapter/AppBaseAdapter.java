package com.songbao.sxb.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class AppBaseAdapter<T> extends BaseAdapter {

	protected Object tag;
	protected List<T> mDataList;
	protected AdapterCallback apCallback;
	protected WeakReference<Context> weakContext;

	public AppBaseAdapter(Context mContext) {
		weakContext = new WeakReference<>(mContext);
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public void setCallback(AdapterCallback callback) {
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

	public void setDataList(List<T> mDataList) {
		this.mDataList = mDataList;
		this.notifyDataSetChanged();
	}

	public void addDataList(List<T> mDataList) {
		if (this.mDataList == null) {
			setDataList(mDataList);
		} else {
			this.mDataList.addAll(mDataList);
			this.notifyDataSetChanged();
		}
	}

	public List<T> getDataList() {
		return mDataList;
	}

	public void clearDataList() {
		if (mDataList != null)
			mDataList.clear();
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	public static class SuperHolder {
		public Object obj;
	}

}
