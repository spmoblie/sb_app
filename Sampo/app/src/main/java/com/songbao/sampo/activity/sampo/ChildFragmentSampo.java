package com.songbao.sampo.activity.sampo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseFragment;
import com.songbao.sampo.activity.common.ScanActivity;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildFragmentSampo extends BaseFragment implements OnClickListener {

	String TAG = ChildFragmentSampo.class.getSimpleName();

	@BindView(R.id.sampo_iv_scan)
	ImageView iv_scan;

	@BindView(R.id.sampo_iv_show)
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

		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
		mContext = getActivity();

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_sampo, null);
			//Butter Knife初始化
			ButterKnife.bind(this, view);

			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void initView() {
		iv_scan.setOnClickListener(this);
		iv_show.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.sampo_iv_scan:
				intent = new Intent(mContext, ScanActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.sampo_iv_show:
				intent = new Intent(getActivity(), SketchActivity.class);
				intent.putExtra("title", "定制效果图");
				intent.putExtra("lodUrl", "https://pano.kujiale.com/xiaoguotu/pano/3FO2YBA0ALE2?fromqrcode=true&tdsourcetag=s_pcqq_aiomsg");
				startActivity(intent);
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

}

