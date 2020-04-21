package com.songbao.sampo_b.activity.two;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.activity.common.clip.ClipPhotoAllActivity;
import com.songbao.sampo_b.activity.mine.CommentPostActivity;
import com.songbao.sampo_b.adapter.PhotoGridAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.CommentEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.widgets.ScrollViewGridView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class GoodsEditActivity extends BaseActivity implements OnClickListener {

	String TAG = CommentPostActivity.class.getSimpleName();

	@BindView(R.id.goods_edit_gv_photo)
	ScrollViewGridView gv_add_photo;

	@BindView(R.id.goods_edit_et_effect_link)
	EditText et_link;

	@BindView(R.id.goods_edit_et_goods_name)
	EditText et_name;

	@BindView(R.id.goods_edit_et_goods_number)
	EditText et_number;

	@BindView(R.id.goods_edit_et_goods_price)
	EditText et_price;

	@BindView(R.id.goods_edit_et_goods_remarks)
	EditText et_remarks;

	@BindView(R.id.goods_edit_tv_goods_remarks_byte)
    TextView tv_remarks_byte;

	@BindView(R.id.goods_edit_tv_confirm)
    TextView tv_confirm;

	private CommentEntity data;
	private boolean isPostOk = false;
	private String orderNo, goodsCode, skuCode, skuComboName, contentStr;
	private ArrayList<String> al_photos_url = new ArrayList<>();
	private ArrayList<String> al_upload_url = new ArrayList<>();
	private ArrayList<String> al_images_url = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_edit);

		data = (CommentEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

		initView();
	}

	private void initView() {
		setTitle(R.string.goods_edit);

		tv_confirm.setOnClickListener(this);
	}

	private void initPhotoView() {
		al_photos_url.clear();
		al_photos_url.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1401-67985a0f46c54c09af83849a7c0870e6.jpg");
		al_photos_url.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1401-67985a0f46c54c09af83849a7c0870e6.jpg");
		al_photos_url.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1401-67985a0f46c54c09af83849a7c0870e6.jpg");
		al_photos_url.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1401-67985a0f46c54c09af83849a7c0870e6.jpg");
		al_photos_url.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1401-67985a0f46c54c09af83849a7c0870e6.jpg");
		al_photos_url.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1401-67985a0f46c54c09af83849a7c0870e6.jpg");
		al_photos_url.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1401-67985a0f46c54c09af83849a7c0870e6.jpg");
		al_photos_url.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-01-2.3.1401-67985a0f46c54c09af83849a7c0870e6.jpg");
		al_photos_url.add("add");
		gv_add_photo.setAdapter(new PhotoGridAdapter(mContext, al_photos_url));
		gv_add_photo.setSelector(R.color.ui_color_app_bg_01);
		gv_add_photo.setOnItemClickListener(gvItemClickListener);
	}

	private boolean checkData() {
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
			case R.id.goods_edit_tv_confirm:
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

	private OnItemClickListener gvItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			openActivity(ClipPhotoAllActivity.class);
		}
	};

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
		map.put("evaluateContent", contentStr);
		/*String images = StringUtil.listToSplitStr(al_images_url);
		if (!StringUtil.isNull(images)) {
			map.put("evaluateImagesStr", images);
		}*/
		//loadSVData(AppConfig.URL_COMMENT_POST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_COMMENT_POST);
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
