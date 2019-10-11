package com.sbwg.sxb.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.widget.GridView;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.activity.common.ViewPagerActivity;
import com.sbwg.sxb.adapter.AdapterCallback;
import com.sbwg.sxb.adapter.MyDesignAdapter;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.DesignEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshBase;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshGridView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class MyDesignActivity extends BaseActivity {

	String TAG = MyDesignActivity.class.getSimpleName();

	@BindView(R.id.refresh_view_gv)
	PullToRefreshGridView refresh_gv;

	GridView mGridView;
	MyDesignAdapter gvAdapter;
	AdapterCallback apCallback;

	private int data_total = -1; //数据总量
	private int current_Page = 1;  //当前列表加载页
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
		refresh_gv.setScrollLoadEnabled(false); //底部翻页
		refresh_gv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				// 下拉刷新
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						refresh_gv.onPullDownRefreshComplete();
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
		mGridView = refresh_gv.getRefreshableView();
		mGridView.setNumColumns(2);
		mGridView.setHorizontalSpacing(CommonTools.dpToPx(mContext, 6));
		mGridView.setVerticalSpacing(CommonTools.dpToPx(mContext, 6));
		mGridView.setVerticalScrollBarEnabled(false);

		// 配置适配器
		apCallback = new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				Intent intent = new Intent(mContext, ViewPagerActivity.class);
				intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_URLS, urlLists);
				intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_INDEX, position);
				startActivity(intent);
			}
		};
		gvAdapter = new MyDesignAdapter(mContext);
		gvAdapter.setDataList(al_show);
		gvAdapter.setCallback(apCallback);
		mGridView.setAdapter(gvAdapter);
	}

	private void updateListData() {
		if (gvAdapter != null) {
			gvAdapter.setDataList(al_show);

			data_total = al_show.size();
			urlLists.clear();
			for (int i = 0; i < al_show.size(); i++) {
				urlLists.add(al_show.get(i).getImgUrl());
			}
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
		current_Page = 1;
		loadServerData();
	}

	/**
	 * 翻页加载
	 */
	private void loadMoreData() {
		//loadServerData();
		al_show.clear();
		al_show.addAll(getDemoData());
		al_show.addAll(getDemoData());
		updateListData();
	}

	/**
	 * 加载数据
	 */
	private void loadServerData() {
		HashMap<String, String> map = new HashMap<>();
		map.put("userId", userManager.getUserId());
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
						List<DesignEntity> lists = baseEn.getLists();
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
			al_show.add((DesignEntity) showLists.get(i));
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
		refresh_gv.onPullDownRefreshComplete();
		refresh_gv.onPullUpRefreshComplete();
	}

	/**
	 * 构建Demo数据
	 */
	private List<DesignEntity> getDemoData() {
		List<DesignEntity> mainLists = new ArrayList<>();
		DesignEntity chEn_1 = new DesignEntity();
		DesignEntity chEn_2 = new DesignEntity();
		DesignEntity chEn_3 = new DesignEntity();
		DesignEntity chEn_4 = new DesignEntity();
		DesignEntity chEn_5 = new DesignEntity();

		chEn_1.setImgUrl(AppConfig.IMAGE_URL + "design_001.png");
		mainLists.add(chEn_1);
		chEn_2.setImgUrl(AppConfig.IMAGE_URL + "design_002.png");
		mainLists.add(chEn_2);
		chEn_3.setImgUrl(AppConfig.IMAGE_URL + "design_003.png");
		mainLists.add(chEn_3);
		chEn_4.setImgUrl(AppConfig.IMAGE_URL + "design_004.png");
		mainLists.add(chEn_4);
		chEn_5.setImgUrl(AppConfig.IMAGE_URL + "design_005.png");
		mainLists.add(chEn_5);

		return mainLists;
	}

}
