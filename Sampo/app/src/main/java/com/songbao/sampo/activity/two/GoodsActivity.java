package com.songbao.sampo.activity.two;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.GoodsDetailsAdapter;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.widgets.ScrollViewListView;
import com.songbao.sampo.widgets.ViewPagerScroller;

import java.util.ArrayList;

import butterknife.BindView;


public class GoodsActivity extends BaseActivity implements OnClickListener {

	String TAG = GoodsActivity.class.getSimpleName();

	@BindView(R.id.goods_view_vp)
	ViewPager goods_vp;

	@BindView(R.id.goods_vp_indicator)
	LinearLayout vp_indicator;

	@BindView(R.id.goods_view_lv)
	ScrollViewListView goods_lv;

	private Runnable mPagerAction;
	private LinearLayout.LayoutParams indicatorsLP;
	private GoodsDetailsAdapter lv_Adapter;

	private boolean vprStop = false;
	private int idsSize, idsPosition, vprPosition;
	private ImageView[] indicators = null;
	private ArrayList<ImageView> viewLists = new ArrayList<>();
	private ArrayList<String> al_img = new ArrayList<>();
	private ArrayList<String> al_detail = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods);

		initView();
	}

	private void initView() {
		setHeadVisibility(View.GONE);

		// 动态调整宽高
		int ind_margin = CommonTools.dpToPx(mContext, 5);
		indicatorsLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		indicatorsLP.setMargins(ind_margin, 0, 0, 0);

		initDemoData();
		initViewPager();
		initListView();
	}

	private void initViewPager() {
		if (al_img.size() > 0) {
			clearViewPagerData();
			idsSize = al_img.size();
			indicators = new ImageView[idsSize]; // 定义指示器数组大小
			if (idsSize == 2 || idsSize == 3) {
				al_img.addAll(al_img);
			}
			for (int i = 0; i < al_img.size(); i++) {
				final String imgUrl = al_img.get(i);

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
		if (lv_Adapter == null) {
			lv_Adapter = new GoodsDetailsAdapter(mContext);
			lv_Adapter.addCallback(new AdapterCallback() {
				@Override
				public void setOnClick(Object data, int position, int type) {

				}
			});
		}
		lv_Adapter.updateData(al_detail);
		goods_lv.setAdapter(lv_Adapter);
		goods_lv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

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
		// 清除轮播数据
		clearViewPagerData();

		super.onDestroy();
	}

	private void initDemoData() {
		al_img.add(AppConfig.IMAGE_URL + "banner_001.png");
		al_img.add(AppConfig.IMAGE_URL + "banner_002.png");
		al_img.add(AppConfig.IMAGE_URL + "banner_003.png");
		al_img.add(AppConfig.IMAGE_URL + "banner_004.png");
		al_img.add(AppConfig.IMAGE_URL + "banner_005.png");

		al_detail.add("");
		al_detail.add("");
		al_detail.add("");
		al_detail.add("");
		al_detail.add("");
	}

}
