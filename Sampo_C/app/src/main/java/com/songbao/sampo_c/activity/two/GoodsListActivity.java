package com.songbao.sampo_c.activity.two;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.activity.common.ScanActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.GoodsListAdapter;
import com.songbao.sampo_c.adapter.GoodsScreenAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.GoodsAttrEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.UserManager;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_c.widgets.recycler.MyRecyclerView;

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

	@BindView(R.id.goods_list_iv_search_clear)
	ImageView iv_clear;

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

	@BindView(R.id.goods_list_iv_data_null)
	ImageView iv_data_null;

	@BindView(R.id.goods_list_tv_data_null)
	TextView tv_data_null;

	@BindView(R.id.goods_list_screen_main)
	LinearLayout screen_main;

	@BindView(R.id.goods_list_screen_hide)
	RelativeLayout screen_hide;

	@BindView(R.id.goods_list_screen_show)
	ConstraintLayout screen_show;

	@BindView(R.id.screen_view_rv)
	RecyclerView rv_screen;

	@BindView(R.id.screen_view_tv_reset)
	TextView tv_reset;

	@BindView(R.id.screen_view_tv_confirm)
	TextView tv_confirm;

	MyRecyclerView mRecyclerView;
	GoodsListAdapter rv_Adapter;
	GoodsScreenAdapter lv_Adapter;

	//private CountDownTimer mCdt;
	private GoodsAttrEntity attrEn;
	private Animation mainShow, mainHide, viewShow, viewHide;

	public static final int TYPE_1 = 1;  //综合
	public static final int TYPE_2 = 2;  //销量
	public static final int TYPE_3 = 3;  //价格
	public static final int TYPE_5 = 5;  //筛选

	private Drawable rank_up, rank_down, rank_normal;
	private int top_type = TYPE_1; //Top标记
	private int sort_type = 0; //排序标记(0:无序/1:升序/2:降序)
	private boolean isRise2 = false; //销量排序控制符(true:销量升序/false:销量降序)
	private boolean isRise3 = false; //价格排序控制符(true:价格升序/false:价格降序)
	private int isHot = 0; //"1"表示热门
	private int isNews = 0; //"1"表示新品
	private int isRecommend = 0; //"1"表示推荐

	private int data_total = -1; //数据总量
	private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
	private int load_page = 1; //加载页数
	private boolean isLoadOk = true; //加载控制
	private boolean isShowSR = false; //是否弹出
	private boolean isScreen = false; //是否筛选
	private boolean isAnimStop = true; //动画控制

	private String sortCode = ""; //商品分类编码
	private String searchStr = "";  //搜索关键字
	private String screenStr = ""; //筛选拼接字串
	private long min_price, max_price;

	private ArrayList<GoodsEntity> al_show = new ArrayList<>();
	private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_list);

		sortCode = getIntent().getStringExtra("sortCode");
		isHot = getIntent().getIntExtra("isHot", 0);
		isNews = getIntent().getIntExtra("isNews", 0);
		isRecommend = getIntent().getIntExtra("isRecommend", 0);

		initView();
	}

	private void initView() {
		setHeadVisibility(View.GONE);

		iv_left.setOnClickListener(this);
		iv_clear.setOnClickListener(this);
		iv_scan.setOnClickListener(this);
		iv_cart.setOnClickListener(this);
		tv_top_1.setOnClickListener(this);
		tv_top_2.setOnClickListener(this);
		tv_top_3.setOnClickListener(this);
		screen_hide.setOnClickListener(this);
		screen_show.setOnClickListener(this);
		tv_reset.setOnClickListener(this);
		tv_confirm.setOnClickListener(this);

		rank_up = getResources().getDrawable(R.mipmap.icon_rank_up);
		rank_down = getResources().getDrawable(R.mipmap.icon_rank_down);
		rank_normal = getResources().getDrawable(R.mipmap.icon_rank_normal);
		rank_up.setBounds(0, 0, rank_up.getMinimumWidth(), rank_up.getMinimumHeight());
		rank_down.setBounds(0, 0, rank_down.getMinimumWidth(), rank_down.getMinimumHeight());
		rank_normal.setBounds(0, 0, rank_normal.getMinimumWidth(), rank_normal.getMinimumHeight());

		initAnimation();
		initEditText();
		initRecyclerView();
		changeItemStatus();
		loadMoreData();

		if (StringUtil.isNull(sortCode)) {
			tv_top_5.setOnClickListener(this);
			tv_top_5.setVisibility(View.VISIBLE);
			editTextFocusAndClear(et_search);
		} else {
			hideSoftInput(et_search);
		}
	}

	private void initAnimation() {
		mainShow = AnimationUtils.loadAnimation(mContext, R.anim.alpha_show);
		mainShow.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				screen_main.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimStop = true;
				initScreenAttr();
				screen_show.startAnimation(viewShow);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		mainHide = AnimationUtils.loadAnimation(mContext, R.anim.alpha_hide);
		mainHide.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				screen_main.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimStop = true;
				isShowSR = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		viewShow = AnimationUtils.loadAnimation(mContext, R.anim.in_from_right);
		viewShow.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				screen_show.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimStop = true;
				isShowSR = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		viewHide = AnimationUtils.loadAnimation(mContext, R.anim.out_to_right);
		viewHide.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				screen_show.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimStop = true;
				screen_main.startAnimation(mainHide);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	/**
	 * 初始化筛选属性列表
	 */
	private void initScreenAttr() {
		if (attrEn == null) return;
		// 设置布局管理器
		LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		rv_screen.setLayoutManager(layoutManager);
		// 配置适配器
		GoodsScreenAdapter.ScreenClickCallback apCallback = new GoodsScreenAdapter.ScreenClickCallback() {

			@Override
			public void updatePrice(long minPrice, long maxPrice, int typeCode) {
				min_price = minPrice;
				max_price = maxPrice;
				switch (typeCode) {
					case 1:
						updateScreenData();
						break;
				}
			}

			@Override
			public void setOnClick(GoodsAttrEntity data, int position, int index, int typeCode) {
				if (data == null || position < 0 || position >= attrEn.getAttrLists().size()) return;
				switch (typeCode) {
					case 0: //选择属性项
						boolean isSelect = !data.isSelect();
						String attrIdStr = "";
						String attrNameStr = "";
						int size = attrEn.getAttrLists().get(position).getAttrLists().size();
						if (isSelect && index == size - 1) { //选择"全部"选项
							ArrayList<GoodsAttrEntity> attrList = attrEn.getAttrLists().get(position).getAttrLists();
							for (int i = 0; i < size; i++) {
								if (attrList.get(i).isSelect()) {
									attrEn.getAttrLists().get(position).getAttrLists().get(i).setSelect(false);
								}
							}
						} else {
							if (index == size - 1) return; //拦截取消"全部"选项
							String attrId = "_" + data.getEntityId();
							String attrName = "_" + data.getAttrName();
							//获取已选择Id串
							attrIdStr = attrEn.getAttrLists().get(position).getAttrIdStr();
							attrNameStr = attrEn.getAttrLists().get(position).getAttrNameStr();
							//修改已选择Id串
							if (isSelect) {
								attrIdStr = attrIdStr + attrId;
								attrNameStr = attrNameStr + attrName;
							} else {
								attrIdStr = attrIdStr.replace(attrId, "");
								attrNameStr = attrNameStr.replace(attrName, "");
							}
							attrEn.getAttrLists().get(position).getAttrLists().get(index).setSelect(isSelect);
						}
						attrEn.getAttrLists().get(position).setAttrIdStr(attrIdStr);
						attrEn.getAttrLists().get(position).setAttrNameStr(attrNameStr);
						//处理"全部"选项
						if (StringUtil.isNull(attrIdStr)) {
							attrEn.getAttrLists().get(position).getAttrLists().get(size - 1).setSelect(true);
						} else {
							attrEn.getAttrLists().get(position).getAttrLists().get(size - 1).setSelect(false);
						}
						updateScreenData();
						break;
					case 1: //展开属性面板
						attrEn.getAttrLists().get(position).setShow(!data.isShow());
						updateScreenData();
						break;
				}
			}

		};
		ArrayList<Integer> resLayout = new ArrayList<>();
		resLayout.add(R.layout.item_list_screen_1);
		resLayout.add(R.layout.item_list_screen_2);
		lv_Adapter = new GoodsScreenAdapter(mContext, resLayout, apCallback);
		lv_Adapter.updateData(attrEn, min_price, max_price);
		rv_screen.setAdapter(lv_Adapter);
	}

	private void updateScreenData() {
		lv_Adapter.updateData(attrEn, min_price, max_price);
	}

	private void initEditText() {
		// 创建定时器
		/*mCdt = new CountDownTimer(3000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {
				handleKeywordData();
			}
		};*/
		// 添加键盘监听器
		et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
					handleKeywordData();
					return true;
				}
				return false;
			}
		});
		// 添加输入监听器
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				searchStr = et_search.getText().toString();
				//mCdt.cancel();
				if (StringUtil.isNull(searchStr)) {
					handleKeywordClear();
					iv_clear.setVisibility(View.GONE);
				}else {
					//mCdt.start();
					iv_clear.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * 处理关键字清空事项
	 */
	private void handleKeywordClear() {
		hideSoftInput(et_search);
		loadFirstPageData();
	}

	/**
	 * 处理关键字搜索事项
	 */
	private void handleKeywordData() {
		/*if (mCdt != null) {
			mCdt.cancel();
		}*/
		hideSoftInput(et_search);
		if (!StringUtil.isNull(searchStr)) {
			loadKeywordData();
		}
	}

	/**
	 * 匹配搜索关键字
	 */
	private void loadKeywordData() {
		loadFirstPageData();
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
		rv_Adapter = new GoodsListAdapter(mContext, R.layout.item_list_goods_list);
		rv_Adapter.addData(al_show);
		rv_Adapter.addCallback(new AdapterCallback() {

			@Override
			public void setOnClick(Object data, int position, int type) {
				if (position < 0 || position >= al_show.size()) return;
				if (type == 1) {
					openGoodsActivity(al_show.get(position).getSkuCode(), true);
				} else {
					openGoodsActivity(al_show.get(position).getSkuCode());
				}
			}
		});
		mRecyclerView.setAdapter(rv_Adapter);
	}

	private void updateListData() {
		if (al_show.size() <= 0) {
			setNullVisibility(View.VISIBLE);
		} else {
			setNullVisibility(View.GONE);
		}
		rv_Adapter.updateData(al_show);
	}

	@Override
	protected void setNullVisibility(int visibility) {
		iv_data_null.setVisibility(visibility);
		tv_data_null.setVisibility(visibility);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goods_list_iv_left:
			finish();
			break;
		case R.id.goods_list_iv_search_clear:
			editTextFocusAndClear(et_search);
			break;
		case R.id.goods_list_iv_scan:
			Intent intent = new Intent(mContext, ScanActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.goods_list_iv_cart:
			openCartActivity();
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
			if (isShowSR) {
				hideScreenView();
			} else {
				top_type = TYPE_5;
				isRise2 = false;
				isRise3 = false;
				sort_type = 0;
				changeItemStatus();
				showScreenView();
			}
			break;
		case R.id.goods_list_screen_hide:
			hideScreenView();
			break;
		case R.id.screen_view_tv_reset:
			resetScreenData();
			break;
		case R.id.screen_view_tv_confirm:
			confirmScreenData();
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
		if (!isScreen) {
			tv_top_5.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		}
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

	/**
	 * 显示筛选View
	 */
	private void showScreenView() {
		if (isAnimStop && screen_main.getVisibility() == View.GONE) {
			isAnimStop = false;
			screen_main.startAnimation(mainShow);
		}
	}

	/**
	 * 隐藏筛选View
	 */
	private void hideScreenView() {
		if (isAnimStop && screen_show.getVisibility() == View.VISIBLE) {
			isAnimStop = false;
			screen_show.startAnimation(viewHide);
			screen_main.setVisibility(View.GONE);
		}
		hideSoftInput(et_search);
		if (isScreen) {
			tv_top_1.performClick();
		}
	}

	/**
	 * 重置筛选Data
	 */
	private void resetScreenData() {
		int size1 = attrEn.getAttrLists().size();
		int size2;
		for (int i = 0; i < size1-1; i++) {
			attrEn.getAttrLists().get(i).setShow(false);
			attrEn.getAttrLists().get(i).setAttrIdStr("");
			attrEn.getAttrLists().get(i).setAttrNameStr("");
			size2 = attrEn.getAttrLists().get(i).getAttrLists().size();
			for (int j = 0; j < size2; j++) {
				if (j == size2 - 1) {
					attrEn.getAttrLists().get(i).getAttrLists().get(j).setSelect(true);
				} else {
					attrEn.getAttrLists().get(i).getAttrLists().get(j).setSelect(false);
				}
			}
		}
		min_price = 0;
		max_price = 0;
		screenStr = "";
		isScreen = false;
		updateScreenData();
		tv_top_1.performClick();
	}

	/**
	 * 确定筛选Data
	 */
	private void confirmScreenData() {
		if (min_price > 0 && max_price <= min_price) {
			CommonTools.showToast(getString(R.string.goods_price_max_error));
			return;
		}
		screenStr = "";
		isScreen = false;
		ArrayList<GoodsAttrEntity> attrList = attrEn.getAttrLists();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attrList.size()-1; i++) {
			GoodsAttrEntity attr = attrList.get(i);
			String attrNameStr = attr.getAttrNameStr();
			if (!StringUtil.isNull(attrNameStr)) {
				String[] names = attrNameStr.split("_");
				for (String name: names) {
					if (!StringUtil.isNull(name)) {
						sb.append(name);
						sb.append("|");
					}
				}
				isScreen = true;
			}
		}
		screenStr = sb.toString();
		if (screenStr.contains("|")) {
			sb.deleteCharAt(sb.lastIndexOf("|"));
			screenStr = sb.toString();
		}
		if (min_price > 0 || max_price > 0) {
			isScreen = true;
		}
		hideScreenView();
	}

	@Override
	protected void updateCartGoodsNum() {
		int cartNum = UserManager.getInstance().getUserCartNum();
		if (cartNum > 0) {
			tv_cart_num.setText(String.valueOf(cartNum));
			tv_cart_num.setVisibility(View.VISIBLE);
		} else {
			tv_cart_num.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		updateCartGoodsNum();

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
		if (StringUtil.isNull(sortCode) && attrEn == null) {
			loadScreenAttrData();
		}
		int page = load_page;
		if (load_type == 0) {
			page = 1;
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("page", page);
		map.put("size", AppConfig.LOAD_SIZE);
		map.put("orderByKey", top_type);
		map.put("sourceType", AppConfig.APP_TYPE);
		if (sort_type > 0) {
			map.put("sortByNum", sort_type);
		}
		if (!StringUtil.isNull(searchStr)) {
			map.put("txt", searchStr);
		} else {
			if (!StringUtil.isNull(sortCode)) {
				map.put("refCatCode", sortCode);
				map.put("isHot", isHot);
				map.put("isNews", isNews);
				map.put("isRecommend", isRecommend);
			}
		}
		if (!StringUtil.isNull(screenStr)) {
			map.put("selecteds", screenStr);
		}
		if (min_price > 0 || max_price > 0) {
			map.put("startPrice", min_price);
			map.put("endPrice", max_price);
		}
		loadSVData(AppConfig.URL_GOODS_LIST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_GOODS_LIST);
	}

	/**
	 * 加载筛选属性数据
	 */
	private void loadScreenAttrData() {
		HashMap<String, Object> map = new HashMap<>();
		loadSVData(AppConfig.URL_SCREEN_ATTR, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_SCREEN_ATTR);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity<GoodsEntity> baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_GOODS_LIST:
					baseEn = JsonUtils.getGoodsListData(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						data_total = baseEn.getDataTotal();
						List<GoodsEntity> lists = filterData(baseEn.getLists(), am_show);
						if (lists != null && lists.size() > 0) {
							if (load_type == 0) {
								//下拉
								LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
								lists.addAll(al_show);
								al_show.clear();
								if (load_page <= 1) {
									load_page = 2;
								}
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
				case AppConfig.REQUEST_SV_SCREEN_ATTR:
					attrEn = JsonUtils.getScreenAttrData(jsonObject);
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
