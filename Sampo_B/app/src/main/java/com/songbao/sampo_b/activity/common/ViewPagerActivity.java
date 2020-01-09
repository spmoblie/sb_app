package com.songbao.sampo_b.activity.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.utils.AsyncImageLoader;
import com.songbao.sampo_b.utils.BitmapUtil;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.widgets.DragImageView;
import com.songbao.sampo_b.widgets.IViewPager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 相片查看器
 */
public class ViewPagerActivity extends BaseActivity {

	String TAG = ViewPagerActivity.class.getSimpleName();

	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	public static final String HASH_MAP_KEY_IMG = "img";
	public static final String HASH_MAP_KEY_BAR = "bar";
	public static final String HASH_MAP_KEY_BTM = "btm";

	private TextView tv_save, tv_page;
	private IViewPager viewPager;
	private DragImageView showView;
	private AsyncImageLoader asyncImageLoader;

	private int mCurrentItem;
	private ArrayList<String> urlLists;
	private ArrayList<View> viewLists = new ArrayList<>();
	private ArrayList<DragImageView> imgLists = new ArrayList<>();
	private ArrayMap<String, DragImageView> am_img = new ArrayMap<>();
	private ArrayMap<String, ProgressBar> am_bar = new ArrayMap<>();
	private ArrayMap<String, Bitmap> am_btm = new ArrayMap<>();

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
			showErrorDialog(null, false, new MyHandler(this));
			return;
		}
		setPageNum(urlLists.size());
		// 创建网络图片加载器
		asyncImageLoader = AsyncImageLoader.getInstance(new AsyncImageLoader.AsyncImageLoaderCallback() {

			@Override
			public void imageLoaded(String path, String cachePath, Bitmap bm) {
				DragImageView imgView = am_img.get(HASH_MAP_KEY_IMG + path);
				if (imgView != null && bm != null) {
					am_btm.put(HASH_MAP_KEY_BTM + path, bm); //记录图片
					imgView.setImageBitmap(bm);
				}
				ProgressBar progress = am_bar.get(HASH_MAP_KEY_BAR + path);
				if (progress != null) {
					progress.setVisibility(View.GONE);
				}
			}
		});
		// 设置布局参数
		FrameLayout.LayoutParams lp_w = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp_w.gravity = Gravity.CENTER;
		FrameLayout.LayoutParams lp_m = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		lp_m.gravity = Gravity.CENTER;
		// 循环添加View
		for (int i = 0; i < urlLists.size(); i++) {
			String imgUrl = urlLists.get(i);
			// 创建父布局
			FrameLayout frameLayout = new FrameLayout(getApplicationContext());
			frameLayout.setLayoutParams(lp_m);
			// 创建子布局-加载动画
			ProgressBar progress = new ProgressBar(getApplicationContext());
			progress.setVisibility(View.GONE);
			progress.setLayoutParams(lp_w);
			// 创建子布局-显示图片
			DragImageView imageView = new DragImageView(getApplicationContext());
			imageView.setLayoutParams(lp_m);
			imageView.setmActivity(this);
			imageView.setScreen_H(screenHeight - statusHeight);
			imageView.setScreen_W(screenWidth);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// 加载图片对象
			AsyncImageLoader.ImageLoadTask task = asyncImageLoader.loadImage(imgUrl);
			if (task != null) {
				if (task.getBitmap() != null) {
					imageView.setImageBitmap(task.getBitmap());
					am_btm.put(HASH_MAP_KEY_BTM + imgUrl, task.getBitmap()); //记录图片
				}else {
					imageView.setImageResource(R.drawable.icon_default_show);
					progress.setVisibility(View.VISIBLE);
					am_bar.put(HASH_MAP_KEY_BAR + imgUrl, progress); //记录加载动画
					am_img.put(HASH_MAP_KEY_IMG + imgUrl, imageView); //记录View
				}
			} else {
				imageView.setImageResource(R.drawable.icon_default_show);
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
			imgLists.add(imageView);
		}
		initShowView();
		viewPager.setAdapter(new PagerAdapter()
        {
            // 创建
			@NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View layout = viewLists.get(position % viewLists.size());
                viewPager.addView(layout);
				return layout;
            }
            
            // 销毁
            @Override
			public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                View layout = viewLists.get(position % viewLists.size());
                viewPager.removeView(layout);
            }
            
            @Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view == object;
            }
            
            @Override
            public int getCount() {
                return viewLists.size();
            }
            
        });
		viewPager.addOnPageChangeListener(new OnPageChangeListener(){
            
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
				CommonTools.showToast(getString(R.string.photo_show_save_ok));
				tv_save.setVisibility(View.GONE);
			}
		});
	}

	private void initShowView() {
		if (mCurrentItem >= 0 && mCurrentItem < imgLists.size()) {
            if (showView != null) {
                showView.setReset();
            }
            showView = imgLists.get(mCurrentItem);
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
		// 销毁对象
		if (asyncImageLoader != null) {
			asyncImageLoader.clearInstance();
		}

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		am_img.clear();
		am_bar.clear();
		am_btm.clear();

		super.onDestroy();
	}

	static class MyHandler extends Handler {

		WeakReference<ViewPagerActivity> mActivity;

		MyHandler(ViewPagerActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case AppConfig.DIALOG_CLICK_OK:
					mActivity.get().finish();
					break;
			}
		}
	}

}
