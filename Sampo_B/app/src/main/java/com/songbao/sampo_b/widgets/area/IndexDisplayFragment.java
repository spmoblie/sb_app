package com.songbao.sampo_b.widgets.area;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseFragment;
import com.songbao.sampo_b.utils.LogUtil;

import java.util.Arrays;
import java.util.List;

public class IndexDisplayFragment extends BaseFragment {

	private static final String TAG = "IndexDisplayFragment";

	private static final String[] indexs = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
	public static final long LIST_POSITION_DIALOG_DELAY_TIME = 1000;

	List<String> indexLists;
	SectionIndexerView mSection;
	ListView indexDisplay_ListView;
	View headerView;
	IndexDisplayAdapter indexAdapter;
	OnItemClickListener onItemClickListener;
	List<Pair<String, List<? extends IndexDisplay>>> dataList;
	ArrayMap<String, Integer> indexHm;
	TextView indexTextView;
	Handler mHandler;
	boolean mReady;
	boolean mShowing;
	boolean isShowRight = false;
	WindowManager mWindowManager;
	RemoveWindow mRemoveWindow = new RemoveWindow();

	Object obj;
	String mPrevString = "";

	private final class RemoveWindow implements Runnable {
		public void run() {
			removeWindow();
		}
	}

	public static IndexDisplayFragment newInstance() {
		return new IndexDisplayFragment();
	}

	public void setAdapter(IndexDisplayAdapter adapter) {
		this.indexAdapter = adapter;
	}

	public void setAdapterOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setDataList(List<Pair<String, List<? extends IndexDisplay>>> dataList) {
		this.dataList = dataList;
	}

	public void updateDataList(List<Pair<String, List<? extends IndexDisplay>>> dataList) {
		if (indexAdapter != null) {
			indexAdapter.updateData(dataList);
			indexAdapter.notifyDataSetChanged();
			indexDisplay_ListView.setAdapter(indexAdapter);
		}
	}

	public void setIndexHashMap(ArrayMap<String, Integer> am_index) {
		this.indexHm = am_index;
		isShowRight = true;
	}

	public void refreshListView() {
		if (indexDisplay_ListView != null && indexAdapter != null) {
			indexDisplay_ListView.setAdapter(indexAdapter);
			indexAdapter.notifyDataSetChanged();
		}
	}

	public void setListViewHeader(View view) {
		headerView = view;
	}

	public void removeWindow() {
		if (mShowing) {
			mShowing = false;
			mPrevString = "";
			indexTextView.setVisibility(View.INVISIBLE);
		}
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Object getObject() {
		return obj;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_index_display, container, false);
		indexDisplay_ListView = view.findViewById(R.id.indexDisplay_ListView);
		if (onItemClickListener != null) {
			indexDisplay_ListView.setOnItemClickListener(onItemClickListener);
		}
		mSection = view.findViewById(R.id.indexDisplay_section_indexer);
		initSection(); //初始化索引View
		indexTextView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.item_list_position, null);
		indexTextView.setVisibility(View.INVISIBLE);
		return view;
	}

	private void initSection() {
		indexLists = Arrays.asList(indexs);
		ContentAdapter adapter = new ContentAdapter(getActivity(), android.R.layout.simple_list_item_1, indexLists);
		mSection.setSectionIndexer(adapter);
		mSection.setSectionListener(new SectionIndexerView.SectionIndexerListener() {
			@Override
			public void onSectionChange(int status, int position, Object newSection) {
				if (status == SectionIndexerView.SectionIndexerListener.STATE_UP) {
					mSection.setBackgroundColor(getResources().getColor(R.color.ui_bg_color_percent_10));
				} else {
					mSection.setBackgroundColor(getResources().getColor(R.color.ui_bg_color_percent_05));
				}
				String keyStr = "";
				if (position >= 0 && position < indexs.length) {
					keyStr = indexs[position];
				}
				if (indexHm != null && indexHm.containsKey(keyStr)) {
					int selectId = indexHm.get(keyStr);
					indexDisplay_ListView.setSelection(selectId);
				}
				if (!mShowing) {
					mShowing = true;
					indexTextView.setVisibility(View.VISIBLE);
				}
				mPrevString = keyStr;
				indexTextView.setText(keyStr);
				mHandler.removeCallbacks(mRemoveWindow);
				mHandler.postDelayed(mRemoveWindow, LIST_POSITION_DIALOG_DELAY_TIME);
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LogUtil.i(TAG, "onActivityCreated() called");
		if (indexAdapter == null) {
			return;
		}
		if (onItemClickListener != null) {
			indexDisplay_ListView.setOnItemClickListener(onItemClickListener);
		}
		if (headerView != null) {
			indexDisplay_ListView.addHeaderView(headerView);
		}
		if (dataList != null) {
			indexAdapter.addData(dataList);
			indexDisplay_ListView.setAdapter(indexAdapter);
		}

		mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		mHandler = new Handler();
		mHandler.post(new Runnable() {
			public void run() {
				mReady = true;
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_APPLICATION,
						WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);
				mWindowManager.addView(indexTextView, lp);
			}
		});

		indexDisplay_ListView.setOnScrollListener(
				new ListView.OnScrollListener() {

					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
						LogUtil.i(TAG, "firstVisibleItem=" + firstVisibleItem);
						if (!mReady)
							return;
						if (indexAdapter == null)
							return;
						// String groupTitle = dataList.get(firstVisibleItem).getRouteNo().substring(0,1);
						// String groupTitle = adapter.getFirstChar(firstVisibleItem);
						String groupTitle = indexAdapter.getIndexChar(firstVisibleItem);
						if (groupTitle != null && !groupTitle.equals("")) {
							mSection.changeStatus(indexLists.indexOf(groupTitle)); //更新索引
							if (isShowRight) return; //有索引时滑动不弹幕
							if (!mShowing && !groupTitle.equalsIgnoreCase(mPrevString)) {
								mShowing = true;
								indexTextView.setVisibility(View.VISIBLE);
							}
							mPrevString = groupTitle;
							indexTextView.setText(groupTitle);
							mHandler.removeCallbacks(mRemoveWindow);
							mHandler.postDelayed(mRemoveWindow, LIST_POSITION_DIALOG_DELAY_TIME);
						}
					}

					public void onScrollStateChanged(AbsListView view, int scrollState) {
						
					}

				});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isShowRight) {
			mSection.setVisibility(View.VISIBLE);
		}else {
			mSection.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mWindowManager != null && indexTextView != null)
			mWindowManager.removeView(indexTextView);
		obj = null;
	}

}
