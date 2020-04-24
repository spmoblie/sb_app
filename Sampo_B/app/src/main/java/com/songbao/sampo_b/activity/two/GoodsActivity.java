package com.songbao.sampo_b.activity.two;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.utils.BitmapUtil;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.QRCodeUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.ObservableScrollView;
import com.songbao.sampo_b.widgets.ViewPagerScroller;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class GoodsActivity extends BaseActivity implements OnClickListener {

    String TAG = GoodsActivity.class.getSimpleName();

    @BindView(R.id.goods_view_sv)
    ObservableScrollView goods_sv;

    @BindView(R.id.goods_view_vp)
    ViewPager goods_vp;

    @BindView(R.id.goods_vp_indicator)
    LinearLayout vp_indicator;

    @BindView(R.id.goods_tv_goods_name)
    TextView tv_name;

    @BindView(R.id.goods_tv_effect_url)
    TextView tv_url;

    @BindView(R.id.goods_tv_effect_check)
    TextView tv_check;

    @BindView(R.id.goods_iv_code)
    ImageView iv_code;

    @BindView(R.id.goods_tv_code_show)
    TextView tv_code_show;

    @BindView(R.id.goods_tv_click)
    TextView tv_click;

    private Runnable mPagerAction;
    private LinearLayout.LayoutParams indicatorsLP;

    private GoodsEntity goodsEn;
    private Bitmap qrImage;
    private String goodsCode = "";
    private boolean isLoop = false;
    private boolean vprStop = false;
    private int idsSize, idsPosition, vprPosition;
    private ImageView[] indicators = null;
    private ArrayList<ImageView> viewLists = new ArrayList<>();
    private ArrayList<String> al_image = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        goodsCode = getIntent().getStringExtra("goodsCode");

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.goods_good_detail));

        tv_check.setOnClickListener(this);
        tv_click.setOnClickListener(this);

        // 动态调整宽高
        int ind_margin = CommonTools.dpToPx(mContext, 5);
        indicatorsLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        indicatorsLP.setMargins(ind_margin, 0, 0, 0);

        loadGoodsData();
    }

    private void initShowView() {
        if (goodsEn != null) {
            tv_name.setText(goodsEn.getName());
            tv_url.setText(goodsEn.getEffectUrl());

            //商品编码
            final String imgName = "QR_" + goodsCode + ".png";
            tv_code_show.setText(imgName);
            int imgSize = AppApplication.screen_width;
            qrImage = QRCodeUtil.createQRImage(goodsCode, imgSize, imgSize, 1, null);
            iv_code.setImageBitmap(BitmapUtil.getBitmap(qrImage, 645, 645));
            iv_code.setOnLongClickListener(new View.OnLongClickListener() {
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

            //商品图片
            if (goodsEn.getImageList() != null && al_image.size() <= 0) {
                al_image.addAll(goodsEn.getImageList());
                initViewPager();
            }
        }
    }

    private void initViewPager() {
        if (al_image.size() > 0) {
            isLoop = false;
            clearViewPagerData();
            ArrayList<String> al_show = new ArrayList<>();
            al_show.addAll(al_image);
            idsSize = al_show.size();
            indicators = new ImageView[idsSize]; // 定义指示器数组大小
            if (idsSize == 2 || idsSize == 3) {
                al_show.addAll(al_image);
            }
            for (int i = 0; i < al_show.size(); i++) {
                final int position = i;
                final String imgUrl = al_show.get(i);

                ImageView iv_show = new ImageView(mContext);
                iv_show.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOptions())
                        .into(iv_show);

                iv_show.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (ClickUtils.isDoubleClick(v.getId())) return;
                        openViewPagerActivity(al_image, position);
                    }
                });
                viewLists.add(iv_show);

                if (i < idsSize) {
                    // 循环加入指示器
                    indicators[i] = new ImageView(mContext);
                    indicators[i].setLayoutParams(indicatorsLP);
                    indicators[i].setImageResource(R.mipmap.indicators_default);
                    if (i == 0) {
                        indicators[i].setImageResource(R.mipmap.indicators_now);
                    }
                    vp_indicator.addView(indicators[i]);
                }
            }
            if (viewLists.size() > 3) {
                isLoop = true;
            }
            goods_vp.setAdapter(new PagerAdapter() {

                // 创建
                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    View layout;
                    if (isLoop) {
                        layout = viewLists.get(position % viewLists.size());
                    } else {
                        layout = viewLists.get(position);
                    }
                    container.addView(layout);
                    return layout;
                }

                // 销毁
                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                    View layout;
                    if (isLoop) {
                        layout = viewLists.get(position % viewLists.size());
                    } else {
                        layout = viewLists.get(position);
                    }
                    container.removeView(layout);
                }

                @Override
                public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                    return view == object;
                }

                @Override
                public int getCount() {
                    if (isLoop) {
                        return Integer.MAX_VALUE;
                    } else {
                        return viewLists.size();
                    }
                }
            });
            goods_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(final int arg0) {
                    if (viewLists.size() <= 1) return;
                    if (isLoop) {
                        vprPosition = arg0;
                        idsPosition = arg0 % viewLists.size();
                        if (idsPosition == viewLists.size()) {
                            idsPosition = 0;
                            goods_vp.setCurrentItem(0);
                        }
                    } else {
                        idsPosition = arg0;
                    }
                    // 更改指示器图片
                    if ((idsSize == 2 || idsSize == 3) && idsPosition >= idsSize) {
                        idsPosition = idsPosition - idsSize;
                    }
                    for (int i = 0; i < idsSize; i++) {
                        ImageView imageView = indicators[i];
                        if (i == idsPosition)
                            imageView.setImageResource(R.mipmap.indicators_now);
                        else
                            imageView.setImageResource(R.mipmap.indicators_default);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    if (arg0 == 1) {
                        vprStop = true;
                    }
                }
            });
            if (isLoop) {
                goods_vp.setCurrentItem(viewLists.size() * 10);
                if (mPagerAction == null) {
                    mPagerAction = new Runnable() {

                        @Override
                        public void run() {
                            if (viewLists.size() <= 1) return;
                            if (!vprStop) {
                                vprPosition++;
                                goods_vp.setCurrentItem(vprPosition);
                            }
                            vprStop = false;
                            goods_vp.postDelayed(mPagerAction, 5000);
                        }
                    };
                }
                goods_vp.post(mPagerAction);

                ViewPagerScroller scroller = new ViewPagerScroller(mContext);
                scroller.initViewPagerScroll(goods_vp);
            }
        }
    }

    private void clearViewPagerData() {
        if (viewLists.size() > 0) {
            viewLists.clear();
        }
        goods_vp.removeAllViews();
        goods_vp.removeCallbacks(mPagerAction);
        vp_indicator.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goods_tv_effect_check:
                if (goodsEn != null) {
                    openWebViewActivity(getString(R.string.goods_effect), goodsEn.getEffectUrl(), null);
                }
                break;
            case R.id.goods_tv_click:
                if (ClickUtils.isDoubleClick(v.getId())) return;
                if (goodsEn == null) {
                    dataErrorHandle();
                    return;
                }
                openGoodsEditActivity(goodsEn);
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
        // 清除轮播数据
        clearViewPagerData();

        super.onDestroy();
    }

    /**
     * 数据报错处理
     */
    private void dataErrorHandle() {
        loadGoodsData();
    }

    /**
     * 加载商品详情数据
     */
    private void loadGoodsData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("goodsCode", goodsCode);
        loadSVData(AppConfig.URL_GOODS_DETAIL, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_GOODS_DETAIL);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        super.callbackData(jsonObject, dataType);
        BaseEntity<GoodsEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_GOODS_DETAIL:
                    baseEn = JsonUtils.getGoodsDetailData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        goodsEn = baseEn.getData();
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
