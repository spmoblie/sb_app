package com.songbao.sampo_b.activity.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.constraint.Group;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.activity.common.FileActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.OrderFilesAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.utils.BitmapUtil;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.QRCodeUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.RoundImageView;
import com.songbao.sampo_b.widgets.ScrollViewListView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class CustomizeGoodsActivity extends BaseActivity implements OnClickListener {

    String TAG = CustomizeGoodsActivity.class.getSimpleName();

    @BindView(R.id.customize_goods_view_sv_image)
    HorizontalScrollView sv_image;

    @BindView(R.id.customize_goods_ll_sv_main)
    LinearLayout ll_sv_main;

    @BindView(R.id.customize_goods_view_lv_files)
    ScrollViewListView lv_files;

    @BindView(R.id.customize_goods_group_files)
    Group group_files;

    @BindView(R.id.customize_goods_iv_link)
    ImageView iv_link;

    @BindView(R.id.customize_goods_tv_effect_url)
    TextView tv_url;

    @BindView(R.id.customize_goods_tv_effect_check)
    TextView tv_url_check;

    @BindView(R.id.customize_goods_tv_effect_copy)
    TextView tv_url_copy;

    @BindView(R.id.customize_goods_group_url)
    Group group_url;

    @BindView(R.id.customize_goods_tv_name_show)
    TextView tv_name;

    @BindView(R.id.customize_goods_tv_number)
    TextView tv_number;

    @BindView(R.id.customize_goods_tv_price_one)
    TextView tv_price_one;

    @BindView(R.id.customize_goods_tv_price_two)
    TextView tv_price_two;

    @BindView(R.id.customize_goods_tv_remarks)
    TextView tv_remarks;

    @BindView(R.id.customize_goods_tv_remarks_show)
    TextView tv_remarks_show;

    OrderFilesAdapter ap_files;
    RelativeLayout.LayoutParams goodsImgLP;

    private GoodsEntity goodsEn;
    private Bitmap qrImage;
    private String effectUrl;
    private ArrayList<String> al_image = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_goods);

        goodsEn = (GoodsEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.order_goods_detail));

        int imageSize = (screenWidth - CommonTools.dpToPx(mContext, 64)) / 3;
        goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        goodsImgLP.setMargins(0, 0, 10, 0);
        goodsImgLP.width = imageSize;
        goodsImgLP.height = imageSize;

        if (goodsEn != null) {
            loadCustomizeData();
        }
    }

    private void initShowData() {
        if (goodsEn != null) {
            // 产品图片
            al_image.clear();
            al_image.addAll(goodsEn.getImageList());
            initImageScrollView();
            // 产品文件
            if (goodsEn.getLabelList() != null && goodsEn.getLabelList().size() > 0) {
                initFilesListView(goodsEn.getLabelList());
                group_files.setVisibility(View.VISIBLE);
            } else {
                group_files.setVisibility(View.GONE);
            }
            // 产品链接
            effectUrl = goodsEn.getEffectUrl();
            if (!StringUtil.isNull(effectUrl) && (effectUrl.contains("http://") || effectUrl.contains("https://"))) {
                tv_url.setText(effectUrl);
                tv_url_copy.setOnClickListener(this);
                tv_url_check.setOnClickListener(this);

                final String imgName = "QR_" + System.currentTimeMillis() + ".png";
                int imgSize = AppApplication.screen_width;
                qrImage = QRCodeUtil.createQRImage(goodsEn.getEffectUrl(), imgSize, imgSize, 1, null);
                iv_link.setImageBitmap(BitmapUtil.getBitmap(qrImage, 645, 645));
                iv_link.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        File file = BitmapUtil.createPath(imgName, true);
                        if (file == null) {
                            showErrorDialog(R.string.photo_show_save_fail);
                            return false;
                        }
                        AppApplication.saveBitmapFile(qrImage, file, 100);
                        CommonTools.showToast(getString(R.string.photo_show_save_ok));
                        return false;
                    }
                });
                group_url.setVisibility(View.VISIBLE);
            } else {
                group_url.setVisibility(View.GONE);
            }
            // 产品信息
            tv_name.setText(goodsEn.getName());
            tv_number.setText(getString(R.string.goods_number_show, goodsEn.getNumber()));
            tv_price_one.setText(getString(R.string.order_rmb, df.format(goodsEn.getCostPrice())));
            if (goodsEn.getCostPricing() > 0 && goodsEn.getCostPricing() != goodsEn.getCostPrice()) {
                tv_price_one.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                tv_price_one.setTextColor(getResources().getColor(R.color.debar_text_color));
                tv_price_two.setText(getString(R.string.order_rmb, df.format(goodsEn.getCostPricing())));
            }
            if (!StringUtil.isNull(goodsEn.getRemarks())) {
                tv_remarks.setVisibility(View.VISIBLE);
                tv_remarks_show.setVisibility(View.VISIBLE);
                tv_remarks_show.setText(goodsEn.getRemarks());
            }
        }
    }

    private void initImageScrollView() {
        ll_sv_main.removeAllViews();
        int imgCount = al_image.size();
        for (int i = 0; i < imgCount; i++) {
            final int position = i;
            final String imgUrl = al_image.get(i);
            RoundImageView iv_img = new RoundImageView(mContext);
            iv_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == imgCount - 1) {
                goodsImgLP.setMargins(0, 0, 0, 0);
            }
            iv_img.setLayoutParams(goodsImgLP);

            iv_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openViewPagerActivity(al_image, position);
                }
            });

            Glide.with(AppApplication.getAppContext())
                    .load(imgUrl)
                    .apply(AppApplication.getShowOptions())
                    .into(iv_img);

            ll_sv_main.addView(iv_img);
        }
    }

    private void initFilesListView(final ArrayList<String> filesList) {
        if (ap_files == null) {
            ap_files = new OrderFilesAdapter(mContext);
            ap_files.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    if (position < 0 || position >= filesList.size()) return;
                    Intent intent = new Intent(mContext, FileActivity.class);
                    intent.putExtra("fileUrl", filesList.get(position));
                    startActivity(intent);
                }
            });
        }
        ap_files.updateData(filesList);
        lv_files.setAdapter(ap_files);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customize_goods_tv_effect_copy:
                copyString(effectUrl, getString(R.string.share_msg_copy_link_ok));
                break;
            case R.id.customize_goods_tv_effect_check:
                openWebViewActivity(getString(R.string.goods_effect), effectUrl);
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

    /**
     * 加载定制商品详情数据
     */
    private void loadCustomizeData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("roleId", userManager.getUserRoleIds());
        map.put("productId", goodsEn.getId());
        map.put("customCode", goodsEn.getSkuCode());
        loadSVData(AppConfig.URL_ORDER_GOODS, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ORDER_GOODS);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        super.callbackData(jsonObject, dataType);
        BaseEntity<GoodsEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ORDER_GOODS:
                    baseEn = JsonUtils.getCustomizeGoodsData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        goodsEn = baseEn.getData();
                        initShowData();
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
