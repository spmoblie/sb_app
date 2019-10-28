package com.songbao.sxb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.AppManager;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.home.ChildFragmentHome;
import com.songbao.sxb.activity.login.LoginActivity;
import com.songbao.sxb.activity.mine.ChildFragmentMine;
import com.songbao.sxb.activity.sampo.ChildFragmentSampo;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.UpdateAppVersion;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends FragmentActivity implements OnClickListener {

	public static final String TAG = MainActivity.class.getSimpleName();

	@BindView(R.id.main_fragment_fl_show)
	FrameLayout fl_show;

	@BindView(R.id.main_fragment_tab_tv_1)
	TextView tab_text_1;
	@BindView(R.id.main_fragment_tab_tv_2)
	TextView tab_text_2;
	@BindView(R.id.main_fragment_tab_tv_3)
	TextView tab_text_3;

	@BindView(R.id.main_fragment_tab_iv_1)
	ImageView tab_icon_1;
	@BindView(R.id.main_fragment_tab_iv_2)
	ImageView tab_icon_2;
	@BindView(R.id.main_fragment_tab_iv_3)
	ImageView tab_icon_3;

	private SharedPreferences shared;
	private FragmentManager manager;
	private FragmentPagerAdapter mFragmentPagerAdapter;
	private static Fragment fragment = null;
	private static String current_fragment; //当前要显示的Fragment
	private static final String[] FRAGMENT_CONTAINER = { "fragment_1", "fragment_2", "fragment_3" };
	private int current_index = 0; //当前要显示的Fragment下标索引
	private boolean exit = false;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		try {
			setContentView(R.layout.activity_main);
			// ButterKnife初始化
			ButterKnife.bind(this);

			LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
			AppManager.getInstance().addActivity(this);// 添加Activity到堆栈

			shared = AppApplication.getSharedPreferences();
			manager = getSupportFragmentManager();
			mFragmentPagerAdapter = new MyFragmentPagerAdapter(manager);
			// 检测App版本信息
			UpdateAppVersion.getInstance(this, true);
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
	}

	private void initView() {
		tab_text_1.setOnClickListener(this);
		tab_text_2.setOnClickListener(this);
		tab_text_3.setOnClickListener(this);

		boolean isJump = shared.getBoolean(AppConfig.KEY_JUMP_PAGE, false);
		int open_index = shared.getInt(AppConfig.KEY_MAIN_CURRENT_INDEX, 0);
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": current_index = " + current_index
				             + " open_index = " + open_index + " isJump = " + isJump);
		// 非跳转页面时出现错误 重启Home
		if (!isJump && current_index != open_index) {
			startFragment();
			return;
		}
		// 设置默认初始化的界面
		switch (open_index) {
		case 0:
			tab_text_1.performClick();
			break;
		case 1:
			tab_text_2.performClick();
			break;
		case 2:
			tab_text_3.performClick();
			break;
		default:
			tab_text_1.performClick();
			break;
		}
		shared.edit().putBoolean(AppConfig.KEY_JUMP_PAGE, false).apply();
	}

	/**
	 * 重启HomeFragmentActivity
	 */
	private void startFragment() {
		shared.edit().putInt(AppConfig.KEY_MAIN_CURRENT_INDEX, current_index).apply();
		finish();
		startActivity(new Intent(this, MainActivity.class));
	}

	/**
	 * 跳转到Fragment子界面
	 */
	public void changeFragment(int index) {
		switch (index) {
			case 0:
				fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_show, R.id.main_fragment_tab_tv_1);
				break;
			case 1:
				fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_show, R.id.main_fragment_tab_tv_2);
				break;
			case 2:
				fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_show, R.id.main_fragment_tab_tv_3);
				break;
		}
		current_index = index;
		current_fragment = FRAGMENT_CONTAINER[current_index];
		mFragmentPagerAdapter.setPrimaryItem(fl_show, 0, fragment);
		mFragmentPagerAdapter.finishUpdate(fl_show);
		updateViewStatus();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//回到桌面
		/*if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent home = new Intent(Intent.ACTION_MAIN);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			home.addCategory(Intent.CATEGORY_HOME);
			startActivity(home);
			return true;
		}*/
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (exit) {
				finish();
			} else {
				exit = Boolean.TRUE;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						exit = Boolean.FALSE;
					}
				}, 2000);
				CommonTools.showToast(getString(R.string.toast_exit_prompt), Toast.LENGTH_SHORT);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		exit = Boolean.FALSE;
		initView();

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
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onDestroy");
		AppManager.getInstance().finishActivity(this);

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_fragment_tab_tv_1:
			current_index = 0;
			break;
		case R.id.main_fragment_tab_tv_2:
			if (current_index == 1) return;
			current_index = 1;
			break;
		case R.id.main_fragment_tab_tv_3:
			/*if (!UserManager.getInstance().checkIsLogin()) {
				openLoginActivity();
				return;
			}*/
			if (current_index == 2) return;
			current_index = 2;
			break;
		}
		fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_show, v.getId());
		mFragmentPagerAdapter.setPrimaryItem(fl_show, 0, fragment);
		mFragmentPagerAdapter.finishUpdate(fl_show);
		current_fragment = FRAGMENT_CONTAINER[current_index];
		shared.edit().putInt(AppConfig.KEY_MAIN_CURRENT_INDEX, current_index).apply();
		updateViewStatus();
		exit = Boolean.FALSE;
	}

	/**
	 * 打开登录Activity
	 */
	protected void openLoginActivity(){
		shared.edit().putBoolean(AppConfig.KEY_JUMP_PAGE, true).apply();
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * 自定义切换底栏的View状态
	 */
	private void updateViewStatus() {
		tab_text_1.setSelected(false);
		tab_text_2.setSelected(false);
		tab_text_3.setSelected(false);
		//tab_icon_1.setSelected(false);
		tab_icon_1.setImageResource(R.mipmap.selector_home_tab_1_no);
		tab_icon_2.setSelected(false);
		tab_icon_3.setSelected(false);
		switch (current_index) {
		case 0:
			tab_text_1.setSelected(true);
			//tab_icon_1.setSelected(true);
			changeImage(tab_icon_1, R.drawable.timg);
			break;
		case 1:
			tab_text_2.setSelected(true);
			tab_icon_2.setSelected(true);
			break;
		case 2:
			tab_text_3.setSelected(true);
			tab_icon_3.setSelected(true);
			break;
		}
	}

	private void changeImage(ImageView iv_show, int imgUrl) {
		Glide.with(AppApplication.getAppContext())
				.load(imgUrl)
				.apply(AppApplication.getHeadOptions())
				.into(iv_show);
	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		
		FragmentManager fm;

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case R.id.main_fragment_tab_tv_1:
				fragment = manager.findFragmentByTag(current_fragment);
				if (fragment == null) {
					fragment = new ChildFragmentHome();
				}
				return fragment;
			case R.id.main_fragment_tab_tv_2:
				fragment = manager.findFragmentByTag(current_fragment);
				if (fragment == null) {
					fragment = new ChildFragmentSampo();
				}
				return fragment;
			case R.id.main_fragment_tab_tv_3:
				fragment = manager.findFragmentByTag(current_fragment);
				if (fragment == null) {
					fragment = new ChildFragmentMine();
				}
				return fragment;
			default:
				fragment = manager.findFragmentByTag(FRAGMENT_CONTAINER[0]);
				if (fragment == null) {
					fragment = new ChildFragmentHome();
				}
				return fragment;
			}
		}

		@Override
		public int getCount() {
			return FRAGMENT_CONTAINER.length;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 得到缓存的fragment
			Fragment fragment = (Fragment) super.instantiateItem(container, position);
			// 得到tag，这点很重要
			/*String fragmentTag = fragment.getTag();
			switch (position) {
				case R.id.main_fragment_tab_tv_2:
					if (isNewFour) {
						FragmentTransaction ft = fm.beginTransaction();
						// 移除旧的fragment
						ft.remove(fragment);
						// 换成新的fragment
						fragment = new ChildFragmentSampo();
						// 添加新fragment时必须用前面获得的tag，这点很重要
						ft.add(container.getId(), fragment, fragmentTag);
						ft.attach(fragment);
						ft.commit();
					} else {
						isNewFour = true;
					}
				break;
			}*/
			return fragment;
		}
	}

}
