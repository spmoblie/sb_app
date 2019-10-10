package com.sbwg.sxb.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.adapter.AdapterCallback;
import com.sbwg.sxb.adapter.MyReserveAdapter;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.MessageEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshBase;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.sbwg.sxb.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class MyReserveActivity extends BaseActivity implements OnClickListener {

	String TAG = MyReserveActivity.class.getSimpleName();

	@BindView(R.id.refresh_view_rv)
	PullToRefreshRecyclerView refresh_rv;

	MyRecyclerView mRecyclerView;
	MyReserveAdapter rvAdapter;
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
		setTitle(getString(R.string.mine_my_reserve));

		initRecyclerView();
		loadMoreData();
	}

	private void initRecyclerView() {
		refresh_rv.setPullRefreshEnabled(true); //下拉刷新
		refresh_rv.setPullLoadEnabled(true); //上拉加载
		refresh_rv.setScrollLoadEnabled(false); //底部翻页
		refresh_rv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyRecyclerView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<MyRecyclerView> refreshView) {
				// 下拉刷新
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (data_total < 0) {
							resetData();
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

			}
		};
		rvAdapter = new MyReserveAdapter(mContext, al_show, apCallback);
		mRecyclerView.setAdapter(rvAdapter);
	}

	private void updateListData() {
		if (rvAdapter != null) {
			rvAdapter.updateData(al_show);
		}
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
	 * 重置数据
	 */
	private void resetData() {
		current_Page = 1;
		loadMoreData();
	}

	/**
	 * 翻页加载
	 */
	private void loadMoreData() {
		loadServerData();
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
					baseEn = JsonUtils.getMyReserveList(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal(); //加载更多数据控制符
						List<MessageEntity> lists = baseEn.getLists();
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
					} else {
						handleErrorCode(baseEn);
						if (baseEn.getErrno() == AppConfig.ERROR_CODE_TIMEOUT) {
							finish();
						}
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

}
