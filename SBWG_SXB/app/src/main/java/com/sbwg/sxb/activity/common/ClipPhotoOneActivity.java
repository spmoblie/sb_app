package com.sbwg.sxb.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.AppManager;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.adapter.ClipPhotoOneAdapter;
import com.sbwg.sxb.entity.ClipPhotoEntity;
import com.sbwg.sxb.utils.LogUtil;

/**
 * "选择一张相片"Activity
 */
public class ClipPhotoOneActivity extends BaseActivity {

	private static final String TAG = "ClipPhotoOneActivity";
	public static ClipPhotoOneActivity instance;

	private GridView gv;
	private ClipPhotoEntity album, photoItem;
	private ClipPhotoOneAdapter adapter;
	private int sizeNub;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_photo_one);
		
		LogUtil.i(TAG, "onCreate");
		AppManager.getInstance().addActivity(this);// 添加Activity到堆栈

		instance = this;
		album = (ClipPhotoEntity) getIntent().getExtras().get("album");
		
		findViewById();
		initView();
	}

	private void findViewById() {
		gv = findViewById(R.id.clip_photo_one_gv);
	}

	private void initView() {
		setTitle(R.string.photo_select_one_title);
		
		sizeNub = album.getBitList().size();
		adapter = new ClipPhotoOneAdapter(this, album);
		gv.setAdapter(adapter);
		gv.setSelector(R.color.ui_bg_color_app);
		gv.setOnItemClickListener(gvItemClickListener);
	}

	@Override
	public void OnListenerRight() {
		photoItem = album.getBitList().get(sizeNub - 1);
		startClipImageActivity();
		super.OnListenerRight();
	}

	/**
	 * 跳转至相片编辑器
	 */
	private void startClipImageActivity() {
		Intent intent;
		switch (shared.getInt(AppConfig.KEY_CLIP_PHOTO_TYPE, AppConfig.PHOTO_TYPE_ROUND)) {
		case AppConfig.PHOTO_TYPE_ROUND: //圆形
			intent = new Intent(this, ClipImageCircularActivity.class);
			break;
		case AppConfig.PHOTO_TYPE_SQUARE: //方形
			intent = new Intent(this, ClipImageSquareActivity.class);
			break;
		default: //圆形
			intent = new Intent(this, ClipImageCircularActivity.class);
			break;
		}
		intent.putExtra(AppConfig.ACTIVITY_CLIP_PHOTO_PATH, photoItem.getPhotoUrl());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LogUtil.i(TAG, "onDestroy");
		super.onDestroy();
		instance = null;
	}

	private OnItemClickListener gvItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (sizeNub - 1 - position >= 0) {
				photoItem = album.getBitList().get(sizeNub - 1 - position);
				startClipImageActivity();
			}
		}
	};

}
