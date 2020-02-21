package com.songbao.sampo_b.activity.two;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseFragment;
import com.songbao.sampo_b.activity.common.ScanActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.GoodsGridAdapter;
import com.songbao.sampo_b.adapter.SortOneAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.GoodsSortEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshGridView;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_b.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildFragmentTwo extends BaseFragment implements OnClickListener {

	String TAG = ChildFragmentTwo.class.getSimpleName();

	@BindView(R.id.fg_two_rv_left)
	PullToRefreshRecyclerView rv_left;

	@BindView(R.id.fg_two_gv_right)
	PullToRefreshGridView gv_right;

	@BindView(R.id.fg_two_data_null_main)
	ConstraintLayout view_null;

	@BindView(R.id.fg_two_iv_scan)
	ImageView iv_scan;

	private Context mContext;
	private GridView mGridView;
	private SortOneAdapter rv_adapter;
	private GoodsGridAdapter gv_Adapter;
	private String postSortCode = "";
	private int currentPos = -1; //当前下标
	private int data_total = -1; //数据总量
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private int load_page = 1; //加载页数
	private boolean isLoadOk = true;
	private boolean isSortOk = false;

	private ArrayList<GoodsSortEntity> al_left = new ArrayList<>();
	private ArrayList<GoodsEntity> al_right = new ArrayList<>();
	private ArrayMap<String, Boolean> am_right = new ArrayMap<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 与Activity不一样
	 */
	@Override
	public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_layout_sampo, null);
		//Butter Knife初始化
		ButterKnife.bind(this, view);

		mContext = getActivity();
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");

		initView();
		return view;
	}

	private void initView() {
		iv_scan.setOnClickListener(this);

		initRecyclerView();
	}

	private void initRecyclerView() {
		rv_left.setPullRefreshEnabled(false); //下拉刷新
		rv_left.setPullLoadEnabled(false); //上拉加载

		// 创建布局管理器
		LinearLayoutManager lm_left = new LinearLayoutManager(mContext);
		lm_left.setOrientation(LinearLayoutManager.VERTICAL);
		// 设置布局管理器
		MyRecyclerView mrv_left = rv_left.getRefreshableView();
		mrv_left.setLayoutManager(lm_left);

		// 配置适配器
		rv_adapter = new SortOneAdapter(mContext, R.layout.item_list_sort_one);
		rv_adapter.addData(al_left);
		rv_adapter.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (!isLoadOk) return;
				if (position == currentPos) return;
				if (position < 0 || position >= al_left.size()) return;
				currentPos = position;
				postSortCode = al_left.get(position).getSortCode();
				loadFirstPageData();
				updateLeftListData(position);
			}
		});
		mrv_left.setAdapter(rv_adapter);


		gv_right.setPullRefreshEnabled(true); //下拉刷新
		gv_right.setPullLoadEnabled(true); //上拉加载
		gv_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
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
						if (!isStopLoadMore(al_right.size(), data_total, 0)) {
							loadMoreData();
						} else {
							gv_right.setHasMoreData(false);
							gv_right.onPullUpRefreshComplete();
						}
					}
				}, AppConfig.LOADING_TIME);
			}
		});
		mGridView = gv_right.getRefreshableView();
		mGridView.setNumColumns(2);
		mGridView.setPadding(10, 10, 10, 10);
		mGridView.setHorizontalSpacing( CommonTools.dpToPx(mContext, 10));
		mGridView.setVerticalSpacing( CommonTools.dpToPx(mContext, 20));
		mGridView.setVerticalScrollBarEnabled(false);

		// 配置适配器
		gv_Adapter = new GoodsGridAdapter(mContext);
		gv_Adapter.addData(al_right);
		gv_Adapter.addCallback(new AdapterCallback() {
			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_right.size()) return;
				openGoodsActivity(al_right.get(position).getGoodsCode());
			}
		});
		mGridView.setAdapter(gv_Adapter);
	}

	/**
	 * 更新左边列表数据
	 */
	private void updateLeftListData(int selectPos) {
		rv_adapter.updateData(al_left, selectPos);
	}

	/**
	 * 更新右边列表数据
	 */
	private void updateRightListData() {
		if (al_right.size() <= 0) {
			setNullVisibility(View.VISIBLE);
		} else {
			setNullVisibility(View.GONE);
		}
		gv_Adapter.updateData(al_right);
		toTop();
	}

	/**
	 * 设置内容为空是否可见
	 */
	private void setNullVisibility(int visibility) {
		view_null.setVisibility(visibility);
	}

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		mGridView.smoothScrollToPosition(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fg_two_iv_scan:
				if (ClickUtils.isDoubleClick()) return;
				Intent intent = new Intent(mContext, ScanActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);

		if (!isSortOk) {
			loadSortData();
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
		// 页面结束
		AppApplication.onPageEnd(getActivity(), TAG);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onDestroy");
		super.onDestroy();
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

	/**
	 * 清空缓存
	 */
	private void clearData() {
		al_right.clear();
		am_right.clear();
	}

	/**
	 * 加载第一页数据
	 */
	private void loadFirstPageData() {
		toTop();
		clearData();
		gv_right.doPullRefreshing(true, 0);
	}

	/**
	 *下拉刷新
	 */
	private void refreshData() {
		load_type = 0;
		loadSortGoods();
	}

	/**
	 * 翻页加载
	 */
	private void loadMoreData() {
		load_type = 1;
		loadSortGoods();
	}

	/**
	 * 加载分类商品
	 */
	private void loadSortGoods() {
		if (!isLoadOk) return; //加载频率控制
		isLoadOk = false;
		String page = String.valueOf(load_page);
		if (load_type == 0) {
			page = "1";
		}
		HashMap<String, String> map = new HashMap<>();
		map.put("page", page);
		map.put("size", AppConfig.LOAD_SIZE);
		map.put("isHot", "0");
		map.put("isNews", "0");
		map.put("isRecommend", "0");
		map.put("refCatCode", postSortCode);
		loadSVData(AppConfig.URL_GOODS_LIST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_GOODS_LIST);
	}

	/**
	 * 加载分类数据
	 */
	private void loadSortData() {
		HashMap<String, String> map = new HashMap<>();
		loadSVData(AppConfig.URL_SORT_LIST, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_SORT_LIST);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_SORT_LIST:
					BaseEntity<GoodsSortEntity> sortEn = JsonUtils.getSortListData(jsonObject);
					if (sortEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						isSortOk = true;
						al_left.clear();
						al_left.addAll(sortEn.getLists());
						if (al_left.size() > 0) {
							postSortCode = al_left.get(0).getSortCode();
							loadFirstPageData();
						}
						updateLeftListData(0);
					} else {
						handleErrorCode(sortEn);
					}
					break;
				case AppConfig.REQUEST_SV_GOODS_LIST:
					BaseEntity<GoodsEntity> goodsEn = JsonUtils.getGoodsListData(jsonObject);
					if (goodsEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = goodsEn.getDataTotal();
						List<GoodsEntity> lists = filterData(goodsEn.getLists(), am_right);
						if (lists != null && lists.size() > 0) {
							if (load_type == 0) {
								//下拉
								LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
								lists.addAll(al_right);
								al_right.clear();
							}else {
								//翻页
								LogUtil.i(LogUtil.LOG_HTTP, TAG + " 翻页数据 —> size = " + lists.size());
								load_page++;
							}
							al_right.addAll(lists);
						}
						updateRightListData();
					} else {
						handleErrorCode(goodsEn);
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
	protected void handleErrorCode(BaseEntity baseEn) {
		CommonTools.showToast(getString(R.string.toast_server_busy));
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		gv_right.onPullUpRefreshComplete();
		gv_right.onPullDownRefreshComplete();
	}
}

