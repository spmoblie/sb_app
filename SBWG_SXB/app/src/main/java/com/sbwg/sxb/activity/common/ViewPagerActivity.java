package com.sbwg.sxb.activity.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.utils.BitmapUtil;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.widgets.DragImageView;
import com.sbwg.sxb.widgets.IViewPager;

import java.io.File;
import java.util.ArrayList;

/**
 * 相片查看器
 */
public class ViewPagerActivity extends BaseActivity {

	String TAG = ViewPagerActivity.class.getSimpleName();

	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	public static final String HASH_MAP_KEY_BTM = "btm";
	
	private ArrayList<String> urlLists;
	private ArrayList<View> viewLists = new ArrayList<View>();
	private ArrayList<DragImageView> imagLists = new ArrayList<DragImageView>();
	private ArrayMap<String, DragImageView> am_img = new ArrayMap<String, DragImageView>();
	private ArrayMap<String, ProgressBar> am_bar = new ArrayMap<String, ProgressBar>();
	private ArrayMap<String, Bitmap> am_btm = new ArrayMap<String, Bitmap>();
	private int mCurrentItem;
	private TextView tv_save, tv_page;
	private FrameLayout frameLayout;
	private ProgressBar progress;
	private DragImageView imageView, showView;
	private IViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager);

		mCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		urlLists = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);

		findViewById();
		initViewPager();
	}


	private void findViewById() {
		viewPager = findViewById(R.id.my_viewpager);
		tv_save = findViewById(R.id.my_image_save);
		tv_page = findViewById(R.id.my_viewpager_tv_page);
	}
	
	private void initViewPager() {
		setHeadVisibility(View.GONE);
		if (urlLists == null) {
			showErrorDialog(null, false, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case AppConfig.DIALOG_CLICK_OK:
							finish();
							break;
					}
				}
			});
			return;
		}
		setPageNum(urlLists.size());
		// 设置布局参数
		FrameLayout.LayoutParams lp_w = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp_w.gravity = Gravity.CENTER;
		FrameLayout.LayoutParams lp_m = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		lp_m.gravity = Gravity.CENTER;
		// 循环添加View
		for (int i = 0; i < urlLists.size(); i++) {
			String imgUrl = urlLists.get(i);
			// 创建父布局
			frameLayout = new FrameLayout(getApplicationContext());
			frameLayout.setLayoutParams(lp_m);
			// 创建子布局-加载动画
			progress = new ProgressBar(getApplicationContext());
			progress.setVisibility(View.GONE);
			progress.setLayoutParams(lp_w);
			// 创建子布局-显示图片
			imageView = new DragImageView(getApplicationContext());
			imageView.setLayoutParams(lp_m);
			imageView.setmActivity(this);
			imageView.setScreen_H(screenHeight - statusHeight);
			imageView.setScreen_W(screenWidth);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// 加载图片对象
			try {
				if (imgUrl.contains(AppConfig.IMAGE_URL)) {
					imgUrl = imgUrl.replace(AppConfig.IMAGE_URL, "");
				}
				Bitmap bm = BitmapFactory.decodeStream(getAssets().open(imgUrl));
				if (bm != null) {
                    imageView.setImageBitmap(bm);
                    am_btm.put(HASH_MAP_KEY_BTM + imgUrl, bm); //记录图片
                }
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
			imageView.setImgOnMoveListener(new DragImageView.ImgOnMoveListener() {
				@Override
				public void onMove(boolean isToLeft, boolean isChange) {
					viewPager.setScanScroll(isChange);
				}
			});
			imageView.setImgOnClickListener(new DragImageView.ImgOnClickListener() {
				@Override
				public void onClick() {
					if (tv_save.getVisibility() == View.VISIBLE) {
						tv_save.setVisibility(View.GONE);
					}else {
						finish();
					}
				}
			});
			imageView.setImgOnLongClickListener(new DragImageView.ImgOnLongClickListener() {
				@Override
				public void onLongClick() {
					if (tv_save.getVisibility() == View.GONE) {
						tv_save.setVisibility(View.VISIBLE);
					}
				}
			});
			frameLayout.addView(imageView);
			frameLayout.addView(progress);
			viewLists.add(frameLayout);
			imagLists.add(imageView);
		}
		initShowView();
		viewPager.setAdapter(new PagerAdapter()
        {
            // 创建
            @Override
            public Object instantiateItem(View container, int position)
            {
                View layout = viewLists.get(position % viewLists.size());
                viewPager.addView(layout);
				return layout;
            }
            
            // 销毁
            @Override
            public void destroyItem(View container, int position, Object object)
            {
                View layout = viewLists.get(position % viewLists.size());
                viewPager.removeView(layout);
            }
            
            @Override
            public boolean isViewFromObject(View arg0, Object arg1)
            {
                return arg0 == arg1;
                
            }
            
            @Override
            public int getCount()
            {
                return viewLists.size();
            }
            
        });
		viewPager.setOnPageChangeListener(new OnPageChangeListener(){
            
            @Override
            public void onPageSelected(final int position){
            	tv_save.setVisibility(View.GONE);
            	mCurrentItem = position % viewLists.size();
				initShowView();
				setPageNum(viewLists.size());
            }
            
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
            	
            }
            
            @Override
            public void onPageScrollStateChanged(int position){
            	
            }
        });
		viewPager.setCurrentItem(mCurrentItem);

		tv_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bitmap bm = am_btm.get(HASH_MAP_KEY_BTM + urlLists.get(mCurrentItem));
				File file = BitmapUtil.createPath(BitmapUtil.filterPath(urlLists.get(mCurrentItem)), true);
				if (file == null) {
	            	showErrorDialog(R.string.photo_show_save_fail);
	    			return;
				}
				AppApplication.saveBitmapFile(bm, file, 100);
				CommonTools.showToast(getString(R.string.photo_show_save_ok), Toast.LENGTH_SHORT);
				tv_save.setVisibility(View.GONE);
			}
		});
	}

	private void initShowView() {
		if (mCurrentItem >= 0 && mCurrentItem < imagLists.size()) {
            if (showView != null) {
                showView.setReset();
            }
            showView = imagLists.get(mCurrentItem);
        }
	}

	private void setPageNum(int totalNum) {
		tv_page.setText(getString(R.string.viewpager_indicator, mCurrentItem + 1, totalNum));
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
		am_img.clear();
		am_bar.clear();
		am_btm.clear();

		super.onDestroy();
	}

}
