package com.sbwg.sxb.activity.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseFragment;
import com.sbwg.sxb.activity.common.MyWebViewActivity;
import com.sbwg.sxb.activity.common.ViewPagerActivity;
import com.sbwg.sxb.adapter.AdapterCallback;
import com.sbwg.sxb.adapter.MineListAdapter;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.DesignEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.FileManager;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.UserManager;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.RoundImageView;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshBase;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;


@SuppressLint("NewApi")
public class ChildFragmentMine extends BaseFragment implements OnClickListener {

	String TAG = ChildFragmentMine.class.getSimpleName();

	PullToRefreshListView refresh_lv;
	ListView mListView;
	RoundImageView iv_user_head;
	ImageView iv_setting, iv_debunk;
	TextView tv_user_name, tv_user_id;
	LinearLayout ll_design_main, ll_head_main;

	private Context mContext;
	private AdapterCallback apCallback;
	private MineListAdapter lv_Adapter;
	private LinearLayout.LayoutParams designItemLP;

	private UserInfoEntity infoEn;
	private UserManager userManager;
	private int data_total = 0; //数据总量
	private int current_Page = 1;  //当前列表加载页
	private boolean isUpdateDesign = true;
	private ArrayList<String> urlLists = new ArrayList<String>();
	private ArrayList<DesignEntity> al_design = new ArrayList<>();
	private ArrayList<ThemeEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 与Activity不一样
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
		mContext = getActivity();
		userManager = UserManager.getInstance();

		int screenWidth = AppApplication.getSharedPreferences().getInt(AppConfig.KEY_SCREEN_WIDTH, 0);
		int goodsWidth = (screenWidth - CommonTools.dpToPx(mContext, 16)) / 3;
		designItemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		designItemLP.setMargins(8, 0, 8, 0);
		designItemLP.width = goodsWidth;
		designItemLP.height = goodsWidth;

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_mine, null);
			//Butter Knife初始化
			ButterKnife.bind(this, view);

			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void findViewById(View view) {
		refresh_lv = view.findViewById(R.id.fg_mine_refresh_lv);

		ll_head_main = (LinearLayout) FrameLayout.inflate(mContext, R.layout.layout_list_head_mine, null);
		iv_setting = ll_head_main.findViewById(R.id.fg_mine_iv_setting);
		iv_debunk = ll_head_main.findViewById(R.id.fg_mine_iv_debunk);
		iv_user_head = ll_head_main.findViewById(R.id.fg_mine_iv_head);
		tv_user_name = ll_head_main.findViewById(R.id.fg_mine_tv_used_name);
		tv_user_id = ll_head_main.findViewById(R.id.fg_mine_tv_used_id);
		ll_design_main = ll_head_main.findViewById(R.id.fg_mine_ll_design_main);
	}

	private void initView() {
		iv_setting.setOnClickListener(this);
		iv_debunk.setOnClickListener(this);
		iv_user_head.setOnClickListener(this);
		tv_user_name.setOnClickListener(this);

		loadDBData();
		initListView();
	}

	private void initListView() {
		refresh_lv.setPullRefreshEnabled(true); //下拉刷新
		refresh_lv.setPullLoadEnabled(true); //上拉加载
		refresh_lv.setScrollLoadEnabled(false); //底部翻页
		refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						refresh_lv.onPullDownRefreshComplete();
					}
				}, AppConfig.LOADING_TIME);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 加载更多
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (!isStopLoadMore(al_show.size(), data_total, 0)) {
							loadListData();
						} else {
							refresh_lv.onPullUpRefreshComplete();
							refresh_lv.setHasMoreData(false);
						}
					}
				}, AppConfig.LOADING_TIME);
			}
		});
		mListView = refresh_lv.getRefreshableView();
		mListView.setDivider(null);
		mListView.setVerticalScrollBarEnabled(false);

		// 配置适配器
		apCallback = new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {
				ThemeEntity data = al_show.get(position);
				if (data != null) {
					openWebViewActivity(data.getTitle(), data.getLinkUrl());
				} else {
					CommonTools.showToast(getString(R.string.toast_error_data_null));
				}
			}
		};
		lv_Adapter = new MineListAdapter(mContext, al_show, apCallback);
		mListView.setAdapter(lv_Adapter);

		// 添加头部View
		mListView.addHeaderView(ll_head_main);
		initHeadView();
	}

	private void initHeadView() {
		if (infoEn != null) {
			Bitmap headBitmap = BitmapFactory.decodeFile(AppConfig.SAVE_USER_HEAD_PATH);
			if (headBitmap != null) {
				iv_user_head.setImageBitmap(headBitmap);
			} else {
				loadUserHead();
				iv_user_head.setImageResource(R.drawable.icon_default_head);
			}
			tv_user_name.setText(infoEn.getUserNick());
			tv_user_id.setText(getString(R.string.mine_text_user_id, infoEn.getUserId()));
			tv_user_id.setVisibility(View.VISIBLE);
		} else {
			iv_user_head.setImageResource(R.drawable.icon_default_head);
			tv_user_name.setText("点击登录");
			tv_user_id.setText(getString(R.string.mine_text_user_id, "000000"));
			tv_user_id.setVisibility(View.GONE);
		}
		initDesignView();
	}

	private void initDesignView() {
		urlLists.clear();
		ll_design_main.removeAllViews();

		for (int i = 0; i < 4; i++) {
			final int idsPosition = i;
			DesignEntity items;
			if (i < al_design.size()) {
				items = al_design.get(i);
			} else {
				items = new DesignEntity();
				if (i < 3) {
					items.setImgUrl(null);
				} else {
					items.setImgUrl("");
				}
			}
			if (items != null) {
				String imgUrl = items.getImgUrl();
				if (i < 3) {
					urlLists.add(imgUrl);
				}

				ImageView imageView = new ImageView(mContext);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				Glide.with(AppApplication.getAppContext())
						.load(imgUrl)
						.apply(AppApplication.getShowOptions())
						.into(imageView);

				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (idsPosition < 3) {
							Intent intent = new Intent(mContext, ViewPagerActivity.class);
							intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_URLS, urlLists);
							intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_INDEX, idsPosition);
							startActivity(intent);
						} else {
							CommonTools.showToast("没有更多了");
						}
					}
				});
				ll_design_main.addView(imageView, designItemLP);
			}
		}
	}

	private void updateListData() {
		if (lv_Adapter != null) {
			lv_Adapter.updateAdapter(al_show);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fg_mine_iv_setting:
				startActivity(new Intent(mContext, SettingActivity.class));
				break;
			case R.id.fg_mine_iv_debunk:
				Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
				intent.putExtra("title", "吐个槽");
				intent.putExtra("lodUrl", "https://support.qq.com/product/1221");
				startActivity(intent);
				break;
			case R.id.fg_mine_iv_head:
			case R.id.fg_mine_tv_used_name:
				if (isLogin()) {
					openPersonalActivity();
				} else {
					openLoginActivity();
				}
				break;
		}
	}

	/**
	 * 跳转个人专页
	 */
	private void openPersonalActivity() {
		Intent intent = new Intent(mContext, PersonalActivity.class);
		intent.putExtra("data", infoEn);
		startActivity(intent);
	}

	// 跳转至WebView
	private void openWebViewActivity(String title, String url) {
		Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("lodUrl", url);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);
		// 用户信息
		if (isLogin()) {
			infoEn = getUserInfoData();
			if (shared.getBoolean(AppConfig.KEY_UPDATE_USER_DATA, true)) {
				requestGetUserInfo();
			}
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
	 * 下载用户头像
	 */
	private void loadUserHead() {
		if (infoEn != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					FutureTarget<Bitmap> ft = Glide
							.with(AppApplication.getAppContext())
							.asBitmap()
							.load(infoEn.getUserHead())
							.submit();
					try{
						Bitmap headBitmap = ft.get();
						if (headBitmap != null) {
							AppApplication.saveBitmapFile(headBitmap, new File(AppConfig.SAVE_USER_HEAD_PATH), 100);
							mHandler.sendEmptyMessage(1);
						}
					}catch (Exception e) {
						ExceptionUtil.handle(e);
					}
				}
			}).start();
		}
	}

	/**
	 * 获取用户信息
	 */
	private void requestGetUserInfo() {
		HashMap<String, String> map = new HashMap<>();
		map.put("userId", userManager.getUserId());
		loadSVData(AppConfig.URL_USER_GET, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_USER_GET);
	}

	/**
	 * 加载头部展示数据
	 */
	private void loadDesignData() {
		HashMap<String, String> map = new HashMap<>();
		map.put("userId", userManager.getUserId());
		loadSVData(AppConfig.URL_DESIGN_ALL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_DESIGN_ALL);
	}

	/**
	 * 加载列表翻页数据
	 */
	private void loadListData() {
		if (isUpdateDesign) { //加载设计数据
			loadDesignData();
		}
		HashMap<String, String> map = new HashMap<>();
		map.put("page", String.valueOf(current_Page));
		map.put("size", "3");
		map.put("userId", userManager.getUserId());
		loadSVData(AppConfig.URL_USER_ACTIVITY, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_USER_ACTIVITY);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn = null;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_POST_USER_GET:
					baseEn = JsonUtils.getUserInfo(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						userManager.saveUserInfo((UserInfoEntity) baseEn.getData());
						infoEn = getUserInfoData();
						initHeadView();
						loadUserHead();
						shared.edit().putBoolean(AppConfig.KEY_UPDATE_USER_DATA, false).apply();
					}
					break;
				case AppConfig.REQUEST_SV_POST_DESIGN_ALL:
					baseEn = JsonUtils.getDesignData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						al_design.addAll(baseEn.getLists());
						initDesignView();
						isUpdateDesign = false;
						FileManager.writeFileSaveObject(AppConfig.mineHeadFileName, baseEn, true);
					}
					break;
				case AppConfig.REQUEST_SV_POST_USER_ACTIVITY:
					baseEn = JsonUtils.getMineList(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal(); //加载更多数据控制符
						List<ThemeEntity> lists = baseEn.getLists();
						if (lists != null && lists.size() > 0) {
							if (current_Page == 1) { //缓存第1页数据
								al_show.clear();
								ThemeEntity listEn = new ThemeEntity();
								listEn.setMainLists(lists);
								FileManager.writeFileSaveObject(AppConfig.mineListFileName, listEn, true);
							}
							List<BaseEntity> newLists = addNewEntity(al_show, lists, am_show);
							if (newLists != null) {
								addNewShowLists(newLists);
								current_Page++;
							}
							updateListData();
							LogUtil.i("Retrofit", TAG + " List数据加载成功 —> " + current_Page);
						} else {
							loadFailHandle();
							LogUtil.i("Retrofit", TAG + " List数据加载失败 —> " + current_Page);
						}
					}
					break;
			}
			handleErrorCode(baseEn);
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
	}

	/**
	 * 显示缓冲动画
	 */
	@Override
	protected void startAnimation() {
		super.startAnimation();
	}

	/**
	 * 停止缓冲动画
	 */
	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		refresh_lv.onPullUpRefreshComplete();
	}

	/**
	 * 加载本地缓存数据
	 */
	private void loadDBData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 我的设计
				Object headObj = FileManager.readFileSaveObject(AppConfig.mineHeadFileName, true);
				if (headObj != null) {
					BaseEntity baseEn = (BaseEntity) headObj;
					al_design.addAll(baseEn.getLists());
				}
				// 我的课程
				Object listObj = FileManager.readFileSaveObject(AppConfig.mineListFileName, true);
				if (listObj != null) {
					ThemeEntity listEn = (ThemeEntity) listObj;
					al_show.addAll(listEn.getMainLists());
				}
				mHandler.sendEmptyMessage(1);
			}
		}).start();
	}
	/**
	 * 获取缓存用户信息
	 */
	private UserInfoEntity getUserInfoData() {
		UserInfoEntity userEn = new UserInfoEntity();
		userEn.setUserId(userManager.getUserId());
		userEn.setUserHead(userManager.getUserHead());
		userEn.setUserNick(userManager.getUserNick());
		userEn.setGenderCode(userManager.getUserGender());
		userEn.setBirthday(userManager.getUserBirthday());
		userEn.setUserArea(userManager.getUserArea());
		userEn.setUserIntro(userManager.getUserIntro());
		userEn.setUserEmail(userManager.getUserEmail());
		return userEn;
	}

	private List<DesignEntity> initDesignData() {
		List<DesignEntity> mainLists = new ArrayList<>();
		DesignEntity chEn_1 = new DesignEntity();
		DesignEntity chEn_2 = new DesignEntity();
		DesignEntity chEn_3 = new DesignEntity();
		DesignEntity chEn_4 = new DesignEntity();

		chEn_1.setImgUrl(null);
		mainLists.add(chEn_1);
		chEn_2.setImgUrl(null);
		mainLists.add(chEn_2);
		chEn_3.setImgUrl(null);
		mainLists.add(chEn_3);
		chEn_4.setImgUrl("");
		mainLists.add(chEn_4);

		return mainLists;
	}

	private List<ThemeEntity> initItemsData() {
		List<ThemeEntity> mainLists = new ArrayList<>();
		ThemeEntity isEn_1 = new ThemeEntity();
		ThemeEntity isEn_2 = new ThemeEntity();
		ThemeEntity isEn_3 = new ThemeEntity();

		isEn_1.setPicUrl(AppConfig.IMAGE_URL+ "items_001.png");
		isEn_1.setLinkUrl("https://mp.weixin.qq.com/s/tMi8j08jb7oEHKtmYqdl0g");
		isEn_1.setTitle("北欧教育 | 比NOKIA更震惊世界的芬兰品牌");
		isEn_1.setStartTime("2019-10-16");
		isEn_1.setAddress("深圳");
		isEn_1.setPeople(6);
		mainLists.add(isEn_1);
		isEn_2.setPicUrl(AppConfig.IMAGE_URL+ "items_002.png");
		isEn_2.setLinkUrl("https://mp.weixin.qq.com/s/p1j-Mv0yAW45tkVvjqLBTA");
		isEn_2.setTitle("全球都在追捧的北欧教育，到底有哪些秘密？");
		isEn_2.setStartTime("2019-10-18");
		isEn_2.setAddress("深圳");
		isEn_2.setPeople(8);
		mainLists.add(isEn_2);
		isEn_3.setPicUrl(AppConfig.IMAGE_URL+ "items_003.png");
		isEn_3.setLinkUrl("https://mp.weixin.qq.com/s/Ln0z3fqwBxT9dUP_dJL1uQ");
		isEn_3.setTitle("上海妈妈在挪威，享受北欧式教育的幸福");
		isEn_3.setStartTime("2019-10-20");
		isEn_3.setAddress("深圳");
		isEn_3.setPeople(10);
		mainLists.add(isEn_3);

		return mainLists;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message mMsg) {
			switch (mMsg.what) {
				case 1:
					/*if (al_design.size() <= 0) {
						al_design.addAll(initDesignData());
					}*/
					initHeadView();
					if (al_show.size() <= 0) {
						al_show.addAll(initItemsData());
					}
					updateListData();

					loadListData();
					break;
			}
		}
	};

}

