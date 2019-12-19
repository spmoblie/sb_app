package com.songbao.sampo.activity.mine;

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
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.entity.CommentEntity;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.StringUtil;
import com.songbao.sampo.widgets.RoundImageView;

import java.util.ArrayList;

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

	private String contentStr;
	private CommentEntity data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_add);

		data = (CommentEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

		initView();
	}

	private void initView() {
		setTitle(R.string.order_comment_add);

		tv_post.setOnClickListener(this);

		int imageSize = CommonTools.dpToPx(mContext, 60);
		goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		goodsImgLP.setMargins(0, 0, 10, 0);
		goodsImgLP.width = imageSize;
		goodsImgLP.height = imageSize;

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

			tv_time.setText(data.getAddTime());
			rb_star.setRating(data.getStarNum());
			tv_content.setText(data.getContent());

			// 评论图片
			if (data.getType() == 1) {
				sv_main.setVisibility(View.VISIBLE);
				initImageView(ll_main, data.getImgList());
			} else {
				ll_main.removeAllViews();
				sv_main.setVisibility(View.GONE);
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
			CommonTools.showToast(getString(R.string.order_comment_null), Toast.LENGTH_SHORT);
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_add_tv_post:
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

	private void postData() {
		startAnimation();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				CommonTools.showToast("发布成功，待审核~");
				stopAnimation();
			}
		}, AppConfig.LOADING_TIME);
	}
	
}
