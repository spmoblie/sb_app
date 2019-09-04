package com.sbwg.sxb.activity.sxb;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseFragment;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildFragmentSXB extends BaseFragment implements OnClickListener {

	private static final String TAG = "ChildFragmentSXB";

	@BindView(R.id.fg_sxb_iv_show)
	ImageView iv_show;

	private Context mContext;

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

		LogUtil.i(TAG, "onCreate");
		mContext = getActivity();

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_sbwg, null);
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fg_sxb_iv_show:
				CommonTools.showToast("功能开发中，敬请期待。。。", 3000);
				break;
		}
	}

	@Override
	public void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(getActivity(), TAG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}

