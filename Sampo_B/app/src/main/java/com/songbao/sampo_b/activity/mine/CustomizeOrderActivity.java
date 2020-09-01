package com.songbao.sampo_b.activity.mine;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
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
import com.songbao.sampo_b.activity.two.PostOrderActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.GoodsOrderShowAdapter;
import com.songbao.sampo_b.adapter.OrderFilesAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.FileEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.FileManager;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.RoundImageView;
import com.songbao.sampo_b.widgets.ScrollViewListView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class CustomizeOrderActivity extends BaseActivity implements OnClickListener {

    String TAG = CustomizeOrderActivity.class.getSimpleName();

    @BindView(R.id.customize_order_tv_status)
    TextView tv_status;

    @BindView(R.id.customize_order_view_lv_goods)
    ScrollViewListView lv_goods;

    @BindView(R.id.customize_order_tv_price_nullify)
    TextView tv_nullify;

    @BindView(R.id.customize_order_tv_price_curr)
    TextView tv_curr;

    @BindView(R.id.customize_order_tv_price_show)
    TextView tv_price;

    @BindView(R.id.customize_order_tv_customer_name)
    TextView tv_customer_name;

    @BindView(R.id.customize_order_tv_customer_phone)
    TextView tv_customer_phone;

    @BindView(R.id.customize_order_tv_build_name)
    TextView tv_build_name;

    @BindView(R.id.customize_order_tv_deal_store)
    TextView tv_deal_store;

    @BindView(R.id.customize_order_tv_order_no)
    TextView tv_order_no;

    @BindView(R.id.customize_order_tv_order_no_copy)
    TextView tv_copy;

    @BindView(R.id.customize_order_tv_time_hope)
    TextView tv_time_hope;

    @BindView(R.id.customize_order_tv_time_term)
    TextView tv_time_term;

    @BindView(R.id.customize_order_tv_time_create)
    TextView tv_time_create;

    @BindView(R.id.customize_order_tv_time_check)
    TextView tv_time_check;

    @BindView(R.id.customize_order_tv_time_price)
    TextView tv_time_price;

    @BindView(R.id.customize_order_tv_time_send)
    TextView tv_time_send;

    @BindView(R.id.customize_order_tv_time_receive)
    TextView tv_time_receive;

    @BindView(R.id.customize_order_remarks_main)
    ConstraintLayout remarks_main;

    @BindView(R.id.customize_order_tv_order_remarks)
    TextView tv_remarks;

    @BindView(R.id.customize_order_tv_order_remarks_show)
    TextView tv_remarks_show;

    @BindView(R.id.customize_order_tv_order_other)
    TextView tv_other;

    @BindView(R.id.customize_order_tv_order_other_show)
    TextView tv_other_show;

    @BindView(R.id.customize_order_view_lv_files)
    ScrollViewListView lv_files;

    @BindView(R.id.customize_order_view_sv_image)
    HorizontalScrollView sv_image;

    @BindView(R.id.customize_order_view_sv_ll_main)
    LinearLayout ll_sv_main;

    @BindView(R.id.customize_order_tv_click)
    TextView tv_click;

    GoodsOrderShowAdapter ap_goods;
    OrderFilesAdapter ap_files;
    RelativeLayout.LayoutParams goodsImgLP;

    private OCustomizeEntity ocEn;
    private int status = 0;
    private int updateCode = 0;
    private String orderNo; //订单编号
    private boolean isOnClick = false;
    private ArrayList<GoodsEntity> al_goods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_order);

        ocEn = (OCustomizeEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.order_detail));

        int imageSize = (screenWidth - CommonTools.dpToPx(mContext, 64)) / 3;
        goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        goodsImgLP.setMargins(0, 0, 10, 0);
        goodsImgLP.width = imageSize;
        goodsImgLP.height = imageSize;

        tv_copy.setOnClickListener(this);
        tv_click.setOnClickListener(this);

        if (ocEn != null) {
            orderNo = ocEn.getOrderNo();
            loadOrderData();
        }
    }

    private void initShowData() {
        if (ocEn != null) {
            isOnClick = false;
            status = ocEn.getStatus();
            switch (status) {
                case AppConfig.ORDER_STATUS_101: //待审核
                    setRightViewText(getString(R.string.order_cancel));
                    tv_status.setText(getString(R.string.order_wait_check));
                    tv_status.setTextColor(getResources().getColor(R.color.app_color_brown));
                    tv_status.setBackgroundResource(R.drawable.shape_style_empty_08_16);
                    tv_click.setText(getString(R.string.order_check_on));
                    tv_click.setBackgroundResource(R.drawable.shape_style_solid_08_08);
                    break;
                case AppConfig.ORDER_STATUS_201: //待初核
                case AppConfig.ORDER_STATUS_202: //待复核
                    setRightViewText(getString(R.string.order_cancel));
                    tv_status.setText(getString(R.string.order_wait_price));
                    tv_status.setTextColor(getResources().getColor(R.color.app_color_green));
                    tv_status.setBackgroundResource(R.drawable.shape_style_empty_09_16);
                    tv_click.setText(getString(R.string.order_price_on));
                    tv_click.setBackgroundResource(R.drawable.shape_style_solid_09_08);
                    break;
                case AppConfig.ORDER_STATUS_301: //生产中
                    isOnClick = true;
                    tv_status.setText(ocEn.getStatusDesc());
                    tv_status.setTextColor(getResources().getColor(R.color.app_color_style));
                    tv_status.setBackgroundResource(R.drawable.shape_style_empty_04_16);
                    tv_click.setText(getString(R.string.order_confirm_receive));
                    tv_click.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                    break;
                case AppConfig.ORDER_STATUS_401: //已发货
                    isOnClick = true;
                    tv_status.setText(getString(R.string.order_wait_receive));
                    tv_status.setTextColor(getResources().getColor(R.color.app_color_violet));
                    tv_status.setBackgroundResource(R.drawable.shape_style_empty_11_16);
                    tv_click.setText(getString(R.string.order_confirm_receive));
                    tv_click.setBackgroundResource(R.drawable.shape_style_solid_11_08);
                    break;
                case AppConfig.ORDER_STATUS_801: //已完成
                    setRightViewText(getString(R.string.order_delete));
                    tv_status.setText(getString(R.string.order_completed));
                    tv_status.setTextColor(getResources().getColor(R.color.debar_text_color));
                    tv_status.setBackgroundResource(R.drawable.shape_style_empty_03_16);
                    tv_click.setText(getString(R.string.order_re_order));
                    tv_click.setBackgroundResource(R.drawable.shape_style_solid_07_08);
                    break;
                case AppConfig.ORDER_STATUS_104: //已拒绝
                    setRightViewText(getString(R.string.order_cancel));
                    tv_status.setText(getString(R.string.order_refused));
                    tv_status.setTextColor(getResources().getColor(R.color.app_color_red_p));
                    tv_status.setBackgroundResource(R.drawable.shape_style_empty_05_16);
                    tv_click.setText(getString(R.string.order_re_order));
                    tv_click.setBackgroundResource(R.drawable.shape_style_solid_07_08);
                    break;
                case AppConfig.ORDER_STATUS_102: //已取消
                default:
                    setRightViewText(getString(R.string.order_delete));
                    tv_status.setText(getString(R.string.order_cancelled));
                    tv_status.setTextColor(getResources().getColor(R.color.debar_text_color));
                    tv_status.setBackgroundResource(R.drawable.shape_style_empty_03_16);
                    tv_click.setText(getString(R.string.order_re_order));
                    tv_click.setBackgroundResource(R.drawable.shape_style_solid_07_08);
                    break;
            }

            al_goods.clear();
            al_goods.addAll(ocEn.getGoodsList());
            initGoodsListView();

            remarks_main.setVisibility(View.GONE);
            tv_nullify.setVisibility(View.GONE);
            tv_price.setText(df.format(ocEn.getPriceOne()));
            tv_curr.setTextColor(getResources().getColor(R.color.app_color_black));
            tv_price.setTextColor(getResources().getColor(R.color.app_color_black));
            if (status == AppConfig.ORDER_STATUS_104) { //已拒绝
                // 审核备注
                if (!StringUtil.isNull(ocEn.getCheckRemarks())) {
                    showRemarksView();
                    tv_other.setVisibility(View.VISIBLE);
                    tv_other.setText(getString(R.string.order_check_remarks));
                    tv_other.setTextColor(getResources().getColor(R.color.price_text_color));
                    tv_other_show.setVisibility(View.VISIBLE);
                    tv_other_show.setText(ocEn.getCheckRemarks());
                    tv_other_show.setTextColor(getResources().getColor(R.color.price_text_color));
                }
                // 文件备注
                if (ocEn.getFilesList() != null && ocEn.getFilesList().size() > 0) {
                    showRemarksView();
                    lv_files.setVisibility(View.VISIBLE);
                    initFileListView(ocEn.getFilesList());
                } else {
                    lv_files.setVisibility(View.GONE);
                }
                // 图片备注
                if (ocEn.getImageList() != null && ocEn.getImageList().size() > 0) {
                    showRemarksView();
                    sv_image.setVisibility(View.VISIBLE);
                    initImageScrollView(ll_sv_main, ocEn.getImageList());
                } else {
                    ll_sv_main.removeAllViews();
                    sv_image.setVisibility(View.GONE);
                }
            } else {
                if (ocEn.getPriceTwo() > 0 && ocEn.getPriceTwo() != ocEn.getPriceOne()) { //核价修改价格
                    tv_nullify.setText(getString(R.string.order_rmb, df.format(ocEn.getPriceOne())));
                    tv_nullify.setVisibility(View.VISIBLE);
                    tv_nullify.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG );

                    tv_price.setText(df.format(ocEn.getPriceTwo()));
                    tv_curr.setTextColor(getResources().getColor(R.color.price_text_color));
                    tv_price.setTextColor(getResources().getColor(R.color.price_text_color));

                    // 核价备注
                    if (!StringUtil.isNull(ocEn.getPriceRemarks())) {
                        showRemarksView();
                        tv_other.setVisibility(View.VISIBLE);
                        tv_other_show.setVisibility(View.VISIBLE);
                        tv_other_show.setText(ocEn.getPriceRemarks());
                    }
                    // 图片备注
                    if (ocEn.getImageList() != null && ocEn.getImageList().size() > 0) {
                        showRemarksView();
                        sv_image.setVisibility(View.VISIBLE);
                        initImageScrollView(ll_sv_main, ocEn.getImageList());
                    } else {
                        ll_sv_main.removeAllViews();
                        sv_image.setVisibility(View.GONE);
                    }
                }
            }

            tv_order_no.setText(getString(R.string.order_order_no, ocEn.getOrderNo()));
            tv_customer_name.setText(getString(R.string.order_customer_name_show, ocEn.getCustomerName()));
            tv_customer_phone.setText(getString(R.string.order_customer_phone_show, ocEn.getCustomerPhone()));
            tv_build_name.setText(getString(R.string.order_build_name_show, ocEn.getBuildName()));
            tv_deal_store.setText(getString(R.string.order_deal_store_show, ocEn.getDealStore()));
            tv_time_hope.setText(getString(R.string.order_hope_date_show, ocEn.getHopeTime()));
            tv_time_term.setText(getString(R.string.order_time_term, ocEn.getTermTime()));
            tv_time_create.setText(getString(R.string.order_time_create, ocEn.getNodeTime1()));
            if (!StringUtil.isNull(ocEn.getNodeTime2())) {
                tv_time_check.setVisibility(View.VISIBLE);
                tv_time_check.setText(getString(R.string.order_time_check, ocEn.getNodeTime2()));
            }
            if (!StringUtil.isNull(ocEn.getNodeTime3())) {
                tv_time_price.setVisibility(View.VISIBLE);
                tv_time_price.setText(getString(R.string.order_time_price, ocEn.getNodeTime3()));
            }
            if (!StringUtil.isNull(ocEn.getNodeTime4())) {
                tv_time_send.setVisibility(View.VISIBLE);
                tv_time_send.setText(getString(R.string.order_time_send, ocEn.getNodeTime4()));
            }
            if (!StringUtil.isNull(ocEn.getNodeTime5())) {
                tv_time_receive.setVisibility(View.VISIBLE);
                tv_time_receive.setText(getString(R.string.order_time_receive, ocEn.getNodeTime5()));
            }
            if (!StringUtil.isNull(ocEn.getOrderRemarks())) {
                showRemarksView();
                tv_remarks.setVisibility(View.VISIBLE);
                tv_remarks_show.setVisibility(View.VISIBLE);
                tv_remarks_show.setText(ocEn.getOrderRemarks());
            }
        }
    }

    private void showRemarksView() {
        if (remarks_main.getVisibility() == View.GONE) {
            remarks_main.setVisibility(View.VISIBLE);
        }
    }

    private void initGoodsListView() {
        if (ap_goods == null) {
            ap_goods = new GoodsOrderShowAdapter(mContext);
            ap_goods.showDetailView(true);
            ap_goods.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    if (position < 0 || position >= al_goods.size()) return;
                    openCustomizeGoodsActivity(al_goods.get(position));
                }
            });
        }
        ap_goods.updateData(al_goods);
        lv_goods.setAdapter(ap_goods);
    }

    private void initFileListView(final ArrayList<FileEntity> filesList) {
        if (ap_files == null) {
            ap_files = new OrderFilesAdapter(mContext, false);
            ap_files.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    if (position < 0 || position >= filesList.size()) return;
                    if (type == 0) {
                        Intent intent = new Intent(mContext, FileActivity.class);
                        intent.putExtra(AppConfig.PAGE_DATA, filesList.get(position));
                        startActivity(intent);
                    }
                }
            });
        }
        ap_files.updateData(filesList);
        lv_files.setAdapter(ap_files);
    }

    /**
     * 图片备注
     */
    private void initImageScrollView(LinearLayout ll_main, final ArrayList<String> imgList) {
        if (ll_main == null || imgList == null || imgList.size() <= 0) return;
        ll_main.removeAllViews();

        int imgCount = imgList.size();
        for (int i = 0; i < imgCount; i++) {
            final int position = i;
            final String imgUrl = imgList.get(i);
            RoundImageView iv_img = new RoundImageView(mContext);
            iv_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == imgCount - 1) {
                goodsImgLP.setMargins(0, 0, 0, 0);
            }
            iv_img.setLayoutParams(goodsImgLP);

            iv_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openViewPagerActivity(imgList, position);
                }
            });

            Glide.with(AppApplication.getAppContext())
                    .load(imgUrl)
                    .apply(AppApplication.getShowOptions())
                    .into(iv_img);

            ll_main.addView(iv_img);
        }
    }

    /**
     * 打开定制产品详情页
     */
    private void openCustomizeGoodsActivity(GoodsEntity goodsEn) {
        Intent intent = new Intent(mContext, CustomizeGoodsActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, goodsEn);
        startActivity(intent);
    }

    /**
     * 重新下单
     */
    private void reOrder() {
        if (ocEn == null || al_goods == null) return;

        OCustomizeEntity newOcEn = new OCustomizeEntity();
        ArrayList<GoodsEntity> goodsList = new ArrayList<>();
        for (int i = 0; i < al_goods.size(); i++) {
            GoodsEntity goodsEn = new GoodsEntity();
            goodsEn.setFilesList(al_goods.get(i).getFilesList());
            goodsEn.setImageList(al_goods.get(i).getImageList());
            goodsEn.setPicUrl(al_goods.get(i).getPicUrl());
            goodsEn.setPicture(al_goods.get(i).isPicture());
            if (!goodsEn.isPicture()) {
                goodsEn.setEffectUrl(al_goods.get(i).getEffectUrl());
            }
            goodsEn.setName(al_goods.get(i).getName());
            goodsEn.setNumber(al_goods.get(i).getNumber());
            goodsEn.setCostPrice(al_goods.get(i).getCostPrice());
            goodsEn.setSalePrice(al_goods.get(i).getSalePrice());
            goodsEn.setRemarks(al_goods.get(i).getRemarks());
            goodsList.add(goodsEn);
        }
        newOcEn.setGoodsList(goodsList);
        newOcEn.setCustomerName(ocEn.getCustomerName());
        newOcEn.setCustomerPhone(ocEn.getCustomerPhone());
        newOcEn.setBuildName(ocEn.getBuildName());
        newOcEn.setDealStore(ocEn.getDealStore());
        newOcEn.setHopeTime(ocEn.getHopeTime());
        newOcEn.setOrderRemarks(ocEn.getOrderRemarks());

        FileManager.writeFileSaveObject(AppConfig.orderDataFileName, newOcEn, 0);
        openActivity(PostOrderActivity.class);
        closeOrderActivity(); //关闭已打开的页面
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customize_order_tv_order_no_copy:
                copyString(orderNo, getString(R.string.order_order_no_copy));
                break;
            case R.id.customize_order_tv_click:
                switch (status) {
                    case AppConfig.ORDER_STATUS_401: //已发货
                        //确认收货
                        if (isOnClick) {
                            showConfirmDialog(getString(R.string.order_confirm_receive_hint), new MyHandler(this), 7);
                        }
                        break;
                    case AppConfig.ORDER_STATUS_104: //已拒绝
                    case AppConfig.ORDER_STATUS_102: //已取消
                    case AppConfig.ORDER_STATUS_801: //已完成
                        // 重新下单
                        reOrder();
                        break;
                }
                break;
        }
    }

    @Override
    protected void OnListenerRight() {
        switch (status) {
            case AppConfig.ORDER_STATUS_101: //待审核
            case AppConfig.ORDER_STATUS_104: //已拒绝
            case AppConfig.ORDER_STATUS_201: //待初核
            case AppConfig.ORDER_STATUS_202: //待复核
                // 取消订单
                showConfirmDialog(getString(R.string.order_cancel_confirm), new MyHandler(this), 101);
                break;
            case AppConfig.ORDER_STATUS_102: //已取消
            case AppConfig.ORDER_STATUS_801: //已完成
                // 删除订单
                showConfirmDialog(getString(R.string.order_delete_confirm), new MyHandler(this), 102);
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
        if (updateCode == -1
                || updateCode == AppConfig.ORDER_STATUS_102
                || updateCode == AppConfig.ORDER_STATUS_801) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConfig.PAGE_DATA, updateCode);
            setResult(RESULT_OK, returnIntent);
        }
        super.finish();
    }

    /**
     * 加载订单详情数据
     */
    private void loadOrderData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("roleId", userManager.getUserRoleIds());
        map.put("customCode", orderNo);
        loadSVData(AppConfig.URL_ORDER_INFO, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ORDER_INFO);
    }


    /**
     * 确认收货
     */
    private void postConfirmReceive() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customCode", orderNo);
        loadSVData(AppConfig.URL_ORDER_RECEIVE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ORDER_RECEIVE);
    }

    /**
     * 取消订单
     */
    private void postConfirmCancel() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customCode", orderNo);
        loadSVData(AppConfig.URL_ORDER_CANCEL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ORDER_CANCEL);
    }

    /**
     * 删除订单
     */
    private void postConfirmDelete() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customCode", orderNo);
        loadSVData(AppConfig.URL_ORDER_DELETE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ORDER_DELETE);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        super.callbackData(jsonObject, dataType);
        BaseEntity<OCustomizeEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ORDER_INFO:
                    baseEn = JsonUtils.getCustomizeDetailData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        ocEn = baseEn.getData();
                        initShowData();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_RECEIVE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        updateCode = AppConfig.ORDER_STATUS_801;
                        loadOrderData();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_CANCEL:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        updateCode = AppConfig.ORDER_STATUS_102;
                        loadOrderData();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_DELETE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        updateCode = -1;
                        finish();
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

    static class MyHandler extends Handler {

        WeakReference<CustomizeOrderActivity> mActivity;

        MyHandler(CustomizeOrderActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CustomizeOrderActivity theActivity = mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {
                    case 7: //确认收货
                        theActivity.postConfirmReceive();
                        break;
                    case 101: //取消订单
                        theActivity.postConfirmCancel();
                        break;
                    case 102: //删除订单
                        theActivity.postConfirmDelete();
                        break;
                }
            }
        }
    }

}
