package com.songbao.sxb.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.activity.home.SignUpDetailActivity;
import com.songbao.sxb.adapter.AdapterCallback;
import com.songbao.sxb.adapter.MySignUpAdapter;
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


public class MySignUpActivity extends BaseActivity {

	String TAG = MySignUpActivity.class.getSimpleName();

	@BindView(R.id.refresh_view_rv)
	PullToRefreshRecyclerView refresh_rv;

	MyRecyclerView mRecyclerView;
	MySignUpAdapter rvAdapter;
	AdapterCallback apCallback;

	private int data_total = -1; //数据总量
	private int current_Page = 1;  //当前列表加载页
	private ArrayList<ThemeEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler_view);

		initView();
	}

	private void initView() {
		setTitle(getString(R.string.mine_my_sing_up));

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
						if (data_total < 0) {
							refreshData();
						} else {
							refresh_rv.onPullDownRefreshComplete();
						}
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
					openSignUpDetailActivity(themeEn);
				}
			}
		};
		rvAdapter = new MySignUpAdapter(mContext, al_show, apCallback);
		mRecyclerView.setAdapter(rvAdapter);
	}

	private void updateListData() {
		if (rvAdapter != null) {
			rvAdapter.updateData(al_show);
		}
	}

	/**
	 * 跳转至活动详情页面
	 * @param data
	 */
	private void openSignUpDetailActivity(ThemeEntity data) {
		if (data == null) return;
		Intent intent = new Intent(mContext, SignUpDetailActivity.class);
		intent.putExtra(AppConfig.PAGE_TYPE, 1);
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
		current_Page = 1;
		loadServerData();
	}

	/**
	 * 翻页加载
	 */
	private void loadMoreData() {
		loadServerData();
		/*al_show.clear();
		al_show.addAll(getDemoData());
		updateListData();*/
	}

	/**
	 * 加载数据
	 */
	private void loadServerData() {
		HashMap<String, String> map = new HashMap<>();
		map.put("page", String.valueOf(current_Page));
		map.put("size", AppConfig.LOAD_SIZE);
		loadSVData(AppConfig.URL_USER_ACTIVITY, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_ACTIVITY);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_USER_ACTIVITY:
					baseEn = JsonUtils.getMyThemeList(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal(); //加载更多数据控制符
						List<ThemeEntity> lists = baseEn.getLists();
						if (lists.size() > 0) {
							if (current_Page == 1) {
								al_show.clear();
								am_show.clear();
							}
							List<BaseEntity> newLists = addNewEntity(lists, al_show, am_show);
							if (newLists != null) {
								current_Page++;
								addNewShowLists(newLists);
							}
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

	private void addNewShowLists(List<BaseEntity> showLists) {
		al_show.clear();
		for (int i = 0; i < showLists.size(); i++) {
			al_show.add((ThemeEntity) showLists.get(i));
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
		refresh_rv.onPullDownRefreshComplete();
		refresh_rv.onPullUpRefreshComplete();
	}

	/**
	 * 构建Demo数据
	 */
	private List<ThemeEntity> getDemoData() {
		List<ThemeEntity> mainLists = new ArrayList<>();

		ThemeEntity chEn_1 = new ThemeEntity();
		ThemeEntity chEn_2 = new ThemeEntity();
		ThemeEntity chEn_3 = new ThemeEntity();
		ThemeEntity chEn_4 = new ThemeEntity();
		ThemeEntity chEn_5 = new ThemeEntity();

		chEn_1.setAddTime("2019-10-28 09:28:28");
		chEn_1.setPicUrl("");
		chEn_1.setTitle("我的活动标题1");
		chEn_1.setStartTime("2019-11-08 09:28:28");
		chEn_1.setAddress("松堡旗舰店");
		chEn_1.setStatus(0);
		mainLists.add(chEn_1);
		chEn_2.setAddTime("2019-10-26 09:26:26");
		chEn_2.setPicUrl("");
		chEn_2.setTitle("我的活动标题2");
		chEn_2.setStartTime("2019-11-02 09:28:28");
		chEn_2.setAddress("松堡旗舰店");
		chEn_2.setStatus(1);
		mainLists.add(chEn_2);
		chEn_3.setAddTime("2019-10-23 09:23:23");
		chEn_3.setPicUrl("");
		chEn_3.setTitle("我的活动标题3");
		chEn_3.setStartTime("2019-10-28 09:28:28");
		chEn_3.setAddress("松堡旗舰店");
		chEn_3.setStatus(2);
		mainLists.add(chEn_3);
		chEn_4.setAddTime("2019-10-20 09:20:20");
		chEn_4.setPicUrl("");
		chEn_4.setTitle("我的活动标题4");
		chEn_4.setStartTime("2019-10-28 09:28:28");
		chEn_4.setAddress("松堡旗舰店");
		chEn_4.setStatus(1);
		mainLists.add(chEn_4);
		chEn_5.setAddTime("2019-10-18 09:18:18");
		chEn_5.setPicUrl("");
		chEn_5.setTitle("我的活动标题5");
		chEn_5.setStartTime("2019-10-20 09:28:28");
		chEn_5.setAddress("松堡旗舰店");
		chEn_5.setStatus(2);
		mainLists.add(chEn_5);

		return mainLists;
	}

}
