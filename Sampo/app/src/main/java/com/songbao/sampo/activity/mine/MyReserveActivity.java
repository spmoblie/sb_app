package com.songbao.sampo.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.activity.home.ReserveDetailActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.MyReserveAdapter;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.ThemeEntity;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.songbao.sampo.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class MyReserveActivity extends BaseActivity {

	String TAG = MyReserveActivity.class.getSimpleName();

	@BindView(R.id.refresh_view_rv)
	PullToRefreshRecyclerView refresh_rv;

	MyReserveAdapter rvAdapter;

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
		setTitle(getString(R.string.mine_my_reserve));

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
		MyRecyclerView mRecyclerView = refresh_rv.getRefreshableView();
		mRecyclerView.setLayoutManager(layoutManager);

		// 配置适配器
		rvAdapter = new MyReserveAdapter(mContext, R.layout.item_list_my_reserve);
		rvAdapter.addData(al_show);
		rvAdapter.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_show.size()) return;
				ThemeEntity themeEn = al_show.get(position);
				if (themeEn != null) {
					openReserveDetailActivity(themeEn);
				}
			}
		});
		mRecyclerView.setAdapter(rvAdapter);
	}

	private void updateListData() {
		rvAdapter.updateData(al_show);
	}

	/**
	 * 跳转至预约详情页面
	 * @param data
	 */
	private void openReserveDetailActivity(ThemeEntity data) {
		if (data == null) return;
		Intent intent = new Intent(mContext, ReserveDetailActivity.class);
		intent.putExtra(AppConfig.PAGE_TYPE, 2);
		intent.putExtra(AppConfig.PAGE_DATA, data);
		startActivity(intent);
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
		loadSVData(AppConfig.URL_USER_RESERVATION, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_RESERVATION);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_USER_RESERVATION:
					baseEn = JsonUtils.getMyThemeList(jsonObject);
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
		ThemeEntity chEn_4 = new ThemeEntity();
		ThemeEntity chEn_5 = new ThemeEntity();

		chEn_1.setId(1);
		chEn_1.setAddTime("2019-10-28 09:28:28");
		chEn_1.setPicUrl("");
		chEn_1.setTitle("我的预约标题1");
		chEn_1.setReserveDate("2019-10-18");
		chEn_1.setReserveTime("09:00-10:30");
		chEn_1.setCheckValue("ALF54SD1F5F4");
		chEn_1.setWriteOffStatus(0);
		al_show.add(chEn_1);
		chEn_2.setId(2);
		chEn_2.setAddTime("2019-10-26 09:26:26");
		chEn_2.setPicUrl("");
		chEn_2.setTitle("我的预约标题2");
		chEn_2.setReserveDate("2019-10-18");
		chEn_2.setReserveTime("09:00-10:30");
		chEn_2.setCheckValue("ALF54SD1F5F4");
		chEn_2.setWriteOffStatus(1);
		al_show.add(chEn_2);
		chEn_3.setId(3);
		chEn_3.setAddTime("2019-10-23 09:23:23");
		chEn_3.setPicUrl("");
		chEn_3.setTitle("我的预约标题3");
		chEn_3.setReserveDate("2019-10-18");
		chEn_3.setReserveTime("09:00-10:30");
		chEn_3.setCheckValue("ALF54SD1F5F4");
		chEn_3.setWriteOffStatus(2);
		al_show.add(chEn_3);
		chEn_4.setId(4);
		chEn_4.setAddTime("2019-10-20 09:20:20");
		chEn_4.setPicUrl("");
		chEn_4.setTitle("我的预约标题4");
		chEn_4.setReserveDate("2019-10-18");
		chEn_4.setReserveTime("09:00-10:30");
		chEn_4.setCheckValue("ALF54SD1F5F4");
		chEn_4.setWriteOffStatus(3);
		al_show.add(chEn_4);
		chEn_5.setId(5);
		chEn_5.setAddTime("2019-10-18 09:18:18");
		chEn_5.setPicUrl("");
		chEn_5.setTitle("我的预约标题5");
		chEn_5.setReserveDate("2019-10-18");
		chEn_5.setReserveTime("09:00-10:30");
		chEn_5.setCheckValue("ALF54SD1F5F4");
		chEn_5.setWriteOffStatus(10);
		al_show.add(chEn_5);

		updateListData();
		stopAnimation();
	}

}
