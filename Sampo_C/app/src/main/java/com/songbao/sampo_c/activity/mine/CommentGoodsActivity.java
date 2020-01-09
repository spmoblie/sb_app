package com.songbao.sampo_c.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.CommentGRCAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CommentEntity;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_c.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class CommentGoodsActivity extends BaseActivity implements View.OnClickListener{

	String TAG = CommentGoodsActivity.class.getSimpleName();

	@BindView(R.id.comment_goods_tv_all)
	TextView tv_all;

	@BindView(R.id.comment_goods_tv_new)
	TextView tv_new;

	@BindView(R.id.comment_goods_tv_img)
	TextView tv_img;

	@BindView(R.id.refresh_view_rv)
	PullToRefreshRecyclerView refresh_rv;

	CommentGRCAdapter rvAdapter;
	MyRecyclerView mRecyclerView;

	private final int DATA_TYPE_01 = 100; //全部
	private final int DATA_TYPE_02 = 101; //最新
	private final int DATA_TYPE_03 = 102; //有图
	private int dataType = DATA_TYPE_01; //数据类型
	private int data_total = 0; //数据总量
	private int load_page = 1; //加载页数
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private boolean isLoadOk = true; //加载控制
	private String goodsCode = "";
	private ArrayList<CommentEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_goods);

		goodsCode = getIntent().getStringExtra("goodsCode");

		initView();
	}

	private void initView() {
		setTitle("精彩评价");

		tv_all.setOnClickListener(this);
		tv_new.setOnClickListener(this);
		tv_img.setOnClickListener(this);

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
		rvAdapter = new CommentGRCAdapter(mContext, R.layout.item_list_comment_goods);
		rvAdapter.addData(al_show);
		rvAdapter.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_show.size()) return;
				CommentEntity msgEn = al_show.get(position);
			}
		});
		mRecyclerView.setAdapter(rvAdapter);
	}

	private void updateListData() {
		if (al_show.size() <= 0) {
			setNullVisibility(View.VISIBLE);
		} else {
			setNullVisibility(View.GONE);
		}
		rvAdapter.updateData(al_show);
	}

	/**
	 * TextView状态切换
	 */
	private void changeViewStatus() {
		tv_all.setBackgroundResource(R.color.ui_bg_color_percent_10);
		tv_new.setBackgroundResource(R.color.ui_bg_color_percent_10);
		tv_img.setBackgroundResource(R.color.ui_bg_color_percent_10);
		tv_all.setTextColor(getResources().getColor(R.color.app_color_gray_5));
		tv_new.setTextColor(getResources().getColor(R.color.app_color_gray_5));
		tv_img.setTextColor(getResources().getColor(R.color.app_color_gray_5));
		switch (dataType) {
			case DATA_TYPE_02:
				tv_new.setBackgroundResource(R.drawable.shape_style_solid_04_08);
				tv_new.setTextColor(getResources().getColor(R.color.app_color_white));
				break;
			case DATA_TYPE_03:
				tv_img.setBackgroundResource(R.drawable.shape_style_solid_04_08);
				tv_img.setTextColor(getResources().getColor(R.color.app_color_white));
				break;
			default:
				tv_all.setBackgroundResource(R.drawable.shape_style_solid_04_08);
				tv_all.setTextColor(getResources().getColor(R.color.app_color_white));
				break;
		}
		loadFirstPageData();
	}

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		mRecyclerView.smoothScrollToPosition(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.comment_goods_tv_all:
				dataType = DATA_TYPE_01;
				changeViewStatus();
				break;
			case R.id.comment_goods_tv_new:
				dataType = DATA_TYPE_02;
				changeViewStatus();
				break;
			case R.id.comment_goods_tv_img:
				dataType = DATA_TYPE_03;
				changeViewStatus();
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

	private void clearData() {
		al_show.clear();
		am_show.clear();
	}

	/**
	 * 加载第一页数据
	 */
	private void loadFirstPageData() {
		toTop();
		clearData();
		refresh_rv.doPullRefreshing(true, 0);
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
		map.put("skuCode", goodsCode);
		map.put("types", String.valueOf(dataType));
		loadSVData(AppConfig.URL_GOODS_COMMENT, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_GOODS_COMMENT);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity<CommentEntity> baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_GOODS_COMMENT:
					baseEn = JsonUtils.getCommentGoodsListData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal();
						List<CommentEntity> lists = filterData(baseEn.getLists(), am_show);
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
						}
						updateListData();
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
		updateListData();
		handleErrorCode(null);
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		refresh_rv.onPullUpRefreshComplete();
		refresh_rv.onPullDownRefreshComplete();
	}

}
