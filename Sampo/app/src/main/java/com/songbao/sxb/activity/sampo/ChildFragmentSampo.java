package com.songbao.sxb.activity.sampo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseFragment;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.LogUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildFragmentSampo extends BaseFragment implements OnClickListener {

	String TAG = ChildFragmentSampo.class.getSimpleName();

	@BindView(R.id.fg_sxb_iv_show)
	ImageView iv_show;

	@BindView(R.id.fg_sxb_ll_module_main)
	LinearLayout ll_module_main;

	private Context mContext;
	private RelativeLayout.LayoutParams moduleItemLP;
	private ArrayList<String> al_module = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 与Activity不一样
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
		mContext = getActivity();

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_sxb, null);
			//Butter Knife初始化
			ButterKnife.bind(this, view);

			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void initView() {
		iv_show.setOnClickListener(this);

		int goodsWidth = (AppApplication.screen_width - CommonTools.dpToPx(mContext, 30)) / 4;
		moduleItemLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		moduleItemLP.setMargins(15, 0, 15, 0);
		moduleItemLP.width = goodsWidth;
		moduleItemLP.height = goodsWidth;

		initData();
		initModuleView();
	}

	private void initModuleView() {
		ll_module_main.removeAllViews();
		for (int i = 0; i < al_module.size(); i++) {
			String imgUrl = al_module.get(i);
			View view = View.inflate(mContext, R.layout.item_sxb_module, null);
			ImageView imageView = view.findViewById(R.id.sxb_module_iv_show);
			imageView.setLayoutParams(moduleItemLP);

			TextView textView = view.findViewById(R.id.sxb_module_tv_show);
			textView.setText("第" + (i + 1) + "块");

			Glide.with(AppApplication.getAppContext())
					.load(imgUrl)
					.apply(AppApplication.getShowOptions())
					.into(imageView);

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonTools.showToast("功能开发中...敬请期待^_^");
				}
			});
			ll_module_main.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fg_sxb_iv_show:
				CommonTools.showToast("功能开发中...敬请期待^_^");
				break;
		}
	}

	@Override
	public void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
		// 页面结束
		AppApplication.onPageEnd(getActivity(), TAG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onDestroy");
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

	private void initData() {
		al_module.add(AppConfig.IMAGE_URL + "module_001.png");
		al_module.add(AppConfig.IMAGE_URL + "module_002.png");
		al_module.add(AppConfig.IMAGE_URL + "module_003.png");
		al_module.add(AppConfig.IMAGE_URL + "module_004.png");
		al_module.add(AppConfig.IMAGE_URL + "module_005.png");
		al_module.add(AppConfig.IMAGE_URL + "module_006.png");
		al_module.add(AppConfig.IMAGE_URL + "module_007.png");
		al_module.add(AppConfig.IMAGE_URL + "module_008.png");
	}

}

