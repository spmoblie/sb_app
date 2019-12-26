package com.songbao.sampo_b.activity.mine;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.GoodsOrderAdapter;
import com.songbao.sampo_b.entity.AddressEntity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.CommentEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.OPurchaseEntity;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.ScrollViewListView;
import com.songbao.sampo_b.wxapi.WXPayEntryActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class PurchaseActivity extends BaseActivity implements View.OnClickListener {

    String TAG = PurchaseActivity.class.getSimpleName();

    @BindView(R.id.purchase_tv_address_district)
    TextView tv_address_district;

    @BindView(R.id.purchase_tv_address_detail)
    TextView tv_address_detail;

    @BindView(R.id.purchase_tv_address_name)
    TextView tv_address_name;

    @BindView(R.id.purchase_tv_time_order)
    TextView tv_time_order;

    @BindView(R.id.purchase_tv_status)
    TextView tv_status;

    @BindView(R.id.purchase_lv_goods)
    ScrollViewListView lv_goods;

    @BindView(R.id.purchase_tv_goods_price_show)
    TextView tv_price_goods;

    @BindView(R.id.purchase_tv_freight_price_show)
    TextView tv_price_freight;

    @BindView(R.id.purchase_tv_discount_price_show)
    TextView tv_price_discount;

    @BindView(R.id.purchase_tv_total_price_show)
    TextView tv_price_total;

    @BindView(R.id.purchase_tv_order_number)
    TextView tv_order_number;

    @BindView(R.id.purchase_tv_order_number_copy)
    TextView tv_number_copy;

    @BindView(R.id.purchase_tv_order_pay_type)
    TextView tv_pay_type;

    @BindView(R.id.purchase_tv_order_pay_number)
    TextView tv_pay_number;

    @BindView(R.id.purchase_tv_order_time_add)
    TextView tv_time_add;

    @BindView(R.id.purchase_tv_order_time_pay)
    TextView tv_time_pay;

    @BindView(R.id.purchase_tv_order_time_send)
    TextView tv_time_send;

    @BindView(R.id.purchase_tv_order_time_done)
    TextView tv_time_done;

    @BindView(R.id.purchase_tv_click_01)
    TextView tv_click_01;

    @BindView(R.id.purchase_tv_click_02)
    TextView tv_click_02;

    @BindView(R.id.purchase_tv_click_03)
    TextView tv_click_03;

    GoodsOrderAdapter lv_Adapter;

    private OPurchaseEntity opEn;
    private ArrayList<GoodsEntity> al_goods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        opEn = (OPurchaseEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

        initView();
    }

    private void initView() {
        setTitle("订单详情");

        tv_number_copy.setOnClickListener(this);
        tv_click_01.setOnClickListener(this);
        tv_click_02.setOnClickListener(this);
        tv_click_03.setOnClickListener(this);

        initDemoData();
    }

    private void initShowData() {
        if (opEn != null) {
            AddressEntity addEn = opEn.getAddEn();
            if (addEn != null) {
                tv_address_district.setText(addEn.getDistrict());
                tv_address_detail.setText(addEn.getAddress());
                tv_address_name.setText(getString(R.string.address_name_phone, addEn.getName(), addEn.getPhone()));
            }

            al_goods.clear();
            al_goods.addAll(opEn.getGoodsLists());
            initListView();

            tv_time_order.setText(opEn.getAddTime());
            switch (opEn.getStatus()) {
                case 1: //待付款
                    tv_status.setText(getString(R.string.order_wait_pay));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_09_04);
                    tv_click_01.setVisibility(View.VISIBLE);
                    tv_click_01.setText(getString(R.string.order_cancel));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setVisibility(View.VISIBLE);
                    tv_click_02.setText(getString(R.string.order_pay));
                    tv_click_02.setTextColor(getResources().getColor(R.color.app_color_white));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                    tv_click_03.setVisibility(View.GONE);
                    break;
                case 2: //生产中
                    break;
                case 3: //待收货
                    tv_status.setText(getString(R.string.order_wait_receive));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
                    tv_click_01.setVisibility(View.VISIBLE);
                    tv_click_01.setText(getString(R.string.order_logistics));
                    tv_click_01.setTextColor(getResources().getColor(R.color.tv_color_status));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_04_08);
                    tv_click_02.setVisibility(View.VISIBLE);
                    tv_click_02.setText(getString(R.string.order_confirm_receipt));
                    tv_click_02.setTextColor(getResources().getColor(R.color.app_color_white));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                    tv_click_03.setVisibility(View.GONE);
                    break;
                case 4: //待评价
                    tv_status.setText(getString(R.string.order_completed));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                    tv_click_01.setVisibility(View.VISIBLE);
                    tv_click_01.setText(getString(R.string.order_post_sale));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setVisibility(View.VISIBLE);
                    tv_click_02.setText(getString(R.string.comment_me));
                    tv_click_02.setTextColor(getResources().getColor(R.color.app_color_white));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_06_08);
                    tv_click_03.setVisibility(View.GONE);
                    break;
                case 5: //已完成
                default:
                    tv_status.setText(getString(R.string.order_completed));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                    tv_click_01.setVisibility(View.VISIBLE);
                    tv_click_01.setText(getString(R.string.order_post_sale));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setVisibility(View.VISIBLE);
                    tv_click_02.setText(getString(R.string.order_delete));
                    tv_click_02.setTextColor(getResources().getColor(R.color.app_color_white));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_03_08);
                    tv_click_03.setVisibility(View.GONE);
                    break;
                case 6: //退换货
                    tv_status.setText(getString(R.string.order_repair_return));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_05_04);
                    tv_click_01.setVisibility(View.VISIBLE);
                    tv_click_01.setText(getString(R.string.order_refund_details));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setVisibility(View.VISIBLE);
                    tv_click_02.setText(getString(R.string.order_revoke_refund));
                    tv_click_02.setTextColor(getResources().getColor(R.color.app_color_white));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_05_08);
                    tv_click_03.setVisibility(View.GONE);
                    break;
            }

            tv_price_goods.setText(getString(R.string.order_rmb, df.format(opEn.getGoodsPrice())));
            tv_price_freight.setText(getString(R.string.order_rmb_add, df.format(opEn.getFreightPrice())));
            tv_price_discount.setText(getString(R.string.order_rmb_minus, df.format(opEn.getDiscountPrice())));
            tv_price_total.setText(getString(R.string.order_rmb, df.format(opEn.getTotalPrice())));

            tv_order_number.setText(getString(R.string.order_order_no, opEn.getOrderNo()));
            tv_pay_type.setText(getString(R.string.order_pay_type, opEn.getPayType()));
            tv_pay_number.setText(getString(R.string.order_pay_no, opEn.getPayNo()));
            tv_time_add.setText(getString(R.string.order_time_add, opEn.getAddTime()));
            tv_time_pay.setText(getString(R.string.order_time_pay, opEn.getPayTime()));
            tv_time_send.setText(getString(R.string.order_time_send, opEn.getSendTime()));
            tv_time_done.setText(getString(R.string.order_time_done, opEn.getDoneTime()));
        }
    }

    private void initListView() {
        if (lv_Adapter == null) {
            lv_Adapter = new GoodsOrderAdapter(mContext, true);
            lv_Adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    if (position < 0 || position >= al_goods.size()) return;
                    Intent intent;
                    switch (type) {
                        case 0: //查看商品
                            openGoodsActivity(al_goods.get(position).getEntityId());
                            break;
                        case 1: //申请售后
                            intent = new Intent(mContext, PostSaleActivity.class);
                            intent.putExtra(AppConfig.PAGE_DATA, al_goods.get(position));
                            startActivity(intent);
                            break;
                        case 2: //退款详情
                            intent = new Intent(mContext, RefundActivity.class);
                            intent.putExtra(AppConfig.PAGE_DATA, al_goods.get(position));
                            startActivity(intent);
                            break;
                        case 3: //我要评价
                            CommentEntity cEn1 = new CommentEntity();
                            cEn1.setGoodsEn(al_goods.get(position));
                            openCommentPostActivity(cEn1);
                            break;
                        case 4: //追加评价
                            CommentEntity cEn2 = new CommentEntity();
                            cEn2.setGoodsEn(al_goods.get(position));
                            openCommentAddActivity(cEn2);
                            break;
                    }
                }
            });
        }
        lv_Adapter.updateData(al_goods);
        lv_goods.setAdapter(lv_Adapter);
        lv_goods.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.purchase_tv_order_number_copy:
                break;
            case R.id.purchase_tv_click_01:

                break;
            case R.id.purchase_tv_click_02:

                break;
            case R.id.purchase_tv_click_03:
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
     * 数据报错处理
     */
    private void dataErrorHandle() {
        showDataError();
        loadServerData();
    }

    /**
     * 加载数据
     */
    private void loadServerData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activityId", "");
        loadSVData(AppConfig.URL_ACTIVITY_DETAIL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ACTIVITY_DETAIL);
    }

    /**
     * 在线支付
     */
    private void startPay() {
        Intent intent = new Intent(mContext, WXPayEntryActivity.class);
        intent.putExtra(AppConfig.PAGE_TYPE, 1);
        intent.putExtra("orderSn", "");
        intent.putExtra("orderTotal", df.format(0));
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PAY_DATA);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ACTIVITY_DETAIL:
                    baseEn = JsonUtils.getThemeDetail(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        //setView((ThemeEntity) baseEn.getData());
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
            if (requestCode == AppConfig.ACTIVITY_CODE_SELECT_ADDS) {
                AddressEntity selectEn = (AddressEntity) data.getSerializableExtra(AppConfig.PAGE_DATA);
                opEn.setAddEn(selectEn);
                initShowData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initDemoData() {
        if (opEn == null) {
            opEn = new OPurchaseEntity();
        }

        // 地址
        AddressEntity addEn = new AddressEntity();
        addEn.setId(2);
        addEn.setDistrict("广东省深圳市南山区");
        addEn.setAddress("粤海街道科发路大冲城市花园5栋16B");
        addEn.setName("张先生");
        addEn.setPhone("15899771986");
        opEn.setAddEn(addEn);

        // 明细
        //opEn.setStatus(1);
        opEn.setOrderNo("SN1215454122121");
        opEn.setPayType("微信支付");
        opEn.setPayNo("4564123132123154566");
        //opEn.setAddTime("2019/11/21 10：28：28");
        opEn.setPayTime("2019/11/21 10：30：28");
        opEn.setSendTime("2019/11/22 11：30：28");
        opEn.setDoneTime("2019/11/23 12：30：28");
        opEn.setGoodsPrice(6008);
        opEn.setFreightPrice(0);
        opEn.setDiscountPrice(100);
        //opEn.setTotalPrice(5908);

        // 商品
        ArrayList<GoodsEntity> lists = new ArrayList<>();
        GoodsEntity goodsEn;
        for (int j = 0; j < 2; j++) {
            goodsEn = new GoodsEntity();
            int is = j + 1;
            goodsEn.setId(is);
            goodsEn.setPicUrl(AppConfig.IMAGE_URL + "design_001.png");
            goodsEn.setName("松堡王国现代简约彩条双层床松堡王国现代简约彩条双层床");
            goodsEn.setAttribute("天蓝色；1350*1900天蓝色；1350*1900");
            goodsEn.setNumber(is);
            goodsEn.setPrice(2999);
            goodsEn.setSaleStatus(1);
            goodsEn.setCommentStatus(3);

            lists.add(goodsEn);
        }
        //opEn.setGoodsLists(lists);

        initShowData();
    }

}