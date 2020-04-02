package com.songbao.sampo_c.activity.two;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.activity.mine.AddressActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.GoodsOrder2Adapter;
import com.songbao.sampo_c.entity.AddressEntity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CartEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.entity.OPurchaseEntity;
import com.songbao.sampo_c.entity.ThemeEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import com.songbao.sampo_c.wxapi.WXPayEntryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @BindView(R.id.post_order_tv_address_status)
    TextView tv_address_status;

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
    private AddressEntity addEn;
    private int addressId = 0;
    private double payPrice = 0;
    private ArrayList<GoodsEntity> al_goods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_order);

        initView();
    }

    private void initView() {
        setTitle(R.string.order_fill);

        address_main.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        loadOrderData();
        loadAddressData();
    }

    private void initShowData() {
        if (opEn != null) {
            al_goods.clear();
            al_goods.addAll(opEn.getGoodsLists());
            initListView();
            initAddressView();

            payPrice = opEn.getTotalPrice();
            tv_price_goods.setText(getString(R.string.order_rmb, df.format(opEn.getGoodsPrice())));
            tv_price_freight.setText(getString(R.string.order_rmb_add, df.format(opEn.getFreightPrice())));
            tv_price_discount.setText(getString(R.string.order_rmb_minus, df.format(opEn.getDiscountPrice())));
            tv_price_total.setText(df.format(payPrice));
        }
    }

    private void initAddressView() {
        if (addEn != null) {
            tv_address_district.setText(addEn.getDistrict());
            tv_address_detail.setText(addEn.getAddress());
            tv_address_name.setText(getString(R.string.address_name_phone, addEn.getName(), addEn.getPhone()));

            addressId = addEn.getId();
            if (userManager.getDefaultAddressId() == addressId) {
                tv_address_status.setVisibility(View.VISIBLE);
            } else {
                tv_address_status.setVisibility(View.GONE);
            }
            tv_address_add.setVisibility(View.GONE);
            tv_address_group.setVisibility(View.VISIBLE);
        } else {
            tv_address_add.setVisibility(View.VISIBLE);
            tv_address_group.setVisibility(View.GONE);
            tv_address_status.setVisibility(View.GONE);
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
                Intent intent = new Intent(mContext, AddressActivity.class);
                intent.putExtra("isFinish", true);
                intent.putExtra("selectId", addressId);
                startActivityForResult(intent, AppConfig.ACTIVITY_CODE_SELECT_ADDS);
                break;
            case R.id.post_order_tv_confirm:
                if (addressId > 0) {
                    postOrderData();
                } else {
                    CommonTools.showToast(getString(R.string.order_address_null));
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

    /**
     * 数据报错处理
     */
    private void dataErrorHandle() {
        showDataError();
        loadOrderData();
    }

    /**
     * 加载数据
     */
    private void loadOrderData() {
        HashMap<String, Object> map = new HashMap<>();
        loadSVData(AppConfig.URL_ORDER_FILL, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ORDER_FILL);
    }

    /**
     * 加载地址列表
     */
    private void loadAddressData() {
        HashMap<String, Object> map = new HashMap<>();
        loadSVData(AppConfig.URL_ADDRESS_LIST, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ADDRESS_LIST);
    }

    /**
     * 提交订单
     */
    private void postOrderData() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("consigneeId", addressId);
            postJsonData(AppConfig.URL_ORDER_CREATE, jsonObj, AppConfig.REQUEST_SV_ORDER_CREATE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 在线支付
     */
    private void startPay(String orderNo) {
        if (StringUtil.isNull(orderNo) || payPrice <= 0) return;
        Intent intent = new Intent(mContext, WXPayEntryActivity.class);
        intent.putExtra("sourceType", WXPayEntryActivity.SOURCE_TYPE_3);
        intent.putExtra("orderSn", orderNo);
        intent.putExtra("orderTotal", df.format(payPrice));
        intent.putExtra("isToOrder", true);
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PAY_DATA);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<OPurchaseEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ORDER_FILL:
                    baseEn = JsonUtils.getOrderFillData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        opEn = baseEn.getData();
                        initShowData();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ADDRESS_LIST:
                    BaseEntity<AddressEntity> addList = JsonUtils.getAddressListData(jsonObject);
                    if (addList.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        List<AddressEntity> lists = addList.getLists();
                        if (lists != null && lists.size() > 0) {
                            AddressEntity item;
                            for (int i = 0; i < lists.size(); i++) {
                                item = lists.get(i);
                                if (item != null && item.isDefault()) {
                                    addEn = item;
                                    break;
                                }
                            }
                            initAddressView();
                        }
                    } else if (addList.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(addList);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_CREATE:
                    BaseEntity resultEn = JsonUtils.getPayOrderOn(jsonObject);
                    if (resultEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        String orderNo = resultEn.getOthers();
                        if (!StringUtil.isNull(orderNo)) {
                            startPay(orderNo);
                            AppApplication.updateCartData(true);
                            finish();
                        } else {
                            loadFailHandle();
                        }
                    } else {
                        showSuccessDialog(resultEn.getErrMsg(), false);
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
                addEn = (AddressEntity) data.getSerializableExtra(AppConfig.PAGE_DATA);
                initAddressView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
