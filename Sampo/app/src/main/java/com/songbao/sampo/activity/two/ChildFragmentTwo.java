package com.songbao.sampo.activity.two;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseFragment;
import com.songbao.sampo.activity.common.ScanActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.SortOneAdapter;
import com.songbao.sampo.adapter.SortTwoAdapter;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.SortEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.songbao.sampo.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildFragmentTwo extends BaseFragment implements OnClickListener {

	String TAG = ChildFragmentTwo.class.getSimpleName();

	@BindView(R.id.fg_two_rl_search)
	RelativeLayout rl_search;

	@BindView(R.id.fg_two_tv_scan)
	TextView tv_scan;

	@BindView(R.id.fg_two_rv_left)
	PullToRefreshRecyclerView rv_left;

	@BindView(R.id.fg_two_rv_right)
	PullToRefreshRecyclerView rv_right;

	private Context mContext;
	private MyRecyclerView mrv_right;
	private SortOneAdapter rv_adapter_1;
	private SortTwoAdapter rv_adapter_2;
	private boolean isLoadOk = false;

	private ArrayList<SortEntity> al_left = new ArrayList<>();
	private ArrayList<SortEntity> al_right = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 与Activity不一样
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
		mContext = getActivity();

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_sampo, null);
			//Butter Knife初始化
			ButterKnife.bind(this, view);

			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void initView() {
		rl_search.setOnClickListener(this);
		tv_scan.setOnClickListener(this);

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
				if (position < 0 || position >= al_left.size()) return;
				updateLeftListData(position);
				al_right.clear();
				al_right.addAll(al_left.get(position).getChildLists());
				updateRightListData();
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
		mrv_right.smoothScrollToPosition(0);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.fg_two_rl_search:
				/*intent = new Intent(getActivity(), SketchActivity.class);
				intent.putExtra("title", "定制效果图");
				intent.putExtra("lodUrl", "https://pano.kujiale.com/xiaoguotu/pano/3FO2YBA0ALE2?fromqrcode=true&tdsourcetag=s_pcqq_aiomsg");
				startActivity(intent);*/
				break;
			case R.id.fg_two_tv_scan:
				intent = new Intent(mContext, ScanActivity.class);
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

		if (!isLoadOk) {
			loadServerData();
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
	 * 加载数据
	 */
	private void loadServerData() {
		HashMap<String, String> map = new HashMap<>();
		map.put("page", "1");
		map.put("size", AppConfig.LOAD_SIZE);
		map.put("isReservation", "1");
		loadSVData(AppConfig.URL_HOME_LIST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_SORT_ALL);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_SORT_ALL:
					baseEn = JsonUtils.getAllSortData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						isLoadOk = true;
						al_left.clear();
						al_left.addAll(baseEn.getLists());
						al_left.addAll(baseEn.getLists());
						if (al_left.size() > 0) {
							al_right.clear();
							al_right.addAll(al_left.get(0).getChildLists());
						}
						updateLeftListData(0);
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
}

