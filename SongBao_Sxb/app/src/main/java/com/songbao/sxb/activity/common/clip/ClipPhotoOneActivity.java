package com.songbao.sxb.activity.common.clip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.adapter.ClipPhotoOneAdapter;
import com.songbao.sxb.entity.ClipPhotoEntity;
import com.songbao.sxb.utils.LogUtil;

/**
 * "选择一张相片"Activity
 */
public class ClipPhotoOneActivity extends BaseActivity {

	String TAG = ClipPhotoOneActivity.class.getSimpleName();

	private GridView gv;
	private ClipPhotoEntity album, photoItem;
	private ClipPhotoOneAdapter adapter;
	private int sizeNub;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_photo_one);

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
		gv.setSelector(R.color.ui_color_app_bg_01);
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
		intent.putExtra(AppConfig.ACTIVITY_KEY_PHOTO_PATH, photoItem.getPhotoUrl());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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
		super.onDestroy();
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
