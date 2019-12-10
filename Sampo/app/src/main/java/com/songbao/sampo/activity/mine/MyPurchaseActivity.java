package com.songbao.sampo.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RadioButton;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.MyPurchaseAdapter;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.PurchaseEntity;
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


public class MyPurchaseActivity extends BaseActivity implements View.OnClickListener{

	String TAG = MyPurchaseActivity.class.getSimpleName();

	@BindView(R.id.top_bar_radio_rb_1)
	RadioButton rb_1;

	@BindView(R.id.top_bar_radio_rb_2)
	RadioButton rb_2;

	@BindView(R.id.top_bar_radio_rb_3)
	RadioButton rb_3;

	@BindView(R.id.top_bar_radio_rb_4)
	RadioButton rb_4;

	@BindView(R.id.top_bar_radio_rb_5)
	RadioButton rb_5;

	@BindView(R.id.refresh_view_rv)
	PullToRefreshRecyclerView refresh_rv;

	MyRecyclerView mRecyclerView;
	MyPurchaseAdapter rvAdapter;

	public static final int TYPE_1 = 0;  //全部
	public static final int TYPE_2 = 1;  //待付款
	public static final int TYPE_3 = 2;  //待收货
	public static final int TYPE_4 = 3;  //待评价
	public static final int TYPE_5 = 4;  //退换货

	private int data_total = -1; //数据总量
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private int load_page = 1; //加载页数
	private int load_page_1 = 1;
	private int load_page_2 = 1;
	private int load_page_3 = 1;
	private int load_page_4 = 1;
	private int load_page_5 = 1;
	private int top_type = TYPE_1; //Top标记
	private int total_1, total_2, total_3, total_4, total_5;
	private boolean isLoadOk = true; //加载控制

	private ArrayList<PurchaseEntity> al_show = new ArrayList<>();
	private ArrayList<PurchaseEntity> al_all_1 = new ArrayList<>();
	private ArrayList<PurchaseEntity> al_all_2 = new ArrayList<>();
	private ArrayList<PurchaseEntity> al_all_3 = new ArrayList<>();
	private ArrayList<PurchaseEntity> al_all_4 = new ArrayList<>();
	private ArrayList<PurchaseEntity> al_all_5 = new ArrayList<>();
	private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<>();
	private ArrayMap<String, Boolean> am_all_2 = new ArrayMap<>();
	private ArrayMap<String, Boolean> am_all_3 = new ArrayMap<>();
	private ArrayMap<String, Boolean> am_all_4 = new ArrayMap<>();
	private ArrayMap<String, Boolean> am_all_5 = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler_view_top);

		initView();
	}

	private void initView() {
		setTitle(getString(R.string.mine_my_purchase));

		initRadioGroup();
		initRecyclerView();
		setDefaultRadioButton();
		loadFirstPageData();
	}

	private void initRadioGroup() {
		rb_1.setText(getString(R.string.order_all));
		rb_2.setText(getString(R.string.order_wait_pay));
		rb_3.setText(getString(R.string.order_wait_receive));
		rb_4.setText(getString(R.string.order_wait_opinion));
		rb_5.setText(getString(R.string.order_repair_return));
		rb_1.setOnClickListener(this);
		rb_2.setOnClickListener(this);
		rb_3.setOnClickListener(this);
		rb_4.setOnClickListener(this);
		rb_5.setOnClickListener(this);
	}

	private void initRecyclerView() {
		refresh_rv.setHeaderLayoutBackground(R.color.ui_color_app_bg_02);
		refresh_rv.setFooterLayoutBackground(R.color.ui_color_app_bg_02);
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
		rvAdapter = new MyPurchaseAdapter(mContext, R.layout.item_list_my_purchase);
		rvAdapter.addData(al_show);
		rvAdapter.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_show.size()) return;
				startActivity(new Intent(mContext, CustomizeActivity.class));
			}
		});
		mRecyclerView.setAdapter(rvAdapter);
	}

	/**
	 * 设置默认项
	 */
	private void setDefaultRadioButton() {
		RadioButton defaultBtn;
		switch (top_type) {
			case TYPE_1:
				defaultBtn = rb_1;
				break;
			case TYPE_2:
				defaultBtn = rb_2;
				break;
			case TYPE_3:
				defaultBtn = rb_3;
				break;
			case TYPE_4:
				defaultBtn = rb_4;
				break;
			case TYPE_5:
				defaultBtn = rb_5;
				break;
			default:
				defaultBtn = rb_1;
				break;
		}
		defaultBtn.setChecked(true);
	}

	@Override
	public void onClick(View v) {
		if (!isLoadOk) { //加载频率控制
			setDefaultRadioButton();
			return;
		}
		switch (v.getId()) {
			case R.id.top_bar_radio_rb_1:
				if (top_type == TYPE_1) return;
				top_type = TYPE_1;
				addOldListData(al_all_1, load_page_1, total_1);
				if (al_all_1.size() <= 0) {
					load_page_1 = 1;
					total_1 = 0;
					loadFirstPageData();
				}
				break;
			case R.id.top_bar_radio_rb_2:
				if (top_type == TYPE_2) return;
				top_type = TYPE_2;
				addOldListData(al_all_2, load_page_2, total_2);
				if (al_all_2.size() <= 0) {
					load_page_2 = 1;
					total_2 = 0;
					loadFirstPageData();
				}
				break;
			case R.id.top_bar_radio_rb_3:
				if (top_type == TYPE_3) return;
				top_type = TYPE_3;
				addOldListData(al_all_3, load_page_3, total_3);
				if (al_all_3.size() <= 0) {
					load_page_3 = 1;
					total_3 = 0;
					loadFirstPageData();
				}
				break;
			case R.id.top_bar_radio_rb_4:
				if (top_type == TYPE_4) return;
				top_type = TYPE_4;
				addOldListData(al_all_4, load_page_4, total_4);
				if (al_all_4.size() <= 0) {
					load_page_4 = 1;
					total_4 = 0;
					loadFirstPageData();
				}
				break;
			case R.id.top_bar_radio_rb_5:
				if (top_type == TYPE_5) return;
				top_type = TYPE_5;
				addOldListData(al_all_5, load_page_5, total_5);
				if (al_all_5.size() <= 0) {
					load_page_5 = 1;
					total_5 = 0;
					loadFirstPageData();
				}
				break;
		}
	}

	/**
	 * 展示已缓存的数据并至顶
	 */
	private void addOldListData(List<PurchaseEntity> oldLists, int oldPage, int oldTotal) {
		refreshAllShow(oldLists, oldTotal);
		load_page = oldPage;
		updateListData();
		if (load_page != 1) {
			toTop();
		}
		setLoadMoreState();
	}

	/**
	 * 更新列表数据
	 */
	private void updateListData() {
		if (load_page == 1) {
			toTop();
		}
		if (al_show.size() <= 0) {
			setNullVisibility(View.VISIBLE);
		} else {
			setNullVisibility(View.GONE);
		}
		rvAdapter.updateData(al_show);
	}

	/**
	 * 刷新需要展示的数据
	 */
	private void refreshAllShow(List<PurchaseEntity> showLists, int total) {
		al_show.clear();
		al_show.addAll(showLists);
		data_total = total;
	}

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		mRecyclerView.smoothScrollToPosition(0);
	}

	/**
	 * 设置允许加载更多
	 */
	private void setLoadMoreState() {
		refresh_rv.setHasMoreData(true);
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
	 * 加载第一页数据
	 */
	private void loadFirstPageData() {
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
		switch (top_type) {
			case TYPE_1:
				load_page = load_page_1;
				break;
			case TYPE_2:
				load_page = load_page_2;
				break;
			case TYPE_3:
				load_page = load_page_3;
				break;
			case TYPE_4:
				load_page = load_page_4;
				break;
			case TYPE_5:
				load_page = load_page_5;
				break;
		}
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
		map.put("type", String.valueOf(top_type));
		map.put("page", page);
		map.put("size", AppConfig.LOAD_SIZE);
		map.put("isReservation", "1");
		loadSVData(AppConfig.URL_HOME_LIST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_PURCHASE);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_USER_PURCHASE:
					baseEn = JsonUtils.getMyPurchaseData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						int newTotal = baseEn.getDataTotal();
						List<PurchaseEntity> lists = new ArrayList<>();
						switch (top_type) {
							case TYPE_1:
								lists = filterData(baseEn.getLists(), am_all_1);
								if (lists.size() > 0) {
									if (load_type == 0) {
										lists.addAll(al_all_1);
										al_all_1.clear();
									}else {
										load_page_1++;
									}
									total_1 = newTotal;
									al_all_1.addAll(lists);
									refreshAllShow(al_all_1, total_1);
								}
								break;
							case TYPE_2:
								lists = filterData(baseEn.getLists(), am_all_2);
								if (lists.size() > 0) {
									if (load_type == 0) {
										lists.addAll(al_all_2);
										al_all_2.clear();
									}else {
										load_page_2++;
									}
									total_2 = newTotal;
									al_all_2.addAll(lists);
									refreshAllShow(al_all_2, total_2);
								}
								break;
							case TYPE_3:
								lists = filterData(baseEn.getLists(), am_all_3);
								if (lists.size() > 0) {
									if (load_type == 0) {
										lists.addAll(al_all_3);
										al_all_3.clear();
									}else {
										load_page_3++;
									}
									total_3 = newTotal;
									al_all_3.addAll(lists);
									refreshAllShow(al_all_3, total_3);
								}
								break;
							case TYPE_4:
								lists = filterData(baseEn.getLists(), am_all_4);
								if (lists.size() > 0) {
									if (load_type == 0) {
										lists.addAll(al_all_4);
										al_all_4.clear();
									}else {
										load_page_4++;
									}
									total_4 = newTotal;
									al_all_4.addAll(lists);
									refreshAllShow(al_all_4, total_4);
								}
								break;
							case TYPE_5:
								lists = filterData(baseEn.getLists(), am_all_5);
								if (lists.size() > 0) {
									if (load_type == 0) {
										lists.addAll(al_all_5);
										al_all_5.clear();
									}else {
										load_page_5++;
									}
									total_5 = newTotal;
									al_all_5.addAll(lists);
									refreshAllShow(al_all_5, total_5);
								}
								break;
						}
						if (load_type == 0) {
							setLoadMoreState();
							LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
						} else {
							LogUtil.i(LogUtil.LOG_HTTP, TAG + " 翻页数据 —> size = " + lists.size());
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

		switch (top_type) {
			case TYPE_1:
				refreshAllShow(al_all_1, total_1);
				break;
			case TYPE_2:
				refreshAllShow(al_all_2, total_2);
				break;
			case TYPE_3:
				refreshAllShow(al_all_3, total_3);
				break;
			case TYPE_4:
				refreshAllShow(al_all_4, total_4);
				break;
			case TYPE_5:
				refreshAllShow(al_all_5, total_5);
				break;
		}
		updateListData();
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		refresh_rv.onPullUpRefreshComplete();
		refresh_rv.onPullDownRefreshComplete();
	}
}
