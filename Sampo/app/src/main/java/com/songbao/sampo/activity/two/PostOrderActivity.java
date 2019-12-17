package com.songbao.sampo.activity.two;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.GoodsOrder2Adapter;
import com.songbao.sampo.entity.AddressEntity;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.entity.OPurchaseEntity;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.songbao.sampo.widgets.ScrollViewListView;
import com.songbao.sampo.wxapi.WXPayEntryActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class PostOrderActivity extends BaseActivity implements View.OnClickListener {

    String TAG = PostOrderActivity.class.getSimpleName();

    @BindView(R.id.post_order_address_main)
    ConstraintLayout address_main;

    @BindView(R.id.post_order_tv_address_add)
    TextView tv_address_add;

    @BindView(R.id.post_order_tv_address_district)
    TextView tv_address_district;

    @BindView(R.id.post_order_tv_address_detail)
    TextView tv_address_detail;

    @BindView(R.id.post_order_tv_address_name)
    TextView tv_address_name;

    @BindView(R.id.post_order_tv_address_group)
    Group tv_address_group;

    @BindView(R.id.post_order_view_lv)
    ScrollViewListView lv_goods;

    @BindView(R.id.post_order_tv_goods_price_show)
    TextView tv_price_goods;

    @BindView(R.id.post_order_tv_freight_price_show)
    TextView tv_price_freight;

    @BindView(R.id.post_order_tv_discount_price_show)
    TextView tv_price_discount;

    @BindView(R.id.post_order_tv_price)
    TextView tv_price_total;

    @BindView(R.id.post_order_tv_confirm)
    TextView tv_confirm;

    GoodsOrder2Adapter lv_Adapter;

    private OPurchaseEntity opEn;
    private ArrayList<GoodsEntity> al_goods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_order);

        initView();
    }

    private void initView() {
        setTitle("填写订单");

        address_main.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        initDemoData();
    }

    private void initShowData() {
        if (opEn != null) {
            AddressEntity addEn = opEn.getAddEn();
            if (addEn != null) {
                tv_address_district.setText(addEn.getDistrict());
                tv_address_detail.setText(addEn.getAddress());
                tv_address_name.setText(getString(R.string.address_name_phone, addEn.getName(), addEn.getPhone()));

                tv_address_add.setVisibility(View.GONE);
                tv_address_group.setVisibility(View.VISIBLE);
            } else {
                tv_address_add.setVisibility(View.VISIBLE);
                tv_address_group.setVisibility(View.GONE);
            }

            al_goods.addAll(opEn.getGoodsLists());
            initListView();

            tv_price_goods.setText(getString(R.string.order_rmb, df.format(opEn.getGoodsPrice())));
            tv_price_freight.setText(getString(R.string.order_rmb_add, df.format(opEn.getFreightPrice())));
            tv_price_discount.setText(getString(R.string.order_rmb_minus, df.format(opEn.getDiscountPrice())));
            tv_price_total.setText(df.format(opEn.getTotalPrice()));
        }
    }

    private void initListView() {
        if (lv_Adapter == null) {
            lv_Adapter = new GoodsOrder2Adapter(mContext);
            lv_Adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {

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
            case R.id.post_order_address_main:
                break;
            case R.id.post_order_tv_confirm:
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
            if (requestCode == AppConfig.ACTIVITY_CODE_PAY_DATA) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initDemoData() {
        opEn = new OPurchaseEntity();

        // 地址
        AddressEntity addEn = new AddressEntity();
        addEn.setDistrict("广东省深圳市南山区");
        addEn.setAddress("粤海街道科发路大冲城市花园5栋16B");
        addEn.setName("张先生");
        addEn.setPhone("15899771986");
        opEn.setAddEn(addEn);

        // 明细
        opEn.setGoodsPrice(8997);
        opEn.setFreightPrice(0);
        opEn.setDiscountPrice(100);
        opEn.setTotalPrice(8897);

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

            lists.add(goodsEn);
        }
        opEn.setGoodsLists(lists);

        initShowData();
    }

}
