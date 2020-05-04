package com.songbao.sampo_b.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.activity.common.photo.PhotoAlbumActivity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.CommentEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.widgets.RoundImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;


public class CommentPostActivity extends BaseActivity implements OnClickListener {

	String TAG = CommentPostActivity.class.getSimpleName();

	@BindView(R.id.comment_post_iv_goods)
	RoundImageView iv_goods;

	@BindView(R.id.comment_post_tv_name)
	TextView tv_name;

	@BindView(R.id.comment_post_tv_attr)
	TextView tv_attr;

	@BindView(R.id.comment_post_rb_star)
	RatingBar rb_star;

	@BindView(R.id.comment_post_et_comment)
	EditText et_comment;

	@BindView(R.id.comment_post_iv_photo_01)
	RoundImageView iv_photo_01;

	@BindView(R.id.comment_post_iv_photo_02)
	RoundImageView iv_photo_02;

	@BindView(R.id.comment_post_iv_photo_03)
	RoundImageView iv_photo_03;

	@BindView(R.id.comment_post_iv_photo_01_delete)
	ImageView iv_photo_01_delete;

	@BindView(R.id.comment_post_iv_photo_02_delete)
	ImageView iv_photo_02_delete;

	@BindView(R.id.comment_post_iv_photo_03_delete)
	ImageView iv_photo_03_delete;

	@BindView(R.id.comment_post_tv_add_photo)
	TextView tv_add_photo;

	@BindView(R.id.comment_post_tv_post)
	TextView tv_post;

	private CommentEntity data;
	private float starNum;
	private String contentStr;
	private ArrayList<String> al_photo_url = new ArrayList<>();
	private ArrayList<String> al_image_url = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_post);

		data = (CommentEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

		initView();
	}

	private void initView() {
		setTitle(R.string.comment_mine);

		iv_photo_01.setOnClickListener(this);
		iv_photo_02.setOnClickListener(this);
		iv_photo_03.setOnClickListener(this);
		iv_photo_01_delete.setOnClickListener(this);
		iv_photo_02_delete.setOnClickListener(this);
		iv_photo_03_delete.setOnClickListener(this);
		tv_add_photo.setOnClickListener(this);
		tv_post.setOnClickListener(this);

		if (data != null) {
			GoodsEntity goodsEn = data.getGoodsEn();
			if (goodsEn != null) {
				Glide.with(AppApplication.getAppContext())
						.load(goodsEn.getPicUrl())
						.apply(AppApplication.getShowOptions())
						.into(iv_goods);

				tv_name.setText(goodsEn.getName());
				tv_attr.setText(goodsEn.getAttribute());
			}
		}

		rb_star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (rating < 1) {
					rb_star.setRating(1);
				}
			}
		});
	}

	private void initPhotoView() {
		iv_photo_01.setVisibility(View.GONE);
		iv_photo_02.setVisibility(View.GONE);
		iv_photo_03.setVisibility(View.GONE);
		iv_photo_01_delete.setVisibility(View.GONE);
		iv_photo_02_delete.setVisibility(View.GONE);
		iv_photo_03_delete.setVisibility(View.GONE);
		tv_add_photo.setVisibility(View.VISIBLE);

		if (al_photo_url.size() > 0) {
			RoundImageView iv_show;
			for (int i = 0; i < al_photo_url.size(); i++) {
				switch (i) {
					case 0:
					default:
						iv_photo_01.setVisibility(View.VISIBLE);
						iv_photo_01_delete.setVisibility(View.VISIBLE);
						iv_show = iv_photo_01;
						break;
					case 1:
						iv_photo_02.setVisibility(View.VISIBLE);
						iv_photo_02_delete.setVisibility(View.VISIBLE);
						iv_show = iv_photo_02;
						break;
					case 2:
						iv_photo_03.setVisibility(View.VISIBLE);
						iv_photo_03_delete.setVisibility(View.VISIBLE);
						tv_add_photo.setVisibility(View.GONE);
						iv_show = iv_photo_03;
						break;
				}
				Glide.with(AppApplication.getAppContext())
						.load(al_photo_url.get(i))
						.apply(AppApplication.getShowOptions())
						.into(iv_show);
			}
		}
	}

	private boolean checkData() {
		starNum = rb_star.getRating();
		contentStr = et_comment.getText().toString();
		// 校验非空
		if (StringUtil.isNull(contentStr)) {
			CommonTools.showToast(getString(R.string.comment_content_null));
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_post_iv_photo_01:
		case R.id.comment_post_iv_photo_01_delete:
			if (al_photo_url.size() > 0) {
				al_photo_url.remove(0);
				initPhotoView();
			}
			break;
		case R.id.comment_post_iv_photo_02:
		case R.id.comment_post_iv_photo_02_delete:
			if (al_photo_url.size() > 1) {
				al_photo_url.remove(1);
				initPhotoView();
			}
			break;
		case R.id.comment_post_iv_photo_03:
		case R.id.comment_post_iv_photo_03_delete:
			if (al_photo_url.size() > 2) {
				al_photo_url.remove(2);
				initPhotoView();
			}
			break;
		case R.id.comment_post_tv_add_photo:
			openActivity(PhotoAlbumActivity.class);
			break;
		case R.id.comment_post_tv_post:
			if (checkData()) {
				checkPhotoUrl();
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		String photoUrl = userManager.getPostPhotoUrl();
		if (!StringUtil.isNull(photoUrl)) {
			if (al_photo_url.size() < 3) {
				al_photo_url.add(photoUrl);
			}
			userManager.savePostPhotoUrl("");
		}
		initPhotoView();

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

	private void checkPhotoUrl() {
		if (al_photo_url.size() > 0) {
			String photoUrl = al_photo_url.remove(0);
			uploadPhoto(photoUrl);
		} else {
			postData();
		}
	}

	/**
	 * 上传照片
	 */
	private void uploadPhoto(String photoUrl) {
		if (!StringUtil.isNull(photoUrl)) {
			startAnimation();
			uploadPushFile(new File(photoUrl), 2, AppConfig.REQUEST_SV_UPLOAD_PHOTO);
		}
	}

	private void postData() {
		startAnimation();
		/*if (al_image_url.size() > 0) {

		}*/
		CommonTools.showToast(starNum + contentStr + al_image_url.size());
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_UPLOAD_PHOTO:
					baseEn = JsonUtils.getUploadResult(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						al_image_url.add(baseEn.getOthers());
						checkPhotoUrl();
					} else {
						handleErrorCode(baseEn);
					}
					break;
			}
		} catch (Exception e) {
			loadFailHandle();
			ExceptionUtil.handle(e);
		}
	}

	@Override
	protected void loadFailHandle() {
		super.loadFailHandle();
		handleErrorCode(null);
	}
	
}
