package com.songbao.sampo_b.activity.two;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.activity.common.photo.PhotoAllActivity;
import com.songbao.sampo_b.activity.common.photo.PhotoShowActivity;
import com.songbao.sampo_b.activity.mine.CommentPostActivity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.CommentEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.widgets.RoundImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class GoodsEditActivity extends BaseActivity implements OnClickListener {

    String TAG = CommentPostActivity.class.getSimpleName();

    @BindView(R.id.goods_edit_iv_photo_01)
    RoundImageView iv_photo_01;

    @BindView(R.id.goods_edit_iv_photo_02)
    RoundImageView iv_photo_02;

    @BindView(R.id.goods_edit_iv_photo_03)
    RoundImageView iv_photo_03;

    @BindView(R.id.goods_edit_iv_photo_04)
    RoundImageView iv_photo_04;

    @BindView(R.id.goods_edit_iv_photo_05)
    RoundImageView iv_photo_05;

    @BindView(R.id.goods_edit_iv_photo_06)
    RoundImageView iv_photo_06;

    @BindView(R.id.goods_edit_iv_photo_07)
    RoundImageView iv_photo_07;

    @BindView(R.id.goods_edit_iv_photo_08)
    RoundImageView iv_photo_08;

    @BindView(R.id.goods_edit_iv_photo_09)
    RoundImageView iv_photo_09;

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

    private RequestOptions showOptions;
    private CommentEntity data;
    private int photoNum;
    private boolean isPostOk = false;
    private String orderNo, goodsCode, skuCode, skuComboName, contentStr;
    private ArrayList<String> al_photos_add = new ArrayList<>();
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
        iv_photo_01.setOnClickListener(this);
        iv_photo_02.setOnClickListener(this);
        iv_photo_03.setOnClickListener(this);
        iv_photo_04.setOnClickListener(this);
        iv_photo_05.setOnClickListener(this);
        iv_photo_06.setOnClickListener(this);
        iv_photo_07.setOnClickListener(this);
        iv_photo_08.setOnClickListener(this);
        iv_photo_09.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        showOptions = new RequestOptions()
                .placeholder(R.drawable.icon_photo_add)
                .fallback(R.drawable.icon_photo_add)
                .error(R.drawable.icon_photo_add);

        updatePhotoData();
    }

    private void updatePhotoData() {
        al_photos_add.clear();
        al_photos_add.addAll(al_photos_url);
        photoNum = al_photos_url.size();
        if (photoNum < 9) {
            al_photos_add.add("add");
        }
        initPhotoView();
    }

    private void initPhotoView() {
        iv_photo_01.setVisibility(View.GONE);
        iv_photo_02.setVisibility(View.GONE);
        iv_photo_03.setVisibility(View.GONE);
        iv_photo_04.setVisibility(View.GONE);
        iv_photo_05.setVisibility(View.GONE);
        iv_photo_06.setVisibility(View.GONE);
        iv_photo_07.setVisibility(View.GONE);
        iv_photo_08.setVisibility(View.GONE);
        iv_photo_09.setVisibility(View.GONE);
        for (int i = 0; i < al_photos_add.size(); i++) {
            switch (i) {
                case 0:
                    showPhotoView(al_photos_add.get(i), iv_photo_01);
                    break;
                case 1:
                    showPhotoView(al_photos_add.get(i), iv_photo_02);
                    break;
                case 2:
                    showPhotoView(al_photos_add.get(i), iv_photo_03);
                    break;
                case 3:
                    showPhotoView(al_photos_add.get(i), iv_photo_04);
                    break;
                case 4:
                    showPhotoView(al_photos_add.get(i), iv_photo_05);
                    break;
                case 5:
                    showPhotoView(al_photos_add.get(i), iv_photo_06);
                    break;
                case 6:
                    showPhotoView(al_photos_add.get(i), iv_photo_07);
                    break;
                case 7:
                    showPhotoView(al_photos_add.get(i), iv_photo_08);
                    break;
                case 8:
                    showPhotoView(al_photos_add.get(i), iv_photo_09);
                    break;
            }
        }
    }

    private void showPhotoView(String photoPath, RoundImageView iv_photo) {
        Glide.with(AppApplication.getAppContext())
                .load(photoPath)
                .apply(showOptions)
                .into(iv_photo);

        iv_photo.setVisibility(View.VISIBLE);
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
        if (ClickUtils.isDoubleClick(v.getId())) return;
        switch (v.getId()) {
            case R.id.goods_edit_iv_photo_01:
                handlePhotoClick(0);
                break;
            case R.id.goods_edit_iv_photo_02:
                handlePhotoClick(1);
                break;
            case R.id.goods_edit_iv_photo_03:
                handlePhotoClick(2);
                break;
            case R.id.goods_edit_iv_photo_04:
                handlePhotoClick(3);
                break;
            case R.id.goods_edit_iv_photo_05:
                handlePhotoClick(4);
                break;
            case R.id.goods_edit_iv_photo_06:
                handlePhotoClick(5);
                break;
            case R.id.goods_edit_iv_photo_07:
                handlePhotoClick(6);
                break;
            case R.id.goods_edit_iv_photo_08:
                handlePhotoClick(7);
                break;
            case R.id.goods_edit_iv_photo_09:
                handlePhotoClick(8);
                break;
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

    /**
     * 处理相片点击事件
     */
    private void handlePhotoClick(int position) {
        if (photoNum < 9 && position == al_photos_add.size() - 1) { //添加相片
            Intent intent = new Intent(mContext, PhotoAllActivity.class);
            intent.putExtra("totalNum", 9 - photoNum);
            startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PHOTO_SELECT);
        } else { //删除相片
            Intent intent = new Intent(mContext, PhotoShowActivity.class);
            intent.putExtra(PhotoShowActivity.EXTRA_IMAGE_URLS, al_photos_url);
            intent.putExtra(PhotoShowActivity.EXTRA_IMAGE_INDEX, position);
            startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PHOTO_DELETE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConfig.ACTIVITY_CODE_PHOTO_SELECT) {
                ArrayList<String> pathList = data.getStringArrayListExtra(AppConfig.PAGE_DATA);
                if (pathList != null) {
                    al_photos_url.addAll(pathList);
                    updatePhotoData();
                }
            } else if (requestCode == AppConfig.ACTIVITY_CODE_PHOTO_DELETE) {
                ArrayList<String> pathList = data.getStringArrayListExtra(AppConfig.PAGE_DATA);
                if (pathList != null) {
                    al_photos_url.clear();
                    al_photos_url.addAll(pathList);
                    updatePhotoData();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
