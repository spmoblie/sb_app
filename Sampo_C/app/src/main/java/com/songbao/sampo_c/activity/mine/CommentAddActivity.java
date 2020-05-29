package com.songbao.sampo_c.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CommentEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
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

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class CommentAddActivity extends BaseActivity implements OnClickListener {

	String TAG = CommentAddActivity.class.getSimpleName();

	@BindView(R.id.comment_add_iv_goods)
	RoundImageView iv_goods;

	@BindView(R.id.comment_add_tv_name)
	TextView tv_name;

	@BindView(R.id.comment_add_tv_attr)
	TextView tv_attr;

	@BindView(R.id.comment_add_rb_star)
	RatingBar rb_star;

	@BindView(R.id.comment_add_tv_time)
	TextView tv_time;

	@BindView(R.id.comment_add_tv_content)
	TextView tv_content;

	@BindView(R.id.comment_add_view_hsv)
	HorizontalScrollView sv_main;

	@BindView(R.id.comment_add_hsv_ll_main)
	LinearLayout ll_main;

	@BindView(R.id.comment_add_et_add_comment)
	EditText et_add_comment;

	@BindView(R.id.comment_add_tv_post)
	TextView tv_post;

	RelativeLayout.LayoutParams goodsImgLP;

	private CommentEntity data;
	private boolean isPostOk = false;
	private String orderNo, goodsCode, skuCode, skuComboName, contentStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_add);

		data = (CommentEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

		initView();
	}

	private void initView() {
		setTitle(R.string.comment_add);

		tv_post.setOnClickListener(this);

		int imageSize = CommonTools.dpToPx(mContext, 60);
		goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		goodsImgLP.setMargins(0, 0, 10, 0);
		goodsImgLP.width = imageSize;
		goodsImgLP.height = imageSize;

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

			// 评论图片
			if (data.isImg()) {
				sv_main.setVisibility(View.VISIBLE);
				initImageView(ll_main, data.getImgList());
			} else {
				ll_main.removeAllViews();
				sv_main.setVisibility(View.GONE);
			}

			tv_time.setText(data.getAddTime());
			rb_star.setRating(data.getStarNum());
			tv_content.setText(data.getContent());

			if (StringUtil.isNull(data.getContent())) {
				loadServerData();
			}
		}
	}

	private void initImageView(LinearLayout ll_main, ArrayList<String> imgList) {
		if (ll_main == null || imgList == null || imgList.size() <= 0) return;
		ll_main.removeAllViews();

		int imgCount = imgList.size();
		for (int i = 0; i < imgCount; i++) {
			final String imgUrl = imgList.get(i);
			RoundImageView iv_img = new RoundImageView(mContext);
			iv_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
			iv_img.setLayoutParams(goodsImgLP);

			Glide.with(AppApplication.getAppContext())
					.load(imgUrl)
					.apply(AppApplication.getShowOptions())
					.into(iv_img);

			ll_main.addView(iv_img);
		}
	}

	private boolean checkData() {
		contentStr = et_add_comment.getText().toString();
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
			case R.id.comment_add_tv_post:
				if (isPostOk) return;
				if (checkData()) {
					postData();
				}
				break;
		}
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

	@Override
	public void finish() {
		if (isPostOk) {
			setResult(RESULT_OK, new Intent());
		}
		super.finish();
	}

	/**
	 * 加载数据
	 */
	private void loadServerData() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("sourceType", AppConfig.APP_TYPE);
		map.put("orderNo", orderNo);
		if (!StringUtil.isNull(skuCode)) {
			map.put("skuCode", skuCode);
		} else {
			map.put("goodsCode", goodsCode);
		}
		loadSVData(AppConfig.URL_COMMENT_GET, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_COMMENT_GET);
	}

	/**
	 * 发布追评
	 */
	private void postData() {
		if (data == null || data.getId() <= 0) return;
		HashMap<String, Object> map = new HashMap<>();
		map.put("appendId", data.getId());
		map.put("appendContext", contentStr);
		loadSVData(AppConfig.URL_COMMENT_POST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_COMMENT_POST);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity<CommentEntity> baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_COMMENT_GET:
					baseEn = JsonUtils.getCommentInfoData(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						CommentEntity loadData = baseEn.getData();
						if (loadData != null) {
							data.setId(loadData.getId());
							data.setContent(loadData.getContent());
							data.setAddTime(loadData.getAddTime());
							data.setStarNum(loadData.getStarNum());
							data.setImg(loadData.isImg());
							data.setImgList(loadData.getImgList());
							data.setAddDay(loadData.getAddDay());
							data.setAddContent(loadData.getAddContent());
						}
						initView();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_COMMENT_POST:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						isPostOk = true;
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
