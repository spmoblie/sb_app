package com.songbao.sampo_c.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.activity.common.clip.ClipPhotoGridActivity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CommentEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.entity.OCustomizeEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.RoundImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
	private boolean isPostOk = false;
	private String orderNo, goodsCode, skuCode, skuComboName, contentStr;
	private ArrayList<String> al_photos_url = new ArrayList<>();
	private ArrayList<String> al_upload_url = new ArrayList<>();
	private ArrayList<String> al_images_url = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_post);

		data = (CommentEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

		initView();
	}

	private void initView() {
		setTitle(R.string.comment_want);

		iv_photo_01.setOnClickListener(this);
		iv_photo_02.setOnClickListener(this);
		iv_photo_03.setOnClickListener(this);
		iv_photo_01_delete.setOnClickListener(this);
		iv_photo_02_delete.setOnClickListener(this);
		iv_photo_03_delete.setOnClickListener(this);
		tv_add_photo.setOnClickListener(this);
		tv_post.setOnClickListener(this);

		if (data != null) {
			orderNo = data.getOrderNo();
			GoodsEntity goodsEn = data.getGoodsEn();
			if (goodsEn != null) {
				goodsCode = goodsEn.getGoodsCode();
				skuCode = goodsEn.getSkuCode();
				skuComboName = goodsEn.getAttribute();

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

		if (al_photos_url.size() > 0) {
			RoundImageView iv_show;
			for (int i = 0; i < al_photos_url.size(); i++) {
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
						.load(al_photos_url.get(i))
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
			CommonTools.showToast(getString(R.string.comment_content_null), Toast.LENGTH_SHORT);
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.comment_post_iv_photo_01:
			case R.id.comment_post_iv_photo_01_delete:
				if (al_photos_url.size() > 0) {
					al_photos_url.remove(0);
					initPhotoView();
				}
				break;
			case R.id.comment_post_iv_photo_02:
			case R.id.comment_post_iv_photo_02_delete:
				if (al_photos_url.size() > 1) {
					al_photos_url.remove(1);
					initPhotoView();
				}
				break;
			case R.id.comment_post_iv_photo_03:
			case R.id.comment_post_iv_photo_03_delete:
				if (al_photos_url.size() > 2) {
					al_photos_url.remove(2);
					initPhotoView();
				}
				break;
			case R.id.comment_post_tv_add_photo:
				openActivity(ClipPhotoGridActivity.class);
				break;
			case R.id.comment_post_tv_post:
				if (isPostOk) return;
				if (checkData()) {
					al_images_url.clear();
					al_upload_url.clear();
					al_upload_url.addAll(al_photos_url);
					checkUploadUrl();
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
			if (al_photos_url.size() < 3) {
				al_photos_url.add(photoUrl);
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

	@Override
	public void finish() {
		if (isPostOk) {
			setResult(RESULT_OK, new Intent());
		}
		super.finish();
	}

	private void checkUploadUrl() {
		if (al_upload_url.size() > 0) {
			String uploadUrl = al_upload_url.remove(0);
			uploadPhoto(uploadUrl);
		} else {
			postData();
		}
	}

	/**
	 * 上传照片
	 */
	private void uploadPhoto(String uploadUrl) {
		if (!StringUtil.isNull(uploadUrl)) {
			startAnimation();
			uploadPushFile(new File(uploadUrl), 2, AppConfig.REQUEST_SV_UPLOAD_COMMENT_PHOTO);
		}
	}

	private void postData() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("sourceType", AppConfig.DATA_TYPE);
		map.put("orderNo", orderNo);
		map.put("goodsCode", goodsCode);
		if (!StringUtil.isNull(skuCode)) {
			map.put("skuCode", skuCode);
		}
		if (!StringUtil.isNull(skuComboName)) {
			map.put("skuComboName", skuComboName);
		}
		map.put("levels", starNum);
		map.put("evaluateContent", contentStr);
		String images = StringUtil.listToSplitStr(al_images_url);
		if (!StringUtil.isNull(images)) {
			map.put("evaluateImagesStr", images);
		}
		loadSVData(AppConfig.URL_COMMENT_POST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_COMMENT_POST);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_UPLOAD_COMMENT_PHOTO:
					baseEn = JsonUtils.getUploadResult(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						al_images_url.add(baseEn.getOthers());
						checkUploadUrl();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_COMMENT_POST:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						isPostOk = true;
						al_photos_url.clear();
						al_upload_url.clear();
						al_images_url.clear();
						CommonTools.showToast(getString(R.string.comment_success));
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								finish();
							}
						}, 500);
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
