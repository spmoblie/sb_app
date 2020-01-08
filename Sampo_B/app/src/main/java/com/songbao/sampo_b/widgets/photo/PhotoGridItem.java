package com.songbao.sampo_b.widgets.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.utils.CommonTools;


public class PhotoGridItem extends RelativeLayout implements Checkable {

	private boolean mCheck;
	private ImageView mImageView, mSelect;

	public PhotoGridItem(Context context) {
		this(context, null, 0);
	}

	public PhotoGridItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PhotoGridItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		int imageSize = (AppApplication.screen_width - CommonTools.dpToPx(context, 40)) / 3;
		RelativeLayout.LayoutParams imageLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		imageLP.width = imageSize;
		imageLP.height = imageSize;

		LayoutInflater.from(context).inflate(R.layout.item_grid_photo, this);
		mSelect = findViewById(R.id.photo_select);
		mImageView = findViewById(R.id.photo_img_view);
		mImageView.setLayoutParams(imageLP);
	}

	@Override
	public void setChecked(boolean checked) {
		mCheck = checked;
		System.out.println(checked);
		// mSelect.setBackgroundDrawable(checked ?
		// getResources().getDrawable(R.drawable.cb_on) :
		// getResources().getDrawable(R.drawable.cb_normal));
		mSelect.setVisibility(checked ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean isChecked() {
		return mCheck;
	}

	@Override
	public void toggle() {
		setChecked(!mCheck);
	}

	public void setImgResID(int id) {
		if (mImageView != null) {
			mImageView.setBackgroundResource(id);
		}
	}

	public void SetBitmap(Bitmap bit) {
		if (mImageView != null) {
			mImageView.setImageBitmap(bit);
		}
	}

}
