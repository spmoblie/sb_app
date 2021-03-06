package com.songbao.sampo_b.activity.common.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.utils.BitmapUtil;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.widgets.photo.ClipImageView;

import java.io.File;


public class ClipImageSquareActivity extends BaseActivity {

	String TAG = ClipImageSquareActivity.class.getSimpleName();

	private String photoPath;
	private ClipImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_image_square);
		
		photoPath = getIntent().getStringExtra(AppConfig.ACTIVITY_KEY_PHOTO_PATH);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		imageView = findViewById(R.id.clip_image_square_src_pic);
	}
	
	private void initView() {
		setTitle(R.string.photo_clip_card_title);
		setRightViewText(getString(R.string.confirm));
		// 设置需要裁剪的图片
		Bitmap bm = BitmapFactory.decodeFile(photoPath);
		if (bm != null) {
			bm = BitmapUtil.resizeImageByWidth(bm, screenWidth);
			imageView.setImageBitmap(bm);
		}else {
			CommonTools.showToast(getString(R.string.photo_select_no_data));
		}
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		// 此处获取剪裁后的bitmap
		Bitmap bm = imageView.clip();
		if (bm != null) {
			File file = BitmapUtil.createPath("ID_Card.png", false);
			if (file == null) {
            	showErrorDialog(R.string.photo_show_save_fail);
    			return;
			}
			AppApplication.saveBitmapFile(bm, file, 100);
			shared.edit().putString(AppConfig.KEY_CLIP_CARD_PATH, file.getAbsolutePath()).apply();
		}else {
			CommonTools.showToast(getString(R.string.photo_clip_error));
		}
		closePhotoActivity();
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
}
