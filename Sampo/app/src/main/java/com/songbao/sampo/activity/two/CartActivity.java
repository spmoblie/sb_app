package com.songbao.sampo.activity.two;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.CartGoodsAdapter;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.CartEntity;
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


public class CartActivity extends BaseActivity implements View.OnClickListener{

	String TAG = CartActivity.class.getSimpleName();

	@BindView(R.id.cart_rv_goods)
	PullToRefreshRecyclerView refresh_rv;

	@BindView(R.id.cart_view_iv_select_all)
	ImageView iv_select_all;

	@BindView(R.id.cart_view_tv_select_all)
	TextView tv_select_all;

	@BindView(R.id.cart_view_tv_price)
	TextView tv_price;

	CartGoodsAdapter rvAdapter;

	private int mPosition = 0;
	private int data_total = -1; //数据总量
	private int load_page = 1; //加载页数
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private boolean isLoadOk = true; //加载控制

	private double totalPrice;
	private boolean isSelectAll;
	private ArrayList<CartEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);

		initView();
	}

	private void initView() {
		setTitle(getString(R.string.cart_shopping));
		setRightViewText("管理");

		iv_select_all.setOnClickListener(this);
		tv_select_all.setOnClickListener(this);

		initRecyclerView();
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
		MyRecyclerView mRecyclerView = refresh_rv.getRefreshableView();
		mRecyclerView.setLayoutManager(layoutManager);

		// 配置适配器
		rvAdapter = new CartGoodsAdapter(mContext, R.layout.item_list_cart_goods);
		rvAdapter.updateData(al_show);
		rvAdapter.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_show.size()) return;
				mPosition = position;
				CartEntity cartEn = al_show.get(position);
				if (cartEn != null) {
					switch (type) {
						case 0: //选择
							boolean isSelect = !cartEn.isSelect();
							al_show.get(position).setSelect(isSelect);
							updateListData();
							checkAllDataStatus();
							break;
						case 5: //定制
							startActivity(new Intent(mContext, DesignerActivity.class));
							break;
						case 6: //删除
							al_show.remove(cartEn);
							updateListData();
							checkAllDataStatus();
							break;
						case 7: //滚动
							rvAdapter.reset(mPosition);
							break;
					}
				}
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
	 * 遍历检查所有数据状态
	 */
	private void checkAllDataStatus() {
		CartEntity cartEn;
		GoodsEntity goodsEn;
		totalPrice = 0;
		isSelectAll = true;
		for (int i = 0; i < al_show.size(); i++) {
			cartEn = al_show.get(i);
			if (cartEn.isSelect()) {
				goodsEn = cartEn.getGoodsEn();
				totalPrice += goodsEn.getPrice()*goodsEn.getNumber();
			} else {
				isSelectAll = false;
			}
		}
		iv_select_all.setSelected(isSelectAll);
		updatePriceTotal();
	}

	/**
	 * 更新购物车商品总价格
	 */
	private void updatePriceTotal() {
		tv_price.setText(df.format(totalPrice));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cart_view_iv_select_all:
			case R.id.cart_view_tv_select_all:
				isSelectAll = !isSelectAll;
				for (int i = 0; i < al_show.size(); i++) {
					al_show.get(i).setSelect(isSelectAll);
				}
				updateListData();
				checkAllDataStatus();
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
					baseEn = JsonUtils.getCartlistData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal();
						List<CartEntity> lists = filterData(baseEn.getLists(), am_show);
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
