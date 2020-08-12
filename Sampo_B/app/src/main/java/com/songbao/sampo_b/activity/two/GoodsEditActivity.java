package com.songbao.sampo_b.activity.two;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.OrderFilesAdapter;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.FileManager;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.widgets.RoundImageView;
import com.songbao.sampo_b.widgets.ScrollViewEditText;
import com.songbao.sampo_b.widgets.ScrollViewListView;

import java.util.ArrayList;

import butterknife.BindView;


public class GoodsEditActivity extends BaseActivity implements OnClickListener {

    String TAG = GoodsEditActivity.class.getSimpleName();

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

    @BindView(R.id.goods_edit_lv_file)
    ScrollViewListView lv_files;

    @BindView(R.id.goods_edit_tv_file_add)
    TextView tv_file_add;

    @BindView(R.id.goods_edit_tv_link_null)
    TextView tv_link_null;

    @BindView(R.id.goods_edit_et_effect_link)
    EditText et_link;

    @BindView(R.id.goods_edit_et_goods_name)
    EditText et_name;

    @BindView(R.id.goods_edit_et_goods_number)
    EditText et_number;

    @BindView(R.id.goods_edit_et_price_cost)
    EditText et_price_cost;

    @BindView(R.id.goods_edit_et_price_sale)
    EditText et_price_sale;

    @BindView(R.id.goods_edit_et_goods_remarks)
    ScrollViewEditText et_remarks;

    @BindView(R.id.goods_edit_tv_goods_remarks_byte)
    TextView tv_remarks_byte;

    @BindView(R.id.goods_edit_tv_confirm)
    TextView tv_confirm;

    private OrderFilesAdapter ap_files;
    private RequestOptions showOptions;
    private GoodsEntity data;
    private int editPos, photoNum, goodsNum;
    private double costPrice, salePrice;
    private boolean isEdit, isNull, isSale, isCost;
    private String linkStr, name, remarks;
    private ArrayList<String> al_photos_add = new ArrayList<>();
    private ArrayList<String> al_photos_url = new ArrayList<>();
    private ArrayList<String> al_files_url = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_edit);

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        editPos = getIntent().getIntExtra("editPos", 0);
        data = (GoodsEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

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
        tv_file_add.setOnClickListener(this);
        tv_link_null.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        if (isHideCostPrice) { //隐藏成本单价
            et_price_cost.setVisibility(View.GONE);
        }

        showOptions = new RequestOptions()
                .placeholder(R.drawable.icon_default_show)
                .fallback(R.drawable.icon_photo_add)
                .error(R.drawable.icon_default_show);

        goodsNum = 1;
        if (data != null) {
            if (data.getImageList() != null) {
                al_photos_url.addAll(data.getImageList());
            }
            if (data.getFilesList() != null) {
                al_files_url.addAll(data.getFilesList());
            }

            linkStr = data.getEffectUrl();
            isNull = data.isPicture();
            updateLinkStatus();

            name = data.getName();
            remarks = data.getRemarks();
            if (data.getNumber() > 0) {
                goodsNum = data.getNumber();
            }
            costPrice = data.getCostPrice();
            salePrice = data.getSalePrice();
        }
        et_link.setText(linkStr);
        et_name.setText(name);
        et_number.setText(String.valueOf(goodsNum));

        if (costPrice > 0) {
            et_price_cost.setText(df.format(costPrice));
        }
        if (salePrice > 0) {
            et_price_sale.setText(df.format(salePrice));
        }
        et_price_cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isCost = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isCost && !isSale) {
                    if (StringUtil.isNull(s.toString())) {
                        costPrice = 0;
                        salePrice = 0;
                        et_price_sale.setText("");
                    } else {
                        costPrice = Double.valueOf(s.toString());
                        salePrice = costPrice * ratios;
                        et_price_sale.setText(df.format(salePrice));
                    }
                }
                isSale = false;
            }
        });
        et_price_sale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isSale = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isSale && !isCost) {
                    if (StringUtil.isNull(s.toString())) {
                        salePrice = 0;
                        costPrice = 0;
                        et_price_cost.setText("");
                    } else {
                        salePrice = Double.valueOf(s.toString());
                        costPrice = salePrice / ratios;
                        et_price_cost.setText(df.format(costPrice));
                    }
                }
                isCost = false;
            }
        });

        updatePhotoData();
        initFilesListView();

        et_remarks.setText(remarks);
        et_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTextBytes(s.length());
            }
        });
        if (StringUtil.isNull(remarks)) {
            updateTextBytes(0);
        } else {
            updateTextBytes(remarks.length());
        }
    }

    private void updateTextBytes(int lengths) {
        tv_remarks_byte.setText(getString(R.string.viewpager_indicator, lengths, 200));
    }

    private void updatePhotoData() {
        al_photos_add.clear();
        al_photos_add.addAll(al_photos_url);
        photoNum = al_photos_url.size();
        if (photoNum < 9) {
            al_photos_add.add(null);
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

    private void initFilesListView() {
        if (ap_files == null) {
            ap_files = new OrderFilesAdapter(mContext);
            ap_files.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    if (position < 0 || position >= al_files_url.size()) return;
                    al_files_url.remove(position);
                    initFilesListView();
                }
            });
        }
        ap_files.updateData(al_files_url);
        lv_files.setAdapter(ap_files);
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
            case R.id.goods_edit_tv_file_add:
                openSystemFile();
                break;
            case R.id.goods_edit_tv_link_null:
                isNull = !isNull;
                updateLinkStatus();
                break;
            case R.id.goods_edit_tv_confirm:
                if (checkData()) {
                    postData();
                }
                break;
        }
    }

    /**
     * 刷新产品链接状态
     */
    private void updateLinkStatus() {
        tv_link_null.setSelected(isNull);
        if (isNull) {
            et_link.setVisibility(View.GONE);
        } else {
            et_link.setVisibility(View.VISIBLE);
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
        } else { //查看相片
            Intent intent = new Intent(mContext, PhotoShowActivity.class);
            intent.putExtra(PhotoShowActivity.EXTRA_IMAGE_URLS, al_photos_url);
            intent.putExtra(PhotoShowActivity.EXTRA_IMAGE_INDEX, position);
            startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PHOTO_DELETE);
        }
    }

    /**
     * 数据格式校验
     */
    private boolean checkData() {
        if (al_photos_url.size() <= 0) {
            CommonTools.showToast(getString(R.string.goods_effect_img_hint), Toast.LENGTH_SHORT);
            return false;
        }
        linkStr = et_link.getText().toString();
        if (!isNull && StringUtil.isNull(linkStr)) {
            CommonTools.showToast(getString(R.string.goods_effect_link_hint), Toast.LENGTH_SHORT);
            return false;
        }
        name = et_name.getText().toString();
        if (StringUtil.isNull(name)) {
            CommonTools.showToast(getString(R.string.goods_name_hint), Toast.LENGTH_SHORT);
            return false;
        }
        String numStr = et_number.getText().toString();
        if (StringUtil.isNull(numStr) || Integer.valueOf(numStr) <= 0) {
            CommonTools.showToast(getString(R.string.goods_number_hint), Toast.LENGTH_SHORT);
            return false;
        }
        goodsNum = Integer.valueOf(numStr);

        if (isHideCostPrice) {
            if (salePrice <= 0 || costPrice <= 0) {
                CommonTools.showToast(getString(R.string.goods_price_sale_hint), Toast.LENGTH_SHORT);
                return false;
            }
        } else {
            if (costPrice <= 0) {
                CommonTools.showToast(getString(R.string.goods_price_cost_hint), Toast.LENGTH_SHORT);
                return false;
            }
            if (salePrice <= 0) {
                CommonTools.showToast(getString(R.string.goods_price_sale_hint), Toast.LENGTH_SHORT);
                return false;
            }
        }

        remarks = et_remarks.getText().toString();

        return true;
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
        GoodsEntity goodsEn = new GoodsEntity();
        goodsEn.setFilesList(al_files_url);
        goodsEn.setImageList(al_photos_url);
        goodsEn.setPicUrl(al_photos_url.get(0));
        goodsEn.setPicture(isNull);
        if (!isNull) {
            goodsEn.setEffectUrl(linkStr);
        }
        goodsEn.setName(name);
        goodsEn.setNumber(goodsNum);
        goodsEn.setCostPrice(costPrice);
        goodsEn.setSalePrice(salePrice);
        goodsEn.setRemarks(remarks);

        OCustomizeEntity ocEn = (OCustomizeEntity) FileManager.readFileSaveObject(AppConfig.orderDataFileName, 0);
        if (ocEn != null && ocEn.getGoodsList() != null) {
            if (isEdit && editPos >=0 && editPos < ocEn.getGoodsList().size()) {
                ocEn.getGoodsList().get(editPos).setFilesList(al_files_url);
                ocEn.getGoodsList().get(editPos).setImageList(al_photos_url);
                ocEn.getGoodsList().get(editPos).setPicUrl(al_photos_url.get(0));
                ocEn.getGoodsList().get(editPos).setPicture(isNull);
                if (!isNull) {
                    ocEn.getGoodsList().get(editPos).setEffectUrl(linkStr);
                }
                ocEn.getGoodsList().get(editPos).setName(name);
                ocEn.getGoodsList().get(editPos).setNumber(goodsNum);
                ocEn.getGoodsList().get(editPos).setCostPrice(costPrice);
                ocEn.getGoodsList().get(editPos).setSalePrice(salePrice);
                ocEn.getGoodsList().get(editPos).setRemarks(remarks);
            } else {
                ocEn.getGoodsList().add(goodsEn);
            }
        } else {
            ocEn = new OCustomizeEntity();
            ArrayList<GoodsEntity> goodsList = new ArrayList<>();
            goodsList.add(goodsEn);
            ocEn.setGoodsList(goodsList);
        }
        FileManager.writeFileSaveObject(AppConfig.orderDataFileName, ocEn, 0);
        openActivity(PostOrderActivity.class);
        finish();
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
            }else if (requestCode == 1) { //从系统文件管理器选择文件
                if(data.getClipData() != null) { //选择多个文件
                    int count = data.getClipData().getItemCount();
                    LogUtil.i(TAG,"url count ："+ count);
                    int currentItem = 0;
                    while(currentItem < count) {
                        Uri fileUri = data.getClipData().getItemAt(currentItem).getUri();
                        String filePath = FileManager.getPathFromUri(mContext, fileUri);
                        if (filePath.contains(".dwg") || filePath.contains(".pdf")) {
                            al_files_url.add(filePath);
                        } else {
                            CommonTools.showToast("请上传.dwg或.pdf格式文件");
                        }
                        LogUtil.i(TAG,"url "+ filePath);
                        currentItem = currentItem + 1;
                    }
                    initFilesListView();
                } else if(data.getData() != null) { //选择一个文件
                    String filePath = FileManager.getPathFromUri(mContext, data.getData());
                    if (filePath.contains(".dwg") || filePath.contains(".pdf")) {
                        al_files_url.add(filePath);
                    } else {
                        CommonTools.showToast("请上传.dwg或.pdf格式文件");
                    }
                    LogUtil.i(TAG,"Single image path ---- " + filePath);
                    initFilesListView();
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
