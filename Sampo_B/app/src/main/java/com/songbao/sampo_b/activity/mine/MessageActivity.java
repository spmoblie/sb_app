package com.songbao.sampo_b.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.MessageAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.MessageEntity;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.UserManager;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_b.widgets.recycler.MyRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class MessageActivity extends BaseActivity {

	String TAG = MessageActivity.class.getSimpleName();

	@BindView(R.id.refresh_view_rv)
	PullToRefreshRecyclerView refresh_rv;

	MessageAdapter rvAdapter;
	MyRecyclerView mRecyclerView;

	private String postId; //事件消息Id
	private int data_total = 0; //数据总量
	private int load_page = 1; //加载页数
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private int newNum = 0; //新消息数量
	private int postPos = 0; //事件消息索引
	private boolean isLoadOk = true; //加载控制
	private ArrayList<MessageEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler_view);

		initView();
	}

	private void initView() {
		setTitle(getString(R.string.mine_message));

		newNum = UserManager.getInstance().getUserMsgNum();

		initRecyclerView();
		loadMoreData();
	}

	private void initRecyclerView() {
		refresh_rv.setBackgroundResource(R.color.ui_color_app_bg_02);
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
		mRecyclerView.setBackgroundResource(R.color.ui_color_app_bg_02);

		// 配置适配器
		rvAdapter = new MessageAdapter(mContext, R.layout.item_list_message);
		rvAdapter.addData(al_show, newNum);
		rvAdapter.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_show.size()) return;
				postPos = position;
				MessageEntity msgEn = al_show.get(postPos);
				switch (type) {
					case 0: //已读
						if (msgEn != null && !msgEn.isRead()) {
							postId = msgEn.getId();
							al_show.get(postPos).setRead(true);
							postReadMessage();
							updateListData();
							AppApplication.updateMineData(true); //刷新红点数
						}
						break;
					case 1: //删除
						if (msgEn != null) {
							postId = msgEn.getId();
							showConfirmDialog(getString(R.string.mine_message_delete), new MyHandler(MessageActivity.this), 101);
						}
						break;
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
		rvAdapter.updateData(al_show, newNum);
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
		int page = load_page;
		if (load_type == 0) {
			page = 1;
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("current", page);
		map.put("size", AppConfig.LOAD_SIZE);
		loadSVData(AppConfig.URL_USER_MESSAGE, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_USER_MESSAGE);
	}

	/**
	 * 提交消息删除
	 */
	private void postDeleteMessage() {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", postId);
			postJsonData(AppConfig.URL_USER_MESSAGE_DELETE, jsonObj, AppConfig.REQUEST_SV_USER_MESSAGE_DELETE);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	/**
	 * 提交消息已读
	 */
	private void postReadMessage() {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", postId);
			postJsonData(AppConfig.URL_USER_MESSAGE_STATUS, jsonObj, AppConfig.REQUEST_SV_USER_MESSAGE_STATUS);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity<MessageEntity> baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_USER_MESSAGE:
					baseEn = JsonUtils.getMessageData(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						int oldTotal = data_total;
						data_total = baseEn.getDataTotal();
						List<MessageEntity> lists = filterData(baseEn.getLists(), am_show);
						if (lists != null && lists.size() > 0) {
							if (load_type == 0) {
								//下拉
								LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
								if (data_total > oldTotal) {
									newNum = data_total - oldTotal;
								}
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
					} else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
						handleTimeOut();
						finish();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_USER_MESSAGE_DELETE:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						if (postPos >= 0 && postPos < al_show.size()) {
							al_show.remove(postPos);
							updateListData();
							AppApplication.updateMineData(true); //刷新红点数
						}
					} else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
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

	static class MyHandler extends Handler {

		WeakReference<MessageActivity> mActivity;

		MyHandler(MessageActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MessageActivity theActivity = mActivity.get();
			if (theActivity != null) {
				switch (msg.what) {
					case 101: //消息删除
						theActivity.postDeleteMessage();
						break;
				}
			}
		}
	}

}
