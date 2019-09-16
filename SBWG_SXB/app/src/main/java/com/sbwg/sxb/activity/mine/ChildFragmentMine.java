package com.sbwg.sxb.activity.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.sbwg.sxb.entity.DesignEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.UserManager;
import com.sbwg.sxb.widgets.RoundImageView;
import com.sbwg.sxb.widgets.ScrollViewListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("NewApi")
public class ChildFragmentMine extends BaseFragment implements OnClickListener {

	private static final String TAG = "ChildFragmentMine";
	private static int screenWidth = AppApplication.getSharedPreferences().getInt(AppConfig.KEY_SCREEN_WIDTH, 0);

	@BindView(R.id.fg_mine_iv_setting)
	ImageView iv_setting;

	@BindView(R.id.fg_mine_iv_feedback)
	ImageView iv_feedback;

	@BindView(R.id.fg_mine_iv_head)
	RoundImageView iv_user_head;

	@BindView(R.id.fg_mine_tv_used_name)
	TextView tv_user_name;

	@BindView(R.id.fg_mine_tv_used_id)
	TextView tv_user_id;

	@BindView(R.id.fg_mine_ll_design_main)
	LinearLayout ll_design_main;

	@BindView(R.id.fg_mine_items_svlv)
	ScrollViewListView svlv;

	private Context mContext;
	private AdapterCallback apCallback;
	private MineListAdapter lv_Adapter;
	private LinearLayout.LayoutParams designItemLP;

	private DesignEntity designEn;
	private ThemeEntity itemsEn;
	private UserInfoEntity infoEn;
	private ArrayList<String> urlLists = new ArrayList<String>();

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					initHeadView();
					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 与Activity不一样
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LogUtil.i(TAG, "onCreate");
		mContext = getActivity();

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

			initData();
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void initData() {
		designEn = new DesignEntity();
		DesignEntity chEn_1 = new DesignEntity();
		DesignEntity chEn_2 = new DesignEntity();
		DesignEntity chEn_3 = new DesignEntity();
		DesignEntity chEn_4 = new DesignEntity();
		DesignEntity chEn_5 = new DesignEntity();
		List<DesignEntity> mainLists = new ArrayList<DesignEntity>();

		chEn_1.setImgUrl("design_001.png");
		mainLists.add(chEn_1);
		chEn_2.setImgUrl("design_002.png");
		mainLists.add(chEn_2);
		chEn_3.setImgUrl("design_003.png");
		mainLists.add(chEn_3);
		chEn_4.setImgUrl("design_004.png");
		mainLists.add(chEn_4);
		chEn_5.setImgUrl("design_005.png");
		mainLists.add(chEn_5);

		designEn.setMainLists(mainLists);

		itemsEn = new ThemeEntity();
		ThemeEntity isEn_1 = new ThemeEntity();
		ThemeEntity isEn_2 = new ThemeEntity();
		ThemeEntity isEn_3 = new ThemeEntity();
		List<ThemeEntity> isLists = new ArrayList<ThemeEntity>();

		isEn_1.setPicUrl("items_006.png");
		isEn_1.setLinkUrl("https://mp.weixin.qq.com/s/tMi8j08jb7oEHKtmYqdl0g");
		isEn_1.setTitle("北欧教育 | 比NOKIA更震惊世界的芬兰品牌");
		isEn_1.setDescription("下周三18:00开始.深圳");
		isEn_1.setPeople(268);
		isLists.add(isEn_1);
		isEn_2.setPicUrl("items_007.png");
		isEn_2.setLinkUrl("https://mp.weixin.qq.com/s/p1j-Mv0yAW45tkVvjqLBTA");
		isEn_2.setTitle("全球都在追捧的北欧教育，到底有哪些秘密？");
		isEn_2.setDescription("下周六13:00开始.深圳");
		isEn_2.setPeople(1635);
		isLists.add(isEn_2);
		isEn_3.setPicUrl("items_008.jpg");
		isEn_3.setLinkUrl("https://mp.weixin.qq.com/s/Ln0z3fqwBxT9dUP_dJL1uQ");
		isEn_3.setTitle("上海妈妈在挪威，享受北欧式教育的幸福");
		isEn_3.setDescription("下周日15:00开始.深圳");
		isEn_3.setPeople(362);
		isLists.add(isEn_3);

		itemsEn.setMainLists(isLists);
	}

	private void initView() {
		iv_setting.setOnClickListener(this);
		iv_feedback.setOnClickListener(this);
		iv_user_head.setOnClickListener(this);

		initShowView(designEn);
		initItemsView(itemsEn);
	}

	private void initShowView(DesignEntity designEn) {
		if (designEn != null && designEn.getMainLists() != null) {
			List<DesignEntity> datas = designEn.getMainLists();
			urlLists.clear();
			ll_design_main.removeAllViews();

			for (int i = 0; i < datas.size(); i++) {
				final int idsPosition = i;
				final DesignEntity items = datas.get(i);
				if (items != null) {
					String imgUrl = items.getImgUrl();
					urlLists.add(imgUrl);

					ImageView imageView = new ImageView(mContext);
					imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					Glide.with(AppApplication.getAppContext())
							.load(imgUrl)
							.apply(AppApplication.getShowOpeions())
							.into(imageView);

					imageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, ViewPagerActivity.class);
							intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_URLS, urlLists);
							intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_INDEX, idsPosition);
							startActivity(intent);
						}
					});
					ll_design_main.addView(imageView, designItemLP);
				}
			}
		}
	}

	private void initItemsView(final ThemeEntity adEn) {
		if (adEn == null || adEn.getMainLists() == null) return;
		apCallback = new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {
				ThemeEntity data = adEn.getMainLists().get(position);
				if (data != null) {
					openWebViewActivity(data.getTitle(), data.getLinkUrl());
				}else {
					CommonTools.showToast(getString(R.string.toast_error_data_null));
				}
			}
		};
		lv_Adapter = new MineListAdapter(mContext, adEn.getMainLists(), apCallback);
		svlv.setAdapter(lv_Adapter);
		svlv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
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
		} else {
			iv_user_head.setImageResource(R.drawable.icon_default_head);
			tv_user_name.setText("用户昵称");
			tv_user_id.setText(getString(R.string.mine_text_user_id, "000000"));
		}
	}

	private void checkLogin() {
		if (isLogin()) { //已登入
			requestGetUserInfo();
		}else {
			infoEn = null;
			initHeadView();
		}
	}

	private void requestGetUserInfo() {
		getUserInfoData();
		initHeadView();
	}

	private void getUserInfoData() {
		UserManager um = UserManager.getInstance();
		infoEn = new UserInfoEntity();
		infoEn.setUserId("968618");
		infoEn.setUserHead(um.getUserHead());
		infoEn.setUserNick(um.getUserNick());
		infoEn.setGenderCode(um.getUserGender());
		infoEn.setBirthday(um.getUserBirthday());
		infoEn.setUserArea(um.getUserArea());
		infoEn.setUserIntro(um.getUserIntro());
		infoEn.setUserEmail(um.getUserEmail());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fg_mine_iv_setting:
				startActivity(new Intent(mContext, SettingActivity.class));
				break;
			case R.id.fg_mine_iv_feedback:
				Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
				intent.putExtra("title", "吐个槽");
				intent.putExtra("lodUrl", "https://support.qq.com/product/1221");
				startActivity(intent);
				break;
			case R.id.fg_mine_iv_head:
				if (isLogin()) {
					openPersonalActivity();
				} else {

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
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);
		checkLogin();
		super.onResume();
	}

	@Override
	public void onPause() {
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(getActivity(), TAG);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		LogUtil.i(TAG, "onDestroy");
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
							//.load(infoEn.getUserHead())
							.load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1567514197085&di=63f6e7ab81589c8cfddc677df3644cfa&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201708%2F23%2F20170823110912_ezTtH.thumb.700_0.jpeg")
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

}

