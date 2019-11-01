package com.songbao.sxb.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.widget.GridView;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.activity.common.ViewPagerActivity;
import com.songbao.sxb.adapter.AdapterCallback;
import com.songbao.sxb.adapter.MyDesignAdapter;
import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.entity.DesignEntity;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.JsonUtils;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.retrofit.HttpRequests;
import com.songbao.sxb.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sxb.widgets.pullrefresh.PullToRefreshGridView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class MyDesignActivity extends BaseActivity {

	String TAG = MyDesignActivity.class.getSimpleName();

	@BindView(R.id.refresh_view_gv)
	PullToRefreshGridView refresh_gv;

	MyDesignAdapter gvAdapter;

	private int data_total = -1; //数据总量
	private int load_page = 1; //加载页数
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private boolean isLoadOk = true; //加载控制
	private ArrayList<String> urlLists = new ArrayList<>();
	private ArrayList<DesignEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);

		initView();
	}

	private void initView() {
		setTitle(getString(R.string.mine_my_design));

		initRecyclerView();
		loadMoreData();
	}

	private void initRecyclerView() {
		refresh_gv.setPullRefreshEnabled(true); //下拉刷新
		refresh_gv.setPullLoadEnabled(true); //上拉加载
		refresh_gv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				// 下拉刷新
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						refreshData();
					}
				}, AppConfig.LOADING_TIME);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				// 加载更多
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (!isStopLoadMore(al_show.size(), data_total, 0)) {
							loadMoreData();
						} else {
							refresh_gv.setHasMoreData(false);
							refresh_gv.onPullUpRefreshComplete();
						}
					}
				}, AppConfig.LOADING_TIME);
			}
		});
		GridView mGridView = refresh_gv.getRefreshableView();
		mGridView.setNumColumns(2);
		mGridView.setHorizontalSpacing(CommonTools.dpToPx(mContext, 6));
		mGridView.setVerticalSpacing(CommonTools.dpToPx(mContext, 6));
		mGridView.setVerticalScrollBarEnabled(false);

		// 配置适配器
		gvAdapter = new MyDesignAdapter(mContext);
		gvAdapter.addData(al_show);
		gvAdapter.addCallback(new AdapterCallback() {
			@Override
			public void setOnClick(Object data, int position, int type) {
				Intent intent = new Intent(mContext, ViewPagerActivity.class);
				intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_URLS, urlLists);
				intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_INDEX, position);
				startActivity(intent);
			}
		});
		mGridView.setAdapter(gvAdapter);
	}

	private void updateListData() {
		gvAdapter.updateData(al_show);

		urlLists.clear();
		for (int i = 0; i < al_show.size(); i++) {
			urlLists.add(al_show.get(i).getImgUrl());
		}
	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		super.onResume();
	}
	
	@Override
	protected void onPause() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 *下拉刷新
	 */
	private void refreshData() {
		load_type = 0;
		loadServerData();
	}

	/**
	 * 翻页加载
	 */
	private void loadMoreData() {
		load_type = 1;
		//loadServerData();
		loadDemoData();
	}

	/**
	 * 加载数据
	 */
	private void loadServerData() {
		if (!isLoadOk) return; //加载频率控制
		isLoadOk = false;
		String page = String.valueOf(load_page);
		if (load_type == 0) {
			page = "1";
		}
		HashMap<String, String> map = new HashMap<>();
		map.put("page", page);
		map.put("size", AppConfig.LOAD_SIZE);
		loadSVData(AppConfig.URL_DESIGN_ALL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_DESIGN_ALL);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_DESIGN_ALL:
					baseEn = JsonUtils.getDesignData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal();
						List<DesignEntity> lists = filterData(baseEn.getLists(), am_show);
						if (lists != null && lists.size() > 0) {
							if (load_type == 0) {
								//下拉
								LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
								lists.addAll(al_show);
								al_show.clear();
							}else {
								//翻页
								LogUtil.i(LogUtil.LOG_HTTP, TAG + " 翻页数据 —> size = " + lists.size());
								load_page++;
							}
							al_show.addAll(lists);
							updateListData();
						}
					} else if (baseEn.getErrno() == AppConfig.ERROR_CODE_TIMEOUT) {
						handleTimeOut();
						finish();
					} else {
						handleErrorCode(baseEn);
					}
					break;
			}
		} catch (Exception e) {
			loadFailHandle();
			ExceptionUtil.handle(e);
		}
	}

	@Override
	protected void loadFailHandle() {
		super.loadFailHandle();
		handleErrorCode(null);
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		refresh_gv.onPullUpRefreshComplete();
		refresh_gv.onPullDownRefreshComplete();
	}

	/**
	 * 构建Demo数据
	 */
	private void loadDemoData() {
		al_show.clear();

		DesignEntity chEn_1 = new DesignEntity();
		DesignEntity chEn_2 = new DesignEntity();
		DesignEntity chEn_3 = new DesignEntity();
		DesignEntity chEn_4 = new DesignEntity();
		DesignEntity chEn_5 = new DesignEntity();

		chEn_1.setImgUrl(AppConfig.IMAGE_URL + "design_001.png");
		al_show.add(chEn_1);
		chEn_2.setImgUrl(AppConfig.IMAGE_URL + "design_002.png");
		al_show.add(chEn_2);
		chEn_3.setImgUrl(AppConfig.IMAGE_URL + "design_003.png");
		al_show.add(chEn_3);
		chEn_4.setImgUrl(AppConfig.IMAGE_URL + "design_004.png");
		al_show.add(chEn_4);
		chEn_5.setImgUrl(AppConfig.IMAGE_URL + "design_005.png");
		al_show.add(chEn_5);

		updateListData();
		stopAnimation();
	}

}
