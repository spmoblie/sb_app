package com.songbao.sampo.activity.mine;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.activity.common.clip.ClipPhotoGridActivity;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.entity.GoodsSaleEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.StringUtil;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.songbao.sampo.widgets.RoundImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class PostSaleActivity extends BaseActivity implements OnClickListener {

	String TAG = PostSaleActivity.class.getSimpleName();
	
	@BindView(R.id.post_sale_goods_main)
	ConstraintLayout goods_main;

	@BindView(R.id.post_sale_iv_goods)
	RoundImageView iv_goods;

	@BindView(R.id.post_sale_tv_name)
	TextView tv_name;

	@BindView(R.id.post_sale_tv_attr)
	TextView tv_attr;
	
	@BindView(R.id.post_sale_tv_number)
	TextView tv_number;
	
	@BindView(R.id.post_sale_tv_price)
	TextView tv_price;
	
	@BindView(R.id.post_sale_tv_change)
	TextView tv_change;
	
	@BindView(R.id.post_sale_tv_return)
	TextView tv_return;
	
	@BindView(R.id.post_sale_et_reason)
	EditText et_reason;

	@BindView(R.id.post_sale_tv_refund_price)
	TextView tv_refund_price;
	
	@BindView(R.id.post_sale_et_express_no)
	EditText et_express_no;

	@BindView(R.id.post_sale_group_express_no)
	Group group_express_no;

	@BindView(R.id.post_sale_iv_photo_01)
	RoundImageView iv_photo_01;

	@BindView(R.id.post_sale_iv_photo_02)
	RoundImageView iv_photo_02;

	@BindView(R.id.post_sale_iv_photo_03)
	RoundImageView iv_photo_03;

	@BindView(R.id.post_sale_iv_photo_01_delete)
	ImageView iv_photo_01_delete;

	@BindView(R.id.post_sale_iv_photo_02_delete)
	ImageView iv_photo_02_delete;

	@BindView(R.id.post_sale_iv_photo_03_delete)
	ImageView iv_photo_03_delete;

	@BindView(R.id.post_sale_tv_add_photo)
	TextView tv_add_photo;

	@BindView(R.id.post_sale_tv_submit)
	TextView tv_submit;

	private GoodsEntity data;
	private GoodsSaleEntity saleEn;
	private int saleType = 1; //1:换货/2：退货
	private int saleStatus = 0; //6:售后(审核中)/7:售后(审核通过)/8:售后(审核拒绝)
	private double totalPrice = 0;
	private boolean isEdit = false;
	private String goodsCode = "";
	private String contentStr = "";
	private ArrayList<String> al_photo_url = new ArrayList<>();
	private ArrayList<String> al_image_url = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_sale);

		data = (GoodsEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

		initView();
	}

	private void initView() {
		setTitle(R.string.order_post_sale);

		tv_change.setOnClickListener(this);
		tv_return.setOnClickListener(this);
		iv_photo_01.setOnClickListener(this);
		iv_photo_02.setOnClickListener(this);
		iv_photo_03.setOnClickListener(this);
		iv_photo_01_delete.setOnClickListener(this);
		iv_photo_02_delete.setOnClickListener(this);
		iv_photo_03_delete.setOnClickListener(this);
		tv_add_photo.setOnClickListener(this);
		tv_submit.setOnClickListener(this);

		if (data != null) {
			Glide.with(AppApplication.getAppContext())
					.load(data.getPicUrl())
					.apply(AppApplication.getShowOptions())
					.into(iv_goods);

			tv_name.setText(data.getName());
			tv_attr.setText(data.getAttribute());

			goodsCode = data.getGoodsCode();
			int number = data.getNumber();
			double price = data.getPrice();
			totalPrice = price*number;
			tv_number.setText(getString(R.string.cart_goods_num, number));
			tv_price.setText(getString(R.string.pay_rmb, df.format(price)));
		}

		initShowView();
		loadSaleData();
	}

	private void initShowView() {
		isEdit = false;
		if (saleEn != null) {
			saleType = saleEn.getSaleType();
			saleStatus = saleEn.getSaleStatus();
			switch (saleStatus) {
				case 6: //审核中
					tv_submit.setText("审核中");
					tv_submit.setBackgroundResource(R.drawable.shape_style_solid_03_08);
					break;
				case 7: //审核通过
					group_express_no.setVisibility(View.VISIBLE);
					break;
				case 8: //审核拒绝
					break;
				default:
					isEdit = true;
					break;
			}

			if (!StringUtil.isNull(saleEn.getSaleReason())) {
				et_reason.setText(saleEn.getSaleReason());
				et_reason.setFocusable(false);
				et_reason.setFocusableInTouchMode(false);
			}
			if (!StringUtil.isNull(saleEn.getExpressNo())) {
				et_express_no.setText(saleEn.getExpressNo());
				et_express_no.setFocusable(false);
				et_express_no.setFocusableInTouchMode(false);
			}
			if (saleEn.getImgList() != null && saleEn.getImgList().size() > 0) {
				al_photo_url.clear();
				al_photo_url.addAll(saleEn.getImgList());
			}
		}
		initPhotoView();
		changeViewStatus();
		if (!isEdit) {
			tv_add_photo.setVisibility(View.GONE);
			iv_photo_01_delete.setVisibility(View.GONE);
			iv_photo_02_delete.setVisibility(View.GONE);
			iv_photo_03_delete.setVisibility(View.GONE);
		}
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
		contentStr = et_reason.getText().toString();
		// 校验非空
		if (StringUtil.isNull(contentStr)) {
			CommonTools.showToast(getString(R.string.order_post_sale_hint), Toast.LENGTH_SHORT);
			return false;
		}
		return true;
	}

	/**
	 * 退、换货状态切换
	 */
	private void changeViewStatus() {
		tv_change.setBackgroundResource(R.color.ui_bg_color_percent_10);
		tv_return.setBackgroundResource(R.color.ui_bg_color_percent_10);
		tv_change.setTextColor(getResources().getColor(R.color.app_color_gray_5));
		tv_return.setTextColor(getResources().getColor(R.color.app_color_gray_5));
		if (saleType == 1) {
			tv_change.setBackgroundResource(R.drawable.shape_style_solid_04_08);
			tv_change.setTextColor(getResources().getColor(R.color.app_color_white));
			tv_refund_price.setText(getString(R.string.order_refund_price, df.format(0)));
		} else {
			tv_return.setBackgroundResource(R.drawable.shape_style_solid_04_08);
			tv_return.setTextColor(getResources().getColor(R.color.app_color_white));
			tv_refund_price.setText(getString(R.string.order_refund_price, df.format(totalPrice)));
		}
	}

	@Override
	public void onClick(View v) {
		if (saleEn == null) { //加载失败
			loadSaleData();
			return;
		}
		if (!isEdit) return; //不可编辑
		switch (v.getId()) {
		case R.id.post_sale_tv_change:
			saleType = 1;
			changeViewStatus();
			break;
		case R.id.post_sale_tv_return:
			saleType = 2;
			changeViewStatus();
			break;
		case R.id.post_sale_iv_photo_01:
		case R.id.post_sale_iv_photo_01_delete:
			if (al_photo_url.size() > 0) {
				al_photo_url.remove(0);
				initPhotoView();
			}
			break;
		case R.id.post_sale_iv_photo_02:
		case R.id.post_sale_iv_photo_02_delete:
			if (al_photo_url.size() > 1) {
				al_photo_url.remove(1);
				initPhotoView();
			}
			break;
		case R.id.post_sale_iv_photo_03:
		case R.id.post_sale_iv_photo_03_delete:
			if (al_photo_url.size() > 2) {
				al_photo_url.remove(2);
				initPhotoView();
			}
			break;
		case R.id.post_sale_tv_add_photo:
			openActivity(ClipPhotoGridActivity.class);
			break;
		case R.id.post_sale_tv_submit:
			if (checkData()) {
				//checkPhotoUrl();
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
			uploadPushFile(new File(photoUrl), 2, AppConfig.REQUEST_SV_UPLOAD_COMMENT_PHOTO);
		}
	}

	private void postData() {
		startAnimation();
		if (al_image_url.size() > 0) {

		}
	}

	private void loadSaleData() {
		HashMap<String, String> map = new HashMap<>();
		map.put("GoodsCode", goodsCode);
		loadSVData(AppConfig.URL_USER_MESSAGE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_GOODS_SALE);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_UPLOAD_COMMENT_PHOTO:
					baseEn = JsonUtils.getUploadResult(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						al_image_url.add(baseEn.getOthers());
						checkPhotoUrl();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_GOODS_SALE:
					baseEn = JsonUtils.getGoodsSaleData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						saleEn = (GoodsSaleEntity) baseEn.getData();
						initShowView();
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
