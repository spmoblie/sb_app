package com.songbao.sampo_c.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.AppManager;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.home.ChildFragmentHome;
import com.songbao.sampo_c.activity.login.LoginActivity;
import com.songbao.sampo_c.activity.mine.ChildFragmentMine;
import com.songbao.sampo_c.activity.three.ChildFragmentThree;
import com.songbao.sampo_c.activity.two.ChildFragmentTwo;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.UpdateAppVersion;

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
    @BindView(R.id.main_fragment_tab_tv_4)
    TextView tab_text_4;

    @BindView(R.id.main_fragment_tab_iv_1)
    ImageView tab_icon_1;
    @BindView(R.id.main_fragment_tab_iv_2)
    ImageView tab_icon_2;
    @BindView(R.id.main_fragment_tab_iv_3)
    ImageView tab_icon_3;
    @BindView(R.id.main_fragment_tab_iv_4)
    ImageView tab_icon_4;

    private SharedPreferences shared;
    private FragmentManager manager;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private static Fragment fragment = null;
    private static String current_fragment; //当前要显示的Fragment
    private static final String[] FRAGMENT_CONTAINER = {"fragment_1", "fragment_2", "fragment_3", "fragment_4"};
    private int current_index = -1; //当前显示的Fragment下标索引
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
        tab_text_4.setOnClickListener(this);

        boolean isJump = shared.getBoolean(AppConfig.KEY_JUMP_PAGE, false);
        int save_index = shared.getInt(AppConfig.KEY_MAIN_CURRENT_INDEX, 0);
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": isJump = " + isJump + " save = " + save_index + " current = " + current_index);
        // 非主动跳转且页面出现错误时
        if (current_index == -1 && !isJump && save_index > 0) {
            save_index = 0;
        }
        // 设置默认初始化的界面
        switch (save_index) {
            case 1:
                tab_text_2.performClick();
                break;
            case 2:
                tab_text_3.performClick();
                break;
            case 3:
                tab_text_4.performClick();
                break;
            default:
                tab_text_1.performClick();
                break;
        }
        shared.edit().putBoolean(AppConfig.KEY_JUMP_PAGE, false).apply();
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

        AppApplication.jumpToHomePage(0);
        AppManager.getInstance().finishActivity(this);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fragment_tab_tv_1:
                if (current_index == 0) return;
                current_index = 0;
                break;
            case R.id.main_fragment_tab_tv_2:
                if (current_index == 1) return;
                current_index = 1;
                break;
            case R.id.main_fragment_tab_tv_3:
                if (current_index == 2) return;
                current_index = 2;
                break;
            case R.id.main_fragment_tab_tv_4:
                /*if (!UserManager.getInstance().checkIsLogin()) {
                    openLoginActivity();
                    return;
                }*/
                if (current_index == 3) return;
                current_index = 3;
                break;
        }
        fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_show, v.getId());
        mFragmentPagerAdapter.setPrimaryItem(fl_show, 0, fragment);
        mFragmentPagerAdapter.finishUpdate(fl_show);
        current_fragment = FRAGMENT_CONTAINER[current_index];
        shared.edit().putInt(AppConfig.KEY_MAIN_CURRENT_INDEX, current_index).apply();
        changeState();
        exit = Boolean.FALSE;
    }

    /**
     * 打开登录Activity
     */
    protected void openLoginActivity() {
        shared.edit().putBoolean(AppConfig.KEY_JUMP_PAGE, true).apply();
        Intent intent = new Intent(AppApplication.getAppContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 切换状态
     */
    private void changeState() {
        tab_text_1.setSelected(false);
        tab_text_2.setSelected(false);
        tab_text_3.setSelected(false);
        tab_text_4.setSelected(false);
        tab_icon_1.setSelected(false);
        tab_icon_2.setSelected(false);
        tab_icon_3.setSelected(false);
        tab_icon_4.setSelected(false);
        switch (current_index) {
            case 1:
                tab_text_2.setSelected(true);
                tab_icon_2.setSelected(true);
                break;
            case 2:
                tab_text_3.setSelected(true);
                tab_icon_3.setSelected(true);
                break;
            case 3:
                tab_text_4.setSelected(true);
                tab_icon_4.setSelected(true);
                break;
            default:
                tab_text_1.setSelected(true);
                tab_icon_1.setSelected(true);
                break;
        }
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

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
                        fragment = new ChildFragmentTwo();
                    }
                    return fragment;
                case R.id.main_fragment_tab_tv_3:
                    fragment = manager.findFragmentByTag(current_fragment);
                    if (fragment == null) {
                        fragment = new ChildFragmentThree();
                    }
                    return fragment;
                case R.id.main_fragment_tab_tv_4:
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

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            // 得到缓存的fragment
            /*Fragment fragment = (Fragment) super.instantiateItem(container, position);
            // 得到tag，这点很重要
			String fragmentTag = fragment.getTag();
			switch (position) {
				case R.id.main_fragment_tab_tv_2:
					if (isNewFour) {
						FragmentTransaction ft = fm.beginTransaction();
						// 移除旧的fragment
						ft.remove(fragment);
						// 换成新的fragment
						fragment = new ChildFragmentTwo();
						// 添加新fragment时必须用前面获得的tag，这点很重要
						ft.add(container.getId(), fragment, fragmentTag);
						ft.attach(fragment);
						ft.commit();
					} else {
						isNewFour = true;
					}
				break;
			}
            return fragment;*/
            return super.instantiateItem(container, position);
        }
    }

}
