package com.songbao.sampo.activity.two;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.CommentLVAdapter;
import com.songbao.sampo.adapter.GoodsDetailsAdapter;
import com.songbao.sampo.entity.CommentEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.widgets.ObservableScrollView;
import com.songbao.sampo.widgets.ScrollViewListView;
import com.songbao.sampo.widgets.ViewPagerScroller;

import java.util.ArrayList;

import butterknife.BindView;


public class GoodsActivity extends BaseActivity implements OnClickListener {

	String TAG = GoodsActivity.class.getSimpleName();

	@BindView(R.id.top_common_ll_main)
	ConstraintLayout ll_top_main;

	@BindView(R.id.top_common_left)
	ImageButton ib_left;

	@BindView(R.id.top_common_right)
	ImageButton ib_right;

	@BindView(R.id.top_common_rb_1)
	RadioButton rb_1;

	@BindView(R.id.top_common_rb_2)
	RadioButton rb_2;

	@BindView(R.id.top_common_rb_3)
	RadioButton rb_3;

	@BindView(R.id.goods_iv_left)
	ImageView iv_left;

	@BindView(R.id.goods_iv_share)
	ImageView iv_share;

	@BindView(R.id.goods_view_sv)
	ObservableScrollView goods_sv;

	@BindView(R.id.goods_view_vp)
	ViewPager goods_vp;

	@BindView(R.id.goods_vp_indicator)
	LinearLayout vp_indicator;

	@BindView(R.id.goods_good_comment_main)
	ConstraintLayout comment_main;

	@BindView(R.id.goods_lv_comment)
	ScrollViewListView lv_comment;

	@BindView(R.id.goods_lv_detail)
	ScrollViewListView lv_detail;

	@BindView(R.id.goods_tv_good_detail)
	TextView title_detail;

	@BindView(R.id.bottom_add_cart_tv_home)
	TextView tv_home;

	@BindView(R.id.bottom_add_cart_tv_cart)
	TextView tv_cart;

	@BindView(R.id.bottom_add_cart_tv_cart_num)
	TextView tv_cart_num;

	@BindView(R.id.bottom_add_cart_tv_cart_add)
	TextView tv_cart_add;

	@BindView(R.id.bottom_add_cart_tv_customize)
	TextView tv_customize;

	private Runnable mPagerAction;
	private LinearLayout.LayoutParams indicatorsLP;
	private CommentLVAdapter lv_comment_Adapter;
	private GoodsDetailsAdapter lv_detail_Adapter;

	public static final int TYPE_1 = 1;  //商品
	public static final int TYPE_2 = 2;  //评价
	public static final int TYPE_3 = 3;  //详情
	private int top_type = TYPE_1; //Top标记

	private String goodsId;
	private boolean vprStop = false;
	private int idsSize, idsPosition, vprPosition;
	private ImageView[] indicators = null;
	private ArrayList<ImageView> viewLists = new ArrayList<>();
	private ArrayList<String> al_image = new ArrayList<>();
	private ArrayList<String> al_detail = new ArrayList<>();
	private ArrayList<CommentEntity> al_comment = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods);

		goodsId = getIntent().getStringExtra("goodsId");

		initView();
	}

	private void initView() {
		setHeadVisibility(View.GONE);

		ib_left.setOnClickListener(this);
		iv_left.setOnClickListener(this);
		ib_right.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		comment_main.setOnClickListener(this);
		tv_home.setOnClickListener(this);
		tv_cart.setOnClickListener(this);
		tv_cart_add.setOnClickListener(this);
		tv_customize.setOnClickListener(this);

		// 动态调整宽高
		int ind_margin = CommonTools.dpToPx(mContext, 5);
		indicatorsLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		indicatorsLP.setMargins(ind_margin, 0, 0, 0);

		initDemoData();
		initRadioGroup();
		initScrollView();
		initViewPager();
		initListView();
	}

	private void initRadioGroup() {
		rb_1.setText("商品");
		rb_2.setText("评价");
		rb_3.setText("详情");
		rb_1.setOnClickListener(this);
		rb_2.setOnClickListener(this);
		rb_3.setOnClickListener(this);
		setDefaultRadioButton();
	}

	private void initScrollView() {
		goods_sv.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int new_x, int new_y, int old_x, int old_y) {
				double alpha;
				if (new_y <= 0){
					//在顶部时完全透明
					alpha = 0;
				}else if (new_y > 0 && new_y <= 600){
					//在滑动高度中时，设置透明度百分比（当前高度/总高度）
					double d = (double) new_y / 600;
					alpha = d * 255;
				}else{
					//滑出总高度 完全不透明
					alpha = 255;
				}
				changeTopViewBackground(alpha);
			}
		});
	}

	private void changeTopViewBackground(double alpha) {
		if (alpha <= 0) {
			ll_top_main.setVisibility(View.GONE);
		} else {
			ll_top_main.setVisibility(View.VISIBLE);
		}
		ll_top_main.setBackgroundColor(Color.argb((int) alpha, 255,255,255));
		ib_left.setBackgroundColor(Color.argb(0, 255,255,255));
		ib_right.setBackgroundColor(Color.argb(0, 255,255,255));
		rb_1.setBackgroundColor(Color.argb(0, 255,255,255));
		rb_1.setBackgroundColor(Color.argb(0, 255,255,255));
		rb_1.setBackgroundColor(Color.argb(0, 255,255,255));
	}

	private void initViewPager() {
		if (al_image.size() > 0) {
			clearViewPagerData();
			idsSize = al_image.size();
			indicators = new ImageView[idsSize]; // 定义指示器数组大小
			if (idsSize == 2 || idsSize == 3) {
				al_image.addAll(al_image);
			}
			for (int i = 0; i < al_image.size(); i++) {
				final String imgUrl = al_image.get(i);

				ImageView iv_show = new ImageView(mContext);
				iv_show.setScaleType(ImageView.ScaleType.CENTER_CROP);

				Glide.with(AppApplication.getAppContext())
						.load(imgUrl)
						.apply(AppApplication.getShowOptions())
						.into(iv_show);

				iv_show.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
				viewLists.add(iv_show);

				if (i < idsSize) {
					// 循环加入指示器
					indicators[i] = new ImageView(mContext);
					indicators[i].setLayoutParams(indicatorsLP);
					indicators[i].setImageResource(R.mipmap.indicators_default);
					if (i == 0) {
						indicators[i].setImageResource(R.mipmap.indicators_now);
					}
					vp_indicator.addView(indicators[i]);
				}
			}
			final boolean loop = viewLists.size() > 3 ? true : false;
			goods_vp.setAdapter(new PagerAdapter() {

				// 创建
				@NonNull
				@Override
				public Object instantiateItem(@NonNull ViewGroup container, int position) {
					if (viewLists.size() <= 0) return null;
					try {
						View layout;
						if (loop) {
							layout = viewLists.get(position % viewLists.size());
						} else {
							layout = viewLists.get(position);
						}
						container.addView(layout);
						return layout;
					} catch (Exception e) {
						ExceptionUtil.handle(e);
						return null;
					}
				}

				// 销毁
				@Override
				public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
					if (viewLists.size() <= 0) return;
					try {
						View layout;
						if (loop) {
							layout = viewLists.get(position % viewLists.size());
						} else {
							layout = viewLists.get(position);
						}
						container.removeView(layout);
					} catch (Exception e) {
						ExceptionUtil.handle(e);
					}
				}

				@Override
				public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
					return view == object;
				}

				@Override
				public int getCount() {
					if (loop) {
						return Integer.MAX_VALUE;
					} else {
						return viewLists.size();
					}
				}
			});
			goods_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				@Override
				public void onPageSelected(final int arg0) {
					if (goods_vp == null || viewLists.size() <= 1) return;
					if (loop) {
						vprPosition = arg0;
						idsPosition = arg0 % viewLists.size();
						if (idsPosition == viewLists.size()) {
							idsPosition = 0;
							goods_vp.setCurrentItem(0);
						}
					} else {
						idsPosition = arg0;
					}
					// 更改指示器图片
					if ((idsSize == 2 || idsSize == 3) && idsPosition >= idsSize) {
						idsPosition = idsPosition - idsSize;
					}
					for (int i = 0; i < idsSize; i++) {
						ImageView imageView = indicators[i];
						if (i == idsPosition)
							imageView.setImageResource(R.mipmap.indicators_now);
						else
							imageView.setImageResource(R.mipmap.indicators_default);
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					if (arg0 == 1) {
						vprStop = true;
					}
				}
			});
			if (loop) {
				goods_vp.setCurrentItem(viewLists.size() * 10);
				if (mPagerAction == null) {
					mPagerAction = new Runnable() {

						@Override
						public void run() {
							if (goods_vp == null || viewLists.size() <= 1) return;
							if (!vprStop) {
								vprPosition++;
								if (goods_vp != null) {
									goods_vp.setCurrentItem(vprPosition);
								}
							}
							vprStop = false;
							goods_vp.postDelayed(mPagerAction, 5000);
						}
					};
				}
				goods_vp.post(mPagerAction);

				ViewPagerScroller scroller = new ViewPagerScroller(mContext);
				scroller.initViewPagerScroll(goods_vp);
			}
		}
	}

	private void clearViewPagerData() {
		if (viewLists.size() > 0) {
			viewLists.clear();
		}
		if (goods_vp != null) {
			goods_vp.removeAllViews();
			goods_vp.removeCallbacks(mPagerAction);
		}
		if (vp_indicator != null) {
			vp_indicator.removeAllViews();
		}
	}

	private void initListView() {
		//精彩评价
		if (lv_comment_Adapter == null) {
			lv_comment_Adapter = new CommentLVAdapter(mContext);
			lv_comment_Adapter.addCallback(new AdapterCallback() {
				@Override
				public void setOnClick(Object data, int position, int type) {
					openCommentActivity(goodsId);
				}
			});
		}
		lv_comment_Adapter.updateData(al_comment);
		lv_comment.setAdapter(lv_comment_Adapter);
		lv_comment.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

		//商品详情
		if (lv_detail_Adapter == null) {
			lv_detail_Adapter = new GoodsDetailsAdapter(mContext);
			lv_detail_Adapter.addCallback(new AdapterCallback() {
				@Override
				public void setOnClick(Object data, int position, int type) {

				}
			});
		}
		lv_detail_Adapter.updateData(al_detail);
		lv_detail.setAdapter(lv_detail_Adapter);
		lv_detail.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.top_common_left:
			case R.id.goods_iv_left:
				finish();
				break;
			case R.id.top_common_right:
			case R.id.goods_iv_share:
				break;
			case R.id.top_common_rb_1:
				if (top_type == TYPE_1) return;
				top_type = TYPE_1;
				changeItemStatus();
				scrollTo(0);
				break;
			case R.id.top_common_rb_2:
				if (top_type == TYPE_2) return;
				top_type = TYPE_2;
				changeItemStatus();
				scrollTo(comment_main.getTop() - 100);
				break;
			case R.id.top_common_rb_3:
				if (top_type == TYPE_3) return;
				top_type = TYPE_3;
				changeItemStatus();
				scrollTo(title_detail.getTop() - 150);
				break;
			case R.id.goods_good_comment_main:
				openCommentActivity(goodsId);
				break;
			case R.id.bottom_add_cart_tv_home:
				break;
			case R.id.bottom_add_cart_tv_cart:
				break;
			case R.id.bottom_add_cart_tv_cart_add:
				loadGoodsAttrData(1);
				break;
			case R.id.bottom_add_cart_tv_customize:
				startActivity(new Intent(mContext, DesignerActivity.class));
				break;
		}
	}

	/**
	 * 设置默认项
	 */
	private void setDefaultRadioButton() {
		RadioButton defaultBtn;
		switch (top_type) {
			default:
			case TYPE_1:
				defaultBtn = rb_1;
				break;
			case TYPE_2:
				defaultBtn = rb_2;
				break;
			case TYPE_3:
				defaultBtn = rb_3;
				break;
		}
		changeItemStatus();
		defaultBtn.setChecked(true);
	}

	/**
	 * 自定义Top Item状态切换
	 */
	private void changeItemStatus() {
		rb_1.setTextSize(15);
		rb_2.setTextSize(15);
		rb_3.setTextSize(15);
		rb_1.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		rb_2.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		rb_3.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		switch (top_type) {
			default:
			case TYPE_1:
				rb_1.setTextSize(18);
				rb_1.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				break;
			case TYPE_2:
				rb_2.setTextSize(18);
				rb_2.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				break;
			case TYPE_3:
				rb_3.setTextSize(18);
				rb_3.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				break;
		}
	}

	/**
	 * 打开评价列表页
	 */
	private void openCommentActivity(String goodsId) {
		Intent intent = new Intent(mContext, CommentActivity.class);
		intent.putExtra("goodsId", goodsId);
		startActivity(intent);
	}

	/**
	 * 滚动到指定位置
	 */
	private void scrollTo(int y) {
		goods_sv.smoothScrollTo(0, y);
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
		// 清除轮播数据
		clearViewPagerData();

		super.onDestroy();
	}

	private void initDemoData() {
		al_image.add(AppConfig.IMAGE_URL + "banner_001.png");
		al_image.add(AppConfig.IMAGE_URL + "banner_002.png");
		al_image.add(AppConfig.IMAGE_URL + "banner_003.png");
		al_image.add(AppConfig.IMAGE_URL + "banner_004.png");
		al_image.add(AppConfig.IMAGE_URL + "banner_005.png");

		al_detail.add("");
		al_detail.add("");
		al_detail.add("");
		al_detail.add("");
		al_detail.add("");

		CommentEntity childEn;
		for (int i = 0; i < 2; i++) {
			childEn = new CommentEntity();
			childEn.setId(""+i+1);
			childEn.setNick("草莓味的冰淇淋");
			childEn.setGoodsAttr("天蓝色；1350*1900");
			childEn.setAddTime("2019/12/25");
			childEn.setContent("很不错，稳固，用料足，没有味道，安装师傅说质量很好，值得购买，还会回购");

			if (i == 0) {
				childEn.setStarNum(2);
				ArrayList<String> imgList = new ArrayList<>();
				imgList.add(AppConfig.IMAGE_URL + "banner_001.png");
				imgList.add(AppConfig.IMAGE_URL + "banner_002.png");
				imgList.add(AppConfig.IMAGE_URL + "banner_003.png");
				childEn.setImgList(imgList);
				childEn.setType(1);
			}else
			if (i == 1) {
				childEn.setStarNum(3);
				ArrayList<String> imgList = new ArrayList<>();
				imgList.add(AppConfig.IMAGE_URL + "banner_001.png");
				imgList.add(AppConfig.IMAGE_URL + "banner_002.png");
				imgList.add(AppConfig.IMAGE_URL + "banner_003.png");
				imgList.add(AppConfig.IMAGE_URL + "banner_004.png");
				imgList.add(AppConfig.IMAGE_URL + "banner_005.png");
				childEn.setImgList(imgList);
				childEn.setType(1);
			}

			al_comment.add(childEn);
		}
	}

}
