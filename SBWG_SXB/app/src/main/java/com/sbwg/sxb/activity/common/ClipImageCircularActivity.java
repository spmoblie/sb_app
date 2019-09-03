package com.sbwg.sxb.activity.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.AppManager;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.utils.BitmapUtil;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.widgets.photo.ClipImageView;

import java.io.File;


public class ClipImageCircularActivity extends BaseActivity {
	
	public static ClipImageCircularActivity instance;
	
	private String photoPath;
	private ClipImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_image_circular);

		LogUtil.i(TAG, "onCreate");
		AppManager.getInstance().addActivity(this);// 添加Activity到堆栈

		instance = this;
		photoPath = getIntent().getExtras().getString(AppConfig.ACTIVITY_CLIP_PHOTO_PATH);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		imageView = findViewById(R.id.clip_image_circular_src_pic);
	}
	
	private void initView() {
		setTitle(R.string.photo_clip_head_title);
		setRightViewText(getString(R.string.confirm));
		// 设置需要裁剪的图片
		Bitmap bm = BitmapFactory.decodeFile(photoPath);
		if (bm != null) {
			bm = BitmapUtil.resizeImageByWidth(bm, screenWidth);
			imageView.setImageBitmap(bm);
		}else {
			CommonTools.showToast(getString(R.string.photo_select_no_data), 1000);
		}
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		// 此处获取剪裁后的bitmap
		Bitmap bm = imageView.clip();
		if (bm != null) {
			File file = BitmapUtil.createPath("clip_head.png", false);
			if (file == null) {
            	showErrorDialog(R.string.photo_show_save_fail);
    			return;
			}
			AppApplication.saveBitmapFile(bm, file, 100);
			editor.putString(AppConfig.KEY_CLIP_HEAD_PATH, file.getAbsolutePath()).apply();
		}else {
			CommonTools.showToast(getString(R.string.photo_clip_error), 1000);
		}
		if (ClipPhotoGridActivity.instance != null) {
			ClipPhotoGridActivity.instance.finish();
		}
		if (ClipPhotoOneActivity.instance != null) {
			ClipPhotoOneActivity.instance.finish();
		}
		finish();
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
}
