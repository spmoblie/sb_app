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
import com.sbwg.sxb.adapter.MyPartyAdapter;
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


public class MyPartyActivity extends BaseActivity implements OnClickListener {

	String TAG = MyPartyActivity.class.getSimpleName();

	@BindView(R.id.refresh_view_rv)
	PullToRefreshRecyclerView refresh_rv;

	MyRecyclerView mRecyclerView;
	MyPartyAdapter rvAdapter;
	AdapterCallback apCallback;

	private int data_total = 0; //数据总量
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
		setTitle(getString(R.string.mine_text_my_party));

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
						refresh_rv.onPullDownRefreshComplete();
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
		rvAdapter = new MyPartyAdapter(mContext, al_show, apCallback);
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
		//loadServerData();
		al_show.clear();
		al_show.addAll(getDemoData().getLists());
		updateListData();
		stopAnimation();
	}

	/**
	 * 加载数据
	 */
	private void loadServerData() {
		HashMap<String, String> map = new HashMap<>();
		map.put("page", String.valueOf(current_Page));
		map.put("size", AppConfig.LOAD_SIZE);
		loadSVData(AppConfig.URL_MESSAGE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_MESSAGE);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_POST_MESSAGE:
					baseEn = JsonUtils.getMessageData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						int newTotal = baseEn.getDataTotal(); //加载更多数据控制符
						List<MessageEntity> lists = baseEn.getLists();
						if (lists.size() > 0) {
							if (current_Page == 1) {
								al_show.clear();
								am_show.clear();
							}
							List<BaseEntity> newLists = addNewEntity(lists, al_show, am_show);
							if (newLists != null) {
								current_Page++;
							}
							if (newLists != null) {
								addNewShowLists(newLists);
							}
							data_total = newTotal;
							updateListData();
						}
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
	private BaseEntity getDemoData() {
		BaseEntity baseEn = new BaseEntity();

		MessageEntity chEn_1 = new MessageEntity();
		MessageEntity chEn_2 = new MessageEntity();
		MessageEntity chEn_3 = new MessageEntity();
		MessageEntity chEn_4 = new MessageEntity();
		MessageEntity chEn_5 = new MessageEntity();

		List<MessageEntity> mainLists = new ArrayList<>();

		chEn_1.setTime("10月08日 10:08");
		chEn_1.setTitle("使用成功");
		chEn_1.setContent("您好！尊敬的松堡迪迪，您已在10月08日 10:06成功参与课程，谢谢您的光临！");
		chEn_1.setRead(false);
		mainLists.add(chEn_1);
		chEn_2.setTime("10月06日 13:18");
		chEn_2.setTitle("预约成功");
		chEn_2.setContent("您好！尊敬的松堡迪迪，您已在10月06日 13:15成功预约并购买小小木匠课程，请注意预约时间，期待您的光临！");
		chEn_2.setRead(false);
		mainLists.add(chEn_2);
		chEn_3.setTime("09月18日 10:08");
		chEn_3.setTitle("使用成功");
		chEn_3.setContent("您好！尊敬的松堡迪迪，您已在09月18日 10:06成功参与课程，谢谢您的光临！");
		chEn_3.setRead(true);
		mainLists.add(chEn_3);
		chEn_4.setTime("09月16日 13:18");
		chEn_4.setTitle("预约成功");
		chEn_4.setContent("您好！尊敬的松堡迪迪，您已在09月16日 13:15成功预约并购买小小木匠课程，请注意预约时间，期待您的光临！");
		chEn_4.setRead(true);
		mainLists.add(chEn_4);
		chEn_5.setTime("09月08日 10:28");
		chEn_5.setTitle("欢迎您来到松小堡");
		chEn_5.setContent("恭喜您成为松小堡家庭中心成员，松小堡欢迎您的到来。");
		chEn_5.setRead(true);
		mainLists.add(chEn_5);

		baseEn.setLists(mainLists);

		return baseEn;
	}

}
