package com.songbao.sampo_c.activity.two;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseFragment;
import com.songbao.sampo_c.activity.common.ScanActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.SortOneAdapter;
import com.songbao.sampo_c.adapter.SortTwoAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.GoodsSortEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.UserManager;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_c.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildFragmentTwo extends BaseFragment implements OnClickListener {

	String TAG = ChildFragmentTwo.class.getSimpleName();

	@BindView(R.id.fg_two_et_search)
	EditText et_search;

	@BindView(R.id.fg_two_tv_scan)
	TextView tv_scan;

	@BindView(R.id.fg_two_rv_left)
	PullToRefreshRecyclerView rv_left;

	@BindView(R.id.fg_two_rv_right)
	PullToRefreshRecyclerView rv_right;

	@BindView(R.id.fg_two_iv_cart)
	ImageView iv_cart;

	@BindView(R.id.fg_two_tv_cart_num)
	TextView tv_cart_num;

	private Context mContext;
	private MyRecyclerView mrv_right;
	private SortOneAdapter rv_adapter_1;
	private SortTwoAdapter rv_adapter_2;
	private String postSortCode = "";
	private int postSortId = 0;
	private boolean isLoadOk = true;
	private boolean isSortOk = false;

	private ArrayList<GoodsSortEntity> al_left = new ArrayList<>();
	private ArrayList<GoodsSortEntity> al_right = new ArrayList<>();

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
		et_search.setOnClickListener(this);
		tv_scan.setOnClickListener(this);
		iv_cart.setOnClickListener(this);

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
		rv_adapter_1 = new SortOneAdapter(mContext, R.layout.item_list_sort_one);
		rv_adapter_1.addData(al_left);
		rv_adapter_1.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (!isLoadOk) return;
				if (position < 0 || position >= al_left.size()) return;
				postSortId = al_left.get(position).getId();
				postSortCode = al_left.get(position).getSortCode();
				loadSortGoods();
				updateLeftListData(position);
			}
		});
		mrv_left.setAdapter(rv_adapter_1);


		rv_right.setPullRefreshEnabled(false); //下拉刷新
		rv_right.setPullLoadEnabled(false); //上拉加载

		// 创建布局管理器
		LinearLayoutManager lm_right = new LinearLayoutManager(mContext);
		lm_right.setOrientation(LinearLayoutManager.VERTICAL);
		// 设置布局管理器
		mrv_right = rv_right.getRefreshableView();
		mrv_right.setLayoutManager(lm_right);

		// 配置适配器
		rv_adapter_2 = new SortTwoAdapter(mContext, R.layout.item_list_sort_two);
		rv_adapter_2.addData(al_right);
		rv_adapter_2.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_right.size()) return;
				int isHot = 0;
				int isNews = 0;
				int isRecommend = 0;
				if (position == 0) { //新品
					isNews = 1;
				} else {
					isHot = 1;
				}
				if (al_right.get(position).getParentId() <= 0) {
					isRecommend = 1;
				}
				openGoodsListActivity(isHot, isNews, isRecommend, al_right.get(position).getSortCode());
			}
		});
		mrv_right.setAdapter(rv_adapter_2);
	}

	/**
	 * 更新左边列表数据
	 */
	private void updateLeftListData(int selectPos) {
		rv_adapter_1.updateData(al_left, selectPos);
	}

	/**
	 * 更新右边列表数据
	 */
	private void updateRightListData() {
		rv_adapter_2.updateData(al_right);
		toTop();
	}

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		mrv_right.smoothScrollToPosition(0);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.fg_two_et_search:
				openGoodsListActivity(0, 0, 0, null);
				break;
			case R.id.fg_two_tv_scan:
				intent = new Intent(mContext, ScanActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.fg_two_iv_cart:
				startActivity(new Intent(mContext, CartActivity.class));
				break;
		}
	}

	/**
	 * 跳转至商品列表页面
	 */
	private void openGoodsListActivity(int isHot, int isNews, int isRecommend, String sortCode) {
		Intent intent = new Intent(mContext, GoodsListActivity.class);
		intent.putExtra("sortCode", sortCode);
		intent.putExtra("isHot", isHot);
		intent.putExtra("isNews", isNews);
		intent.putExtra("isRecommend", isRecommend);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);

		if (!isSortOk) {
			loadSortData();
		}
		int cartNum = UserManager.getInstance().getUserCartNum();
		if (cartNum > 0) {
			tv_cart_num.setText(String.valueOf(cartNum));
			tv_cart_num.setVisibility(View.VISIBLE);
		} else {
			tv_cart_num.setVisibility(View.GONE);
		}

		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
		// 页面结束
		AppApplication.onPageEnd(getActivity(), TAG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onDestroy");
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

	/**
	 * 加载分类数据
	 */
	private void loadSortData() {
		HashMap<String, String> map = new HashMap<>();
		loadSVData(AppConfig.URL_SORT_LIST, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_SORT_LIST);
	}

	/**
	 * 加载分类商品
	 */
	private void loadSortGoods() {
		if (!isLoadOk) return;
		isLoadOk = false;
		HashMap<String, String> map = new HashMap<>();
		map.put("refCatCode", postSortCode);
		loadSVData(AppConfig.URL_SORT_GOODS, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_SORT_GOODS);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity<GoodsSortEntity> baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_SORT_LIST:
					baseEn = JsonUtils.getSortListData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						isSortOk = true;
						al_left.clear();
						al_left.addAll(baseEn.getLists());
						if (al_left.size() > 0) {
							postSortId = al_left.get(0).getId();
							postSortCode = al_left.get(0).getSortCode();
							loadSortGoods();
						}
						updateLeftListData(0);
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_SORT_GOODS:
					baseEn = JsonUtils.getSortGoodsData(jsonObject, postSortId, postSortCode);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						al_right.clear();
						al_right.addAll(baseEn.getLists());
						updateRightListData();
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
	protected void handleErrorCode(BaseEntity baseEn) {
		CommonTools.showToast(getString(R.string.toast_server_busy));
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
	}
}

