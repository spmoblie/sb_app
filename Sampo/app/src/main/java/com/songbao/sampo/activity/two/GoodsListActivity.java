package com.songbao.sampo.activity.two;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.GoodsListAdapter;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.GoodsEntity;
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


public class GoodsListActivity extends BaseActivity implements OnClickListener {

	String TAG = GoodsListActivity.class.getSimpleName();

	@BindView(R.id.goods_list_iv_left)
	ImageView iv_left;

	@BindView(R.id.goods_list_et_search)
	EditText et_search;

	@BindView(R.id.goods_list_iv_scan)
	ImageView iv_scan;

	@BindView(R.id.goods_list_tv_top_item_1)
	TextView tv_top_1;

	@BindView(R.id.goods_list_tv_top_item_2)
	TextView tv_top_2;

	@BindView(R.id.goods_list_tv_top_item_3)
	TextView tv_top_3;

	@BindView(R.id.goods_list_tv_top_item_5)
	TextView tv_top_5;

	@BindView(R.id.goods_list_rv_goods)
	PullToRefreshRecyclerView refresh_rv;

	@BindView(R.id.goods_list_iv_cart)
	ImageView iv_cart;

	@BindView(R.id.goods_list_tv_cart_num)
	TextView tv_cart_num;

	MyRecyclerView mRecyclerView;
	GoodsListAdapter rvAdapter;

	public static final int TYPE_1 = 1;  //综合
	public static final int TYPE_2 = 2;  //销量
	public static final int TYPE_3 = 3;  //价格
	public static final int TYPE_5 = 5;  //筛选

	private Drawable rank_up, rank_down, rank_normal;
	private int top_type = TYPE_1; //Top标记
	private int sort_type = 0; //排序标记(0:默认/1:升序/2:降序)
	private boolean isRise2 = false; //销量排序控制符(true:销量升序/false:销量降序)
	private boolean isRise3 = false; //价格排序控制符(true:价格升序/false:价格降序)

	private int data_total = -1; //数据总量
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private int load_page = 1; //加载页数
	private boolean isLoadOk = true; //加载控制

	private ArrayList<GoodsEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_list);

		initView();
	}

	private void initView() {
		setHeadVisibility(View.GONE);

		iv_left.setOnClickListener(this);
		iv_scan.setOnClickListener(this);
		iv_cart.setOnClickListener(this);
		tv_top_1.setOnClickListener(this);
		tv_top_2.setOnClickListener(this);
		tv_top_3.setOnClickListener(this);
		tv_top_5.setOnClickListener(this);

		rank_up = getResources().getDrawable(R.mipmap.icon_rank_up);
		rank_down = getResources().getDrawable(R.mipmap.icon_rank_down);
		rank_normal = getResources().getDrawable(R.mipmap.icon_rank_normal);
		rank_up.setBounds(0, 0, rank_up.getMinimumWidth(), rank_up.getMinimumHeight());
		rank_down.setBounds(0, 0, rank_down.getMinimumWidth(), rank_down.getMinimumHeight());
		rank_normal.setBounds(0, 0, rank_normal.getMinimumWidth(), rank_normal.getMinimumHeight());

		initRecyclerView();
		changeItemStatus();
		loadMoreData();
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
		rvAdapter = new GoodsListAdapter(mContext, R.layout.item_list_goods_list);
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

	private void updateListData() {
		if (al_show.size() <= 0) {
			setNullVisibility(View.VISIBLE);
		} else {
			setNullVisibility(View.GONE);
		}
		rvAdapter.updateData(al_show);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goods_list_iv_left:
			finish();
			break;
		case R.id.goods_list_iv_scan:
			startActivity(new Intent(mContext, DesignerActivity.class));
			break;
		case R.id.goods_list_iv_cart:
			break;
		case R.id.goods_list_tv_top_item_1:
			if (!isLoadOk) return; //加载频率控制
			if (top_type == TYPE_1) return;
			top_type = TYPE_1;
			isRise2 = false;
			isRise3 = false;
			sort_type = 0;
			changeItemStatus();
			loadFirstPageData();
			break;
		case R.id.goods_list_tv_top_item_2:
			if (!isLoadOk) return; //加载频率控制
			top_type = TYPE_2;
			isRise2 = !isRise2;
			if (isRise2) {
				sort_type = 1;
			} else {
				sort_type = 2;
			}
			isRise3 = false;
			changeItemStatus();
			loadFirstPageData();
			break;
		case R.id.goods_list_tv_top_item_3:
			if (!isLoadOk) return; //加载频率控制
			top_type = TYPE_3;
			isRise3 = !isRise3;
			if (isRise3) {
				sort_type = 1;
			} else {
				sort_type = 2;
			}
			isRise2 = false;
			changeItemStatus();
			loadFirstPageData();
			break;
		case R.id.goods_list_tv_top_item_5:
			if (top_type == TYPE_5) return;
			top_type = TYPE_5;
			isRise2 = false;
			isRise3 = false;
			sort_type = 0;
			changeItemStatus();
			break;
		}
	}

	/**
	 * 自定义Top Item状态切换
	 */
	private void changeItemStatus() {
		tv_top_1.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		tv_top_2.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		tv_top_3.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		tv_top_5.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		switch (top_type) {
			case TYPE_1:
				tv_top_1.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				tv_top_2.setCompoundDrawables(null, null, rank_normal, null);
				tv_top_3.setCompoundDrawables(null, null, rank_normal, null);
				break;
			case TYPE_2:
				tv_top_2.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				if (isRise2) {
					tv_top_2.setCompoundDrawables(null, null, rank_up, null);
				} else {
					tv_top_2.setCompoundDrawables(null, null, rank_down, null);
				}
				tv_top_3.setCompoundDrawables(null, null, rank_normal, null);
				break;
			case TYPE_3:
				tv_top_3.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				if (isRise3) {
					tv_top_3.setCompoundDrawables(null, null, rank_up, null);
				} else {
					tv_top_3.setCompoundDrawables(null, null, rank_down, null);
				}
				tv_top_2.setCompoundDrawables(null, null, rank_normal, null);
				break;
			case TYPE_5:
				tv_top_5.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				tv_top_2.setCompoundDrawables(null, null, rank_normal, null);
				tv_top_3.setCompoundDrawables(null, null, rank_normal, null);
				break;
		}
	}

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		mRecyclerView.smoothScrollToPosition(0);
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
		toTop();
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
		map.put("type", String.valueOf(top_type));
		map.put("sort", String.valueOf(sort_type));
		map.put("page", page);
		map.put("size", AppConfig.LOAD_SIZE);
		map.put("isReservation", "1");
		loadSVData(AppConfig.URL_HOME_LIST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_GOODS_LIST);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_GOODS_LIST:
					baseEn = JsonUtils.getGoodsListData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal();
						List<GoodsEntity> lists = filterData(baseEn.getLists(), am_show);
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

}
