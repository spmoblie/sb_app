package com.songbao.sxb.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.adapter.AdapterCallback;
import com.songbao.sxb.adapter.ThemeListAdapter;
import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.entity.ThemeEntity;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.JsonUtils;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.retrofit.HttpRequests;
import com.songbao.sxb.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sxb.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sxb.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class ReserveListActivity extends BaseActivity implements OnClickListener {

	String TAG = ReserveListActivity.class.getSimpleName();

	@BindView(R.id.refresh_view_rv)
	PullToRefreshRecyclerView refresh_rv;

	MyRecyclerView mRecyclerView;
	ThemeListAdapter rvAdapter;
	AdapterCallback apCallback;

	private int data_total = -1; //数据总量
	private int load_page = 1; //加载页数
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private boolean isLoadOk = true; //加载控制
	private ArrayList<ThemeEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler_view);

		initView();
	}

	private void initView() {
		setTitle(getString(R.string.main_head_reserve));

		initRecyclerView();
		loadMoreData();
	}

	private void initRecyclerView() {
		refresh_rv.setPullRefreshEnabled(true); //下拉刷新
		refresh_rv.setPullLoadEnabled(true); //上拉加载
		refresh_rv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyRecyclerView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<MyRecyclerView> refreshView) {
				// 下拉刷新
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						refreshData();
					}
				}, AppConfig.LOADING_TIME);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<MyRecyclerView> refreshView) {
				// 加载更多
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (!isStopLoadMore(al_show.size(), data_total, 0)) {
							loadMoreData();
						} else {
							refresh_rv.onPullUpRefreshComplete();
							refresh_rv.setHasMoreData(false);
						}
					}
				}, AppConfig.LOADING_TIME);
			}
		});

		// 创建布局管理器
		LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		// 设置布局管理器
		mRecyclerView = refresh_rv.getRefreshableView();
		mRecyclerView.setLayoutManager(layoutManager);

		// 配置适配器
		apCallback = new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_show.size()) return;
				ThemeEntity themeEn = al_show.get(position);
				if (themeEn != null) {
					openDetailActivity(themeEn);
				}
			}
		};
		rvAdapter = new ThemeListAdapter(mContext, al_show, apCallback);
		mRecyclerView.setAdapter(rvAdapter);
	}

	private void updateListData() {
		if (rvAdapter != null) {
			rvAdapter.updateData(al_show);
		}
	}

	/**
	 * 跳转至详情页面
	 * @param data
	 */
	private void openDetailActivity(ThemeEntity data) {
		if (data == null) return;
		Intent intent = new Intent(mContext, ReserveDetailActivity.class);
		intent.putExtra(AppConfig.PAGE_DATA, data);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feed_back_btn_submit:

			break;
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
		loadServerData();
		//loadDemoData();
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
		map.put("isReservation", "1");
		loadSVData(AppConfig.URL_HOME_LIST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_HOME_LIST);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_HOME_LIST:
					baseEn = JsonUtils.getHomeList(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal();
						List<ThemeEntity> lists = filterData(baseEn.getLists(), am_show);
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
		refresh_rv.onPullUpRefreshComplete();
		refresh_rv.onPullDownRefreshComplete();
	}

	/**
	 * 构建Demo数据
	 */
	private void loadDemoData() {
		al_show.clear();

		ThemeEntity chEn_1 = new ThemeEntity();
		ThemeEntity chEn_2 = new ThemeEntity();
		ThemeEntity chEn_3 = new ThemeEntity();

		chEn_1.setPicUrl(AppConfig.IMAGE_URL+ "items_001.png");
		chEn_1.setTitle("北欧教育 | 比NOKIA更震惊世界的芬兰品牌");
		chEn_1.setUserName("松堡王国设计部");
		chEn_1.setSeries("美学培养系列");
		chEn_1.setAddTime("2019-10-18 10:30");
		chEn_1.setThemeType(1);
		al_show.add(chEn_1);
		chEn_2.setPicUrl(AppConfig.IMAGE_URL+ "items_002.png");
		chEn_2.setTitle("全球都在追捧的北欧教育，到底有哪些秘密？");
		chEn_2.setUserName("松小堡线下运营");
		chEn_2.setSeries("益智系列");
		chEn_2.setAddTime("2019-10-10 15:30");
		chEn_2.setThemeType(1);
		al_show.add(chEn_2);
		chEn_3.setPicUrl(AppConfig.IMAGE_URL+ "items_004.png");
		chEn_3.setTitle("芬兰：北欧小国的大教育观");
		chEn_3.setUserName("Sampo");
		chEn_3.setSeries("益智系列");
		chEn_3.setAddTime("2019-09-26 14:00");
		chEn_3.setThemeType(1);
		al_show.add(chEn_3);

		updateListData();
		stopAnimation();
	}

}
